package com.conflict.conflict;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class SpawnCommand {

    public static void register(CommandDispatcher<CommandSourceStack> d){
        d.register(Commands.literal("spawnpt")
                .requires(src -> src.hasPermission(2)) // админ

                // /spawnpt set <blue|red> <radius>  — взять блок из центра взгляда игрока
                .then(Commands.literal("set")
                        .then(Commands.argument("team", StringArgumentType.word())
                                .suggests((c,b)->{ b.suggest("blue"); b.suggest("red"); return b.buildFuture(); })
                                .then(Commands.argument("radius", IntegerArgumentType.integer(0, 64))
                                        .executes(ctx -> {
                                            CommandSourceStack src = ctx.getSource();
                                            if (!(src.getEntity() instanceof ServerPlayer sp)){
                                                src.sendFailure(Component.literal("Команда только для игрока"));
                                                return 0;
                                            }
                                            BlockPos pos = raycastBlock(sp, 30.0);
                                            if (pos == null){
                                                src.sendFailure(Component.literal("Посмотри на любой блок на расстоянии ≤30"));
                                                return 0;
                                            }
                                            boolean blue = "blue".equalsIgnoreCase(StringArgumentType.getString(ctx, "team"));
                                            int r = IntegerArgumentType.getInteger(ctx, "radius");
                                            ServerLevel lvl = src.getLevel();
                                            SpawnPoints.set(lvl, pos, r, blue);
                                            src.sendSuccess(() -> Component.literal(
                                                    "Точка спавна " + (blue ? "BLUE" : "RED") +
                                                            " = " + pos.toShortString() + ", радиус " + r), true);
                                            return 1;
                                        })
                                )
                        )
                )

                // /spawnpt set <blue|red> <x> <y> <z> <radius> — задать координатами
                .then(Commands.literal("set")
                        .then(Commands.argument("team", StringArgumentType.word())
                                .suggests((c,b)->{ b.suggest("blue"); b.suggest("red"); return b.buildFuture(); })
                                .then(Commands.argument("x", DoubleArgumentType.doubleArg())
                                        .then(Commands.argument("y", DoubleArgumentType.doubleArg())
                                                .then(Commands.argument("z", DoubleArgumentType.doubleArg())
                                                        .then(Commands.argument("radius", IntegerArgumentType.integer(0,64))
                                                                .executes(ctx -> {
                                                                    boolean blue = "blue".equalsIgnoreCase(StringArgumentType.getString(ctx, "team"));
                                                                    double x = DoubleArgumentType.getDouble(ctx, "x");
                                                                    double y = DoubleArgumentType.getDouble(ctx, "y");
                                                                    double z = DoubleArgumentType.getDouble(ctx, "z");
                                                                    int r    = IntegerArgumentType.getInteger(ctx, "radius");
                                                                    BlockPos pos = BlockPos.containing(x, y, z);
                                                                    ServerLevel lvl = ctx.getSource().getLevel();
                                                                    SpawnPoints.set(lvl, pos, r, blue);
                                                                    ctx.getSource().sendSuccess(() -> Component.literal(
                                                                            "Точка спавна " + (blue ? "BLUE" : "RED") +
                                                                                    " = " + pos.toShortString() + ", радиус " + r), true);
                                                                    return 1;
                                                                })
                                                        )
                                                )
                                        )
                                )
                        )
                )

                // /spawnpt show <blue|red>
                .then(Commands.literal("show")
                        .then(Commands.argument("team", StringArgumentType.word())
                                .suggests((c,b)->{ b.suggest("blue"); b.suggest("red"); return b.buildFuture(); })
                                .executes(ctx -> {
                                    boolean blue = "blue".equalsIgnoreCase(StringArgumentType.getString(ctx, "team"));
                                    ServerLevel lvl = ctx.getSource().getLevel();
                                    BlockPos p = SpawnPoints.get(lvl, blue);
                                    int r = SpawnPoints.radius(lvl, blue);
                                    if (p == null)
                                        ctx.getSource().sendFailure(Component.literal("Точка спавна " + (blue?"BLUE":"RED") + " не задана"));
                                    else
                                        ctx.getSource().sendSuccess(() -> Component.literal(
                                                "Точка " + (blue?"BLUE":"RED") + ": " + p.toShortString() + " (r=" + r + ")"), false);
                                    return 1;
                                })
                        )
                )

                // /spawnpt clear <blue|red>
                .then(Commands.literal("clear")
                        .then(Commands.argument("team", StringArgumentType.word())
                                .suggests((c,b)->{ b.suggest("blue"); b.suggest("red"); return b.buildFuture(); })
                                .executes(ctx -> {
                                    boolean blue = "blue".equalsIgnoreCase(StringArgumentType.getString(ctx, "team"));
                                    ServerLevel lvl = ctx.getSource().getLevel();
                                    SpawnPoints.clear(lvl, blue);
                                    ctx.getSource().sendSuccess(() -> Component.literal(
                                            "Точка спавна " + (blue?"BLUE":"RED") + " очищена"), true);
                                    return 1;
                                })
                        )
                )
        );
    }

    private static BlockPos raycastBlock(ServerPlayer sp, double dist){
        Vec3 eye = sp.getEyePosition();
        Vec3 look = sp.getViewVector(1.0F);
        Vec3 end  = eye.add(look.x*dist, look.y*dist, look.z*dist);
        var ctx = new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, sp);
        var res = sp.level().clip(ctx);
        if (res.getType() != HitResult.Type.BLOCK) return null;
        return ((BlockHitResult)res).getBlockPos();
    }
}
