package com.conflict.conflict;

import net.minecraft.network.chat.Component;
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

        // Убедимся, что команды в табло существуют
        Scoreboards.ensureTeams();

        String f = Factions.get(sp);
        if (f == null) {
            // Новичок/без фракции — откроем экран выбора
            Network.CH.send(PacketDistributor.PLAYER.with(() -> sp), new PacketOpenFactionScreen());

            // Новенькому сразу отправим скины всех остальных
            SkinBroadcaster.syncAllTo(sp);
            return;
        }

        // Синхронизируем табло + режим
        Factions.set(sp, f);
        sp.setGameMode(GameType.SURVIVAL);

        // Телепорт к базе, если задана
        SafeTeleport.toTeamSpawn(sp, "BLUE".equals(f));

        // Выдать КИТ фракции (очищаем инвентарь перед выдачей)
        Loadouts.giveFor(sp, "BLUE".equals(f), true);

        // Всем — какая у игрока "текстура фракции"
        SkinBroadcaster.sendFor(sp);
        // Вошедшему — скины всех остальных
        SkinBroadcaster.syncAllTo(sp);
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer sp)) return;

        sp.setGameMode(GameType.SURVIVAL);

        String f = Factions.get(sp);
        if (f == null) {
            // Без фракции — просто подскажем выбрать
            sp.displayClientMessage(Component.literal("Выберите фракцию командой /faction"), false);
            return;
        }

        // Переносим на базу и выдаём кит — в главном треде сервера
        sp.server.execute(() -> {
            SafeTeleport.toTeamSpawn(sp, "BLUE".equals(f));
            Loadouts.giveFor(sp, "BLUE".equals(f), true);
        });
    }
}
