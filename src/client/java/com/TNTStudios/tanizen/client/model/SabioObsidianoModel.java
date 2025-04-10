package com.TNTStudios.tanizen.client.model;

import com.TNTStudios.tanizen.entity.SabioObsidianoEntity;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.util.Identifier;

public class SabioObsidianoModel extends GeoModel<SabioObsidianoEntity> {

    @Override
    public Identifier getModelResource(SabioObsidianoEntity entity) {
        return new Identifier("tanizen", "geo/sabio.geo.json");
    }

    @Override
    public Identifier getTextureResource(SabioObsidianoEntity entity) {
        return new Identifier("tanizen", "textures/entity/sabio.png");
    }

    @Override
    public Identifier getAnimationResource(SabioObsidianoEntity entity) {
        return new Identifier("tanizen", "animations/sabio.animation.json");
    }
}
