package com.TNTStudios.tanizen.registry;

import com.TNTStudios.tanizen.entity.SabioObsidianoEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


public class TanizenEntities {
    public static final EntityType<SabioObsidianoEntity> SABIO_OBSIDIANO = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("tanizen", "sabio_obsidiano"),
            EntityType.Builder.create(SabioObsidianoEntity::new, SpawnGroup.CREATURE)
                    .setDimensions(0.6f, 1.95f)
                    .build("sabio_obsidiano")

    );

    public static void register() {}
}
