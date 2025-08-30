package com.conflict.conflict;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public class SkinBroadcaster {

    /** Разослать всем текущее состояние скина конкретного игрока */
    public static void sendFor(ServerPlayer sp){
        String f = Factions.get(sp);
        String skin = (f == null) ? "NONE" : f; // "BLUE"/"RED"/"NONE"
        Network.CH.send(PacketDistributor.ALL.noArg(), new PacketSkinUpdate(sp.getUUID(), skin));
    }

    /** Отправить присоединившемуся игроку скины всех остальных (чтобы он их сразу видел) */
    public static void syncAllTo(ServerPlayer receiver){
        for (ServerPlayer other : receiver.server.getPlayerList().getPlayers()){
            String f = Factions.get(other);
            String skin = (f == null) ? "NONE" : f;
            Network.CH.send(PacketDistributor.PLAYER.with(() -> receiver), new PacketSkinUpdate(other.getUUID(), skin));
        }
    }
}
