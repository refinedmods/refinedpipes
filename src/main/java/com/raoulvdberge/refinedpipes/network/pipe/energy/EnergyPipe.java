package com.raoulvdberge.refinedpipes.network.pipe.energy;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.network.NetworkManager;
import com.raoulvdberge.refinedpipes.network.energy.EnergyNetwork;
import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyPipe extends Pipe {
    public static final ResourceLocation ID = new ResourceLocation(RefinedPipes.ID, "energy");

    private final EnergyPipeType type;

    public EnergyPipe(World world, BlockPos pos, EnergyPipeType type) {
        super(world, pos);
        this.type = type;
    }

    public EnergyPipeType getType() {
        return type;
    }

    @Override
    public void update(World world) {
        super.update(world);

        for (Direction dir : Direction.values()) {
            TileEntity tile = world.getTileEntity(pos.offset(dir));
            if (tile == null) {
                continue;
            }

            tile.getCapability(CapabilityEnergy.ENERGY, dir.getOpposite())
                .ifPresent(energyStorage -> extractEnergy((EnergyNetwork) network, energyStorage));
        }
    }

    private void extractEnergy(EnergyNetwork network, IEnergyStorage source) {
        if (!source.canExtract()) {
            return;
        }

        int extracted = source.extractEnergy(type.getToExtract(), true);
        if (extracted == 0) {
            return;
        }

        int receivedInNetwork = network.getEnergyStorage().receiveEnergy(extracted, true);
        if (receivedInNetwork <= 0) {
            return;
        }

        int toDrain = Math.min(type.getToExtract(), receivedInNetwork);

        extracted = source.extractEnergy(toDrain, false);

        network.getEnergyStorage().receiveEnergy(extracted, false);

        NetworkManager.get(world).markDirty();
    }

    @Override
    public CompoundNBT writeToNbt(CompoundNBT tag) {
        tag = super.writeToNbt(tag);

        tag.putInt("type", type.ordinal());

        return tag;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public ResourceLocation getNetworkType() {
        return EnergyNetwork.TYPE;
    }
}
