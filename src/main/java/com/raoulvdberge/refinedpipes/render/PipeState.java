package com.raoulvdberge.refinedpipes.render;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class PipeState {
    @Nullable
    private final BlockState state;
    @Nullable
    private final ResourceLocation[] attachmentState;
    private final Direction side;
    private final Random rand;

    public PipeState(@Nullable BlockState state, @Nullable ResourceLocation[] attachmentState, Direction side, Random rand) {
        this.state = state;
        this.attachmentState = attachmentState;
        this.side = side;
        this.rand = rand;
    }

    @Nullable
    public BlockState getState() {
        return state;
    }

    @Nullable
    public ResourceLocation[] getAttachmentState() {
        return attachmentState;
    }

    @Nullable
    public ResourceLocation getAttachmentState(Direction direction) {
        if (attachmentState == null) {
            return null;
        }

        return attachmentState[direction.ordinal()];
    }

    public boolean hasAttachmentState(Direction direction) {
        return getAttachmentState(direction) != null;
    }

    public Direction getSide() {
        return side;
    }

    public Random getRand() {
        return rand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PipeState pipeState = (PipeState) o;
        return Objects.equals(state, pipeState.state) &&
            Arrays.equals(attachmentState, pipeState.attachmentState) &&
            side == pipeState.side;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(state, side);
        result = 31 * result + Arrays.hashCode(attachmentState);
        return result;
    }
}
