package com.refinedmods.refinedpipes.setup;

import com.refinedmods.refinedpipes.RefinedPipes;
import com.refinedmods.refinedpipes.RefinedPipesBlocks;
import com.refinedmods.refinedpipes.RefinedPipesContainers;
import com.refinedmods.refinedpipes.RefinedPipesTileEntities;
import com.refinedmods.refinedpipes.network.pipe.attachment.AttachmentFactory;
import com.refinedmods.refinedpipes.network.pipe.attachment.AttachmentRegistry;
import com.refinedmods.refinedpipes.network.pipe.energy.EnergyPipeType;
import com.refinedmods.refinedpipes.network.pipe.fluid.FluidPipeType;
import com.refinedmods.refinedpipes.network.pipe.item.ItemPipeType;
import com.refinedmods.refinedpipes.render.FluidPipeTileEntityRenderer;
import com.refinedmods.refinedpipes.render.ItemPipeTileEntityRenderer;
import com.refinedmods.refinedpipes.render.PipeBakedModel;
import com.refinedmods.refinedpipes.screen.ExtractorAttachmentScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class ClientSetup {
    private static final Logger LOGGER = LogManager.getLogger(ClientSetup.class);

    public ClientSetup() {
        for (AttachmentFactory factory : AttachmentRegistry.INSTANCE.all()) {
            LOGGER.debug("Registering attachment model {}", factory.getModelLocation());

            ModelLoader.addSpecialModel(factory.getModelLocation());
        }

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModelBake);
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent e) {
        ScreenManager.registerFactory(RefinedPipesContainers.EXTRACTOR_ATTACHMENT, ExtractorAttachmentScreen::new);

        RenderTypeLookup.setRenderLayer(RefinedPipesBlocks.BASIC_ITEM_PIPE, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RefinedPipesBlocks.IMPROVED_ITEM_PIPE, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RefinedPipesBlocks.ADVANCED_ITEM_PIPE, RenderType.getCutout());

        RenderTypeLookup.setRenderLayer(RefinedPipesBlocks.BASIC_FLUID_PIPE, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RefinedPipesBlocks.IMPROVED_FLUID_PIPE, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RefinedPipesBlocks.ADVANCED_FLUID_PIPE, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RefinedPipesBlocks.ELITE_FLUID_PIPE, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RefinedPipesBlocks.ULTIMATE_FLUID_PIPE, RenderType.getCutout());

        ClientRegistry.bindTileEntityRenderer(RefinedPipesTileEntities.BASIC_ITEM_PIPE, ItemPipeTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(RefinedPipesTileEntities.IMPROVED_ITEM_PIPE, ItemPipeTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(RefinedPipesTileEntities.ADVANCED_ITEM_PIPE, ItemPipeTileEntityRenderer::new);

        ClientRegistry.bindTileEntityRenderer(RefinedPipesTileEntities.BASIC_FLUID_PIPE, FluidPipeTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(RefinedPipesTileEntities.IMPROVED_FLUID_PIPE, FluidPipeTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(RefinedPipesTileEntities.ADVANCED_FLUID_PIPE, FluidPipeTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(RefinedPipesTileEntities.ELITE_FLUID_PIPE, FluidPipeTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(RefinedPipesTileEntities.ULTIMATE_FLUID_PIPE, FluidPipeTileEntityRenderer::new);

        for (String type : new String[]{"item", "fluid", "energy"}) {
            ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/" + type + "/basic/core"));
            ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/" + type + "/basic/extension"));
            ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/" + type + "/basic/straight"));

            ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/" + type + "/improved/core"));
            ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/" + type + "/improved/extension"));
            ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/" + type + "/improved/straight"));

            ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/" + type + "/advanced/core"));
            ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/" + type + "/advanced/extension"));
            ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/" + type + "/advanced/straight"));

            if (type.equals("fluid") || type.equals("energy")) {
                ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/" + type + "/elite/core"));
                ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/" + type + "/elite/extension"));
                ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/" + type + "/elite/straight"));

                ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/" + type + "/ultimate/core"));
                ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/" + type + "/ultimate/extension"));
                ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/" + type + "/ultimate/straight"));
            }
        }

        ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment"));
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent e) {
        Map<ResourceLocation, IBakedModel> attachmentModels = new HashMap<>();

        for (AttachmentFactory factory : AttachmentRegistry.INSTANCE.all()) {
            attachmentModels.put(factory.getId(), e.getModelRegistry().get(factory.getModelLocation()));
        }

        for (ResourceLocation id : e.getModelRegistry().keySet()) {
            if (isPipeModel(id, ItemPipeType.BASIC.getId())) {
                e.getModelRegistry().put(id, new PipeBakedModel(
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/basic/core")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/basic/extension")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/basic/straight")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment")),
                    attachmentModels
                ));
            } else if (isPipeModel(id, ItemPipeType.IMPROVED.getId())) {
                e.getModelRegistry().put(id, new PipeBakedModel(
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/improved/core")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/improved/extension")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/improved/straight")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment")),
                    attachmentModels
                ));
            } else if (isPipeModel(id, ItemPipeType.ADVANCED.getId())) {
                e.getModelRegistry().put(id, new PipeBakedModel(
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/advanced/core")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/advanced/extension")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/advanced/straight")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment")),
                    attachmentModels
                ));
            } else if (isPipeModel(id, FluidPipeType.BASIC.getId())) {
                e.getModelRegistry().put(id, new PipeBakedModel(
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/fluid/basic/core")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/fluid/basic/extension")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/fluid/basic/straight")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment")),
                    attachmentModels
                ));
            } else if (isPipeModel(id, FluidPipeType.IMPROVED.getId())) {
                e.getModelRegistry().put(id, new PipeBakedModel(
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/fluid/improved/core")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/fluid/improved/extension")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/fluid/improved/straight")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment")),
                    attachmentModels
                ));
            } else if (isPipeModel(id, FluidPipeType.ADVANCED.getId())) {
                e.getModelRegistry().put(id, new PipeBakedModel(
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/fluid/advanced/core")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/fluid/advanced/extension")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/fluid/advanced/straight")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment")),
                    attachmentModels
                ));
            } else if (isPipeModel(id, FluidPipeType.ELITE.getId())) {
                e.getModelRegistry().put(id, new PipeBakedModel(
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/fluid/elite/core")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/fluid/elite/extension")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/fluid/elite/straight")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment")),
                    attachmentModels
                ));
            } else if (isPipeModel(id, FluidPipeType.ULTIMATE.getId())) {
                e.getModelRegistry().put(id, new PipeBakedModel(
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/fluid/ultimate/core")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/fluid/ultimate/extension")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/fluid/ultimate/straight")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment")),
                    attachmentModels
                ));
            } else if (isPipeModel(id, EnergyPipeType.BASIC.getId())) {
                e.getModelRegistry().put(id, new PipeBakedModel(
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/energy/basic/core")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/energy/basic/extension")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/energy/basic/straight")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment")),
                    attachmentModels
                ));
            } else if (isPipeModel(id, EnergyPipeType.IMPROVED.getId())) {
                e.getModelRegistry().put(id, new PipeBakedModel(
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/energy/improved/core")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/energy/improved/extension")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/energy/improved/straight")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment")),
                    attachmentModels
                ));
            } else if (isPipeModel(id, EnergyPipeType.ADVANCED.getId())) {
                e.getModelRegistry().put(id, new PipeBakedModel(
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/energy/advanced/core")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/energy/advanced/extension")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/energy/advanced/straight")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment")),
                    attachmentModels
                ));
            } else if (isPipeModel(id, EnergyPipeType.ELITE.getId())) {
                e.getModelRegistry().put(id, new PipeBakedModel(
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/energy/elite/core")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/energy/elite/extension")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/energy/elite/straight")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment")),
                    attachmentModels
                ));
            } else if (isPipeModel(id, EnergyPipeType.ULTIMATE.getId())) {
                e.getModelRegistry().put(id, new PipeBakedModel(
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/energy/ultimate/core")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/energy/ultimate/extension")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/energy/ultimate/straight")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment")),
                    attachmentModels
                ));
            }
        }
    }

    private boolean isPipeModel(ResourceLocation modelId, ResourceLocation pipeId) {
        return modelId instanceof ModelResourceLocation
            && modelId.getNamespace().equals(RefinedPipes.ID)
            && modelId.getPath().equals(pipeId.getPath())
            && !((ModelResourceLocation) modelId).getVariant().equals("inventory");
    }
}
