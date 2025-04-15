package com.TNTStudios.tanizen.mixin;

import com.TNTStudios.playertimelimit.Playertimelimit;
import com.TNTStudios.playertimelimit.api.PlayerTimeLimitAPI;
import com.TNTStudios.tanizen.missions.SrTiempoMissionData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "onDeath", at = @At("TAIL"))
    private void onDeathInject(DamageSource source, CallbackInfo ci) {
        if (!(source.getAttacker() instanceof ServerPlayerEntity player)) return;

        LivingEntity self = (LivingEntity)(Object)this;

        if (!self.getWorld().isClient) {
            SrTiempoMissionData data = SrTiempoMissionData.load(player);
            if (!data.isMissionActivated()) return;

            boolean justCompleted = data.tryAddKill(self.getType());


            if (justCompleted && !data.isCompletedToday()) {
                data.setCompletedToday(true);
                PlayerTimeLimitAPI api = Playertimelimit.getAPI();
                api.addTime(player.getUuid(), 3600); // 1 hora = 3600 segundos
                player.sendMessage(Text.of("§a¡Has completado la misión diaria! Se te ha añadido 1 hora."), false);
            }

            data.save(player);
        }
    }
}
