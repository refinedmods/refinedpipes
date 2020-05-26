package com.refinedmods.refinedpipes.routing;

public class Edge<T> {
    private final String id;
    private final Node<T> source;
    private final Node<T> destination;
    private final int weight;

    public Edge(String id, Node<T> source, Node<T> destination, int weight) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    public String getId() {
        return id;
    }

    public Node<T> getDestination() {
        return destination;
    }

    public Node<T> getSource() {
        return source;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return source + " " + destination;
    }
}