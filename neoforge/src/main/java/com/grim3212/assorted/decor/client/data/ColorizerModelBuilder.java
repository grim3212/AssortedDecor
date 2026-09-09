package com.grim3212.assorted.decor.client.data;

import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;

import net.minecraft.resources.Identifier;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ColorizerModelBuilder extends ModelBuilder<ColorizerModelBuilder> {

	private Identifier loader;
	private Identifier model;
	private Identifier colorizer;
	private Map<String, Identifier> textures;

	protected ColorizerModelBuilder(Identifier outputLocation, ExistingFileHelper existingFileHelper) {
		super(outputLocation, existingFileHelper);
		this.textures = Maps.newConcurrentMap();
	}

	public ColorizerModelBuilder loader(Identifier loader) {
		this.loader = loader;
		return this;
	}

	public ColorizerModelBuilder objModel(Identifier model) {
		this.model = model;
		return this;
	}

	public ColorizerModelBuilder colorizer(Identifier colorizer) {
		this.colorizer = colorizer;
		return this;
	}

	public ColorizerModelBuilder addTexture(String name, Identifier texture) {
		this.textures.put(name, texture);
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

			if (this.textures.size() > 0) {
				JsonObject textureObj = new JsonObject();

				textures.forEach((k, v) -> {
					textureObj.addProperty(k, v.toString());
				});

				ret.add("textures", textureObj);
			}
		}

		if (colorizer != null) {
			JsonObject colorizerObj = new JsonObject();
			colorizerObj.addProperty("parent", colorizer.toString());

			if (this.textures.size() > 0) {
				JsonObject textureObj = new JsonObject();

				textures.forEach((k, v) -> {
					textureObj.addProperty(k, v.toString());
				});

				colorizerObj.add("textures", textureObj);
			}

			ret.add("colorizer", colorizerObj);
		}
		return ret;
	}
}