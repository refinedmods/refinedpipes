package com.raoulvdberge.refinedpipes.block;

import com.raoulvdberge.refinedpipes.item.AttachmentItem;
import com.raoulvdberge.refinedpipes.tile.PipeTileEntity;
import com.raoulvdberge.refinedpipes.util.Raytracer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PipeShapeCache {
    private static final boolean[] NO_ATTACHMENT_STATE = new boolean[Direction.values().length];

    private final PipeShapeFactory shapeFactory;
    private final List<AxisAlignedBB> attachmentShapes = new ArrayList<>();
    private final Map<PipeShapeCacheEntry, VoxelShape> cache = new HashMap<>();

    public PipeShapeCache(PipeShapeFactory shapeFactory) {
        this.shapeFactory = shapeFactory;

        attachmentShapes.add(PipeShapeProps.NORTH_ATTACHMENT_SHAPE.getBoundingBox());
        attachmentShapes.add(PipeShapeProps.EAST_ATTACHMENT_SHAPE.getBoundingBox());
        attachmentShapes.add(PipeShapeProps.SOUTH_ATTACHMENT_SHAPE.getBoundingBox());
        attachmentShapes.add(PipeShapeProps.WEST_ATTACHMENT_SHAPE.getBoundingBox());
        attachmentShapes.add(PipeShapeProps.UP_ATTACHMENT_SHAPE.getBoundingBox());
        attachmentShapes.add(PipeShapeProps.DOWN_ATTACHMENT_SHAPE.getBoundingBox());
    }

    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
        VoxelShape shape = createShapeIfNeeded(state, world, pos);

        if (ctx.getEntity() instanceof PlayerEntity &&
            ((PlayerEntity) ctx.getEntity()).getHeldItemMainhand().getItem() instanceof AttachmentItem) {
            shape = addFakeAttachmentShape(pos, ctx.getEntity(), shape);
        }

        return shape;
    }

    private VoxelShape addFakeAttachmentShape(BlockPos pos, Entity entity, VoxelShape shape) {
        Pair<Vec3d, Vec3d> vec = Raytracer.getVectors(entity);

        Raytracer.AdvancedRayTraceResult<BlockRayTraceResult> result = Raytracer.collisionRayTrace(pos, vec.getLeft(), vec.getRight(), attachmentShapes);
        if (result != null) {
            shape = VoxelShapes.or(shape, VoxelShapes.create(result.bounds));
        }

        return shape;
    }

    private VoxelShape createShapeIfNeeded(BlockState state, IBlockReader world, BlockPos pos) {
        boolean[] attachmentState;

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof PipeTileEntity) {
            attachmentState = ((PipeTileEntity) tile).getAttachmentManager().getState();
        } else {
            attachmentState = NO_ATTACHMENT_STATE;
        }

        PipeShapeCacheEntry entry = new PipeShapeCacheEntry(state, attachmentState);

        return cache.computeIfAbsent(entry, e -> shapeFactory.createShape(e.getState(), e.getAttachmentState()));
    }
}
