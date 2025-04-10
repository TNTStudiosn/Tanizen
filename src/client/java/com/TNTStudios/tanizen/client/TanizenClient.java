package com.TNTStudios.tanizen.client;

import com.TNTStudios.tanizen.client.gui.SabioObsidianoScreen;
import com.TNTStudios.tanizen.client.network.ModPackets;
import com.TNTStudios.tanizen.registry.TanizenEntities;
import net.fabricmc.api.ClientModInitializer;
import com.TNTStudios.tanizen.client.discord.DiscordPresenceHandler;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import com.TNTStudios.tanizen.client.renderer.SabioObsidianoRenderer;
import net.minecraft.client.MinecraftClient;

public class TanizenClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        DiscordPresenceHandler.init();
        EntityRendererRegistry.register(TanizenEntities.SABIO_OBSIDIANO, SabioObsidianoRenderer::new);
        MinecraftClient.getInstance().setScreen(new SabioObsidianoScreen());
        ModPackets.registerS2CPackets();
    }

}
