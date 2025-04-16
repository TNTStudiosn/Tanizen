package com.TNTStudios.tanizen.util;

import com.google.gson.*;
import net.minecraft.util.Identifier;

import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SrTiempoMissionConfig {
    private static final Path CONFIG_PATH = Paths.get("config", "tanizen", "srtiempo_config.json");

    public static Map<Identifier, Integer> mobTargets = new LinkedHashMap<>();
    public static Map<String, String> guiText = new HashMap<>();

    public static void load() {
        mobTargets.clear();
        guiText.clear();

        try (Reader reader = new FileReader(CONFIG_PATH.toFile())) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

            JsonArray targets = json.getAsJsonArray("targets");
            for (JsonElement el : targets) {
                JsonObject obj = el.getAsJsonObject();
                Identifier type = new Identifier(obj.get("entity_type").getAsString());
                int amount = obj.get("amount").getAsInt();
                if (mobTargets.size() < 7) {
                    mobTargets.put(type, amount);
                }
            }

            JsonObject gui = json.getAsJsonObject("gui_text");
            for (Map.Entry<String, JsonElement> entry : gui.entrySet()) {
                guiText.put(entry.getKey(), entry.getValue().getAsString());
            }

        } catch (Exception e) {
            System.err.println("[TaniMod] Error al cargar srtiempo_config.json: " + e.getMessage());
        }
    }
}
