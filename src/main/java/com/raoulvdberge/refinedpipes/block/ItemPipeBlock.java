package com.raoulvdberge.refinedpipes.block;

import com.raoulvdberge.refinedpipes.item.AttachmentItem;
import com.raoulvdberge.refinedpipes.network.NetworkManager;
import com.raoulvdberge.refinedpipes.network.pipe.ItemPipe;
import com.raoulvdberge.refinedpipes.network.pipe.ItemPipeType;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.Attachment;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentType;
import com.raoulvdberge.refinedpipes.tile.ItemPipeTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class ItemPipeBlock extends Block {
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");

    public static final BooleanProperty INV_NORTH = BooleanProperty.create("inv_north");
    public static final BooleanProperty INV_EAST = BooleanProperty.create("inv_east");
    public static final BooleanProperty INV_SOUTH = BooleanProperty.create("inv_south");
    public static final BooleanProperty INV_WEST = BooleanProperty.create("inv_west");
    public static final BooleanProperty INV_UP = BooleanProperty.create("inv_up");
    public static final BooleanProperty INV_DOWN = BooleanProperty.create("inv_down");

    public static final VoxelShape CORE_SHAPE = makeCuboidShape(4, 4, 4, 12, 12, 12);
    public static final VoxelShape NORTH_EXTENSION_SHAPE = makeCuboidShape(4, 4, 0, 12, 12, 4);
    public static final VoxelShape EAST_EXTENSION_SHAPE = makeCuboidShape(12, 4, 4, 16, 12, 12);
    public static final VoxelShape SOUTH_EXTENSION_SHAPE = makeCuboidShape(4, 4, 12, 12, 12, 16);
    public static final VoxelShape WEST_EXTENSION_SHAPE = makeCuboidShape(0, 4, 4, 4, 12, 12);
    public static final VoxelShape UP_EXTENSION_SHAPE = makeCuboidShape(4, 12, 4, 12, 16, 12);
    public static final VoxelShape DOWN_EXTENSION_SHAPE = makeCuboidShape(4, 0, 4, 12, 4, 12);

    private static final VoxelShape NORTH_ATTACHMENT_SHAPE = makeCuboidShape(3, 3, 0, 13, 13, 3);
    private static final VoxelShape EAST_ATTACHMENT_SHAPE = makeCuboidShape(13, 3, 3, 16, 13, 13);
    private static final VoxelShape SOUTH_ATTACHMENT_SHAPE = makeCuboidShape(3, 3, 13, 13, 13, 16);
    private static final VoxelShape WEST_ATTACHMENT_SHAPE = makeCuboidShape(0, 3, 3, 3, 13, 13);
    private static final VoxelShape UP_ATTACHMENT_SHAPE = makeCuboidShape(3, 13, 3, 13, 16, 13);
    private static final VoxelShape DOWN_ATTACHMENT_SHAPE = makeCuboidShape(3, 0, 3, 13, 3, 13);

    private final ItemPipeType type;

    public ItemPipeBlock(ItemPipeType type) {
        super(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.35F));

        this.type = type;

        this.setRegistryName(type.getId());
        this.setDefaultState(getDefaultState()
            .with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(UP, false).with(DOWN, false)
            .with(INV_NORTH, false).with(INV_EAST, false).with(INV_SOUTH, false).with(INV_WEST, false).with(INV_UP, false).with(INV_DOWN, false)
        );
    }

    public ItemPipeType getType() {
        return type;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        Direction dirClicked = getAttachmentDirectionClicked(pos, hit.getHitVec());

        if (dirClicked == null) {
            dirClicked = hit.getFace();
        }

        ItemStack held = player.getHeldItemMainhand();

        if (held.getItem() instanceof AttachmentItem) {
            return addAttachment(player, world, pos, held, dirClicked);
        } else if (held.isEmpty() && player.isCrouching()) {
            return removeAttachment(world, pos, dirClicked);
        }

        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }

    @Nullable
    private Direction getAttachmentDirectionClicked(BlockPos pos, Vec3d hit) {
        if (NORTH_ATTACHMENT_SHAPE.getBoundingBox().grow(0.01).offset(pos).contains(hit)) {
            return Direction.NORTH;
        }

        if (EAST_ATTACHMENT_SHAPE.getBoundingBox().grow(0.01).offset(pos).contains(hit)) {
            return Direction.EAST;
        }

        if (SOUTH_ATTACHMENT_SHAPE.getBoundingBox().grow(0.01).offset(pos).contains(hit)) {
            return Direction.SOUTH;
        }

        if (WEST_ATTACHMENT_SHAPE.getBoundingBox().grow(0.01).offset(pos).contains(hit)) {
            return Direction.WEST;
        }

        if (UP_ATTACHMENT_SHAPE.getBoundingBox().grow(0.01).offset(pos).contains(hit)) {
            return Direction.UP;
        }

        if (DOWN_ATTACHMENT_SHAPE.getBoundingBox().grow(0.01).offset(pos).contains(hit)) {
            return Direction.DOWN;
        }

        return null;
    }

    private ActionResultType addAttachment(PlayerEntity player, World world, BlockPos pos, ItemStack attachment, Direction dir) {
        if (!world.isRemote) {
            ItemPipe pipe = NetworkManager.get(world).getPipe(pos);

            if (pipe != null && !pipe.getAttachmentManager().hasAttachment(dir)) {
                AttachmentType type = ((AttachmentItem) attachment.getItem()).getType();

                pipe.getAttachmentManager().setAttachment(dir, type);
                pipe.sendBlockUpdate();

                world.setBlockState(pos, getState(world.getBlockState(pos), world, pos));

                // Re-scan graph, required to rebuild destinations (chests with an attachment connected are no valid destination, refresh that)
                pipe.getNetwork().scanGraph(world, pos);

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
            ItemPipe pipe = NetworkManager.get(world).getPipe(pos);

            if (pipe != null && pipe.getAttachmentManager().hasAttachment(dir)) {
                Attachment attachment = pipe.getAttachmentManager().getAttachment(dir);

                pipe.getAttachmentManager().removeAttachment(dir);
                pipe.sendBlockUpdate();

                world.setBlockState(pos, getState(world.getBlockState(pos), world, pos));

                // Re-scan graph, required to rebuild destinations (chests with an attachment connected are no valid destination, refresh that)
                pipe.getNetwork().scanGraph(world, pos);

                NetworkManager.get(world).markDirty();

                Block.spawnAsEntity(world, pos.offset(dir), attachment.getType().toStack());
            }

            return ActionResultType.SUCCESS;
        } else {
            return ((ItemPipeTileEntity) world.getTileEntity(pos)).hasAttachment(dir) ? ActionResultType.SUCCESS : ActionResultType.FAIL;
        }
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
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);

        builder.add(
            NORTH, EAST, SOUTH, WEST, UP, DOWN,
            INV_NORTH, INV_EAST, INV_SOUTH, INV_WEST, INV_UP, INV_DOWN
        );
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);

        if (!world.isRemote) {
            ItemPipe pipe = NetworkManager.get(world).getPipe(pos);

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

        Predicate<Direction> hasAttachment = (dir) -> false;

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof ItemPipeTileEntity) {
            hasAttachment = ((ItemPipeTileEntity) tile)::hasAttachment;
        }

        if (hasAttachment.test(Direction.NORTH) || state.get(INV_NORTH)) {
            shape = VoxelShapes.or(shape, NORTH_ATTACHMENT_SHAPE);
        }

        if (hasAttachment.test(Direction.EAST) || state.get(INV_EAST)) {
            shape = VoxelShapes.or(shape, EAST_ATTACHMENT_SHAPE);
        }

        if (hasAttachment.test(Direction.SOUTH) || state.get(INV_SOUTH)) {
            shape = VoxelShapes.or(shape, SOUTH_ATTACHMENT_SHAPE);
        }

        if (hasAttachment.test(Direction.WEST) || state.get(INV_WEST)) {
            shape = VoxelShapes.or(shape, WEST_ATTACHMENT_SHAPE);
        }

        if (hasAttachment.test(Direction.UP) || state.get(INV_UP)) {
            shape = VoxelShapes.or(shape, UP_ATTACHMENT_SHAPE);
        }

        if (hasAttachment.test(Direction.DOWN) || state.get(INV_DOWN)) {
            shape = VoxelShapes.or(shape, DOWN_ATTACHMENT_SHAPE);
        }

        return shape;
    }

    private static boolean hasConnection(IWorld world, BlockPos pos, Direction direction) {
        TileEntity currentTile = world.getTileEntity(pos);
        if (currentTile instanceof ItemPipeTileEntity && ((ItemPipeTileEntity) currentTile).hasAttachment(direction)) {
            return false;
        }

        BlockState facingState = world.getBlockState(pos.offset(direction));
        TileEntity facingTile = world.getTileEntity(pos.offset(direction));

        if (facingTile instanceof ItemPipeTileEntity && ((ItemPipeTileEntity) facingTile).hasAttachment(direction.getOpposite())) {
            return false;
        }

        return facingState.getBlock() instanceof ItemPipeBlock;
    }

    private static boolean hasInvConnection(IWorld world, BlockPos pos, Direction direction) {
        TileEntity facingTile = world.getTileEntity(pos.offset(direction));

        return facingTile != null
            && facingTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()).isPresent();
    }

    private static BlockState getState(BlockState currentState, IWorld world, BlockPos pos) {
        return currentState
            .with(NORTH, hasConnection(world, pos, Direction.NORTH))
            .with(EAST, hasConnection(world, pos, Direction.EAST))
            .with(SOUTH, hasConnection(world, pos, Direction.SOUTH))
            .with(WEST, hasConnection(world, pos, Direction.WEST))
            .with(UP, hasConnection(world, pos, Direction.UP))
            .with(DOWN, hasConnection(world, pos, Direction.DOWN))
            .with(INV_NORTH, hasInvConnection(world, pos, Direction.NORTH))
            .with(INV_EAST, hasInvConnection(world, pos, Direction.EAST))
            .with(INV_SOUTH, hasInvConnection(world, pos, Direction.SOUTH))
            .with(INV_WEST, hasInvConnection(world, pos, Direction.WEST))
            .with(INV_UP, hasInvConnection(world, pos, Direction.UP))
            .with(INV_DOWN, hasInvConnection(world, pos, Direction.DOWN));
    }
}
