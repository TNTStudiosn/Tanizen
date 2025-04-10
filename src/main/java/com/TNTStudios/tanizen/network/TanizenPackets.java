package com.TNTStudios.tanizen.network;

import com.TNTStudios.tanizen.missions.SabioObsidianoMissionData;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.network.PacketByteBuf;

import java.util.Map;


public class TanizenPackets {
    public static final Identifier OPEN_DIALOG_SCREEN = new Identifier("tanizen", "open_dialog");
    public static final Identifier DELIVER_MISSION_PACKET = new Identifier("tanizen", "deliver_mission");

    public static void openDialog(ServerPlayerEntity player, SabioObsidianoMissionData data) {
        PacketByteBuf buf = PacketByteBufs.create();
        // Escribir el estado de la misión en el paquete
        buf.writeUuid(player.getUuid());
        buf.writeBoolean(data.isCompleted());
        buf.writeInt(data.getDelivered().size());
        for (Map.Entry<Item, Integer> entry : data.getDelivered().entrySet()) {
            buf.writeItemStack(new ItemStack(entry.getKey())); // Ítem
            buf.writeInt(entry.getValue()); // Cantidad entregada
        }
        ServerPlayNetworking.send(player, OPEN_DIALOG_SCREEN, buf);
    }

    public static void deliverMission(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        ServerPlayNetworking.send(player, DELIVER_MISSION_PACKET, buf);
    }
}