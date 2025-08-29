package com.conflict.conflict;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component; // <-- ВАЖНЫЙ ИМПОРТ
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class FlagCommand {
    public static void register(CommandDispatcher<CommandSourceStack> d) {
        d.register(Commands.literal("flag")
                .requires(src -> src.hasPermission(0)) // для тестов — всем можно

                // /flag set <blue|red>  (луч через взгляд игрока)
                .then(Commands.literal("set")
                        .then(Commands.argument("team", StringArgumentType.word())
                                .suggests((c,b)->{ b.suggest("blue"); b.suggest("red"); return b.buildFuture(); })
                                .executes(ctx -> {
                                    CommandSourceStack src = ctx.getSource();
                                    if (!(src.getEntity() instanceof ServerPlayer sp)) {
                                        src.sendFailure(Component.literal("Команда только для игрока"));
                                        return 0;
                                    }
                                    BlockPos pos = raycastBlock(sp, 20.0);
                                    if (pos == null) {
                                        src.sendFailure(Component.literal("Посмотри на баннер (<20 блоков)"));
                                        return 0;
                                    }
                                    var be = sp.level().getBlockEntity(pos);
                                    if (!(be instanceof BannerBlockEntity)) {
                                        src.sendFailure(Component.literal("Нужен ВАНИЛЬНЫЙ баннер"));
                                        return 0;
                                    }
                                    boolean blue = "blue".equalsIgnoreCase(StringArgumentType.getString(ctx, "team"));
                                    FlagPositions.set(pos, blue);
                                    src.sendSuccess(() -> Component.literal(
                                            "Флаг " + (blue ? "BLUE" : "RED") + " установлен: " + pos.toShortString()), true);
                                    return 1;
                                })
                        )
                )

                // /flag set <blue|red> <x> <y> <z>  (ручные координаты)
                .then(Commands.literal("set")
                        .then(Commands.argument("team", StringArgumentType.word())
                                .suggests((c,b)->{ b.suggest("blue"); b.suggest("red"); return b.buildFuture(); })
                                .then(Commands.argument("x", DoubleArgumentType.doubleArg())
                                        .then(Commands.argument("y", DoubleArgumentType.doubleArg())
                                                .then(Commands.argument("z", DoubleArgumentType.doubleArg())
                                                        .executes(ctx -> {
                                                            boolean blue = "blue".equalsIgnoreCase(StringArgumentType.getString(ctx, "team"));
                                                            double x = DoubleArgumentType.getDouble(ctx, "x");
                                                            double y = DoubleArgumentType.getDouble(ctx, "y");
                                                            double z = DoubleArgumentType.getDouble(ctx, "z");
                                                            BlockPos pos = BlockPos.containing(x, y, z);

                                                            var level = ctx.getSource().getLevel();
                                                            var be = level.getBlockEntity(pos);
                                                            if (!(be instanceof BannerBlockEntity)) {
                                                                ctx.getSource().sendFailure(Component.literal("По этим координатам нет баннера"));
                                                                return 0;
                                                            }
                                                            FlagPositions.set(pos, blue);
                                                            ctx.getSource().sendSuccess(() -> Component.literal(
                                                                    "Флаг " + (blue ? "BLUE" : "RED") + " установлен: " + pos.toShortString()), true);
                                                            return 1;
                                                        })
                                                )
                                        )
                                )
                        )
                )

                // /flag show <blue|red>
                .then(Commands.literal("show")
                        .then(Commands.argument("team", StringArgumentType.word())
                                .suggests((c,b)->{ b.suggest("blue"); b.suggest("red"); return b.buildFuture(); })
                                .executes(ctx -> {
                                    boolean blue = "blue".equalsIgnoreCase(StringArgumentType.getString(ctx, "team"));
                                    BlockPos p = FlagPositions.get(blue);
                                    if (p == null) {
                                        ctx.getSource().sendFailure(Component.literal("Флаг " + (blue ? "BLUE" : "RED") + " не установлен"));
                                    } else {
                                        ctx.getSource().sendSuccess(() -> Component.literal(
                                                "Флаг " + (blue ? "BLUE" : "RED") + ": " + p.toShortString()), false);
                                    }
                                    return 1;
                                })
                        )
                )

                // /flag clear <blue|red>
                .then(Commands.literal("clear")
                        .then(Commands.argument("team", StringArgumentType.word())
                                .suggests((c,b)->{ b.suggest("blue"); b.suggest("red"); return b.buildFuture(); })
                                .executes(ctx -> {
                                    boolean blue = "blue".equalsIgnoreCase(StringArgumentType.getString(ctx, "team"));
                                    FlagPositions.clear(blue);
                                    ctx.getSource().sendSuccess(() -> Component.literal(
                                            "Флаг " + (blue ? "BLUE" : "RED") + " очищен"), true);
                                    return 1;
                                })
                        )
                )
        );
    }

    // Надёжный серверный рейкаст по блоку на дистанции dist (метров)
    private static BlockPos raycastBlock(ServerPlayer sp, double dist) {
        Vec3 eye = sp.getEyePosition();
        Vec3 look = sp.getViewVector(1.0F);
        Vec3 end  = eye.add(look.x * dist, look.y * dist, look.z * dist);

        var ctx = new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, sp);
        var result = sp.level().clip(ctx);
        if (result.getType() != HitResult.Type.BLOCK) return null;
        return ((BlockHitResult) result).getBlockPos();
    }
}
