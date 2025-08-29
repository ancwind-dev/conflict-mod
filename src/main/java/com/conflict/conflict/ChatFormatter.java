package com.conflict.conflict;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ConflictMod.MODID)
public class ChatFormatter {

    @SubscribeEvent
    public static void onChat(ServerChatEvent e) {
        ServerPlayer sp = e.getPlayer();

        String f = Factions.get(sp);
        ChatFormatting color = ChatFormatting.GRAY;
        String tag = "[NEUTRAL] ";

        if ("BLUE".equals(f)) {
            color = ChatFormatting.BLUE;
            tag = "[BLUE] ";
        } else if ("RED".equals(f)) {
            color = ChatFormatting.RED;
            tag = "[RED] ";
        }

        // [TAG] Имя: сообщение
        Component formatted = Component.literal(tag).withStyle(color)
                .append(Component.literal(sp.getName().getString()).withStyle(color))
                .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                .append(e.getMessage());

        // Полностью берём на себя вывод: отменяем дефолт и рассылаем сами
        e.setCanceled(true);
        sp.getServer().getPlayerList().broadcastSystemMessage(formatted, false);
    }
}
