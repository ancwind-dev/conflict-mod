package com.conflict.conflict;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ConflictMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommandEvents {
    @SubscribeEvent
    public static void onRegisterCommands(net.minecraftforge.event.RegisterCommandsEvent e){
        SpawnCommand.register(e.getDispatcher());
        FactionCommand.register(e.getDispatcher());
        ConflictAdminCommand.register(e.getDispatcher()); // ← ДОБАВИЛИ
    }
}



