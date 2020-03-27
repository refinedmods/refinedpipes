package com.raoulvdberge.refinedpipes.network.graph.scanner;

import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.pipe.item.ItemDestination;

import java.util.List;
import java.util.Set;

public class NetworkGraphScannerResult {
    private final Set<Pipe> foundPipes;
    private final Set<Pipe> newPipes;
    private final Set<Pipe> removedPipes;
    private final Set<ItemDestination> destinations;
    private final List<NetworkGraphScannerRequest> requests;

    public NetworkGraphScannerResult(Set<Pipe> foundPipes, Set<Pipe> newPipes, Set<Pipe> removedPipes, Set<ItemDestination> destinations, List<NetworkGraphScannerRequest> requests) {
        this.foundPipes = foundPipes;
        this.newPipes = newPipes;
        this.removedPipes = removedPipes;
        this.destinations = destinations;
        this.requests = requests;
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

    public Set<ItemDestination> getDestinations() {
        return destinations;
    }

    public List<NetworkGraphScannerRequest> getRequests() {
        return requests;
    }
}
