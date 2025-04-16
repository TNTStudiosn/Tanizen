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
import org.lwjgl.glfw.GLFW;

import java.util.Map;

public class SrTiempoScreen extends Screen {
    private static final int GUI_WIDTH = 440;
    private static final int GUI_HEIGHT = 320;

    private boolean completed;
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

        float baseScale = Math.min((float) width / (GUI_WIDTH + 40), (float) height / (GUI_HEIGHT + 40));
        float scale = baseScale * 0.9f;
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1);

        int panelW = GUI_WIDTH, panelH = GUI_HEIGHT;
        int x0 = (int) ((width / scale - panelW) / 2), y0 = (int) ((height / scale - panelH) / 2);
        int bgColor = 0xCC000000, borderColor = 0xFF6D1B89;

        // Panel
        context.fill(x0, y0, x0 + panelW, y0 + panelH, bgColor);
        context.drawHorizontalLine(x0, x0 + panelW, y0, borderColor);
        context.drawHorizontalLine(x0, x0 + panelW, y0 + panelH, borderColor);
        context.drawVerticalLine(x0, y0, y0 + panelH, borderColor);
        context.drawVerticalLine(x0 + panelW, y0, y0 + panelH, borderColor);

        // Title
        String title = guiText.getOrDefault("intro_1", "👋 Sr. Tiempo");
        int titleW = textRenderer.getWidth(title);
        context.drawText(textRenderer, title, x0 + (panelW - titleW) / 2, y0 + 10, 0xFFDD55, false);

        // Intro lines
        int y = y0 + 32;
        for (int i = 2; i <= 6; i++) {
            String line = guiText.getOrDefault("intro_" + i, "");
            context.drawText(textRenderer, line, x0 + 20, y, 0xCCCCCC, false);
            y += 16;
        }

        // Separator
        y += 8;
        context.drawHorizontalLine(x0 + 10, x0 + panelW - 10, y, 0x444444);
        y += 14;

        boolean hasMobs = !mobTargets.isEmpty();
        boolean hasItems = !itemTargets.isEmpty();

        if (!hasMobs && !hasItems) {
            // No missions at all
            String none = guiText.getOrDefault("not_has_any", "🔔 No hay misiones disponibles.");
            int w = textRenderer.getWidth(none);
            context.drawText(textRenderer, none, x0 + (panelW - w) / 2, y + 20, 0xFFFFFF, false);
        } else if (hasMobs && hasItems) {
            // Two columns
            int colMid = panelW / 2;
            renderMobColumn(context, x0 + 20, y, colMid - 40);
            renderItemColumn(context, x0 + colMid + 10, y);
        } else if (hasMobs) {
            // Only mobs: center column
            int barW = panelW - 40;
            renderMobColumn(context, x0 + 20, y, barW);
        } else {
            // Only items: center column
            int itemX = x0 + (panelW - 100) / 2; // approx column width
            renderItemColumn(context, itemX, y);
        }

        // Final message
        int msgY = y0 + panelH - 30;
        String finalMsg = completed
                ? guiText.getOrDefault("completed", "✅ ¡Ya completaste la misión hoy!")
                : guiText.getOrDefault("not_completed", "🚀 ¡Ve a completar la misión y gana +1h!");
        int msgW = textRenderer.getWidth(finalMsg);
        context.drawText(textRenderer, finalMsg, x0 + (panelW - msgW) / 2, msgY, completed ? 0x00FF00 : 0xFFFF88, false);

        context.getMatrices().pop();
        super.render(context, mouseX, mouseY, delta);
    }

    private void renderMobColumn(DrawContext context, int startX, int startY, int barW) {
        int y = startY;
        String mobTitle = guiText.getOrDefault("progress_title", "📊 Progreso de muertes:");
        context.drawText(textRenderer, mobTitle, startX, y, 0xAAAAFF, false);
        y += 18;
        int barH = 10;
        for (Map.Entry<Identifier, Integer> entry : mobTargets.entrySet()) {
            Identifier id = entry.getKey();
            int req = entry.getValue();
            int cur = kills.getOrDefault(id, 0);
            boolean done = cur >= req;
            context.fill(startX, y, startX + barW, y + barH, 0xAA444444);
            int fw = (int) ((float) cur / req * barW);
            context.fill(startX, y, startX + fw, y + barH, done ? 0xEE00FF00 : 0xEEFF5555);
            String name = Registries.ENTITY_TYPE.get(id).getName().getString();
            context.drawText(textRenderer, name + " " + cur + "/" + req, startX + 4, y + 1, 0xFFFFFF, false);
            y += barH + 8;
        }
    }

    private void renderItemColumn(DrawContext context, int startX, int startY) {
        int y = startY;
        context.drawText(textRenderer, "📦 Progreso de ítems:", startX, y, 0xAAAAFF, false);
        y += 18;
        for (Map.Entry<Identifier, Integer> entry : itemTargets.entrySet()) {
            Identifier id = entry.getKey();
            int req = entry.getValue();
            int cur = itemsDelivered.getOrDefault(id, 0);
            boolean done = cur >= req;
            Item item = Registries.ITEM.get(id);
            context.drawItem(new ItemStack(item), startX, y);
            context.drawText(
                    textRenderer,
                    cur + " / " + req,
                    startX + 20,
                    y + 4,
                    done ? 0x00FF00 : 0xFFFFFF,
                    false
            );
            y += 20;
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.client.setScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void updateData(
            Map<Identifier, Integer> newKills,
            Map<Identifier, Integer> newItemsDelivered,
            boolean newCompleted,
            Map<String, String> newGuiText,
            Map<Identifier, Integer> newMobTargets,
            Map<Identifier, Integer> newItemTargets
    ) {
        kills.clear(); kills.putAll(newKills);
        itemsDelivered.clear(); itemsDelivered.putAll(newItemsDelivered);
        completed = newCompleted;
        guiText.clear(); guiText.putAll(newGuiText);
        mobTargets.clear(); mobTargets.putAll(newMobTargets);
        itemTargets.clear(); itemTargets.putAll(newItemTargets);
    }
}
