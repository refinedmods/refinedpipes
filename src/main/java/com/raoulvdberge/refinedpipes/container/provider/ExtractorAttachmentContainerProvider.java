package com.raoulvdberge.refinedpipes.container.provider;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.container.ExtractorAttachmentContainer;
import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.extractor.ExtractorAttachment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class ExtractorAttachmentContainerProvider implements INamedContainerProvider {
    private final Pipe pipe;
    private final ExtractorAttachment attachment;

    public ExtractorAttachmentContainerProvider(Pipe pipe, ExtractorAttachment attachment) {
        this.pipe = pipe;
        this.attachment = attachment;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("item." + RefinedPipes.ID + "." + attachment.getType().getId().getPath() + "_attachment");
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
        return new ExtractorAttachmentContainer(
            windowId,
            player,
            pipe.getPos(),
            attachment.getDirection(),
            attachment.getRedstoneMode(),
            attachment.getBlacklistWhitelist(),
            attachment.getType(),
            attachment.getItemFilter()
        );
    }

    public static void open(Pipe pipe, ExtractorAttachment attachment, ServerPlayerEntity player) {
        ExtractorAttachmentContainerProvider provider = new ExtractorAttachmentContainerProvider(pipe, attachment);

        NetworkHooks.openGui(player, provider, buf -> {
            buf.writeBlockPos(pipe.getPos());
            buf.writeByte(attachment.getDirection().ordinal());
            buf.writeByte(attachment.getRedstoneMode().ordinal());
            buf.writeByte(attachment.getBlacklistWhitelist().ordinal());
            buf.writeByte(attachment.getType().ordinal());
        });
    }
}
