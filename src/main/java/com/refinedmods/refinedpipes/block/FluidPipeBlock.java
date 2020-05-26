package com.refinedmods.refinedpipes.block;

import com.refinedmods.refinedpipes.network.pipe.fluid.FluidPipeType;
import com.refinedmods.refinedpipes.network.pipe.shape.PipeShapeCache;
import com.refinedmods.refinedpipes.tile.FluidPipeTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;

public class FluidPipeBlock extends PipeBlock {
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
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new FluidPipeTileEntity(type);
    }

    @Override
    protected boolean hasConnection(IWorld world, BlockPos pos, Direction direction) {
        TileEntity currentTile = world.getTileEntity(pos);
        if (currentTile instanceof FluidPipeTileEntity &&
            ((FluidPipeTileEntity) currentTile).getAttachmentManager().hasAttachment(direction)) {
            return false;
        }

        BlockState facingState = world.getBlockState(pos.offset(direction));
        TileEntity facingTile = world.getTileEntity(pos.offset(direction));

        if (facingTile instanceof FluidPipeTileEntity &&
            ((FluidPipeTileEntity) facingTile).getAttachmentManager().hasAttachment(direction.getOpposite())) {
            return false;
        }

        return facingState.getBlock() instanceof FluidPipeBlock
            && ((FluidPipeBlock) facingState.getBlock()).getType() == type;
    }

    @Override
    protected boolean hasInvConnection(IWorld world, BlockPos pos, Direction direction) {
        TileEntity facingTile = world.getTileEntity(pos.offset(direction));

        return facingTile != null
            && facingTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite()).isPresent();
    }
}
