package com.raoulvdberge.refinedpipes.network.graph;

import com.raoulvdberge.refinedpipes.network.pipe.Destination;
import com.raoulvdberge.refinedpipes.network.route.Path;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class DestinationPathCache {
    private Map<BlockPos, Map<Destination, Path<BlockPos>>> paths = new HashMap<>();

    public void addPath(BlockPos source, Destination destination, Path<BlockPos> path) {
        paths.computeIfAbsent(source, s -> new HashMap<>()).put(destination, path);
    }

    @Nullable
    public Path<BlockPos> getPath(BlockPos source, Destination destination) {
        Map<Destination, Path<BlockPos>> pathsFromSource = paths.get(source);
        if (pathsFromSource == null) {
            return null;
        }

        return pathsFromSource.get(destination);
    }

    @Nullable
    public Destination findNearestDestination(BlockPos source, Predicate<Destination> filter) {
        // TODO: don't connect to pipe that has attachment.
        Map<Destination, Path<BlockPos>> pathsFromSource = paths.get(source);
        if (pathsFromSource == null) {
            return null;
        }

        Destination foundDestination = null;
        int shortestDistance = -1;

        for (Map.Entry<Destination, Path<BlockPos>> destinationAndPath : pathsFromSource.entrySet()) {
            Destination destination = destinationAndPath.getKey();
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
