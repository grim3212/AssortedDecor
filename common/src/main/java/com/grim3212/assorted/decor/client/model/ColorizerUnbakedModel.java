package com.grim3212.assorted.decor.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecification;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecificationLoader;
import com.grim3212.assorted.lib.client.model.loaders.context.IModelBakingContext;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class ColorizerUnbakedModel implements IModelSpecification<ColorizerUnbakedModel> {

    public static final ResourceLocation LOADER_NAME = new ResourceLocation(Constants.MOD_ID, "colorizer");

    private BlockModel unbakedColorizer;

    private ColorizerUnbakedModel(BlockModel unbakedColorizer) {
        this.unbakedColorizer = unbakedColorizer;
    }

    @Override
    public BakedModel bake(IModelBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ResourceLocation modelLocation) {
        this.unbakedColorizer.resolveParents(baker::getModel);
        return new ColorizerBakedModel(context, unbakedColorizer, baker, spriteGetter, modelState, modelLocation);
    }

    public static final class Loader implements IModelSpecificationLoader<ColorizerUnbakedModel> {
        public static final Loader INSTANCE = new Loader();

        public ColorizerUnbakedModel read(JsonDeserializationContext deserializationContext, JsonObject jsonObject) {
            return new ColorizerUnbakedModel(deserializationContext.deserialize(jsonObject.getAsJsonObject("colorizer"), BlockModel.class));
        }
    }
}
