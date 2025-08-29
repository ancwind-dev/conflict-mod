package com.conflict.conflict;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketChooseFaction {
    public enum F { BLUE, RED }

    private final F choice;

    public PacketChooseFaction(F c) {
        this.choice = c;
    }

    public static void encode(PacketChooseFaction m, FriendlyByteBuf buf) {
        buf.writeEnum(m.choice);
    }

    public static PacketChooseFaction decode(FriendlyByteBuf buf) {
        return new PacketChooseFaction(buf.readEnum(F.class));
    }

    public static void handle(PacketChooseFaction msg, Supplier<NetworkEvent.Context> ctxSup) {
        NetworkEvent.Context ctx = ctxSup.get();
        ctx.enqueueWork(() -> {
            ServerPlayer sp = ctx.getSender();
            if (sp == null) return;

            // 1) Фракция из выбора
            String val = (msg.choice == F.BLUE) ? "BLUE" : "RED";

            // 2) Сохраняем в SavedData и синхронизируем команды (через Factions.set)
            Factions.set(sp, val);
            boolean blue = "BLUE".equals(val);

            // 3) Всегда переводим в выживание
            sp.setGameMode(GameType.SURVIVAL);

            // 4) Если задана точка спавна — сразу ТП в радиусе, иначе просто остаёмся где есть
            var pos = SpawnPoints.get(blue);
            if (pos != null) {
                ServerLevel lvl = sp.serverLevel();
                int r = Math.max(0, SpawnPoints.radius(blue));
                var target = pos.offset(sp.getRandom().nextInt(r * 2 + 1) - r, 0,
                        sp.getRandom().nextInt(r * 2 + 1) - r);
                var safe = lvl.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, target);
                sp.teleportTo(lvl,
                        safe.getX() + 0.5,
                        safe.getY() + 0.2,
                        safe.getZ() + 0.5,
                        sp.getYRot(), sp.getXRot());
            } else {
                sp.displayClientMessage(Component.literal(
                        "Точка спавна вашей фракции пока не задана. Используйте /spawnpt set ..."), false);
            }

            sp.displayClientMessage(Component.literal("Фракция выбрана: " + val), false);
        });
        ctx.setPacketHandled(true);
    }
}
