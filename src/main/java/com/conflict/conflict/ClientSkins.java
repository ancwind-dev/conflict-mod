package com.conflict.conflict;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Клиентское хранилище выбранных скинов по UUID игрока.
 * Значение: "BLUE", "RED" или "NONE".
 * Сюда пишет PacketSkinUpdate.handle(...)
 */
public class ClientSkins {
    private static final Map<UUID, String> OVERRIDES = new ConcurrentHashMap<>();

    public static void apply(UUID uuid, String skinKey){
        if (skinKey == null || "NONE".equalsIgnoreCase(skinKey)) {
            OVERRIDES.remove(uuid);
        } else {
            OVERRIDES.put(uuid, skinKey.toUpperCase());
        }
    }

    public static String get(UUID uuid){
        return OVERRIDES.get(uuid);
    }
}
