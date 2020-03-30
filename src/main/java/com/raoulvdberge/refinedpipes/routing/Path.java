package com.raoulvdberge.refinedpipes.routing;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class Path<T> {
    private final List<Node<T>> path;

    public Path(List<Node<T>> path) {
        this.path = path;
    }

    public Node<T> at(int i) {
        return path.get(i);
    }

    public int length() {
        return path.size();
    }

    public Deque<T> toQueue() {
        Deque<T> path = new ArrayDeque<>();

        for (int i = length() - 1; i >= 0; --i) {
            path.push(at(i).getId());
        }

        return path;
    }
}
