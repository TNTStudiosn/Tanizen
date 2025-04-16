package com.TNTStudios.tanizen.commands;

import com.TNTStudios.tanizen.missions.SrTiempoMissionData;
import com.TNTStudios.tanizen.util.SrTiempoMissionConfig;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SrTiempoCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("srtiempo")
                        .requires(src -> src.hasPermissionLevel(4))
                        .then(
                                CommandManager.literal("reset")
                                        // Reset para un jugador específico
                                        .then(CommandManager.argument("target", EntityArgumentType.player())
                                                .executes(ctx -> {
                                                    ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "target");
                                                    // Cargar y resetear datos del jugador
                                                    SrTiempoMissionData data = SrTiempoMissionData.load(target);
                                                    data.resetAll();
                                                    data.save(target);
                                                    // Eliminar archivo offline de este jugador
                                                    Path file = Paths.get("config", "tanizen", "srtiempo_missions", target.getUuidAsString() + "_srtiempo.json");
                                                    try {
                                                        Files.deleteIfExists(file);
                                                    } catch (Exception e) {
                                                        System.err.println("[TaniMod] No se pudo borrar el archivo de " + target.getName().getString() + ": " + e.getMessage());
                                                    }
                                                    ctx.getSource().sendFeedback(
                                                            () -> Text.of("§eMisión diaria reiniciada para §b" + target.getName().getString()),
                                                            false
                                                    );
                                                    return 1;
                                                })
                                        )
                                        // Reset para todos los jugadores online
                                        .then(CommandManager.literal("@a")
                                                .executes(ctx -> {
                                                    for (ServerPlayerEntity player : ctx.getSource().getServer().getPlayerManager().getPlayerList()) {
                                                        SrTiempoMissionData data = SrTiempoMissionData.load(player);
                                                        data.resetAll();
                                                        data.save(player);
                                                    }
                                                    // Eliminar todos los archivos offline
                                                    Path folder = Paths.get("config", "tanizen", "srtiempo_missions");
                                                    if (Files.exists(folder)) {
                                                        try {
                                                            Files.list(folder)
                                                                    .filter(f -> f.getFileName().toString().endsWith("_srtiempo.json"))
                                                                    .forEach(f -> {
                                                                        try { Files.delete(f); } catch (Exception ignore) {}
                                                                    });
                                                        } catch (Exception e) {
                                                            System.err.println("[TaniMod] No se pudieron limpiar los datos offline: " + e.getMessage());
                                                        }
                                                    }
                                                    ctx.getSource().sendFeedback(
                                                            () -> Text.of("§aSe reinició la misión diaria para todos los jugadores online."),
                                                            false
                                                    );
                                                    return 1;
                                                })
                                        )
                        )
                        .then(
                                CommandManager.literal("reload")
                                        .executes(ctx -> {
                                            // Recargar configuración y reiniciar datos para todos
                                            SrTiempoMissionConfig.load();
                                            ctx.getSource().sendFeedback(
                                                    () -> Text.of("§a[TaniMod] Configuración recargada."),
                                                    true
                                            );
                                            return 1;
                                        })
                        )
                        .then(
                                CommandManager.literal("reloadyreset")
                                        .executes(ctx -> {
                                            // Recargar configuración y reiniciar datos para todos
                                            SrTiempoMissionConfig.load();
                                            for (ServerPlayerEntity player : ctx.getSource().getServer().getPlayerManager().getPlayerList()) {
                                                SrTiempoMissionData data = SrTiempoMissionData.load(player);
                                                data.resetAll();
                                                data.save(player);
                                            }
                                            // Limpiar archivos offline
                                            Path folder = Paths.get("config", "tanizen", "srtiempo_missions");
                                            if (Files.exists(folder)) {
                                                try {
                                                    Files.list(folder)
                                                            .filter(f -> f.getFileName().toString().endsWith("_srtiempo.json"))
                                                            .forEach(f -> {
                                                                try { Files.delete(f); } catch (Exception ignore) {}
                                                            });
                                                } catch (Exception e) {
                                                    System.err.println("[TaniMod] No se pudieron limpiar los datos offline tras reload: " + e.getMessage());
                                                }
                                            }
                                            ctx.getSource().sendFeedback(
                                                    () -> Text.of("§a[TaniMod] Configuración recargada y misiones diarias reiniciadas."),
                                                    true
                                            );
                                            return 1;
                                        })
                        )
        );
    }
}
