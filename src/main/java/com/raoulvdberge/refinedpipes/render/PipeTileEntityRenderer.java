package com.raoulvdberge.refinedpipes.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.raoulvdberge.refinedpipes.item.BlockItemBase;
import com.raoulvdberge.refinedpipes.tile.PipeTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import org.lwjgl.opengl.GL11;

public class PipeTileEntityRenderer extends TileEntityRenderer<PipeTileEntity> {
    public PipeTileEntityRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(PipeTileEntity tile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferType, int combinedLight, int combinedOverlay) {
        if (tile.getStack() != null) {
            matrixStack.push();

            Direction dir = tile.getDirection();

            double pipeLength = 1D;

            if (tile.isFirstPipe()) {
                pipeLength = 1.5D; // Every transport starts in the center to go to the next center. But for the first pipe we start from the beginning, so it's a bit longer.
                // [X][X][X]
                // X = A center
                // []= The pipe casing
                // For the first pipe we start at [, not X. So hence the 1.5
            }

            if (tile.isLastPipe()) {
                pipeLength = 0.25D; // Every transport starts in the center to go to the next center. But for the last pipe we only want to go to the end part, not the "next" center.
                // [X][X][Y]</>
                // X/Y= A center
                // [] = The pipe casing
                // For the last pipe we start at Y, and go to ]. We don't want to go to /.
            }

            double maxTicksInPipe = (double) tile.getMaxTicksInPipe() * pipeLength;

            double v = (((double) tile.getProgress() + partialTicks) / maxTicksInPipe) * pipeLength;

            if (tile.isFirstPipe() && v < 0.5) {
                dir = tile.getInitialDirection(); // Get the item out first
            }

            if (tile.isFirstPipe()) {
                v -= 0.5D; // Every transport starts in the center. For the first pipe, we want to start from the beginning. Remove the centering.
            }

            matrixStack.translate(
                0.5 + (dir.getXOffset() * v),
                0.5 + (dir.getYOffset() * v),
                0.5 + (dir.getZOffset() * v)
            );
            matrixStack.scale(0.5F, 0.5F, 0.5F);

            Minecraft.getInstance().getItemRenderer().renderItem(
                tile.getStack(),
                ItemCameraTransforms.TransformType.FIXED,
                combinedLight,
                combinedOverlay,
                matrixStack,
                bufferType
            );

            matrixStack.pop();
        }
    }
}
