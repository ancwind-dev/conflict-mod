package com.conflict.conflict;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ConflictMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientHud {
    private static volatile int blue = 0, red = 0, timerSec = 0, target = 50;
    private static volatile boolean running = false;

    static void accept(PacketGameState s){
        blue = s.blue; red = s.red; timerSec = s.timerSec; target = s.target; running = s.running;
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post e){
        var mc = Minecraft.getInstance();
        if (mc.player == null) return;

        GuiGraphics g = e.getGuiGraphics();
        int w = mc.getWindow().getGuiScaledWidth();

        // формат таймера
        int m = Math.max(0, timerSec) / 60;
        int s = Math.max(0, timerSec) % 60;
        String time = String.format("%02d:%02d", m, s);

        String line = "BLUE " + blue + " | " + red + " RED   " + time + (running ? "" : " [PAUSED]");
        var comp = Component.literal(line);

        // рисуем по центру сверху
        int textW = mc.font.width(comp);
        int x = (w - textW) / 2;
        int y = 6;

        RenderSystem.disableDepthTest();
        g.drawString(mc.font, comp, x, y, 0xFFFFFF, true);
        RenderSystem.enableDepthTest();
    }
}
