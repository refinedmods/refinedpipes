package com.refinedmods.refinedpipes.block;

import com.refinedmods.refinedpipes.network.pipe.energy.EnergyPipeEnergyStorage;
import com.refinedmods.refinedpipes.network.pipe.energy.EnergyPipeType;
import com.refinedmods.refinedpipes.network.pipe.shape.PipeShapeCache;
import com.refinedmods.refinedpipes.tile.EnergyPipeTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class EnergyPipeBlock extends PipeBlock implements EntityBlock {
    private final EnergyPipeType type;

    public EnergyPipeBlock(PipeShapeCache shapeCache, EnergyPipeType type) {
        super(shapeCache);

        this.type = type;
        this.setRegistryName(type.getId());
    }

    public EnergyPipeType getType() {
        return type;
    }

    @Override
    protected boolean hasConnection(LevelAccessor world, BlockPos pos, Direction direction) {
        BlockEntity currentTile = world.getBlockEntity(pos);
        if (currentTile instanceof EnergyPipeTileEntity &&
            ((EnergyPipeTileEntity) currentTile).getAttachmentManager().hasAttachment(direction)) {
            return false;
        }

        BlockState facingState = world.getBlockState(pos.relative(direction));
        BlockEntity facingTile = world.getBlockEntity(pos.relative(direction));

        if (facingTile instanceof EnergyPipeTileEntity &&
            ((EnergyPipeTileEntity) facingTile).getAttachmentManager().hasAttachment(direction.getOpposite())) {
            return false;
        }

        return facingState.getBlock() instanceof EnergyPipeBlock
            && ((EnergyPipeBlock) facingState.getBlock()).getType() == type;
    }

    @Override
    protected boolean hasInvConnection(LevelAccessor world, BlockPos pos, Direction direction) {
        BlockEntity facingTile = world.getBlockEntity(pos.relative(direction));
        if (facingTile == null) {
            return false;
        }

        IEnergyStorage energyStorage = facingTile.getCapability(CapabilityEnergy.ENERGY, direction.getOpposite()).orElse(null);
        if (energyStorage == null) {
            return false;
        }

        if (energyStorage instanceof EnergyPipeEnergyStorage) {
            return false;
        }

        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyPipeTileEntity(pos, state, type);
    }
}
