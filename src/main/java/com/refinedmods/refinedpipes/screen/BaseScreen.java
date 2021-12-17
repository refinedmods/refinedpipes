package com.refinedmods.refinedpipes.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedpipes.container.BaseContainer;
import com.refinedmods.refinedpipes.container.slot.FluidFilterSlot;
import com.refinedmods.refinedpipes.render.FluidRenderer;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;

public abstract class BaseScreen<T extends BaseContainer> extends AbstractContainerScreen<T> {
    public BaseScreen(T screenContainer, Inventory inv, Component title) {
        super(screenContainer, inv, title);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        for (FluidFilterSlot slot : menu.getFluidSlots()) {
            FluidStack stack = slot.getFluidInventory().getFluid(slot.getSlotIndex());
            if (stack.isEmpty()) {
                continue;
            }

            FluidRenderer.INSTANCE.render(leftPos + slot.x, topPos + slot.y, stack);
        }
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        for (FluidFilterSlot slot : menu.getFluidSlots()) {
            FluidStack stack = slot.getFluidInventory().getFluid(slot.getSlotIndex());
            if (stack.isEmpty()) {
                continue;
            }

            if (!isHovering(slot.x, slot.y, 17, 17, mouseX, mouseY)) {
                continue;
            }

            renderTooltip(matrixStack, stack.getDisplayName(), mouseX - leftPos, mouseY - topPos);
        }
    }
}
