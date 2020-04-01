package com.raoulvdberge.refinedpipes.network.pipe.attachment;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ServerAttachmentManager implements AttachmentManager {
    private static final Logger LOGGER = LogManager.getLogger(ServerAttachmentManager.class);

    private final Map<Direction, Attachment> attachments = new HashMap<>();
    private final boolean[] attachmentState = new boolean[Direction.values().length];

    @Override
    public boolean hasAttachment(Direction dir) {
        return attachments.containsKey(dir);
    }

    @Nullable
    @Override
    public AttachmentType getAttachmentType(Direction dir) {
        return hasAttachment(dir) ? getAttachment(dir).getType() : null;
    }

    @Override
    public Map<Direction, AttachmentType> getAttachmentsPerDirection() {
        throw new RuntimeException("Shouldn't be called on the server");
    }

    public void removeAttachment(Direction dir) {
        attachments.remove(dir);
        attachmentState[dir.ordinal()] = false;
    }

    @Nullable
    public Attachment getAttachment(Direction dir) {
        return attachments.get(dir);
    }

    public Collection<Attachment> getAttachments() {
        return attachments.values();
    }

    public void setAttachment(Direction dir, AttachmentType type) {
        attachments.put(dir, type.createNew(dir));
        attachmentState[dir.ordinal()] = true;
    }

    public void setAttachment(Direction dir, Attachment attachment) {
        attachments.put(dir, attachment);
        attachmentState[dir.ordinal()] = true;
    }

    public CompoundNBT writeToNbt(CompoundNBT tag) {
        ListNBT attch = new ListNBT();
        getAttachments().forEach(a -> {
            CompoundNBT attchTag = new CompoundNBT();
            attchTag.putString("typ", a.getType().getId().toString());
            attch.add(a.writeToNbt(attchTag));
        });
        tag.put("attch", attch);
        return tag;
    }

    public void readFromNbt(CompoundNBT tag) {
        ListNBT attch = tag.getList("attch", Constants.NBT.TAG_COMPOUND);
        for (INBT item : attch) {
            CompoundNBT attchTag = (CompoundNBT) item;

            AttachmentType type = AttachmentRegistry.INSTANCE.getType(new ResourceLocation(attchTag.getString("typ")));
            if (type != null) {
                Attachment attachment = type.createFromNbt(attchTag);
                setAttachment(attachment.getDirection(), attachment);
            } else {
                LOGGER.warn("Attachment {} no longer exists", attchTag.getString("typ"));
            }
        }
    }

    @Override
    public boolean[] getState() {
        return attachmentState;
    }

    @Override
    public void writeUpdate(CompoundNBT tag) {
        for (Direction dir : Direction.values()) {
            if (hasAttachment(dir)) {
                tag.putString("attch_" + dir.ordinal(), getAttachment(dir).getType().getId().toString());
            }
        }
    }

    @Override
    public void readUpdate(CompoundNBT tag) {
        throw new RuntimeException("Server doesn't read updates");
    }
}
