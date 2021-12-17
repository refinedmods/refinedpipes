package com.refinedmods.refinedpipes.network.pipe.shape;

import com.refinedmods.refinedpipes.block.PipeBlock;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PipeShapeFactory {
    public VoxelShape createShape(BlockState state, ResourceLocation[] attachmentState) {
        VoxelShape shape = PipeShapeProps.CORE_SHAPE;

        if (state.getValue(PipeBlock.NORTH)) {
            shape = Shapes.or(shape, PipeShapeProps.NORTH_EXTENSION_SHAPE);
        }

        if (state.getValue(PipeBlock.EAST)) {
            shape = Shapes.or(shape, PipeShapeProps.EAST_EXTENSION_SHAPE);
        }

        if (state.getValue(PipeBlock.SOUTH)) {
            shape = Shapes.or(shape, PipeShapeProps.SOUTH_EXTENSION_SHAPE);
        }

        if (state.getValue(PipeBlock.WEST)) {
            shape = Shapes.or(shape, PipeShapeProps.WEST_EXTENSION_SHAPE);
        }

        if (state.getValue(PipeBlock.UP)) {
            shape = Shapes.or(shape, PipeShapeProps.UP_EXTENSION_SHAPE);
        }

        if (state.getValue(PipeBlock.DOWN)) {
            shape = Shapes.or(shape, PipeShapeProps.DOWN_EXTENSION_SHAPE);
        }

        if (attachmentState[Direction.NORTH.ordinal()] != null || state.getValue(PipeBlock.INV_NORTH)) {
            shape = Shapes.or(shape, PipeShapeProps.NORTH_ATTACHMENT_SHAPE);
        }

        if (attachmentState[Direction.EAST.ordinal()] != null || state.getValue(PipeBlock.INV_EAST)) {
            shape = Shapes.or(shape, PipeShapeProps.EAST_ATTACHMENT_SHAPE);
        }

        if (attachmentState[Direction.SOUTH.ordinal()] != null || state.getValue(PipeBlock.INV_SOUTH)) {
            shape = Shapes.or(shape, PipeShapeProps.SOUTH_ATTACHMENT_SHAPE);
        }

        if (attachmentState[Direction.WEST.ordinal()] != null || state.getValue(PipeBlock.INV_WEST)) {
            shape = Shapes.or(shape, PipeShapeProps.WEST_ATTACHMENT_SHAPE);
        }

        if (attachmentState[Direction.UP.ordinal()] != null || state.getValue(PipeBlock.INV_UP)) {
            shape = Shapes.or(shape, PipeShapeProps.UP_ATTACHMENT_SHAPE);
        }

        if (attachmentState[Direction.DOWN.ordinal()] != null || state.getValue(PipeBlock.INV_DOWN)) {
            shape = Shapes.or(shape, PipeShapeProps.DOWN_ATTACHMENT_SHAPE);
        }

        return shape;
    }
}
