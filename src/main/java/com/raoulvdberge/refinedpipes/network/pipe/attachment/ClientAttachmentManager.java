package com.raoulvdberge.refinedpipes.network.pipe.attachment;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ClientAttachmentManager implements AttachmentManager {
    private final Map<Direction, ResourceLocation> attachments = new HashMap<>();
    private final Map<Direction, ItemStack> pickBlocks = new HashMap<>();
    private final boolean[] attachmentState = new boolean[Direction.values().length];

    @Override
    public boolean[] getState() {
        return attachmentState;
    }

    @Override
    public boolean hasAttachment(Direction dir) {
        return attachments.containsKey(dir);
    }

    @Override
    public void openAttachmentContainer(Direction dir, ServerPlayerEntity player) {
        throw new RuntimeException("Server-side only");
    }

    @Nonnull
    @Override
    public ItemStack getPickBlock(Direction dir) {
        return pickBlocks.getOrDefault(dir, ItemStack.EMPTY);
    }

    @Override
    public Map<Direction, ResourceLocation> getAttachmentsPerDirection() {
        return attachments;
    }

    @Override
    @Nullable
    public Attachment getAttachment(Direction dir) {
        throw new RuntimeException("Server-side only");
    }

    @Override
    public void writeUpdate(CompoundNBT tag) {
        throw new RuntimeException("Server-side only");
    }

    @Override
    public void readUpdate(CompoundNBT tag) {
        this.attachments.clear();
        this.pickBlocks.clear();

        for (Direction dir : Direction.values()) {
            String attachmentKey = "attch_" + dir.ordinal();
            String pickBlockKey = "pb_" + dir.ordinal();

            if (tag.contains(attachmentKey) || tag.contains(pickBlockKey)) {
                attachments.put(dir, new ResourceLocation(tag.getString(attachmentKey)));
                pickBlocks.put(dir, ItemStack.read(tag.getCompound(pickBlockKey)));

                attachmentState[dir.ordinal()] = true;
            } else {
                attachmentState[dir.ordinal()] = false;
            }
        }
    }
}
