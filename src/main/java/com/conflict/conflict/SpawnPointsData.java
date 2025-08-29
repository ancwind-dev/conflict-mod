package com.conflict.conflict;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class SpawnPointsData extends SavedData {
    private static final String NAME = "conflict_spawnpoints_v1";

    private BlockPos bluePos = null;
    private int blueR = 6;
    private BlockPos redPos = null;
    private int redR = 6;

    public static SpawnPointsData get(ServerLevel level){
        return level.getDataStorage().computeIfAbsent(SpawnPointsData::load, SpawnPointsData::new, NAME);
    }

    public static SpawnPointsData load(CompoundTag tag){
        SpawnPointsData d = new SpawnPointsData();
        if (tag.contains("bluePos")) d.bluePos = BlockPos.of(tag.getLong("bluePos"));
        if (tag.contains("redPos"))  d.redPos  = BlockPos.of(tag.getLong("redPos"));
        d.blueR = tag.getInt("blueR");
        d.redR  = tag.getInt("redR");
        if (d.blueR <= 0) d.blueR = 6;
        if (d.redR  <= 0) d.redR  = 6;
        return d;
    }

    @Override
    public CompoundTag save(CompoundTag tag){
        if (bluePos != null) tag.putLong("bluePos", bluePos.asLong());
        if (redPos  != null) tag.putLong("redPos",  redPos.asLong());
        tag.putInt("blueR", blueR);
        tag.putInt("redR",  redR);
        return tag;
    }

    // getters/setters
    public BlockPos getBlue(){ return bluePos; }
    public int getBlueR(){ return blueR; }
    public void setBlue(BlockPos p, int r){ bluePos = p; blueR = Math.max(0,r); setDirty(); }

    public BlockPos getRed(){ return redPos; }
    public int getRedR(){ return redR; }
    public void setRed(BlockPos p, int r){ redPos = p; redR = Math.max(0,r); setDirty(); }

    public void clearBlue(){ bluePos = null; setDirty(); }
    public void clearRed(){ redPos = null; setDirty(); }
}
