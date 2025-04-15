package com.TNTStudios.tanizen.util;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Map;

public class TimeLimitConfig {
    public static LocalTime resetTime = LocalTime.of(11, 59); // fallback
    public static ZoneId resetZone = ZoneId.of("Europe/Madrid"); // fallback

    public static void load() {
        try {
            File file = new File("config/playertimelimit.yaml");
            if (!file.exists()) return;

            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(new FileInputStream(file));

            if (config.containsKey("reinicio")) {
                Map<String, Object> reinicio = (Map<String, Object>) config.get("reinicio");

                String hora = (String) reinicio.get("hora");
                String zona = (String) reinicio.get("zonaHoraria");

                if (hora != null && hora.matches("\\d{1,2}:\\d{2}")) {
                    String[] parts = hora.split(":");
                    int hour = Integer.parseInt(parts[0]);
                    int minute = Integer.parseInt(parts[1]);
                    resetTime = LocalTime.of(hour, minute);
                }

                if (zona != null) {
                    resetZone = ZoneId.of(zona);
                }
            }

        } catch (Exception e) {
            System.err.println("[Tanizen] No se pudo leer playertimelimit.yaml: " + e.getMessage());
        }
    }
}
