package com.TNTStudios.tanizen.client.gui;

import com.TNTStudios.tanizen.util.SrTiempoMissionConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Map;

public class SrTiempoScreen extends Screen {
    private static final int GUI_WIDTH = 440;
    private static final int GUI_HEIGHT = 320;

    private final boolean completed;
    private final Map<String, String> guiText;
    private final Map<Identifier, Integer> mobTargets;
    private final Map<Identifier, Integer> kills;
    private final Map<Identifier, Integer> itemTargets;
    private final Map<Identifier, Integer> itemsDelivered;
    private final ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

    public SrTiempoScreen(
            Map<Identifier, Integer> kills,
            Map<Identifier, Integer> itemsDelivered,
            boolean completed,
            Map<String, String> guiText,
            Map<Identifier, Integer> mobTargets,
            Map<Identifier, Integer> itemTargets
    ) {
        super(Text.of("Sr. Tiempo"));
        this.kills = kills;
        this.itemsDelivered = itemsDelivered;
        this.completed = completed;
        this.guiText = guiText;
        this.mobTargets = mobTargets;
        this.itemTargets = itemTargets;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        // Escalado adaptativo
        float baseScale = Math.min((float) this.width / (GUI_WIDTH + 40), (float) this.height / (GUI_HEIGHT + 40));
        float scale = baseScale * 0.9f;
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1);

        int panelW = GUI_WIDTH;
        int panelH = GUI_HEIGHT;
        int x0 = (int) ((this.width / scale - panelW) / 2);
        int y0 = (int) ((this.height / scale - panelH) / 2);

        // Fondo y borde
        int bgColor = 0xCC000000, borderColor = 0xFF6D1B89;
        context.fill(x0, y0, x0 + panelW, y0 + panelH, bgColor);
        context.drawHorizontalLine(x0, x0 + panelW, y0, borderColor);
        context.drawHorizontalLine(x0, x0 + panelW, y0 + panelH, borderColor);
        context.drawVerticalLine(x0, y0, y0 + panelH, borderColor);
        context.drawVerticalLine(x0 + panelW, y0, y0 + panelH, borderColor);

        // Título centrado
        String title = guiText.getOrDefault("intro_1", "👋 Sr. Tiempo");
        int titleW = textRenderer.getWidth(title);
        context.drawText(textRenderer, title, x0 + (panelW - titleW) / 2, y0 + 10, 0xFFDD55, false);

        // Introductorio
        int y = y0 + 32;
        for (int i = 2; i <= 6; i++) {
            String line = guiText.getOrDefault("intro_" + i, "");
            context.drawText(textRenderer, line, x0 + 20, y, 0xCCCCCC, false);
            y += 16;
        }

        // Separador
        y += 8;
        context.drawHorizontalLine(x0 + 10, x0 + panelW - 10, y, 0x444444);
        y += 14;

        // Columnas
        int colMid = panelW / 2;
        int leftX = x0 + 20;
        int rightX = x0 + colMid + 10;
        int mobY = y;
        int itemY = y;

        // 📊 Muertes
        String mobTitle = guiText.getOrDefault("progress_title", "📊 Progreso de muertes:");
        context.drawText(textRenderer, mobTitle, leftX, mobY, 0xAAAAFF, false);
        mobY += 18;
        int barW = colMid - 40, barH = 10;
        for (Map.Entry<Identifier, Integer> entry : mobTargets.entrySet()) {
            Identifier id = entry.getKey();
            int req = entry.getValue();
            int cur = kills.getOrDefault(id, 0);
            boolean done = cur >= req;

            // barra de fondo y progreso
            context.fill(leftX, mobY, leftX + barW, mobY + barH, 0xAA444444);
            int fw = (int) ((float) cur / req * barW);
            context.fill(leftX, mobY, leftX + fw, mobY + barH, done ? 0xEE00FF00 : 0xEEFF5555);

            // texto
            String name = Registries.ENTITY_TYPE.get(id).getName().getString();
            context.drawText(textRenderer, name + " " + cur + "/" + req, leftX + 4, mobY + 1, 0xFFFFFF, false);
            mobY += barH + 8;
        }

        // 📦 Entregas
        context.drawText(textRenderer, "📦 Progreso de ítems:", rightX, itemY, 0xAAAAFF, false);
        itemY += 18;
        for (Map.Entry<Identifier, Integer> entry : itemTargets.entrySet()) {
            Identifier id = entry.getKey();
            int req = entry.getValue();
            int cur = itemsDelivered.getOrDefault(id, 0);
            boolean done = cur >= req;
            Item item = Registries.ITEM.get(id);

            // icono
            context.drawItem(new ItemStack(item), rightX, itemY);

            // texto
            context.drawText(
                    textRenderer,
                    cur + " / " + req,
                    rightX + 20,
                    itemY + 4,
                    done ? 0x00FF00 : 0xFFFFFF,
                    false
            );
            itemY += 20;
        }

        // Mensaje final abajo centrado
        int msgY = y0 + panelH - 30;
        String finalMsg = completed
                ? guiText.getOrDefault("completed", "✅ ¡Ya completaste la misión hoy!")
                : guiText.getOrDefault("not_completed", "🚀 ¡Ve a completar la misión y gana +1h!");
        int msgW = textRenderer.getWidth(finalMsg);
        context.drawText(textRenderer, finalMsg, x0 + (panelW - msgW) / 2, msgY, completed ? 0x00FF00 : 0xFFFF88, false);

        context.getMatrices().pop();
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
