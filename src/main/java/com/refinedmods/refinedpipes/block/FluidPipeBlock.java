package com.refinedmods.refinedpipes.block;

import com.refinedmods.refinedpipes.network.pipe.fluid.FluidPipeType;
import com.refinedmods.refinedpipes.network.pipe.shape.PipeShapeCache;
import com.refinedmods.refinedpipes.tile.FluidPipeTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class FluidPipeBlock extends PipeBlock implements EntityBlock {
    private final FluidPipeType type;

    public FluidPipeBlock(PipeShapeCache shapeCache, FluidPipeType type) {
        super(shapeCache);

        this.type = type;
        this.setRegistryName(type.getId());
    }

    public FluidPipeType getType() {
        return type;
    }

    @Override
    protected boolean hasConnection(LevelAccessor world, BlockPos pos, Direction direction) {
        BlockEntity currentTile = world.getBlockEntity(pos);
        if (currentTile instanceof FluidPipeTileEntity &&
            ((FluidPipeTileEntity) currentTile).getAttachmentManager().hasAttachment(direction)) {
            return false;
        }

        BlockState facingState = world.getBlockState(pos.relative(direction));
        BlockEntity facingTile = world.getBlockEntity(pos.relative(direction));

        if (facingTile instanceof FluidPipeTileEntity &&
            ((FluidPipeTileEntity) facingTile).getAttachmentManager().hasAttachment(direction.getOpposite())) {
            return false;
        }

        return facingState.getBlock() instanceof FluidPipeBlock
            && ((FluidPipeBlock) facingState.getBlock()).getType() == type;
    }

    @Override
    protected boolean hasInvConnection(LevelAccessor world, BlockPos pos, Direction direction) {
        BlockEntity facingTile = world.getBlockEntity(pos.relative(direction));

        return facingTile != null
            && facingTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite()).isPresent();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FluidPipeTileEntity(pos, state, type);
    }
}
