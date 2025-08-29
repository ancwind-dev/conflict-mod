package com.conflict.conflict;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public class SpawnPoints {
    public static void set(ServerLevel lvl, BlockPos pos, int radius, boolean blue){
        var d = SpawnPointsData.get(lvl);
        if (blue) d.setBlue(pos.immutable(), radius);
        else      d.setRed(pos.immutable(),  radius);
    }
    public static void clear(ServerLevel lvl, boolean blue){
        var d = SpawnPointsData.get(lvl);
        if (blue) d.clearBlue();
        else      d.clearRed();
    }
    public static BlockPos get(ServerLevel lvl, boolean blue){
        var d = SpawnPointsData.get(lvl);
        return blue ? d.getBlue() : d.getRed();
    }
    public static int radius(ServerLevel lvl, boolean blue){
        var d = SpawnPointsData.get(lvl);
        return blue ? d.getBlueR() : d.getRedR();
    }
    public static boolean has(ServerLevel lvl, boolean blue){
        return get(lvl, blue) != null;
    }
}
