package com.raoulvdberge.refinedpipes.setup;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.RefinedPipesBlocks;
import com.raoulvdberge.refinedpipes.RefinedPipesTileEntities;
import com.raoulvdberge.refinedpipes.network.pipe.PipeType;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentRegistry;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentType;
import com.raoulvdberge.refinedpipes.render.PipeBakedModel;
import com.raoulvdberge.refinedpipes.render.PipeTileEntityRenderer;
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
        RenderTypeLookup.setRenderLayer(RefinedPipesBlocks.SIMPLE_PIPE, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RefinedPipesBlocks.BASIC_PIPE, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RefinedPipesBlocks.IMPROVED_PIPE, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RefinedPipesBlocks.ADVANCED_PIPE, RenderType.getCutout());

        ClientRegistry.bindTileEntityRenderer(RefinedPipesTileEntities.SIMPLE_PIPE, PipeTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(RefinedPipesTileEntities.BASIC_PIPE, PipeTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(RefinedPipesTileEntities.IMPROVED_PIPE, PipeTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(RefinedPipesTileEntities.ADVANCED_PIPE, PipeTileEntityRenderer::new);

        ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/simple/core"));
        ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/simple/extension"));
        ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/simple/straight"));

        ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/basic/core"));
        ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/basic/extension"));
        ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/basic/straight"));

        ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/improved/core"));
        ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/improved/extension"));
        ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/improved/straight"));

        ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/advanced/core"));
        ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/advanced/extension"));
        ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/advanced/straight"));
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent e) {
        Map<AttachmentType, IBakedModel> attachmentModels = new HashMap<>();

        for (AttachmentType type : AttachmentRegistry.INSTANCE.getTypes()) {
            attachmentModels.put(type, e.getModelRegistry().get(type.getModelLocation()));
        }

        for (ResourceLocation id : e.getModelRegistry().keySet()) {
            if (isPipeModel(id, PipeType.SIMPLE)) {
                e.getModelRegistry().put(id, new PipeBakedModel(
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/simple/core")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/simple/extension")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/simple/straight")),
                    attachmentModels
                ));
            } else if (isPipeModel(id, PipeType.BASIC)) {
                e.getModelRegistry().put(id, new PipeBakedModel(
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/basic/core")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/basic/extension")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/basic/straight")),
                    attachmentModels
                ));
            } else if (isPipeModel(id, PipeType.IMPROVED)) {
                e.getModelRegistry().put(id, new PipeBakedModel(
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/improved/core")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/improved/extension")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/improved/straight")),
                    attachmentModels
                ));
            } else if (isPipeModel(id, PipeType.ADVANCED)) {
                e.getModelRegistry().put(id, new PipeBakedModel(
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/advanced/core")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/advanced/extension")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/advanced/straight")),
                    attachmentModels
                ));
            }
        }
    }

    private boolean isPipeModel(ResourceLocation id, PipeType pipeType) {
        return id instanceof ModelResourceLocation
            && id.getNamespace().equals(RefinedPipes.ID)
            && id.getPath().equals(pipeType.getId().getPath())
            && !((ModelResourceLocation) id).getVariant().equals("inventory");
    }
}
