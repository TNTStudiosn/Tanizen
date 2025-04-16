package com.TNTStudios.tanizen;

import com.TNTStudios.playertimelimit.Playertimelimit;
import com.TNTStudios.playertimelimit.api.PlayerTimeLimitAPI;
import com.TNTStudios.tanizen.commands.SrTiempoCommand;
import com.TNTStudios.tanizen.entity.SabioObsidianoEntity;
import com.TNTStudios.tanizen.entity.SrTiempoEntity;
import com.TNTStudios.tanizen.missions.SrTiempoMissionData;
import com.TNTStudios.tanizen.network.DeliverMissionPacket;
import com.TNTStudios.tanizen.network.TanizenPackets;
import com.TNTStudios.tanizen.registry.TanizenEntities;
import com.TNTStudios.tanizen.util.SrTiempoMissionConfig;
import com.TNTStudios.tanizen.util.TimeLimitConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class Tanizen implements ModInitializer {
    private static int lastResetDay = -1;

    @Override
    public void onInitialize() {
        SrTiempoMissionConfig.load();
        TimeLimitConfig.load();
        TanizenEntities.register();
        FabricDefaultAttributeRegistry.register(TanizenEntities.SABIO_OBSIDIANO, SabioObsidianoEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(TanizenEntities.SRTIEMPO_NPC, SrTiempoEntity.createAttributes());
        ServerPlayNetworking.registerGlobalReceiver(
                TanizenPackets.DELIVER_MISSION_PACKET,
                DeliverMissionPacket::handle
        );

        ServerPlayNetworking.registerGlobalReceiver(
                TanizenPackets.REQUEST_START_SRTIEMPO,
                (server, player, handler, buf, responseSender) -> {
                    server.execute(() -> {
                        SrTiempoMissionData data = SrTiempoMissionData.load(player);
                        if (data.isCompletedToday()) {
                            player.sendMessage(Text.of("§cYa has obtenido tu hora por hoy. Vuelve mañana."), false);
                            return;
                        }
                        data.activateMission();
                        data.save(player);
                        TanizenPackets.sendSrTiempoProgress(player, data);
                    });
                }
        );

        ServerPlayNetworking.registerGlobalReceiver(
                TanizenPackets.REQUEST_BUY_HOUR,
                (server, player, handler, buf, responseSender) -> {
                    server.execute(() -> {
                        SrTiempoMissionData data = SrTiempoMissionData.load(player);
                        if (data.isCompletedToday()) {
                            player.sendMessage(Text.of("§cYa has obtenido tu hora por hoy. Vuelve mañana."), false);
                            return;
                        }
                        // Comprobamos y quitamos monedas
                        int cost = SrTiempoMissionConfig.buyCost;
                        Item coin = Registries.ITEM.get(SrTiempoMissionConfig.buyItem);
                        // método auxiliar para contar
                        int have = player.getInventory().main.stream()
                                .filter(s -> s.getItem() == coin).mapToInt(s -> s.getCount()).sum();
                        if (have < cost) {
                            player.sendMessage(Text.of("§cNo tienes suficientes " + coin.getName().getString()), false);
                            return;
                        }
                        // quitar monedas
                        int toRemove = cost;
                        for (int i = 0; i < player.getInventory().size() && toRemove > 0; i++) {
                            ItemStack stack = player.getInventory().getStack(i);
                            if (stack.getItem() == coin) {
                                int d = Math.min(stack.getCount(), toRemove);
                                stack.decrement(d);
                                toRemove -= d;
                            }
                        }
                        // agregar hora
                        PlayerTimeLimitAPI api = Playertimelimit.getAPI();
                        api.addTime(player.getUuid(), 3600);
                        player.sendMessage(Text.of("§aHas comprado 1 hora extra por " + cost + " monedas."), false);
                        data.setCompletedToday(true);
                        data.save(player);
                    });
                }
        );

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            long current = System.currentTimeMillis();
            ZonedDateTime now = ZonedDateTime.ofInstant(Instant.ofEpochMilli(current), TimeLimitConfig.resetZone);

            // Condición de reinicio: hora pasada y día no igual al último reiniciado
            if (now.toLocalTime().isAfter(TimeLimitConfig.resetTime) && now.getDayOfYear() != lastResetDay) {
                lastResetDay = now.getDayOfYear();

                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    SrTiempoMissionData data = SrTiempoMissionData.load(player);
                    data.resetAll();
                    data.save(player);
                }
                System.out.println("[Tanizen] Misiones reiniciadas automáticamente a las " + now.toLocalTime());
            }
        });


        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            SrTiempoCommand.register(dispatcher);
        });

    }
}
