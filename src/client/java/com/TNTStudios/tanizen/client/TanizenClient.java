package com.TNTStudios.tanizen.client;

import com.TNTStudios.tanizen.client.discord.DiscordPresenceHandler;
import com.TNTStudios.tanizen.client.gui.OptionSelectionScreen;
import com.TNTStudios.tanizen.client.gui.SabioObsidianoScreen;
import com.TNTStudios.tanizen.client.gui.SrTiempoScreen;
import com.TNTStudios.tanizen.client.renderer.SabioObsidianoRenderer;
import com.TNTStudios.tanizen.client.renderer.SrTiempoRenderer;
import com.TNTStudios.tanizen.missions.SabioObsidianoMissionData;
import com.TNTStudios.tanizen.network.TanizenPackets;
import com.TNTStudios.tanizen.registry.TanizenEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.*;

public class TanizenClient implements ClientModInitializer {
    public static SabioObsidianoMissionData clientMissionData;

    @Override
    public void onInitializeClient() {
        DiscordPresenceHandler.init();
        EntityRendererRegistry.register(TanizenEntities.SABIO_OBSIDIANO, SabioObsidianoRenderer::new);
        EntityRendererRegistry.register(TanizenEntities.SRTIEMPO_NPC, SrTiempoRenderer::new);

        // Sabio Obsidiano Packet
        ClientPlayNetworking.registerGlobalReceiver(TanizenPackets.OPEN_DIALOG_SCREEN, (client, handler, buf, responseSender) -> {
            UUID playerUuid = buf.readUuid();
            boolean completed = buf.readBoolean();
            boolean rewardGiven = buf.readBoolean();
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
                clientMissionData.setRewardGiven(rewardGiven);
                client.setScreen(new SabioObsidianoScreen());
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(
                TanizenPackets.OPEN_SRTIEMPO_OPTIONS,
                (client, handler, buf, responseSender) -> {
                    // Leemos coste e ítem
                    int buyCost = buf.readInt();
                    Identifier buyItem = buf.readIdentifier();
                    client.execute(() -> {
                        // Pasamos buyCost al constructor
                        client.setScreen(new OptionSelectionScreen(buyCost, buyItem));
                    });
                }
        );

        // Sr. Tiempo Packet
        ClientPlayNetworking.registerGlobalReceiver(TanizenPackets.MISSION_PROGRESS_SRTIEMPO, (client, handler, buf, responseSender) -> {
            // Leer kills por mob
            int killsSize = buf.readInt();
            Map<Identifier, Integer> kills = new HashMap<>();
            for (int i = 0; i < killsSize; i++) {
                Identifier id = buf.readIdentifier();
                int amount = buf.readInt();
                kills.put(id, amount);
            }

            boolean completed = buf.readBoolean();

            // Textos GUI
            int guiSize = buf.readInt();
            Map<String, String> guiText = new HashMap<>();
            for (int i = 0; i < guiSize; i++) {
                guiText.put(buf.readString(), buf.readString());
            }

            // Objetivos
            int targetSize = buf.readInt();
            Map<Identifier, Integer> targets = new LinkedHashMap<>();
            for (int i = 0; i < targetSize; i++) {
                targets.put(buf.readIdentifier(), buf.readInt());
            }

            client.execute(() -> {
                client.setScreen(new SrTiempoScreen(kills, completed, guiText, targets));
            });
        });
    }
}
