package com.raoulvdberge.refinedpipes.network;

import com.raoulvdberge.refinedpipes.network.graph.NetworkGraph;
import com.raoulvdberge.refinedpipes.network.graph.NetworkGraphScannerResult;
import com.raoulvdberge.refinedpipes.render.Color;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.Random;

public class Network {
    private static final Logger LOGGER = LogManager.getLogger(Network.class);

    private final NetworkGraph graph = new NetworkGraph(this);
    private final String id;
    private final Color color;

    public Network() {
        Random r = new Random();

        this.id = generateRandomString(r, 8);
        this.color = new Color(
            r.nextInt(255) + 1,
            r.nextInt(255) + 1,
            r.nextInt(255) + 1
        );
    }

    public Network(String id, Color color) {
        this.id = id;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public Color getColor() {
        return color;
    }

    public NetworkGraphScannerResult scanGraph(World originWorld, BlockPos originPos) {
        return graph.scan(originWorld, originPos);
    }

    public CompoundNBT writeToNbt(CompoundNBT tag) {
        tag.putString("id", id);
        color.writeToTag(tag);

        ListNBT graph = new ListNBT();
        this.graph.getPipes().forEach(p -> graph.add(new LongNBT(p.getPos().toLong())));
        tag.put("graph", graph);

        return tag;
    }

    public static Network fromNbt(NetworkManager manager, CompoundNBT tag) {
        Network network = new Network(tag.getString("id"), Color.fromNbt(tag));

        ListNBT graph = tag.getList("graph", Constants.NBT.TAG_LONG);
        for (INBT item : graph) {
            BlockPos pos = BlockPos.fromLong(((LongNBT) item).getLong());

            Pipe pipe = manager.getPipe(pos);
            if (pipe == null) {
                throw new RuntimeException("Pipe at " + pos + " not found");
            }

            pipe.setNetwork(network);

            network.graph.getPipes().add(pipe);
        }

        LOGGER.debug("Deserialized network " + network.id + " with " + graph.size() + " pipes");

        return network;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Network network = (Network) o;
        return id.equals(network.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    private static String generateRandomString(Random r, int length) {
        StringBuilder word = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int tmp = 'a' + r.nextInt('z' - 'a');
            word.append((char) tmp);
        }
        return word.toString();
    }
}
