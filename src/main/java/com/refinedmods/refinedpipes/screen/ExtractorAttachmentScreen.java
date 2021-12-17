package com.refinedmods.refinedpipes.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedpipes.RefinedPipes;
import com.refinedmods.refinedpipes.container.ExtractorAttachmentContainerMenu;
import com.refinedmods.refinedpipes.network.pipe.attachment.extractor.BlacklistWhitelist;
import com.refinedmods.refinedpipes.network.pipe.attachment.extractor.ExtractorAttachment;
import com.refinedmods.refinedpipes.network.pipe.attachment.extractor.RedstoneMode;
import com.refinedmods.refinedpipes.network.pipe.attachment.extractor.RoutingMode;
import com.refinedmods.refinedpipes.screen.widget.IconButton;
import com.refinedmods.refinedpipes.screen.widget.IconButtonPreset;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ExtractorAttachmentScreen extends BaseScreen<ExtractorAttachmentContainerMenu> {
    private static final ResourceLocation RESOURCE = new ResourceLocation(RefinedPipes.ID, "textures/gui/extractor_attachment.png");

    private final List<Component> tooltip = new ArrayList<>();

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

    public ExtractorAttachmentScreen(ExtractorAttachmentContainerMenu container, Inventory inv, Component title) {
        super(container, inv, title);

        this.imageWidth = 176;
        this.imageHeight = 193;
    }

    @Override
    protected void init() {
        super.init();

        redstoneModeButton = this.addRenderableWidget(new IconButton(
            this.leftPos + 32,
            this.topPos + 76,
            IconButtonPreset.NORMAL,
            getRedstoneModeX(menu.getRedstoneMode()),
            61,
            getRedstoneModeText(menu.getRedstoneMode()),
            btn -> setRedstoneMode((IconButton) btn, menu.getRedstoneMode().next())
        ));

        redstoneModeButton.active = menu.getExtractorAttachmentType().getCanSetRedstoneMode();

        blacklistWhitelistButton = this.addRenderableWidget(new IconButton(
            this.leftPos + 55,
            this.topPos + 76,
            IconButtonPreset.NORMAL,
            getBlacklistWhitelistX(menu.getBlacklistWhitelist()),
            82,
            getBlacklistWhitelistText(menu.getBlacklistWhitelist()),
            btn -> setBlacklistWhitelist((IconButton) btn, menu.getBlacklistWhitelist().next())
        ));

        blacklistWhitelistButton.active = menu.getExtractorAttachmentType().getCanSetWhitelistBlacklist();

        exactModeButton = this.addRenderableWidget(new IconButton(
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
            routingModeButton = this.addRenderableWidget(new IconButton(
                this.leftPos + 101,
                this.topPos + 76,
                IconButtonPreset.NORMAL,
                getRoutingModeX(menu.getRoutingMode()),
                194,
                getRoutingModeText(menu.getRoutingMode()),
                btn -> setRoutingMode((IconButton) btn, menu.getRoutingMode().next())
            ));

            routingModeButton.active = menu.getExtractorAttachmentType().getCanSetWhitelistBlacklist();

            plusButton = this.addRenderableWidget(new IconButton(
                this.leftPos + 125,
                this.topPos + 76 - 3,
                IconButtonPreset.SMALL,
                198,
                19,
                new TextComponent("+"),
                btn -> updateStackSize(1)
            ));

            minusButton = this.addRenderableWidget(new IconButton(
                this.leftPos + 125,
                this.topPos + 76 + 14 - 3,
                IconButtonPreset.SMALL,
                198,
                34,
                new TextComponent("-"),
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

    private MutableComponent getRedstoneModeText(RedstoneMode redstoneMode) {
        return new TranslatableComponent("misc.refinedpipes.redstone_mode." + redstoneMode.toString().toLowerCase());
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

    private MutableComponent getBlacklistWhitelistText(BlacklistWhitelist blacklistWhitelist) {
        return new TranslatableComponent("misc.refinedpipes.mode." + blacklistWhitelist.toString().toLowerCase());
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

    private MutableComponent getRoutingModeText(RoutingMode routingMode) {
        return new TranslatableComponent("misc.refinedpipes.routing_mode." + routingMode.toString().toLowerCase());
    }

    private void setRoutingMode(IconButton button, RoutingMode routingMode) {
        button.setMessage(getRoutingModeText(routingMode));
        button.setOverlayTexX(getRoutingModeX(routingMode));

        menu.setRoutingMode(routingMode);
    }

    private int getExactModeX(boolean exactMode) {
        return exactMode ? 177 : 198;
    }

    private MutableComponent getExactModeText(boolean exactMode) {
        return new TranslatableComponent("misc.refinedpipes.exact_mode." + (exactMode ? "on" : "off"));
    }

    private void setExactMode(IconButton button, boolean exactMode) {
        button.setMessage(getExactModeText(exactMode));
        button.setOverlayTexX(getExactModeX(exactMode));

        menu.setExactMode(exactMode);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        font.draw(poseStack, title.getString(), 7, 7, 4210752);
        font.draw(poseStack, I18n.get("container.inventory"), 7, 103 - 4, 4210752);

        if (!menu.isFluidMode()) {
            font.draw(poseStack, "" + menu.getStackSize(), 143, 83, 4210752);
        }

        renderTooltip(poseStack, mouseX - leftPos, mouseY - topPos);

        tooltip.clear();

        if (blacklistWhitelistButton.isHoveredOrFocused()) {
            tooltip.add(new TranslatableComponent("misc.refinedpipes.mode"));
            tooltip.add(getBlacklistWhitelistText(menu.getBlacklistWhitelist()).withStyle(ChatFormatting.GRAY));
        } else if (redstoneModeButton.isHoveredOrFocused()) {
            tooltip.add(new TranslatableComponent("misc.refinedpipes.redstone_mode"));
            tooltip.add(getRedstoneModeText(menu.getRedstoneMode()).withStyle(ChatFormatting.GRAY));
        } else if (routingModeButton != null && routingModeButton.isHoveredOrFocused()) {
            tooltip.add(new TranslatableComponent("misc.refinedpipes.routing_mode"));
            tooltip.add(getRoutingModeText(menu.getRoutingMode()).withStyle(ChatFormatting.GRAY));
        } else if (exactModeButton.isHoveredOrFocused()) {
            tooltip.add(new TranslatableComponent("misc.refinedpipes.exact_mode"));
            tooltip.add(getExactModeText(menu.isExactMode()).withStyle(ChatFormatting.GRAY));
        }

        if (!tooltip.isEmpty()) {
            renderComponentTooltip(poseStack, tooltip, mouseX - leftPos, mouseY - topPos);
        }

        super.renderLabels(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        renderBackground(poseStack);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, RESOURCE);

        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(poseStack, i, j, 0, 0, this.imageWidth, this.imageHeight);

        int x = 43;
        int y = 18;
        for (int filterSlotId = 1; filterSlotId <= ExtractorAttachment.MAX_FILTER_SLOTS; ++filterSlotId) {
            if (filterSlotId > menu.getExtractorAttachmentType().getFilterSlots()) {
                this.blit(poseStack, i + x, j + y, 198, 0, 18, 18);
            }

            if (filterSlotId % 5 == 0) {
                x = 43;
                y += 18;
            } else {
                x += 18;
            }
        }

        super.renderBg(poseStack, partialTicks, mouseX, mouseY);
    }
}
