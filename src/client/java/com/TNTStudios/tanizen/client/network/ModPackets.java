package com.TNTStudios.tanizen.client.network;

import com.TNTStudios.tanizen.client.gui.SabioObsidianoScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;

public class ModPackets {

    public static final Identifier OPEN_DIALOG_SCREEN = new Identifier("tanizen", "open_dialog");

    public static void registerC2SPackets() {}
    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(OPEN_DIALOG_SCREEN, (client, handler, buf, responseSender) -> {
            client.execute(() -> MinecraftClient.getInstance().setScreen(new SabioObsidianoScreen()));
        });
    }

    public static void sendOpenDialogPacket(ServerPlayerEntity player) {
        PacketByteBuf buf = new PacketByteBuf(net.minecraft.network.PacketByteBufs.create());
        ServerPlayNetworking.send(player, OPEN_DIALOG_SCREEN, buf);
    }
}
