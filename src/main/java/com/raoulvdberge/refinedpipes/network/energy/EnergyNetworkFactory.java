package com.raoulvdberge.refinedpipes.network.energy;

import com.raoulvdberge.refinedpipes.network.Network;
import com.raoulvdberge.refinedpipes.network.NetworkFactory;
import com.raoulvdberge.refinedpipes.util.StringUtil;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class EnergyNetworkFactory implements NetworkFactory {
    private static final Logger LOGGER = LogManager.getLogger(EnergyNetworkFactory.class);

    @Override
    public Network create(BlockPos pos) {
        return new EnergyNetwork(pos, StringUtil.randomString(new Random(), 8));
    }

    @Override
    public Network create(CompoundNBT tag) {
        EnergyNetwork network = new EnergyNetwork(BlockPos.fromLong(tag.getLong("origin")), tag.getString("id"));

        LOGGER.debug("Deserialized energy network {}", network.getId());

        return network;
    }
}
