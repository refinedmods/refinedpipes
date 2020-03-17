package com.raoulvdberge.refinedpipes.render;

import com.raoulvdberge.refinedpipes.block.PipeBlock;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentType;
import com.raoulvdberge.refinedpipes.tile.PipeTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class PipeBakedModel implements IBakedModel {
    private final IBakedModel core;
    private final IBakedModel extension;
    private final IBakedModel straight;
    private final Map<AttachmentType, Map<Direction, IBakedModel>> attachmentModels = new HashMap<>();

    public PipeBakedModel(IBakedModel core, IBakedModel extension, IBakedModel straight, Map<AttachmentType, IBakedModel> attachmentModels) {
        this.core = core;
        this.extension = extension;
        this.straight = straight;

        for (Map.Entry<AttachmentType, IBakedModel> entry : attachmentModels.entrySet()) {
            Map<Direction, IBakedModel> dirToModel = new HashMap<>();

            for (Direction dir : Direction.values()) {
                dirToModel.put(dir, new TrsrBakedModel(
                    entry.getValue(),
                    dir
                ));
            }

            this.attachmentModels.put(entry.getKey(), dirToModel);
        }
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        return getQuads(state, side, rand, EmptyModelData.INSTANCE);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        List<BakedQuad> quads = new ArrayList<>();

        if (state != null) {
            boolean north = state.get(PipeBlock.NORTH);
            boolean east = state.get(PipeBlock.EAST);
            boolean south = state.get(PipeBlock.SOUTH);
            boolean west = state.get(PipeBlock.WEST);
            boolean up = state.get(PipeBlock.UP);
            boolean down = state.get(PipeBlock.DOWN);

            if (north && south && !east && !west && !up && !down) {
                quads.addAll(straight.getQuads(state, side, rand, EmptyModelData.INSTANCE));
            } else if (!north && !south && east && west && !up && !down) {
                quads.addAll(new TrsrBakedModel(straight, Direction.EAST).getQuads(state, side, rand, EmptyModelData.INSTANCE));
            } else if (!north && !south && !east && !west && up && down) {
                quads.addAll(new TrsrBakedModel(straight, Direction.UP).getQuads(state, side, rand, EmptyModelData.INSTANCE));
            } else if (!north && !south && !east && !west && !up && !down) {
                quads.addAll(core.getQuads(state, side, rand, EmptyModelData.INSTANCE));
            } else {
                quads.addAll(core.getQuads(state, side, rand, EmptyModelData.INSTANCE));

                if (north) {
                    quads.addAll(extension.getQuads(state, side, rand, EmptyModelData.INSTANCE));
                }

                if (east) {
                    quads.addAll(new TrsrBakedModel(extension, Direction.EAST).getQuads(state, side, rand, EmptyModelData.INSTANCE));
                }

                if (south) {
                    quads.addAll(new TrsrBakedModel(extension, Direction.SOUTH).getQuads(state, side, rand, EmptyModelData.INSTANCE));
                }

                if (west) {
                    quads.addAll(new TrsrBakedModel(extension, Direction.WEST).getQuads(state, side, rand, EmptyModelData.INSTANCE));
                }

                if (up) {
                    quads.addAll(new TrsrBakedModel(extension, Direction.UP).getQuads(state, side, rand, EmptyModelData.INSTANCE));
                }

                if (down) {
                    quads.addAll(new TrsrBakedModel(extension, Direction.DOWN).getQuads(state, side, rand, EmptyModelData.INSTANCE));
                }
            }
        }

        Map<Direction, AttachmentType> attachments = extraData.getData(PipeTileEntity.ATTACHMENTS_PROPERTY);
        if (attachments != null) {
            for (Map.Entry<Direction, AttachmentType> entry : attachments.entrySet()) {
                quads.addAll(attachmentModels.get(entry.getValue()).get(entry.getKey()).getQuads(state, side, rand, extraData));
            }
        }

        return quads;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return core.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return core.isGui3d();
    }

    @Override
    public boolean func_230044_c_() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return core.isBuiltInRenderer();
    }

    @Override
    @SuppressWarnings("deprecation")
    public TextureAtlasSprite getParticleTexture() {
        return core.getParticleTexture();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return core.getOverrides();
    }
}
