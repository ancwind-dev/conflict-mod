package com.conflict.conflict;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ConflictMod.MODID)
public class SpawnHandler {

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer sp)) return;
        String f = Factions.get(sp);
        if (f == null) return;

        boolean blue = "BLUE".equals(f);
        sp.setGameMode(GameType.SURVIVAL);

        var pos = SpawnPoints.get(blue);
        if (pos == null) return; // нет точки — обычный спавн

        ServerLevel lvl = sp.serverLevel();
        int r = Math.max(0, SpawnPoints.radius(blue));
        var target = pos.offset(sp.getRandom().nextInt(r * 2 + 1) - r, 0, sp.getRandom().nextInt(r * 2 + 1) - r);
        var safe = lvl.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, target);
        sp.teleportTo(lvl, safe.getX() + 0.5, safe.getY() + 0.2, safe.getZ() + 0.5, sp.getYRot(), sp.getXRot());
    }
}
