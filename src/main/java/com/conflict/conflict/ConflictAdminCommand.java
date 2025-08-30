package com.conflict.conflict;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ConflictAdminCommand {

    public static void register(CommandDispatcher<CommandSourceStack> d){
        d.register(Commands.literal("conflict").requires(src -> src.hasPermission(2))
                // ── Старт/стоп/сброс матча ────────────────────────────────────────────────
                .then(Commands.literal("start").executes(ctx -> {
                    var lvl = ctx.getSource().getLevel();
                    var gs = GameSaved.get(lvl);
                    gs.running = true;
                    gs.mark();
                    ctx.getSource().sendSuccess(() -> Component.literal("Матч запущен"), true);
                    GameLogic.broadcastState(ctx.getSource().getServer(), gs);
                    return 1;
                }))
                .then(Commands.literal("stop").executes(ctx -> {
                    var lvl = ctx.getSource().getLevel();
                    var gs = GameSaved.get(lvl);
                    gs.running = false;
                    gs.mark();
                    ctx.getSource().sendSuccess(() -> Component.literal("Матч остановлен"), true);
                    GameLogic.broadcastState(ctx.getSource().getServer(), gs);
                    return 1;
                }))
                .then(Commands.literal("reset").executes(ctx -> {
                    var lvl = ctx.getSource().getLevel();
                    var gs = GameSaved.get(lvl);
                    gs.blue = 0;
                    gs.red  = 0;
                    gs.timerSec = 60 * 60; // 60 минут по умолчанию
                    gs.mark();
                    ctx.getSource().sendSuccess(() -> Component.literal("Счёт и таймер сброшены"), true);
                    GameLogic.broadcastState(ctx.getSource().getServer(), gs);
                    return 1;
                }))

                // ── Настройки цели и таймера ─────────────────────────────────────────────
                .then(Commands.literal("set")
                        .then(Commands.literal("target")
                                .then(Commands.argument("value", IntegerArgumentType.integer(1, 10000))
                                        .executes(ctx -> {
                                            int v = IntegerArgumentType.getInteger(ctx, "value");
                                            var gs = GameSaved.get(ctx.getSource().getLevel());
                                            gs.target = v;
                                            gs.mark();
                                            ctx.getSource().sendSuccess(() -> Component.literal("Цель очков установлена: " + v), true);
                                            GameLogic.broadcastState(ctx.getSource().getServer(), gs);
                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("minutes")
                                .then(Commands.argument("value", IntegerArgumentType.integer(0, 24 * 60))
                                        .executes(ctx -> {
                                            int v = IntegerArgumentType.getInteger(ctx, "value");
                                            var gs = GameSaved.get(ctx.getSource().getLevel());
                                            gs.timerSec = v * 60;
                                            gs.mark();
                                            ctx.getSource().sendSuccess(() -> Component.literal("Таймер установлен: " + v + " минут"), true);
                                            GameLogic.broadcastState(ctx.getSource().getServer(), gs);
                                            return 1;
                                        })
                                )
                        )
                )

                // ── Friendly Fire ───────────────────────────────────────────────────────
                .then(Commands.literal("ff")
                        .then(Commands.literal("on").executes(ctx -> {
                            var gs = GameSaved.get(ctx.getSource().getLevel());
                            gs.ffEnabled = true;
                            gs.mark();
                            ctx.getSource().sendSuccess(() -> Component.literal("Friendly fire: ON"), true);
                            GameLogic.broadcastState(ctx.getSource().getServer(), gs);
                            return 1;
                        }))
                        .then(Commands.literal("off").executes(ctx -> {
                            var gs = GameSaved.get(ctx.getSource().getLevel());
                            gs.ffEnabled = false;
                            gs.mark();
                            ctx.getSource().sendSuccess(() -> Component.literal("Friendly fire: OFF"), true);
                            GameLogic.broadcastState(ctx.getSource().getServer(), gs);
                            return 1;
                        }))
                        .then(Commands.literal("toggle").executes(ctx -> {
                            var gs = GameSaved.get(ctx.getSource().getLevel());
                            gs.ffEnabled = !gs.ffEnabled;
                            gs.mark();
                            ctx.getSource().sendSuccess(() -> Component.literal("Friendly fire: " + (gs.ffEnabled ? "ON" : "OFF")), true);
                            GameLogic.broadcastState(ctx.getSource().getServer(), gs);
                            return 1;
                        }))
                        .then(Commands.literal("status").executes(ctx -> {
                            var gs = GameSaved.get(ctx.getSource().getLevel());
                            ctx.getSource().sendSuccess(() ->
                                    Component.literal("Friendly fire: " + (gs.ffEnabled ? "ON" : "OFF")), false);
                            return 1;
                        }))
                )

                // ── Статус матча ────────────────────────────────────────────────────────
                .then(Commands.literal("status").executes(ctx -> {
                    var gs = GameSaved.get(ctx.getSource().getLevel());
                    String msg = "Статус: " + (gs.running ? "идёт" : "остановлен")
                            + " | BLUE=" + gs.blue + " RED=" + gs.red
                            + " | Цель=" + gs.target
                            + " | Осталось=" + (gs.timerSec / 60) + "м " + (gs.timerSec % 60) + "с"
                            + " | FF=" + (gs.ffEnabled ? "ON" : "OFF");
                    ctx.getSource().sendSuccess(() -> Component.literal(msg), false);
                    return 1;
                }))
        );
    }
}
