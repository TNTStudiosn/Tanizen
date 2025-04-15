package com.TNTStudios.tanizen.commands;

import com.TNTStudios.tanizen.missions.SrTiempoMissionData;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

public class SrTiempoCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("srtiempo")
                .requires(src -> src.hasPermissionLevel(4)) // permisos
                .then(CommandManager.literal("reset")
                        .then(CommandManager.argument("target", net.minecraft.command.argument.EntityArgumentType.player())
                                .executes(ctx -> {
                                    ServerPlayerEntity player = net.minecraft.command.argument.EntityArgumentType.getPlayer(ctx, "target");
                                    SrTiempoMissionData data = SrTiempoMissionData.load(player);
                                    data.setCompletedToday(false);
                                    data.save(player);
                                    ctx.getSource().sendFeedback(() -> Text.of("§eReiniciada misión para §b" + player.getName().getString()), false);
                                    return 1;
                                })
                        )
                        .then(CommandManager.literal("@a")
                                .executes(ctx -> {
                                    for (ServerPlayerEntity player : ctx.getSource().getServer().getPlayerManager().getPlayerList()) {
                                        SrTiempoMissionData data = SrTiempoMissionData.load(player);
                                        data.setCompletedToday(false);
                                        data.save(player);
                                    }
                                    ctx.getSource().sendFeedback(() -> Text.of("§aSe reinició la misión para todos los jugadores conectados."), false);
                                    return 1;
                                })
                        )
                )
        );
    }
}
