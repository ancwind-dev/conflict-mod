package com.conflict.conflict;

import net.minecraft.resources.ResourceLocation;                 // ← ИМЕННО этот импорт
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;          // ← ВАЖНО: simple.SimpleChannel

public class Network {
    private static final String PROTO = "1";
    public static SimpleChannel CH;

    public static void init() {
        CH = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(ConflictMod.MODID, "main"),
                () -> PROTO, PROTO::equals, PROTO::equals
        );
        CH.registerMessage(0, PacketChooseFaction.class,
                PacketChooseFaction::encode, PacketChooseFaction::decode, PacketChooseFaction::handle);
        CH.registerMessage(1, PacketOpenFactionScreen.class,
                PacketOpenFactionScreen::encode, PacketOpenFactionScreen::decode, PacketOpenFactionScreen::handle);
    }
}

