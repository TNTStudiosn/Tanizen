package com.TNTStudios.tanizen.client.gui;

import com.TNTStudios.tanizen.network.TanizenPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class OptionSelectionScreen extends Screen {
    private static final int GUI_WIDTH = 240;
    private static final int GUI_HEIGHT = 100;

    private final int buyCost;
    private final Identifier buyItem;
    private ButtonWidget buyButton;
    private ButtonWidget missionButton;

    public OptionSelectionScreen(int buyCost, Identifier buyItem) {
        super(Text.of("Sr. Tiempo"));
        this.buyCost = buyCost;
        this.buyItem = buyItem;
    }

    @Override
    protected void init() {
        super.init();
        int cx = (this.width - GUI_WIDTH) / 2;
        int cy = (this.height - GUI_HEIGHT) / 2;
        int btnWidth = GUI_WIDTH - 40;
        int btnHeight = 24;
        int buyY = cy + 40;
        int missionY = cy + 70;

        // Comprar hora
        buyButton = new ButtonWidget(
                cx + 20, buyY,
                btnWidth, btnHeight,
                Text.of("🛒 Comprar 1hora (" + buyCost + " Monedas de oro)"),
                btn -> {
                    ClientPlayNetworking.send(TanizenPackets.REQUEST_BUY_HOUR, PacketByteBufs.create());
                    this.client.setScreen(null);
                },
                button -> Text.literal("Paga " + buyCost + " monedas de oro para 1hora extra")
        ) {
            @Override
            public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
                int bgColor = this.isHovered() ? 0xFF44AA44 : 0xFF228822;
                context.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), bgColor);
                int textWidth = OptionSelectionScreen.this.textRenderer.getWidth(this.getMessage().getString());
                context.drawText(OptionSelectionScreen.this.textRenderer,
                        this.getMessage(),
                        this.getX() + (this.getWidth() - textWidth) / 2,
                        this.getY() + (this.getHeight() - 8) / 2,
                        0xFFFFFFFF, false);
            }
        };
        addDrawableChild(buyButton);

        // Misión diaria
        missionButton = new ButtonWidget(
                cx + 20, missionY,
                btnWidth, btnHeight,
                Text.of("🎯 Misión diaria (gratis)"),
                btn -> ClientPlayNetworking.send(TanizenPackets.REQUEST_START_SRTIEMPO, PacketByteBufs.create()),
                button -> Text.literal("Activa la misión diaria y gana 1h gratis")
        ) {
            @Override
            public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
                int bgColor = this.isHovered() ? 0xFF5555CC : 0xFF333388;
                context.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), bgColor);
                int textWidth = OptionSelectionScreen.this.textRenderer.getWidth(this.getMessage().getString());
                context.drawText(OptionSelectionScreen.this.textRenderer,
                        this.getMessage(),
                        this.getX() + (this.getWidth() - textWidth) / 2,
                        this.getY() + (this.getHeight() - 8) / 2,
                        0xFFFFFFFF, false);
            }
        };
        addDrawableChild(missionButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Fondo tenue
        this.renderBackground(context);
        int cx = (this.width - GUI_WIDTH) / 2;
        int cy = (this.height - GUI_HEIGHT) / 2;

        // Panel
        int panelX = cx - 10;
        int panelY = cy - 30;
        int panelW = GUI_WIDTH + 20;
        int panelH = GUI_HEIGHT + 80;
        int bgColor = 0xCC000000;
        int borderColor = 0xFFFFFFFF;

        context.fill(panelX, panelY, panelX + panelW, panelY + panelH, bgColor);
        context.drawHorizontalLine(panelX, panelX + panelW, panelY, borderColor);
        context.drawHorizontalLine(panelX, panelX + panelW, panelY + panelH, borderColor);
        context.drawVerticalLine(panelX, panelY, panelY + panelH, borderColor);
        context.drawVerticalLine(panelX + panelW, panelY, panelY + panelH, borderColor);

        // Título
        String title = "¿Qué prefieres hoy?";
        int titleW = textRenderer.getWidth(title);
        context.getMatrices().push();
        float scale = 1.4f;
        context.getMatrices().translate((this.width - titleW * scale) / 2f, panelY + 8, 0);
        context.getMatrices().scale(scale, scale, 1f);
        context.drawText(textRenderer, title, 0, 0, 0xFFDD55, false);
        context.getMatrices().pop();

        // Botones
        buyButton.render(context, mouseX, mouseY, delta);
        missionButton.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
