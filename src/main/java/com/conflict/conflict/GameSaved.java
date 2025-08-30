package com.conflict.conflict;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class GameSaved extends SavedData {
    private static final String NAME = "conflict_game_v1";

    public int blue = 0;
    public int red  = 0;
    public int target = 50;
    public int timerSec = 60*60;
    public boolean running = false;

    // NEW:
    public boolean ffEnabled = false; // дружеский огонь: по умолчанию выключен

    public static GameSaved get(ServerLevel lvl){
        return lvl.getDataStorage().computeIfAbsent(GameSaved::load, GameSaved::new, NAME);
    }

    public static GameSaved load(CompoundTag tag){
        GameSaved d = new GameSaved();
        d.blue = tag.getInt("blue");
        d.red  = tag.getInt("red");
        d.target = tag.getInt("target");
        d.timerSec = tag.getInt("timer");
        d.running  = tag.getBoolean("running");
        d.ffEnabled = tag.getBoolean("ffEnabled"); // NEW
        if (d.target <= 0) d.target = 50;
        if (d.timerSec < 0) d.timerSec = 0;
        return d;
    }

    @Override
    public CompoundTag save(CompoundTag tag){
        tag.putInt("blue", blue);
        tag.putInt("red",  red);
        tag.putInt("target", target);
        tag.putInt("timer", timerSec);
        tag.putBoolean("running", running);
        tag.putBoolean("ffEnabled", ffEnabled); // NEW
        return tag;
    }

    public void mark(){ setDirty(); }
}
