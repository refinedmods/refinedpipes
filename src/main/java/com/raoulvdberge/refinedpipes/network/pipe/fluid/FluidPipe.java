package com.raoulvdberge.refinedpipes.network.pipe.fluid;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.message.FluidPipeMessage;
import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.Attachment;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentRegistry;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FluidPipe extends Pipe {
    private static final Logger LOGGER = LogManager.getLogger(FluidPipe.class);

    private final FluidPipeType type;
    private float lastFullness = 0;

    public FluidPipe(World world, BlockPos pos, FluidPipeType type) {
        super(world, pos);

        this.type = type;
    }

    @Override
    public void update(World world) {
        super.update(world);

        float f = getFullness();
        if (lastFullness != f) {
            lastFullness = f;

            sendFluidPipeUpdate();
        }
    }

    public void sendFluidPipeUpdate() {
        RefinedPipes.NETWORK.sendInArea(world, pos, 32, new FluidPipeMessage(pos, network.getFluidTank().getFluid(), getFullness()));
    }

    public float getFullness() {
        int cap = network.getFluidTank().getCapacity();
        int stored = network.getFluidTank().getFluidAmount();

        return (float) stored / (float) cap;
    }

    public FluidPipeType getType() {
        return type;
    }

    // TODO: write proper system for pipe serialisation
    @Override
    public CompoundNBT writeToNbt(CompoundNBT tag) {
        tag = super.writeToNbt(tag);

        tag.putString("id", new ResourceLocation(RefinedPipes.ID, "fluid").toString());
        tag.putInt("type", type.ordinal());

        return tag;
    }

    public static FluidPipe fromNbt(World world, CompoundNBT tag) {
        BlockPos pos = BlockPos.fromLong(tag.getLong("pos"));

        FluidPipeType pipeType = FluidPipeType.values()[tag.getInt("type")];

        FluidPipe pipe = new FluidPipe(world, pos, pipeType);

        // TODO: generify
        ListNBT attch = tag.getList("attch", Constants.NBT.TAG_COMPOUND);
        for (INBT item : attch) {
            CompoundNBT attchTag = (CompoundNBT) item;

            AttachmentType type = AttachmentRegistry.INSTANCE.getType(new ResourceLocation(attchTag.getString("typ")));
            if (type != null) {
                Attachment attachment = type.createFromNbt(attchTag);
                pipe.attachmentManager.setAttachment(attachment.getDirection(), attachment);
            } else {
                LOGGER.warn("Attachment {} no longer exists", attchTag.getString("typ"));
            }
        }

        return pipe;
    }

    @Override
    public boolean canFormNetworkWith(Pipe otherPipe) {
        return otherPipe instanceof FluidPipe;
    }
}
