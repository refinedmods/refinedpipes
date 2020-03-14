package com.raoulvdberge.refinedpipes.network.route;

import java.util.List;

public class Path<T> {
    private List<Node<T>> path;

    public Path(List<Node<T>> path) {
        this.path = path;
    }

    public Node<T> at(int i) {
        return path.get(i);
    }

    public int length() {
        return path.size();
    }
}
