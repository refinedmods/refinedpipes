package com.raoulvdberge.refinedpipes.tile;

import com.raoulvdberge.refinedpipes.network.NetworkManager;
import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.pipe.PipeType;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentRegistry;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentType;
import com.raoulvdberge.refinedpipes.network.pipe.transport.ItemTransport;
import com.raoulvdberge.refinedpipes.network.pipe.transport.ItemTransportProps;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.InventoryHelper;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PipeTileEntity extends TileEntity implements ITickableTileEntity {
    public static final ModelProperty<Map<Direction, AttachmentType>> ATTACHMENTS_PROPERTY = new ModelProperty<>();

    private Map<Direction, AttachmentType> attachments = new HashMap<>();
    private List<ItemTransportProps> props = new ArrayList<>();

    private final PipeType type;

    public PipeTileEntity(PipeType type) {
        super(type.getTileType());

        this.type = type;
    }

    @Override
    public void tick() {
        if (world.isRemote) {
            props.forEach(ItemTransportProps::tick);
        }
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

    public List<ItemTransportProps> getProps() {
        return props;
    }

    public void setProps(List<ItemTransportProps> props) {
        this.props = props;
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
                mgr.addPipe(new Pipe(world, pos, type));
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
                for (ItemTransport transport : pipe.getTransports()) {
                    InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), transport.getValue());
                }
            }

            mgr.removePipe(pos);
        }
    }
}
