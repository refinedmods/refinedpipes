package com.raoulvdberge.refinedpipes.network;

import com.raoulvdberge.refinedpipes.network.graph.NetworkGraph;
import com.raoulvdberge.refinedpipes.network.graph.scanner.NetworkGraphScannerResult;
import com.raoulvdberge.refinedpipes.network.pipe.fluid.FluidDestination;
import com.raoulvdberge.refinedpipes.util.StringUtil;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.Random;

public class Network {
    private static final Logger LOGGER = LogManager.getLogger(Network.class);

    private final NetworkGraph graph = new NetworkGraph(this);
    private final String id;
    private BlockPos originPos;
    private boolean didDoInitialScan;
    private FluidTank fluidTank = new FluidTank(FluidAttributes.BUCKET_VOLUME);

    public Network(BlockPos originPos) {
        this(originPos, StringUtil.randomString(new Random(), 8));
    }

    public Network(BlockPos originPos, String id) {
        this.id = id;
        this.originPos = originPos;
    }

    public void setOriginPos(BlockPos originPos) {
        this.originPos = originPos;
    }

    public String getId() {
        return id;
    }

    public NetworkGraphScannerResult scanGraph(World originWorld, BlockPos originPos) {
        return graph.scan(originWorld, originPos);
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }

    public CompoundNBT writeToNbt(CompoundNBT tag) {
        tag.putString("id", id);
        tag.putLong("origin", originPos.toLong());
        tag.put("tank", fluidTank.writeToNBT(new CompoundNBT()));

        return tag;
    }

    public static Network fromNbt(CompoundNBT tag) {
        Network network = new Network(BlockPos.fromLong(tag.getLong("origin")), tag.getString("id"));

        if (tag.contains("tank")) {
            network.fluidTank.readFromNBT(tag.getCompound("tank"));
        }

        LOGGER.debug("Deserialized network {}", network.id);

        return network;
    }

    public void update(World world) {
        if (!didDoInitialScan) {
            didDoInitialScan = true;

            scanGraph(world, originPos);
        }

        if (!fluidTank.getFluid().isEmpty() && !graph.getFluidDestinations().isEmpty()) {
            int toDistribute = (int) Math.floor((float) getThroughput() / (float) graph.getFluidDestinations().size());

            for (FluidDestination fluidDestination : graph.getFluidDestinations()) {
                TileEntity tile = fluidDestination.getConnectedPipe().getWorld().getTileEntity(fluidDestination.getReceiver());
                if (tile == null) {
                    continue;
                }

                IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, fluidDestination.getIncomingDirection().getOpposite()).orElse(null);
                if (handler == null) {
                    continue;
                }

                FluidStack drained = fluidTank.drain(toDistribute, IFluidHandler.FluidAction.EXECUTE);
                if (drained.isEmpty()) {
                    continue;
                }

                int filled = handler.fill(drained, IFluidHandler.FluidAction.EXECUTE);

                int remainder = drained.getAmount() - filled;
                if (remainder > 0) {
                    drained = drained.copy();
                    drained.setAmount(remainder);

                    fluidTank.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                }
            }
        }

        graph.getPipes().forEach(p -> p.update(world));
    }

    private int getThroughput() {
        int viscosity = fluidTank.getFluid().getFluid().getAttributes().getViscosity(fluidTank.getFluid());
        viscosity = Math.max(100, viscosity);

        return MathHelper.clamp(120000 / viscosity, 80, 600);
    }

    public NetworkGraph getGraph() {
        return graph;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Network network = (Network) o;
        return id.equals(network.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
