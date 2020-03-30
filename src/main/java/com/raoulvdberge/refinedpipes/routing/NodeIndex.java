package com.raoulvdberge.refinedpipes.routing;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeIndex<T> {
    private final Map<T, Node<T>> index = new HashMap<>();

    private NodeIndex() {
    }

    public static <T> NodeIndex<T> of(List<Node<T>> nodes) {
        NodeIndex<T> nodeIndex = new NodeIndex<>();

        for (Node<T> node : nodes) {
            nodeIndex.index.put(node.getId(), node);
        }

        return nodeIndex;
    }

    @Nullable
    public Node<T> getNode(T id) {
        return index.get(id);
    }
}
