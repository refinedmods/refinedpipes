package com.raoulvdberge.refinedpipes.network.energy;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.network.Network;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class EnergyNetwork extends Network {
    public static final ResourceLocation TYPE = new ResourceLocation(RefinedPipes.ID, "energy");

    public EnergyNetwork(BlockPos originPos, String id) {
        super(originPos, id);
    }

    @Override
    public void onMergedWith(Network mainNetwork) {

    }

    @Override
    public ResourceLocation getType() {
        return TYPE;
    }
}
