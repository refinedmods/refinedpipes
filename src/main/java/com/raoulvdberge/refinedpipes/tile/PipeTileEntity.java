package com.raoulvdberge.refinedpipes.tile;

import com.raoulvdberge.refinedpipes.RefinedPipesTileEntities;
import com.raoulvdberge.refinedpipes.network.NetworkManager;
import com.raoulvdberge.refinedpipes.network.Pipe;
import com.raoulvdberge.refinedpipes.render.Color;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

public class PipeTileEntity extends TileEntity implements ITickableTileEntity {
    private Color color;

    public PipeTileEntity() {
        super(RefinedPipesTileEntities.PIPE);
    }

    @Override
    public void tick() {

    }

    public CompoundNBT writeUpdate(CompoundNBT tag) {
        Pipe pipe = NetworkManager.get(world).getPipe(pos);

        if (pipe != null && pipe.getNetwork() != null) {
            pipe.getNetwork().getColor().writeToTag(tag);
        }

        return tag;
    }

    public void readUpdate(CompoundNBT tag) {
        this.color = Color.fromNbt(tag);
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

    @Override
    public void validate() {
        super.validate();

        if (!world.isRemote) {
            NetworkManager.get(world).addPipe(new Pipe(world, pos));
        }
    }

    @Override
    public void remove() {
        super.remove();

        if (!world.isRemote) {
            NetworkManager.get(world).removePipe(new Pipe(world, pos));
        }
    }

    public Color getColor() {
        return color;
    }
}
