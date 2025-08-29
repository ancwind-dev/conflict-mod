package com.conflict.conflict;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketGameState {
    public final int blue, red, timerSec, target;
    public final boolean running;

    public PacketGameState(int blue, int red, int timerSec, int target, boolean running){
        this.blue = blue; this.red = red; this.timerSec = timerSec; this.target = target; this.running = running;
    }

    public static void encode(PacketGameState m, FriendlyByteBuf b){
        b.writeVarInt(m.blue);
        b.writeVarInt(m.red);
        b.writeVarInt(m.timerSec);
        b.writeVarInt(m.target);
        b.writeBoolean(m.running);
    }

    public static PacketGameState decode(FriendlyByteBuf b){
        return new PacketGameState(b.readVarInt(), b.readVarInt(), b.readVarInt(), b.readVarInt(), b.readBoolean());
    }

    public static void handle(PacketGameState m, Supplier<NetworkEvent.Context> ctxSup){
        var ctx = ctxSup.get();
        ctx.enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHud.accept(m))
        );
        ctx.setPacketHandled(true);
    }
}
