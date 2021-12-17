package com.refinedmods.refinedpipes.block;

import com.refinedmods.refinedpipes.item.AttachmentItem;
import com.refinedmods.refinedpipes.network.NetworkManager;
import com.refinedmods.refinedpipes.network.pipe.Pipe;
import com.refinedmods.refinedpipes.network.pipe.attachment.Attachment;
import com.refinedmods.refinedpipes.network.pipe.attachment.AttachmentFactory;
import com.refinedmods.refinedpipes.network.pipe.attachment.AttachmentManager;
import com.refinedmods.refinedpipes.network.pipe.shape.PipeShapeCache;
import com.refinedmods.refinedpipes.network.pipe.shape.PipeShapeProps;
import com.refinedmods.refinedpipes.tile.PipeTileEntity;
import com.refinedmods.refinedpipes.util.Raytracer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
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
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class PipeBlock extends Block {
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

    private final PipeShapeCache shapeCache;

    public PipeBlock(PipeShapeCache shapeCache) {
        super(Block.Properties.of(Material.STONE).strength(0.35F));

        this.shapeCache = shapeCache;

        this.registerDefaultState(defaultBlockState()
            .setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(UP, false).setValue(DOWN, false)
            .setValue(INV_NORTH, false).setValue(INV_EAST, false).setValue(INV_SOUTH, false).setValue(INV_WEST, false).setValue(INV_UP, false).setValue(INV_DOWN, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);

        builder.add(
            NORTH, EAST, SOUTH, WEST, UP, DOWN,
            INV_NORTH, INV_EAST, INV_SOUTH, INV_WEST, INV_UP, INV_DOWN
        );
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);

        if (!world.isClientSide) {
            Pipe pipe = NetworkManager.get(world).getPipe(pos);

            if (pipe != null && pipe.getNetwork() != null) {
                pipe.getNetwork().scanGraph(world, pos);
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        Direction dirClicked = getAttachmentDirectionClicked(pos, hit.getLocation());

        if (dirClicked != null) {
            ItemStack held = player.getMainHandItem();
            TileEntity tile = world.getBlockEntity(pos);

            if (held.isEmpty() && player.isCrouching()) {
                return removeAttachment(world, pos, dirClicked);
            } else if (tile instanceof PipeTileEntity && ((PipeTileEntity) tile).getAttachmentManager().hasAttachment(dirClicked)) {
                return openAttachmentContainer(player, pos, ((PipeTileEntity) tile).getAttachmentManager(), dirClicked);
            } else if (held.getItem() instanceof AttachmentItem) {
                return addAttachment(player, world, pos, held, dirClicked);
            }
        }

        return super.use(state, world, pos, player, hand, hit);
    }

    private ActionResultType addAttachment(PlayerEntity player, World world, BlockPos pos, ItemStack attachment, Direction dir) {
        if (!world.isClientSide) {
            Pipe pipe = NetworkManager.get(world).getPipe(pos);

            if (pipe != null && !pipe.getAttachmentManager().hasAttachment(dir)) {
                AttachmentFactory type = ((AttachmentItem) attachment.getItem()).getFactory();
                if (!type.canPlaceOnPipe(this)) {
                    return ActionResultType.SUCCESS;
                }

                pipe.getAttachmentManager().setAttachmentAndScanGraph(dir, type.create(pipe, dir));
                NetworkManager.get(world).setDirty();

                pipe.sendBlockUpdate();
                world.setBlockAndUpdate(pos, getState(world.getBlockState(pos), world, pos));

                if (!player.isCreative()) {
                    attachment.shrink(1);
                }
            }
        }

        return ActionResultType.SUCCESS;
    }

    private ActionResultType removeAttachment(World world, BlockPos pos, Direction dir) {
        if (!world.isClientSide) {
            Pipe pipe = NetworkManager.get(world).getPipe(pos);

            if (pipe != null && pipe.getAttachmentManager().hasAttachment(dir)) {
                Attachment attachment = pipe.getAttachmentManager().getAttachment(dir);

                pipe.getAttachmentManager().removeAttachmentAndScanGraph(dir);
                NetworkManager.get(world).setDirty();

                pipe.sendBlockUpdate();
                world.setBlockAndUpdate(pos, getState(world.getBlockState(pos), world, pos));

                Block.popResource(world, pos.relative(dir), attachment.getDrop());
            }

            return ActionResultType.SUCCESS;
        } else {
            return ((PipeTileEntity) world.getBlockEntity(pos)).getAttachmentManager().hasAttachment(dir) ? ActionResultType.SUCCESS : ActionResultType.FAIL;
        }
    }

    private ActionResultType openAttachmentContainer(PlayerEntity player, BlockPos pos, AttachmentManager attachmentManager, Direction dir) {
        if (player instanceof ServerPlayerEntity) {
            attachmentManager.openAttachmentContainer(dir, (ServerPlayerEntity) player);
        }

        return ActionResultType.SUCCESS;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext ctx) {
        return getState(defaultBlockState(), ctx.getLevel(), ctx.getClickedPos());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction dir, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos) {
        return getState(state, world, pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
        return shapeCache.getShape(state, world, pos, ctx);
    }

    private BlockState getState(BlockState currentState, IWorld world, BlockPos pos) {
        return currentState
            .setValue(NORTH, hasConnection(world, pos, Direction.NORTH))
            .setValue(EAST, hasConnection(world, pos, Direction.EAST))
            .setValue(SOUTH, hasConnection(world, pos, Direction.SOUTH))
            .setValue(WEST, hasConnection(world, pos, Direction.WEST))
            .setValue(UP, hasConnection(world, pos, Direction.UP))
            .setValue(DOWN, hasConnection(world, pos, Direction.DOWN))
            .setValue(INV_NORTH, hasInvConnection(world, pos, Direction.NORTH))
            .setValue(INV_EAST, hasInvConnection(world, pos, Direction.EAST))
            .setValue(INV_SOUTH, hasInvConnection(world, pos, Direction.SOUTH))
            .setValue(INV_WEST, hasInvConnection(world, pos, Direction.WEST))
            .setValue(INV_UP, hasInvConnection(world, pos, Direction.UP))
            .setValue(INV_DOWN, hasInvConnection(world, pos, Direction.DOWN));
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        Direction dirClicked = getAttachmentDirectionClicked(pos, target.getLocation());

        if (dirClicked != null) {
            TileEntity tile = world.getBlockEntity(pos);
            if (tile instanceof PipeTileEntity) {
                return ((PipeTileEntity) tile).getAttachmentManager().getPickBlock(dirClicked);
            }
        }

        return super.getPickBlock(state, target, world, pos, player);
    }

    @Nullable
    public Direction getAttachmentDirectionClicked(BlockPos pos, Vector3d hit) {
        if (Raytracer.inclusiveContains(PipeShapeProps.NORTH_ATTACHMENT_SHAPE.bounds().move(pos), hit)) {
            return Direction.NORTH;
        }

        if (Raytracer.inclusiveContains(PipeShapeProps.EAST_ATTACHMENT_SHAPE.bounds().move(pos), hit)) {
            return Direction.EAST;
        }

        if (Raytracer.inclusiveContains(PipeShapeProps.SOUTH_ATTACHMENT_SHAPE.bounds().move(pos), hit)) {
            return Direction.SOUTH;
        }

        if (Raytracer.inclusiveContains(PipeShapeProps.WEST_ATTACHMENT_SHAPE.bounds().move(pos), hit)) {
            return Direction.WEST;
        }

        if (Raytracer.inclusiveContains(PipeShapeProps.UP_ATTACHMENT_SHAPE.bounds().move(pos), hit)) {
            return Direction.UP;
        }

        if (Raytracer.inclusiveContains(PipeShapeProps.DOWN_ATTACHMENT_SHAPE.bounds().move(pos), hit)) {
            return Direction.DOWN;
        }

        return null;
    }

    protected abstract boolean hasConnection(IWorld world, BlockPos pos, Direction direction);

    protected abstract boolean hasInvConnection(IWorld world, BlockPos pos, Direction direction);
}
