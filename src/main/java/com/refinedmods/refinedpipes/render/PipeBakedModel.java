package com.refinedmods.refinedpipes.render;

import com.google.common.collect.ImmutableList;

import com.refinedmods.refinedpipes.block.PipeBlock;
import com.refinedmods.refinedpipes.tile.PipeTileEntity;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.TRSRTransformer;
import net.minecraftforge.common.model.TransformationHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class PipeBakedModel implements IBakedModel {
    private static final Map<Direction, TransformationMatrix> SIDE_TRANSFORMS = new EnumMap<>(Direction.class);
    private final IBakedModel core;
    private final IBakedModel extension;
    private final IBakedModel straight;
    private final IBakedModel inventoryAttachment;
    private final Map<ResourceLocation, IBakedModel> attachmentModels;
    private final Map<PipeState, List<BakedQuad>> cache = new HashMap<>();

    public PipeBakedModel(IBakedModel core, IBakedModel extension, IBakedModel straight, IBakedModel inventoryAttachment, Map<ResourceLocation, IBakedModel> attachmentModels) {
        this.core = core;
        this.extension = extension;
        this.straight = straight;
        this.inventoryAttachment = inventoryAttachment;
        this.attachmentModels = attachmentModels;
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
                quads.addAll(getTransformedQuads(straight, Direction.EAST, state));
            } else if (!north && !south && !east && !west && up && down) {
                quads.addAll(getTransformedQuads(straight, Direction.UP, state));
            } else if (!north && !south && !east && !west && !up && !down) {
                quads.addAll(core.getQuads(state.getState(), state.getSide(), state.getRand(), EmptyModelData.INSTANCE));
            } else {
                quads.addAll(core.getQuads(state.getState(), state.getSide(), state.getRand(), EmptyModelData.INSTANCE));

                if (north) {
                    quads.addAll(extension.getQuads(state.getState(), state.getSide(), state.getRand(), EmptyModelData.INSTANCE));
                }

                if (east) {
                    quads.addAll(getTransformedQuads(extension, Direction.EAST, state));
                }

                if (south) {
                    quads.addAll(getTransformedQuads(extension, Direction.SOUTH, state));
                }

                if (west) {
                    quads.addAll(getTransformedQuads(extension, Direction.WEST, state));
                }

                if (up) {
                    quads.addAll(getTransformedQuads(extension, Direction.UP, state));
                }

                if (down) {
                    quads.addAll(getTransformedQuads(extension, Direction.DOWN, state));
                }
            }
        }

        if (state.getAttachmentState() != null) {
            for (Direction dir : Direction.values()) {
                ResourceLocation attachmentId = state.getAttachmentState()[dir.ordinal()];

                if (attachmentId != null) {
                    quads.addAll(getTransformedQuads(attachmentModels.get(attachmentId), dir, state));
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
                quads.addAll(getTransformedQuads(inventoryAttachment, Direction.NORTH, state));
            }

            if (invEast && !state.hasAttachmentState(Direction.EAST)) {
                quads.addAll(getTransformedQuads(inventoryAttachment, Direction.EAST, state));
            }

            if (invSouth && !state.hasAttachmentState(Direction.SOUTH)) {
                quads.addAll(getTransformedQuads(inventoryAttachment, Direction.SOUTH, state));
            }

            if (invWest && !state.hasAttachmentState(Direction.WEST)) {
                quads.addAll(getTransformedQuads(inventoryAttachment, Direction.WEST, state));
            }

            if (invUp && !state.hasAttachmentState(Direction.UP)) {
                quads.addAll(getTransformedQuads(inventoryAttachment, Direction.UP, state));
            }

            if (invDown && !state.hasAttachmentState(Direction.DOWN)) {
                quads.addAll(getTransformedQuads(inventoryAttachment, Direction.DOWN, state));
            }
        }

        return quads;
    }

    private static List<BakedQuad> getTransformedQuads(IBakedModel model, Direction facing, PipeState state) {
        TransformationMatrix transformation = SIDE_TRANSFORMS.computeIfAbsent(facing, face -> {
            Quaternion quaternion;
            if (face == Direction.UP) {
                quaternion = TransformationHelper.quatFromXYZ(new Vector3f(90, 0, 0), true);
            } else if (face == Direction.DOWN) {
                quaternion = TransformationHelper.quatFromXYZ(new Vector3f(270, 0, 0), true);
            } else {
                double r = Math.PI * (360 - face.getOpposite().getHorizontalIndex() * 90) / 180d;

                quaternion = TransformationHelper.quatFromXYZ(new Vector3f(0, (float) r, 0), false);
            }

            return new TransformationMatrix(null, quaternion, null, null).blockCenterToCorner();
        });

        ImmutableList.Builder<BakedQuad> quads = ImmutableList.builder();
        Direction side = state.getSide();

        if (side != null && side.getHorizontalIndex() > -1) {
            int faceOffset = 4 + Direction.NORTH.getHorizontalIndex() - facing.getHorizontalIndex();
            side = Direction.byHorizontalIndex((side.getHorizontalIndex() + faceOffset) % 4);
        }

        for (BakedQuad quad : model.getQuads(state.getState(), side, state.getRand(), EmptyModelData.INSTANCE)) {
            BakedQuadBuilder builder = new BakedQuadBuilder(quad.func_187508_a());
            TRSRTransformer transformer = new TRSRTransformer(builder, transformation);

            quad.pipe(transformer);

            quads.add(builder.build());
        }

        return quads.build();
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
