package com.raoulvdberge.refinedpipes.network.pipe.attachment.extractor;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.RefinedPipesItems;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public enum ExtractorAttachmentType {
    BASIC(1),
    IMPROVED(2),
    ADVANCED(3),
    ELITE(4),
    ULTIMATE(5);

    private final int tier;

    ExtractorAttachmentType(int tier) {
        this.tier = tier;
    }

    public int getTier() {
        return tier;
    }

    int getItemTickInterval() {
        switch (this) {
            case BASIC:
                return RefinedPipes.SERVER_CONFIG.getBasicExtractorAttachment().getItemTickInterval();
            case IMPROVED:
                return RefinedPipes.SERVER_CONFIG.getImprovedExtractorAttachment().getItemTickInterval();
            case ADVANCED:
                return RefinedPipes.SERVER_CONFIG.getAdvancedExtractorAttachment().getItemTickInterval();
            case ELITE:
                return RefinedPipes.SERVER_CONFIG.getEliteExtractorAttachment().getItemTickInterval();
            case ULTIMATE:
                return RefinedPipes.SERVER_CONFIG.getUltimateExtractorAttachment().getItemTickInterval();
            default:
                throw new RuntimeException("?");
        }
    }

    int getFluidTickInterval() {
        switch (this) {
            case BASIC:
                return RefinedPipes.SERVER_CONFIG.getBasicExtractorAttachment().getFluidTickInterval();
            case IMPROVED:
                return RefinedPipes.SERVER_CONFIG.getImprovedExtractorAttachment().getFluidTickInterval();
            case ADVANCED:
                return RefinedPipes.SERVER_CONFIG.getAdvancedExtractorAttachment().getFluidTickInterval();
            case ELITE:
                return RefinedPipes.SERVER_CONFIG.getEliteExtractorAttachment().getFluidTickInterval();
            case ULTIMATE:
                return RefinedPipes.SERVER_CONFIG.getUltimateExtractorAttachment().getFluidTickInterval();
            default:
                throw new RuntimeException("?");
        }
    }

    int getItemsToExtract() {
        switch (this) {
            case BASIC:
                return RefinedPipes.SERVER_CONFIG.getBasicExtractorAttachment().getItemsToExtract();
            case IMPROVED:
                return RefinedPipes.SERVER_CONFIG.getImprovedExtractorAttachment().getItemsToExtract();
            case ADVANCED:
                return RefinedPipes.SERVER_CONFIG.getAdvancedExtractorAttachment().getItemsToExtract();
            case ELITE:
                return RefinedPipes.SERVER_CONFIG.getEliteExtractorAttachment().getItemsToExtract();
            case ULTIMATE:
                return RefinedPipes.SERVER_CONFIG.getUltimateExtractorAttachment().getItemsToExtract();
            default:
                throw new RuntimeException("?");
        }
    }

    int getFluidsToExtract() {
        switch (this) {
            case BASIC:
                return RefinedPipes.SERVER_CONFIG.getBasicExtractorAttachment().getFluidsToExtract();
            case IMPROVED:
                return RefinedPipes.SERVER_CONFIG.getImprovedExtractorAttachment().getFluidsToExtract();
            case ADVANCED:
                return RefinedPipes.SERVER_CONFIG.getAdvancedExtractorAttachment().getFluidsToExtract();
            case ELITE:
                return RefinedPipes.SERVER_CONFIG.getEliteExtractorAttachment().getFluidsToExtract();
            case ULTIMATE:
                return RefinedPipes.SERVER_CONFIG.getUltimateExtractorAttachment().getFluidsToExtract();
            default:
                throw new RuntimeException("?");
        }
    }

    public ResourceLocation getId() {
        switch (this) {
            case BASIC:
                return new ResourceLocation(RefinedPipes.ID, "basic_extractor");
            case IMPROVED:
                return new ResourceLocation(RefinedPipes.ID, "improved_extractor");
            case ADVANCED:
                return new ResourceLocation(RefinedPipes.ID, "advanced_extractor");
            case ELITE:
                return new ResourceLocation(RefinedPipes.ID, "elite_extractor");
            case ULTIMATE:
                return new ResourceLocation(RefinedPipes.ID, "ultimate_extractor");
            default:
                throw new RuntimeException("?");
        }
    }

    ResourceLocation getItemId() {
        switch (this) {
            case BASIC:
                return new ResourceLocation(RefinedPipes.ID, "basic_extractor_attachment");
            case IMPROVED:
                return new ResourceLocation(RefinedPipes.ID, "improved_extractor_attachment");
            case ADVANCED:
                return new ResourceLocation(RefinedPipes.ID, "advanced_extractor_attachment");
            case ELITE:
                return new ResourceLocation(RefinedPipes.ID, "elite_extractor_attachment");
            case ULTIMATE:
                return new ResourceLocation(RefinedPipes.ID, "ultimate_extractor_attachment");
            default:
                throw new RuntimeException("?");
        }
    }

    Item getItem() {
        switch (this) {
            case BASIC:
                return RefinedPipesItems.BASIC_EXTRACTOR_ATTACHMENT;
            case IMPROVED:
                return RefinedPipesItems.IMPROVED_EXTRACTOR_ATTACHMENT;
            case ADVANCED:
                return RefinedPipesItems.ADVANCED_EXTRACTOR_ATTACHMENT;
            case ELITE:
                return RefinedPipesItems.ELITE_EXTRACTOR_ATTACHMENT;
            case ULTIMATE:
                return RefinedPipesItems.ULTIMATE_EXTRACTOR_ATTACHMENT;
            default:
                throw new RuntimeException("?");
        }
    }

    ResourceLocation getModelLocation() {
        switch (this) {
            case BASIC:
                return new ResourceLocation(RefinedPipes.ID, "block/pipe/attachment/extractor/basic");
            case IMPROVED:
                return new ResourceLocation(RefinedPipes.ID, "block/pipe/attachment/extractor/improved");
            case ADVANCED:
                return new ResourceLocation(RefinedPipes.ID, "block/pipe/attachment/extractor/advanced");
            case ELITE:
                return new ResourceLocation(RefinedPipes.ID, "block/pipe/attachment/extractor/elite");
            case ULTIMATE:
                return new ResourceLocation(RefinedPipes.ID, "block/pipe/attachment/extractor/ultimate");
            default:
                throw new RuntimeException("?");
        }
    }
}
