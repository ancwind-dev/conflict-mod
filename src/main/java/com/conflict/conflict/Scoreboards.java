package com.conflict.conflict;

import net.minecraft.ChatFormatting;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.server.ServerLifecycleHooks;

public class Scoreboards {
    public static final String TEAM_BLUE = "conflict_blue";
    public static final String TEAM_RED  = "conflict_red";

    public static void ensureTeams() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        Scoreboard sb = server.getScoreboard();

        PlayerTeam blue = sb.getPlayerTeam(TEAM_BLUE);
        if (blue == null) {
            blue = sb.addPlayerTeam(TEAM_BLUE);
        }
        // Настройки синей команды
        blue.setDisplayName(net.minecraft.network.chat.Component.literal("BLUE").withStyle(ChatFormatting.BLUE));
        blue.setColor(ChatFormatting.BLUE);
        blue.setAllowFriendlyFire(false);
        blue.setSeeFriendlyInvisibles(true);
        blue.setPlayerPrefix(net.minecraft.network.chat.Component.literal("[BLUE] ").withStyle(ChatFormatting.BLUE));

        PlayerTeam red = sb.getPlayerTeam(TEAM_RED);
        if (red == null) {
            red = sb.addPlayerTeam(TEAM_RED);
        }
        // Настройки красной команды
        red.setDisplayName(net.minecraft.network.chat.Component.literal("RED").withStyle(ChatFormatting.RED));
        red.setColor(ChatFormatting.RED);
        red.setAllowFriendlyFire(false);
        red.setSeeFriendlyInvisibles(true);
        red.setPlayerPrefix(net.minecraft.network.chat.Component.literal("[RED] ").withStyle(ChatFormatting.RED));
    }

    public static void addToTeam(ServerPlayer p, boolean blueTeam) {
        Scoreboard sb = p.getServer().getScoreboard();
        ensureTeams(); // на всякий случай
        // Убрать из обеих, если вдруг уже состоял
        PlayerTeam blue = sb.getPlayerTeam(TEAM_BLUE);
        PlayerTeam red  = sb.getPlayerTeam(TEAM_RED);
        if (blue != null) sb.removePlayerFromTeam(p.getScoreboardName(), blue);
        if (red  != null) sb.removePlayerFromTeam(p.getScoreboardName(), red);

        String id = blueTeam ? TEAM_BLUE : TEAM_RED;
        PlayerTeam team = sb.getPlayerTeam(id);
        if (team != null) {
            sb.addPlayerToTeam(p.getScoreboardName(), team);
        }
    }
}
