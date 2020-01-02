package com.raoulvdberge.refinedpipes.network.graph;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NetworkGraphScannerRequest {
    private final World world;
    private final BlockPos pos;

    public NetworkGraphScannerRequest(World world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
    }

    public World getWorld() {
        return world;
    }

    public BlockPos getPos() {
        return pos;
    }
}
