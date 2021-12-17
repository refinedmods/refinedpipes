package com.refinedmods.refinedpipes.network.pipe.attachment;

import com.refinedmods.refinedpipes.network.pipe.Pipe;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

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

    public CompoundTag writeToNbt(CompoundTag tag) {
        tag.putInt("dir", direction.ordinal());

        return tag;
    }

    public abstract void update();

    public abstract ResourceLocation getId();

    public abstract ItemStack getDrop();

    public void openContainer(ServerPlayer player) {
    }
}
