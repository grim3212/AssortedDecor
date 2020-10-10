package com.grim3212.assorted.decor.client.data;

import com.google.common.collect.ImmutableList;
import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.client.model.ColorizerModel.ColorizerLoader;
import com.grim3212.assorted.decor.client.model.ColorizerModelBuilder;
import com.grim3212.assorted.decor.client.model.ColorizerModelProvider;
import com.grim3212.assorted.decor.common.block.DecorBlocks;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class DecorBlockstateProvider extends BlockStateProvider {

	private final ColorizerModelProvider loaderModels;

	public DecorBlockstateProvider(DataGenerator generator, ExistingFileHelper exFileHelper, ColorizerModelProvider loader) {
		super(generator, AssortedDecor.MODID, exFileHelper);
		this.loaderModels = loader;
	}

	@Override
	public String getName() {
		return "Assorted Decor block states";
	}

	@Override
	protected void registerStatesAndModels() {
		ColorizerModelBuilder parent = this.loaderModels.getBuilder("block/colorizer").loader(ColorizerLoader.LOCATION).parts(ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "block/tinted_cube")));
		customLoaderState(DecorBlocks.COLORIZER.get(), new ConfiguredModel(parent));
		genericBlock(DecorBlocks.COLORIZER.get());
		
		simpleBlock(DecorBlocks.HARDENED_WOOD.get());
		genericBlock(DecorBlocks.HARDENED_WOOD.get());

		this.loaderModels.previousModels();
	}

	private ItemModelBuilder generatedItem(Block b) {
		return generatedItem(name(b));
	}

	private ItemModelBuilder generatedItem(String name) {
		return itemModels().withExistingParent(name, "item/generated").texture("layer0", prefix("item/" + name));
	}

	private ItemModelBuilder genericBlock(Block b) {
		String name = name(b);
		return itemModels().withExistingParent(name, prefix("block/" + name));
	}

	private static String name(Block i) {
		return Registry.BLOCK.getKey(i).getPath();
	}

	private String prefix(String name) {
		return new ResourceLocation(AssortedDecor.MODID, name).toString();
	}

	private void customLoaderState(Block block, ConfiguredModel model) {
		getVariantBuilder(block).partialState().setModels(model);
	}
}
