package com.refinedmods.refinedpipes.network.graph;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class NetworkGraphScannerRequest {
    private final Level world;
    private final BlockPos pos;
    @Nullable
    private final Direction direction;
    @Nullable
    private final NetworkGraphScannerRequest parent;
    private boolean successful;

    public NetworkGraphScannerRequest(Level world, BlockPos pos, @Nullable Direction direction, @Nullable NetworkGraphScannerRequest parent) {
        this.world = world;
        this.pos = pos;
        this.direction = direction;
        this.parent = parent;
    }

    public Level getWorld() {
        return world;
    }

    public BlockPos getPos() {
        return pos;
    }

    @Nullable
    public Direction getDirection() {
        return direction;
    }

    @Nullable
    public NetworkGraphScannerRequest getParent() {
        return parent;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
}
