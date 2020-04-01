package com.raoulvdberge.refinedpipes.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.Collections;

// https://github.com/mekanism/Mekanism/blob/9a3fe1a5d78bb38fa963a3bcbc8d3846412315e4/src/main/java/mekanism/common/util/MultipartUtils.java
public class Raytracer {
    public static Pair<Vec3d, Vec3d> getVectors(Entity entity) {
        float pitch = entity.rotationPitch;
        float yaw = entity.rotationYaw;
        Vec3d start = new Vec3d(entity.getPosX(), entity.getPosY() + entity.getEyeHeight(), entity.getPosZ());
        float f1 = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f3 = -MathHelper.cos(-pitch * 0.017453292F);
        float f4 = MathHelper.sin(-pitch * 0.017453292F);
        float f5 = f2 * f3;
        float f6 = f1 * f3;
        double d3 = 5.0D;
        if (entity instanceof ServerPlayerEntity) {
            d3 = ((ServerPlayerEntity) entity).getAttribute(PlayerEntity.REACH_DISTANCE).getValue();
        }
        Vec3d end = start.add(f5 * d3, f4 * d3, f6 * d3);
        return Pair.of(start, end);
    }

    public static AdvancedRayTraceResult<BlockRayTraceResult> collisionRayTrace(BlockPos pos, Vec3d start, Vec3d end, Collection<AxisAlignedBB> boxes) {
        double minDistance = Double.POSITIVE_INFINITY;
        AdvancedRayTraceResult<BlockRayTraceResult> hit = null;
        int i = -1;

        for (AxisAlignedBB aabb : boxes) {
            AdvancedRayTraceResult<BlockRayTraceResult> result = aabb == null ? null : collisionRayTrace(pos, start, end, aabb, i, null);
            if (result != null) {
                double d = result.squareDistanceTo(start);
                if (d < minDistance) {
                    minDistance = d;
                    hit = result;
                }
            }
            i++;
        }

        return hit;
    }

    public static AdvancedRayTraceResult<BlockRayTraceResult> collisionRayTrace(BlockPos pos, Vec3d start, Vec3d end, AxisAlignedBB bounds, int subHit, Object hitInfo) {
        BlockRayTraceResult result = AxisAlignedBB.rayTrace(Collections.singleton(bounds), start, end, pos);
        if (result == null) {
            return null;
        }

        result.subHit = subHit;
        result.hitInfo = hitInfo;

        return new AdvancedRayTraceResult<>(result, bounds);
    }

    public static class AdvancedRayTraceResult<T extends RayTraceResult> {
        public final AxisAlignedBB bounds;
        public final T hit;

        public AdvancedRayTraceResult(T mop, AxisAlignedBB aabb) {
            hit = mop;
            bounds = aabb;
        }

        public boolean valid() {
            return hit != null && bounds != null;
        }

        public double squareDistanceTo(Vec3d vec) {
            return hit.getHitVec().squareDistanceTo(vec);
        }
    }

    public static boolean inclusiveContains(AxisAlignedBB aabb, Vec3d hit) {
        return hit.x >= aabb.minX
            && hit.x <= aabb.maxX
            && hit.y >= aabb.minY
            && hit.y <= aabb.maxY
            && hit.z >= aabb.minZ
            && hit.z <= aabb.maxZ;
    }
}
