package com.conflict.conflict;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketChooseFaction {
    public enum F { BLUE, RED }

    private final F choice;

    public PacketChooseFaction(F c) { this.choice = c; }

    public static void encode(PacketChooseFaction m, FriendlyByteBuf buf) { buf.writeEnum(m.choice); }
    public static PacketChooseFaction decode(FriendlyByteBuf buf) { return new PacketChooseFaction(buf.readEnum(F.class)); }

    public static void handle(PacketChooseFaction msg, Supplier<NetworkEvent.Context> ctxSup) {
        NetworkEvent.Context ctx = ctxSup.get();
        ctx.enqueueWork(() -> {
            ServerPlayer sp = ctx.getSender();
            if (sp == null) return;

            // 1) фракция из выбора
            String val = (msg.choice == F.BLUE) ? "BLUE" : "RED";

            // 2) сохраняем в SavedData и синхронизируем команды
            Factions.set(sp, val);
            boolean blue = "BLUE".equals(val);

            // 3) сразу режим выживания
            sp.setGameMode(GameType.SURVIVAL);

            // 4) если точка спавна задана — безопасный телепорт; иначе покажем подсказку
            ServerLevel lvl = sp.serverLevel();
            if (SpawnPoints.has(lvl, blue)) {
                SafeTeleport.toTeamSpawn(sp, blue);
            } else {
                sp.displayClientMessage(Component.literal(
                        "Точка спавна вашей фракции пока не задана. Используйте /spawnpt set ..."), false);
            }

            sp.displayClientMessage(Component.literal("Фракция выбрана: " + val), false);
        });
        ctx.setPacketHandled(true);
    }
}
