package com.grim3212.assorted.decor.client.model;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ColorizerModelBuilder extends ModelBuilder<ColorizerModelBuilder> {

	private ResourceLocation loader;
	private ResourceLocation model;
	private ImmutableList<ResourceLocation> extraTextures;

	protected ColorizerModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper) {
		super(outputLocation, existingFileHelper);
	}

	public ColorizerModelBuilder loader(ResourceLocation loader) {
		this.loader = loader;
		return this;
	}

	public ColorizerModelBuilder model(ResourceLocation model) {
		this.model = model;
		return this;
	}

	public ColorizerModelBuilder extraTextures(ImmutableList<ResourceLocation> extraTextures) {
		this.extraTextures = extraTextures;
		return this;
	}

	@Override
	public JsonObject toJson() {
		JsonObject ret = super.toJson();
		if (loader != null) {
			ret.addProperty("loader", loader.toString());
		}

		if (model != null) {
			ret.addProperty("model", model.toString());
		}

		if (extraTextures != null) {
			JsonArray arr = new JsonArray();
			for (ResourceLocation loc : extraTextures)
				arr.add(loc.toString());

			ret.add("extraTextures", arr);
		}
		return ret;
	}
}