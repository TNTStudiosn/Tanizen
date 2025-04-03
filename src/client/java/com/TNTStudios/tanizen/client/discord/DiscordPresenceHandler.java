package com.TNTStudios.tanizen.client.discord;

import com.hypherionmc.craterlib.core.rpcsdk.DiscordRichPresence;
import com.hypherionmc.craterlib.core.rpcsdk.DiscordRPC;
import com.hypherionmc.craterlib.core.rpcsdk.helpers.RPCButton;

public class DiscordPresenceHandler {

    private static boolean initialized = false;
    private static final String APP_ID = "1325598588905979964";

    @SuppressWarnings("removal")
    public static void init() {
        if (initialized) return;

        DiscordRPC.INSTANCE.Discord_Initialize(APP_ID, null, true, null);
        updatePresence();
        initialized = true;
    }

    @SuppressWarnings("removal")
    public static void updatePresence() {
        DiscordRichPresence presence = new DiscordRichPresence();

        // 🏗️ Añadir espacio entre detalles y estado usando \n
        presence.details = "👾Servidor de Tanizen🐲";
        presence.state = "🌌𝗛𝗢𝗦𝗧𝗘𝗔𝗗𝗢 𝗘𝗡 𝗛𝗢𝗟𝗬𝗛𝗢𝗦𝗧𝗜𝗡𝗚🌌";
        presence.startTimestamp = System.currentTimeMillis() / 1000L;
        presence.largeImageKey = "tanizen";
        presence.largeImageText = "🌟Serie  de Minecraft🌟";
        presence.smallImageKey = "icono";
        presence.smallImageText = "🚀TNTStudios🚀";

        // 🔥 Crear y asignar los botones correctamente
        RPCButton button1 = RPCButton.create("🔥 HolyHosting 🔥", "https://www.holy.gg/");
        //RPCButton button2 = RPCButton.create("⚡ TNTStudios ⚡", "https://tntstudiosn.space/");

        presence.button_label_1 = button1.getLabel();
        presence.button_url_1 = button1.getUrl();
        //presence.button_label_2 = button2.getLabel();
        //presence.button_url_2 = button2.getUrl();

        DiscordRPC.INSTANCE.Discord_UpdatePresence(presence);
    }

    @SuppressWarnings("removal")
    public static void tick() {
        DiscordRPC.INSTANCE.Discord_RunCallbacks();
    }

    @SuppressWarnings("removal")
    public static void shutdown() {
        DiscordRPC.INSTANCE.Discord_Shutdown();
    }
}