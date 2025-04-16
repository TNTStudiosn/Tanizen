package com.TNTStudios.tanizen.client.gui;

import com.TNTStudios.tanizen.network.TanizenPackets;
import com.TNTStudios.tanizen.util.SrTiempoMissionConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class OptionSelectionScreen extends Screen {
    private static final int GUI_WIDTH = 220;
    private static final int GUI_HEIGHT = 80;

    public OptionSelectionScreen() {
        super(Text.of("Sr. Tiempo"));
    }

    @Override
    protected void init() {
        super.init();
        int cx = (this.width - GUI_WIDTH) / 2;
        int cy = (this.height - GUI_HEIGHT) / 2;

        // Botón de compra
        String buyLabel = "Comprar hora (" + SrTiempoMissionConfig.buyCost + " monedas)";
        ButtonWidget buyButton = ButtonWidget.builder(Text.of(buyLabel), btn -> {
                    ClientPlayNetworking.send(TanizenPackets.REQUEST_BUY_HOUR, PacketByteBufs.create());
                    this.client.setScreen(null);
                })
                .dimensions(cx, cy + 30, GUI_WIDTH, 20)
                .build();
        addDrawableChild(buyButton);

        // Botón de misión diaria
        ButtonWidget missionButton = ButtonWidget.builder(Text.of("Misión diaria (gratis)"), btn -> {
                    ClientPlayNetworking.send(TanizenPackets.REQUEST_START_SRTIEMPO, PacketByteBufs.create());
                })
                .dimensions(cx, cy + 55, GUI_WIDTH, 20)
                .build();
        addDrawableChild(missionButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Fondo estándar atenuado
        this.renderBackground(context);

        int cx = (this.width - GUI_WIDTH) / 2;
        int cy = (this.height - GUI_HEIGHT) / 2;

        // Panel semitransparente
        int panelX = cx - 10;
        int panelY = cy - 20;
        int panelW = GUI_WIDTH + 20;
        int panelH = GUI_HEIGHT + 60;
        int bgColor = 0xCC000000;     // Negro 80% opaco
        int borderColor = 0xFFFFFFFF; // Blanco

        context.fill(panelX, panelY, panelX + panelW, panelY + panelH, bgColor);
        context.drawHorizontalLine(panelX, panelX + panelW, panelY, borderColor);
        context.drawHorizontalLine(panelX, panelX + panelW, panelY + panelH, borderColor);
        context.drawVerticalLine(panelX, panelY, panelY + panelH, borderColor);
        context.drawVerticalLine(panelX + panelW, panelY, panelY + panelH, borderColor);

        // Título con escala
        String title = "¿Qué prefieres hoy?";
        int tw = textRenderer.getWidth(title);
        context.getMatrices().push();
        float scale = 1.3f;
        float tx = (this.width - tw * scale) / 2f;
        float ty = panelY + 8;
        context.getMatrices().translate(tx, ty, 0);
        context.getMatrices().scale(scale, scale, 1f);
        context.drawText(textRenderer, title, 0, 0, 0xFFDD55, false);
        context.getMatrices().pop();

        // Dibujar botones y sus tooltips
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
