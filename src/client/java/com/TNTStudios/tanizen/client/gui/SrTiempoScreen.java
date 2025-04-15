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

        float baseScale = Math.min((float) this.width / 427f, (float) this.height / 280f);
        float scale = baseScale * 0.75f;

        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1.0f);

        int cx = (int) ((this.width / scale - GUI_WIDTH) / 2);
        int cy = (int) ((this.height / scale - GUI_HEIGHT) / 2);

        int bgColor = 0xCC000000;
        int borderColor = 0xFF6D1B89;

        context.fill(cx, cy, cx + GUI_WIDTH, cy + GUI_HEIGHT, bgColor);
        context.drawHorizontalLine(cx, cx + GUI_WIDTH, cy, borderColor);
        context.drawHorizontalLine(cx, cx + GUI_WIDTH, cy + GUI_HEIGHT, borderColor);
        context.drawVerticalLine(cx, cy, cy + GUI_HEIGHT, borderColor);
        context.drawVerticalLine(cx + GUI_WIDTH, cy, cy + GUI_HEIGHT, borderColor);

        // Sección de presentación
        int y = cy + 10;
        context.drawText(textRenderer, "👋 Hola buenas, soy el Sr. Tiempo", cx + 14, y, 0xFFFFFF, false);
        y += 16;
        context.drawText(textRenderer, "🕒 Si deseas que agregue 1h más a tu contador", cx + 14, y, 0xCCCCCC, false);
        y += 14;
        context.drawText(textRenderer, "📜 Deberás hacer una misión para mí", cx + 14, y, 0xCCCCCC, false);
        y += 14;
        context.drawText(textRenderer, "⏳ Esta misión solo la podrás hacer 1 sola vez al día", cx + 14, y, 0xCCCCCC, false);
        y += 14;
        context.drawText(textRenderer, "🔄 Por el momento siempre será la misma", cx + 14, y, 0xCCCCCC, false);
        y += 14;
        context.drawText(textRenderer, "🎁 Pero en cuanto la termines te daré tu hora extra", cx + 14, y, 0xDDDD99, false);

        // Línea divisoria estética
        y += 10;
        context.drawHorizontalLine(cx + 10, cx + GUI_WIDTH - 10, y, 0x444444);
        y += 12;

        // Progreso visual
        context.drawText(textRenderer, "📊 Progreso de la misión diaria:", cx + 14, y, 0xAAAAFF, false);
        y += 16;

        int zColor = zombies >= 10 ? 0x00FF00 : 0xFF5555;
        int cColor = creepers >= 10 ? 0x00FF00 : 0xFFAA00;
        int pColor = phantoms >= 10 ? 0x00FF00 : 0x66CCFF;

        context.drawText(textRenderer, "• Zombies:  " + zombies + " / 10", cx + 20, y, zColor, false);
        y += 12;
        context.drawText(textRenderer, "• Creepers: " + creepers + " / 10", cx + 20, y, cColor, false);
        y += 12;
        context.drawText(textRenderer, "• Phantoms: " + phantoms + " / 10", cx + 20, y, pColor, false);
        y += 18;

        if (completed) {
            context.drawText(textRenderer, "✅ ¡Ya completaste la misión hoy!", cx + 14, y, 0x00FF00, false);
        } else {
            context.drawText(textRenderer, "🚀 ¡Ve a completar la misión y gana +1h!", cx + 14, y, 0xFFFF88, false);
        }

        context.getMatrices().pop();
        super.render(context, mouseX, mouseY, delta);
    }


    @Override
    public boolean shouldPause() {
        return false;
    }
}