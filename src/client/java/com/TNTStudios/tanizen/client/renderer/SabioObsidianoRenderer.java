package com.TNTStudios.tanizen.client.renderer;

import com.TNTStudios.tanizen.client.model.SabioObsidianoModel;
import com.TNTStudios.tanizen.entity.SabioObsidianoEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SabioObsidianoRenderer extends GeoEntityRenderer<SabioObsidianoEntity> {
    public SabioObsidianoRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new SabioObsidianoModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public boolean hasLabel(SabioObsidianoEntity entity) {
        return true; // muestra el nombre
    }
}
