package com.TNTStudios.tanizen;

import com.TNTStudios.tanizen.commands.SrTiempoCommand;
import com.TNTStudios.tanizen.entity.SabioObsidianoEntity;
import com.TNTStudios.tanizen.entity.SrTiempoEntity;
import com.TNTStudios.tanizen.missions.SrTiempoMissionData;
import com.TNTStudios.tanizen.network.DeliverMissionPacket;
import com.TNTStudios.tanizen.network.TanizenPackets;
import com.TNTStudios.tanizen.registry.TanizenEntities;
import com.TNTStudios.tanizen.util.TimeLimitConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.server.network.ServerPlayerEntity;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class Tanizen implements ModInitializer {
    private static int lastResetDay = -1;

    @Override
    public void onInitialize() {
        TimeLimitConfig.load();
        TanizenEntities.register();
        FabricDefaultAttributeRegistry.register(TanizenEntities.SABIO_OBSIDIANO, SabioObsidianoEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(TanizenEntities.SRTIEMPO_NPC, SrTiempoEntity.createAttributes());
        ServerPlayNetworking.registerGlobalReceiver(
                TanizenPackets.DELIVER_MISSION_PACKET,
                DeliverMissionPacket::handle
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
