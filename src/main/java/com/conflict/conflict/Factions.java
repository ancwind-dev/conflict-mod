package com.conflict.conflict;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public class Factions {
    // читаем из SavedData; если пусто — пробуем из команд
    public static String get(ServerPlayer p) {
        String saved = FactionData.get(p.serverLevel()).get(p.getUUID());
        if ("BLUE".equals(saved) || "RED".equals(saved)) return saved;

        Scoreboard sb = p.getServer().getScoreboard();
        PlayerTeam team = sb.getPlayersTeam(p.getScoreboardName());
        if (team == null) return null;
        if (Scoreboards.TEAM_BLUE.equals(team.getName())) return "BLUE";
        if (Scoreboards.TEAM_RED.equals(team.getName())) return "RED";
        return null;
    }

    // записываем в SavedData + синхронизируем команды
    public static void set(ServerPlayer p, String value) {
        // SavedData
        FactionData fd = FactionData.get(p.serverLevel());
        if ("BLUE".equals(value) || "RED".equals(value)) fd.set(p.getUUID(), value);
        else fd.clear(p.getUUID());

        // Scoreboard гарантируем
        Scoreboards.ensureTeams();
        var sb = p.getServer().getScoreboard();
        var blue = sb.getPlayerTeam(Scoreboards.TEAM_BLUE);
        var red = sb.getPlayerTeam(Scoreboards.TEAM_RED);

        // кто сейчас?
        var current = sb.getPlayersTeam(p.getScoreboardName());

        // снятие ТОЛЬКО если реально в этой тиме
        if (current != null) {
            if (blue != null && current.getName().equals(Scoreboards.TEAM_BLUE)) {
                sb.removePlayerFromTeam(p.getScoreboardName(), blue);
            } else if (red != null && current.getName().equals(Scoreboards.TEAM_RED)) {
                sb.removePlayerFromTeam(p.getScoreboardName(), red);
            }
        }

        // добавить по значению
        if ("BLUE".equals(value) && blue != null) sb.addPlayerToTeam(p.getScoreboardName(), blue);
        if ("RED".equals(value) && red != null) sb.addPlayerToTeam(p.getScoreboardName(), red);
    }
}
