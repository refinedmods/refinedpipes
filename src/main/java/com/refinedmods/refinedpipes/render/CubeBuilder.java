package com.refinedmods.refinedpipes.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

import javax.annotation.Nullable;

public class CubeBuilder {
    public static final CubeBuilder INSTANCE = new CubeBuilder();

    private final byte[] uvRotations = new byte[Direction.values().length];

    private CubeBuilder() {
    }

    public void putCube(PoseStack matrixStack, VertexConsumer builder, float x1, float y1, float z1, float x2, float y2, float z2, int r, int g, int b, int a, int light, TextureAtlasSprite sprite) {
        putCube(matrixStack, builder, x1, y1, z1, x2, y2, z2, r, g, b, a, light, sprite, null);
    }

    public void putCube(PoseStack matrixStack, VertexConsumer builder, float x1, float y1, float z1, float x2, float y2, float z2, int r, int g, int b, int a, int light, TextureAtlasSprite sprite, @Nullable Direction exclude) {
        matrixStack.pushPose();

        for (Direction face : Direction.values()) {
            if (face != exclude) {
                putFace(matrixStack, builder, x1, y1, z1, x2, y2, z2, r, g, b, a, light, sprite, face);
            }
        }

        matrixStack.popPose();
    }

    public void putFace(PoseStack matrixStack, VertexConsumer builder, float x1, float y1, float z1, float x2, float y2, float z2, int r, int g, int b, int a, int light, TextureAtlasSprite sprite, Direction face) {
        UvVector uv = getDefaultUv(face, sprite, x1, y1, z1, x2, y2, z2);

        switch (face) {
            case DOWN:
                this.putVertexTR(builder, matrixStack, face, r, g, b, a, light, x2, y1, z1, uv);
                this.putVertexBR(builder, matrixStack, face, r, g, b, a, light, x2, y1, z2, uv);
                this.putVertexBL(builder, matrixStack, face, r, g, b, a, light, x1, y1, z2, uv);
                this.putVertexTL(builder, matrixStack, face, r, g, b, a, light, x1, y1, z1, uv);
                break;
            case UP:
                this.putVertexTL(builder, matrixStack, face, r, g, b, a, light, x1, y2, z1, uv);
                this.putVertexBL(builder, matrixStack, face, r, g, b, a, light, x1, y2, z2, uv);
                this.putVertexBR(builder, matrixStack, face, r, g, b, a, light, x2, y2, z2, uv);
                this.putVertexTR(builder, matrixStack, face, r, g, b, a, light, x2, y2, z1, uv);
                break;
            case NORTH:
                this.putVertexBR(builder, matrixStack, face, r, g, b, a, light, x2, y2, z1, uv);
                this.putVertexTR(builder, matrixStack, face, r, g, b, a, light, x2, y1, z1, uv);
                this.putVertexTL(builder, matrixStack, face, r, g, b, a, light, x1, y1, z1, uv);
                this.putVertexBL(builder, matrixStack, face, r, g, b, a, light, x1, y2, z1, uv);
                break;
            case SOUTH:
                this.putVertexBL(builder, matrixStack, face, r, g, b, a, light, x1, y2, z2, uv);
                this.putVertexTL(builder, matrixStack, face, r, g, b, a, light, x1, y1, z2, uv);
                this.putVertexTR(builder, matrixStack, face, r, g, b, a, light, x2, y1, z2, uv);
                this.putVertexBR(builder, matrixStack, face, r, g, b, a, light, x2, y2, z2, uv);
                break;
            case WEST:
                this.putVertexTL(builder, matrixStack, face, r, g, b, a, light, x1, y1, z1, uv);
                this.putVertexTR(builder, matrixStack, face, r, g, b, a, light, x1, y1, z2, uv);
                this.putVertexBR(builder, matrixStack, face, r, g, b, a, light, x1, y2, z2, uv);
                this.putVertexBL(builder, matrixStack, face, r, g, b, a, light, x1, y2, z1, uv);
                break;
            case EAST:
                this.putVertexBR(builder, matrixStack, face, r, g, b, a, light, x2, y2, z1, uv);
                this.putVertexBL(builder, matrixStack, face, r, g, b, a, light, x2, y2, z2, uv);
                this.putVertexTL(builder, matrixStack, face, r, g, b, a, light, x2, y1, z2, uv);
                this.putVertexTR(builder, matrixStack, face, r, g, b, a, light, x2, y1, z1, uv);
                break;
        }

    }

    private UvVector getDefaultUv(Direction face, TextureAtlasSprite texture, float x1, float y1, float z1, float x2, float y2, float z2) {
        UvVector uv = new UvVector();

        switch (face) {
            case DOWN:
                uv.u1 = texture.getU(x1 * 16);
                uv.v1 = texture.getV(z1 * 16);
                uv.u2 = texture.getU(x2 * 16);
                uv.v2 = texture.getV(z2 * 16);
                break;
            case UP:
                uv.u1 = texture.getU(x1 * 16);
                uv.v1 = texture.getV(z1 * 16);
                uv.u2 = texture.getU(x2 * 16);
                uv.v2 = texture.getV(z2 * 16);
                break;
            case NORTH:
                uv.u1 = texture.getU(x1 * 16);
                uv.v1 = texture.getV(16 - y1 * 16);
                uv.u2 = texture.getU(x2 * 16);
                uv.v2 = texture.getV(16 - y2 * 16);
                break;
            case SOUTH:
                uv.u1 = texture.getU(x1 * 16);
                uv.v1 = texture.getV(16 - y1 * 16);
                uv.u2 = texture.getU(x2 * 16);
                uv.v2 = texture.getV(16 - y2 * 16);
                break;
            case WEST:
                uv.u1 = texture.getU(z1 * 16);
                uv.v1 = texture.getV(16 - y1 * 16);
                uv.u2 = texture.getU(z2 * 16);
                uv.v2 = texture.getV(16 - y2 * 16);
                break;
            case EAST:
                uv.u1 = texture.getU(z2 * 16);
                uv.v1 = texture.getV(16 - y1 * 16);
                uv.u2 = texture.getU(z1 * 16);
                uv.v2 = texture.getV(16 - y2 * 16);
                break;
        }

        return uv;
    }

    // uv.u1, uv.v1
    private void putVertexTL(VertexConsumer builder, PoseStack matrixStack, Direction face, int r, int g, int b, int a, int light, float x, float y, float z, UvVector uv) {
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

        this.putVertex(builder, matrixStack, r, g, b, a, light, x, y, z, u, v);
    }

    // uv.u2, uv.v1
    private void putVertexTR(VertexConsumer builder, PoseStack matrixStack, Direction face, int r, int g, int b, int a, int light, float x, float y, float z, UvVector uv) {
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

        this.putVertex(builder, matrixStack, r, g, b, a, light, x, y, z, u, v);
    }

    // uv.u2, uv.v2
    private void putVertexBR(VertexConsumer builder, PoseStack matrixStack, Direction face, int r, int g, int b, int a, int light, float x, float y, float z, UvVector uv) {

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

        this.putVertex(builder, matrixStack, r, g, b, a, light, x, y, z, u, v);
    }

    // uv.u1, uv.v2
    private void putVertexBL(VertexConsumer builder, PoseStack matrixStack, Direction face, int r, int g, int b, int a, int light, float x, float y, float z, UvVector uv) {

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

        this.putVertex(builder, matrixStack, r, g, b, a, light, x, y, z, u, v);
    }

    private void putVertex(VertexConsumer builder, PoseStack matrixStack, int r, int g, int b, int a, int light, float x, float y, float z, float u, float v) {
        builder.vertex(matrixStack.last().pose(), x, y, z)
            .color(r, g, b, a)
            .uv(u, v)
            .uv2(light)
            .endVertex();
    }

    private static final class UvVector {
        float u1;
        float u2;
        float v1;
        float v2;
    }
}
