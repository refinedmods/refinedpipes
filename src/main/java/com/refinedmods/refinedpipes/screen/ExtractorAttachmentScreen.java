package com.refinedmods.refinedpipes.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.refinedmods.refinedpipes.RefinedPipes;
import com.refinedmods.refinedpipes.container.ExtractorAttachmentContainer;
import com.refinedmods.refinedpipes.network.pipe.attachment.extractor.BlacklistWhitelist;
import com.refinedmods.refinedpipes.network.pipe.attachment.extractor.ExtractorAttachment;
import com.refinedmods.refinedpipes.network.pipe.attachment.extractor.RedstoneMode;
import com.refinedmods.refinedpipes.network.pipe.attachment.extractor.RoutingMode;
import com.refinedmods.refinedpipes.screen.widget.IconButton;
import com.refinedmods.refinedpipes.screen.widget.IconButtonPreset;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.client.gui.GuiUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ExtractorAttachmentScreen extends BaseScreen<ExtractorAttachmentContainer> {
    private static final ResourceLocation RESOURCE = new ResourceLocation(RefinedPipes.ID, "textures/gui/extractor_attachment.png");

    private final List<ITextComponent> tooltip = new ArrayList<>();

    private Button redstoneModeButton;
    private Button blacklistWhitelistButton;
    @Nullable
    private Button routingModeButton;
    @Nullable
    private Button exactModeButton;
    @Nullable
    private Button plusButton;
    @Nullable
    private Button minusButton;

    public ExtractorAttachmentScreen(ExtractorAttachmentContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);

        this.xSize = 176;
        this.ySize = 193;
    }

    @Override
    protected void init() {
        super.init();

        redstoneModeButton = addButton(new IconButton(
            this.guiLeft + 32,
            this.guiTop + 76,
            IconButtonPreset.NORMAL,
            getRedstoneModeX(container.getRedstoneMode()),
            61,
            getRedstoneModeText(container.getRedstoneMode()),
            btn -> setRedstoneMode((IconButton) btn, container.getRedstoneMode().next())
        ));

        redstoneModeButton.active = container.getExtractorAttachmentType().getCanSetRedstoneMode();

        blacklistWhitelistButton = addButton(new IconButton(
            this.guiLeft + 55,
            this.guiTop + 76,
            IconButtonPreset.NORMAL,
            getBlacklistWhitelistX(container.getBlacklistWhitelist()),
            82,
            getBlacklistWhitelistText(container.getBlacklistWhitelist()),
            btn -> setBlacklistWhitelist((IconButton) btn, container.getBlacklistWhitelist().next())
        ));

        blacklistWhitelistButton.active = container.getExtractorAttachmentType().getCanSetWhitelistBlacklist();

        exactModeButton = addButton(new IconButton(
            this.guiLeft + 78,
            this.guiTop + 76,
            IconButtonPreset.NORMAL,
            getExactModeX(container.isExactMode()),
            103,
            getExactModeText(container.isExactMode()),
            btn -> setExactMode((IconButton) btn, !container.isExactMode())
        ));

        exactModeButton.active = container.getExtractorAttachmentType().getCanSetExactMode();

        if (!container.isFluidMode()) {
            routingModeButton = addButton(new IconButton(
                this.guiLeft + 101,
                this.guiTop + 76,
                IconButtonPreset.NORMAL,
                getRoutingModeX(container.getRoutingMode()),
                194,
                getRoutingModeText(container.getRoutingMode()),
                btn -> setRoutingMode((IconButton) btn, container.getRoutingMode().next())
            ));

            routingModeButton.active = container.getExtractorAttachmentType().getCanSetWhitelistBlacklist();

            plusButton = addButton(new IconButton(
                this.guiLeft + 125,
                this.guiTop + 76 - 3,
                IconButtonPreset.SMALL,
                198,
                19,
                new StringTextComponent("+"),
                btn -> updateStackSize(1)
            ));

            minusButton = addButton(new IconButton(
                this.guiLeft + 125,
                this.guiTop + 76 + 14 - 3,
                IconButtonPreset.SMALL,
                198,
                34,
                new StringTextComponent("-"),
                btn -> updateStackSize(-1)
            ));

            minusButton.active = container.getStackSize() > 0;
            plusButton.active = container.getStackSize() < container.getExtractorAttachmentType().getItemsToExtract();
        }
    }

    private void updateStackSize(int amount) {
        if (hasShiftDown()) {
            amount *= 4;
        }

        int newAmount = container.getStackSize() + amount;
        if (newAmount < 0) {
            newAmount = 0;
        }

        if (newAmount > container.getExtractorAttachmentType().getItemsToExtract()) {
            newAmount = container.getExtractorAttachmentType().getItemsToExtract();
        }

        minusButton.active = newAmount > 0;
        plusButton.active = newAmount < container.getExtractorAttachmentType().getItemsToExtract();

        container.setStackSize(newAmount);
    }

    private int getRedstoneModeX(RedstoneMode redstoneMode) {
        switch (redstoneMode) {
            case IGNORED:
                return 219;
            case HIGH:
                return 177;
            case LOW:
                return 198;
            default:
                return 0;
        }
    }

    private IFormattableTextComponent getRedstoneModeText(RedstoneMode redstoneMode) {
        return new TranslationTextComponent("misc.refinedpipes.redstone_mode." + redstoneMode.toString().toLowerCase());
    }

    private void setRedstoneMode(IconButton button, RedstoneMode redstoneMode) {
        button.setMessage(getRedstoneModeText(redstoneMode));
        button.setOverlayTexX(getRedstoneModeX(redstoneMode));

        container.setRedstoneMode(redstoneMode);
    }

    private int getBlacklistWhitelistX(BlacklistWhitelist blacklistWhitelist) {
        switch (blacklistWhitelist) {
            case BLACKLIST:
                return 198;
            case WHITELIST:
                return 177;
            default:
                return 0;
        }
    }

    private IFormattableTextComponent getBlacklistWhitelistText(BlacklistWhitelist blacklistWhitelist) {
        return new TranslationTextComponent("misc.refinedpipes.mode." + blacklistWhitelist.toString().toLowerCase());
    }

    private void setBlacklistWhitelist(IconButton button, BlacklistWhitelist blacklistWhitelist) {
        button.setMessage(getBlacklistWhitelistText(blacklistWhitelist));
        button.setOverlayTexX(getBlacklistWhitelistX(blacklistWhitelist));

        container.setBlacklistWhitelist(blacklistWhitelist);
    }

    private int getRoutingModeX(RoutingMode routingMode) {
        switch (routingMode) {
            case NEAREST:
                return 0;
            case FURTHEST:
                return 21;
            case RANDOM:
                return 42;
            case ROUND_ROBIN:
                return 63;
            default:
                return 0;
        }
    }

    private IFormattableTextComponent getRoutingModeText(RoutingMode routingMode) {
        return new TranslationTextComponent("misc.refinedpipes.routing_mode." + routingMode.toString().toLowerCase());
    }

    private void setRoutingMode(IconButton button, RoutingMode routingMode) {
        button.setMessage(getRoutingModeText(routingMode));
        button.setOverlayTexX(getRoutingModeX(routingMode));

        container.setRoutingMode(routingMode);
    }

    private int getExactModeX(boolean exactMode) {
        return exactMode ? 177 : 198;
    }

    private IFormattableTextComponent getExactModeText(boolean exactMode) {
        return new TranslationTextComponent("misc.refinedpipes.exact_mode." + (exactMode ? "on" : "off"));
    }

    private void setExactMode(IconButton button, boolean exactMode) {
        button.setMessage(getExactModeText(exactMode));
        button.setOverlayTexX(getExactModeX(exactMode));

        container.setExactMode(exactMode);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        font.drawString(matrixStack, title.getString(), 7, 7, 4210752);
        font.drawString(matrixStack, I18n.format("container.inventory"), 7, 103 - 4, 4210752);

        if (!container.isFluidMode()) {
            font.drawString(matrixStack, "" + container.getStackSize(), 143, 83, 4210752);
        }

        func_230459_a_(matrixStack, mouseX - guiLeft, mouseY - guiTop);

        tooltip.clear();

        if (blacklistWhitelistButton.isHovered()) {
            tooltip.add(new TranslationTextComponent("misc.refinedpipes.mode"));
            tooltip.add(getBlacklistWhitelistText(container.getBlacklistWhitelist()).mergeStyle(TextFormatting.GRAY));
        } else if (redstoneModeButton.isHovered()) {
            tooltip.add(new TranslationTextComponent("misc.refinedpipes.redstone_mode"));
            tooltip.add(getRedstoneModeText(container.getRedstoneMode()).mergeStyle(TextFormatting.GRAY));
        } else if (routingModeButton != null && routingModeButton.isHovered()) {
            tooltip.add(new TranslationTextComponent("misc.refinedpipes.routing_mode"));
            tooltip.add(getRoutingModeText(container.getRoutingMode()).mergeStyle(TextFormatting.GRAY));
        } else if (exactModeButton.isHovered()) {
            tooltip.add(new TranslationTextComponent("misc.refinedpipes.exact_mode"));
            tooltip.add(getExactModeText(container.isExactMode()).mergeStyle(TextFormatting.GRAY));
        }

        if (!tooltip.isEmpty()) {
            GuiUtils.drawHoveringText(matrixStack, tooltip, mouseX - guiLeft, mouseY - guiTop, width, height, -1, Minecraft.getInstance().fontRenderer);
        }

        super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        renderBackground(matrixStack);

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(RESOURCE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);

        int x = 43;
        int y = 18;
        for (int filterSlotId = 1; filterSlotId <= ExtractorAttachment.MAX_FILTER_SLOTS; ++filterSlotId) {
            if (filterSlotId > container.getExtractorAttachmentType().getFilterSlots()) {
                this.blit(matrixStack, i + x, j + y, 198, 0, 18, 18);
            }

            if (filterSlotId % 5 == 0) {
                x = 43;
                y += 18;
            } else {
                x += 18;
            }
        }

        super.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
    }
}
