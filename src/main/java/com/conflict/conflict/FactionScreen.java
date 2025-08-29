package com.conflict.conflict;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class FactionScreen extends Screen {
    protected FactionScreen() { super(Component.literal("Выбор фракции")); }

    @Override
    protected void init() {
        int cx = width / 2;
        int cy = height / 2;

        // Слева ВСЕГДА BLUE
        addRenderableWidget(Button.builder(Component.literal("BLUE"), b -> {
            Network.CH.sendToServer(new PacketChooseFaction(PacketChooseFaction.F.BLUE));
            // Не закрываем насильно — сервер сам снимет SPECTATOR и добавит в команду
            onClose();
        }).bounds(cx - 110, cy - 10, 100, 20).build());

        // Справа ВСЕГДА RED
        addRenderableWidget(Button.builder(Component.literal("RED"), b -> {
            Network.CH.sendToServer(new PacketChooseFaction(PacketChooseFaction.F.RED));
            onClose();
        }).bounds(cx + 10, cy - 10, 100, 20).build());
    }

    // затемнение фона
    @Override
    public void render(net.minecraft.client.gui.GuiGraphics gfx, int mx, int my, float dt) {
        this.renderBackground(gfx);
        super.render(gfx, mx, my, dt);
        gfx.drawCenteredString(this.font, "Выберите фракцию", this.width/2, this.height/2 - 30, 0xFFFFFF);
    }

    // Запрещаем закрытие ESC, чтобы игрок не уходил без выбора
    @Override public boolean shouldCloseOnEsc() { return false; }
    @Override public void onClose() {
        // Ничего не делаем — окно всё равно закроется вызовом setScreen(null),
        // но если ESC — shouldCloseOnEsc() вернёт false
        super.onClose();
    }
}
