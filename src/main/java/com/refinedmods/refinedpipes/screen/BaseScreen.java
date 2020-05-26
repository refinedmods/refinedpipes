package com.refinedmods.refinedpipes.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.refinedmods.refinedpipes.container.BaseContainer;
import com.refinedmods.refinedpipes.container.slot.FluidFilterSlot;
import com.refinedmods.refinedpipes.render.FluidRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseScreen<T extends BaseContainer> extends ContainerScreen<T> {
    private final List<String> fluidTooltip = new ArrayList<>(1);

    public BaseScreen(T screenContainer, PlayerInventory inv, ITextComponent title) {
        super(screenContainer, inv, title);

        fluidTooltip.add("");
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        for (FluidFilterSlot slot : container.getFluidSlots()) {
            FluidStack stack = slot.getFluidInventory().getFluid(slot.getSlotIndex());
            if (stack.isEmpty()) {
                continue;
            }

            FluidRenderer.INSTANCE.render(guiLeft + slot.xPos, guiTop + slot.yPos, stack);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        for (FluidFilterSlot slot : container.getFluidSlots()) {
            FluidStack stack = slot.getFluidInventory().getFluid(slot.getSlotIndex());
            if (stack.isEmpty()) {
                continue;
            }

            if (!isPointInRegion(slot.xPos, slot.yPos, 17, 17, mouseX, mouseY)) {
                continue;
            }

            fluidTooltip.set(0, stack.getDisplayName().getFormattedText());

            GuiUtils.drawHoveringText(fluidTooltip, mouseX - guiLeft, mouseY - guiTop, width, height, -1, font);
        }
    }
}
