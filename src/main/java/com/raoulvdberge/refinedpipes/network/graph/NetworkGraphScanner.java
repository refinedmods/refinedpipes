package com.raoulvdberge.refinedpipes.network.graph;

import com.raoulvdberge.refinedpipes.network.NetworkManager;
import com.raoulvdberge.refinedpipes.network.Pipe;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class NetworkGraphScanner {
    private final Set<Pipe> foundPipes = new HashSet<>();
    private final Set<Pipe> newPipes = new HashSet<>();
    private final Set<Pipe> removedPipes = new HashSet<>();
    private final Set<Pipe> currentPipes;

    private final Queue<NetworkGraphScannerRequest> requests = new ArrayDeque<>();

    public NetworkGraphScanner(Set<Pipe> currentPipes) {
        this.currentPipes = currentPipes;
        this.removedPipes.addAll(currentPipes);
    }

    public void scanAt(World world, BlockPos pos) {
        Pipe pipe = NetworkManager.get(world).getPipe(pos);
        if (pipe != null) {
            if (foundPipes.add(pipe)) {
                if (!currentPipes.contains(pipe)) {
                    newPipes.add(pipe);
                }

                removedPipes.remove(pipe);

                for (Direction dir : Direction.values()) {
                    requests.add(new NetworkGraphScannerRequest(
                        world,
                        pos.offset(dir)
                    ));
                }
            }
        }
    }

    public Queue<NetworkGraphScannerRequest> getRequests() {
        return requests;
    }

    public Set<Pipe> getFoundPipes() {
        return foundPipes;
    }

    public Set<Pipe> getNewPipes() {
        return newPipes;
    }

    public Set<Pipe> getRemovedPipes() {
        return removedPipes;
    }
}
