package com.TNTStudios.tanizen.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.screen.EnchantmentScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Mixin(EnchantmentScreenHandler.class)
public class EnchantmentScreenForceLevelOneMixin {

    private static final Random random = new Random();

    @ModifyReturnValue(
            method = "generateEnchantments(Lnet/minecraft/item/ItemStack;II)Ljava/util/List;",
            at = @At("RETURN")
    )
    private List<EnchantmentLevelEntry> modifyEnchantmentsToLevelOneOrTwo(List<EnchantmentLevelEntry> original) {
        return original.stream()
                .map(entry -> new EnchantmentLevelEntry(entry.enchantment, 1 + random.nextInt(2)))
                .collect(Collectors.toList());
    }
}
