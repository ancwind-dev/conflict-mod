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

            String val = (msg.choice == F.BLUE) ? "BLUE" : "RED";

            // 1) сохраняем фракцию и синхронизируем команды
            Factions.set(sp, val);
            boolean blue = "BLUE".equals(val);

            // 2) режим игры
            sp.setGameMode(GameType.SURVIVAL);

            // 3) телепорт к точке спавна (если задана), иначе покажем подсказку
            ServerLevel lvl = sp.serverLevel();
            if (SpawnPoints.has(lvl, blue)) {
                SafeTeleport.toTeamSpawn(sp, blue);
            } else {
                sp.displayClientMessage(Component.literal(
                        "Точка спавна вашей фракции пока не задана. Используйте /spawnpt set ..."), false);
            }

            // 4) РАССЫЛКА СКИНА — ключевой момент
            SkinBroadcaster.sendFor(sp);

            sp.displayClientMessage(Component.literal("Фракция выбрана: " + val), false);
        });
        ctx.setPacketHandled(true);
    }
}
