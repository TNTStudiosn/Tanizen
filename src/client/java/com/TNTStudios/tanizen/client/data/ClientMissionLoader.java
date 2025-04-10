package com.TNTStudios.tanizen.client.data;

import com.TNTStudios.tanizen.missions.SabioObsidianoMissionData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class ClientMissionLoader {

    public static SabioObsidianoMissionData loadFromClient(ClientPlayerEntity player) {
        SabioObsidianoMissionData data = new SabioObsidianoMissionData(player.getUuid());

        Path file = SabioObsidianoMissionData.getSavePath(player.getUuid());

        if (Files.exists(file)) {
            try (Reader reader = Files.newBufferedReader(file)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

                JsonObject entregados = json.getAsJsonObject("entregados");
                for (Map.Entry<String, JsonElement> entry : entregados.entrySet()) {
                    Identifier id = new Identifier(entry.getKey());
                    if (Registries.ITEM.containsId(id)) {
                        Item item = Registries.ITEM.get(id);
                        data.getDelivered().put(item, entry.getValue().getAsInt());
                    }
                }

                data.setCompleted(json.get("completado").getAsBoolean());
            } catch (IOException ignored) {}
        }

        return data;
    }
}
