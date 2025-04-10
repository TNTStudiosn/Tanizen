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
import net.minecraft.util.Identifier;

import java.util.Map;

public class SabioObsidianoScreen extends Screen {
    private static final int GUI_WIDTH = 256;
    private static final int GUI_HEIGHT = 180;

    private final SabioObsidianoMissionData missionData;
    private final ItemRenderer itemRenderer;

    public SabioObsidianoScreen() {
        super(Text.of("El Legado del Sabio Obsidiano"));
        this.missionData = SabioObsidianoMissionData.load(MinecraftClient.getInstance().player);
        this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int centerX = (this.width - GUI_WIDTH) / 2;
        int centerY = (this.height - GUI_HEIGHT) / 2;

        int bgColor = 0xAA000000;
        int borderColor = 0xFF6D1B89;

        context.fill(centerX, centerY, centerX + GUI_WIDTH, centerY + GUI_HEIGHT, bgColor);
        context.drawHorizontalLine(centerX, centerX + GUI_WIDTH, centerY, borderColor);
        context.drawHorizontalLine(centerX, centerX + GUI_WIDTH, centerY + GUI_HEIGHT, borderColor);
        context.drawVerticalLine(centerX, centerY, centerY + GUI_HEIGHT, borderColor);
        context.drawVerticalLine(centerX + GUI_WIDTH, centerY, centerY + GUI_HEIGHT, borderColor);

        // Título
        context.drawText(textRenderer, "Misión: El Legado del Sabio", centerX + 10, centerY + 8, 0xFFFFFF, false);

        int startY = centerY + 28;
        int lineSpacing = 18;
        int iconSize = 16;

        int i = 0;
        for (Map.Entry<Item, Integer> entry : missionData.getRequiredItems().entrySet()) {
            Item item = entry.getKey();
            int required = entry.getValue();
            int delivered = missionData.getDelivered().getOrDefault(item, 0);
            boolean completed = delivered >= required;

            int itemX = centerX + 14;
            int itemY = startY + i * lineSpacing;

            // Render ítem
            context.drawItem(new ItemStack(item), itemX, itemY);
            context.drawText(textRenderer, required + "x " + item.getName().getString(), itemX + 20, itemY + 5, 0xFFFFFF, false);

            // Check ✔️ o ❌
            String status = completed ? "✔" : "✖";
            int color = completed ? 0x00FF00 : 0xFF3333;
            context.drawText(textRenderer, status, itemX + 160, itemY + 5, color, false);

            i++;
        }

        if (missionData.isCompleted()) {
            context.drawText(textRenderer, "¡Misión completada!", centerX + 60, centerY + GUI_HEIGHT - 20, 0x00FF00, true);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
