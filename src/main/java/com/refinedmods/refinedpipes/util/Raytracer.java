package com.refinedmods.refinedpipes.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.Collections;

// https://github.com/mekanism/Mekanism/blob/9a3fe1a5d78bb38fa963a3bcbc8d3846412315e4/src/main/java/mekanism/common/util/MultipartUtils.java
public class Raytracer {
    public static Pair<Vec3, Vec3> getVectors(Entity entity) {
        float pitch = entity.getXRot();
        float yaw = entity.getYRot();
        Vec3 start = new Vec3(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ());
        float f1 = Mth.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = Mth.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f3 = -Mth.cos(-pitch * 0.017453292F);
        float f4 = Mth.sin(-pitch * 0.017453292F);
        float f5 = f2 * f3;
        float f6 = f1 * f3;
        double d3 = 5.0D;
        if (entity instanceof ServerPlayer) {
            d3 = ((ServerPlayer) entity).getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue();
        }
        Vec3 end = start.add(f5 * d3, f4 * d3, f6 * d3);
        return Pair.of(start, end);
    }

    public static AdvancedRayTraceResult<BlockHitResult> collisionRayTrace(BlockPos pos, Vec3 start, Vec3 end, Collection<AABB> boxes) {
        double minDistance = Double.POSITIVE_INFINITY;
        AdvancedRayTraceResult<BlockHitResult> hit = null;
        int i = -1;

        for (AABB aabb : boxes) {
            AdvancedRayTraceResult<BlockHitResult> result = aabb == null ? null : collisionRayTrace(pos, start, end, aabb, i, null);
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

    public static AdvancedRayTraceResult<BlockHitResult> collisionRayTrace(BlockPos pos, Vec3 start, Vec3 end, AABB bounds, int subHit, Object hitInfo) {
        BlockHitResult result = AABB.clip(Collections.singleton(bounds), start, end, pos);
        if (result == null) {
            return null;
        }
        return new AdvancedRayTraceResult<>(result, bounds);
    }

    public static boolean inclusiveContains(AABB aabb, Vec3 hit) {
        return hit.x >= aabb.minX
            && hit.x <= aabb.maxX
            && hit.y >= aabb.minY
            && hit.y <= aabb.maxY
            && hit.z >= aabb.minZ
            && hit.z <= aabb.maxZ;
    }

    public static class AdvancedRayTraceResult<T extends HitResult> {
        public final AABB bounds;
        public final T hit;

        public AdvancedRayTraceResult(T mop, AABB aabb) {
            hit = mop;
            bounds = aabb;
        }

        public double squareDistanceTo(Vec3 vec) {
            return hit.getLocation().distanceToSqr(vec);
        }
    }
}
