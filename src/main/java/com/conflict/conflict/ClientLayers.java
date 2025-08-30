package com.conflict.conflict;

import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Регистрируем наш слой поверх рендера игрока для обоих моделей: "default" (wide) и "slim".
 */
@Mod.EventBusSubscriber(modid = ConflictMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientLayers {

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        // ЯВНО задаём тип PlayerRenderer (без var)
        PlayerRenderer def = event.getSkin("default");
        if (def != null) {
            def.addLayer(new FactionSkinLayer(def));
        }

        PlayerRenderer slim = event.getSkin("slim");
        if (slim != null) {
            slim.addLayer(new FactionSkinLayer(slim));
        }
    }
}
