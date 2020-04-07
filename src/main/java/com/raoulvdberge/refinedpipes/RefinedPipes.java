package com.raoulvdberge.refinedpipes;

import com.raoulvdberge.refinedpipes.config.ServerConfig;
import com.raoulvdberge.refinedpipes.item.group.MainItemGroup;
import com.raoulvdberge.refinedpipes.setup.ClientSetup;
import com.raoulvdberge.refinedpipes.setup.CommonSetup;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
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
    public static final ItemGroup MAIN_GROUP = new MainItemGroup();
    public static final RefinedPipesNetwork NETWORK = new RefinedPipesNetwork();
    public static final ServerConfig SERVER_CONFIG = new ServerConfig();

    public RefinedPipes() {
        CommonSetup commonSetup = new CommonSetup();

        DistExecutor.runWhenOn(Dist.CLIENT, () -> ClientSetup::new);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG.getSpec());

        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, commonSetup::onRegisterBlocks);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, commonSetup::onRegisterItems);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(TileEntityType.class, commonSetup::onRegisterTileEntities);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(ContainerType.class, commonSetup::onRegisterContainers);

        MinecraftForge.EVENT_BUS.addListener(commonSetup::onWorldTick);
    }
}
