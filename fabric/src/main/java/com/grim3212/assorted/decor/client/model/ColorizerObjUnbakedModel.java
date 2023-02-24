package com.grim3212.assorted.decor.client.model;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.foml.baked.OBJBakedModel;
import com.grim3212.assorted.decor.foml.baked.OBJUnbakedModel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class ColorizerObjUnbakedModel extends OBJUnbakedModel {

    private final OBJUnbakedModel unbakedModel;

    public ColorizerObjUnbakedModel(OBJUnbakedModel objUnbakedModel) {
        super(objUnbakedModel.obj, objUnbakedModel.mtls, objUnbakedModel.transform);
        this.unbakedModel = objUnbakedModel;
    }

    @Override
    public Material getDefaultSprite() {
        return new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(Constants.MOD_ID, "block/colorizer"));
    }

    @Override
    public OBJBakedModel bakeOBJ(ModelBaker loader, Function<Material, TextureAtlasSprite> textureGetter, ModelState bakeSettings, ResourceLocation modelId) {
        OBJBakedModel originalModel = super.bakeOBJ(loader, textureGetter, bakeSettings, modelId);
        return new ColorizerObjBakedModel(unbakedModel, originalModel.getTransforms(), originalModel.getParticleIcon(), loader, textureGetter, bakeSettings, modelId);
    }
}
