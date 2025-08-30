package com.conflict.conflict;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = ConflictMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEvents {

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer sp)) return;

        // на всякий случай — существование команд в табло
        Scoreboards.ensureTeams();

        String f = Factions.get(sp);
        if (f == null) {
            // новичок — открыть экран выбора
            Network.CH.send(PacketDistributor.PLAYER.with(() -> sp), new PacketOpenFactionScreen());
            // новенькому сразу отсылаем скины всех остальных
            SkinBroadcaster.syncAllTo(sp);
            return;
        }

        // синхронизируем команду (складём в SavedData + в табло)
        Factions.set(sp, f);

        // режим — выживание
        sp.setGameMode(GameType.SURVIVAL);

        // телепорт к базе (если задана)
        SafeTeleport.toTeamSpawn(sp, "BLUE".equals(f));

        // всем рассказать, какой скин у вошедшего
        SkinBroadcaster.sendFor(sp);
        // и вошедшему — скины остальных
        SkinBroadcaster.syncAllTo(sp);
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer sp)) return;

        sp.setGameMode(GameType.SURVIVAL);

        String f = Factions.get(sp);
        if (f == null) return;

        sp.server.execute(() -> {
            SafeTeleport.toTeamSpawn(sp, "BLUE".equals(f));
            // (киты будем выдавать позже)
        });
    }
}
