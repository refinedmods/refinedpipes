package com.raoulvdberge.refinedpipes.setup;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.RefinedPipesBlocks;
import com.raoulvdberge.refinedpipes.RefinedPipesTileEntities;
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
        RenderTypeLookup.setRenderLayer(RefinedPipesBlocks.PIPE, RenderType.getCutout());

        ClientRegistry.bindTileEntityRenderer(RefinedPipesTileEntities.PIPE, PipeTileEntityRenderer::new);

        ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/core"));
        ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/extension"));
        ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe/straight"));
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent e) {
        Map<AttachmentType, IBakedModel> attachmentModels = new HashMap<>();

        for (AttachmentType type : AttachmentRegistry.INSTANCE.getTypes()) {
            attachmentModels.put(type, e.getModelRegistry().get(type.getModelLocation()));
        }

        for (ResourceLocation id : e.getModelRegistry().keySet()) {
            if (id instanceof ModelResourceLocation &&
                id.getNamespace().equals(RefinedPipes.ID) &&
                id.getPath().equals("pipe") &&
                !((ModelResourceLocation) id).getVariant().equals("inventory")) {

                e.getModelRegistry().put(id, new PipeBakedModel(
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/core")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/extension")),
                    e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe/straight")),
                    attachmentModels
                ));
            }
        }
    }
}
