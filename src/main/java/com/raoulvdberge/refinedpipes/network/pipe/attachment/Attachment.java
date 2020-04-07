package com.raoulvdberge.refinedpipes.network.pipe.attachment;

import com.raoulvdberge.refinedpipes.network.Network;
import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

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

    @Nullable
    public INamedContainerProvider getContainerProvider() {
        return null;
    }

    public abstract void update(World world, Network network, Pipe pipe);

    public abstract ResourceLocation getId();

    public abstract ItemStack getDrop();
}
