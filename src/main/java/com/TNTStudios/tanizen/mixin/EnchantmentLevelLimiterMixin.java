package com.TNTStudios.tanizen.mixin;

import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentScreenHandler.class)
public class EnchantmentLevelLimiterMixin {

    @Inject(method = "onButtonClick", at = @At("HEAD"), cancellable = true)
    private void blockHighLevelEnchantments(PlayerEntity player, int id, CallbackInfoReturnable<Boolean> cir) {
        if (id > 0) {
            player.sendMessage(Text.of("§cSolo puedes encantar al nivel 1."), true); // Mensaje corto y en action bar
            player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(), SoundCategory.PLAYERS, 1.0f, 0.5f);
            cir.setReturnValue(false);
        }
    }
}
