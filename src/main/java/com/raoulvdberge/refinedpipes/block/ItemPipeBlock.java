package com.raoulvdberge.refinedpipes.block;

import com.raoulvdberge.refinedpipes.network.pipe.item.ItemPipeType;
import com.raoulvdberge.refinedpipes.tile.ItemPipeTileEntity;
import com.raoulvdberge.refinedpipes.tile.PipeTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

public class ItemPipeBlock extends PipeBlock {
    private final ItemPipeType type;

    public ItemPipeBlock(ItemPipeType type) {
        this.type = type;
        this.setRegistryName(type.getId());
    }

    public ItemPipeType getType() {
        return type;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ItemPipeTileEntity(type);
    }

    @Override
    protected boolean hasConnection(IWorld world, BlockPos pos, Direction direction) {
        TileEntity currentTile = world.getTileEntity(pos);
        if (currentTile instanceof PipeTileEntity && ((PipeTileEntity) currentTile).hasAttachment(direction)) {
            return false;
        }

        BlockState facingState = world.getBlockState(pos.offset(direction));
        TileEntity facingTile = world.getTileEntity(pos.offset(direction));

        if (facingTile instanceof PipeTileEntity && ((PipeTileEntity) facingTile).hasAttachment(direction.getOpposite())) {
            return false;
        }

        return facingState.getBlock() instanceof PipeBlock;
    }

    @Override
    protected boolean hasInvConnection(IWorld world, BlockPos pos, Direction direction) {
        TileEntity facingTile = world.getTileEntity(pos.offset(direction));

        return facingTile != null
            && facingTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()).isPresent();
    }
}
