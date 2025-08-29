package com.conflict.conflict;

import net.minecraft.core.BlockPos;

public class SpawnPoints {
    private static BlockPos BLUE_POS = null;
    private static int      BLUE_R   = 6;
    private static BlockPos RED_POS  = null;
    private static int      RED_R    = 6;

    public static void set(BlockPos pos, int radius, boolean blue){
        if (blue){ BLUE_POS = pos.immutable(); BLUE_R = Math.max(0, radius); }
        else     { RED_POS  = pos.immutable(); RED_R  = Math.max(0, radius); }
    }
    public static void clear(boolean blue){ if (blue){ BLUE_POS = null; } else { RED_POS = null; } }

    public static BlockPos get(boolean blue){ return blue ? BLUE_POS : RED_POS; }
    public static int radius(boolean blue){ return blue ? BLUE_R : RED_R; }

    public static boolean has(boolean blue){ return get(blue) != null; }
}
