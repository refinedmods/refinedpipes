package com.raoulvdberge.refinedpipes.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.raoulvdberge.refinedpipes.item.BlockItemBase;
import com.raoulvdberge.refinedpipes.tile.PipeTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class PipeTileEntityRenderer extends TileEntityRenderer<PipeTileEntity> {
    @Override
    public void render(PipeTileEntity tile, double x, double y, double z, float partialTicks, int destroyStage) {
        super.render(tile, x, y, z, partialTicks, destroyStage);

        if (tile.getColor() == null || !(Minecraft.getInstance().player.inventory.getCurrentItem().getItem() instanceof BlockItemBase)) {
            return;
        }

        GlStateManager.pushMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.translated(x, y, z);
        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull();
        GlStateManager.disableDepthTest();
        GlStateManager.depthMask(false);
        GlStateManager.color4f(1F, 1F, 1F, 1F);

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();

        double minX = 0, minY = 0, minZ = 0;
        double maxX = 1, maxY = 1, maxZ = 1;

        float r = (float) tile.getColor().getR() / 255F;
        float g = (float) tile.getColor().getG() / 255F;
        float b = (float) tile.getColor().getB() / 255F;
        float a = 0.20F;

        // case DOWN:
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(minX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(minX, minY, maxZ).color(r, g, b, a).endVertex();
        Tessellator.getInstance().draw();

        // case UP:
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(minX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(minX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, maxY, minZ).color(r, g, b, a).endVertex();
        Tessellator.getInstance().draw();

        // case NORTH:
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(minX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(minX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, minY, minZ).color(r, g, b, a).endVertex();
        Tessellator.getInstance().draw();

        // case SOUTH:
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(minX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(minX, maxY, maxZ).color(r, g, b, a).endVertex();
        Tessellator.getInstance().draw();

        //case WEST:
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(minX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(minX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(minX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(minX, maxY, minZ).color(r, g, b, a).endVertex();
        Tessellator.getInstance().draw();

        //case EAST:
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(maxX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, minY, maxZ).color(r, g, b, a).endVertex();
        Tessellator.getInstance().draw();

        GlStateManager.depthMask(true);
        GlStateManager.enableDepthTest();
        GlStateManager.enableCull();
        GlStateManager.enableTexture();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}
