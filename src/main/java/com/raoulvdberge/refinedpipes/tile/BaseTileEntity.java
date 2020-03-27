package com.raoulvdberge.refinedpipes.tile;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public abstract class BaseTileEntity extends TileEntity {
    public BaseTileEntity(TileEntityType<?> type) {
        super(type);
    }

    @Override
    public final CompoundNBT getUpdateTag() {
        return writeUpdate(super.getUpdateTag());
    }

    @Override
    public final SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, 1, getUpdateTag());
    }

    @Override
    public final void onDataPacket(net.minecraft.network.NetworkManager net, SUpdateTileEntityPacket packet) {
        readUpdate(packet.getNbtCompound());
    }

    @Override
    public final void handleUpdateTag(CompoundNBT tag) {
        super.read(tag);

        readUpdate(tag);
    }

    public CompoundNBT writeUpdate(CompoundNBT tag) {
        return tag;
    }

    public void readUpdate(CompoundNBT tag) {
    }
}
