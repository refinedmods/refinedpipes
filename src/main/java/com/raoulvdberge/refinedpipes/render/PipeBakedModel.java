package com.raoulvdberge.refinedpipes.render;

import com.raoulvdberge.refinedpipes.block.PipeBlock;
import com.raoulvdberge.refinedpipes.tile.PipeTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class PipeBakedModel implements IBakedModel {
    private final IBakedModel core;
    private final IBakedModel extension;
    private final IBakedModel straight;
    private final Map<ResourceLocation, Map<Direction, IBakedModel>> attachmentModels = new HashMap<>();
    private final Map<Direction, IBakedModel> inventoryAttachmentModels = new HashMap<>();
    private final Map<PipeState, List<BakedQuad>> cache = new HashMap<>();

    public PipeBakedModel(IBakedModel core, IBakedModel extension, IBakedModel straight, IBakedModel inventoryAttachment, Map<ResourceLocation, IBakedModel> attachmentModels) {
        this.core = core;
        this.extension = extension;
        this.straight = straight;

        for (Map.Entry<ResourceLocation, IBakedModel> entry : attachmentModels.entrySet()) {
            Map<Direction, IBakedModel> dirToModel = new HashMap<>();

            for (Direction dir : Direction.values()) {
                dirToModel.put(dir, new TrsrBakedModel(entry.getValue(), dir));
            }

            this.attachmentModels.put(entry.getKey(), dirToModel);
        }

        for (Direction direction : Direction.values()) {
            this.inventoryAttachmentModels.put(direction, new TrsrBakedModel(inventoryAttachment, direction));
        }
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        return getQuads(state, side, rand, EmptyModelData.INSTANCE);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        PipeState pipeState = new PipeState(state, extraData.getData(PipeTileEntity.ATTACHMENTS_PROPERTY), side, rand);

        return cache.computeIfAbsent(pipeState, this::createQuads);
    }

    private List<BakedQuad> createQuads(PipeState state) {
        List<BakedQuad> quads = new ArrayList<>();

        if (state.getState() != null) {
            boolean north = state.getState().get(PipeBlock.NORTH);
            boolean east = state.getState().get(PipeBlock.EAST);
            boolean south = state.getState().get(PipeBlock.SOUTH);
            boolean west = state.getState().get(PipeBlock.WEST);
            boolean up = state.getState().get(PipeBlock.UP);
            boolean down = state.getState().get(PipeBlock.DOWN);

            if (north && south && !east && !west && !up && !down) {
                quads.addAll(straight.getQuads(state.getState(), state.getSide(), state.getRand(), EmptyModelData.INSTANCE));
            } else if (!north && !south && east && west && !up && !down) {
                quads.addAll(new TrsrBakedModel(straight, Direction.EAST).getQuads(state.getState(), state.getSide(), state.getRand(), EmptyModelData.INSTANCE));
            } else if (!north && !south && !east && !west && up && down) {
                quads.addAll(new TrsrBakedModel(straight, Direction.UP).getQuads(state.getState(), state.getSide(), state.getRand(), EmptyModelData.INSTANCE));
            } else if (!north && !south && !east && !west && !up && !down) {
                quads.addAll(core.getQuads(state.getState(), state.getSide(), state.getRand(), EmptyModelData.INSTANCE));
            } else {
                quads.addAll(core.getQuads(state.getState(), state.getSide(), state.getRand(), EmptyModelData.INSTANCE));

                if (north) {
                    quads.addAll(extension.getQuads(state.getState(), state.getSide(), state.getRand(), EmptyModelData.INSTANCE));
                }

                if (east) {
                    quads.addAll(new TrsrBakedModel(extension, Direction.EAST).getQuads(state.getState(), state.getSide(), state.getRand(), EmptyModelData.INSTANCE));
                }

                if (south) {
                    quads.addAll(new TrsrBakedModel(extension, Direction.SOUTH).getQuads(state.getState(), state.getSide(), state.getRand(), EmptyModelData.INSTANCE));
                }

                if (west) {
                    quads.addAll(new TrsrBakedModel(extension, Direction.WEST).getQuads(state.getState(), state.getSide(), state.getRand(), EmptyModelData.INSTANCE));
                }

                if (up) {
                    quads.addAll(new TrsrBakedModel(extension, Direction.UP).getQuads(state.getState(), state.getSide(), state.getRand(), EmptyModelData.INSTANCE));
                }

                if (down) {
                    quads.addAll(new TrsrBakedModel(extension, Direction.DOWN).getQuads(state.getState(), state.getSide(), state.getRand(), EmptyModelData.INSTANCE));
                }
            }
        }

        if (state.getAttachmentState() != null) {
            for (Direction dir : Direction.values()) {
                ResourceLocation attachmentId = state.getAttachmentState()[dir.ordinal()];

                if (attachmentId != null) {
                    quads.addAll(attachmentModels.get(attachmentId).get(dir).getQuads(state.getState(), state.getSide(), state.getRand(), EmptyModelData.INSTANCE));
                }
            }
        }

        if (state.getState() != null) {
            boolean invNorth = state.getState().get(PipeBlock.INV_NORTH);
            boolean invEast = state.getState().get(PipeBlock.INV_EAST);
            boolean invSouth = state.getState().get(PipeBlock.INV_SOUTH);
            boolean invWest = state.getState().get(PipeBlock.INV_WEST);
            boolean invUp = state.getState().get(PipeBlock.INV_UP);
            boolean invDown = state.getState().get(PipeBlock.INV_DOWN);

            if (invNorth && !state.hasAttachmentState(Direction.NORTH)) {
                quads.addAll(inventoryAttachmentModels.get(Direction.NORTH).getQuads(state.getState(), state.getSide(), state.getRand(), EmptyModelData.INSTANCE));
            }

            if (invEast && !state.hasAttachmentState(Direction.EAST)) {
                quads.addAll(inventoryAttachmentModels.get(Direction.EAST).getQuads(state.getState(), state.getSide(), state.getRand(), EmptyModelData.INSTANCE));
            }

            if (invSouth && !state.hasAttachmentState(Direction.SOUTH)) {
                quads.addAll(inventoryAttachmentModels.get(Direction.SOUTH).getQuads(state.getState(), state.getSide(), state.getRand(), EmptyModelData.INSTANCE));
            }

            if (invWest && !state.hasAttachmentState(Direction.WEST)) {
                quads.addAll(inventoryAttachmentModels.get(Direction.WEST).getQuads(state.getState(), state.getSide(), state.getRand(), EmptyModelData.INSTANCE));
            }

            if (invUp && !state.hasAttachmentState(Direction.UP)) {
                quads.addAll(inventoryAttachmentModels.get(Direction.UP).getQuads(state.getState(), state.getSide(), state.getRand(), EmptyModelData.INSTANCE));
            }

            if (invDown && !state.hasAttachmentState(Direction.DOWN)) {
                quads.addAll(inventoryAttachmentModels.get(Direction.DOWN).getQuads(state.getState(), state.getSide(), state.getRand(), EmptyModelData.INSTANCE));
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
