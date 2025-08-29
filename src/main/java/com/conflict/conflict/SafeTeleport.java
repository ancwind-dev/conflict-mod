package com.conflict.conflict;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.concurrent.ThreadLocalRandom;

public final class SafeTeleport {
    private SafeTeleport() {}

    /**
     * Безопасный телепорт игрока к точке спавна своей фракции.
     * - гарантированно подгружает нужные чанки;
     * - ищет разумную высоту по heightmap;
     * - делает несколько попыток в радиусе;
     * - при провале — фоллбэк на общий спавн мира;
     * - сбрасывает fallDistance.
     */
    public static void toTeamSpawn(ServerPlayer sp, boolean blue) {
        ServerLevel lvl = sp.serverLevel();
        BlockPos base = SpawnPoints.get(lvl, blue);
        if (base == null) return; // точка не задана — ничего не делаем

        // 1) Подгружаем чанк базы
        forceLoadChunk(lvl, base);

        // 2) Пытаемся найти безопасную позицию в радиусе точки
        int r = Math.max(0, SpawnPoints.radius(lvl, blue));
        BlockPos target = null;

        for (int i = 0; i < 8; i++) {
            int dx = (r == 0) ? 0 : ThreadLocalRandom.current().nextInt(-r, r + 1);
            int dz = (r == 0) ? 0 : ThreadLocalRandom.current().nextInt(-r, r + 1);
            BlockPos candidate = base.offset(dx, 0, dz);

            forceLoadChunk(lvl, candidate);

            BlockPos safeTop = lvl.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, candidate);
            if (isReasonable(lvl, safeTop)) {
                target = safeTop;
                break;
            }
        }

        // 3) Фоллбэк: общий спавн мира
        if (target == null) {
            BlockPos spawn = lvl.getSharedSpawnPos();
            forceLoadChunk(lvl, spawn);
            BlockPos safeTop = lvl.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawn);
            target = isReasonable(lvl, safeTop) ? safeTop : spawn;
        }

        // 4) Телепорт + защита от "падения"
        sp.teleportTo(lvl,
                target.getX() + 0.5,
                target.getY() + 0.2,
                target.getZ() + 0.5,
                sp.getYRot(), sp.getXRot());
        sp.fallDistance = 0.0F;
    }

    private static boolean isReasonable(ServerLevel lvl, BlockPos pos) {
        return pos.getY() > lvl.getMinBuildHeight() && pos.getY() < lvl.getMaxBuildHeight();
    }

    private static void forceLoadChunk(ServerLevel lvl, BlockPos pos) {
        ChunkPos cp = new ChunkPos(pos);
        // Вызов гарантирует, что чанк будет загружен/сгенерирован к моменту использования
        lvl.getChunk(cp.x, cp.z);
    }
}
