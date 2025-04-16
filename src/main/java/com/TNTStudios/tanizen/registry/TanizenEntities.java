package com.TNTStudios.tanizen.registry;

import com.TNTStudios.tanizen.entity.SabioObsidianoEntity;
import com.TNTStudios.tanizen.entity.SrTiempoEntity;
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

    public static final EntityType<SrTiempoEntity> SRTIEMPO_NPC = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("tanizen", "srtiempo"),
            EntityType.Builder.create(SrTiempoEntity::new, SpawnGroup.CREATURE)
                    .setDimensions(1.0f, 1.5f) // ancho: 0.6 (normal), alto: 1 bloque
                    .build("srtiempo")
    );

    public static void register() {}
}
