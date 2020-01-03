package com.raoulvdberge.refinedpipes.tile;

import com.raoulvdberge.refinedpipes.RefinedPipesTileEntities;
import com.raoulvdberge.refinedpipes.network.NetworkManager;
import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentRegistry;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentType;
import com.raoulvdberge.refinedpipes.render.Color;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class PipeTileEntity extends TileEntity implements ITickableTileEntity {
    public static final ModelProperty<Map<Direction, AttachmentType>> ATTACHMENTS_PROPERTY = new ModelProperty<>();

    private Color color;
    private Map<Direction, AttachmentType> attachments = new HashMap<>();

    public PipeTileEntity() {
        super(RefinedPipesTileEntities.PIPE);
    }

    @Override
    public void tick() {

    }

    public boolean hasAttachment(Direction dir) {
        return attachments.containsKey(dir);
    }

    public CompoundNBT writeUpdate(CompoundNBT tag) {
        Pipe pipe = NetworkManager.get(world).getPipe(pos);

        if (pipe != null && pipe.getNetwork() != null) {
            for (Direction dir : Direction.values()) {
                if (pipe.getAttachmentManager().hasAttachment(dir)) {
                    tag.putString("attch_" + dir.ordinal(), pipe.getAttachmentManager().getAttachment(dir).getType().getId().toString());
                }
            }

            pipe.getNetwork().getColor().writeToTag(tag);
        }

        return tag;
    }

    public void readUpdate(CompoundNBT tag) {
        this.color = Color.fromNbt(tag);

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

    @Override
    public final CompoundNBT getUpdateTag() {
        return writeUpdate(super.getUpdateTag());
    }

    @Override
    public final SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, 1, getUpdateTag());
    }

    @Override
    public final void onDataPacket(net.minecraft.network.NetworkManager net, SUpdateTileEntityPacket packet) {
        readUpdate(packet.getNbtCompound());
    }

    @Override
    public final void handleUpdateTag(CompoundNBT tag) {
        super.read(tag);

        readUpdate(tag);
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        return new ModelDataMap.Builder().withInitial(ATTACHMENTS_PROPERTY, attachments).build();
    }

    @Override
    public void validate() {
        super.validate();

        if (!world.isRemote) {
            NetworkManager mgr = NetworkManager.get(world);

            if (mgr.getPipe(pos) == null) {
                mgr.addPipe(new Pipe(world, pos));
            }
        }
    }

    @Override
    public void remove() {
        super.remove();

        if (!world.isRemote) {
            NetworkManager.get(world).removePipe(pos);
        }
    }

    public Color getColor() {
        return color;
    }
}
