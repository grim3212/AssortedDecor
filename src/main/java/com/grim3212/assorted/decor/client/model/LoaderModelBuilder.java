package com.grim3212.assorted.decor.client.model;

import com.google.gson.JsonObject;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class LoaderModelBuilder extends ModelBuilder<LoaderModelBuilder> {

	private ResourceLocation loader;

	protected LoaderModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper) {
		super(outputLocation, existingFileHelper);
	}

	public LoaderModelBuilder loader(ResourceLocation loader) {
		this.loader = loader;
		return this;
	}

	@Override
	public JsonObject toJson() {
		JsonObject ret = super.toJson();
		if (loader != null) {
			ret.addProperty("loader", loader.toString());
		}
		return ret;
	}
}