package com.grim3212.assorted.decor.client.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

public class ColorizerBlockModel implements IUnbakedGeometry<ColorizerBlockModel> {

	private BlockModel unbakedColorizer;

	private ColorizerBlockModel(BlockModel unbakedColorizer) {
		this.unbakedColorizer = unbakedColorizer;
	}

	@Nonnull
	@Override
	public Collection<Material> getMaterials(IGeometryBakingContext owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
		Set<Material> ret = new HashSet<>();
		ret.addAll(this.unbakedColorizer.getMaterials(modelGetter, missingTextureErrors));
		return ret;
	}

	@Nullable
	@Override
	public BakedModel bake(IGeometryBakingContext owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation name) {
		BakedModel bakedColorizer = unbakedColorizer.bake(bakery, spriteGetter, transform, name);
		return new ColorizerBlockBakedModel(bakedColorizer, unbakedColorizer, owner, spriteGetter.apply(owner.getMaterial("particle")), bakery, spriteGetter, transform, overrides, name);
	}

	public enum Loader implements IGeometryLoader<ColorizerBlockModel> {
		INSTANCE;

		@Nonnull
		@Override
		public ColorizerBlockModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
			BlockModel colorizer = deserializationContext.deserialize(jsonObject.getAsJsonObject("colorizer"), BlockModel.class);
			return new ColorizerBlockModel(colorizer);
		}
	}
}
