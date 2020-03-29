package com.raoulvdberge.refinedpipes.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fluids.FluidAttributes;

public class ServerConfig {
    private ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    private ForgeConfigSpec spec;

    private ItemPipe basicItemPipe;
    private ItemPipe improvedItemPipe;
    private ItemPipe advancedItemPipe;

    private FluidPipe basicFluidPipe;
    private FluidPipe improvedFluidPipe;
    private FluidPipe advancedFluidPipe;

    private ExtractorAttachment basicExtractorAttachment;
    private ExtractorAttachment improvedExtractorAttachment;
    private ExtractorAttachment advancedExtractorAttachment;
    private ExtractorAttachment eliteExtractorAttachment;
    private ExtractorAttachment ultimateExtractorAttachment;

    public ServerConfig() {
        builder.push("pipe");
        {
            builder.push("item");
            {
                basicItemPipe = new ItemPipe("basic", 30);
                improvedItemPipe = new ItemPipe("improved", 20);
                advancedItemPipe = new ItemPipe("advanced", 10);
            }
            builder.pop();

            builder.push("fluid");
            {
                basicFluidPipe = new FluidPipe("basic", FluidAttributes.BUCKET_VOLUME);
                improvedFluidPipe = new FluidPipe("improved", FluidAttributes.BUCKET_VOLUME * 4);
                advancedFluidPipe = new FluidPipe("advanced", FluidAttributes.BUCKET_VOLUME * 8);
            }
            builder.pop();

            builder.push("attachment");
            {
                builder.push("extractor");
                {
                    basicExtractorAttachment = new ExtractorAttachment(
                        "basic",
                        20 * 3, 8,
                        0, 100
                    );
                    improvedExtractorAttachment = new ExtractorAttachment(
                        "improved",
                        20 * 2, 16,
                        0, 400
                    );
                    advancedExtractorAttachment = new ExtractorAttachment(
                        "advanced",
                        20, 32,
                        0, 800
                    );
                    eliteExtractorAttachment = new ExtractorAttachment(
                        "elite",
                        10, 64,
                        0, 1600
                    );
                    ultimateExtractorAttachment = new ExtractorAttachment(
                        "ultimate",
                        10, 64,
                        0, 3200
                    );
                }
                builder.pop();
            }
            builder.pop();
        }
        builder.pop();

        spec = builder.build();
    }

    public ForgeConfigSpec getSpec() {
        return spec;
    }

    public ItemPipe getBasicItemPipe() {
        return basicItemPipe;
    }

    public ItemPipe getImprovedItemPipe() {
        return improvedItemPipe;
    }

    public ItemPipe getAdvancedItemPipe() {
        return advancedItemPipe;
    }

    public FluidPipe getBasicFluidPipe() {
        return basicFluidPipe;
    }

    public FluidPipe getImprovedFluidPipe() {
        return improvedFluidPipe;
    }

    public FluidPipe getAdvancedFluidPipe() {
        return advancedFluidPipe;
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

    public class FluidPipe {
        private ForgeConfigSpec.IntValue capacity;

        public FluidPipe(String type, int defaultCapacity) {
            builder.push(type);

            capacity = builder.comment("The capacity in mB of the pipe.").defineInRange("capacity", defaultCapacity, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getCapacity() {
            return capacity.get();
        }
    }

    public class ExtractorAttachment {
        private ForgeConfigSpec.IntValue itemTickInterval;
        private ForgeConfigSpec.IntValue itemsToExtract;
        private ForgeConfigSpec.IntValue fluidTickInterval;
        private ForgeConfigSpec.IntValue fluidsToExtract;

        public ExtractorAttachment(String type, int defaultItemTickInterval, int defaultItemsToExtract, int defaultFluidTickInterval, int defaultFluidsToExtract) {
            builder.push(type);

            itemTickInterval = builder.comment("The interval between item extractions in ticks. Lower is faster.").defineInRange("itemTickInterval", defaultItemTickInterval, 0, Integer.MAX_VALUE);
            itemsToExtract = builder.comment("The amount of items to extract per extraction.").defineInRange("itemsToExtract", defaultItemsToExtract, 0, 64);
            fluidTickInterval = builder.comment("The interval between fluid extractions in ticks. Lower is faster.").defineInRange("fluidTickInterval", defaultFluidTickInterval, 0, Integer.MAX_VALUE);
            fluidsToExtract = builder.comment("The amount of fluids in mB to extract per extraction.").defineInRange("fluidsToExtract", defaultFluidsToExtract, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getItemTickInterval() {
            return itemTickInterval.get();
        }

        public int getItemsToExtract() {
            return itemsToExtract.get();
        }

        public int getFluidTickInterval() {
            return fluidTickInterval.get();
        }

        public int getFluidsToExtract() {
            return fluidsToExtract.get();
        }
    }
}
