package com.TNTStudios.tanizen.entity;

import com.TNTStudios.playertimelimit.Playertimelimit;
import com.TNTStudios.tanizen.missions.SrTiempoMissionData;
import com.TNTStudios.tanizen.network.TanizenPackets;
import com.TNTStudios.tanizen.util.SrTiempoMissionConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.tag.DamageTypeTags;

public class SrTiempoEntity extends PathAwareEntity implements GeoAnimatable {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public SrTiempoEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }


    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!player.getWorld().isClient && player instanceof ServerPlayerEntity serverPlayer) {
            SrTiempoMissionData data = SrTiempoMissionData.load(serverPlayer);

            // 1️⃣ Si ya completó hoy, avisar y salir
            if (data.isCompletedToday()) {
                serverPlayer.sendMessage(Text.of("§cYa has obtenido tu hora por hoy. Vuelve mañana."), false);
                return ActionResult.SUCCESS;
            }

            // 2️⃣ Procesar entrega de ítems si sostiene uno de los objetivos
            ItemStack stack = serverPlayer.getMainHandStack();
            Identifier held = Registries.ITEM.getId(stack.getItem());
            if (stack.getCount() > 0 && SrTiempoMissionConfig.itemTargets.containsKey(held)) {
                int amount = stack.getCount();
                boolean justCompleted = data.tryDeliverItem(held, amount);
                stack.decrement(amount);
                data.save(serverPlayer);

                // Actualizar cliente y notificar entrega
                TanizenPackets.sendSrTiempoProgress(serverPlayer, data);
                serverPlayer.sendMessage(
                        Text.of("§aEntregaste " + amount + "x " + stack.getItem().getName().getString()),
                        false
                );

                // Si con esta entrega completó la misión, dar la hora
                if (justCompleted && !data.isCompletedToday()) {
                    data.setCompletedToday(true);
                    Playertimelimit.getAPI().addTime(serverPlayer.getUuid(), 3600);
                    serverPlayer.sendMessage(Text.of("§6¡Misión completada y recibiste 1h!"), false);
                    data.save(serverPlayer);
                }
                return ActionResult.SUCCESS;
            }

            // 3️⃣ Si no entregó ítems, abrimos el menú de opciones
            TanizenPackets.openSrTiempoOptions(serverPlayer);
            return ActionResult.SUCCESS;
        }
        return super.interactMob(player, hand);
    }



    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.isOf(DamageTypes.OUT_OF_WORLD) && amount == Float.MAX_VALUE) {
            return super.damage(source, amount);
        } else {
            return false;
        }
    }

    @Override
    public void kill() {
        DamageSource outOfWorld = new DamageSource(this.getWorld().getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(DamageTypes.OUT_OF_WORLD));
        this.damage(outOfWorld, Float.MAX_VALUE);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0);
    }

    @Override
    public boolean isAiDisabled() {
        return true;
    }

    @Override
    public double getTick(Object animatable) {
        return age;
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> state) {
        state.setAnimation(RawAnimation.begin().thenLoop("animation.srtiempo.idle"));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }


    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        if (this.isRemoved()) {
            return true; // Permitir despawn si la entidad está marcada como eliminada
        }
        return false;
    }
}