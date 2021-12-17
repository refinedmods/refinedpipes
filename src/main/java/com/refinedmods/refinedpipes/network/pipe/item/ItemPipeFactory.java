package com.refinedmods.refinedpipes.network.pipe.item;

import com.refinedmods.refinedpipes.network.pipe.Pipe;
import com.refinedmods.refinedpipes.network.pipe.PipeFactory;
import com.refinedmods.refinedpipes.network.pipe.transport.ItemTransport;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class ItemPipeFactory implements PipeFactory {
    @Override
    public Pipe createFromNbt(World world, CompoundNBT tag) {
        BlockPos pos = BlockPos.of(tag.getLong("pos"));

        ItemPipeType pipeType = ItemPipeType.values()[tag.getInt("type")];

        ItemPipe pipe = new ItemPipe(world, pos, pipeType);

        pipe.getAttachmentManager().readFromNbt(tag);

        ListNBT transports = tag.getList("transports", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < transports.size(); ++i) {
            CompoundNBT transportTag = transports.getCompound(i);

            ItemTransport itemTransport = ItemTransport.of(transportTag);
            if (itemTransport != null) {
                pipe.getTransports().add(itemTransport);
            }
        }

        return pipe;
    }
}
