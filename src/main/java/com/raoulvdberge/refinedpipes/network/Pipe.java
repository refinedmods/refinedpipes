package com.raoulvdberge.refinedpipes.network;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class Pipe {
    private static final Logger LOGGER = LogManager.getLogger(Pipe.class);

    private final World world;
    private final BlockPos pos;
    private Network network;

    public Pipe(World world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
    }

    public World getWorld() {
        return world;
    }

    public BlockPos getPos() {
        return pos;
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void joinNetwork(Network network) {
        this.network = network;

        LOGGER.debug(pos + " joined network " + network.getId());

        updateBlock();
    }

    public void leaveNetwork() {
        LOGGER.debug(pos + " left network " + network.getId());

        this.network = null;

        updateBlock();
    }

    private void updateBlock() {
        BlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 1 | 2);
    }

    public CompoundNBT writeToNbt(CompoundNBT tag) {
        tag.putLong("pos", pos.toLong());

        return tag;
    }

    public static Pipe fromNbt(World world, CompoundNBT tag) {
        BlockPos pos = BlockPos.fromLong(tag.getLong("pos"));

        return new Pipe(world, pos);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pipe pipe = (Pipe) o;
        return world.equals(pipe.world) &&
            pos.equals(pipe.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, pos);
    }
}
