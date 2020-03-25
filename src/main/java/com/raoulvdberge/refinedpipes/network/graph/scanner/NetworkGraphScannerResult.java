package com.raoulvdberge.refinedpipes.network.graph.scanner;

import com.raoulvdberge.refinedpipes.network.pipe.Destination;
import com.raoulvdberge.refinedpipes.network.pipe.ItemPipe;

import java.util.List;
import java.util.Set;

public class NetworkGraphScannerResult {
    private final Set<ItemPipe> foundPipes;
    private final Set<ItemPipe> newPipes;
    private final Set<ItemPipe> removedPipes;
    private final Set<Destination> destinations;
    private final List<NetworkGraphScannerRequest> requests;

    public NetworkGraphScannerResult(Set<ItemPipe> foundPipes, Set<ItemPipe> newPipes, Set<ItemPipe> removedPipes, Set<Destination> destinations, List<NetworkGraphScannerRequest> requests) {
        this.foundPipes = foundPipes;
        this.newPipes = newPipes;
        this.removedPipes = removedPipes;
        this.destinations = destinations;
        this.requests = requests;
    }

    public Set<ItemPipe> getFoundPipes() {
        return foundPipes;
    }

    public Set<ItemPipe> getNewPipes() {
        return newPipes;
    }

    public Set<ItemPipe> getRemovedPipes() {
        return removedPipes;
    }

    public Set<Destination> getDestinations() {
        return destinations;
    }

    public List<NetworkGraphScannerRequest> getRequests() {
        return requests;
    }
}
