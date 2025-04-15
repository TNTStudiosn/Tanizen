package com.TNTStudios.tanizen.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class SrTiempoScreen extends Screen {
    private static final int GUI_WIDTH = 380;
    private static final int GUI_HEIGHT = 260;
    private final int zombies, creepers, phantoms;
    private final boolean completed;

    public SrTiempoScreen(int zombies, int creepers, int phantoms, boolean completed) {
        super(Text.of("Sr. Tiempo"));
        this.zombies = zombies;
        this.creepers = creepers;
        this.phantoms = phantoms;
        this.completed = completed;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        // Obtener escala del GUI
        float baseScale = Math.min((float) this.width / 427f, (float) this.height / 280f);
        float scale = baseScale * 0.75f; // Reducción del 15%

        // Aplicar escala al DrawContext
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1.0f);

        // Recalcular centro con escala aplicada
        int scaledCenterX = (int) ((this.width / scale - GUI_WIDTH) / 2);
        int scaledCentery = (int) ((this.height / scale - GUI_HEIGHT) / 2);

        // Colores para el fondo y borde
        int bgColor = 0xAA000000; // Fondo semi-transparente
        int borderColor = 0xFF6D1B89; // Borde morado

        // Dibujar fondo y bordes
        context.fill(scaledCenterX, scaledCentery, scaledCenterX + GUI_WIDTH, scaledCentery + GUI_HEIGHT, bgColor);
        context.drawHorizontalLine(scaledCenterX, scaledCenterX + GUI_WIDTH, scaledCentery, borderColor);
        context.drawHorizontalLine(scaledCenterX, scaledCenterX + GUI_WIDTH, scaledCentery + GUI_HEIGHT, borderColor);
        context.drawVerticalLine(scaledCenterX, scaledCentery, scaledCentery + GUI_HEIGHT, borderColor);
        context.drawVerticalLine(scaledCenterX + GUI_WIDTH, scaledCentery, scaledCentery + GUI_HEIGHT, borderColor);

        // Dibujar el mensaje línea por línea
        int y = scaledCentery + 8;
        context.drawText(textRenderer, "👋 Hola buenas, soy el Sr. Tiempo", scaledCenterX + 10, y, 0xFFFFFF, false);
        y += 14;
        context.drawText(textRenderer, "🕒 Si deseas que agregue 1h más a tu contador", scaledCenterX + 10, y, 0xCCCCCC, false);
        y += 14;
        context.drawText(textRenderer, "📜 Deberás hacer una misión para mí", scaledCenterX + 10, y, 0xCCCCCC, false);
        y += 14;
        context.drawText(textRenderer, "⏳ Esta misión solo la podrás hacer 1 sola vez al día", scaledCenterX + 10, y, 0xCCCCCC, false);
        y += 14;
        context.drawText(textRenderer, "🔄 Por el momento siempre será la misma", scaledCenterX + 10, y, 0xCCCCCC, false);
        y += 14;
        context.drawText(textRenderer, "🎁 Pero en cuanto la termines te daré tu hora extra", scaledCenterX + 10, y, 0xDDDD99, false);

        int yStats = y + 14;
        context.drawText(textRenderer, "📊 Progreso de la misión diaria:", scaledCenterX + 10, yStats, 0xAAAAFF, false);
        yStats += 14;
        context.drawText(textRenderer, "• Zombies: " + zombies + "/10", scaledCenterX + 10, yStats, 0xFFFFFF, false);
        yStats += 12;
        context.drawText(textRenderer, "• Creepers: " + creepers + "/10", scaledCenterX + 10, yStats, 0xFFFFFF, false);
        yStats += 12;
        context.drawText(textRenderer, "• Phantoms: " + phantoms + "/10", scaledCenterX + 10, yStats, 0xFFFFFF, false);
        yStats += 14;

        if (completed) {
            context.drawText(textRenderer, "✅ ¡Ya completaste la misión hoy!", scaledCenterX + 10, yStats, 0x00FF00, false);
        }


        context.getMatrices().pop();

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}