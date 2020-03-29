package com.raoulvdberge.refinedpipes.tile;

import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.pipe.item.ItemPipe;
import com.raoulvdberge.refinedpipes.network.pipe.item.ItemPipeType;
import com.raoulvdberge.refinedpipes.network.pipe.transport.ItemTransport;
import com.raoulvdberge.refinedpipes.network.pipe.transport.ItemTransportProps;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ItemPipeTileEntity extends PipeTileEntity implements ITickableTileEntity {
    private List<ItemTransportProps> props = new ArrayList<>();

    private final ItemPipeType type;

    public ItemPipeTileEntity(ItemPipeType type) {
        super(type.getTileType());

        this.type = type;
    }

    @Override
    public void tick() {
        if (world.isRemote) {
            props.forEach(ItemTransportProps::tick);
        }
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
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), transport.getValue());
        }
    }

    @Override
    protected Pipe createPipe(World world, BlockPos pos) {
        return new ItemPipe(world, pos, type);
    }
}
