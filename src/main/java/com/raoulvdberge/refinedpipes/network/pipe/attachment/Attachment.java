package com.raoulvdberge.refinedpipes.network.pipe.attachment;

import com.raoulvdberge.refinedpipes.network.Network;
import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public class Attachment {
    private final AttachmentType type;
    private final Direction direction;
    
    private int ticks;

    public Attachment(AttachmentType type, Direction direction) {
        this.type = type;
        this.direction = direction;
    }

    public AttachmentType getType() {
        return type;
    }

    public Direction getDirection() {
        return direction;
    }

    public CompoundNBT writeToNbt(CompoundNBT tag) {
        tag.putInt("dir", direction.ordinal());

        return tag;
    }

    public void update(World world, Network network, Pipe pipe) {
        ++ticks;

        type.update(world, network, pipe, this, ticks);
    }
}
