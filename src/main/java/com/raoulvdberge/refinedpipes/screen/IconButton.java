package com.raoulvdberge.refinedpipes.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.raoulvdberge.refinedpipes.RefinedPipes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.ArrayList;
import java.util.List;

public class IconButton extends Button {
    private static final ResourceLocation RESOURCE = new ResourceLocation(RefinedPipes.ID, "textures/gui/extractor_attachment.png");

    private static final int SIZE = 20;

    private static final int TEX_Y_NORMAL = 0;
    private static final int TEX_Y_HOVER = 20;
    private static final int TEX_Y_DISABLED = 40;
    private static final int TEX_X = 177;

    private int iconTexX;
    private int iconTexY;

    private List<String> tooltip = new ArrayList<>(1);

    public IconButton(int x, int y, int iconTexX, int iconTexY, String text, IPressable onPress) {
        super(x, y, SIZE, SIZE, text, onPress);

        this.iconTexX = iconTexX;
        this.iconTexY = iconTexY;

        tooltip.add(text);
    }

    @Override
    public void setMessage(String message) {
        super.setMessage(message);
        tooltip.set(0, message);
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(RESOURCE);

        RenderSystem.disableDepthTest();

        int y = TEX_Y_NORMAL;
        if (!active) {
            y = TEX_Y_DISABLED;
        } else if (isHovered) {
            y = TEX_Y_HOVER;
        }

        blit(this.x, this.y, TEX_X, y, this.width, this.height, 256, 256);

        // Fiddling with -1 to remove the blue border
        blit(this.x + 1, this.y + 1, iconTexX + 1, iconTexY + 1, this.width - 2, this.height - 2, 256, 256);

        RenderSystem.enableDepthTest();

        if (mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height) {
            GuiUtils.drawHoveringText(tooltip, mouseX, mouseY, Minecraft.getInstance().currentScreen.width, Minecraft.getInstance().currentScreen.height, -1, Minecraft.getInstance().fontRenderer);
        }
    }
}
