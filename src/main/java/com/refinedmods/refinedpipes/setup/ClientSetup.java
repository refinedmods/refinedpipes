package com.refinedmods.refinedpipes.setup;

import com.refinedmods.refinedpipes.RefinedPipes;
import com.refinedmods.refinedpipes.RefinedPipesBlockEntities;
import com.refinedmods.refinedpipes.RefinedPipesBlocks;
import com.refinedmods.refinedpipes.RefinedPipesContainerMenus;
import com.refinedmods.refinedpipes.network.pipe.attachment.AttachmentFactory;
import com.refinedmods.refinedpipes.network.pipe.attachment.AttachmentRegistry;
import com.refinedmods.refinedpipes.network.pipe.energy.EnergyPipeType;
import com.refinedmods.refinedpipes.network.pipe.fluid.FluidPipeType;
import com.refinedmods.refinedpipes.network.pipe.item.ItemPipeType;
import com.refinedmods.refinedpipes.render.FluidPipeBlockEntityRenderer;
import com.refinedmods.refinedpipes.render.ItemPipeBlockEntityRenderer;
import com.refinedmods.refinedpipes.render.PipeBakedModel;
import com.refinedmods.refinedpipes.screen.ExtractorAttachmentScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class ClientSetup {
    private static final Logger LOGGER = LogManager.getLogger(ClientSetup.class);

    private ClientSetup() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent e) {
        for (AttachmentFactory factory : AttachmentRegistry.INSTANCE.all()) {
            LOGGER.debug("Registering attachment model {}", factory.getModelLocation());

            ForgeModelBakery.addSpecialModel(factory.getModelLocation());
        }

        for (String type : new String[]{"item", "fluid", "energy"}) {
            ForgeModelBakery.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/" + type + "/basic/core"));
            ForgeModelBakery.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/" + type + "/basic/extension"));
            ForgeModelBakery.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/" + type + "/basic/straight"));

            ForgeModelBakery.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/" + type + "/improved/core"));
            ForgeModelBakery.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/" + type + "/improved/extension"));
            ForgeModelBakery.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/" + type + "/improved/straight"));

            ForgeModelBakery.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/" + type + "/advanced/core"));
            ForgeModelBakery.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/" + type + "/advanced/extension"));
            ForgeModelBakery.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/" + type + "/advanced/straight"));

            if (type.equals("fluid") || type.equals("energy")) {
                ForgeModelBakery.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/" + type + "/elite/core"));
                ForgeModelBakery.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/" + type + "/elite/extension"));
                ForgeModelBakery.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/" + type + "/elite/straight"));

                ForgeModelBakery.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/" + type + "/ultimate/core"));
                ForgeModelBakery.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/" + type + "/ultimate/extension"));
                ForgeModelBakery.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/" + type + "/ultimate/straight"));
            }
        }

        ForgeModelBakery.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment"));

        MenuScreens.register(RefinedPipesContainerMenus.EXTRACTOR_ATTACHMENT, ExtractorAttachmentScreen::new);

        ItemBlockRenderTypes.setRenderLayer(RefinedPipesBlocks.BASIC_ITEM_PIPE, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(RefinedPipesBlocks.IMPROVED_ITEM_PIPE, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(RefinedPipesBlocks.ADVANCED_ITEM_PIPE, RenderType.cutout());

        ItemBlockRenderTypes.setRenderLayer(RefinedPipesBlocks.BASIC_FLUID_PIPE, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(RefinedPipesBlocks.IMPROVED_FLUID_PIPE, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(RefinedPipesBlocks.ADVANCED_FLUID_PIPE, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(RefinedPipesBlocks.ELITE_FLUID_PIPE, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(RefinedPipesBlocks.ULTIMATE_FLUID_PIPE, RenderType.cutout());

        BlockEntityRenderers.register(RefinedPipesBlockEntities.BASIC_ITEM_PIPE, ctx -> new ItemPipeBlockEntityRenderer());
        BlockEntityRenderers.register(RefinedPipesBlockEntities.IMPROVED_ITEM_PIPE, ctx -> new ItemPipeBlockEntityRenderer());
        BlockEntityRenderers.register(RefinedPipesBlockEntities.ADVANCED_ITEM_PIPE, ctx -> new ItemPipeBlockEntityRenderer());

        BlockEntityRenderers.register(RefinedPipesBlockEntities.BASIC_FLUID_PIPE, ctx -> new FluidPipeBlockEntityRenderer());
        BlockEntityRenderers.register(RefinedPipesBlockEntities.IMPROVED_FLUID_PIPE, ctx -> new FluidPipeBlockEntityRenderer());
        BlockEntityRenderers.register(RefinedPipesBlockEntities.ADVANCED_FLUID_PIPE, ctx -> new FluidPipeBlockEntityRenderer());
        BlockEntityRenderers.register(RefinedPipesBlockEntities.ELITE_FLUID_PIPE, ctx -> new FluidPipeBlockEntityRenderer());
        BlockEntityRenderers.register(RefinedPipesBlockEntities.ULTIMATE_FLUID_PIPE, ctx -> new FluidPipeBlockEntityRenderer());
    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent e) {
        Map<ResourceLocation, BakedModel> attachmentModels = new HashMap<>();

        for (AttachmentFactory factory : AttachmentRegistry.INSTANCE.all()) {
            attachmentModels.put(factory.getId(), e.getModelRegistry().get(factory.getModelLocation()));
        }

        Map<ResourceLocation, PipeBakedModel> pipeModels = new HashMap<>();

        pipeModels.put(ItemPipeType.BASIC.getId(), new PipeBakedModel(
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/basic/core")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/basic/extension")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/basic/straight")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment")),
            attachmentModels
        ));
        pipeModels.put(ItemPipeType.IMPROVED.getId(), new PipeBakedModel(
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/improved/core")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/improved/extension")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/improved/straight")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment")),
            attachmentModels
        ));
        pipeModels.put(ItemPipeType.ADVANCED.getId(), new PipeBakedModel(
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/advanced/core")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/advanced/extension")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/advanced/straight")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment")),
            attachmentModels
        ));
        pipeModels.put(FluidPipeType.BASIC.getId(), new PipeBakedModel(
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/fluid/basic/core")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/fluid/basic/extension")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/fluid/basic/straight")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment")),
            attachmentModels
        ));
        pipeModels.put(FluidPipeType.IMPROVED.getId(), new PipeBakedModel(
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/fluid/improved/core")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/fluid/improved/extension")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/fluid/improved/straight")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment")),
            attachmentModels
        ));
        pipeModels.put(FluidPipeType.ADVANCED.getId(), new PipeBakedModel(
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/fluid/advanced/core")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/fluid/advanced/extension")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/fluid/advanced/straight")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment")),
            attachmentModels
        ));
        pipeModels.put(FluidPipeType.ELITE.getId(), new PipeBakedModel(
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/fluid/elite/core")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/fluid/elite/extension")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/fluid/elite/straight")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment")),
            attachmentModels
        ));
        pipeModels.put(FluidPipeType.ULTIMATE.getId(), new PipeBakedModel(
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/fluid/ultimate/core")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/fluid/ultimate/extension")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/fluid/ultimate/straight")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment")),
            attachmentModels
        ));
        pipeModels.put(EnergyPipeType.BASIC.getId(), new PipeBakedModel(
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/energy/basic/core")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/energy/basic/extension")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/energy/basic/straight")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment")),
            attachmentModels
        ));
        pipeModels.put(EnergyPipeType.IMPROVED.getId(), new PipeBakedModel(
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/energy/improved/core")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/energy/improved/extension")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/energy/improved/straight")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment")),
            attachmentModels
        ));
        pipeModels.put(EnergyPipeType.ADVANCED.getId(), new PipeBakedModel(
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/energy/advanced/core")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/energy/advanced/extension")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/energy/advanced/straight")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment")),
            attachmentModels
        ));
        pipeModels.put(EnergyPipeType.ELITE.getId(), new PipeBakedModel(
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/energy/elite/core")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/energy/elite/extension")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/energy/elite/straight")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment")),
            attachmentModels
        ));
        pipeModels.put(EnergyPipeType.ULTIMATE.getId(), new PipeBakedModel(
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/energy/ultimate/core")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/energy/ultimate/extension")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/energy/ultimate/straight")),
            e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment")),
            attachmentModels
        ));

        on: for (ResourceLocation id : e.getModelRegistry().keySet()) {
            for (Entry<ResourceLocation, PipeBakedModel> entry : pipeModels.entrySet()) {
                if (isPipeModel(id, entry.getKey())) {
                    e.getModelRegistry().put(id, entry.getValue());
                    continue on;
                }
            }
        }
    }

    private static boolean isPipeModel(ResourceLocation modelId, ResourceLocation pipeId) {
        return modelId instanceof ModelResourceLocation
            && modelId.getNamespace().equals(RefinedPipes.ID)
            && modelId.getPath().equals(pipeId.getPath())
            && !((ModelResourceLocation) modelId).getVariant().equals("inventory");
    }
}
