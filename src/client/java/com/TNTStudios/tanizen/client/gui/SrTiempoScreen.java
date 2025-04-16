package com.TNTStudios.tanizen.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Map;

public class SrTiempoScreen extends Screen {
    private static final int GUI_WIDTH = 380;
    private static final int GUI_HEIGHT = 260;

    private final boolean completed;
    private final Map<String, String> guiText;
    private final Map<Identifier, Integer> targets;
    private final Map<Identifier, Integer> kills;

    public SrTiempoScreen(Map<Identifier, Integer> kills, boolean completed, Map<String, String> guiText, Map<Identifier, Integer> targets) {
        super(Text.of("Sr. Tiempo"));
        this.kills = kills;
        this.completed = completed;
        this.guiText = guiText;
        this.targets = targets;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        // Escalado adaptativo
        float baseScale = Math.min((float) this.width / 500f, (float) this.height / 300f);
        float scale = baseScale * 0.85f;
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1);

        // Panel central
        int panelW = GUI_WIDTH;
        int panelH = GUI_HEIGHT;
        int x0 = (int) ((this.width / scale - panelW) / 2);
        int y0 = (int) ((this.height / scale - panelH) / 2);
        int bgColor = 0xCC000000;
        int borderColor = 0xFF6D1B89;

        context.fill(x0, y0, x0 + panelW, y0 + panelH, bgColor);
        context.drawHorizontalLine(x0, x0 + panelW, y0, borderColor);
        context.drawHorizontalLine(x0, x0 + panelW, y0 + panelH, borderColor);
        context.drawVerticalLine(x0, y0, y0 + panelH, borderColor);
        context.drawVerticalLine(x0 + panelW, y0, y0 + panelH, borderColor);

        // Título
        String title = guiText.getOrDefault("intro_1", "👋 Sr. Tiempo");
        int tw = textRenderer.getWidth(title);
        context.drawText(textRenderer, title, x0 + (panelW - tw) / 2, y0 + 10, 0xFFDD55, false);

        int y = y0 + 32;
        // Líneas intro
        String[] intros = {"intro_2","intro_3","intro_4","intro_5","intro_6"};
        for (String key : intros) {
            String line = guiText.getOrDefault(key, "");
            context.drawText(textRenderer, line, x0 + 20, y, 0xCCCCCC, false);
            y += 16;
        }

        // Separador
        context.drawHorizontalLine(x0 + 10, x0 + panelW - 10, y, 0x444444);
        y += 14;

        // Título progreso
        String progTitle = guiText.getOrDefault("progress_title", "📊 Progreso de la misión diaria:");
        context.drawText(textRenderer, progTitle, x0 + 20, y, 0xAAAAFF, false);
        y += 18;

        // Barras de progreso
        int barW = panelW - 40;
        int barH = 10;
        for (Map.Entry<Identifier, Integer> entry : targets.entrySet()) {
            Identifier id = entry.getKey();
            int req = entry.getValue();
            int cur = kills.getOrDefault(id, 0);
            boolean done = cur >= req;

            // Fondo de barra
            context.fill(x0 + 20, y, x0 + 20 + barW, y + barH, 0xAA444444);
            // Relleno
            int fillW = (int) ((float) cur / req * barW);
            int fillColor = done ? 0xEE00FF00 : 0xEEFF5555;
            context.fill(x0 + 20, y, x0 + 20 + fillW, y + barH, fillColor);

            // Texto encima
            String name = Registries.ENTITY_TYPE.get(id).getName().getString();
            String txt = name + " " + cur + " / " + req;
            context.drawText(textRenderer, txt, x0 + 24, y + 1, 0xFFFFFF, false);
            y += barH + 8;
        }

        // Mensaje final
        y += 6;
        String finalMsg = completed
                ? guiText.getOrDefault("completed", "✅ ¡Ya completaste la misión hoy!")
                : guiText.getOrDefault("not_completed", "🚀 ¡Ve a completar la misión y gana +1h!");
        int color = completed ? 0x00FF00 : 0xFFFF88;
        context.drawText(textRenderer, finalMsg, x0 + 20, y, color, false);

        context.getMatrices().pop();
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
