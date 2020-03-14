package com.raoulvdberge.refinedpipes.network.graph;

import com.raoulvdberge.refinedpipes.network.pipe.Destination;
import com.raoulvdberge.refinedpipes.network.route.Path;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class DestinationPathCache<T> {
    private Map<BlockPos, Map<Destination<T>, Path<BlockPos>>> paths = new HashMap<>();

    public void addPath(BlockPos source, Destination<T> destination, Path<BlockPos> path) {
        paths.computeIfAbsent(source, s -> new HashMap<>()).put(destination, path);
    }

    @Nullable
    public Path<BlockPos> getPath(BlockPos source, Destination<T> destination) {
        Map<Destination<T>, Path<BlockPos>> pathsFromSource = paths.get(source);
        if (pathsFromSource == null) {
            return null;
        }

        return pathsFromSource.get(destination);
    }

    @Nullable
    public Destination<T> findNearestDestination(BlockPos source, Predicate<Destination<T>> filter) {
        Map<Destination<T>, Path<BlockPos>> pathsFromSource = paths.get(source);
        if (pathsFromSource == null) {
            return null;
        }

        Destination<T> foundDestination = null;
        int shortestDistance = -1;

        for (Map.Entry<Destination<T>, Path<BlockPos>> destinationAndPath : pathsFromSource.entrySet()) {
            Destination<T> destination = destinationAndPath.getKey();
            if (!filter.test(destination)) {
                continue;
            }

            Path<BlockPos> path = destinationAndPath.getValue();
            int distance = path.length();

            if ((shortestDistance == -1 || distance < shortestDistance)) {
                shortestDistance = distance;
                foundDestination = destination;
            }
        }

        return foundDestination;
    }
}
