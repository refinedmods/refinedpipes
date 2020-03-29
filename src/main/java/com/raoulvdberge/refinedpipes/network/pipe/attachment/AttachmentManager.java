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

public class AttachmentManager {
    private static final Logger LOGGER = LogManager.getLogger(AttachmentManager.class);

    private final Map<Direction, Attachment> attachments = new HashMap<>();

    public boolean hasAttachment(Direction dir) {
        return attachments.containsKey(dir);
    }

    public boolean hasAttachment(AttachmentType type) {
        return attachments.values().stream().anyMatch(a -> a.getType() == type);
    }

    public void removeAttachment(Direction dir) {
        attachments.remove(dir);
    }

    @Nullable
    public Attachment getAttachment(Direction dir) {
        return attachments.get(dir);
    }

    @Nullable
    public Attachment getAttachment(AttachmentType type) {
        return attachments.values().stream().filter(a -> a.getType() == type).findFirst().orElse(null);
    }

    public Collection<Attachment> getAttachments() {
        return attachments.values();
    }

    public void setAttachment(Direction dir, AttachmentType type) {
        attachments.put(dir, type.createNew(dir));
    }

    public void setAttachment(Direction dir, Attachment attachment) {
        attachments.put(dir, attachment);
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
}
