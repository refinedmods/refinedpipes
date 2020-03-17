package com.raoulvdberge.refinedpipes.block;

import com.raoulvdberge.refinedpipes.item.AttachmentItem;
import com.raoulvdberge.refinedpipes.network.NetworkManager;
import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.pipe.PipeType;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.Attachment;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentType;
import com.raoulvdberge.refinedpipes.tile.PipeTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class PipeBlock extends Block {
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");

    public static final VoxelShape CORE_SHAPE = makeCuboidShape(4, 4, 4, 12, 12, 12);
    public static final VoxelShape NORTH_EXTENSION_SHAPE = makeCuboidShape(4, 4, 0, 12, 12, 4);
    public static final VoxelShape EAST_EXTENSION_SHAPE = makeCuboidShape(12, 4, 4, 16, 12, 12);
    public static final VoxelShape SOUTH_EXTENSION_SHAPE = makeCuboidShape(4, 4, 12, 12, 12, 16);
    public static final VoxelShape WEST_EXTENSION_SHAPE = makeCuboidShape(0, 4, 4, 4, 12, 12);
    public static final VoxelShape UP_EXTENSION_SHAPE = makeCuboidShape(4, 12, 4, 12, 16, 12);
    public static final VoxelShape DOWN_EXTENSION_SHAPE = makeCuboidShape(4, 0, 4, 12, 4, 12);

    private final PipeType type;

    public PipeBlock(PipeType type) {
        super(Block.Properties.create(Material.ROCK));

        this.type = type;

        this.setRegistryName(type.getId());
        this.setDefaultState(getDefaultState()
            .with(NORTH, false)
            .with(EAST, false)
            .with(SOUTH, false)
            .with(WEST, false)
            .with(UP, false)
            .with(DOWN, false)
        );
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack held = player.getHeldItemMainhand();

        if (held.getItem() instanceof AttachmentItem) {
            return addAttachment(player, world, pos, held, hit.getFace());
        } else if (held.isEmpty() && player.isCrouching()) {
            return removeAttachment(world, pos, hit.getFace());
        }

        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }

    private ActionResultType addAttachment(PlayerEntity player, World world, BlockPos pos, ItemStack attachment, Direction dir) {
        if (!world.isRemote) {
            Pipe pipe = NetworkManager.get(world).getPipe(pos);

            if (pipe != null && !pipe.getAttachmentManager().hasAttachment(dir)) {
                AttachmentType type = ((AttachmentItem) attachment.getItem()).getType();

                pipe.getAttachmentManager().setAttachment(dir, type);
                pipe.sendBlockUpdate();

                NetworkManager.get(world).markDirty();

                if (!player.isCreative()) {
                    attachment.shrink(1);
                }
            }
        }

        return ActionResultType.SUCCESS;
    }

    private ActionResultType removeAttachment(World world, BlockPos pos, Direction dir) {
        if (!world.isRemote) {
            Pipe pipe = NetworkManager.get(world).getPipe(pos);

            if (pipe != null && pipe.getAttachmentManager().hasAttachment(dir)) {
                Attachment attachment = pipe.getAttachmentManager().getAttachment(dir);

                pipe.getAttachmentManager().removeAttachment(dir);
                pipe.sendBlockUpdate();

                NetworkManager.get(world).markDirty();

                Block.spawnAsEntity(world, pos.offset(dir), attachment.getType().toStack());
            }

            return ActionResultType.SUCCESS;
        } else {
            return ((PipeTileEntity) world.getTileEntity(pos)).hasAttachment(dir) ? ActionResultType.SUCCESS : ActionResultType.FAIL;
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new PipeTileEntity(type);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);

        builder.add(
            NORTH,
            EAST,
            SOUTH,
            WEST,
            UP,
            DOWN
        );
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);

        if (!world.isRemote) {
            Pipe pipe = NetworkManager.get(world).getPipe(pos);

            if (pipe != null && pipe.getNetwork() != null) {
                pipe.getNetwork().scanGraph(world, pos);
            }
        }
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

    private static boolean hasConnection(IWorld world, BlockPos pos, Direction direction) {
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof PipeTileEntity)) {
            return false;
        }

        if (((PipeTileEntity) tile).hasAttachment(direction)) {
            return false;
        }

        return world.getBlockState(pos.offset(direction)).getBlock() instanceof PipeBlock
            || world.getTileEntity(pos.offset(direction)) instanceof ChestTileEntity;
    }

    private static BlockState getState(BlockState currentState, IWorld world, BlockPos pos) {
        boolean north = hasConnection(world, pos, Direction.NORTH);
        boolean east = hasConnection(world, pos, Direction.EAST);
        boolean south = hasConnection(world, pos, Direction.SOUTH);
        boolean west = hasConnection(world, pos, Direction.WEST);
        boolean up = hasConnection(world, pos, Direction.UP);
        boolean down = hasConnection(world, pos, Direction.DOWN);

        return currentState
            .with(NORTH, north)
            .with(EAST, east)
            .with(SOUTH, south)
            .with(WEST, west)
            .with(UP, up)
            .with(DOWN, down);
    }
}
