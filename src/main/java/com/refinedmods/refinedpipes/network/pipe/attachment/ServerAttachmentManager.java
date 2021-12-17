package com.refinedmods.refinedpipes.network.pipe.attachment;

import com.refinedmods.refinedpipes.network.pipe.Pipe;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
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
    public void openAttachmentContainer(Direction dir, ServerPlayerEntity player) {
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

    public CompoundNBT writeToNbt(CompoundNBT tag) {
        ListNBT attch = new ListNBT();
        getAttachments().forEach(a -> {
            CompoundNBT attchTag = new CompoundNBT();
            attchTag.putString("typ", a.getId().toString());
            attch.add(a.writeToNbt(attchTag));
        });
        tag.put("attch", attch);
        return tag;
    }

    public void readFromNbt(CompoundNBT tag) {
        ListNBT attch = tag.getList("attch", Constants.NBT.TAG_COMPOUND);
        for (INBT item : attch) {
            CompoundNBT attchTag = (CompoundNBT) item;

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
    public void writeUpdate(CompoundNBT tag) {
        for (Direction dir : Direction.values()) {
            if (hasAttachment(dir)) {
                tag.putString("attch_" + dir.ordinal(), getAttachment(dir).getId().toString());
                tag.put("pb_" + dir.ordinal(), getAttachment(dir).getDrop().save(new CompoundNBT()));
            }
        }
    }

    @Override
    public void readUpdate(CompoundNBT tag) {
        throw new RuntimeException("Client-side only");
    }
}
