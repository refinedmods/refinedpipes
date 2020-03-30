package com.raoulvdberge.refinedpipes.network.fluid;

import com.raoulvdberge.refinedpipes.network.Network;
import com.raoulvdberge.refinedpipes.network.NetworkFactory;
import com.raoulvdberge.refinedpipes.util.StringUtil;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class FluidNetworkFactory implements NetworkFactory {
    private static final Logger LOGGER = LogManager.getLogger(FluidNetworkFactory.class);

    @Override
    public Network create(BlockPos pos) {
        return new FluidNetwork(pos, StringUtil.randomString(new Random(), 8));
    }

    @Override
    public Network create(CompoundNBT tag) {
        FluidNetwork network = new FluidNetwork(BlockPos.fromLong(tag.getLong("origin")), tag.getString("id"));

        if (tag.contains("tank")) {
            network.getFluidTank().readFromNBT(tag.getCompound("tank"));
        }

        LOGGER.debug("Deserialized fluid network {}", network.getId());

        return network;
    }
}
