package com.conflict.conflict;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ConflictMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DamageGuard {

    @SubscribeEvent
    public static void onFriendlyFire(LivingHurtEvent e){
        if (!(e.getEntity() instanceof Player victim)) return;
        Entity srcEnt = e.getSource().getEntity();
        if (!(srcEnt instanceof Player attacker)) return;

        // обе стороны — игроки; если в одной фракции — отменяем урон
        var v = (net.minecraft.server.level.ServerPlayer) victim;
        var a = (net.minecraft.server.level.ServerPlayer) attacker;
        String fv = Factions.get(v);
        String fa = Factions.get(a);
        if (fv != null && fv.equals(fa)) {
            e.setCanceled(true);
        }
    }
}
