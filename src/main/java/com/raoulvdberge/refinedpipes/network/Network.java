package com.raoulvdberge.refinedpipes.network;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.network.graph.NetworkGraph;
import com.raoulvdberge.refinedpipes.network.graph.NetworkGraphScannerResult;
import com.raoulvdberge.refinedpipes.network.pipe.Destination;
import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentRegistry;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentType;
import com.raoulvdberge.refinedpipes.network.pipe.transport.ItemTransport;
import com.raoulvdberge.refinedpipes.network.route.Node;
import com.raoulvdberge.refinedpipes.render.Color;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandler;
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
    private int ticksElapsed;

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
        ticksElapsed++;

        if (ticksElapsed % 5 == 0 && transports.isEmpty()) {
            AttachmentType extractor = AttachmentRegistry.INSTANCE.getType(new ResourceLocation(RefinedPipes.ID, "extractor"));

            // Debug
            Pipe from = this.graph.getPipes().stream()
                .filter(p -> p.getAttachmentManager().hasAttachment(extractor))
                .findFirst()
                .orElse(null);

            if (from != null) {

                // Step 1: get all paths
                Map<BlockPos, Map<Destination<IItemHandler>, List<Node<BlockPos>>>> paths = graph.getNodeToDestinationPaths();

                if (paths == null) {
                    return;
                }

                // Step 2: get the paths for our source
                Map<Destination<IItemHandler>, List<Node<BlockPos>>> pathsFromSource = paths.get(from.getPos());

                // Step 3: find the shortest distance
                Destination<IItemHandler> destination = null;
                List<Node<BlockPos>> path = null;
                int shortestDistance = -1;

                for (Map.Entry<Destination<IItemHandler>, List<Node<BlockPos>>> dest : pathsFromSource.entrySet()) {
                    int distance = dest.getValue().size();

                    if ((shortestDistance == -1 || distance < shortestDistance)) {
                        shortestDistance = distance;
                        destination = dest.getKey();
                        path = dest.getValue();
                    }
                }

                if (destination == null || path == null) {
                    LOGGER.error("Destination pipe or path is null?");
                    return;
                }

                // Step 4: transport the path to pipes to go queue
                Deque<Pipe> pipesToGo = new ArrayDeque<>();

                for (int i = path.size() - 1; i >= 0; --i) {
                    Node<BlockPos> pathItem = path.get(i);

                    Pipe pipe = NetworkManager.get(world).getPipe(pathItem.getId());
                    if (pipe == null) {
                        LOGGER.error("Pipe @ " + pathItem.getId() + " is null");
                        return;
                    }

                    pipesToGo.push(pipe);
                }

                // Step 5: construct fromPos and toPos
                BlockPos fromPos = from.getPos().offset(from.getAttachmentManager().getAttachment(extractor).getDirection());
                BlockPos toPos = destination.getDestPos();

                // Step 5: create a transport
                transportsToAdd.add(new ItemTransport(
                    new ItemStack(Items.DIAMOND),
                    fromPos,
                    toPos,
                    pipesToGo
                ));

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
