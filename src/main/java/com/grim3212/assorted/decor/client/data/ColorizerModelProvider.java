package com.grim3212.assorted.decor.client.data;

import java.util.HashMap;
import java.util.Map;

import com.grim3212.assorted.decor.AssortedDecor;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ColorizerModelProvider extends ModelProvider<ColorizerModelBuilder> {

	final Map<ResourceLocation, ColorizerModelBuilder> previousModels = new HashMap<>();

	public ColorizerModelProvider(PackOutput output, ExistingFileHelper exHelper) {
		super(output, AssortedDecor.MODID, "block", ColorizerModelBuilder::new, exHelper);
	}

	@Override
	public String getName() {
		return "Colorizer model provider";
	}

	@Override
	protected void registerModels() {
		super.generatedModels.putAll(previousModels);
	}

	public void previousModels() {
		previousModels.putAll(super.generatedModels);
	}
}