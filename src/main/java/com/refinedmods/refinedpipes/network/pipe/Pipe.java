package com.refinedmods.refinedpipes.network.pipe;

import com.refinedmods.refinedpipes.network.Network;
import com.refinedmods.refinedpipes.network.pipe.attachment.Attachment;
import com.refinedmods.refinedpipes.network.pipe.attachment.ServerAttachmentManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public abstract class Pipe {
    protected final Level world;
    protected final BlockPos pos;
    protected final ServerAttachmentManager attachmentManager = new ServerAttachmentManager(this);
    private final Logger logger = LogManager.getLogger(getClass());
    protected Network network;

    public Pipe(Level world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
    }

    public void update() {
        for (Attachment attachment : attachmentManager.getAttachments()) {
            attachment.update();
        }
    }

    public ServerAttachmentManager getAttachmentManager() {
        return attachmentManager;
    }

    public Level getWorld() {
        return world;
    }

    public BlockPos getPos() {
        return pos;
    }

    public Network getNetwork() {
        return network;
    }

    public void joinNetwork(Network network) {
        this.network = network;

        logger.debug(pos + " joined network " + network.getId());

        sendBlockUpdate();
    }

    public void leaveNetwork() {
        logger.debug(pos + " left network " + network.getId());

        this.network = null;

        sendBlockUpdate();
    }

    public void sendBlockUpdate() {
        BlockState state = world.getBlockState(pos);
        world.sendBlockUpdated(pos, state, state, 1 | 2);
    }

    public CompoundTag writeToNbt(CompoundTag tag) {
        tag.putLong("pos", pos.asLong());

        attachmentManager.writeToNbt(tag);

        return tag;
    }

    public abstract ResourceLocation getId();

    public abstract ResourceLocation getNetworkType();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pipe pipe = (Pipe) o;
        return world.equals(pipe.world) &&
            pos.equals(pipe.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, pos);
    }
}
