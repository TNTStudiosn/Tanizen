package com.TNTStudios.tanizen.client;

import com.TNTStudios.tanizen.client.gui.SabioObsidianoScreen;

import com.TNTStudios.tanizen.network.TanizenPackets;
import com.TNTStudios.tanizen.registry.TanizenEntities;
import net.fabricmc.api.ClientModInitializer;
import com.TNTStudios.tanizen.client.discord.DiscordPresenceHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import com.TNTStudios.tanizen.client.renderer.SabioObsidianoRenderer;
import net.minecraft.client.MinecraftClient;

public class TanizenClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        DiscordPresenceHandler.init();
        EntityRendererRegistry.register(TanizenEntities.SABIO_OBSIDIANO, SabioObsidianoRenderer::new);
        // Registra el receptor del paquete "open_dialog"
        ClientPlayNetworking.registerGlobalReceiver(TanizenPackets.OPEN_DIALOG_SCREEN, (client, handler, buf, responseSender) -> {
            // Aquí estamos en un hilo de red, debemos programar la acción en el hilo del main-client
            client.execute(() -> {
                // Abrimos la GUI
                client.setScreen(new SabioObsidianoScreen());
            });
        });
    }

}
