package com.conflict.conflict;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/**
 * Слой рендера игрока, рисующий поверх базового скина текстуру фракции (BLUE/RED).
 * Визуально это выглядит как полная замена скина и работает в offline-mode.
 */
public class FactionSkinLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private static final ResourceLocation BLUE_TEX =
            new ResourceLocation(ConflictMod.MODID, "textures/skins/blue.png");
    private static final ResourceLocation RED_TEX  =
            new ResourceLocation(ConflictMod.MODID, "textures/skins/red.png");

    public FactionSkinLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack,
                       MultiBufferSource buffer,
                       int packedLight,
                       AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount, float partialTicks,
                       float ageInTicks, float netHeadYaw, float headPitch) {

        String key = ClientSkins.get(player.getUUID());
        if (key == null || "NONE".equals(key)) return;

        final ResourceLocation tex =
                "BLUE".equals(key) ? BLUE_TEX :
                        "RED".equals(key)  ? RED_TEX  : null;

        if (tex == null) return;

        var model = this.getParentModel();
        var vb = buffer.getBuffer(RenderType.entityCutoutNoCull(tex));
        // Рендерим модель с нашей текстурой поверх базового скина
        model.renderToBuffer(poseStack, vb, packedLight, OverlayTexture.NO_OVERLAY,
                1f, 1f, 1f, 1f);
    }
}
