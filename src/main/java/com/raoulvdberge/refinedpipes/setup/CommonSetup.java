package com.raoulvdberge.refinedpipes.setup;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.RefinedPipesBlocks;
import com.raoulvdberge.refinedpipes.block.PipeBlock;
import com.raoulvdberge.refinedpipes.item.AttachmentItem;
import com.raoulvdberge.refinedpipes.item.BlockItemBase;
import com.raoulvdberge.refinedpipes.network.NetworkManager;
import com.raoulvdberge.refinedpipes.network.pipe.PipeType;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentRegistry;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentType;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.ExtractorAttachmentType;
import com.raoulvdberge.refinedpipes.network.pipe.transport.callback.ItemBounceBackTransportCallback;
import com.raoulvdberge.refinedpipes.network.pipe.transport.callback.ItemInsertTransportCallback;
import com.raoulvdberge.refinedpipes.network.pipe.transport.callback.ItemPipeGoneTransportCallback;
import com.raoulvdberge.refinedpipes.network.pipe.transport.callback.TransportCallbackFactoryRegistry;
import com.raoulvdberge.refinedpipes.tile.PipeTileEntity;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommonSetup {
    public CommonSetup() {
        AttachmentRegistry.INSTANCE.addType(new ExtractorAttachmentType());

        TransportCallbackFactoryRegistry.INSTANCE.addFactory(ItemInsertTransportCallback.ID, ItemInsertTransportCallback::of);
        TransportCallbackFactoryRegistry.INSTANCE.addFactory(ItemBounceBackTransportCallback.ID, ItemBounceBackTransportCallback::of);
        TransportCallbackFactoryRegistry.INSTANCE.addFactory(ItemPipeGoneTransportCallback.ID, ItemPipeGoneTransportCallback::of);

        RefinedPipes.NETWORK.register();
    }

    @SubscribeEvent
    public void onRegisterBlocks(RegistryEvent.Register<Block> e) {
        e.getRegistry().register(new PipeBlock(PipeType.SIMPLE));
        e.getRegistry().register(new PipeBlock(PipeType.BASIC));
        e.getRegistry().register(new PipeBlock(PipeType.IMPROVED));
        e.getRegistry().register(new PipeBlock(PipeType.ADVANCED));
    }

    @SubscribeEvent
    public void onRegisterItems(RegistryEvent.Register<Item> e) {
        e.getRegistry().register(new BlockItemBase(RefinedPipesBlocks.SIMPLE_PIPE));
        e.getRegistry().register(new BlockItemBase(RefinedPipesBlocks.BASIC_PIPE));
        e.getRegistry().register(new BlockItemBase(RefinedPipesBlocks.IMPROVED_PIPE));
        e.getRegistry().register(new BlockItemBase(RefinedPipesBlocks.ADVANCED_PIPE));

        for (AttachmentType type : AttachmentRegistry.INSTANCE.getTypes()) {
            e.getRegistry().register(new AttachmentItem(type));
        }
    }

    @SubscribeEvent
    public void onRegisterTileEntities(RegistryEvent.Register<TileEntityType<?>> e) {
        e.getRegistry().register(TileEntityType.Builder.create(() -> new PipeTileEntity(PipeType.SIMPLE), RefinedPipesBlocks.SIMPLE_PIPE).build(null).setRegistryName(PipeType.SIMPLE.getId()));
        e.getRegistry().register(TileEntityType.Builder.create(() -> new PipeTileEntity(PipeType.BASIC), RefinedPipesBlocks.BASIC_PIPE).build(null).setRegistryName(PipeType.BASIC.getId()));
        e.getRegistry().register(TileEntityType.Builder.create(() -> new PipeTileEntity(PipeType.IMPROVED), RefinedPipesBlocks.IMPROVED_PIPE).build(null).setRegistryName(PipeType.IMPROVED.getId()));
        e.getRegistry().register(TileEntityType.Builder.create(() -> new PipeTileEntity(PipeType.ADVANCED), RefinedPipesBlocks.ADVANCED_PIPE).build(null).setRegistryName(PipeType.ADVANCED.getId()));
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent e) {
        if (!e.world.isRemote && e.phase == TickEvent.Phase.END) {
            NetworkManager.get(e.world).getNetworks().forEach(n -> n.update(e.world));
        }
    }
}
