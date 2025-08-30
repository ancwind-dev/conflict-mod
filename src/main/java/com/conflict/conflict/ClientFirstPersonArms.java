package com.conflict.conflict;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Перекрываем руку в 1-м лице текстурой фракции (BLUE/RED),
 * чтобы не оставалась «рука Стива» в offline-mode.
 */
@Mod.EventBusSubscriber(modid = ConflictMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientFirstPersonArms {

    private static final ResourceLocation BLUE_TEX =
            new ResourceLocation(ConflictMod.MODID, "textures/skins/blue.png");
    private static final ResourceLocation RED_TEX =
            new ResourceLocation(ConflictMod.MODID, "textures/skins/red.png");

    @SubscribeEvent
    public static void onRenderArm(RenderArmEvent e) {
        AbstractClientPlayer player = e.getPlayer();
        String key = ClientSkins.get(player.getUUID());
        if (key == null || "NONE".equals(key)) return;

        ResourceLocation tex = "BLUE".equals(key) ? BLUE_TEX : ("RED".equals(key) ? RED_TEX : null);
        if (tex == null) return;

        // 1) Полностью отключаем ванильный рендер руки,
        //    чтобы не было "слоя Стива" под нашей текстурой.
        e.setCanceled(true);

        // 2) Берём рендерер/модель игрока и рисуем ТОЛЬКО нужную руку нашей текстурой
        var dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        var base = dispatcher.getRenderer(player);
        if (!(base instanceof PlayerRenderer renderer)) return;

        PlayerModel<AbstractClientPlayer> model = renderer.getModel();

        // Повторяем подготовку модели (как делает ванилла)
        model.attackTime = 0.0f;
        model.crouching = player.isCrouching();
        model.riding = player.isPassenger();
        model.young = false;

        // Отображаем только нужную руку (+рукав), остальное скрываем
        model.setAllVisible(false);
        if (e.getArm() == HumanoidArm.RIGHT) {
            model.rightArm.visible = true;
            model.rightSleeve.visible = true;
        } else {
            model.leftArm.visible = true;
            model.leftSleeve.visible = true;
        }

        PoseStack pose = e.getPoseStack();
        MultiBufferSource buf = e.getMultiBufferSource();

        var vb = buf.getBuffer(RenderType.entityCutoutNoCull(tex));
        model.renderToBuffer(pose, vb, e.getPackedLight(), OverlayTexture.NO_OVERLAY,
                1f, 1f, 1f, 1f);
    }
}
