package com.raoulvdberge.refinedpipes.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.container.ExtractorAttachmentContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ExtractorAttachmentScreen extends ContainerScreen<ExtractorAttachmentContainer> {
    private static final ResourceLocation RESOURCE = new ResourceLocation(RefinedPipes.ID, "textures/gui/extractor_attachment.png");

    public ExtractorAttachmentScreen(ExtractorAttachmentContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);

        xSize = 176;
        ySize = 167;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        font.drawString(title.getFormattedText(), 7, 7, 4210752);
        font.drawString(I18n.format("container.inventory"), 7, 73, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        renderBackground();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(RESOURCE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(i, j, 0, 0, this.xSize, this.ySize);
    }
}
