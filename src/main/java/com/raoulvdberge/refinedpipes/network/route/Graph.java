package com.raoulvdberge.refinedpipes.network.route;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph<T> {
    private final List<Node<T>> nodes;
    private final List<Edge<T>> edges;
    private final Map<T, Node<T>> nodeIndex = new HashMap<>();

    public Graph(List<Node<T>> nodes, List<Edge<T>> edges) {
        this.nodes = nodes;
        this.edges = edges;

        for (Node<T> node : nodes) {
            nodeIndex.put(node.getId(), node);
        }
    }

    @Nullable
    public Node<T> getNode(T value) {
        return nodeIndex.get(value);
    }

    public List<Node<T>> getNodes() {
        return nodes;
    }

    public List<Edge<T>> getEdges() {
        return edges;
    }
}
