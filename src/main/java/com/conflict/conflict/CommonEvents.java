package com.conflict.conflict;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = ConflictMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEvents {

    /**
     * Показываем меню выбора фракции, если игрок ещё не выбран.
     * Иначе — синхронизируем команды с SavedData и ставим режим выживания.
     */
    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer sp)) return;

        // На всякий случай создадим команды
        Scoreboards.ensureTeams();

        // Читаем фракцию из SavedData/команд
        String f = Factions.get(sp);
        if (f == null) {
            // Новичок — открыть клиентский экран
            Network.CH.send(PacketDistributor.PLAYER.with(() -> sp), new PacketOpenFactionScreen());
            return;
        }

        // Синхронизируем команды с SavedData (безопасно)
        Factions.set(sp, f);

        // Всегда выживание (телепорт при респавне/выборе фракции разрулит позицию)
        sp.setGameMode(GameType.SURVIVAL);
    }

    /**
     * Респавн у точки фракции (если задана).
     * Телепорт откладываем на тик, чтобы гарантировать прогрузку высоты/чанка.
     */
    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer sp)) return;

        // Игровой режим держим в выживании
        sp.setGameMode(GameType.SURVIVAL);

        String f = Factions.get(sp);
        if (f == null) return;

        boolean blue = "BLUE".equals(f);
        BlockPos base = SpawnPoints.get(blue);
        if (base == null) return; // точки нет — обычный спавн

        // Отложим на 1 тик выполнение телепорта — безопаснее
        sp.server.execute(() -> {
            ServerLevel lvl = sp.serverLevel();
            int r = Math.max(0, SpawnPoints.radius(blue));
            BlockPos target = base.offset(
                    sp.getRandom().nextInt(r * 2 + 1) - r,
                    0,
                    sp.getRandom().nextInt(r * 2 + 1) - r
            );
            BlockPos safe = lvl.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, target);
            sp.teleportTo(lvl,
                    safe.getX() + 0.5,
                    safe.getY() + 0.2,
                    safe.getZ() + 0.5,
                    sp.getYRot(), sp.getXRot());
        });
    }
}
