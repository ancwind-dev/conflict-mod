package com.conflict.conflict;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class PacketSkinUpdate {
    public final UUID uuid;
    public final String skin; // "BLUE", "RED", "NONE"

    public PacketSkinUpdate(UUID uuid, String skin) {
        this.uuid = uuid;
        this.skin = skin;
    }

    public static void encode(PacketSkinUpdate m, FriendlyByteBuf b) {
        b.writeUUID(m.uuid);
        b.writeUtf(m.skin);
    }

    public static PacketSkinUpdate decode(FriendlyByteBuf b) {
        return new PacketSkinUpdate(b.readUUID(), b.readUtf());
    }

    public static void handle(PacketSkinUpdate m, Supplier<NetworkEvent.Context> ctxSup) {
        var ctx = ctxSup.get();
        ctx.enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientSkins.apply(m.uuid, m.skin))
        );
        ctx.setPacketHandled(true);
    }
}
