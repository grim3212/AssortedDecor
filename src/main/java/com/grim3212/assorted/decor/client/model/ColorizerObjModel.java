package com.grim3212.assorted.decor.client.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;

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

public class ColorizerObjModel implements IUnbakedGeometry<ColorizerObjModel> {

	private final ObjModelCopy objModel;

	private ColorizerObjModel(ResourceLocation objModelLocation) {
		this.objModel = new ObjModelCopy(defaultSettings(objModelLocation), Maps.newHashMap());
	}

	private ObjModelCopy.ModelSettings defaultSettings(ResourceLocation loc) {
		return new ObjModelCopy.ModelSettings(loc, true, true, true, true, null);
	}

	@Nonnull
	@Override
	public Collection<Material> getMaterials(IGeometryBakingContext owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
		Set<Material> ret = new HashSet<>();
		ret.addAll(this.objModel.getMaterials(owner, modelGetter, missingTextureErrors));
		return ret;
	}

	@Nullable
	@Override
	public BakedModel bake(IGeometryBakingContext owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation name) {
		BakedModel bakedColorizer = this.objModel.bake(owner, bakery, spriteGetter, transform, overrides, name);
		return new ColorizerObjBakedModel(bakedColorizer, objModel, owner, spriteGetter.apply(owner.getMaterial("particle")), bakery, spriteGetter, transform, overrides, name);
	}

	public enum Loader implements IGeometryLoader<ColorizerObjModel> {
		INSTANCE;

		@Nonnull
		@Override
		public ColorizerObjModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
			if (!jsonObject.has("model"))
				throw new UnsupportedOperationException("Model location not found for a ColorizerOBJModel");
			return new ColorizerObjModel(new ResourceLocation(jsonObject.getAsJsonObject("model").getAsString()));
		}
	}

}
