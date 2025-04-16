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
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class TanizenClient implements ClientModInitializer {
    public static SabioObsidianoMissionData clientMissionData;

    @Override
    public void onInitializeClient() {
        DiscordPresenceHandler.init();
        EntityRendererRegistry.register(TanizenEntities.SABIO_OBSIDIANO, SabioObsidianoRenderer::new);
        EntityRendererRegistry.register(TanizenEntities.SRTIEMPO_NPC, SrTiempoRenderer::new);

        // Sabio Obsidiano Packet
        ClientPlayNetworking.registerGlobalReceiver(
                TanizenPackets.OPEN_DIALOG_SCREEN,
                (client, handler, buf, responseSender) -> {
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
                }
        );

        // Opciones Sr. Tiempo
        ClientPlayNetworking.registerGlobalReceiver(
                TanizenPackets.OPEN_SRTIEMPO_OPTIONS,
                (client, handler, buf, responseSender) -> {
                    int buyCost = buf.readInt();
                    Identifier buyItem = buf.readIdentifier();
                    client.execute(() -> {
                        client.setScreen(new OptionSelectionScreen(buyCost, buyItem));
                    });
                }
        );

        // Sr. Tiempo – progreso y entrega de ítems
        ClientPlayNetworking.registerGlobalReceiver(
                TanizenPackets.MISSION_PROGRESS_SRTIEMPO,
                (client, handler, buf, responseSender) -> {
                    // 1️⃣ Leer kills (muertes)
                    int killsSize = buf.readInt();
                    Map<Identifier, Integer> kills = new LinkedHashMap<>();
                    for (int i = 0; i < killsSize; i++) {
                        kills.put(buf.readIdentifier(), buf.readInt());
                    }

                    // 2️⃣ Leer deliveredItems (entregas de ítems)
                    int deliveredSize = buf.readInt();
                    Map<Identifier, Integer> delivered = new LinkedHashMap<>();
                    for (int i = 0; i < deliveredSize; i++) {
                        delivered.put(buf.readIdentifier(), buf.readInt());
                    }

                    // 3️⃣ Estado completado hoy
                    boolean completed = buf.readBoolean();

                    // 4️⃣ Textos de la GUI
                    int guiSize = buf.readInt();
                    Map<String, String> guiText = new HashMap<>();
                    for (int i = 0; i < guiSize; i++) {
                        guiText.put(buf.readString(), buf.readString());
                    }

                    // 5️⃣ Objetivos de mobs
                    int mobTargetSize = buf.readInt();
                    Map<Identifier, Integer> mobTargets = new LinkedHashMap<>();
                    for (int i = 0; i < mobTargetSize; i++) {
                        mobTargets.put(buf.readIdentifier(), buf.readInt());
                    }

                    // 6️⃣ Objetivos de ítems
                    int itemTargetSize = buf.readInt();
                    Map<Identifier, Integer> itemTargets = new LinkedHashMap<>();
                    for (int i = 0; i < itemTargetSize; i++) {
                        itemTargets.put(buf.readIdentifier(), buf.readInt());
                    }

                    client.execute(() -> {
                        MinecraftClient mc = MinecraftClient.getInstance();
                        if (mc.currentScreen instanceof SrTiempoScreen screen) {
                            // Ya está abierta: actualizamos datos
                            screen.updateData(
                                    kills,
                                    delivered,
                                    completed,
                                    guiText,
                                    mobTargets,
                                    itemTargets
                            );
                        } else {
                            // Abrimos por primera vez
                            mc.setScreen(new SrTiempoScreen(
                                    kills,
                                    delivered,
                                    completed,
                                    guiText,
                                    mobTargets,
                                    itemTargets
                            ));
                            // Y pedimos entrega de ítems sólo una vez
                            ClientPlayNetworking.send(
                                    TanizenPackets.REQUEST_DELIVER_SRTIEMPO_ITEMS,
                                    PacketByteBufs.create()
                            );
                        }
                    });
                }
        );
    }
}
