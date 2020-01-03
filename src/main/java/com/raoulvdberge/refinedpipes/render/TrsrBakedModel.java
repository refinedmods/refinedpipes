package com.raoulvdberge.refinedpipes.render;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.client.model.pipeline.VertexTransformer;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.annotation.Nonnull;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.List;
import java.util.Random;

public class TrsrBakedModel implements IBakedModel {
    protected final IBakedModel original;
    protected TRSRTransformation transformation;

    public TrsrBakedModel(IBakedModel original, float x, float y, float z, float scale) {
        this(original, x, y, z, 0, 0, 0, scale, scale, scale);
    }

    public TrsrBakedModel(IBakedModel original, float x, float y, float z, float rotX, float rotY, float rotZ, float scale) {
        this(original, x, y, z, rotX, rotY, rotZ, scale, scale, scale);
    }

    public TrsrBakedModel(IBakedModel original, float x, float y, float z, float rotX, float rotY, float rotZ, float scaleX, float scaleY, float scaleZ) {
        this(original, new TRSRTransformation(
            new Vector3f(x, y, z),
            null,
            new Vector3f(scaleX, scaleY, scaleZ),
            TRSRTransformation.quatFromXYZ(rotX, rotY, rotZ)
        ));
    }

    public TrsrBakedModel(IBakedModel original, TRSRTransformation transform) {
        this.original = original;
        this.transformation = TRSRTransformation.blockCenterToCorner(transform);
    }

    public TrsrBakedModel(IBakedModel original, Direction facing) {
        this.original = original;

        if (facing.getHorizontalIndex() > -1) {
            double r = Math.PI * (360 - facing.getOpposite().getHorizontalIndex() * 90) / 180d;
            TRSRTransformation t = new TRSRTransformation(null, null, null, TRSRTransformation.quatFromXYZ(0, (float) r, 0));
            this.transformation = TRSRTransformation.blockCenterToCorner(t);
        } else {
            double r = Math.PI * (360 - (facing == Direction.DOWN ? 90 : 270)) / 180d;
            TRSRTransformation t = new TRSRTransformation(null, null, null, TRSRTransformation.quatFromXYZ((float) r, 0, 0));
            this.transformation = TRSRTransformation.blockCenterToCorner(t);
        }
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

        for (BakedQuad quad : original.getQuads(state, side, rand)) {
            Transformer transformer = new Transformer(transformation, quad.getFormat());
            quad.pipe(transformer);
            builder.add(transformer.build());
        }

        return builder.build();
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return original.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return original.isBuiltInRenderer();
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public TextureAtlasSprite getParticleTexture() {
        return original.getParticleTexture();
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public ItemCameraTransforms getItemCameraTransforms() {
        return original.getItemCameraTransforms();
    }

    @Nonnull
    @Override
    public ItemOverrideList getOverrides() {
        return original.getOverrides();
    }

    private static class Transformer extends VertexTransformer {
        protected Matrix4f transformation;
        protected Matrix3f normalTransformation;

        public Transformer(TRSRTransformation transformation, VertexFormat format) {
            super(new UnpackedBakedQuad.Builder(format));

            this.transformation = transformation.getMatrixVec();

            this.normalTransformation = new Matrix3f();
            this.transformation.getRotationScale(this.normalTransformation);
            this.normalTransformation.invert();
            this.normalTransformation.transpose();
        }

        @Override
        public void put(int element, float... data) {
            VertexFormatElement.Usage usage = parent.getVertexFormat().getElement(element).getUsage();

            if (usage == VertexFormatElement.Usage.POSITION && data.length >= 3) {
                Vector4f vec = new Vector4f(data[0], data[1], data[2], 1f);
                transformation.transform(vec);
                data = new float[4];
                vec.get(data);
            } else if (usage == VertexFormatElement.Usage.NORMAL && data.length >= 3) {
                Vector3f vec = new Vector3f(data);
                normalTransformation.transform(vec);
                vec.normalize();
                data = new float[4];
                vec.get(data);
            }

            super.put(element, data);
        }

        public UnpackedBakedQuad build() {
            return ((UnpackedBakedQuad.Builder) parent).build();
        }
    }
}
