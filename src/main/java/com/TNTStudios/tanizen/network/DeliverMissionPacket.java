package com.TNTStudios.tanizen.network;

import com.TNTStudios.tanizen.missions.SabioObsidianoMissionData;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.Map;

public class DeliverMissionPacket {
    public static void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        server.execute(() -> {
            SabioObsidianoMissionData data = SabioObsidianoMissionData.load(player);

            boolean entregoAlgo = false;

            for (Map.Entry<Item, Integer> entry : data.getRequiredItems().entrySet()) {
                Item item = entry.getKey();
                int required = entry.getValue();
                int entregados = data.getDelivered().getOrDefault(item, 0);
                int restantes = required - entregados;

                if (restantes <= 0) continue;

                int countInInv = countItem(player, item);

                if (countInInv > 0) {
                    int toRemove = Math.min(restantes, countInInv);
                    removeItem(player, item, toRemove);
                    data.tryDeliverItem(item, toRemove);
                    entregoAlgo = true;
                }
            }

            data.save(player);

            if (entregoAlgo) {
                player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                player.sendMessage(Text.of("§aHas entregado algunos objetos para la misión."), false);
            }

            if (data.isCompleted()) {
                player.playSound(SoundEvents.BLOCK_BELL_USE, 1f, 1f);
                player.sendMessage(Text.of("§6¡Has completado la misión 'El Legado del Sabio Obsidiano'!"), false);
                player.getInventory().insertStack(new ItemStack(Item.byRawId(116))); // mesa de encantamientos
            }
        });
    }

    private static int countItem(ServerPlayerEntity player, Item item) {
        return (int) player.getInventory().main.stream()
                .filter(stack -> stack.getItem() == item)
                .mapToInt(ItemStack::getCount)
                .sum();
    }

    private static void removeItem(ServerPlayerEntity player, Item item, int amount) {
        int removed = 0;

        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() == item) {
                int toRemove = Math.min(stack.getCount(), amount - removed);
                stack.decrement(toRemove);
                removed += toRemove;
                if (removed >= amount) break;
            }
        }
    }
}
