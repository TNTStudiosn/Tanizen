package com.TNTStudios.tanizen.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.EndPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndPortalBlock.class)
public class EndPortalBlockMixin {

    @Inject(
            method = "onEntityCollision(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onEntityCollision(
            BlockState state,
            World world,
            BlockPos pos,
            Entity entity,
            CallbackInfo ci
    ) {
        // Solo en servidor y solo jugadores
        if (!(world instanceof ServerWorld) || !(entity instanceof ServerPlayerEntity player)) {
            return;
        }


        // Mensaje en action bar, en rojo
        player.sendMessage(
                Text.literal("[END] aún no disponible").formatted(Formatting.RED),
                /*actionBar=*/ true
        );


        // Evita el teletransporte
        ci.cancel();
    }
}
