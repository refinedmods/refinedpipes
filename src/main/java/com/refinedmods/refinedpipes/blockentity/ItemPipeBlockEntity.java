package com.refinedmods.refinedpipes.blockentity;

import com.refinedmods.refinedpipes.network.pipe.Pipe;
import com.refinedmods.refinedpipes.network.pipe.item.ItemPipe;
import com.refinedmods.refinedpipes.network.pipe.item.ItemPipeType;
import com.refinedmods.refinedpipes.network.pipe.transport.ItemTransport;
import com.refinedmods.refinedpipes.network.pipe.transport.ItemTransportProps;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class ItemPipeBlockEntity extends PipeBlockEntity {
    private final ItemPipeType type;
    private List<ItemTransportProps> props = new ArrayList<>();

    public ItemPipeBlockEntity(BlockPos pos, BlockState state, ItemPipeType type) {
        super(type.getBlockEntityType(), pos, state);
        this.type = type;
    }

    public static void tick(ItemPipeBlockEntity blockEntity) {
        blockEntity.props.forEach(ItemTransportProps::tick);
    }

    public List<ItemTransportProps> getProps() {
        return props;
    }

    public void setProps(List<ItemTransportProps> props) {
        this.props = props;
    }

    @Override
    protected void spawnDrops(Pipe pipe) {
        super.spawnDrops(pipe);

        for (ItemTransport transport : ((ItemPipe) pipe).getTransports()) {
            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), transport.getValue());
        }
    }

    @Override
    protected Pipe createPipe(Level level, BlockPos pos) {
        return new ItemPipe(level, pos, type);
    }
}
