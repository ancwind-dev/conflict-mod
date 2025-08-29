package com.conflict.conflict;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class FactionCommand {

    public static void register(CommandDispatcher<CommandSourceStack> d) {
        d.register(Commands.literal("faction")
                // /faction get — доступно всем
                .then(Commands.literal("get")
                        .requires(src -> true)
                        .executes(ctx -> {
                            if (!(ctx.getSource().getEntity() instanceof ServerPlayer sp)) {
                                ctx.getSource().sendFailure(Component.literal("Команда доступна только игроку"));
                                return 0;
                            }
                            String f = Factions.get(sp);
                            ctx.getSource().sendSuccess(() ->
                                    Component.literal("Твоя фракция: " + String.valueOf(f)), false);
                            return 1;
                        })
                )

                // /faction set <BLUE|RED> — только OP
                .then(Commands.literal("set")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.argument("side", StringArgumentType.word())
                                .suggests((c, b) -> { b.suggest("BLUE"); b.suggest("RED"); return b.buildFuture(); })
                                .executes(ctx -> {
                                    if (!(ctx.getSource().getEntity() instanceof ServerPlayer sp)) {
                                        ctx.getSource().sendFailure(Component.literal("Команда доступна только игроку"));
                                        return 0;
                                    }
                                    String side = StringArgumentType.getString(ctx, "side").toUpperCase();
                                    if (!"BLUE".equals(side) && !"RED".equals(side)) {
                                        ctx.getSource().sendFailure(Component.literal("Используй: BLUE или RED"));
                                        return 0;
                                    }
                                    Factions.set(sp, side); // пишет в SavedData и синкает команды
                                    ctx.getSource().sendSuccess(() ->
                                            Component.literal("Фракция установлена: " + side), true);
                                    return 1;
                                })
                        )
                )

                // /faction reset — только OP (убрать из фракции)
                .then(Commands.literal("reset")
                        .requires(src -> src.hasPermission(2))
                        .executes(ctx -> {
                            if (!(ctx.getSource().getEntity() instanceof ServerPlayer sp)) {
                                ctx.getSource().sendFailure(Component.literal("Команда доступна только игроку"));
                                return 0;
                            }
                            Factions.set(sp, null);
                            ctx.getSource().sendSuccess(() ->
                                    Component.literal("Фракция сброшена"), true);
                            return 1;
                        })
                )
        );
    }
}
