package com.refinedmods.refinedpipes.blockentity;

import com.refinedmods.refinedpipes.network.NetworkManager;
import com.refinedmods.refinedpipes.network.pipe.Pipe;
import com.refinedmods.refinedpipes.network.pipe.attachment.Attachment;
import com.refinedmods.refinedpipes.network.pipe.attachment.AttachmentManager;
import com.refinedmods.refinedpipes.network.pipe.attachment.ClientAttachmentManager;
import com.refinedmods.refinedpipes.network.pipe.attachment.DummyAttachmentManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class PipeBlockEntity extends BaseBlockEntity {
    public static final ModelProperty<ResourceLocation[]> ATTACHMENTS_PROPERTY = new ModelProperty<>();
    private final AttachmentManager clientAttachmentManager = new ClientAttachmentManager();

    protected PipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

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

    // TODO: remove when https://github.com/MinecraftForge/MinecraftForge/pull/8303/files is merged
    private boolean unloaded;

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        unloaded = true;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        if (!level.isClientSide && !unloaded) {
            NetworkManager mgr = NetworkManager.get(level);

            Pipe pipe = mgr.getPipe(worldPosition);
            if (pipe != null) {
                spawnDrops(pipe);

                for (Attachment attachment : pipe.getAttachmentManager().getAttachments()) {
                    Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), attachment.getDrop());
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
    public CompoundTag writeUpdate(CompoundTag tag) {
        getAttachmentManager().writeUpdate(tag);

        return tag;
    }

    @Override
    public void readUpdate(@Nullable CompoundTag tag) {
        getAttachmentManager().readUpdate(tag);

        requestModelDataUpdate();

        BlockState state = level.getBlockState(worldPosition);
        level.sendBlockUpdated(worldPosition, state, state, 1 | 2);
    }

    protected abstract Pipe createPipe(Level level, BlockPos pos);
}
