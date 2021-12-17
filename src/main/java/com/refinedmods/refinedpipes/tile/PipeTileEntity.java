package com.refinedmods.refinedpipes.tile;

import com.refinedmods.refinedpipes.network.NetworkManager;
import com.refinedmods.refinedpipes.network.pipe.Pipe;
import com.refinedmods.refinedpipes.network.pipe.attachment.Attachment;
import com.refinedmods.refinedpipes.network.pipe.attachment.AttachmentManager;
import com.refinedmods.refinedpipes.network.pipe.attachment.ClientAttachmentManager;
import com.refinedmods.refinedpipes.network.pipe.attachment.DummyAttachmentManager;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;

public abstract class PipeTileEntity extends BaseTileEntity {
    public static final ModelProperty<ResourceLocation[]> ATTACHMENTS_PROPERTY = new ModelProperty<>();

    public PipeTileEntity(TileEntityType<?> type) {
        super(type);
    }

    private final AttachmentManager clientAttachmentManager = new ClientAttachmentManager();

    public AttachmentManager getAttachmentManager() {
        if (level.isClientSide) {
            return clientAttachmentManager;
        }

        Pipe pipe = NetworkManager.get(level).getPipe(worldPosition);

        if (pipe != null) {
            return pipe.getAttachmentManager();
        }

        return DummyAttachmentManager.INSTANCE;
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();

        if (!level.isClientSide) {
            NetworkManager mgr = NetworkManager.get(level);

            if (mgr.getPipe(worldPosition) == null) {
                mgr.addPipe(createPipe(level, worldPosition));
            }
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        if (!level.isClientSide) {
            NetworkManager mgr = NetworkManager.get(level);

            Pipe pipe = mgr.getPipe(worldPosition);
            if (pipe != null) {
                spawnDrops(pipe);

                for (Attachment attachment : pipe.getAttachmentManager().getAttachments()) {
                    InventoryHelper.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), attachment.getDrop());
                }
            }

            mgr.removePipe(worldPosition);
        }
    }

    protected void spawnDrops(Pipe pipe) {
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        return new ModelDataMap.Builder().withInitial(ATTACHMENTS_PROPERTY, getAttachmentManager().getState()).build();
    }

    @Override
    public CompoundNBT writeUpdate(CompoundNBT tag) {
        getAttachmentManager().writeUpdate(tag);

        return tag;
    }

    @Override
    public void readUpdate(CompoundNBT tag) {
        getAttachmentManager().readUpdate(tag);

        requestModelDataUpdate();

        BlockState state = level.getBlockState(worldPosition);
        level.sendBlockUpdated(worldPosition, state, state, 1 | 2);
    }

    protected abstract Pipe createPipe(World world, BlockPos pos);
}
