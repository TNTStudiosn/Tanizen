package com.TNTStudios.tanizen.entity;

import com.TNTStudios.tanizen.missions.SabioObsidianoMissionData;
import com.TNTStudios.tanizen.network.TanizenPackets;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;

import java.util.Map;

public class SabioObsidianoEntity extends PathAwareEntity implements GeoAnimatable {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public SabioObsidianoEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!player.getWorld().isClient && hand == Hand.MAIN_HAND && player instanceof ServerPlayerEntity serverPlayer) {
            SabioObsidianoMissionData data = SabioObsidianoMissionData.load(serverPlayer);

            boolean gaveSomething = false;

            // Verificar si la misión ya está completada
            if (data.isCompleted()) {
                player.sendMessage(Text.of("§aYa has completado esta misión."), false);
            } else {
                // Procesar entregas
                for (Map.Entry<Item, Integer> entry : data.getRequiredItems().entrySet()) {
                    Item item = entry.getKey();
                    int needed = entry.getValue();
                    int alreadyDelivered = data.getDelivered().getOrDefault(item, 0);
                    int missing = needed - alreadyDelivered;

                    // Verifica si el jugador tiene exactamente los que faltan
                    if (missing > 0 && countItem(serverPlayer, item) >= missing) {
                        removeItems(serverPlayer, item, missing);
                        data.tryDeliverItem(item, missing);
                        gaveSomething = true;

                        // Reproduce sonido de entrega y notifica al jugador
                        serverPlayer.playSound(net.minecraft.sound.SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.2F);
                        player.sendMessage(Text.of("§aEntregaste " + missing + "x " + item.getName().getString() + "."), false);
                    }
                }
            }

            // Guardar el progreso
            data.save(serverPlayer);

            // Si completó la misión, dar recompensa y notificar
            if (data.isCompleted()) {
                serverPlayer.playSound(net.minecraft.sound.SoundEvents.BLOCK_BELL_USE, 1.0F, 0.8F);
                player.sendMessage(Text.of("§6¡Misión completada! Recibiste una mesa de encantamientos."), false);

                // Recompensa: Mesa de encantamientos con manejo de inventario lleno
                ItemStack reward = new ItemStack(Items.ENCHANTING_TABLE);
                if (!serverPlayer.getInventory().insertStack(reward)) {
                    serverPlayer.dropItem(reward, false);
                    player.sendMessage(Text.of("§eTu inventario está lleno, la mesa de encantamientos está en el suelo."), false);
                }
            }

            // Abre la GUI en cliente con los datos actualizados
            TanizenPackets.openDialog(serverPlayer, data);

            return gaveSomething ? ActionResult.SUCCESS : ActionResult.PASS;
        }

        return super.interactMob(player, hand);
    }


    private int countItem(PlayerEntity player, Item item) {
        return player.getInventory().main.stream()
                .filter(stack -> stack.getItem() == item)
                .mapToInt(ItemStack::getCount)
                .sum();
    }

    private void removeItems(PlayerEntity player, Item item, int amount) {
        int toRemove = amount;
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() == item) {
                int removed = Math.min(stack.getCount(), toRemove);
                stack.decrement(removed);
                toRemove -= removed;
                if (toRemove <= 0) break;
            }
        }
    }


    @Override
    public boolean isAiDisabled() {
        return true;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object animatable) {
        return age;
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> state) {
        state.setAnimation(RawAnimation.begin().thenLoop("animation.sabio.idle"));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0); // no se mueve
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }

}
