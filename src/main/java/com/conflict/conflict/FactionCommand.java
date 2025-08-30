package com.conflict.conflict;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public class FactionCommand {

    public static void register(CommandDispatcher<CommandSourceStack> d){
        d.register(Commands.literal("faction")
                // "/faction" — открыть экран выбора (для себя)
                .executes(ctx -> {
                    if (!(ctx.getSource().getEntity() instanceof ServerPlayer sp)) {
                        ctx.getSource().sendFailure(Component.literal("Только игрок"));
                        return 0;
                    }
                    Network.CH.send(PacketDistributor.PLAYER.with(() -> sp), new PacketOpenFactionScreen());
                    return 1;
                })

                // "/faction set <blue|red>" — админом назначить фракцию игроку (опционально)
                .then(Commands.literal("set").requires(src -> src.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("faction", StringArgumentType.word())
                                        .suggests((c,b)->{ b.suggest("blue"); b.suggest("red"); return b.buildFuture(); })
                                        .executes(ctx -> {
                                            ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
                                            String fac = StringArgumentType.getString(ctx, "faction");
                                            String val = "blue".equalsIgnoreCase(fac) ? "BLUE" : "RED";

                                            Factions.set(target, val);
                                            // оповещаем клиентов о скине
                                            SkinBroadcaster.sendFor(target);

                                            ctx.getSource().sendSuccess(() ->
                                                    Component.literal("Игроку " + target.getGameProfile().getName() +
                                                            " назначена фракция " + val), true);
                                            return 1;
                                        })
                                )
                        )
                )

                // "/faction reset <player>" — СБРОС ФРАКЦИИ (ТО САМОЕ МЕСТО ДЛЯ П.3)
                .then(Commands.literal("reset").requires(src -> src.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> {
                                    ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
                                    // сбрасываем фракцию (null/NONE — в зависимости от твоей реализации Factions)
                                    Factions.set(target, null);

                                    // ВАЖНО: после сброса — разослать клиентам, что у игрока скин "NONE"
                                    SkinBroadcaster.sendFor(target);   // ← ПУНКТ (3)

                                    ctx.getSource().sendSuccess(() ->
                                                    Component.literal("Фракция игрока " + target.getGameProfile().getName() + " сброшена"),
                                            true);
                                    return 1;
                                })
                        )
                )
        );
    }
}
