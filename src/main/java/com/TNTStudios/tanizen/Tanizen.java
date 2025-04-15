package com.TNTStudios.tanizen;

import com.TNTStudios.tanizen.entity.SabioObsidianoEntity;
import com.TNTStudios.tanizen.entity.SrTiempoEntity;
import com.TNTStudios.tanizen.network.DeliverMissionPacket;
import com.TNTStudios.tanizen.network.TanizenPackets;
import com.TNTStudios.tanizen.registry.TanizenEntities;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

public class Tanizen implements ModInitializer {

    @Override
    public void onInitialize() {
        TanizenEntities.register();
        FabricDefaultAttributeRegistry.register(TanizenEntities.SABIO_OBSIDIANO, SabioObsidianoEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(TanizenEntities.SRTIEMPO_NPC, SrTiempoEntity.createAttributes());
        ServerPlayNetworking.registerGlobalReceiver(
                TanizenPackets.DELIVER_MISSION_PACKET,
                DeliverMissionPacket::handle
        );
    }
}
