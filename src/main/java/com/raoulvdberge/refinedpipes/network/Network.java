package com.raoulvdberge.refinedpipes.network;

import com.raoulvdberge.refinedpipes.network.graph.NetworkGraph;
import com.raoulvdberge.refinedpipes.network.graph.NetworkGraphScanner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class Network {
    private final NetworkGraph graph = new NetworkGraph(this);
    private final String id;

    private static String generateRandomWord(int wordLength) {
        Random r = new Random(); // Intialize a Random Number Generator with SysTime as the seed
        StringBuilder sb = new StringBuilder(wordLength);
        for (int i = 0; i < wordLength; i++) { // For each letter in the word
            int tmp = 'a' + r.nextInt('z' - 'a'); // Generate a letter between a and z
            sb.append((char) tmp); // Add it to the String
        }
        return sb.toString();
    }

    public Network() {
        this.id = generateRandomWord(4);
    }

    public String getId() {
        return id;
    }

    public NetworkGraphScanner scanGraph(World originWorld, BlockPos originPos) {
        return graph.scan(originWorld, originPos);
    }
}
