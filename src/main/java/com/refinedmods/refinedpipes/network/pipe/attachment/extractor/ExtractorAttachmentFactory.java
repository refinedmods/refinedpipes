package com.refinedmods.refinedpipes.network.pipe.attachment.extractor;

import com.refinedmods.refinedpipes.block.FluidPipeBlock;
import com.refinedmods.refinedpipes.block.ItemPipeBlock;
import com.refinedmods.refinedpipes.network.pipe.Pipe;
import com.refinedmods.refinedpipes.network.pipe.attachment.Attachment;
import com.refinedmods.refinedpipes.network.pipe.attachment.AttachmentFactory;
import com.refinedmods.refinedpipes.util.DirectionUtil;
import com.refinedmods.refinedpipes.util.StringUtil;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;

import java.util.List;

public class ExtractorAttachmentFactory implements AttachmentFactory {
    private final ExtractorAttachmentType type;

    public ExtractorAttachmentFactory(ExtractorAttachmentType type) {
        this.type = type;
    }

    @Override
    public Attachment createFromNbt(Pipe pipe, CompoundNBT tag) {
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
    public void addInformation(List<ITextComponent> tooltip) {
        tooltip.add(new TranslationTextComponent("misc.refinedpipes.tier", new TranslationTextComponent("enchantment.level." + type.getTier())).setStyle(new Style().setColor(TextFormatting.YELLOW)));

        ITextComponent itemsToExtract = new StringTextComponent(StringUtil.formatNumber(type.getItemsToExtract()) + " ")
            .appendSibling(new TranslationTextComponent("misc.refinedpipes.item" + (type.getItemsToExtract() == 1 ? "" : "s")))
            .setStyle(new Style().setColor(TextFormatting.WHITE));

        float itemSecondsInterval = type.getItemTickInterval() / 20F;
        ITextComponent itemTickInterval = new StringTextComponent(StringUtil.formatNumber(itemSecondsInterval) + " ")
            .appendSibling(new TranslationTextComponent("misc.refinedpipes.second" + (itemSecondsInterval == 1 ? "" : "s")))
            .setStyle(new Style().setColor(TextFormatting.WHITE));

        tooltip.add(new TranslationTextComponent(
            "tooltip.refinedpipes.extractor_attachment.item_extraction_rate",
            itemsToExtract,
            itemTickInterval
        ).setStyle(new Style().setColor(TextFormatting.GRAY)));

        ITextComponent fluidsToExtract = new StringTextComponent(StringUtil.formatNumber(type.getFluidsToExtract()) + " mB")
            .setStyle(new Style().setColor(TextFormatting.WHITE));

        float fluidSecondsInterval = type.getFluidTickInterval() / 20F;
        ITextComponent fluidTickInterval = new StringTextComponent(StringUtil.formatNumber(fluidSecondsInterval) + " ")
            .appendSibling(new TranslationTextComponent("misc.refinedpipes.second" + (fluidSecondsInterval == 1 ? "" : "s")))
            .setStyle(new Style().setColor(TextFormatting.WHITE));

        tooltip.add(new TranslationTextComponent(
            "tooltip.refinedpipes.extractor_attachment.fluid_extraction_rate",
            fluidsToExtract,
            fluidTickInterval
        ).setStyle(new Style().setColor(TextFormatting.GRAY)));

        tooltip.add(new TranslationTextComponent(
            "tooltip.refinedpipes.extractor_attachment.filter_slots",
            new StringTextComponent("" + type.getFilterSlots()).setStyle(new Style().setColor(TextFormatting.WHITE))
        ).setStyle(new Style().setColor(TextFormatting.GRAY)));

        addAbilityToInformation(tooltip, type.getCanSetRedstoneMode(), "misc.refinedpipes.redstone_mode");
        addAbilityToInformation(tooltip, type.getCanSetWhitelistBlacklist(), "misc.refinedpipes.mode");
        addAbilityToInformation(tooltip, type.getCanSetRoutingMode(), "misc.refinedpipes.routing_mode");
        addAbilityToInformation(tooltip, type.getCanSetExactMode(), "misc.refinedpipes.exact_mode");
    }

    private void addAbilityToInformation(List<ITextComponent> tooltip, boolean possible, String key) {
        tooltip.add(
            new StringTextComponent(possible ? "✓ " : "❌ ").appendSibling(new TranslationTextComponent(key))
                .setStyle(new Style().setColor(possible ? TextFormatting.GREEN : TextFormatting.RED))
        );
    }

    @Override
    public boolean canPlaceOnPipe(Block pipe) {
        return pipe instanceof ItemPipeBlock
            || pipe instanceof FluidPipeBlock;
    }
}
