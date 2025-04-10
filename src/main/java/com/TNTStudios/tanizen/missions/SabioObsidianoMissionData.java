package com.TNTStudios.tanizen.missions;

import com.google.gson.*;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SabioObsidianoMissionData {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path SAVE_FOLDER = Paths.get("config", "tanizen", "missions");

    public static final String MISSION_ID = "el_legado_del_sabio";

    private static final LinkedHashMap<Item, Integer> REQUIRED_ITEMS = new LinkedHashMap<>() {{
        put(Items.OBSIDIAN, 4);
        put(Items.BOOK, 1);
        put(Items.SUGAR_CANE, 64); // Fragmentos de sabiduría natural
        put(Items.GUNPOWDER, 16);  // Lágrimas de criatura nocturna
        put(Items.ENDER_PEARL, 8); // Esencias sombrías
        put(Items.GOLDEN_APPLE, 1); // Corazón del bosque
    }};

    private final UUID playerUUID;
    private final Map<Item, Integer> delivered = new HashMap<>();
    private boolean completed = false;
    private boolean rewardGiven = false;

    public SabioObsidianoMissionData(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    // Método load actualizado
    public static SabioObsidianoMissionData load(ServerPlayerEntity player) {
        try {
            Files.createDirectories(SAVE_FOLDER);
            Path file = SAVE_FOLDER.resolve(player.getUuidAsString() + ".json");

            if (Files.exists(file)) {
                try (Reader reader = Files.newBufferedReader(file)) {
                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                    SabioObsidianoMissionData data = new SabioObsidianoMissionData(player.getUuid());

                    JsonObject entregados = json.getAsJsonObject("entregados");
                    for (Map.Entry<String, JsonElement> entry : entregados.entrySet()) {
                        Identifier id = new Identifier(entry.getKey());
                        Item item = Item.byRawId(Item.getRawId(Items.AIR)); // default
                        if (Registries.ITEM.containsId(id)) {
                            item = Registries.ITEM.get(id);
                        }
                        data.delivered.put(item, entry.getValue().getAsInt());
                    }

                    data.completed = json.get("completado").getAsBoolean();
                    data.rewardGiven = json.has("rewardGiven") ? json.get("rewardGiven").getAsBoolean() : false; // Cargar rewardGiven
                    return data;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new SabioObsidianoMissionData(player.getUuid());
    }

    public void save(ServerPlayerEntity player) {
        try {
            Files.createDirectories(SAVE_FOLDER);
            Path file = SAVE_FOLDER.resolve(playerUUID.toString() + ".json");

            JsonObject json = new JsonObject();
            JsonObject entregados = new JsonObject();

            json.addProperty("uuid", playerUUID.toString());
            json.addProperty("nombre", player.getEntityName());

            for (Item item : REQUIRED_ITEMS.keySet()) {
                int entregado = delivered.getOrDefault(item, 0);
                String id = Registries.ITEM.getId(item).toString();
                entregados.addProperty(id, entregado);
            }

            json.add("entregados", entregados);
            json.addProperty("completado", completed);
            json.addProperty("rewardGiven", rewardGiven); // Guardar rewardGiven

            try (Writer writer = Files.newBufferedWriter(file)) {
                GSON.toJson(json, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getter y Setter para rewardGiven
    public boolean isRewardGiven() {
        return rewardGiven;
    }

    public void setRewardGiven(boolean rewardGiven) {
        this.rewardGiven = rewardGiven;
    }

    // Resto de los métodos sin cambios
    public boolean isCompleted() {
        return completed;
    }

    public Map<Item, Integer> getDelivered() {
        return delivered;
    }

    public LinkedHashMap<Item, Integer> getRequiredItems() {
        return REQUIRED_ITEMS;
    }

    public boolean tryDeliverItem(Item item, int amount) {
        if (!REQUIRED_ITEMS.containsKey(item)) return false;

        int required = REQUIRED_ITEMS.get(item);
        int current = delivered.getOrDefault(item, 0);
        int deliverable = Math.min(required - current, amount);

        if (deliverable <= 0) return false;

        delivered.put(item, current + deliverable);
        checkCompletion();
        return true;
    }

    private void checkCompletion() {
        this.completed = REQUIRED_ITEMS.entrySet().stream().allMatch(entry ->
                delivered.getOrDefault(entry.getKey(), 0) >= entry.getValue()
        );
    }
    public static Path getSavePath(UUID uuid) {
        return SAVE_FOLDER.resolve(uuid.toString() + ".json");
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

}
