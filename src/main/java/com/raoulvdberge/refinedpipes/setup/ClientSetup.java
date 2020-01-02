package com.raoulvdberge.refinedpipes.setup;

import com.raoulvdberge.refinedpipes.render.PipeTileEntityRenderer;
import com.raoulvdberge.refinedpipes.tile.PipeTileEntity;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientSetup {
    public ClientSetup() {
        ClientRegistry.bindTileEntitySpecialRenderer(PipeTileEntity.class, new PipeTileEntityRenderer());
    }
}
