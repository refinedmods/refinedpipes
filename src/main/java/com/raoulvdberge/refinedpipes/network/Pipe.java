package com.raoulvdberge.refinedpipes.network;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

public class Pipe {
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

    public void joinNetwork(Network network) {
        this.network = network;
        System.out.println(pos + " joined network " + network.getId());
    }

    public void leaveNetwork() {
        System.out.println(pos + " left network " + network.getId());
        this.network = null;
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
