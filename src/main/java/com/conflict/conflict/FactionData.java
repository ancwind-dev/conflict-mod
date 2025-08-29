package com.conflict.conflict;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FactionData extends SavedData {
    private static final String NAME = "conflict_factions_v1";
    private final Map<UUID, String> map = new HashMap<>(); // "BLUE"/"RED"

    public static FactionData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                FactionData::load, FactionData::new, NAME);
    }

    public static FactionData load(CompoundTag nbt) {
        FactionData fd = new FactionData();
        ListTag list = nbt.getList("entries", ListTag.TAG_COMPOUND);
        for (int i=0;i<list.size();i++) {
            CompoundTag e = list.getCompound(i);
            try {
                UUID id = UUID.fromString(e.getString("uuid"));
                String v = e.getString("val");
                if ("BLUE".equals(v) || "RED".equals(v)) fd.map.put(id, v);
            } catch (Exception ignored) {}
        }
        return fd;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        ListTag list = new ListTag();
        for (var ent : map.entrySet()) {
            CompoundTag e = new CompoundTag();
            e.putString("uuid", ent.getKey().toString());
            e.putString("val", ent.getValue());
            list.add(e);
        }
        nbt.put("entries", list);
        return nbt;
    }

    public String get(UUID id) { return map.get(id); }
    public void set(UUID id, String v) { if (v==null) map.remove(id); else map.put(id, v); setDirty(); }
    public void clear(UUID id) { map.remove(id); setDirty(); }
}
