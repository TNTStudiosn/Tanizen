package com.TNTStudios.tanizen.client.model;

import com.TNTStudios.tanizen.entity.SrTiempoEntity;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.util.Identifier;

public class SrTiempoModel extends GeoModel<SrTiempoEntity> {

    @Override
    public Identifier getModelResource(SrTiempoEntity entity) {
        return new Identifier("tanizen", "geo/srtiempo.geo.json");
    }

    @Override
    public Identifier getTextureResource(SrTiempoEntity entity) {
        return new Identifier("tanizen", "textures/entity/srtiempo.png");
    }

    @Override
    public Identifier getAnimationResource(SrTiempoEntity entity) {
        return new Identifier("tanizen", "animations/srtiempo.animation.json");
    }
}
