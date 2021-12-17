package com.refinedmods.refinedpipes.network.graph;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class NetworkGraphScannerRequest {
    private final Level level;
    private final BlockPos pos;
    @Nullable
    private final Direction direction;
    @Nullable
    private final NetworkGraphScannerRequest parent;
    private boolean successful;

    public NetworkGraphScannerRequest(Level level, BlockPos pos, @Nullable Direction direction, @Nullable NetworkGraphScannerRequest parent) {
        this.level = level;
        this.pos = pos;
        this.direction = direction;
        this.parent = parent;
    }

    public Level getLevel() {
        return level;
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
