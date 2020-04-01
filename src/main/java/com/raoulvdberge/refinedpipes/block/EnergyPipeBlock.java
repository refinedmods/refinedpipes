package com.raoulvdberge.refinedpipes.block;

import com.raoulvdberge.refinedpipes.network.pipe.energy.EnergyPipeEnergyStorage;
import com.raoulvdberge.refinedpipes.network.pipe.energy.EnergyPipeType;
import com.raoulvdberge.refinedpipes.tile.EnergyPipeTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;

public class EnergyPipeBlock extends PipeBlock {
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
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new EnergyPipeTileEntity(type);
    }

    @Override
    protected boolean hasConnection(IWorld world, BlockPos pos, Direction direction) {
        TileEntity currentTile = world.getTileEntity(pos);
        if (currentTile instanceof EnergyPipeTileEntity &&
            ((EnergyPipeTileEntity) currentTile).getAttachmentManager().hasAttachment(direction)) {
            return false;
        }

        BlockState facingState = world.getBlockState(pos.offset(direction));
        TileEntity facingTile = world.getTileEntity(pos.offset(direction));

        if (facingTile instanceof EnergyPipeTileEntity &&
            ((EnergyPipeTileEntity) facingTile).getAttachmentManager().hasAttachment(direction.getOpposite())) {
            return false;
        }

        return facingState.getBlock() instanceof EnergyPipeBlock;
    }

    @Override
    protected boolean hasInvConnection(IWorld world, BlockPos pos, Direction direction) {
        TileEntity facingTile = world.getTileEntity(pos.offset(direction));

        return facingTile != null
            && facingTile.getCapability(CapabilityEnergy.ENERGY, direction.getOpposite()).isPresent()
            && !(facingTile.getCapability(CapabilityEnergy.ENERGY, direction.getOpposite()).orElse(null) instanceof EnergyPipeEnergyStorage);
    }
}
