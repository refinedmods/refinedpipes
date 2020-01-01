package com.raoulvdberge.refinedpipes;

import com.raoulvdberge.refinedpipes.block.PipeBlock;
import com.raoulvdberge.refinedpipes.item.BlockItemBase;
import com.raoulvdberge.refinedpipes.item.group.MainItemGroup;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RefinedPipes.ID)
public class RefinedPipes {
    public static final String ID = "refinedpipes";
    public static final ItemGroup MAIN_GROUP = new MainItemGroup();

    public RefinedPipes() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private void clientSetup(final FMLClientSetupEvent event) {
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onRegisterBlocks(RegistryEvent.Register<Block> e) {
            e.getRegistry().register(new PipeBlock());
        }

        @SubscribeEvent
        public static void onRegisterItems(RegistryEvent.Register<Item> e) {
            e.getRegistry().register(new BlockItemBase(RefinedPipesBlocks.PIPE));
        }
    }
}
