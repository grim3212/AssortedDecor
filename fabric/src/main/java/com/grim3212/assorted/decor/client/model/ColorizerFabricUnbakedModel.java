package com.grim3212.assorted.decor.client.model;

import com.grim3212.assorted.lib.client.model.DynamicUnbakedModel;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ColorizerFabricUnbakedModel extends DynamicUnbakedModel {

    public ColorizerFabricUnbakedModel(BlockModel originalModel) {
        super(originalModel);
    }

    @Nullable
    @Override
    public BakedModel bake(ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state, ResourceLocation location) {
        // We need the parent location because the model we pass does not have any elements
        return new ColorizerFabricBakedModel(baker.getModel(this.parentLocation), baker, spriteGetter, state, location);
    }
}
