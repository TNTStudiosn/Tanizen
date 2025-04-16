package com.TNTStudios.tanizen.missions;

import com.TNTStudios.tanizen.util.SrTiempoMissionConfig;
import com.google.gson.*;
import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SrTiempoMissionData {
    private static final Path SAVE_FOLDER = Paths.get("config", "tanizen", "srtiempo_missions");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final UUID uuid;
    private final Map<Identifier, Integer> kills = new HashMap<>();
    private final Map<Identifier, Integer> itemsDelivered = new HashMap<>();
    private boolean completedToday = false;
    private boolean missionActivated = false;

    public SrTiempoMissionData(UUID uuid) {
        this.uuid = uuid;
    }

    public static SrTiempoMissionData load(ServerPlayerEntity player) {
        try {
            Files.createDirectories(SAVE_FOLDER);
            Path file = SAVE_FOLDER.resolve(player.getUuidAsString() + "_srtiempo.json");
            if (Files.exists(file)) {
                try (Reader reader = Files.newBufferedReader(file)) {
                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                    SrTiempoMissionData data = new SrTiempoMissionData(player.getUuid());

                    // Cargar kills (mobs)
                    if (json.has("kills")) {
                        JsonObject killsObj = json.getAsJsonObject("kills");
                        for (Map.Entry<String, JsonElement> entry : killsObj.entrySet()) {
                            Identifier id = new Identifier(entry.getKey());
                            data.kills.put(id, entry.getValue().getAsInt());
                        }
                    }

                    // Cargar deliveredItems (entregas de ítems)
                    if (json.has("deliveredItems")) {
                        JsonObject itemsObj = json.getAsJsonObject("deliveredItems");
                        for (Map.Entry<String, JsonElement> entry : itemsObj.entrySet()) {
                            Identifier id = new Identifier(entry.getKey());
                            data.itemsDelivered.put(id, entry.getValue().getAsInt());
                        }
                    }

                    // Estado de la misión
                    data.completedToday = json.get("completedToday").getAsBoolean();
                    data.missionActivated = json.has("missionActivated")
                            && json.get("missionActivated").getAsBoolean();

                    return data;
                }
            }
        } catch (IOException ignored) {}
        return new SrTiempoMissionData(player.getUuid());
    }

    public void save(ServerPlayerEntity player) {
        try {
            Files.createDirectories(SAVE_FOLDER);
            Path file = SAVE_FOLDER.resolve(uuid.toString() + "_srtiempo.json");
            JsonObject json = new JsonObject();

            json.addProperty("uuid", uuid.toString());
            json.addProperty("completedToday", completedToday);
            json.addProperty("missionActivated", missionActivated);

            // Guardar kills (mobs)
            JsonObject killsObj = new JsonObject();
            for (Map.Entry<Identifier, Integer> e : kills.entrySet()) {
                killsObj.addProperty(e.getKey().toString(), e.getValue());
            }
            json.add("kills", killsObj);

            // Guardar deliveredItems (entregas de ítems)
            JsonObject itemsObj = new JsonObject();
            for (Map.Entry<Identifier, Integer> e : itemsDelivered.entrySet()) {
                itemsObj.addProperty(e.getKey().toString(), e.getValue());
            }
            json.add("deliveredItems", itemsObj);

            try (Writer writer = Files.newBufferedWriter(file)) {
                GSON.toJson(json, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean tryAddKill(EntityType<?> type) {
        if (completedToday) return false;

        Identifier id = EntityType.getId(type);
        int required = SrTiempoMissionConfig.mobTargets.getOrDefault(id, -1);
        if (required <= 0) return false;

        int current = kills.getOrDefault(id, 0);
        if (current >= required) return false;

        kills.put(id, current + 1);
        return isCompleted();
    }

    public boolean tryDeliverItem(Identifier itemId, int amount) {
        if (completedToday) return false;

        int required = SrTiempoMissionConfig.itemTargets.getOrDefault(itemId, -1);
        if (required <= 0) return false;

        int current = itemsDelivered.getOrDefault(itemId, 0);
        if (current >= required) return false;

        int toAdd = Math.min(required - current, amount);
        itemsDelivered.put(itemId, current + toAdd);
        return isCompleted();
    }

    public boolean isCompleted() {
        // Verificar mobs
        for (Map.Entry<Identifier, Integer> entry : SrTiempoMissionConfig.mobTargets.entrySet()) {
            if (kills.getOrDefault(entry.getKey(), 0) < entry.getValue()) {
                return false;
            }
        }
        // Verificar ítems
        for (Map.Entry<Identifier, Integer> entry : SrTiempoMissionConfig.itemTargets.entrySet()) {
            if (itemsDelivered.getOrDefault(entry.getKey(), 0) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    public Map<Identifier, Integer> getKills() {
        return kills;
    }

    public Map<Identifier, Integer> getItemsDelivered() {
        return itemsDelivered;
    }

    public boolean isCompletedToday() {
        return completedToday;
    }

    public void setCompletedToday(boolean completedToday) {
        this.completedToday = completedToday;
    }

    public boolean isMissionActivated() {
        return missionActivated;
    }

    public void activateMission() {
        this.missionActivated = true;
    }

    public void resetAll() {
        this.kills.clear();
        this.itemsDelivered.clear();
        this.completedToday = false;
        this.missionActivated = false;
    }
}
