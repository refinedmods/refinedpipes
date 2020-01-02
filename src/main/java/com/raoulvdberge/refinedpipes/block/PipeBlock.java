package com.raoulvdberge.refinedpipes.block;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.tile.PipeTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

import javax.annotation.Nullable;

public class PipeBlock extends Block {
    private static final BooleanProperty NORTH = BooleanProperty.create("north");
    private static final BooleanProperty EAST = BooleanProperty.create("east");
    private static final BooleanProperty SOUTH = BooleanProperty.create("south");
    private static final BooleanProperty WEST = BooleanProperty.create("west");
    private static final BooleanProperty UP = BooleanProperty.create("up");
    private static final BooleanProperty DOWN = BooleanProperty.create("down");

    public static final VoxelShape CORE_SHAPE = makeCuboidShape(4, 4, 4, 12, 12, 12);
    public static final VoxelShape NORTH_EXTENSION_SHAPE = makeCuboidShape(4, 4, 0, 12, 12, 4);
    public static final VoxelShape EAST_EXTENSION_SHAPE = makeCuboidShape(12, 4, 4, 16, 12, 12);
    public static final VoxelShape SOUTH_EXTENSION_SHAPE = makeCuboidShape(4, 4, 12, 12, 12, 16);
    public static final VoxelShape WEST_EXTENSION_SHAPE = makeCuboidShape(0, 4, 4, 4, 12, 12);
    public static final VoxelShape UP_EXTENSION_SHAPE = makeCuboidShape(4, 12, 4, 12, 16, 12);
    public static final VoxelShape DOWN_EXTENSION_SHAPE = makeCuboidShape(4, 0, 4, 12, 4, 12);

    public PipeBlock() {
        super(Block.Properties.create(Material.ROCK));

        this.setRegistryName(RefinedPipes.ID, "pipe");
        this.setDefaultState(getDefaultState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(UP, false).with(DOWN, false));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new PipeTile();
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);

        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext ctx) {
        return getState(getDefaultState(), ctx.getWorld(), ctx.getPos());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updatePostPlacement(BlockState state, Direction dir, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos) {
        return getState(state, world, pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
        VoxelShape shape = CORE_SHAPE;

        if (state.get(NORTH)) {
            shape = VoxelShapes.or(shape, NORTH_EXTENSION_SHAPE);
        }

        if (state.get(EAST)) {
            shape = VoxelShapes.or(shape, EAST_EXTENSION_SHAPE);
        }

        if (state.get(SOUTH)) {
            shape = VoxelShapes.or(shape, SOUTH_EXTENSION_SHAPE);
        }

        if (state.get(WEST)) {
            shape = VoxelShapes.or(shape, WEST_EXTENSION_SHAPE);
        }

        if (state.get(UP)) {
            shape = VoxelShapes.or(shape, UP_EXTENSION_SHAPE);
        }

        if (state.get(DOWN)) {
            shape = VoxelShapes.or(shape, DOWN_EXTENSION_SHAPE);
        }

        return shape;
    }

    private boolean hasNode(IWorld world, BlockPos pos, Direction direction) {
        return world.getBlockState(pos.offset(direction)).getBlock() instanceof PipeBlock ||
            world.getTileEntity(pos.offset(direction)) instanceof ChestTileEntity;
    }

    private BlockState getState(BlockState currentState, IWorld world, BlockPos pos) {
        boolean north = hasNode(world, pos, Direction.NORTH);
        boolean east = hasNode(world, pos, Direction.EAST);
        boolean south = hasNode(world, pos, Direction.SOUTH);
        boolean west = hasNode(world, pos, Direction.WEST);
        boolean up = hasNode(world, pos, Direction.UP);
        boolean down = hasNode(world, pos, Direction.DOWN);

        return currentState
            .with(NORTH, north)
            .with(EAST, east)
            .with(SOUTH, south)
            .with(WEST, west)
            .with(UP, up)
            .with(DOWN, down);
    }
}
