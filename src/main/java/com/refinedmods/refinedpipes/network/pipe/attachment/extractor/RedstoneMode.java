package com.refinedmods.refinedpipes.network.pipe.attachment.extractor;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public enum RedstoneMode {
    IGNORED,
    HIGH,
    LOW;

    public boolean isEnabled(World world, BlockPos pos) {
        switch (this) {
            case IGNORED:
                return true;
            case HIGH:
                return world.isBlockPowered(pos);
            case LOW:
                return !world.isBlockPowered(pos);
            default:
                return false;
        }
    }

    public static RedstoneMode get(byte b) {
        RedstoneMode[] m = values();

        if (b < 0 || b >= m.length) {
            return IGNORED;
        }

        return m[b];
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
