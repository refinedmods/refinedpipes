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
    public void openAttachmentContainer(Direction dir, ServerPlayerEntity player) {
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
    public void writeUpdate(CompoundNBT tag) {
        throw new RuntimeException("Server-side only");
    }

    @Override
    public void readUpdate(CompoundNBT tag) {
        this.pickBlocks.clear();

        for (Direction dir : Direction.values()) {
            String attachmentKey = "attch_" + dir.ordinal();
            String pickBlockKey = "pb_" + dir.ordinal();

            if (tag.contains(attachmentKey) || tag.contains(pickBlockKey)) {
                pickBlocks.put(dir, ItemStack.read(tag.getCompound(pickBlockKey)));

                attachmentState[dir.ordinal()] = new ResourceLocation(tag.getString(attachmentKey));
            } else {
                attachmentState[dir.ordinal()] = null;
            }
        }
    }
}
