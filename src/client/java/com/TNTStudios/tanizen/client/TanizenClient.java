package com.TNTStudios.tanizen.client;

import com.TNTStudios.tanizen.client.gui.SabioObsidianoScreen;

import com.TNTStudios.tanizen.missions.SabioObsidianoMissionData;
import com.TNTStudios.tanizen.network.TanizenPackets;
import com.TNTStudios.tanizen.registry.TanizenEntities;
import net.fabricmc.api.ClientModInitializer;
import com.TNTStudios.tanizen.client.discord.DiscordPresenceHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import com.TNTStudios.tanizen.client.renderer.SabioObsidianoRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TanizenClient implements ClientModInitializer {
    public static SabioObsidianoMissionData clientMissionData; // Almacenar datos en el cliente

    @Override
    public void onInitializeClient() {
        DiscordPresenceHandler.init();
        EntityRendererRegistry.register(TanizenEntities.SABIO_OBSIDIANO, SabioObsidianoRenderer::new);

        ClientPlayNetworking.registerGlobalReceiver(TanizenPackets.OPEN_DIALOG_SCREEN, (client, handler, buf, responseSender) -> {
            UUID playerUuid = buf.readUuid();
            boolean completed = buf.readBoolean();
            int deliveredSize = buf.readInt();
            Map<Item, Integer> delivered = new HashMap<>();
            for (int i = 0; i < deliveredSize; i++) {
                Item item = buf.readItemStack().getItem();
                int amount = buf.readInt();
                delivered.put(item, amount);
            }

            client.execute(() -> {
                // Actualizar los datos del cliente
                clientMissionData = new SabioObsidianoMissionData(playerUuid);
                clientMissionData.getDelivered().putAll(delivered);
                clientMissionData.setCompleted(completed);
                // Abrir la GUI con los datos actualizados
                client.setScreen(new SabioObsidianoScreen());
            });
        });
    }
}
