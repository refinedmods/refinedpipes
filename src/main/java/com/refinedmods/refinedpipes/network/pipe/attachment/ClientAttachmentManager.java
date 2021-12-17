package com.refinedmods.refinedpipes.network.pipe.attachment;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ClientAttachmentManager implements AttachmentManager {
    private final ResourceLocation[] attachmentState = new ResourceLocation[Direction.values().length];
    private final Map<Direction, ItemStack> pickBlocks = new HashMap<>();

    @Override
    public ResourceLocation[] getState() {
        return attachmentState;
    }

    @Override
    public boolean hasAttachment(Direction dir) {
        return attachmentState[dir.ordinal()] != null;
    }

    @Override
    public void openAttachmentContainer(Direction dir, ServerPlayer player) {
        throw new RuntimeException("Server-side only");
    }

    @Nonnull
    @Override
    public ItemStack getPickBlock(Direction dir) {
        return pickBlocks.getOrDefault(dir, ItemStack.EMPTY);
    }

    @Override
    @Nullable
    public Attachment getAttachment(Direction dir) {
        throw new RuntimeException("Server-side only");
    }

    @Override
    public void writeUpdate(CompoundTag tag) {
        throw new RuntimeException("Server-side only");
    }

    @Override
    public void readUpdate(@Nullable CompoundTag tag) {
        this.pickBlocks.clear();

        for (Direction dir : Direction.values()) {
            String attachmentKey = "attch_" + dir.ordinal();
            String pickBlockKey = "pb_" + dir.ordinal();

            if (tag != null && (tag.contains(attachmentKey) || tag.contains(pickBlockKey))) {
                pickBlocks.put(dir, ItemStack.of(tag.getCompound(pickBlockKey)));

                attachmentState[dir.ordinal()] = new ResourceLocation(tag.getString(attachmentKey));
            } else {
                attachmentState[dir.ordinal()] = null;
            }
        }
    }
}
