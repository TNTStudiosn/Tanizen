package com.TNTStudios.tanizen.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.network.PacketByteBuf;


public class TanizenPackets {
    // Canal de comunicación (S2C) para abrir la GUI
    public static final Identifier OPEN_DIALOG_SCREEN = new Identifier("tanizen", "open_dialog");

    // Método para que el servidor envíe un paquete al cliente
    public static void openDialog(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        // No mandamos info adicional, pero podrías escribir datos en buf aquí
        ServerPlayNetworking.send(player, OPEN_DIALOG_SCREEN, buf);
    }
}
