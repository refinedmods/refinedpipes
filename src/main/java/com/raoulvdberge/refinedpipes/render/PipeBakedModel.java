package com.raoulvdberge.refinedpipes.render;

import com.raoulvdberge.refinedpipes.block.ItemPipeBlock;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentType;
import com.raoulvdberge.refinedpipes.tile.ItemPipeTileEntity;
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
    private final Map<Direction, IBakedModel> inventoryAttachmentModels = new HashMap<>();

    public PipeBakedModel(IBakedModel core, IBakedModel extension, IBakedModel straight, IBakedModel inventoryAttachment, Map<AttachmentType, IBakedModel> attachmentModels) {
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

        for (Direction direction : Direction.values()) {
            this.inventoryAttachmentModels.put(direction, new TrsrBakedModel(
                inventoryAttachment,
                direction
            ));
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
            boolean north = state.get(ItemPipeBlock.NORTH);
            boolean east = state.get(ItemPipeBlock.EAST);
            boolean south = state.get(ItemPipeBlock.SOUTH);
            boolean west = state.get(ItemPipeBlock.WEST);
            boolean up = state.get(ItemPipeBlock.UP);
            boolean down = state.get(ItemPipeBlock.DOWN);

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

                // TODO: cache extensions as well.
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

        Map<Direction, AttachmentType> attachments = extraData.getData(ItemPipeTileEntity.ATTACHMENTS_PROPERTY);
        if (attachments != null) {
            for (Map.Entry<Direction, AttachmentType> entry : attachments.entrySet()) {
                quads.addAll(attachmentModels.get(entry.getValue()).get(entry.getKey()).getQuads(state, side, rand, extraData));
            }
        }

        if (state != null) {
            boolean invNorth = state.get(ItemPipeBlock.INV_NORTH);
            boolean invEast = state.get(ItemPipeBlock.INV_EAST);
            boolean invSouth = state.get(ItemPipeBlock.INV_SOUTH);
            boolean invWest = state.get(ItemPipeBlock.INV_WEST);
            boolean invUp = state.get(ItemPipeBlock.INV_UP);
            boolean invDown = state.get(ItemPipeBlock.INV_DOWN);

            if (invNorth && (attachments == null || !attachments.containsKey(Direction.NORTH))) {
                quads.addAll(inventoryAttachmentModels.get(Direction.NORTH).getQuads(state, side, rand, EmptyModelData.INSTANCE));
            }

            if (invEast && (attachments == null || !attachments.containsKey(Direction.EAST))) {
                quads.addAll(inventoryAttachmentModels.get(Direction.EAST).getQuads(state, side, rand, EmptyModelData.INSTANCE));
            }

            if (invSouth && (attachments == null || !attachments.containsKey(Direction.SOUTH))) {
                quads.addAll(inventoryAttachmentModels.get(Direction.SOUTH).getQuads(state, side, rand, EmptyModelData.INSTANCE));
            }

            if (invWest && (attachments == null || !attachments.containsKey(Direction.WEST))) {
                quads.addAll(inventoryAttachmentModels.get(Direction.WEST).getQuads(state, side, rand, EmptyModelData.INSTANCE));
            }

            if (invUp && (attachments == null || !attachments.containsKey(Direction.UP))) {
                quads.addAll(inventoryAttachmentModels.get(Direction.UP).getQuads(state, side, rand, EmptyModelData.INSTANCE));
            }

            if (invDown && (attachments == null || !attachments.containsKey(Direction.DOWN))) {
                quads.addAll(inventoryAttachmentModels.get(Direction.DOWN).getQuads(state, side, rand, EmptyModelData.INSTANCE));
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
