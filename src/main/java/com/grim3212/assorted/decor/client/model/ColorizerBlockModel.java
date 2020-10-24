package com.grim3212.assorted.decor.client.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.grim3212.assorted.decor.AssortedDecor;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.IModelGeometry;

public class ColorizerBlockModel implements IModelGeometry<ColorizerBlockModel> {
	private BlockModel unbakedColorizer;

	private ColorizerBlockModel(BlockModel unbakedColorizer) {
		this.unbakedColorizer = unbakedColorizer;
	}

	@Nonnull
	@Override
	public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
		Set<RenderMaterial> ret = new HashSet<>();
		ret.addAll(this.unbakedColorizer.getTextures(modelGetter, missingTextureErrors));
		return ret;
	}

	@Nullable
	@Override
	public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation name) {
		IBakedModel bakedColorizer = unbakedColorizer.bakeModel(bakery, spriteGetter, transform, name);
		return new ColorizerBlockBakedModel(bakedColorizer, unbakedColorizer, owner, spriteGetter.apply(owner.resolveTexture("particle")), bakery, spriteGetter, transform, overrides, name);
	}

	public enum Loader implements IModelLoader<ColorizerBlockModel> {
		INSTANCE;

		public static final ResourceLocation LOCATION = new ResourceLocation(AssortedDecor.MODID, "models/colorizer");

		@Override
		public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
		}

		@Nonnull
		@Override
		public ColorizerBlockModel read(JsonDeserializationContext ctx, JsonObject model) {
			BlockModel colorizer = ctx.deserialize(model.getAsJsonObject("colorizer"), BlockModel.class);
			return new ColorizerBlockModel(colorizer);
		}
	}
}
