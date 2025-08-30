package com.conflict.conflict;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MineCommand {

    public static void register(CommandDispatcher<CommandSourceStack> d){
        d.register(Commands.literal("mine").requires(src -> src.hasPermission(2))
                // /mine add [seconds]
                .then(Commands.literal("add")
                        .executes(ctx -> addHere(ctx.getSource(), 60))
                        .then(Commands.argument("seconds", IntegerArgumentType.integer(1, 24*60*60))
                                .executes(ctx -> addHere(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "seconds")))
                        )
                )
                // /mine remove
                .then(Commands.literal("remove").executes(ctx -> {
                    CommandSourceStack src = ctx.getSource();
                    if (!(src.getEntity() instanceof ServerPlayer sp)){
                        src.sendFailure(Component.literal("Только игрок"));
                        return 0;
                    }
                    BlockPos pos = raycastBlock(sp, 40.0);
                    if (pos == null){ src.sendFailure(Component.literal("Посмотри на блок ≤40 м")); return 0; }
                    ServerLevel lvl = src.getLevel();
                    OreNodesData data = OreNodesData.get(lvl);
                    if (!data.isNode(pos)){
                        src.sendFailure(Component.literal("Этот блок не зарегистрирован как рудной узел"));
                        return 0;
                    }
                    data.remove(pos);
                    src.sendSuccess(() -> Component.literal("Узел удалён: " + pos.toShortString()), true);
                    return 1;
                }))
        );
    }

    private static int addHere(CommandSourceStack src, int seconds){
        if (!(src.getEntity() instanceof ServerPlayer sp)){
            src.sendFailure(Component.literal("Только игрок"));
            return 0;
        }
        BlockPos pos = raycastBlock(sp, 40.0);
        if (pos == null){
            src.sendFailure(Component.literal("Посмотри на блок ≤40 м"));
            return 0;
        }
        ServerLevel lvl = src.getLevel();
        Block block = lvl.getBlockState(pos).getBlock();
        OreNodesData.get(lvl).add(pos, block, seconds);
        src.sendSuccess(() -> Component.literal(
                "Рудной узел добавлен: " + pos.toShortString() + " (блок=" + block + ", реген=" + seconds + "с)"), true);
        return 1;
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
