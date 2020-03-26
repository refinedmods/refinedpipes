package com.raoulvdberge.refinedpipes.setup;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.RefinedPipesBlocks;
import com.raoulvdberge.refinedpipes.RefinedPipesTileEntities;
import com.raoulvdberge.refinedpipes.network.pipe.ItemPipeType;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentRegistry;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentType;
import com.raoulvdberge.refinedpipes.render.ItemPipeBakedModel;
import com.raoulvdberge.refinedpipes.render.ItemPipeTileEntityRenderer;
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
        for (AttachmentType type : AttachmentRegistry.INSTANCE.getTypes()) {
            LOGGER.debug("Registering attachment model {} for {}", type.getModelLocation(), type.getId());

            ModelLoader.addSpecialModel(type.getModelLocation());
        }

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModelBake);
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent e) {
        RenderTypeLookup.setRenderLayer(RefinedPipesBlocks.BASIC_ITEM_PIPE, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RefinedPipesBlocks.IMPROVED_ITEM_PIPE, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RefinedPipesBlocks.ADVANCED_ITEM_PIPE, RenderType.getCutout());

        ClientRegistry.bindTileEntityRenderer(RefinedPipesTileEntities.BASIC_ITEM_PIPE, ItemPipeTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(RefinedPipesTileEntities.IMPROVED_ITEM_PIPE, ItemPipeTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(RefinedPipesTileEntities.ADVANCED_ITEM_PIPE, ItemPipeTileEntityRenderer::new);

        ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/basic/core"));
        ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/basic/extension"));
        ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/basic/straight"));

        ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/improved/core"));
        ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/improved/extension"));
        ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/improved/straight"));

        ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/advanced/core"));
        ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/advanced/extension"));
        ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/advanced/straight"));

        ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment"));
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent e) {
        Map<AttachmentType, IBakedModel> attachmentModels = new HashMap<>();

        for (AttachmentType type : AttachmentRegistry.INSTANCE.getTypes()) {
            attachmentModels.put(type, e.getModelRegistry().get(type.getModelLocation()));
        }

        for (ResourceLocation id : e.getModelRegistry().keySet()) {
            if (isPipeModel(id, ItemPipeType.BASIC)) {
                e.getModelRegistry().put(id, new ItemPipeBakedModel(
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/basic/core")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/basic/extension")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/basic/straight")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment")),
                    attachmentModels
                ));
            } else if (isPipeModel(id, ItemPipeType.IMPROVED)) {
                e.getModelRegistry().put(id, new ItemPipeBakedModel(
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/improved/core")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/improved/extension")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/improved/straight")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment")),
                    attachmentModels
                ));
            } else if (isPipeModel(id, ItemPipeType.ADVANCED)) {
                e.getModelRegistry().put(id, new ItemPipeBakedModel(
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/advanced/core")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/advanced/extension")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/item/advanced/straight")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/attachment/inventory_attachment")),
                    attachmentModels
                ));
            }
        }
    }

    private boolean isPipeModel(ResourceLocation id, ItemPipeType pipeType) {
        return id instanceof ModelResourceLocation
            && id.getNamespace().equals(RefinedPipes.ID)
            && id.getPath().equals(pipeType.getId().getPath())
            && !((ModelResourceLocation) id).getVariant().equals("inventory");
    }
}
