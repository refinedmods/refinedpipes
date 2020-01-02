package com.raoulvdberge.refinedpipes.render;

import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;

public class Color {
    private final int r;
    private final int g;
    private final int b;

    public Color(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    public void writeToTag(CompoundNBT tag) {
        tag.putInt("R", r);
        tag.putInt("G", g);
        tag.putInt("B", b);
    }

    @Nullable
    public static Color fromNbt(CompoundNBT tag) {
        if (tag.contains("R") && tag.contains("G") && tag.contains("B")) {
            return new Color(
                tag.getInt("R"),
                tag.getInt("G"),
                tag.getInt("B")
            );
        }

        return null;
    }
}
