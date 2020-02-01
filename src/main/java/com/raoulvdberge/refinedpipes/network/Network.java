package com.raoulvdberge.refinedpipes.network;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.network.graph.NetworkGraph;
import com.raoulvdberge.refinedpipes.network.graph.NetworkGraphScannerResult;
import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.pipe.transport.ItemTransport;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentRegistry;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentType;
import com.raoulvdberge.refinedpipes.network.route.DijkstraAlgorithm;
import com.raoulvdberge.refinedpipes.network.route.Node;
import com.raoulvdberge.refinedpipes.render.Color;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class Network {
    private static final Logger LOGGER = LogManager.getLogger(Network.class);

    private final NetworkGraph graph = new NetworkGraph(this);
    private final String id;
    private final Color color;
    private final List<ItemTransport> transports = new ArrayList<>();
    private final List<ItemTransport> transportsToAdd = new ArrayList<>();

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
        this.graph.getPipes().forEach(p -> graph.add(LongNBT.valueOf(p.getPos().toLong())));
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

    public void addTransport(ItemTransport transport) {
        transportsToAdd.add(transport);
    }

    public void update(World world) {
        if (transports.isEmpty()) {
            AttachmentType extractor = AttachmentRegistry.INSTANCE.getType(new ResourceLocation(RefinedPipes.ID, "extractor"));
            AttachmentType insertor = AttachmentRegistry.INSTANCE.getType(new ResourceLocation(RefinedPipes.ID, "insertor"));

            // Debug
            Pipe from = this.graph.getPipes().stream()
                .filter(p -> p.getAttachmentManager().hasAttachment(extractor))
                .findFirst()
                .orElse(null);

            Pipe to = this.graph.getPipes().stream()
                .filter(p -> p.getAttachmentManager().hasAttachment(insertor))
                .findFirst()
                .orElse(null);

            if (from != null && to != null && graph.getRoutingGraph() != null) {
                DijkstraAlgorithm<BlockPos> dijkstra = new DijkstraAlgorithm<>(graph.getRoutingGraph());

                dijkstra.execute(graph.getRoutingGraph().getNode(from.getPos()));

                List<Node<BlockPos>> path = dijkstra.getPath(graph.getRoutingGraph().getNode(to.getPos()));

                if (path != null) {
                    Deque<Pipe> pipesToGo = new ArrayDeque<>();
                    path.forEach(p -> {
                        BlockPos pos = p.getId();
                        Pipe pipe = NetworkManager.get(world).getPipe(pos);
                        pipesToGo.add(pipe);
                    });

                    BlockPos fromPos = from.getPos().offset(from.getAttachmentManager().getAttachment(extractor).getDirection());
                    BlockPos toPos = to.getPos().offset(to.getAttachmentManager().getAttachment(insertor).getDirection());

                    ItemTransport t = new ItemTransport(
                        new ItemStack(Blocks.DIRT),
                        fromPos,
                        toPos,
                        pipesToGo
                    );

                    addTransport(t);
                }
            }
        }

        transports.addAll(transportsToAdd);
        transportsToAdd.clear();

        transports.removeIf(ItemTransport::update);
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
