package com.refinedmods.refinedpipes.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedpipes.network.pipe.transport.ItemTransportProps;
import com.refinedmods.refinedpipes.tile.ItemPipeTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Quaternion;

public class ItemPipeTileEntityRenderer extends TileEntityRenderer<ItemPipeTileEntity> {
    public ItemPipeTileEntityRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void render(ItemPipeTileEntity tile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferType, int combinedLight, int combinedOverlay) {
        for (ItemTransportProps prop : tile.getProps()) {
            Direction dir = prop.getDirection();

            double pipeLength = 1D;

            if (prop.isFirstPipe()) {
                pipeLength = 1.25D; // Every transport starts in the center to go to the next center. But for the first pipe we start from the beginning, so it's a bit longer.
                // [X][X][X]
                // X = A center
                // []= The pipe casing
                // For the first pipe we start at [, not X. So hence the 1.5
            }

            if (prop.isLastPipe()) {
                pipeLength = 0.25D; // Every transport starts in the center to go to the next center. But for the last pipe we only want to go to the end part, not the "next" center.
                // [X][X][Y]</>
                // X/Y= A center
                // [] = The pipe casing
                // For the last pipe we start at Y, and go to ]. We don't want to go to /.
            }

            double maxTicksInPipe = (double) prop.getMaxTicksInPipe() * pipeLength;

            double v = (((double) prop.getProgress() + partialTicks) / maxTicksInPipe) * pipeLength;

            if (prop.isFirstPipe() && v < 0.25) {
                dir = prop.getInitialDirection(); // Get the item out first
            }

            if (prop.isFirstPipe()) {
                v -= 0.25D; // Every transport starts in the center. For the first pipe, we want to start from the beginning. Remove the centering.
            }

            // If the next pipe is gone..
            if (v > 0.25 && tile.getWorld().isAirBlock(tile.getPos().offset(prop.getDirection()))) {
                continue;
            }

            v = Math.min(1F, v);

            matrixStack.push();

            matrixStack.translate(
                0.5 + (dir.getXOffset() * v),
                0.5 + (dir.getYOffset() * v),
                0.5 + (dir.getZOffset() * v)
            );
            matrixStack.rotate(new Quaternion(0, (float) ((Minecraft.getInstance().world.getGameTime() / 25D) % (Math.PI * 2) + (partialTicks / 25D)), 0, false));
            matrixStack.scale(0.5F, 0.5F, 0.5F);

            Minecraft.getInstance().getItemRenderer().renderItem(
                prop.getStack(),
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
