package com.conflict.conflict;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = ConflictMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEvents {

    /**
     * При входе: если фракция не выбрана — открыть меню.
     * Если выбрана — синхронизировать команды и мгновенно (без мигания) телепортировать к точке спавна,
     * если она задана. В любом случае режим ставим SURVIVAL.
     */
    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer sp)) return;

        // гарантируем существование команд
        Scoreboards.ensureTeams();

        String f = Factions.get(sp);
        if (f == null) {
            // новичок — открыть экран выбора
            Network.CH.send(PacketDistributor.PLAYER.with(() -> sp), new PacketOpenFactionScreen());
            return;
        }

        // синхронизация с SavedData (безопасная)
        Factions.set(sp, f);

        // режим — выживание
        sp.setGameMode(GameType.SURVIVAL);

        // телепорт к точке спавна фракции (если задана)
        SafeTeleport.toTeamSpawn(sp, "BLUE".equals(f));
    }

    /**
     * При респавне: телепорт в радиус точки спавна фракции (если задана).
     * Откладываем на тик — безопаснее по прогрузке.
     */
    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer sp)) return;

        // режим — выживание
        sp.setGameMode(GameType.SURVIVAL);

        String f = Factions.get(sp);
        if (f == null) return;

        // отложим на тик, чтобы мир успел прогрузиться
        sp.server.execute(() -> SafeTeleport.toTeamSpawn(sp, "BLUE".equals(f)));
    }
}
