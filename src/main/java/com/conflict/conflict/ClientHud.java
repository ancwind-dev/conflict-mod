package com.conflict.conflict;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
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

        // форматируем таймер
        int m = Math.max(0, timerSec) / 60;
        int s = Math.max(0, timerSec) % 60;
        String time = String.format("%02d:%02d", m, s) + (running ? "" : " [PAUSED]");

        // верхний контейнер (фон)
        int boxW = 240;
        int boxH = 38;
        int x0 = (w - boxW) / 2;
        int y0 = 6;

        RenderSystem.disableDepthTest();
        // фон (полупрозрачный)
        g.fill(x0, y0, x0 + boxW, y0 + boxH, 0x88000000);

        // названия и счёт
        Component left  = Component.literal("BLUE " + blue).withStyle(net.minecraft.ChatFormatting.BLUE);
        Component right = Component.literal(red + " RED").withStyle(net.minecraft.ChatFormatting.RED);
        Component mid   = Component.literal(time).withStyle(net.minecraft.ChatFormatting.WHITE);

        int pad = 6;
        // слева
        g.drawString(mc.font, left, x0 + pad, y0 + 6, 0xFFFFFF, false);
        // справа
        int rightW = mc.font.width(right);
        g.drawString(mc.font, right, x0 + boxW - pad - rightW, y0 + 6, 0xFFFFFF, false);
        // центр (таймер жирно)
        g.drawString(mc.font, mid, x0 + (boxW - mc.font.width(mid))/2, y0 + 6, 0xFFFF55, true);

        // прогресс-полоса
        int barX = x0 + 10;
        int barY = y0 + 22;
        int barW = boxW - 20;
        int barH = 10;
        g.fill(barX, barY, barX + barW, barY + barH, 0xFF202020); // фон полосы

        // доли
        float total = Math.max(1, target);
        float blueFrac = Mth.clamp(blue / total, 0f, 1f);
        float redFrac  = Mth.clamp(red  / total, 0f, 1f);

        int blueW = Math.round(barW * blueFrac);
        int redW  = Math.round(barW * redFrac);

        // левая синяя часть
        if (blueW > 0) g.fill(barX, barY, barX + blueW, barY + barH, 0xFF3A66FF);
        // правая красная часть (заполняем справа налево)
        if (redW > 0)  g.fill(barX + barW - redW, barY, barX + barW, barY + barH, 0xFFFF3A3A);

        // граница
        g.fill(barX, barY, barX + barW, barY + 1, 0xFF000000);
        g.fill(barX, barY + barH - 1, barX + barW, barY + barH, 0xFF000000);
        g.fill(barX, barY, barX + 1, barY + barH, 0xFF000000);
        g.fill(barX + barW - 1, barY, barX + barW, barY + barH, 0xFF000000);

        RenderSystem.enableDepthTest();
    }
}
