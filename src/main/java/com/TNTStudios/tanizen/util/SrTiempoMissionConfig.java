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
    public static Map<Identifier,Integer> itemTargets = new LinkedHashMap<>();
    // Configuración de compra
    public static int buyCost = 1;  //Costo por defecto si no está en JSON
    public static Identifier buyItem = new Identifier("bsroleplay", "gold_coin"); //Ítem por defecto

    public static void load() {
        mobTargets.clear();
        itemTargets.clear();
        guiText.clear();

        try (Reader reader = new FileReader(CONFIG_PATH.toFile())) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

            // Cargar objetivos de mobs
            JsonArray targets = json.getAsJsonArray("targets");
            for (JsonElement el : targets) {
                JsonObject obj = el.getAsJsonObject();
                Identifier type = new Identifier(obj.get("entity_type").getAsString());
                int amount = obj.get("amount").getAsInt();
                if (mobTargets.size() < 7) {
                    mobTargets.put(type, amount);
                }
            }

            // Cargar textos de GUI
            JsonObject gui = json.getAsJsonObject("gui_text");
            for (Map.Entry<String, JsonElement> entry : gui.entrySet()) {
                guiText.put(entry.getKey(), entry.getValue().getAsString());
            }

            // Cargar configuración de compra
            if (json.has("buy_cost")) {
                buyCost = json.get("buy_cost").getAsInt();
            }
            if (json.has("buy_item")) {
                buyItem = new Identifier(json.get("buy_item").getAsString());
            }

            // + parsear item_targets
            if (json.has("item_targets")) {
                JsonArray items = json.getAsJsonArray("item_targets");
                for (JsonElement el : items) {
                    JsonObject obj = el.getAsJsonObject();
                    Identifier id = new Identifier(obj.get("item").getAsString());
                    int amt = obj.get("amount").getAsInt();
                    itemTargets.put(id, amt);
                }
            }

        } catch (Exception e) {
            System.err.println("[TaniMod] Error al cargar srtiempo_config.json: " + e.getMessage());
        }
    }
}
