package com.grim3212.assorted.decor.client.model;

import java.util.HashMap;
import java.util.Map;

import com.grim3212.assorted.decor.AssortedDecor;

import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class LoaderModelProvider extends ModelProvider<LoaderModelBuilder> {

	final Map<ResourceLocation, LoaderModelBuilder> previousModels = new HashMap<>();

	public LoaderModelProvider(DataGenerator gen, ExistingFileHelper exHelper) {
		super(gen, AssortedDecor.MODID, "block", LoaderModelBuilder::new, exHelper);
	}

	@Override
	public String getName() {
		return "Decor Loader Models";
	}

	@Override
	protected void registerModels() {
		super.generatedModels.putAll(previousModels);
	}

	public void previousModels() {
		previousModels.putAll(super.generatedModels);
	}
}