package com.refinedmods.refinedpipes.network.pipe.shape;

import com.refinedmods.refinedpipes.item.AttachmentItem;
import com.refinedmods.refinedpipes.network.pipe.attachment.AttachmentFactory;
import com.refinedmods.refinedpipes.tile.PipeTileEntity;
import com.refinedmods.refinedpipes.util.Raytracer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PipeShapeCache {
    private static final ResourceLocation[] NO_ATTACHMENT_STATE = new ResourceLocation[Direction.values().length];

    private final PipeShapeFactory shapeFactory;
    private final List<AABB> attachmentShapes = new ArrayList<>();
    private final Map<PipeShapeCacheEntry, VoxelShape> cache = new HashMap<>();

    public PipeShapeCache(PipeShapeFactory shapeFactory) {
        this.shapeFactory = shapeFactory;

        attachmentShapes.add(PipeShapeProps.NORTH_ATTACHMENT_SHAPE.bounds());
        attachmentShapes.add(PipeShapeProps.EAST_ATTACHMENT_SHAPE.bounds());
        attachmentShapes.add(PipeShapeProps.SOUTH_ATTACHMENT_SHAPE.bounds());
        attachmentShapes.add(PipeShapeProps.WEST_ATTACHMENT_SHAPE.bounds());
        attachmentShapes.add(PipeShapeProps.UP_ATTACHMENT_SHAPE.bounds());
        attachmentShapes.add(PipeShapeProps.DOWN_ATTACHMENT_SHAPE.bounds());
    }

    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        VoxelShape shape = createShapeIfNeeded(state, world, pos);

        if (ctx instanceof EntityCollisionContext entityCollisionContext && entityCollisionContext.getEntity() instanceof Player player) {
            Item inHand = player.getMainHandItem().getItem();

            if (inHand instanceof AttachmentItem) {
                shape = addFakeAttachmentShape(state.getBlock(), pos, player, shape, ((AttachmentItem) inHand).getFactory());
            }
        }

        return shape;
    }

    private VoxelShape addFakeAttachmentShape(Block block, BlockPos pos, Entity entity, VoxelShape shape, AttachmentFactory type) {
        if (!type.canPlaceOnPipe(block)) {
            return shape;
        }

        Pair<Vec3, Vec3> vec = Raytracer.getVectors(entity);

        Raytracer.AdvancedRayTraceResult<BlockHitResult> result = Raytracer.collisionRayTrace(pos, vec.getLeft(), vec.getRight(), attachmentShapes);
        if (result != null) {
            shape = Shapes.or(shape, Shapes.create(result.bounds));
        }

        return shape;
    }

    private VoxelShape createShapeIfNeeded(BlockState state, BlockGetter world, BlockPos pos) {
        ResourceLocation[] attachmentState;

        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof PipeTileEntity) {
            attachmentState = ((PipeTileEntity) tile).getAttachmentManager().getState();
        } else {
            attachmentState = NO_ATTACHMENT_STATE;
        }

        PipeShapeCacheEntry entry = new PipeShapeCacheEntry(state, attachmentState);

        return cache.computeIfAbsent(entry, e -> shapeFactory.createShape(e.getState(), e.getAttachmentState()));
    }
}
