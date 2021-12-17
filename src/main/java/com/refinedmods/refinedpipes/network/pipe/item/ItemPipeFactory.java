package com.refinedmods.refinedpipes.network.pipe.item;

import com.refinedmods.refinedpipes.network.pipe.Pipe;
import com.refinedmods.refinedpipes.network.pipe.PipeFactory;
import com.refinedmods.refinedpipes.network.pipe.transport.ItemTransport;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;

public class ItemPipeFactory implements PipeFactory {
    @Override
    public Pipe createFromNbt(Level level, CompoundTag tag) {
        BlockPos pos = BlockPos.of(tag.getLong("pos"));

        ItemPipeType pipeType = ItemPipeType.values()[tag.getInt("type")];

        ItemPipe pipe = new ItemPipe(level, pos, pipeType);

        pipe.getAttachmentManager().readFromNbt(tag);

        ListTag transports = tag.getList("transports", Tag.TAG_COMPOUND);
        for (int i = 0; i < transports.size(); ++i) {
            CompoundTag transportTag = transports.getCompound(i);

            ItemTransport itemTransport = ItemTransport.of(transportTag);
            if (itemTransport != null) {
                pipe.getTransports().add(itemTransport);
            }
        }

        return pipe;
    }
}
