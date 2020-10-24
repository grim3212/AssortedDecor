package com.grim3212.assorted.decor.client.model;

import java.util.function.Function;

import com.google.common.collect.ImmutableMap;

import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;

public class ColorizerBlockBakedModel extends ColorizerBaseBakedModel {

	private final IUnbakedModel unbakedModel;

	public ColorizerBlockBakedModel(IBakedModel bakedColorizer, IUnbakedModel unbakedModel, IModelConfiguration owner, TextureAtlasSprite baseSprite, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation name) {
		super(bakedColorizer, owner, baseSprite, bakery, spriteGetter, transform, overrides, name);
		this.unbakedModel = unbakedModel;
	}

	@Override
	protected IBakedModel generateModel(ImmutableMap<String, String> texture) {
		RetexturableBlockModel toBake = RetexturableBlockModel.from((BlockModel) this.unbakedModel);
		return toBake.retexture(texture).bakeModel(this.bakery, toBake, this.spriteGetter, this.transform, this.name, true);
	}

}
