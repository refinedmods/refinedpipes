package com.refinedmods.refinedpipes.network.pipe.attachment;

import com.refinedmods.refinedpipes.network.pipe.Pipe;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ServerAttachmentManager implements AttachmentManager {
    private static final Logger LOGGER = LogManager.getLogger(ServerAttachmentManager.class);

    private final Map<Direction, Attachment> attachments = new HashMap<>();
    private final ResourceLocation[] attachmentState = new ResourceLocation[Direction.values().length];

    private final Pipe pipe;

    public ServerAttachmentManager(Pipe pipe) {
        this.pipe = pipe;
    }

    @Override
    public boolean hasAttachment(Direction dir) {
        return attachments.containsKey(dir);
    }

    @Override
    public void openAttachmentContainer(Direction dir, ServerPlayer player) {
        if (hasAttachment(dir)) {
            getAttachment(dir).openContainer(player);
        }
    }

    @Nonnull
    @Override
    public ItemStack getPickBlock(Direction dir) {
        throw new RuntimeException("Shouldn't be called on the server");
    }

    public void removeAttachmentAndScanGraph(Direction dir) {
        attachments.remove(dir);
        attachmentState[dir.ordinal()] = null;

        // Re-scan graph, required to rebuild destinations (chests with an attachment connected are no valid destination, refresh that)
        pipe.getNetwork().scanGraph(pipe.getWorld(), pipe.getPos());
    }

    public void setAttachmentAndScanGraph(Direction dir, Attachment attachment) {
        setAttachment(dir, attachment);

        // Re-scan graph, required to rebuild destinations (chests with an attachment connected are no valid destination, refresh that)
        pipe.getNetwork().scanGraph(pipe.getWorld(), pipe.getPos());
    }

    private void setAttachment(Direction dir, Attachment attachment) {
        attachments.put(dir, attachment);
        attachmentState[dir.ordinal()] = attachment.getId();
    }

    @Override
    @Nullable
    public Attachment getAttachment(Direction dir) {
        return attachments.get(dir);
    }

    public Collection<Attachment> getAttachments() {
        return attachments.values();
    }

    public CompoundTag writeToNbt(CompoundTag tag) {
        ListTag attch = new ListTag();
        getAttachments().forEach(a -> {
            CompoundTag attchTag = new CompoundTag();
            attchTag.putString("typ", a.getId().toString());
            attch.add(a.writeToNbt(attchTag));
        });
        tag.put("attch", attch);
        return tag;
    }

    public void readFromNbt(CompoundTag tag) {
        ListTag attch = tag.getList("attch", Tag.TAG_COMPOUND);
        for (Tag item : attch) {
            CompoundTag attchTag = (CompoundTag) item;

            AttachmentFactory factory = AttachmentRegistry.INSTANCE.getFactory(new ResourceLocation(attchTag.getString("typ")));
            if (factory != null) {
                Attachment attachment = factory.createFromNbt(pipe, attchTag);
                setAttachment(attachment.getDirection(), attachment);
            } else {
                LOGGER.warn("Attachment {} no longer exists", attchTag.getString("typ"));
            }
        }
    }

    @Override
    public ResourceLocation[] getState() {
        return attachmentState;
    }

    @Override
    public void writeUpdate(CompoundTag tag) {
        for (Direction dir : Direction.values()) {
            if (hasAttachment(dir)) {
                tag.putString("attch_" + dir.ordinal(), getAttachment(dir).getId().toString());
                tag.put("pb_" + dir.ordinal(), getAttachment(dir).getDrop().save(new CompoundTag()));
            }
        }
    }

    @Override
    public void readUpdate(@Nullable CompoundTag tag) {
        throw new RuntimeException("Client-side only");
    }
}
