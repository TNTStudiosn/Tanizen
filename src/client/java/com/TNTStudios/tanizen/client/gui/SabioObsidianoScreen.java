package com.TNTStudios.tanizen.client.gui;

import com.TNTStudios.tanizen.missions.SabioObsidianoMissionData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.LinkedHashMap;
import java.util.Map;

public class SabioObsidianoScreen extends Screen {
    private static final int GUI_WIDTH = 380;
    private static final int GUI_HEIGHT = 260;

    private final SabioObsidianoMissionData missionData;
    private final ItemRenderer itemRenderer;

    private static final LinkedHashMap<Item, String> CUSTOM_NAMES = new LinkedHashMap<>();

    static {
        // Nombres personalizados de los materiales
        CUSTOM_NAMES.put(net.minecraft.item.Items.OBSIDIAN, "Bloques de obsidiana");
        CUSTOM_NAMES.put(net.minecraft.item.Items.BOOK, "Libro");
        CUSTOM_NAMES.put(net.minecraft.item.Items.SUGAR_CANE, "Fragmentos de sabiduría natural");
        CUSTOM_NAMES.put(net.minecraft.item.Items.GUNPOWDER, "Lágrimas de criatura nocturna");
        CUSTOM_NAMES.put(net.minecraft.item.Items.ENDER_PEARL, "Esencias sombrías");
        CUSTOM_NAMES.put(net.minecraft.item.Items.GOLDEN_APPLE, "Corazón del bosque");
    }

    public SabioObsidianoScreen() {
        super(Text.of("El Legado del Sabio Obsidiano"));
        this.missionData = com.TNTStudios.tanizen.client.data.ClientMissionLoader.loadFromClient(MinecraftClient.getInstance().player);
        this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

// Obtener escala del GUI
        float baseScale = Math.min((float) this.width / 427f, (float) this.height / 280f);
        float scale = baseScale * 0.75f; // reducción del 15%


// Aplicar escala al DrawContext
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1.0f);

// Recalcular centro con escala aplicada
        int scaledCenterX = (int) ((this.width / scale - GUI_WIDTH) / 2);
        int scaledCentery = (int) ((this.height / scale - GUI_HEIGHT) / 2);

        int bgColor = 0xAA000000;
        int borderColor = 0xFF6D1B89;

        context.fill(scaledCenterX, scaledCentery, scaledCenterX + GUI_WIDTH, scaledCentery + GUI_HEIGHT, bgColor);
        context.drawHorizontalLine(scaledCenterX, scaledCenterX + GUI_WIDTH, scaledCentery, borderColor);
        context.drawHorizontalLine(scaledCenterX, scaledCenterX + GUI_WIDTH, scaledCentery + GUI_HEIGHT, borderColor);
        context.drawVerticalLine(scaledCenterX, scaledCentery, scaledCentery + GUI_HEIGHT, borderColor);
        context.drawVerticalLine(scaledCenterX + GUI_WIDTH, scaledCentery, scaledCentery + GUI_HEIGHT, borderColor);

        int y = scaledCentery + 8;
        context.drawText(textRenderer, "🔮 Misión: El Legado del Sabio Obsidiano", scaledCenterX + 10, y, 0xFFFFFF, false);
        y += 14;
        context.drawText(textRenderer, "🎯 Objetivo: Reunir materiales para restaurar la mesa mágica", scaledCenterX + 10, y, 0xCCCCCC, false);
        y += 14;
        context.drawText(textRenderer, "🏆 Recompensa: Mesa de Encantamientos", scaledCenterX + 10, y, 0xDDDD99, false);

        y += 20;
        context.drawText(textRenderer, "📖 Leyenda:", scaledCenterX + 10, y, 0xAAAAFF, false);
        y += 12;
        context.drawText(textRenderer, "\"Hace siglos, un sabio llamado Obsidiano selló su conocimiento arcano...", scaledCenterX + 10, y, 0xBBBBBB, false);
        y += 10;
        context.drawText(textRenderer, "en una mesa mágica que fue destruida y dispersada por el mundo.\"", scaledCenterX + 10, y, 0xBBBBBB, false);
        y += 10;
        context.drawText(textRenderer, "\"Hoy, un eco de su poder ha reaparecido... y solo quienes reúnan", scaledCenterX + 10, y, 0xBBBBBB, false);
        y += 10;
        context.drawText(textRenderer, "los elementos perdidos serán dignos de encantar.\"", scaledCenterX + 10, y, 0xBBBBBB, false);

        y += 20;
        context.drawText(textRenderer, "📦 Materiales requeridos:", scaledCenterX + 10, y, 0xFFFFFF, false);
        y += 12;

        int i = 0;
        for (Map.Entry<Item, Integer> entry : missionData.getRequiredItems().entrySet()) {
            Item item = entry.getKey();
            int required = entry.getValue();
            int delivered = missionData.getDelivered().getOrDefault(item, 0);
            boolean completed = delivered >= required;

            int itemX = scaledCenterX + 14;
            int itemY = y + i * 18;

            // Render ítem
            context.drawItem(new ItemStack(item), itemX, itemY);

            // Nombre personalizado
            String name = CUSTOM_NAMES.getOrDefault(item, item.getName().getString());
            context.drawText(textRenderer, required + "x " + name, itemX + 20, itemY + 5, 0xFFFFFF, false);

            // Estado de entrega
            String status = completed ? "✔" : "✖";
            int color = completed ? 0x00FF00 : 0xFF4444;
            context.drawText(textRenderer, status, scaledCenterX + GUI_WIDTH - 20, itemY + 5, color, false);

            i++;
        }

        if (missionData.isCompleted()) {
            context.drawText(textRenderer, "✅ ¡Has completado la misión!", scaledCenterX + 60, scaledCentery + GUI_HEIGHT - 20, 0x00FF00, true);
        }
        context.getMatrices().pop();

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
