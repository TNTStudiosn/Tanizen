package com.TNTStudios.tanizen.missions;

import com.google.gson.*;
import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SrTiempoMissionData {
    private static final Path SAVE_FOLDER = Paths.get("config", "tanizen", "srtiempo_missions");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final UUID uuid;
    private int zombiesKilled = 0;
    private int creepersKilled = 0;
    private int phantomsKilled = 0;
    private boolean completedToday = false;

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
                    data.zombiesKilled = json.get("zombies").getAsInt();
                    data.creepersKilled = json.get("creepers").getAsInt();
                    data.phantomsKilled = json.get("phantoms").getAsInt();
                    data.completedToday = json.get("completedToday").getAsBoolean();
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
            json.addProperty("zombies", zombiesKilled);
            json.addProperty("creepers", creepersKilled);
            json.addProperty("phantoms", phantomsKilled);
            json.addProperty("completedToday", completedToday);

            try (Writer writer = Files.newBufferedWriter(file)) {
                GSON.toJson(json, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean tryAddKill(EntityType<?> type) {
        if (completedToday) return false;

        if (type == EntityType.ZOMBIE && zombiesKilled < 10) zombiesKilled++;
        else if (type == EntityType.CREEPER && creepersKilled < 10) creepersKilled++;
        else if (type == EntityType.PHANTOM && phantomsKilled < 10) phantomsKilled++;

        return isCompleted();
    }

    public boolean isCompleted() {
        return zombiesKilled >= 10 && creepersKilled >= 10 && phantomsKilled >= 10;
    }

    public void setCompletedToday(boolean completedToday) {
        this.completedToday = completedToday;
    }

    public boolean isCompletedToday() {
        return completedToday;
    }

    public int getZombiesKilled() {
        return zombiesKilled;
    }

    public int getCreepersKilled() {
        return creepersKilled;
    }

    public int getPhantomsKilled() {
        return phantomsKilled;
    }

}
