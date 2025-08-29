package com.conflict.conflict;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketOpenFactionScreen {
    public static void encode(PacketOpenFactionScreen m, FriendlyByteBuf b) {}
    public static PacketOpenFactionScreen decode(FriendlyByteBuf b){ return new PacketOpenFactionScreen(); }

    public static void handle(PacketOpenFactionScreen m, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(PacketOpenFactionScreen::openClient);
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void openClient() {
        Minecraft.getInstance().setScreen(new FactionScreen());
    }
}

