package com.conflict.conflict;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class FactionScreen extends Screen {

    private Button blueBtn;
    private Button redBtn;

    protected FactionScreen() {
        super(Component.literal("Выбор фракции"));
    }

    @Override
    protected void init() {
        int w = this.width;
        int h = this.height;

        // Размеры и позиции кнопок
        int btnW = 150;
        int btnH = 30;
        int gap = 20;
        int y = h / 2;

        blueBtn = Button.builder(
                        Component.literal("BLUE").withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD),
                        b -> {
                            Network.CH.sendToServer(new PacketChooseFaction(PacketChooseFaction.F.BLUE));
                            this.onClose();
                        })
                .pos(w / 2 - btnW - gap, y)
                .size(btnW, btnH)
                .build();

        redBtn = Button.builder(
                        Component.literal("RED").withStyle(ChatFormatting.RED, ChatFormatting.BOLD),
                        b -> {
                            Network.CH.sendToServer(new PacketChooseFaction(PacketChooseFaction.F.RED));
                            this.onClose();
                        })
                .pos(w / 2 + gap, y)
                .size(btnW, btnH)
                .build();

        addRenderableWidget(blueBtn);
        addRenderableWidget(redBtn);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTicks) {
        // Полупрозрачный фон
        RenderSystem.disableDepthTest();
        g.fill(0, 0, this.width, this.height, 0xAA000000);

        // Заголовок
        String title = "Выберите свою фракцию";
        int titleW = this.font.width(title);
        g.drawString(this.font, title,
                (this.width - titleW) / 2,
                this.height / 2 - 60,
                0xFFFFFF, true);

        // Подписи под кнопками
        g.drawString(this.font, "Синие - КМП США", blueBtn.getX(), blueBtn.getY() + 40, 0xAAAAFF, false);
        g.drawString(this.font, "Красные — ВС РФ", redBtn.getX(), redBtn.getY() + 40, 0xFFAAAA, false);

        super.render(g, mouseX, mouseY, partialTicks);
        RenderSystem.enableDepthTest();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
