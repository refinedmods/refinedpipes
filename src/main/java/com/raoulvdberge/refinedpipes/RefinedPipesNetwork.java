package com.raoulvdberge.refinedpipes;

import com.raoulvdberge.refinedpipes.message.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class RefinedPipesNetwork {
    private final String protocolVersion = Integer.toString(1);
    private final SimpleChannel handler = NetworkRegistry.ChannelBuilder
        .named(new ResourceLocation(RefinedPipes.ID, "main_channel"))
        .clientAcceptedVersions(protocolVersion::equals)
        .serverAcceptedVersions(protocolVersion::equals)
        .networkProtocolVersion(() -> protocolVersion)
        .simpleChannel();

    public void register() {
        handler.registerMessage(0, ItemTransportMessage.class, ItemTransportMessage::encode, ItemTransportMessage::decode, ItemTransportMessage::handle);
        handler.registerMessage(1, FluidPipeMessage.class, FluidPipeMessage::encode, FluidPipeMessage::decode, FluidPipeMessage::handle);
        handler.registerMessage(2, ChangeRedstoneModeMessage.class, ChangeRedstoneModeMessage::encode, ChangeRedstoneModeMessage::decode, ChangeRedstoneModeMessage::handle);
        handler.registerMessage(3, ChangeBlacklistWhitelistMessage.class, ChangeBlacklistWhitelistMessage::encode, ChangeBlacklistWhitelistMessage::decode, ChangeBlacklistWhitelistMessage::handle);
        handler.registerMessage(4, ChangeRoutingModeMessage.class, ChangeRoutingModeMessage::encode, ChangeRoutingModeMessage::decode, ChangeRoutingModeMessage::handle);
        handler.registerMessage(5, ChangeStackSizeMessage.class, ChangeStackSizeMessage::encode, ChangeStackSizeMessage::decode, ChangeStackSizeMessage::handle);
    }

    public void sendInArea(World world, BlockPos pos, int radius, Object message) {
        handler.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
            pos.getX(),
            pos.getY(),
            pos.getZ(),
            radius,
            world.getDimension().getType()
        )), message);
    }

    public void sendToServer(Object message) {
        handler.sendToServer(message);
    }
}
