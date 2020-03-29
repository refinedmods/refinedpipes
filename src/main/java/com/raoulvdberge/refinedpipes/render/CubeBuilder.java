package com.raoulvdberge.refinedpipes.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;

public class CubeBuilder {
    public static final CubeBuilder INSTANCE = new CubeBuilder();

    private byte[] uvRotations = new byte[Direction.values().length];

    private CubeBuilder() {
    }

    public void addCube(MatrixStack matrixStack, IVertexBuilder buffer, float x1, float y1, float z1, float x2, float y2, float z2, int colorRed, int colorGreen, int colorBlue, int colorAlpha, int light, TextureAtlasSprite sprite) {
        matrixStack.push();

        for (Direction face : Direction.values()) {
            this.putFace(matrixStack, buffer, x1, y1, z1, x2, y2, z2, colorRed, colorGreen, colorBlue, colorAlpha, light, sprite, face);
        }

        matrixStack.pop();
    }

    public void putFace(MatrixStack matrixStack, IVertexBuilder buffer, float x1, float y1, float z1, float x2, float y2, float z2, int colorRed, int colorGreen, int colorBlue, int colorAlpha, int light, TextureAtlasSprite sprite, Direction face) {
        UvVector uv = getDefaultUv(face, sprite, x1, y1, z1, x2, y2, z2);

        switch (face) {
            case DOWN:
                this.putVertexTR(buffer, matrixStack, face, colorRed, colorGreen, colorBlue, colorAlpha, light, x2, y1, z1, uv);
                this.putVertexBR(buffer, matrixStack, face, colorRed, colorGreen, colorBlue, colorAlpha, light, x2, y1, z2, uv);
                this.putVertexBL(buffer, matrixStack, face, colorRed, colorGreen, colorBlue, colorAlpha, light, x1, y1, z2, uv);
                this.putVertexTL(buffer, matrixStack, face, colorRed, colorGreen, colorBlue, colorAlpha, light, x1, y1, z1, uv);
                break;
            case UP:
                this.putVertexTL(buffer, matrixStack, face, colorRed, colorGreen, colorBlue, colorAlpha, light, x1, y2, z1, uv);
                this.putVertexBL(buffer, matrixStack, face, colorRed, colorGreen, colorBlue, colorAlpha, light, x1, y2, z2, uv);
                this.putVertexBR(buffer, matrixStack, face, colorRed, colorGreen, colorBlue, colorAlpha, light, x2, y2, z2, uv);
                this.putVertexTR(buffer, matrixStack, face, colorRed, colorGreen, colorBlue, colorAlpha, light, x2, y2, z1, uv);
                break;
            case NORTH:
                this.putVertexBR(buffer, matrixStack, face, colorRed, colorGreen, colorBlue, colorAlpha, light, x2, y2, z1, uv);
                this.putVertexTR(buffer, matrixStack, face, colorRed, colorGreen, colorBlue, colorAlpha, light, x2, y1, z1, uv);
                this.putVertexTL(buffer, matrixStack, face, colorRed, colorGreen, colorBlue, colorAlpha, light, x1, y1, z1, uv);
                this.putVertexBL(buffer, matrixStack, face, colorRed, colorGreen, colorBlue, colorAlpha, light, x1, y2, z1, uv);
                break;
            case SOUTH:
                this.putVertexBL(buffer, matrixStack, face, colorRed, colorGreen, colorBlue, colorAlpha, light, x1, y2, z2, uv);
                this.putVertexTL(buffer, matrixStack, face, colorRed, colorGreen, colorBlue, colorAlpha, light, x1, y1, z2, uv);
                this.putVertexTR(buffer, matrixStack, face, colorRed, colorGreen, colorBlue, colorAlpha, light, x2, y1, z2, uv);
                this.putVertexBR(buffer, matrixStack, face, colorRed, colorGreen, colorBlue, colorAlpha, light, x2, y2, z2, uv);
                break;
            case WEST:
                this.putVertexTL(buffer, matrixStack, face, colorRed, colorGreen, colorBlue, colorAlpha, light, x1, y1, z1, uv);
                this.putVertexTR(buffer, matrixStack, face, colorRed, colorGreen, colorBlue, colorAlpha, light, x1, y1, z2, uv);
                this.putVertexBR(buffer, matrixStack, face, colorRed, colorGreen, colorBlue, colorAlpha, light, x1, y2, z2, uv);
                this.putVertexBL(buffer, matrixStack, face, colorRed, colorGreen, colorBlue, colorAlpha, light, x1, y2, z1, uv);
                break;
            case EAST:
                this.putVertexBR(buffer, matrixStack, face, colorRed, colorGreen, colorBlue, colorAlpha, light, x2, y2, z1, uv);
                this.putVertexBL(buffer, matrixStack, face, colorRed, colorGreen, colorBlue, colorAlpha, light, x2, y2, z2, uv);
                this.putVertexTL(buffer, matrixStack, face, colorRed, colorGreen, colorBlue, colorAlpha, light, x2, y1, z2, uv);
                this.putVertexTR(buffer, matrixStack, face, colorRed, colorGreen, colorBlue, colorAlpha, light, x2, y1, z1, uv);
                break;
        }

    }

    private UvVector getDefaultUv(Direction face, TextureAtlasSprite texture, float x1, float y1, float z1, float x2, float y2, float z2) {
        UvVector uv = new UvVector();

        switch (face) {
            case DOWN:
                uv.u1 = texture.getInterpolatedU(x1 * 16);
                uv.v1 = texture.getInterpolatedV(z1 * 16);
                uv.u2 = texture.getInterpolatedU(x2 * 16);
                uv.v2 = texture.getInterpolatedV(z2 * 16);
                break;
            case UP:
                uv.u1 = texture.getInterpolatedU(x1 * 16);
                uv.v1 = texture.getInterpolatedV(z1 * 16);
                uv.u2 = texture.getInterpolatedU(x2 * 16);
                uv.v2 = texture.getInterpolatedV(z2 * 16);
                break;
            case NORTH:
                uv.u1 = texture.getInterpolatedU(x1 * 16);
                uv.v1 = texture.getInterpolatedV(16 - y1 * 16);
                uv.u2 = texture.getInterpolatedU(x2 * 16);
                uv.v2 = texture.getInterpolatedV(16 - y2 * 16);
                break;
            case SOUTH:
                uv.u1 = texture.getInterpolatedU(x1 * 16);
                uv.v1 = texture.getInterpolatedV(16 - y1 * 16);
                uv.u2 = texture.getInterpolatedU(x2 * 16);
                uv.v2 = texture.getInterpolatedV(16 - y2 * 16);
                break;
            case WEST:
                uv.u1 = texture.getInterpolatedU(z1 * 16);
                uv.v1 = texture.getInterpolatedV(16 - y1 * 16);
                uv.u2 = texture.getInterpolatedU(z2 * 16);
                uv.v2 = texture.getInterpolatedV(16 - y2 * 16);
                break;
            case EAST:
                uv.u1 = texture.getInterpolatedU(z2 * 16);
                uv.v1 = texture.getInterpolatedV(16 - y1 * 16);
                uv.u2 = texture.getInterpolatedU(z1 * 16);
                uv.v2 = texture.getInterpolatedV(16 - y2 * 16);
                break;
        }

        return uv;
    }

    // uv.u1, uv.v1
    private void putVertexTL(IVertexBuilder builder, MatrixStack matrixStack, Direction face, int colorRed, int colorGreen, int colorBlue, int colorAlpha, int light, float x, float y, float z, UvVector uv) {
        float u, v;

        switch (this.uvRotations[face.ordinal()]) {
            default:
            case 0:
                u = uv.u1;
                v = uv.v1;
                break;
            case 1: // 90° clockwise
                u = uv.u1;
                v = uv.v2;
                break;
            case 2: // 180° clockwise
                u = uv.u2;
                v = uv.v2;
                break;
            case 3: // 270° clockwise
                u = uv.u2;
                v = uv.v1;
                break;
        }

        this.putVertex(builder, matrixStack, colorRed, colorGreen, colorBlue, colorAlpha, light, x, y, z, u, v);
    }

    // uv.u2, uv.v1
    private void putVertexTR(IVertexBuilder builder, MatrixStack matrixStack, Direction face, int colorRed, int colorGreen, int colorBlue, int colorAlpha, int light, float x, float y, float z, UvVector uv) {
        float u, v;

        switch (this.uvRotations[face.ordinal()]) {
            default:
            case 0:
                u = uv.u2;
                v = uv.v1;
                break;
            case 1: // 90° clockwise
                u = uv.u1;
                v = uv.v1;
                break;
            case 2: // 180° clockwise
                u = uv.u1;
                v = uv.v2;
                break;
            case 3: // 270° clockwise
                u = uv.u2;
                v = uv.v2;
                break;
        }

        this.putVertex(builder, matrixStack, colorRed, colorGreen, colorBlue, colorAlpha, light, x, y, z, u, v);
    }

    // uv.u2, uv.v2
    private void putVertexBR(IVertexBuilder builder, MatrixStack matrixStack, Direction face, int colorRed, int colorGreen, int colorBlue, int colorAlpha, int light, float x, float y, float z, UvVector uv) {

        float u;
        float v;

        switch (this.uvRotations[face.ordinal()]) {
            default:
            case 0:
                u = uv.u2;
                v = uv.v2;
                break;
            case 1: // 90° clockwise
                u = uv.u2;
                v = uv.v1;
                break;
            case 2: // 180° clockwise
                u = uv.u1;
                v = uv.v1;
                break;
            case 3: // 270° clockwise
                u = uv.u1;
                v = uv.v2;
                break;
        }

        this.putVertex(builder, matrixStack, colorRed, colorGreen, colorBlue, colorAlpha, light, x, y, z, u, v);
    }

    // uv.u1, uv.v2
    private void putVertexBL(IVertexBuilder builder, MatrixStack matrixStack, Direction face, int colorRed, int colorGreen, int colorBlue, int colorAlpha, int light, float x, float y, float z, UvVector uv) {

        float u;
        float v;

        switch (this.uvRotations[face.ordinal()]) {
            default:
            case 0:
                u = uv.u1;
                v = uv.v2;
                break;
            case 1: // 90° clockwise
                u = uv.u2;
                v = uv.v2;
                break;
            case 2: // 180° clockwise
                u = uv.u2;
                v = uv.v1;
                break;
            case 3: // 270° clockwise
                u = uv.u1;
                v = uv.v1;
                break;
        }

        this.putVertex(builder, matrixStack, colorRed, colorGreen, colorBlue, colorAlpha, light, x, y, z, u, v);
    }

    public void putVertex(IVertexBuilder builder, MatrixStack matrixStack, int colorRed, int colorGreen, int colorBlue, int colorAlpha, int light, float x, float y, float z, float u, float v) {
        builder.pos(matrixStack.getLast().getMatrix(), x, y, z)
            .color(colorRed, colorGreen, colorBlue, colorAlpha)
            .tex(u, v)
            .lightmap(light)
            .endVertex();
    }

    private static final class UvVector {
        float u1;
        float u2;
        float v1;
        float v2;
    }
}
