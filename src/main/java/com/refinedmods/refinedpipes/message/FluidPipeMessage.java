package com.refinedmods.refinedpipes.message;

import com.refinedmods.refinedpipes.tile.FluidPipeTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FluidPipeMessage {
    private BlockPos pos;
    private FluidStack fluid;
    private float fullness;

    public FluidPipeMessage(BlockPos pos, FluidStack fluid, float fullness) {
        this.pos = pos;
        this.fluid = fluid;
        this.fullness = fullness;
    }

    public FluidPipeMessage() {
    }

    public static void encode(FluidPipeMessage message, FriendlyByteBuf buf) {
        buf.writeBlockPos(message.pos);
        buf.writeFluidStack(message.fluid);
        buf.writeFloat(message.fullness);
    }

    public static FluidPipeMessage decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        FluidStack fluid = buf.readFluidStack();
        float fullness = buf.readFloat();

        return new FluidPipeMessage(pos, fluid, fullness);
    }

    public static void handle(FluidPipeMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            BlockEntity tile = Minecraft.getInstance().level.getBlockEntity(message.pos);

            if (tile instanceof FluidPipeTileEntity) {
                ((FluidPipeTileEntity) tile).setFluid(message.fluid);
                ((FluidPipeTileEntity) tile).setFullness(message.fullness);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
