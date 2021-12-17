package com.refinedmods.refinedpipes.block;

import com.refinedmods.refinedpipes.blockentity.EnergyPipeBlockEntity;
import com.refinedmods.refinedpipes.network.pipe.energy.EnergyPipeEnergyStorage;
import com.refinedmods.refinedpipes.network.pipe.energy.EnergyPipeType;
import com.refinedmods.refinedpipes.network.pipe.shape.PipeShapeCache;
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
    protected boolean hasConnection(LevelAccessor level, BlockPos pos, Direction direction) {
        BlockEntity currentBlockEntity = level.getBlockEntity(pos);
        if (currentBlockEntity instanceof EnergyPipeBlockEntity &&
            ((EnergyPipeBlockEntity) currentBlockEntity).getAttachmentManager().hasAttachment(direction)) {
            return false;
        }

        BlockState facingState = level.getBlockState(pos.relative(direction));
        BlockEntity facingBlockEntity = level.getBlockEntity(pos.relative(direction));

        if (facingBlockEntity instanceof EnergyPipeBlockEntity &&
            ((EnergyPipeBlockEntity) facingBlockEntity).getAttachmentManager().hasAttachment(direction.getOpposite())) {
            return false;
        }

        return facingState.getBlock() instanceof EnergyPipeBlock
            && ((EnergyPipeBlock) facingState.getBlock()).getType() == type;
    }

    @Override
    protected boolean hasInvConnection(LevelAccessor level, BlockPos pos, Direction direction) {
        BlockEntity facingBlockEntityy = level.getBlockEntity(pos.relative(direction));
        if (facingBlockEntityy == null) {
            return false;
        }

        IEnergyStorage energyStorage = facingBlockEntityy.getCapability(CapabilityEnergy.ENERGY, direction.getOpposite()).orElse(null);
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
        return new EnergyPipeBlockEntity(pos, state, type);
    }
}
