package com.raoulvdberge.refinedpipes.setup;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientSetup {
    public ClientSetup() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModelBake);
    }

    public void onModelBake(ModelBakeEvent e) {
        ResourceLocation pipeItem = new ModelResourceLocation(
            new ResourceLocation(RefinedPipes.ID, "pipe"),
            "inventory"
        );
        ResourceLocation pipeBlock = new ModelResourceLocation(
            new ResourceLocation(RefinedPipes.ID, "pipe"),
            "down=false,east=false,north=false,south=false,up=false,west=false"
        );

        e.getModelRegistry().put(pipeItem, e.getModelRegistry().get(pipeBlock));
    }
}
