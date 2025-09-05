package com.conflict.conflict;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource; // ВАЖНО
import net.minecraft.world.level.levelgen.Heightmap;

public class SafeTeleport {

    public static void toTeamSpawn(ServerPlayer sp, boolean blue){
        ServerLevel lvl = sp.serverLevel();
        BlockPos base = SpawnPoints.get(lvl, blue);
        if (base == null) return;

        int r = Math.max(0, SpawnPoints.radius(lvl, blue));
        BlockPos target = pickSafe(lvl, base, r, sp.getRandom()); // RandomSource
        sp.teleportTo(lvl, target.getX() + 0.5, target.getY() + 0.2, target.getZ() + 0.5, sp.getYRot(), sp.getXRot());
    }

    private static BlockPos pickSafe(ServerLevel lvl, BlockPos base, int radius, RandomSource rnd){
        if (radius <= 0) {
            var surface = lvl.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, base);
            return new BlockPos(surface.getX(), surface.getY(), surface.getZ());
        }

        BlockPos best = null;
        for (int i = 0; i < 24; i++){
            int dx = rnd.nextInt(radius * 2 + 1) - radius;
            int dz = rnd.nextInt(radius * 2 + 1) - radius;
            BlockPos cand = base.offset(dx, 0, dz);

            var surface = lvl.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, cand);
            BlockPos pos = new BlockPos(surface.getX(), surface.getY(), surface.getZ());
            if (best == null) best = pos;
        }
        return best != null ? best : base;
    }
}
