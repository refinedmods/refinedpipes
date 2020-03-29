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
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

public class FluidPipeTileEntityRenderer extends TileEntityRenderer<FluidPipeTileEntity> {
    public FluidPipeTileEntityRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void render(FluidPipeTileEntity tile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferType, int combinedLight, int combinedOverlay) {
        FluidStack fluidStack = new FluidStack(Fluids.WATER, 1000);

        int light = WorldRenderer.getCombinedLight(tile.getWorld(), tile.getPos());
        FluidAttributes attributes = fluidStack.getFluid().getAttributes();
        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(attributes.getStillTexture(fluidStack));
        int fluidColor = attributes.getColor(fluidStack);

        int colorRed = fluidColor >> 16 & 0xFF;
        int colorGreen = fluidColor >> 8 & 0xFF;
        int colorBlue = fluidColor & 0xFF;
        int colorAlpha = fluidColor >> 24 & 0xFF;

        IVertexBuilder buffer = bufferType.getBuffer(RenderType.getText(sprite.getAtlasTexture().getTextureLocation()));

        float fullness = 0.50F;

        BlockState state = tile.getWorld().getBlockState(tile.getPos());
        if (state.get(FluidPipeBlock.NORTH)) {
            float x1 = 4;
            float y1 = 4;
            float z1 = 0;
            float x2 = 12;
            float y2 = 4 + (fullness * (12 - 4));
            float z2 = 4;

            CubeBuilder.INSTANCE.addCube(matrixStack, buffer, (x1 / 16F) + 0.001F, (y1 / 16F) + 0.001F, (z1 / 16F) + 0.001F, (x2 / 16F) - 0.001F, (y2 / 16F) - 0.001F, (z2 / 16F) - 0.001F, colorRed, colorGreen, colorBlue, colorAlpha, light, sprite);
        }

        if (state.get(FluidPipeBlock.EAST)) {
            float x1 = 12;
            float y1 = 4;
            float z1 = 4;
            float x2 = 16;
            float y2 = 4 + (fullness * (12 - 4));
            float z2 = 12;

            CubeBuilder.INSTANCE.addCube(matrixStack, buffer, (x1 / 16F) + 0.001F, (y1 / 16F) + 0.001F, (z1 / 16F) + 0.001F, (x2 / 16F) - 0.001F, (y2 / 16F) - 0.001F, (z2 / 16F) - 0.001F, colorRed, colorGreen, colorBlue, colorAlpha, light, sprite);
        }

        if (state.get(FluidPipeBlock.SOUTH)) {
            float x1 = 4;
            float y1 = 4;
            float z1 = 12;
            float x2 = 12;
            float y2 = 4 + (fullness * (12 - 4));
            float z2 = 16;

            CubeBuilder.INSTANCE.addCube(matrixStack, buffer, (x1 / 16F) + 0.001F, (y1 / 16F) + 0.001F, (z1 / 16F) + 0.001F, (x2 / 16F) - 0.001F, (y2 / 16F) - 0.001F, (z2 / 16F) - 0.001F, colorRed, colorGreen, colorBlue, colorAlpha, light, sprite);
        }

        if (state.get(FluidPipeBlock.WEST)) {
            float x1 = 0;
            float y1 = 4;
            float z1 = 4;
            float x2 = 4;
            float y2 = 4 + (fullness * (12 - 4));
            float z2 = 12;

            CubeBuilder.INSTANCE.addCube(matrixStack, buffer, (x1 / 16F) + 0.001F, (y1 / 16F) + 0.001F, (z1 / 16F) + 0.001F, (x2 / 16F) - 0.001F, (y2 / 16F) - 0.001F, (z2 / 16F) - 0.001F, colorRed, colorGreen, colorBlue, colorAlpha, light, sprite);
        }

        if (state.get(FluidPipeBlock.UP)) {
            float x1 = 4;
            float y1 = 12;
            float z1 = 4;
            float x2 = 12;
            float y2 = 16;
            float z2 = 12;

            int offset = 3;

            x1 += offset;
            z1 += offset;

            x2 -= offset;
            z2 -= offset;

            CubeBuilder.INSTANCE.addCube(matrixStack, buffer, (x1 / 16F) + 0.001F, (y1 / 16F) + 0.001F, (z1 / 16F) + 0.001F, (x2 / 16F) - 0.001F, (y2 / 16F) - 0.001F, (z2 / 16F) - 0.001F, colorRed, colorGreen, colorBlue, colorAlpha, light, sprite);
        }

        if (state.get(FluidPipeBlock.DOWN)) {
            float x1 = 4;
            float y1 = 0;
            float z1 = 4;
            float x2 = 12;
            float y2 = 4;
            float z2 = 12;

            int offset = 3;

            x1 += offset;
            z1 += offset;

            x2 -= offset;
            z2 -= offset;

            CubeBuilder.INSTANCE.addCube(matrixStack, buffer, (x1 / 16F) + 0.001F, (y1 / 16F) + 0.001F, (z1 / 16F) + 0.001F, (x2 / 16F) - 0.001F, (y2 / 16F) - 0.001F, (z2 / 16F) - 0.001F, colorRed, colorGreen, colorBlue, colorAlpha, light, sprite);
        }

        {
            float x1 = 4;
            float y1 = 4;
            float z1 = 4;
            float x2 = 12;
            float y2 = 4 + (fullness * (12 - 4));
            float z2 = 12;

            CubeBuilder.INSTANCE.addCube(matrixStack, buffer, (x1 / 16F) + 0.001F, (y1 / 16F) + 0.001F, (z1 / 16F) + 0.001F, (x2 / 16F) - 0.001F, (y2 / 16F) - 0.001F, (z2 / 16F) - 0.001F, colorRed, colorGreen, colorBlue, colorAlpha, light, sprite);
        }
    }
}
