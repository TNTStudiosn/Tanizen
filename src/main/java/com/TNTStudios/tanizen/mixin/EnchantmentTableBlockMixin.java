package com.TNTStudios.tanizen.mixin;

import com.TNTStudios.tanizen.missions.SabioObsidianoMissionData;
import net.minecraft.block.BlockState;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantingTableBlock.class)
public abstract class EnchantmentTableBlockMixin {

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void restrictIfMissionNotCompleted(BlockState state, World world, BlockPos pos,
                                               PlayerEntity player, Hand hand, BlockHitResult hit,
                                               CallbackInfoReturnable<ActionResult> cir) {
        if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
            SabioObsidianoMissionData data = SabioObsidianoMissionData.load(serverPlayer);
            if (!data.isCompleted()) {
                serverPlayer.sendMessage(Text.literal("§cCompleta la misión primero."), true); // Action bar
                world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(), SoundCategory.BLOCKS, 1.0f, 0.5f);
                cir.setReturnValue(ActionResult.FAIL);
            }
        }
    }
}
