package com.raoulvdberge.refinedpipes.network.route;

import java.util.List;

public class Graph<T> {
    private final List<Node<T>> nodes;
    private final List<Edge<T>> edges;

    public Graph(List<Node<T>> nodes, List<Edge<T>> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public List<Node<T>> getNodes() {
        return nodes;
    }

    public List<Edge<T>> getEdges() {
        return edges;
    }
}
