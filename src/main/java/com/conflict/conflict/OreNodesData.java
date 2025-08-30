package com.conflict.conflict;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class OreNodesData extends net.minecraft.world.level.saveddata.SavedData {
    private static final String NAME = "conflict_ore_nodes_v1";

    public static class Node {
        public String blockId;   // "minecraft:iron_ore" и т.п. — что восстанавливать
        public int regenSec;     // через сколько секунд восстановить
        public long nextTick;    // gameTime (tick), когда надо восстановить (0 = не запланировано)

        public Node(String blockId, int regenSec, long nextTick){
            this.blockId = blockId;
            this.regenSec = regenSec;
            this.nextTick = nextTick;
        }
    }

    // Зарегистрированные узлы: позиция -> параметры
    private final Map<BlockPos, Node> nodes = new HashMap<>();

    public static OreNodesData get(ServerLevel lvl){
        return lvl.getDataStorage().computeIfAbsent(OreNodesData::load, OreNodesData::new, NAME);
    }

    public static OreNodesData load(CompoundTag tag){
        OreNodesData d = new OreNodesData();
        ListTag list = tag.getList("nodes", ListTag.TAG_COMPOUND);
        for (int i=0;i<list.size();i++){
            CompoundTag t = list.getCompound(i);
            BlockPos pos = new BlockPos(t.getInt("x"), t.getInt("y"), t.getInt("z"));
            String blockId = t.getString("block");
            int regenSec = t.getInt("regen");
            long next = t.getLong("next");
            d.nodes.put(pos, new Node(blockId, regenSec, next));
        }
        return d;
    }

    @Override
    public CompoundTag save(CompoundTag tag){
        ListTag list = new ListTag();
        for (var e : nodes.entrySet()){
            CompoundTag t = new CompoundTag();
            BlockPos p = e.getKey();
            Node n = e.getValue();
            t.putInt("x", p.getX());
            t.putInt("y", p.getY());
            t.putInt("z", p.getZ());
            t.putString("block", n.blockId);
            t.putInt("regen", n.regenSec);
            t.putLong("next", n.nextTick);
            list.add(t);
        }
        tag.put("nodes", list);
        return tag;
    }

    public Map<BlockPos, Node> all() { return nodes; }

    public void add(BlockPos pos, Block block, int regenSec){
        String id = String.valueOf(ForgeRegistries.BLOCKS.getKey(block));
        nodes.put(pos.immutable(), new Node(id, regenSec, 0L));
        setDirty();
    }

    public void remove(BlockPos pos){
        nodes.remove(pos);
        setDirty();
    }

    public boolean isNode(BlockPos pos){ return nodes.containsKey(pos); }

    public void planRegen(ServerLevel lvl, BlockPos pos){
        Node n = nodes.get(pos);
        if (n == null) return;
        long now = lvl.getGameTime();
        n.nextTick = now + n.regenSec * 20L;
        setDirty();
    }

    public void tryRegen(ServerLevel lvl){
        long now = lvl.getGameTime();
        // пробежимся по копии, чтобы безопасно модифицировать
        for (var e : new HashMap<>(nodes).entrySet()){
            BlockPos pos = e.getKey();
            Node n = e.getValue();
            if (n.nextTick > 0 && n.nextTick <= now){
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(n.blockId));
                if (block != null){
                    lvl.setBlock(pos, block.defaultBlockState(), 3);
                }
                n.nextTick = 0L;
                setDirty();
            }
        }
    }
}
