package com.raoulvdberge.refinedpipes.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.raoulvdberge.refinedpipes.block.FluidPipeBlock;
import com.raoulvdberge.refinedpipes.tile.FluidPipeTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.Direction;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

public class FluidPipeTileEntityRenderer extends TileEntityRenderer<FluidPipeTileEntity> {
    private static final float INSET = 0.001F;

    public FluidPipeTileEntityRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void render(FluidPipeTileEntity tile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferType, int combinedLight, int combinedOverlay) {
        FluidStack fluidStack = tile.getFluid();
        if (fluidStack.isEmpty()) {
            tile.updateAndGetRenderFullness(partialTicks);
            return;
        }

        int light = WorldRenderer.getCombinedLight(tile.getWorld(), tile.getPos());
        FluidAttributes attributes = fluidStack.getFluid().getAttributes();
        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(attributes.getStillTexture(fluidStack));
        int fluidColor = attributes.getColor(fluidStack);

        int r = fluidColor >> 16 & 0xFF;
        int g = fluidColor >> 8 & 0xFF;
        int b = fluidColor & 0xFF;
        int a = fluidColor >> 24 & 0xFF;

        IVertexBuilder buffer = bufferType.getBuffer(RenderType.getText(sprite.getAtlasTexture().getTextureLocation()));

        float fullness = tile.updateAndGetRenderFullness(partialTicks);
        if (fullness == 0) {
            return;
        }

        BlockState state = tile.getWorld().getBlockState(tile.getPos());
        if (state.get(FluidPipeBlock.NORTH)) {
            float x1 = 4;
            float y1 = 4;
            float z1 = 0;
            float x2 = 12;
            float y2 = 4 + (fullness * (12 - 4));
            float z2 = 4;

            CubeBuilder.INSTANCE.putCube(
                matrixStack,
                buffer,
                (x1 / 16F) + INSET,
                (y1 / 16F) + INSET,
                (z1 / 16F) + INSET,
                (x2 / 16F) - INSET,
                (y2 / 16F) - INSET,
                (z2 / 16F) - INSET,
                r,
                g,
                b,
                a,
                light,
                sprite,
                Direction.SOUTH
            );
        }

        if (state.get(FluidPipeBlock.EAST)) {
            float x1 = 12;
            float y1 = 4;
            float z1 = 4;
            float x2 = 16;
            float y2 = 4 + (fullness * (12 - 4));
            float z2 = 12;

            CubeBuilder.INSTANCE.putCube(
                matrixStack,
                buffer,
                (x1 / 16F) + INSET,
                (y1 / 16F) + INSET,
                (z1 / 16F) + INSET,
                (x2 / 16F) - INSET,
                (y2 / 16F) - INSET,
                (z2 / 16F) - INSET,
                r,
                g,
                b,
                a,
                light,
                sprite,
                Direction.WEST
            );
        }

        if (state.get(FluidPipeBlock.SOUTH)) {
            float x1 = 4;
            float y1 = 4;
            float z1 = 12;
            float x2 = 12;
            float y2 = 4 + (fullness * (12 - 4));
            float z2 = 16;

            CubeBuilder.INSTANCE.putCube(
                matrixStack,
                buffer,
                (x1 / 16F) + INSET,
                (y1 / 16F) + INSET,
                (z1 / 16F) + INSET,
                (x2 / 16F) - INSET,
                (y2 / 16F) - INSET,
                (z2 / 16F) - INSET,
                r,
                g,
                b,
                a,
                light,
                sprite,
                Direction.NORTH
            );
        }

        if (state.get(FluidPipeBlock.WEST)) {
            float x1 = 0;
            float y1 = 4;
            float z1 = 4;
            float x2 = 4;
            float y2 = 4 + (fullness * (12 - 4));
            float z2 = 12;

            CubeBuilder.INSTANCE.putCube(
                matrixStack,
                buffer,
                (x1 / 16F) + INSET,
                (y1 / 16F) + INSET,
                (z1 / 16F) + INSET,
                (x2 / 16F) - INSET,
                (y2 / 16F) - INSET,
                (z2 / 16F) - INSET,
                r,
                g,
                b,
                a,
                light,
                sprite,
                Direction.EAST
            );
        }

        if (state.get(FluidPipeBlock.UP)) {
            float x1 = 4;
            float y1 = 12;
            float z1 = 4;
            float x2 = 12;
            float y2 = 16;
            float z2 = 12;

            float shrinkage = (1F - fullness) * 4F;

            x1 += shrinkage;
            z1 += shrinkage;

            x2 -= shrinkage;
            z2 -= shrinkage;

            // If the lower core part isn't 100% full yet, we can go a bit lower.
            // Core Y is from 4  to 12
            // Up   Y is from 12 to 16
            // We should be able to go from Y 12 to Y 4.
            y1 -= (1F - fullness) * (12F - 4F);

            CubeBuilder.INSTANCE.putCube(
                matrixStack,
                buffer,
                (x1 / 16F) + INSET,
                (y1 / 16F) + INSET,
                (z1 / 16F) + INSET,
                (x2 / 16F) - INSET,
                (y2 / 16F) - INSET,
                (z2 / 16F) - INSET,
                r,
                g,
                b,
                a,
                light,
                sprite,
                Direction.DOWN
            );
        }

        if (state.get(FluidPipeBlock.DOWN)) {
            float x1 = 4;
            float y1 = 0;
            float z1 = 4;
            float x2 = 12;
            float y2 = 4;
            float z2 = 12;

            float shrinkage = (1F - fullness) * 4F;

            x1 += shrinkage;
            z1 += shrinkage;

            x2 -= shrinkage;
            z2 -= shrinkage;

            CubeBuilder.INSTANCE.putCube(
                matrixStack,
                buffer,
                (x1 / 16F) + INSET,
                (y1 / 16F) + INSET,
                (z1 / 16F) + INSET,
                (x2 / 16F) - INSET,
                (y2 / 16F) - INSET,
                (z2 / 16F) - INSET,
                r,
                g,
                b,
                a,
                light,
                sprite,
                Direction.UP
            );
        }

        {
            float x1 = 4;
            float y1 = 4;
            float z1 = 4;
            float x2 = 12;
            float y2 = 4 + (fullness * (12 - 4));
            float z2 = 12;

            matrixStack.push();

            CubeBuilder.INSTANCE.putFace(
                matrixStack,
                buffer,
                (x1 / 16F) + INSET,
                (y1 / 16F) + INSET,
                (z1 / 16F) + INSET,
                (x2 / 16F) - INSET,
                (y2 / 16F) - INSET,
                (z2 / 16F) - INSET,
                r,
                g,
                b,
                a,
                light,
                sprite,
                Direction.UP
            );

            CubeBuilder.INSTANCE.putFace(
                matrixStack,
                buffer,
                (x1 / 16F) + INSET,
                (y1 / 16F) + INSET,
                (z1 / 16F) + INSET,
                (x2 / 16F) - INSET,
                (y2 / 16F) - INSET,
                (z2 / 16F) - INSET,
                r,
                g,
                b,
                a,
                light,
                sprite,
                Direction.DOWN
            );

            if (!state.get(FluidPipeBlock.NORTH)) {
                CubeBuilder.INSTANCE.putFace(
                    matrixStack,
                    buffer,
                    (x1 / 16F) + INSET,
                    (y1 / 16F) + INSET,
                    (z1 / 16F) + INSET,
                    (x2 / 16F) - INSET,
                    (y2 / 16F) - INSET,
                    (z2 / 16F) - INSET,
                    r,
                    g,
                    b,
                    a,
                    light,
                    sprite,
                    Direction.NORTH
                );
            }

            if (!state.get(FluidPipeBlock.EAST)) {
                CubeBuilder.INSTANCE.putFace(
                    matrixStack,
                    buffer,
                    (x1 / 16F) + INSET,
                    (y1 / 16F) + INSET,
                    (z1 / 16F) + INSET,
                    (x2 / 16F) - INSET,
                    (y2 / 16F) - INSET,
                    (z2 / 16F) - INSET,
                    r,
                    g,
                    b,
                    a,
                    light,
                    sprite,
                    Direction.EAST
                );
            }

            if (!state.get(FluidPipeBlock.SOUTH)) {
                CubeBuilder.INSTANCE.putFace(
                    matrixStack,
                    buffer,
                    (x1 / 16F) + INSET,
                    (y1 / 16F) + INSET,
                    (z1 / 16F) + INSET,
                    (x2 / 16F) - INSET,
                    (y2 / 16F) - INSET,
                    (z2 / 16F) - INSET,
                    r,
                    g,
                    b,
                    a,
                    light,
                    sprite,
                    Direction.SOUTH
                );
            }

            if (!state.get(FluidPipeBlock.WEST)) {
                CubeBuilder.INSTANCE.putFace(
                    matrixStack,
                    buffer,
                    (x1 / 16F) + INSET,
                    (y1 / 16F) + INSET,
                    (z1 / 16F) + INSET,
                    (x2 / 16F) - INSET,
                    (y2 / 16F) - INSET,
                    (z2 / 16F) - INSET,
                    r,
                    g,
                    b,
                    a,
                    light,
                    sprite,
                    Direction.WEST
                );
            }

            matrixStack.pop();
        }
    }
}
