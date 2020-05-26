package com.refinedmods.refinedpipes.routing;

import java.util.*;

public class DijkstraAlgorithm<T> {
    private final List<Edge<T>> edges;
    private Set<Node<T>> settledNodes;
    private Set<Node<T>> unSettledNodes;
    private Map<Node<T>, Node<T>> predecessors;
    private Map<Node<T>, Integer> distance;

    public DijkstraAlgorithm(Graph<T> graph) {
        this.edges = new ArrayList<>(graph.getEdges());
    }

    public void execute(Node<T> source) {
        this.settledNodes = new HashSet<>();
        this.unSettledNodes = new HashSet<>();
        this.distance = new HashMap<>();
        this.predecessors = new HashMap<>();
        this.distance.put(source, 0);
        this.unSettledNodes.add(source);

        while (unSettledNodes.size() > 0) {
            Node<T> node = getMinimum(unSettledNodes);
            settledNodes.add(node);
            unSettledNodes.remove(node);
            findMinimalDistances(node);
        }
    }

    private void findMinimalDistances(Node<T> node) {
        List<Node<T>> adjacentNodes = getNeighbors(node);

        for (Node<T> target : adjacentNodes) {
            if (getShortestDistance(target) > getShortestDistance(node) + getDistance(node, target)) {
                distance.put(target, getShortestDistance(node) + getDistance(node, target));
                predecessors.put(target, node);
                unSettledNodes.add(target);
            }
        }
    }

    private int getDistance(Node<T> node, Node<T> target) {
        for (Edge<T> edge : edges) {
            if (edge.getSource().equals(node) && edge.getDestination().equals(target)) {
                return edge.getWeight();
            }
        }

        throw new RuntimeException("Should not happen");
    }

    private List<Node<T>> getNeighbors(Node<T> node) {
        List<Node<T>> neighbors = new ArrayList<>();

        for (Edge<T> edge : edges) {
            if (edge.getSource().equals(node) && !isSettled(edge.getDestination())) {
                neighbors.add(edge.getDestination());
            }
        }

        return neighbors;
    }

    private Node<T> getMinimum(Set<Node<T>> nodes) {
        Node<T> minimum = null;

        for (Node<T> node : nodes) {
            if (minimum == null) {
                minimum = node;
            } else {
                if (getShortestDistance(node) < getShortestDistance(minimum)) {
                    minimum = node;
                }
            }
        }

        return minimum;
    }

    private boolean isSettled(Node<T> node) {
        return settledNodes.contains(node);
    }

    private int getShortestDistance(Node<T> destination) {
        Integer d = distance.get(destination);

        if (d == null) {
            return Integer.MAX_VALUE;
        } else {
            return d;
        }
    }

    public LinkedList<Node<T>> getPath(Node<T> target) {
        LinkedList<Node<T>> path = new LinkedList<>();

        Node<T> step = target;

        if (predecessors.get(step) == null) {
            return null;
        }

        path.add(step);

        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            path.add(step);
        }

        Collections.reverse(path);

        return path;
    }
}
