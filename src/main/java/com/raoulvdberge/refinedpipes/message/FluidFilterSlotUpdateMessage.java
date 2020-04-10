package com.raoulvdberge.refinedpipes.message;

import com.raoulvdberge.refinedpipes.container.slot.FluidFilterSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class FluidFilterSlotUpdateMessage {
    private final int containerSlot;
    private final FluidStack stack;

    public FluidFilterSlotUpdateMessage(int containerSlot, FluidStack stack) {
        this.containerSlot = containerSlot;
        this.stack = stack;
    }

    public static void encode(FluidFilterSlotUpdateMessage message, PacketBuffer buf) {
        buf.writeInt(message.containerSlot);
        message.stack.writeToPacket(buf);
    }

    public static FluidFilterSlotUpdateMessage decode(PacketBuffer buf) {
        return new FluidFilterSlotUpdateMessage(buf.readInt(), FluidStack.readFromPacket(buf));
    }

    public static void handle(FluidFilterSlotUpdateMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Container container = Minecraft.getInstance().player.openContainer;
            if (container == null) {
                return;
            }

            if (message.containerSlot < 0 || message.containerSlot >= container.inventorySlots.size()) {
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
