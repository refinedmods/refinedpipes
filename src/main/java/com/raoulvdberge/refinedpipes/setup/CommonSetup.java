package com.raoulvdberge.refinedpipes.setup;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.RefinedPipesBlocks;
import com.raoulvdberge.refinedpipes.block.EnergyPipeBlock;
import com.raoulvdberge.refinedpipes.block.FluidPipeBlock;
import com.raoulvdberge.refinedpipes.block.ItemPipeBlock;
import com.raoulvdberge.refinedpipes.item.AttachmentItem;
import com.raoulvdberge.refinedpipes.item.EnergyPipeBlockItem;
import com.raoulvdberge.refinedpipes.item.FluidPipeBlockItem;
import com.raoulvdberge.refinedpipes.item.ItemPipeBlockItem;
import com.raoulvdberge.refinedpipes.network.NetworkManager;
import com.raoulvdberge.refinedpipes.network.NetworkRegistry;
import com.raoulvdberge.refinedpipes.network.energy.EnergyNetwork;
import com.raoulvdberge.refinedpipes.network.energy.EnergyNetworkFactory;
import com.raoulvdberge.refinedpipes.network.fluid.FluidNetwork;
import com.raoulvdberge.refinedpipes.network.fluid.FluidNetworkFactory;
import com.raoulvdberge.refinedpipes.network.item.ItemNetwork;
import com.raoulvdberge.refinedpipes.network.item.ItemNetworkFactory;
import com.raoulvdberge.refinedpipes.network.pipe.PipeRegistry;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentRegistry;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentType;
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
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommonSetup {
    private final PipeShapeCache pipeShapeCache = new PipeShapeCache(new PipeShapeFactory());

    public CommonSetup() {
        NetworkRegistry.INSTANCE.addFactory(ItemNetwork.TYPE, new ItemNetworkFactory());
        NetworkRegistry.INSTANCE.addFactory(FluidNetwork.TYPE, new FluidNetworkFactory());
        NetworkRegistry.INSTANCE.addFactory(EnergyNetwork.TYPE, new EnergyNetworkFactory());

        PipeRegistry.INSTANCE.addFactory(ItemPipe.ID, new ItemPipeFactory());
        PipeRegistry.INSTANCE.addFactory(FluidPipe.ID, new FluidPipeFactory());
        PipeRegistry.INSTANCE.addFactory(EnergyPipe.ID, new EnergyPipeFactory());

        AttachmentRegistry.INSTANCE.addType(new ExtractorAttachmentType(ExtractorAttachmentType.Type.BASIC));
        AttachmentRegistry.INSTANCE.addType(new ExtractorAttachmentType(ExtractorAttachmentType.Type.IMPROVED));
        AttachmentRegistry.INSTANCE.addType(new ExtractorAttachmentType(ExtractorAttachmentType.Type.ADVANCED));
        AttachmentRegistry.INSTANCE.addType(new ExtractorAttachmentType(ExtractorAttachmentType.Type.ELITE));
        AttachmentRegistry.INSTANCE.addType(new ExtractorAttachmentType(ExtractorAttachmentType.Type.ULTIMATE));

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

        e.getRegistry().register(new EnergyPipeBlock(pipeShapeCache, EnergyPipeType.BASIC));
    }

    @SubscribeEvent
    public void onRegisterItems(RegistryEvent.Register<Item> e) {
        e.getRegistry().register(new ItemPipeBlockItem(RefinedPipesBlocks.BASIC_ITEM_PIPE));
        e.getRegistry().register(new ItemPipeBlockItem(RefinedPipesBlocks.IMPROVED_ITEM_PIPE));
        e.getRegistry().register(new ItemPipeBlockItem(RefinedPipesBlocks.ADVANCED_ITEM_PIPE));

        e.getRegistry().register(new FluidPipeBlockItem(RefinedPipesBlocks.BASIC_FLUID_PIPE));
        e.getRegistry().register(new FluidPipeBlockItem(RefinedPipesBlocks.IMPROVED_FLUID_PIPE));
        e.getRegistry().register(new FluidPipeBlockItem(RefinedPipesBlocks.ADVANCED_FLUID_PIPE));

        e.getRegistry().register(new EnergyPipeBlockItem(RefinedPipesBlocks.BASIC_ENERGY_PIPE));

        for (AttachmentType type : AttachmentRegistry.INSTANCE.getTypes()) {
            e.getRegistry().register(new AttachmentItem(type));
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

        e.getRegistry().register(TileEntityType.Builder.create(() -> new EnergyPipeTileEntity(EnergyPipeType.BASIC), RefinedPipesBlocks.BASIC_ENERGY_PIPE).build(null).setRegistryName(EnergyPipeType.BASIC.getId()));
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent e) {
        if (!e.world.isRemote && e.phase == TickEvent.Phase.END) {
            NetworkManager.get(e.world).getNetworks().forEach(n -> n.update(e.world));
        }
    }
}
