package com.refinedmods.refinedpipes.network.fluid;

import com.refinedmods.refinedpipes.network.Network;
import com.refinedmods.refinedpipes.network.NetworkFactory;
import com.refinedmods.refinedpipes.network.pipe.fluid.FluidPipeType;
import com.refinedmods.refinedpipes.util.StringUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class FluidNetworkFactory implements NetworkFactory {
    private static final Logger LOGGER = LogManager.getLogger(FluidNetworkFactory.class);

    private final FluidPipeType pipeType;

    public FluidNetworkFactory(FluidPipeType pipeType) {
        this.pipeType = pipeType;
    }

    @Override
    public Network create(BlockPos pos) {
        return new FluidNetwork(pos, StringUtil.randomString(new Random(), 8), pipeType);
    }

    @Override
    public Network create(CompoundTag tag) {
        FluidNetwork network = new FluidNetwork(BlockPos.of(tag.getLong("origin")), tag.getString("id"), pipeType);

        if (tag.contains("tank")) {
            network.getFluidTank().readFromNBT(tag.getCompound("tank"));
        }

        LOGGER.debug("Deserialized fluid network {} of type {}", network.getId(), network.getType().toString());

        return network;
    }
}
