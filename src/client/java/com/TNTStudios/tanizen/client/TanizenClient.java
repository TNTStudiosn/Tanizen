package com.TNTStudios.tanizen.client;

import net.fabricmc.api.ClientModInitializer;
import com.TNTStudios.tanizen.client.discord.DiscordPresenceHandler;
public class TanizenClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        DiscordPresenceHandler.init();
    }
}
