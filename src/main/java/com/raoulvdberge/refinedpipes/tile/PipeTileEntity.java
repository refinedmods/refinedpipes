package com.raoulvdberge.refinedpipes.tile;

import com.raoulvdberge.refinedpipes.RefinedPipesTileEntities;
import com.raoulvdberge.refinedpipes.network.NetworkManager;
import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentRegistry;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentType;
import com.raoulvdberge.refinedpipes.render.Color;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
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
    private ItemStack stack;
    private byte maxTicksInPipe;
    private int progress;
    private Direction direction = Direction.NORTH;
    private Direction initialDirection = Direction.NORTH;
    private boolean lastPipe = false;
    private boolean firstPipe = false;

    public PipeTileEntity() {
        super(RefinedPipesTileEntities.PIPE);
    }

    @Override
    public void tick() {
        if (world.isRemote && stack != null) {
            progress++;
        }
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

            if (pipe.getCurrentTransport() != null) {
                tag.put("transport", pipe.getCurrentTransport().getValue().write(new CompoundNBT()));
                tag.putInt("pr", pipe.getCurrentTransport().getProgressInCurrentPipe());
                tag.putByte("dir", (byte)pipe.getCurrentTransport().getDirection().ordinal());
                tag.putByte("idir", (byte)pipe.getCurrentTransport().getInitialDirection().ordinal());
                tag.putBoolean("lst", pipe.getCurrentTransport().isLastPipe());
                tag.putBoolean("fst", pipe.getCurrentTransport().isFirstPipe());
            }

            tag.putByte("mt", pipe.getMaxTicksInPipe());
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

        if (tag.contains("mt")) {
            this.maxTicksInPipe = tag.getByte("mt");
        }

        if (tag.contains("dir")) {
            this.direction = Direction.values()[tag.getByte("dir")];
        }

        if (tag.contains("idir")) {
            this.initialDirection = Direction.values()[tag.getByte("idir")];
        }

        if (tag.contains("pr")) {
            this.progress = tag.getInt("pr");
        } else {
            this.progress = 0;
        }

        if (tag.contains("lst")) {
            this.lastPipe = tag.getBoolean("lst");
        } else {
            this.lastPipe = false;
        }

        if (tag.contains("fst")) {
            this.firstPipe = tag.getBoolean("fst");
        } else {
            this.firstPipe = false;
        }

        if (tag.contains("transport")) {
            this.stack = ItemStack.read(tag.getCompound("transport"));
        } else {
            this.stack = null;
        }

        requestModelDataUpdate();

        BlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 1 | 2);
    }

    public ItemStack getStack() {
        return stack;
    }

    public byte getMaxTicksInPipe() {
        return maxTicksInPipe;
    }

    public int getProgress() {
        return progress;
    }

    public Direction getDirection() {
        return direction;
    }

    public Direction getInitialDirection() {
        return initialDirection;
    }

    public boolean isFirstPipe() {
        return firstPipe;
    }

    public boolean isLastPipe() {
        return lastPipe;
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
