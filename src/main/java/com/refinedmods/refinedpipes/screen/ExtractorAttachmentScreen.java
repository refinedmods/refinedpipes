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

        this.imageWidth = 176;
        this.imageHeight = 193;
    }

    @Override
    protected void init() {
        super.init();

        redstoneModeButton = addButton(new IconButton(
            this.leftPos + 32,
            this.topPos + 76,
            IconButtonPreset.NORMAL,
            getRedstoneModeX(menu.getRedstoneMode()),
            61,
            getRedstoneModeText(menu.getRedstoneMode()),
            btn -> setRedstoneMode((IconButton) btn, menu.getRedstoneMode().next())
        ));

        redstoneModeButton.active = menu.getExtractorAttachmentType().getCanSetRedstoneMode();

        blacklistWhitelistButton = addButton(new IconButton(
            this.leftPos + 55,
            this.topPos + 76,
            IconButtonPreset.NORMAL,
            getBlacklistWhitelistX(menu.getBlacklistWhitelist()),
            82,
            getBlacklistWhitelistText(menu.getBlacklistWhitelist()),
            btn -> setBlacklistWhitelist((IconButton) btn, menu.getBlacklistWhitelist().next())
        ));

        blacklistWhitelistButton.active = menu.getExtractorAttachmentType().getCanSetWhitelistBlacklist();

        exactModeButton = addButton(new IconButton(
            this.leftPos + 78,
            this.topPos + 76,
            IconButtonPreset.NORMAL,
            getExactModeX(menu.isExactMode()),
            103,
            getExactModeText(menu.isExactMode()),
            btn -> setExactMode((IconButton) btn, !menu.isExactMode())
        ));

        exactModeButton.active = menu.getExtractorAttachmentType().getCanSetExactMode();

        if (!menu.isFluidMode()) {
            routingModeButton = addButton(new IconButton(
                this.leftPos + 101,
                this.topPos + 76,
                IconButtonPreset.NORMAL,
                getRoutingModeX(menu.getRoutingMode()),
                194,
                getRoutingModeText(menu.getRoutingMode()),
                btn -> setRoutingMode((IconButton) btn, menu.getRoutingMode().next())
            ));

            routingModeButton.active = menu.getExtractorAttachmentType().getCanSetWhitelistBlacklist();

            plusButton = addButton(new IconButton(
                this.leftPos + 125,
                this.topPos + 76 - 3,
                IconButtonPreset.SMALL,
                198,
                19,
                new StringTextComponent("+"),
                btn -> updateStackSize(1)
            ));

            minusButton = addButton(new IconButton(
                this.leftPos + 125,
                this.topPos + 76 + 14 - 3,
                IconButtonPreset.SMALL,
                198,
                34,
                new StringTextComponent("-"),
                btn -> updateStackSize(-1)
            ));

            minusButton.active = menu.getStackSize() > 0;
            plusButton.active = menu.getStackSize() < menu.getExtractorAttachmentType().getItemsToExtract();
        }
    }

    private void updateStackSize(int amount) {
        if (hasShiftDown()) {
            amount *= 4;
        }

        int newAmount = menu.getStackSize() + amount;
        if (newAmount < 0) {
            newAmount = 0;
        }

        if (newAmount > menu.getExtractorAttachmentType().getItemsToExtract()) {
            newAmount = menu.getExtractorAttachmentType().getItemsToExtract();
        }

        minusButton.active = newAmount > 0;
        plusButton.active = newAmount < menu.getExtractorAttachmentType().getItemsToExtract();

        menu.setStackSize(newAmount);
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

        menu.setRedstoneMode(redstoneMode);
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

        menu.setBlacklistWhitelist(blacklistWhitelist);
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

        menu.setRoutingMode(routingMode);
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

        menu.setExactMode(exactMode);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        font.draw(matrixStack, title.getString(), 7, 7, 4210752);
        font.draw(matrixStack, I18n.get("container.inventory"), 7, 103 - 4, 4210752);

        if (!menu.isFluidMode()) {
            font.draw(matrixStack, "" + menu.getStackSize(), 143, 83, 4210752);
        }

        renderTooltip(matrixStack, mouseX - leftPos, mouseY - topPos);

        tooltip.clear();

        if (blacklistWhitelistButton.isHovered()) {
            tooltip.add(new TranslationTextComponent("misc.refinedpipes.mode"));
            tooltip.add(getBlacklistWhitelistText(menu.getBlacklistWhitelist()).withStyle(TextFormatting.GRAY));
        } else if (redstoneModeButton.isHovered()) {
            tooltip.add(new TranslationTextComponent("misc.refinedpipes.redstone_mode"));
            tooltip.add(getRedstoneModeText(menu.getRedstoneMode()).withStyle(TextFormatting.GRAY));
        } else if (routingModeButton != null && routingModeButton.isHovered()) {
            tooltip.add(new TranslationTextComponent("misc.refinedpipes.routing_mode"));
            tooltip.add(getRoutingModeText(menu.getRoutingMode()).withStyle(TextFormatting.GRAY));
        } else if (exactModeButton.isHovered()) {
            tooltip.add(new TranslationTextComponent("misc.refinedpipes.exact_mode"));
            tooltip.add(getExactModeText(menu.isExactMode()).withStyle(TextFormatting.GRAY));
        }

        if (!tooltip.isEmpty()) {
            GuiUtils.drawHoveringText(matrixStack, tooltip, mouseX - leftPos, mouseY - topPos, width, height, -1, Minecraft.getInstance().font);
        }

        super.renderLabels(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        renderBackground(matrixStack);

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(RESOURCE);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);

        int x = 43;
        int y = 18;
        for (int filterSlotId = 1; filterSlotId <= ExtractorAttachment.MAX_FILTER_SLOTS; ++filterSlotId) {
            if (filterSlotId > menu.getExtractorAttachmentType().getFilterSlots()) {
                this.blit(matrixStack, i + x, j + y, 198, 0, 18, 18);
            }

            if (filterSlotId % 5 == 0) {
                x = 43;
                y += 18;
            } else {
                x += 18;
            }
        }

        super.renderBg(matrixStack, partialTicks, mouseX, mouseY);
    }
}
