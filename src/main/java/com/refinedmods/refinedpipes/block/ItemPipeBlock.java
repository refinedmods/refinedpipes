package com.refinedmods.refinedpipes.block;

import com.refinedmods.refinedpipes.network.pipe.item.ItemPipeType;
import com.refinedmods.refinedpipes.network.pipe.shape.PipeShapeCache;
import com.refinedmods.refinedpipes.tile.ItemPipeTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.Nullable;

public class ItemPipeBlock extends PipeBlock implements EntityBlock {
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
    protected boolean hasConnection(LevelAccessor world, BlockPos pos, Direction direction) {
        BlockEntity currentTile = world.getBlockEntity(pos);
        if (currentTile instanceof ItemPipeTileEntity &&
            ((ItemPipeTileEntity) currentTile).getAttachmentManager().hasAttachment(direction)) {
            return false;
        }

        BlockState facingState = world.getBlockState(pos.relative(direction));
        BlockEntity facingTile = world.getBlockEntity(pos.relative(direction));

        if (facingTile instanceof ItemPipeTileEntity &&
            ((ItemPipeTileEntity) facingTile).getAttachmentManager().hasAttachment(direction.getOpposite())) {
            return false;
        }

        return facingState.getBlock() instanceof ItemPipeBlock;
    }

    @Override
    protected boolean hasInvConnection(LevelAccessor world, BlockPos pos, Direction direction) {
        BlockEntity facingTile = world.getBlockEntity(pos.relative(direction));

        return facingTile != null
            && facingTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()).isPresent();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ItemPipeTileEntity(pos, state, type);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? (levelTicker, pos, stateTicker, blockEntity) -> ItemPipeTileEntity.tick((ItemPipeTileEntity) blockEntity) : null;
    }
}
