package com.TNTStudios.tanizen.client;

import com.TNTStudios.tanizen.client.gui.SabioObsidianoScreen;

import com.TNTStudios.tanizen.client.gui.SrTiempoScreen;
import com.TNTStudios.tanizen.missions.SabioObsidianoMissionData;
import com.TNTStudios.tanizen.network.TanizenPackets;
import com.TNTStudios.tanizen.registry.TanizenEntities;
import net.fabricmc.api.ClientModInitializer;
import com.TNTStudios.tanizen.client.discord.DiscordPresenceHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import com.TNTStudios.tanizen.client.renderer.SabioObsidianoRenderer;
import com.TNTStudios.tanizen.client.renderer.SrTiempoRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TanizenClient implements ClientModInitializer {
    public static SabioObsidianoMissionData clientMissionData;

    @Override
    public void onInitializeClient() {
        DiscordPresenceHandler.init();
        EntityRendererRegistry.register(TanizenEntities.SABIO_OBSIDIANO, SabioObsidianoRenderer::new);
        EntityRendererRegistry.register(TanizenEntities.SRTIEMPO_NPC, SrTiempoRenderer::new);

        ClientPlayNetworking.registerGlobalReceiver(TanizenPackets.OPEN_DIALOG_SCREEN, (client, handler, buf, responseSender) -> {
            UUID playerUuid = buf.readUuid();
            boolean completed = buf.readBoolean();
            boolean rewardGiven = buf.readBoolean(); // Leer rewardGiven
            int deliveredSize = buf.readInt();
            Map<Item, Integer> delivered = new HashMap<>();
            for (int i = 0; i < deliveredSize; i++) {
                Item item = buf.readItemStack().getItem();
                int amount = buf.readInt();
                delivered.put(item, amount);
            }

            client.execute(() -> {
                clientMissionData = new SabioObsidianoMissionData(playerUuid);
                clientMissionData.getDelivered().putAll(delivered);
                clientMissionData.setCompleted(completed);
                clientMissionData.setRewardGiven(rewardGiven); // Establecer rewardGiven
                client.setScreen(new SabioObsidianoScreen());
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(TanizenPackets.OPEN_SRTIEMPO_SCREEN, (client, handler, buf, responseSender) -> {
            client.execute(() -> {
                client.setScreen(new SrTiempoScreen());
            });
        });

    }
}