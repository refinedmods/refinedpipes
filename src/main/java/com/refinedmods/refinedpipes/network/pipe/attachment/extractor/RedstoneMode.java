package com.refinedmods.refinedpipes.network.pipe.attachment.extractor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public enum RedstoneMode {
    IGNORED,
    HIGH,
    LOW;

    public static RedstoneMode get(byte b) {
        RedstoneMode[] m = values();

        if (b < 0 || b >= m.length) {
            return IGNORED;
        }

        return m[b];
    }

    public boolean isEnabled(Level world, BlockPos pos) {
        switch (this) {
            case IGNORED:
                return true;
            case HIGH:
                return world.hasNeighborSignal(pos);
            case LOW:
                return !world.hasNeighborSignal(pos);
            default:
                return false;
        }
    }

    public RedstoneMode next() {
        switch (this) {
            case IGNORED:
                return HIGH;
            case HIGH:
                return LOW;
            case LOW:
                return IGNORED;
            default:
                return IGNORED;
        }
    }
}
