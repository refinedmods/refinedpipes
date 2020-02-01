package com.raoulvdberge.refinedpipes.render;

import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentType;
import com.raoulvdberge.refinedpipes.tile.PipeTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class PipeBakedModel implements IBakedModel {
    private final IBakedModel pipe;
    private final Map<AttachmentType, Map<Direction, IBakedModel>> attachmentModels = new HashMap<>();

    public PipeBakedModel(IBakedModel pipe, Map<AttachmentType, IBakedModel> attachmentModels) {
        this.pipe = pipe;

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
        return pipe.getQuads(state, side, rand);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        List<BakedQuad> quads = new ArrayList<>(pipe.getQuads(state, side, rand, extraData));

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
        return pipe.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return pipe.isGui3d();
    }

    @Override
    public boolean func_230044_c_() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return pipe.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return pipe.getParticleTexture();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return pipe.getOverrides();
    }
}
