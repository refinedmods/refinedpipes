package com.raoulvdberge.refinedpipes.setup;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.network.AttachmentType;
import com.raoulvdberge.refinedpipes.render.PipeBakedModel;
import com.raoulvdberge.refinedpipes.render.PipeTileEntityRenderer;
import com.raoulvdberge.refinedpipes.tile.PipeTileEntity;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.HashMap;
import java.util.Map;

public class ClientSetup {
    public ClientSetup() {
        ClientRegistry.bindTileEntitySpecialRenderer(PipeTileEntity.class, new PipeTileEntityRenderer());

        ModelLoader.addSpecialModel(new ResourceLocation(RefinedPipes.ID + ":block/pipe_attachment"));

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModelBake);
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent e) {
        Map<AttachmentType, IBakedModel> attachmentModels = new HashMap<>();
        attachmentModels.put(AttachmentType.NORMAL, e.getModelRegistry().get(new ResourceLocation(RefinedPipes.ID + ":block/pipe_attachment")));

        for (ResourceLocation id : e.getModelRegistry().keySet()) {
            if (id instanceof ModelResourceLocation &&
                id.getNamespace().equals(RefinedPipes.ID) &&
                id.getPath().equals("pipe") &&
                !((ModelResourceLocation) id).getVariant().equals("inventory")) {
                e.getModelRegistry().put(id, new PipeBakedModel(
                    e.getModelRegistry().get(id),
                    attachmentModels
                ));
            }
        }
    }
}
