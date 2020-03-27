package com.raoulvdberge.refinedpipes.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    private ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    private ForgeConfigSpec spec;

    private ItemPipe basicPipe;
    private ItemPipe improvedPipe;
    private ItemPipe advancedPipe;

    private ExtractorAttachment basicExtractorAttachment;
    private ExtractorAttachment improvedExtractorAttachment;
    private ExtractorAttachment advancedExtractorAttachment;
    private ExtractorAttachment eliteExtractorAttachment;
    private ExtractorAttachment ultimateExtractorAttachment;

    public ServerConfig() {
        builder.push("itemPipe");
        basicPipe = new ItemPipe("basic", 30);
        improvedPipe = new ItemPipe("improved", 20);
        advancedPipe = new ItemPipe("advanced", 10);
        builder.pop();

        builder.push("attachment");
        builder.push("extractor");
        basicExtractorAttachment = new ExtractorAttachment("basic", 20 * 3, 8);
        improvedExtractorAttachment = new ExtractorAttachment("improved", 20 * 2, 16);
        advancedExtractorAttachment = new ExtractorAttachment("advanced", 20, 32);
        eliteExtractorAttachment = new ExtractorAttachment("elite", 10, 64);
        ultimateExtractorAttachment = new ExtractorAttachment("ultimate", 10, 64);
        builder.pop();
        builder.pop();

        spec = builder.build();
    }

    public ForgeConfigSpec getSpec() {
        return spec;
    }

    public ItemPipe getBasicPipe() {
        return basicPipe;
    }

    public ItemPipe getImprovedPipe() {
        return improvedPipe;
    }

    public ItemPipe getAdvancedPipe() {
        return advancedPipe;
    }

    public ExtractorAttachment getBasicExtractorAttachment() {
        return basicExtractorAttachment;
    }

    public ExtractorAttachment getImprovedExtractorAttachment() {
        return improvedExtractorAttachment;
    }

    public ExtractorAttachment getAdvancedExtractorAttachment() {
        return advancedExtractorAttachment;
    }

    public ExtractorAttachment getEliteExtractorAttachment() {
        return eliteExtractorAttachment;
    }

    public ExtractorAttachment getUltimateExtractorAttachment() {
        return ultimateExtractorAttachment;
    }

    public class ItemPipe {
        private ForgeConfigSpec.IntValue maxTicks;

        public ItemPipe(String type, int defaultMaxTicks) {
            builder.push(type);

            maxTicks = builder.comment("The maximum amount of ticks that items can be in the pipe. Lower is faster.").defineInRange("maxTicks", defaultMaxTicks, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getMaxTicks() {
            return maxTicks.get();
        }
    }

    public class ExtractorAttachment {
        private ForgeConfigSpec.IntValue tickInterval;
        private ForgeConfigSpec.IntValue itemsToExtract;

        public ExtractorAttachment(String type, int defaultTickInterval, int defaultItemsToExtract) {
            builder.push(type);

            tickInterval = builder.comment("The interval between item extractions in ticks. Lower is faster.").defineInRange("tickInterval", defaultTickInterval, 0, Integer.MAX_VALUE);
            itemsToExtract = builder.comment("The amount of items to extract per extraction.").defineInRange("itemsToExtract", defaultItemsToExtract, 0, 64);

            builder.pop();
        }

        public int getTickInterval() {
            return tickInterval.get();
        }

        public int getItemsToExtract() {
            return itemsToExtract.get();
        }
    }
}
