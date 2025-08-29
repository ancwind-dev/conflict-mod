package com.conflict.conflict;

import net.minecraft.core.BlockPos;

public class FlagPositions {
    private static BlockPos BLUE = null;
    private static BlockPos RED  = null;

    public static void set(BlockPos pos, boolean blue){ if (blue) BLUE = pos.immutable(); else RED = pos.immutable(); }
    public static void clear(boolean blue){ if (blue) BLUE = null; else RED = null; }
    public static BlockPos get(boolean blue){ return blue ? BLUE : RED; }
    public static boolean has(boolean blue){ return get(blue) != null; }
}
