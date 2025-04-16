package com.TNTStudios.tanizen.network;

import com.TNTStudios.tanizen.missions.SabioObsidianoMissionData;
import com.TNTStudios.tanizen.missions.SrTiempoMissionData;
import com.TNTStudios.tanizen.util.SrTiempoMissionConfig;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Map;

public class TanizenPackets {
    public static final Identifier OPEN_DIALOG_SCREEN = new Identifier("tanizen", "open_dialog");
    public static final Identifier DELIVER_MISSION_PACKET = new Identifier("tanizen", "deliver_mission");
    public static final Identifier OPEN_SRTIEMPO_SCREEN = new Identifier("tanizen", "open_srtiempo");
    public static final Identifier MISSION_PROGRESS_SRTIEMPO = new Identifier("tanizen", "srtiempo_progress");
    public static final Identifier OPEN_SRTIEMPO_OPTIONS    = new Identifier("tanizen", "open_srtiempo_options");
    public static final Identifier REQUEST_START_SRTIEMPO    = new Identifier("tanizen", "request_start_srtiempo");
    public static final Identifier REQUEST_BUY_HOUR          = new Identifier("tanizen", "request_buy_hour");

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

    public static void openSrTiempoOptions(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        ServerPlayNetworking.send(player, OPEN_SRTIEMPO_OPTIONS, buf);
    }

    public static void sendSrTiempoProgress(ServerPlayerEntity player, SrTiempoMissionData data) {
        PacketByteBuf buf = PacketByteBufs.create();

        // Nuevo: kills por Identifier
        Map<Identifier, Integer> kills = data.getKills();
        buf.writeInt(kills.size());
        for (Map.Entry<Identifier, Integer> entry : kills.entrySet()) {
            buf.writeIdentifier(entry.getKey());
            buf.writeInt(entry.getValue());
        }

        // Estado completado
        buf.writeBoolean(data.isCompletedToday());

        // Textos GUI
        Map<String, String> gui = SrTiempoMissionConfig.guiText;
        buf.writeInt(gui.size());
        gui.forEach((key, value) -> {
            buf.writeString(key);
            buf.writeString(value);
        });

        // Objetivos de mobs
        Map<Identifier, Integer> mobs = SrTiempoMissionConfig.mobTargets;
        buf.writeInt(mobs.size());
        mobs.forEach((id, amount) -> {
            buf.writeIdentifier(id);
            buf.writeInt(amount);
        });

        ServerPlayNetworking.send(player, MISSION_PROGRESS_SRTIEMPO, buf);
    }
}
