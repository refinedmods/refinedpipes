package com.refinedmods.refinedpipes.container.provider;

import com.refinedmods.refinedpipes.RefinedPipes;
import com.refinedmods.refinedpipes.container.ExtractorAttachmentContainer;
import com.refinedmods.refinedpipes.network.pipe.Pipe;
import com.refinedmods.refinedpipes.network.pipe.attachment.extractor.ExtractorAttachment;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class ExtractorAttachmentContainerProvider implements MenuProvider {
    private final Pipe pipe;
    private final ExtractorAttachment attachment;

    public ExtractorAttachmentContainerProvider(Pipe pipe, ExtractorAttachment attachment) {
        this.pipe = pipe;
        this.attachment = attachment;
    }

    public static void open(Pipe pipe, ExtractorAttachment attachment, ServerPlayer player) {
        ExtractorAttachmentContainerProvider provider = new ExtractorAttachmentContainerProvider(pipe, attachment);

        NetworkHooks.openGui(player, provider, buf -> {
            buf.writeBlockPos(pipe.getPos());
            buf.writeByte(attachment.getDirection().ordinal());
            buf.writeByte(attachment.getRedstoneMode().ordinal());
            buf.writeByte(attachment.getBlacklistWhitelist().ordinal());
            buf.writeByte(attachment.getRoutingMode().ordinal());
            buf.writeInt(attachment.getStackSize());
            buf.writeBoolean(attachment.isExactMode());
            buf.writeByte(attachment.getType().ordinal());
            buf.writeBoolean(attachment.isFluidMode());
        });
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("item." + RefinedPipes.ID + "." + attachment.getType().getId().getPath() + "_attachment");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
        return new ExtractorAttachmentContainer(
            windowId,
            player,
            pipe.getPos(),
            attachment.getDirection(),
            attachment.getRedstoneMode(),
            attachment.getBlacklistWhitelist(),
            attachment.getRoutingMode(),
            attachment.getStackSize(),
            attachment.isExactMode(),
            attachment.getType(),
            attachment.getItemFilter(),
            attachment.getFluidFilter(),
            attachment.isFluidMode()
        );
    }
}
