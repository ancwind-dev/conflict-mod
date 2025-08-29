package com.conflict.conflict;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = ConflictMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GameLogic {
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent e){
        if (!(e.getEntity() instanceof ServerPlayer victim)) return;
        var lvl = victim.serverLevel();
        GameSaved gs = GameSaved.get(lvl);
        if (!gs.running) return;

        String f = Factions.get(victim);
        if ("BLUE".equals(f)) gs.red++;
        else if ("RED".equals(f)) gs.blue++;
        else return;

        gs.mark();
        broadcastState(victim.getServer(), gs);
        checkWin(victim.getServer(), gs);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent e){
        if (e.phase != TickEvent.Phase.END) return;
        tickCounter++;
        if (tickCounter % 20 != 0) return; // раз в секунду

        MinecraftServer srv = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
        if (srv == null) return;

        var lvl = srv.overworld();
        GameSaved gs = GameSaved.get(lvl);

        if (gs.running) {
            if (gs.timerSec > 0) gs.timerSec--;
            gs.mark();
            broadcastState(srv, gs);
            if (gs.timerSec == 0) {
                announceWinner(srv, gs);
                gs.running = false;
                gs.mark();
                broadcastState(srv, gs);
            }
        }
    }

    private static void checkWin(MinecraftServer srv, GameSaved gs){
        if (gs.blue >= gs.target || gs.red >= gs.target) {
            announceWinner(srv, gs);
            gs.running = false;
            gs.mark();
        }
    }

    private static void announceWinner(MinecraftServer srv, GameSaved gs){
        String msg;
        if (gs.blue > gs.red) msg = "Победа BLUE! (" + gs.blue + " : " + gs.red + ")";
        else if (gs.red > gs.blue) msg = "Победа RED! (" + gs.red + " : " + gs.blue + ")";
        else msg = "Ничья! (" + gs.blue + " : " + gs.red + ")";
        srv.getPlayerList().broadcastSystemMessage(net.minecraft.network.chat.Component.literal(msg), false);
    }

    public static void broadcastState(MinecraftServer srv, GameSaved gs){
        for (ServerPlayer p : srv.getPlayerList().getPlayers()){
            Network.CH.send(
                    PacketDistributor.PLAYER.with(() -> p),
                    new PacketGameState(gs.blue, gs.red, gs.timerSec, gs.target, gs.running)
            );
        }
    }
}
