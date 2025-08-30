package com.conflict.conflict;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ConflictMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MineHandler {

    // Когда ломают зарегистрированный узел — планируем его восстановление через N секунд.
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent e){
        if (!(e.getLevel() instanceof ServerLevel lvl)) return;
        var data = OreNodesData.get(lvl);
        var pos = e.getPos();
        if (!data.isNode(pos)) return;

        // Ничего не отменяем: игрок получает обычный дроп.
        // Просто планируем восстановление исходного блока (default state) через n секунд.
        data.planRegen(lvl, pos);
    }

    // Раз в секунду проверяем все уровни: пора ли что-то восстановить
    private static int ticks = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent e){
        if (e.phase != TickEvent.Phase.END) return;
        ticks++;
        if (ticks % 20 != 0) return;

        MinecraftServer srv = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
        if (srv == null) return;

        for (ServerLevel lvl : srv.getAllLevels()){
            OreNodesData.get(lvl).tryRegen(lvl);
        }
    }
}
