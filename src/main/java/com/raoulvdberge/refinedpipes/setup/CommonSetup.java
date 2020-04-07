package com.raoulvdberge.refinedpipes.setup;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.RefinedPipesBlocks;
import com.raoulvdberge.refinedpipes.block.EnergyPipeBlock;
import com.raoulvdberge.refinedpipes.block.FluidPipeBlock;
import com.raoulvdberge.refinedpipes.block.ItemPipeBlock;
import com.raoulvdberge.refinedpipes.container.ExtractorAttachmentContainer;
import com.raoulvdberge.refinedpipes.container.factory.TileEntityContainerFactory;
import com.raoulvdberge.refinedpipes.item.AttachmentItem;
import com.raoulvdberge.refinedpipes.item.EnergyPipeBlockItem;
import com.raoulvdberge.refinedpipes.item.FluidPipeBlockItem;
import com.raoulvdberge.refinedpipes.item.ItemPipeBlockItem;
import com.raoulvdberge.refinedpipes.network.NetworkManager;
import com.raoulvdberge.refinedpipes.network.NetworkRegistry;
import com.raoulvdberge.refinedpipes.network.energy.EnergyNetworkFactory;
import com.raoulvdberge.refinedpipes.network.fluid.FluidNetworkFactory;
import com.raoulvdberge.refinedpipes.network.item.ItemNetwork;
import com.raoulvdberge.refinedpipes.network.item.ItemNetworkFactory;
import com.raoulvdberge.refinedpipes.network.pipe.PipeRegistry;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentFactory;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentRegistry;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.extractor.ExtractorAttachmentFactory;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.extractor.ExtractorAttachmentType;
import com.raoulvdberge.refinedpipes.network.pipe.energy.EnergyPipe;
import com.raoulvdberge.refinedpipes.network.pipe.energy.EnergyPipeFactory;
import com.raoulvdberge.refinedpipes.network.pipe.energy.EnergyPipeType;
import com.raoulvdberge.refinedpipes.network.pipe.fluid.FluidPipe;
import com.raoulvdberge.refinedpipes.network.pipe.fluid.FluidPipeFactory;
import com.raoulvdberge.refinedpipes.network.pipe.fluid.FluidPipeType;
import com.raoulvdberge.refinedpipes.network.pipe.item.ItemPipe;
import com.raoulvdberge.refinedpipes.network.pipe.item.ItemPipeFactory;
import com.raoulvdberge.refinedpipes.network.pipe.item.ItemPipeType;
import com.raoulvdberge.refinedpipes.network.pipe.shape.PipeShapeCache;
import com.raoulvdberge.refinedpipes.network.pipe.shape.PipeShapeFactory;
import com.raoulvdberge.refinedpipes.network.pipe.transport.callback.ItemBounceBackTransportCallback;
import com.raoulvdberge.refinedpipes.network.pipe.transport.callback.ItemInsertTransportCallback;
import com.raoulvdberge.refinedpipes.network.pipe.transport.callback.ItemPipeGoneTransportCallback;
import com.raoulvdberge.refinedpipes.network.pipe.transport.callback.TransportCallbackFactoryRegistry;
import com.raoulvdberge.refinedpipes.tile.EnergyPipeTileEntity;
import com.raoulvdberge.refinedpipes.tile.FluidPipeTileEntity;
import com.raoulvdberge.refinedpipes.tile.ItemPipeTileEntity;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommonSetup {
    private final PipeShapeCache pipeShapeCache = new PipeShapeCache(new PipeShapeFactory());

    public CommonSetup() {
        NetworkRegistry.INSTANCE.addFactory(ItemNetwork.TYPE, new ItemNetworkFactory());

        for (FluidPipeType pipeType : FluidPipeType.values()) {
            NetworkRegistry.INSTANCE.addFactory(pipeType.getNetworkType(), new FluidNetworkFactory(pipeType));
        }

        for (EnergyPipeType pipeType : EnergyPipeType.values()) {
            NetworkRegistry.INSTANCE.addFactory(pipeType.getNetworkType(), new EnergyNetworkFactory(pipeType));
        }

        PipeRegistry.INSTANCE.addFactory(ItemPipe.ID, new ItemPipeFactory());
        PipeRegistry.INSTANCE.addFactory(FluidPipe.ID, new FluidPipeFactory());
        PipeRegistry.INSTANCE.addFactory(EnergyPipe.ID, new EnergyPipeFactory());

        AttachmentRegistry.INSTANCE.addFactory(ExtractorAttachmentType.BASIC.getId(), new ExtractorAttachmentFactory(ExtractorAttachmentType.BASIC));
        AttachmentRegistry.INSTANCE.addFactory(ExtractorAttachmentType.IMPROVED.getId(), new ExtractorAttachmentFactory(ExtractorAttachmentType.IMPROVED));
        AttachmentRegistry.INSTANCE.addFactory(ExtractorAttachmentType.ADVANCED.getId(), new ExtractorAttachmentFactory(ExtractorAttachmentType.ADVANCED));
        AttachmentRegistry.INSTANCE.addFactory(ExtractorAttachmentType.ELITE.getId(), new ExtractorAttachmentFactory(ExtractorAttachmentType.ELITE));
        AttachmentRegistry.INSTANCE.addFactory(ExtractorAttachmentType.ULTIMATE.getId(), new ExtractorAttachmentFactory(ExtractorAttachmentType.ULTIMATE));

        TransportCallbackFactoryRegistry.INSTANCE.addFactory(ItemInsertTransportCallback.ID, ItemInsertTransportCallback::of);
        TransportCallbackFactoryRegistry.INSTANCE.addFactory(ItemBounceBackTransportCallback.ID, ItemBounceBackTransportCallback::of);
        TransportCallbackFactoryRegistry.INSTANCE.addFactory(ItemPipeGoneTransportCallback.ID, ItemPipeGoneTransportCallback::of);

        RefinedPipes.NETWORK.register();
    }

    @SubscribeEvent
    public void onRegisterBlocks(RegistryEvent.Register<Block> e) {
        e.getRegistry().register(new ItemPipeBlock(pipeShapeCache, ItemPipeType.BASIC));
        e.getRegistry().register(new ItemPipeBlock(pipeShapeCache, ItemPipeType.IMPROVED));
        e.getRegistry().register(new ItemPipeBlock(pipeShapeCache, ItemPipeType.ADVANCED));

        e.getRegistry().register(new FluidPipeBlock(pipeShapeCache, FluidPipeType.BASIC));
        e.getRegistry().register(new FluidPipeBlock(pipeShapeCache, FluidPipeType.IMPROVED));
        e.getRegistry().register(new FluidPipeBlock(pipeShapeCache, FluidPipeType.ADVANCED));
        e.getRegistry().register(new FluidPipeBlock(pipeShapeCache, FluidPipeType.ELITE));
        e.getRegistry().register(new FluidPipeBlock(pipeShapeCache, FluidPipeType.ULTIMATE));

        e.getRegistry().register(new EnergyPipeBlock(pipeShapeCache, EnergyPipeType.BASIC));
        e.getRegistry().register(new EnergyPipeBlock(pipeShapeCache, EnergyPipeType.IMPROVED));
        e.getRegistry().register(new EnergyPipeBlock(pipeShapeCache, EnergyPipeType.ADVANCED));
        e.getRegistry().register(new EnergyPipeBlock(pipeShapeCache, EnergyPipeType.ELITE));
        e.getRegistry().register(new EnergyPipeBlock(pipeShapeCache, EnergyPipeType.ULTIMATE));
    }

    @SubscribeEvent
    public void onRegisterItems(RegistryEvent.Register<Item> e) {
        e.getRegistry().register(new ItemPipeBlockItem(RefinedPipesBlocks.BASIC_ITEM_PIPE));
        e.getRegistry().register(new ItemPipeBlockItem(RefinedPipesBlocks.IMPROVED_ITEM_PIPE));
        e.getRegistry().register(new ItemPipeBlockItem(RefinedPipesBlocks.ADVANCED_ITEM_PIPE));

        e.getRegistry().register(new FluidPipeBlockItem(RefinedPipesBlocks.BASIC_FLUID_PIPE));
        e.getRegistry().register(new FluidPipeBlockItem(RefinedPipesBlocks.IMPROVED_FLUID_PIPE));
        e.getRegistry().register(new FluidPipeBlockItem(RefinedPipesBlocks.ADVANCED_FLUID_PIPE));
        e.getRegistry().register(new FluidPipeBlockItem(RefinedPipesBlocks.ELITE_FLUID_PIPE));
        e.getRegistry().register(new FluidPipeBlockItem(RefinedPipesBlocks.ULTIMATE_FLUID_PIPE));

        e.getRegistry().register(new EnergyPipeBlockItem(RefinedPipesBlocks.BASIC_ENERGY_PIPE));
        e.getRegistry().register(new EnergyPipeBlockItem(RefinedPipesBlocks.IMPROVED_ENERGY_PIPE));
        e.getRegistry().register(new EnergyPipeBlockItem(RefinedPipesBlocks.ADVANCED_ENERGY_PIPE));
        e.getRegistry().register(new EnergyPipeBlockItem(RefinedPipesBlocks.ELITE_ENERGY_PIPE));
        e.getRegistry().register(new EnergyPipeBlockItem(RefinedPipesBlocks.ULTIMATE_ENERGY_PIPE));

        for (AttachmentFactory factory : AttachmentRegistry.INSTANCE.all()) {
            e.getRegistry().register(new AttachmentItem(factory));
        }
    }

    @SubscribeEvent
    public void onRegisterTileEntities(RegistryEvent.Register<TileEntityType<?>> e) {
        e.getRegistry().register(TileEntityType.Builder.create(() -> new ItemPipeTileEntity(ItemPipeType.BASIC), RefinedPipesBlocks.BASIC_ITEM_PIPE).build(null).setRegistryName(ItemPipeType.BASIC.getId()));
        e.getRegistry().register(TileEntityType.Builder.create(() -> new ItemPipeTileEntity(ItemPipeType.IMPROVED), RefinedPipesBlocks.IMPROVED_ITEM_PIPE).build(null).setRegistryName(ItemPipeType.IMPROVED.getId()));
        e.getRegistry().register(TileEntityType.Builder.create(() -> new ItemPipeTileEntity(ItemPipeType.ADVANCED), RefinedPipesBlocks.ADVANCED_ITEM_PIPE).build(null).setRegistryName(ItemPipeType.ADVANCED.getId()));

        e.getRegistry().register(TileEntityType.Builder.create(() -> new FluidPipeTileEntity(FluidPipeType.BASIC), RefinedPipesBlocks.BASIC_FLUID_PIPE).build(null).setRegistryName(FluidPipeType.BASIC.getId()));
        e.getRegistry().register(TileEntityType.Builder.create(() -> new FluidPipeTileEntity(FluidPipeType.IMPROVED), RefinedPipesBlocks.IMPROVED_FLUID_PIPE).build(null).setRegistryName(FluidPipeType.IMPROVED.getId()));
        e.getRegistry().register(TileEntityType.Builder.create(() -> new FluidPipeTileEntity(FluidPipeType.ADVANCED), RefinedPipesBlocks.ADVANCED_FLUID_PIPE).build(null).setRegistryName(FluidPipeType.ADVANCED.getId()));
        e.getRegistry().register(TileEntityType.Builder.create(() -> new FluidPipeTileEntity(FluidPipeType.ELITE), RefinedPipesBlocks.ELITE_FLUID_PIPE).build(null).setRegistryName(FluidPipeType.ELITE.getId()));
        e.getRegistry().register(TileEntityType.Builder.create(() -> new FluidPipeTileEntity(FluidPipeType.ULTIMATE), RefinedPipesBlocks.ULTIMATE_FLUID_PIPE).build(null).setRegistryName(FluidPipeType.ULTIMATE.getId()));

        e.getRegistry().register(TileEntityType.Builder.create(() -> new EnergyPipeTileEntity(EnergyPipeType.BASIC), RefinedPipesBlocks.BASIC_ENERGY_PIPE).build(null).setRegistryName(EnergyPipeType.BASIC.getId()));
        e.getRegistry().register(TileEntityType.Builder.create(() -> new EnergyPipeTileEntity(EnergyPipeType.IMPROVED), RefinedPipesBlocks.IMPROVED_ENERGY_PIPE).build(null).setRegistryName(EnergyPipeType.IMPROVED.getId()));
        e.getRegistry().register(TileEntityType.Builder.create(() -> new EnergyPipeTileEntity(EnergyPipeType.ADVANCED), RefinedPipesBlocks.ADVANCED_ENERGY_PIPE).build(null).setRegistryName(EnergyPipeType.ADVANCED.getId()));
        e.getRegistry().register(TileEntityType.Builder.create(() -> new EnergyPipeTileEntity(EnergyPipeType.ELITE), RefinedPipesBlocks.ELITE_ENERGY_PIPE).build(null).setRegistryName(EnergyPipeType.ELITE.getId()));
        e.getRegistry().register(TileEntityType.Builder.create(() -> new EnergyPipeTileEntity(EnergyPipeType.ULTIMATE), RefinedPipesBlocks.ULTIMATE_ENERGY_PIPE).build(null).setRegistryName(EnergyPipeType.ULTIMATE.getId()));
    }

    @SubscribeEvent
    public void onRegisterContainers(RegistryEvent.Register<ContainerType<?>> e) {
        e.getRegistry().register(IForgeContainerType.create(
            new TileEntityContainerFactory<>(
                (windowId, inv, tile) -> new ExtractorAttachmentContainer(windowId, inv.player)
            )
        ).setRegistryName(RefinedPipes.ID, "extractor_attachment"));
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent e) {
        if (!e.world.isRemote && e.phase == TickEvent.Phase.END) {
            NetworkManager.get(e.world).getNetworks().forEach(n -> n.update(e.world));
        }
    }
}
