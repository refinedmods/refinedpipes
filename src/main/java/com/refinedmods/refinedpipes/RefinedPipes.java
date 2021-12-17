package com.refinedmods.refinedpipes;

import com.refinedmods.refinedpipes.config.ServerConfig;
import com.refinedmods.refinedpipes.item.creativetab.MainCreativeModeTab;
import com.refinedmods.refinedpipes.setup.ClientSetup;
import com.refinedmods.refinedpipes.setup.CommonSetup;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RefinedPipes.ID)
public class RefinedPipes {
    public static final String ID = "refinedpipes";
    public static final CreativeModeTab MAIN_GROUP = new MainCreativeModeTab();
    public static final RefinedPipesNetwork NETWORK = new RefinedPipesNetwork();
    public static final ServerConfig SERVER_CONFIG = new ServerConfig();

    public RefinedPipes() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::onClientSetup);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::onModelBake);
        });

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG.getSpec());

        FMLJavaModLoadingContext.get().getModEventBus().addListener(CommonSetup::onConstructMod);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CommonSetup::onCommonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, CommonSetup::onRegisterBlocks);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, CommonSetup::onRegisterItems);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(BlockEntityType.class, CommonSetup::onRegisterBlockEntities);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(MenuType.class, CommonSetup::onRegisterContainerMenus);

        MinecraftForge.EVENT_BUS.addListener(CommonSetup::onLevelTick);
    }
}
