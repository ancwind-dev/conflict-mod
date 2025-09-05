package com.conflict.conflict;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class Loadouts {

    private static final Logger LOG = LogUtils.getLogger();

    /**
     * Выдать стартовый набор игроку.
     * @param sp   игрок
     * @param blue true = BLUE, false = RED
     * @param wipe очистить инвентарь перед выдачей (рекомендуется для TDM)
     */
    public static void giveFor(ServerPlayer sp, boolean blue, boolean wipe){
        if (wipe) {
            // Полная очистка инвентаря + рук + брони
            sp.getInventory().clearContent();
            sp.setItemSlot(EquipmentSlot.HEAD,  ItemStack.EMPTY);
            sp.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
            sp.setItemSlot(EquipmentSlot.LEGS,  ItemStack.EMPTY);
            sp.setItemSlot(EquipmentSlot.FEET,  ItemStack.EMPTY);
        }

        if (blue) giveBlue(sp);
        else      giveRed(sp);

        sp.inventoryMenu.broadcastChanges();
    }

    // ============ BLUE ============
    private static void giveBlue(ServerPlayer sp){
        // Броня
        equip(sp, "superbwarfare:us_helmet_pastg", EquipmentSlot.HEAD);
        equip(sp, "superbwarfare:us_chest_iotv",   EquipmentSlot.CHEST);

        // Оружие/снаряжение
        add(sp, "superbwarfare:knife", 1);
        add(sp, "superbwarfare:m_4",   1);

        // Патроны: 3 стака
        add(sp, "superbwarfare:rifle_ammo", 64);
        add(sp, "superbwarfare:rifle_ammo", 64);
        add(sp, "superbwarfare:rifle_ammo", 64);

        // Аптечки и гранаты
        add(sp, "superbwarfare:medical_kit", 2);
        add(sp, "superbwarfare:hand_grenade", 2);

        // Еда и кирка
        add(sp, "minecraft:baked_potato", 64);
        add(sp, "minecraft:iron_pickaxe", 1);
    }

    // ============ RED ============
    private static void giveRed(ServerPlayer sp){
        equip(sp, "superbwarfare:ru_helmet_6b47", EquipmentSlot.HEAD);
        equip(sp, "superbwarfare:ru_chest_6b43",  EquipmentSlot.CHEST);

        add(sp, "superbwarfare:knife", 1);
        add(sp, "superbwarfare:ak_47", 1);

        add(sp, "superbwarfare:rifle_ammo", 64);
        add(sp, "superbwarfare:rifle_ammo", 64);
        add(sp, "superbwarfare:rifle_ammo", 64);

        add(sp, "superbwarfare:medical_kit", 2);
        add(sp, "superbwarfare:hand_grenade", 2);

        add(sp, "minecraft:baked_potato", 64);
        add(sp, "minecraft:iron_pickaxe", 1);
    }

    // ---------- helpers ----------

    /** Добавить предмет по ID (modid:item), учитывая максимальный размер стака. */
    private static void add(ServerPlayer sp, String id, int count){
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
        if (item == null){
            warnMissing(sp, id);
            return;
        }
        int max = Math.max(1, item.getDefaultInstance().getMaxStackSize());
        int left = count;
        while (left > 0){
            int give = Math.min(max, left);
            sp.getInventory().add(new ItemStack(item, give));
            left -= give;
        }
    }

    /** Надеть предмет в слот брони, если он существует. */
    private static void equip(ServerPlayer sp, String id, EquipmentSlot slot){
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
        if (item == null){
            warnMissing(sp, id);
            return;
        }
        sp.setItemSlot(slot, new ItemStack(item));
    }

    private static void warnMissing(ServerPlayer sp, String id){
        sp.sendSystemMessage(Component.literal("§c[Conflict] Не найден предмет: " + id));
        LOG.warn("[Conflict] Item not found: {}", id);
    }
}
