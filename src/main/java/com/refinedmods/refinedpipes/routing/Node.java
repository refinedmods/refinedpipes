package com.refinedmods.refinedpipes.routing;

import java.util.Objects;

public class Node<T> {
    private final T id;

    public Node(T id) {
        this.id = id;
    }

    public T getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node<?> node = (Node<?>) o;
        return id.equals(node.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id.toString();
    }
}