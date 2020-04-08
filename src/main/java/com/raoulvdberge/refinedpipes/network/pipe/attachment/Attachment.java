package com.raoulvdberge.refinedpipes.network.pipe.attachment;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

public abstract class Attachment {
    private final Direction direction;

    public Attachment(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public CompoundNBT writeToNbt(CompoundNBT tag) {
        tag.putInt("dir", direction.ordinal());

        return tag;
    }

    public abstract void update();

    public abstract ResourceLocation getId();

    public abstract ItemStack getDrop();

    public void openContainer(ServerPlayerEntity player) {
    }
}
