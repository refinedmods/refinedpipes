package com.raoulvdberge.refinedpipes.tile;

import com.raoulvdberge.refinedpipes.RefinedPipesTiles;
import com.raoulvdberge.refinedpipes.network.NetworkManager;
import com.raoulvdberge.refinedpipes.network.Pipe;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

public class PipeTile extends TileEntity implements ITickableTileEntity {
    public PipeTile() {
        super(RefinedPipesTiles.PIPE);
    }

    @Override
    public void tick() {

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
}
