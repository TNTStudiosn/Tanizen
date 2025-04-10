package com.TNTStudios.tanizen.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.TNTStudios.tanizen.Tanizen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SabioObsidianoScreen extends Screen {

    private static final Identifier CUADRO_TEXTURE = new Identifier("tanizen", "textures/gui/cuadro.png");
    private static final Identifier RENDER_TEXTURE = new Identifier("tanizen", "textures/gui/rendersabio.png");

    public SabioObsidianoScreen() {
        super(Text.of("Diálogo del Sabio Obsidiano"));
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        // Centrado en pantalla
        int centerX = (this.width - 254) / 2;
        int centerY = (this.height - 166) / 2;

        // Fondo del cuadro
        RenderSystem.setShaderTexture(0, CUADRO_TEXTURE);
        drawTexture(matrices, centerX, centerY, 0, 0, 254, 166);

        // Render a la izquierda del cuadro
        RenderSystem.setShaderTexture(0, RENDER_TEXTURE);
        drawTexture(matrices, centerX - 70, centerY + 10, 0, 0, 64, 64); // ajustable

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
