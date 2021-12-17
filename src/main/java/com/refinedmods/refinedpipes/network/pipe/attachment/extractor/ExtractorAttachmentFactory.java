package com.refinedmods.refinedpipes.network.pipe.attachment.extractor;

import com.refinedmods.refinedpipes.block.FluidPipeBlock;
import com.refinedmods.refinedpipes.block.ItemPipeBlock;
import com.refinedmods.refinedpipes.network.pipe.Pipe;
import com.refinedmods.refinedpipes.network.pipe.attachment.Attachment;
import com.refinedmods.refinedpipes.network.pipe.attachment.AttachmentFactory;
import com.refinedmods.refinedpipes.util.DirectionUtil;
import com.refinedmods.refinedpipes.util.StringUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class ExtractorAttachmentFactory implements AttachmentFactory {
    private final ExtractorAttachmentType type;

    public ExtractorAttachmentFactory(ExtractorAttachmentType type) {
        this.type = type;
    }

    @Override
    public Attachment createFromNbt(Pipe pipe, CompoundTag tag) {
        Direction dir = DirectionUtil.safeGet((byte) tag.getInt("dir"));

        ExtractorAttachment attachment = new ExtractorAttachment(pipe, dir, type);

        if (tag.contains("itemfilter")) {
            attachment.getItemFilter().deserializeNBT(tag.getCompound("itemfilter"));
        }

        if (tag.contains("rm")) {
            attachment.setRedstoneMode(RedstoneMode.get(tag.getByte("rm")));
        }

        if (tag.contains("bw")) {
            attachment.setBlacklistWhitelist(BlacklistWhitelist.get(tag.getByte("bw")));
        }

        if (tag.contains("rr")) {
            attachment.setRoundRobinIndex(tag.getInt("rr"));
        }

        if (tag.contains("routingm")) {
            attachment.setRoutingMode(RoutingMode.get(tag.getByte("routingm")));
        }

        if (tag.contains("stacksi")) {
            attachment.setStackSize(tag.getInt("stacksi"));
        }

        if (tag.contains("exa")) {
            attachment.setExactMode(tag.getBoolean("exa"));
        }

        if (tag.contains("fluidfilter")) {
            attachment.getFluidFilter().readFromNbt(tag.getCompound("fluidfilter"));
        }

        return attachment;
    }

    @Override
    public Attachment create(Pipe pipe, Direction dir) {
        return new ExtractorAttachment(pipe, dir, type);
    }

    @Override
    public ResourceLocation getItemId() {
        return type.getItemId();
    }

    @Override
    public ResourceLocation getId() {
        return type.getId();
    }

    @Override
    public ResourceLocation getModelLocation() {
        return type.getModelLocation();
    }

    @Override
    public void addInformation(List<Component> tooltip) {
        tooltip.add(new TranslatableComponent("misc.refinedpipes.tier", new TranslatableComponent("enchantment.level." + type.getTier())).withStyle(ChatFormatting.YELLOW));

        Component itemsToExtract = new TextComponent(StringUtil.formatNumber(type.getItemsToExtract()) + " ")
            .append(new TranslatableComponent("misc.refinedpipes.item" + (type.getItemsToExtract() == 1 ? "" : "s")))
            .withStyle(ChatFormatting.WHITE);

        float itemSecondsInterval = type.getItemTickInterval() / 20F;
        Component itemTickInterval = new TextComponent(StringUtil.formatNumber(itemSecondsInterval) + " ")
            .append(new TranslatableComponent("misc.refinedpipes.second" + (itemSecondsInterval == 1 ? "" : "s")))
            .withStyle(ChatFormatting.WHITE);

        tooltip.add(new TranslatableComponent(
            "tooltip.refinedpipes.extractor_attachment.item_extraction_rate",
            itemsToExtract,
            itemTickInterval
        ).withStyle(ChatFormatting.GRAY));

        Component fluidsToExtract = new TextComponent(StringUtil.formatNumber(type.getFluidsToExtract()) + " mB")
            .withStyle(ChatFormatting.WHITE);

        float fluidSecondsInterval = type.getFluidTickInterval() / 20F;
        Component fluidTickInterval = new TextComponent(StringUtil.formatNumber(fluidSecondsInterval) + " ")
            .append(new TranslatableComponent("misc.refinedpipes.second" + (fluidSecondsInterval == 1 ? "" : "s")))
            .withStyle(ChatFormatting.WHITE);

        tooltip.add(new TranslatableComponent(
            "tooltip.refinedpipes.extractor_attachment.fluid_extraction_rate",
            fluidsToExtract,
            fluidTickInterval
        ).withStyle(ChatFormatting.GRAY));

        tooltip.add(new TranslatableComponent(
            "tooltip.refinedpipes.extractor_attachment.filter_slots",
            new TextComponent("" + type.getFilterSlots()).withStyle(ChatFormatting.WHITE)
        ).withStyle(ChatFormatting.GRAY));

        addAbilityToInformation(tooltip, type.getCanSetRedstoneMode(), "misc.refinedpipes.redstone_mode");
        addAbilityToInformation(tooltip, type.getCanSetWhitelistBlacklist(), "misc.refinedpipes.mode");
        addAbilityToInformation(tooltip, type.getCanSetRoutingMode(), "misc.refinedpipes.routing_mode");
        addAbilityToInformation(tooltip, type.getCanSetExactMode(), "misc.refinedpipes.exact_mode");
    }

    private void addAbilityToInformation(List<Component> tooltip, boolean possible, String key) {
        tooltip.add(
            new TextComponent(possible ? "✓ " : "❌ ").append(new TranslatableComponent(key))
                .withStyle(possible ? ChatFormatting.GREEN : ChatFormatting.RED)
        );
    }

    @Override
    public boolean canPlaceOnPipe(Block pipe) {
        return pipe instanceof ItemPipeBlock
            || pipe instanceof FluidPipeBlock;
    }
}
