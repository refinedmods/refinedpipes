package com.refinedmods.refinedpipes;

import com.refinedmods.refinedpipes.message.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
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
        int id = 0;

        handler.registerMessage(id++, ItemTransportMessage.class, ItemTransportMessage::encode, ItemTransportMessage::decode, ItemTransportMessage::handle);
        handler.registerMessage(id++, FluidPipeMessage.class, FluidPipeMessage::encode, FluidPipeMessage::decode, FluidPipeMessage::handle);
        handler.registerMessage(id++, ChangeRedstoneModeMessage.class, ChangeRedstoneModeMessage::encode, ChangeRedstoneModeMessage::decode, ChangeRedstoneModeMessage::handle);
        handler.registerMessage(id++, ChangeBlacklistWhitelistMessage.class, ChangeBlacklistWhitelistMessage::encode, ChangeBlacklistWhitelistMessage::decode, ChangeBlacklistWhitelistMessage::handle);
        handler.registerMessage(id++, ChangeRoutingModeMessage.class, ChangeRoutingModeMessage::encode, ChangeRoutingModeMessage::decode, ChangeRoutingModeMessage::handle);
        handler.registerMessage(id++, ChangeStackSizeMessage.class, ChangeStackSizeMessage::encode, ChangeStackSizeMessage::decode, ChangeStackSizeMessage::handle);
        handler.registerMessage(id++, ChangeExactModeMessage.class, ChangeExactModeMessage::encode, ChangeExactModeMessage::decode, ChangeExactModeMessage::handle);
        handler.registerMessage(id++, FluidFilterSlotUpdateMessage.class, FluidFilterSlotUpdateMessage::encode, FluidFilterSlotUpdateMessage::decode, FluidFilterSlotUpdateMessage::handle);
    }

    public void sendInArea(World world, BlockPos pos, int radius, Object message) {
        handler.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
            pos.getX(),
            pos.getY(),
            pos.getZ(),
            radius,
            world.dimension()
        )), message);
    }

    public void sendToServer(Object message) {
        handler.sendToServer(message);
    }

    public void sendToClient(ServerPlayerEntity player, Object message) {
        handler.sendTo(message, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
