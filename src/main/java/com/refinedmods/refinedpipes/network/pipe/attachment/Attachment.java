package com.refinedmods.refinedpipes.network.pipe.attachment;

import com.refinedmods.refinedpipes.network.pipe.Pipe;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

public abstract class Attachment {
    protected final Pipe pipe;
    private final Direction direction;

    public Attachment(Pipe pipe, Direction direction) {
        this.pipe = pipe;
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public Pipe getPipe() {
        return pipe;
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
