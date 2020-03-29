package com.raoulvdberge.refinedpipes.tile;

import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.pipe.item.ItemPipe;
import com.raoulvdberge.refinedpipes.network.pipe.item.ItemPipeType;
import com.raoulvdberge.refinedpipes.network.pipe.transport.ItemTransport;
import com.raoulvdberge.refinedpipes.network.pipe.transport.ItemTransportProps;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemPipeTileEntity extends PipeTileEntity {

    private final FluidTank tank = new FluidTank(FluidAttributes.BUCKET_VOLUME * 1000);

    private List<ItemTransportProps> props = new ArrayList<>();

    private final ItemPipeType type;

    public ItemPipeTileEntity(ItemPipeType type) {
        super(type.getTileType());

        this.type = type;
    }

    @Override
    public void tick() {
        if (world.isRemote) {
            props.forEach(ItemTransportProps::tick);
        } else {
            tank.setFluid(new FluidStack(Fluids.LAVA, 1000));
        }
    }

    public List<ItemTransportProps> getProps() {
        return props;
    }

    public void setProps(List<ItemTransportProps> props) {
        this.props = props;
    }

    @Override
    protected void spawnDrops(Pipe pipe) {
        super.spawnDrops(pipe);

        for (ItemTransport transport : ((ItemPipe) pipe).getTransports()) {
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), transport.getValue());
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return LazyOptional.of(() -> tank).cast();

        return super.getCapability(cap, side);
    }

    @Override
    protected Pipe createPipe(World world, BlockPos pos) {
        return new ItemPipe(world, pos, type);
    }
}
