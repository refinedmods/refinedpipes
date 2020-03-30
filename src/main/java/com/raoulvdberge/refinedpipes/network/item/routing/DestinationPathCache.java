package com.raoulvdberge.refinedpipes.network.item.routing;

import com.raoulvdberge.refinedpipes.network.pipe.item.ItemDestination;
import com.raoulvdberge.refinedpipes.routing.Path;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class DestinationPathCache {
    private Map<BlockPos, Map<ItemDestination, Path<BlockPos>>> paths = new HashMap<>();

    public void addPath(BlockPos source, ItemDestination destination, Path<BlockPos> path) {
        paths.computeIfAbsent(source, s -> new HashMap<>()).put(destination, path);
    }

    @Nullable
    public Path<BlockPos> getPath(BlockPos source, ItemDestination destination) {
        Map<ItemDestination, Path<BlockPos>> pathsFromSource = paths.get(source);
        if (pathsFromSource == null) {
            return null;
        }

        return pathsFromSource.get(destination);
    }

    @Nullable
    public ItemDestination findNearestDestination(BlockPos source, Predicate<ItemDestination> filter) {
        Map<ItemDestination, Path<BlockPos>> pathsFromSource = paths.get(source);
        if (pathsFromSource == null) {
            return null;
        }

        ItemDestination foundDestination = null;
        int shortestDistance = -1;

        for (Map.Entry<ItemDestination, Path<BlockPos>> destinationAndPath : pathsFromSource.entrySet()) {
            ItemDestination destination = destinationAndPath.getKey();
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
