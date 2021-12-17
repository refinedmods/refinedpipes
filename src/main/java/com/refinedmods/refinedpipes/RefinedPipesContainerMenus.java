package com.refinedmods.refinedpipes;

import com.refinedmods.refinedpipes.container.ExtractorAttachmentContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.ObjectHolder;

public class RefinedPipesContainerMenus {
    @ObjectHolder(RefinedPipes.ID + ":extractor_attachment")
    public static final MenuType<ExtractorAttachmentContainerMenu> EXTRACTOR_ATTACHMENT = null;
}
