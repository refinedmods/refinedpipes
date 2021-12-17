package com.refinedmods.refinedpipes.message;

import com.refinedmods.refinedpipes.container.slot.FluidFilterSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FluidFilterSlotUpdateMessage {
    private final int containerSlot;
    private final FluidStack stack;

    public FluidFilterSlotUpdateMessage(int containerSlot, FluidStack stack) {
        this.containerSlot = containerSlot;
        this.stack = stack;
    }

    public static void encode(FluidFilterSlotUpdateMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.containerSlot);
        message.stack.writeToPacket(buf);
    }

    public static FluidFilterSlotUpdateMessage decode(FriendlyByteBuf buf) {
        return new FluidFilterSlotUpdateMessage(buf.readInt(), FluidStack.readFromPacket(buf));
    }

    public static void handle(FluidFilterSlotUpdateMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            AbstractContainerMenu container = Minecraft.getInstance().player.containerMenu;
            if (container == null) {
                return;
            }

            if (message.containerSlot < 0 || message.containerSlot >= container.slots.size()) {
                return;
            }

            Slot slot = container.getSlot(message.containerSlot);
            if (!(slot instanceof FluidFilterSlot)) {
                return;
            }

            ((FluidFilterSlot) slot).getFluidInventory().setFluid(slot.getSlotIndex(), message.stack);
        });

        ctx.get().setPacketHandled(true);
    }
}
