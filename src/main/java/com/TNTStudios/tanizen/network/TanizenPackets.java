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
    public static final Identifier OPEN_SRTIEMPO_SCREEN = new Identifier("tanizen", "open_srtiempo");

    public static void openDialog(ServerPlayerEntity player, SabioObsidianoMissionData data) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(player.getUuid());
        buf.writeBoolean(data.isCompleted());
        buf.writeBoolean(data.isRewardGiven()); // Enviar rewardGiven
        buf.writeInt(data.getDelivered().size());
        for (Map.Entry<Item, Integer> entry : data.getDelivered().entrySet()) {
            buf.writeItemStack(new ItemStack(entry.getKey()));
            buf.writeInt(entry.getValue());
        }
        ServerPlayNetworking.send(player, OPEN_DIALOG_SCREEN, buf);
    }

    public static void deliverMission(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        ServerPlayNetworking.send(player, DELIVER_MISSION_PACKET, buf);
    }

    public static void openSrTiempoScreen(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        ServerPlayNetworking.send(player, OPEN_SRTIEMPO_SCREEN, buf);
    }

}