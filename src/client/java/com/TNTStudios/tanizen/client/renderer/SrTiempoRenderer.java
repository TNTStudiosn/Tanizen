package com.TNTStudios.tanizen.client.renderer;

import com.TNTStudios.tanizen.client.model.SrTiempoModel;
import com.TNTStudios.tanizen.entity.SrTiempoEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.util.Identifier;

public class SrTiempoRenderer extends GeoEntityRenderer<SrTiempoEntity> {
    public SrTiempoRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new SrTiempoModel());
        this.shadowRadius = 0.5f; // Ajusta según el tamaño y estética deseados
    }

    @Override
    public boolean hasLabel(SrTiempoEntity entity) {
        return true;
    }

    public RenderLayer getRenderType(SrTiempoEntity entity, Identifier texture) {
        return RenderLayer.getEntityTranslucent(texture);
    }
}
