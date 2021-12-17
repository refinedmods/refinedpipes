package com.refinedmods.refinedpipes.block;

import com.refinedmods.refinedpipes.network.pipe.item.ItemPipeType;
import com.refinedmods.refinedpipes.network.pipe.shape.PipeShapeCache;
import com.refinedmods.refinedpipes.tile.ItemPipeTileEntity;
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

    public ItemPipeBlock(PipeShapeCache shapeCache, ItemPipeType type) {
        super(shapeCache);

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
        TileEntity currentTile = world.getBlockEntity(pos);
        if (currentTile instanceof ItemPipeTileEntity &&
            ((ItemPipeTileEntity) currentTile).getAttachmentManager().hasAttachment(direction)) {
            return false;
        }

        BlockState facingState = world.getBlockState(pos.relative(direction));
        TileEntity facingTile = world.getBlockEntity(pos.relative(direction));

        if (facingTile instanceof ItemPipeTileEntity &&
            ((ItemPipeTileEntity) facingTile).getAttachmentManager().hasAttachment(direction.getOpposite())) {
            return false;
        }

        return facingState.getBlock() instanceof ItemPipeBlock;
    }

    @Override
    protected boolean hasInvConnection(IWorld world, BlockPos pos, Direction direction) {
        TileEntity facingTile = world.getBlockEntity(pos.relative(direction));

        return facingTile != null
            && facingTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()).isPresent();
    }
}
