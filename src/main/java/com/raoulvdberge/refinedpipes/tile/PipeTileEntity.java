package com.raoulvdberge.refinedpipes.tile;

import com.raoulvdberge.refinedpipes.network.NetworkManager;
import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.Attachment;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentManager;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.ClientAttachmentManager;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.DummyAttachmentManager;
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
import java.util.Map;

public abstract class PipeTileEntity extends BaseTileEntity {
    public static final ModelProperty<Map<Direction, ResourceLocation>> ATTACHMENTS_PROPERTY = new ModelProperty<>();

    public PipeTileEntity(TileEntityType<?> type) {
        super(type);
    }

    private final AttachmentManager clientAttachmentManager = new ClientAttachmentManager();

    public AttachmentManager getAttachmentManager() {
        if (world.isRemote) {
            return clientAttachmentManager;
        }

        Pipe pipe = NetworkManager.get(world).getPipe(pos);

        if (pipe != null) {
            return pipe.getAttachmentManager();
        }

        return DummyAttachmentManager.INSTANCE;
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
                    InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), attachment.getDrop());
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
        return new ModelDataMap.Builder().withInitial(ATTACHMENTS_PROPERTY, getAttachmentManager().getAttachmentsPerDirection()).build();
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

        BlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 1 | 2);
    }

    protected abstract Pipe createPipe(World world, BlockPos pos);
}
