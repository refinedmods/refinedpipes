package com.raoulvdberge.refinedpipes.tile;

import com.raoulvdberge.refinedpipes.network.NetworkManager;
import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.Attachment;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentRegistry;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentType;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public abstract class PipeTileEntity extends BaseTileEntity {
    public static final ModelProperty<Map<Direction, AttachmentType>> ATTACHMENTS_PROPERTY = new ModelProperty<>();

    private final Map<Direction, AttachmentType> attachments = new HashMap<>();

    public PipeTileEntity(TileEntityType<?> type) {
        super(type);
    }

    public boolean hasAttachment(Direction dir) {
        if (!world.isRemote) {
            Pipe pipe = NetworkManager.get(world).getPipe(pos);

            if (pipe != null) {
                return pipe.getAttachmentManager().hasAttachment(dir);
            }

            return false;
        }

        return attachments.containsKey(dir);
    }

    @Nullable
    public AttachmentType getAttachment(Direction dir) {
        if (!world.isRemote) {
            throw new IllegalStateException("Client-side only");
        }

        return attachments.get(dir);
    }

    @Override
    public void validate() {
        super.validate();

        if (!world.isRemote) {
            NetworkManager mgr = NetworkManager.get(world);

            if (mgr.getPipe(pos) == null) {
                mgr.addPipe(createPipe(world, pos));
            }
        }
    }

    @Override
    public void remove() {
        super.remove();

        if (!world.isRemote) {
            NetworkManager mgr = NetworkManager.get(world);

            Pipe pipe = mgr.getPipe(pos);
            if (pipe != null) {
                spawnDrops(pipe);

                for (Attachment attachment : pipe.getAttachmentManager().getAttachments()) {
                    InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), attachment.getType().toStack());
                }
            }

            mgr.removePipe(pos);
        }
    }

    protected void spawnDrops(Pipe pipe) {
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        return new ModelDataMap.Builder().withInitial(ATTACHMENTS_PROPERTY, attachments).build();
    }

    @Override
    public CompoundNBT writeUpdate(CompoundNBT tag) {
        Pipe pipe = NetworkManager.get(world).getPipe(pos);

        if (pipe != null && pipe.getNetwork() != null) {
            for (Direction dir : Direction.values()) {
                if (pipe.getAttachmentManager().hasAttachment(dir)) {
                    tag.putString("attch_" + dir.ordinal(), pipe.getAttachmentManager().getAttachment(dir).getType().getId().toString());
                }
            }
        }

        return tag;
    }

    @Override
    public void readUpdate(CompoundNBT tag) {
        this.attachments.clear();
        for (Direction dir : Direction.values()) {
            String key = "attch_" + dir.ordinal();

            if (tag.contains(key)) {
                this.attachments.put(dir, AttachmentRegistry.INSTANCE.getType(new ResourceLocation(tag.getString(key))));
            }
        }

        requestModelDataUpdate();

        BlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 1 | 2);
    }

    protected abstract Pipe createPipe(World world, BlockPos pos);
}
