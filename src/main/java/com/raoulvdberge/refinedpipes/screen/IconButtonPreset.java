package com.raoulvdberge.refinedpipes.screen;

public class IconButtonPreset {
    public static final IconButtonPreset NORMAL = new IconButtonPreset(20, 20, 177, 0, 20, 40);
    public static final IconButtonPreset SMALL = new IconButtonPreset(14, 14, 242, 0, 14, 28);

    private final int width;
    private final int height;
    private final int xTex;
    private final int yTexNormal;
    private final int yTexHover;
    private final int yTexDisabled;

    private IconButtonPreset(int width, int height, int xTex, int yTexNormal, int yTexHover, int yTexDisabled) {
        this.width = width;
        this.height = height;
        this.xTex = xTex;
        this.yTexNormal = yTexNormal;
        this.yTexHover = yTexHover;
        this.yTexDisabled = yTexDisabled;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getXTex() {
        return xTex;
    }

    public int getYTexNormal() {
        return yTexNormal;
    }

    public int getYTexHover() {
        return yTexHover;
    }

    public int getYTexDisabled() {
        return yTexDisabled;
    }
}
