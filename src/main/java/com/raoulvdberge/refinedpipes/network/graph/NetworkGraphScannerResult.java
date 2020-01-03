package com.raoulvdberge.refinedpipes.network.graph;

import com.raoulvdberge.refinedpipes.network.pipe.Pipe;

import java.util.Set;

public class NetworkGraphScannerResult {
    private final Set<Pipe> foundPipes;
    private final Set<Pipe> newPipes;
    private final Set<Pipe> removedPipes;

    public NetworkGraphScannerResult(Set<Pipe> foundPipes, Set<Pipe> newPipes, Set<Pipe> removedPipes) {
        this.foundPipes = foundPipes;
        this.newPipes = newPipes;
        this.removedPipes = removedPipes;
    }

    public Set<Pipe> getFoundPipes() {
        return foundPipes;
    }

    public Set<Pipe> getNewPipes() {
        return newPipes;
    }

    public Set<Pipe> getRemovedPipes() {
        return removedPipes;
    }
}
