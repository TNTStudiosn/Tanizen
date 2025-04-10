package com.TNTStudios.tanizen.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SabioObsidianoScreen extends Screen {
    private static final int GUI_WIDTH = 256;
    private static final int GUI_HEIGHT = 160;

    public SabioObsidianoScreen() {
        super(Text.of("Diálogo del Sabio Obsidiano"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Fondo "standard" (nubes gris claro)
        this.renderBackground(context);

        // Calculamos la posición centrada de la GUI
        int centerX = (this.width - GUI_WIDTH) / 2;
        int centerY = (this.height - GUI_HEIGHT) / 2;

        // 1) Dibuja un rectángulo semitransparente como fondo
        // color ARGB -> 0xAA000000 (0xAA = ~66% alpha)
        int bgColor = 0xAA000000;
        context.fill(centerX, centerY, centerX + GUI_WIDTH, centerY + GUI_HEIGHT, bgColor);

        // 2) Borde en color morado (opcional)
        int borderColor = 0xFF6D1B89; // un morado
        // Línea horizontal superior e inferior
        context.drawHorizontalLine(centerX, centerX + GUI_WIDTH, centerY, borderColor);
        context.drawHorizontalLine(centerX, centerX + GUI_WIDTH, centerY + GUI_HEIGHT, borderColor);
        // Línea vertical izquierda y derecha
        context.drawVerticalLine(centerX, centerY, centerY + GUI_HEIGHT, borderColor);
        context.drawVerticalLine(centerX + GUI_WIDTH, centerY, centerY + GUI_HEIGHT, borderColor);


        // 4) Escribe texto de ejemplo a la izquierda
        //   Podrías hacer varias líneas, etc.
        //   Para escribir texto: context.drawText(textRenderer, "Texto", x, y, color, shadow?)
        int textX = centerX + 10;
        int textY = centerY + 10;
        context.drawText(this.textRenderer, "¡Saludos, viajero!", textX, textY, 0xFFFFFF, false);
        context.drawText(this.textRenderer, "Yo soy el Sabio Obsidiano.", textX, textY + 12, 0xFFDDDD, false);
        context.drawText(this.textRenderer, "Haz click otra vez \npara más diálogo...", textX, textY + 24, 0xFFFFFF, false);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
