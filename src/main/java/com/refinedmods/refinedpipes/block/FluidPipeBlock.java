package com.refinedmods.refinedpipes.block;

import com.refinedmods.refinedpipes.blockentity.FluidPipeBlockEntity;
import com.refinedmods.refinedpipes.network.pipe.fluid.FluidPipeType;
import com.refinedmods.refinedpipes.network.pipe.shape.PipeShapeCache;
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
    protected boolean hasConnection(LevelAccessor level, BlockPos pos, Direction direction) {
        BlockEntity currentBlockEntity = level.getBlockEntity(pos);
        if (currentBlockEntity instanceof FluidPipeBlockEntity &&
            ((FluidPipeBlockEntity) currentBlockEntity).getAttachmentManager().hasAttachment(direction)) {
            return false;
        }

        BlockState facingState = level.getBlockState(pos.relative(direction));
        BlockEntity facingBlockEntity = level.getBlockEntity(pos.relative(direction));

        if (facingBlockEntity instanceof FluidPipeBlockEntity &&
            ((FluidPipeBlockEntity) facingBlockEntity).getAttachmentManager().hasAttachment(direction.getOpposite())) {
            return false;
        }

        return facingState.getBlock() instanceof FluidPipeBlock
            && ((FluidPipeBlock) facingState.getBlock()).getType() == type;
    }

    @Override
    protected boolean hasInvConnection(LevelAccessor level, BlockPos pos, Direction direction) {
        BlockEntity facingBlockEntity = level.getBlockEntity(pos.relative(direction));

        return facingBlockEntity != null
            && facingBlockEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite()).isPresent();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FluidPipeBlockEntity(pos, state, type);
    }
}
