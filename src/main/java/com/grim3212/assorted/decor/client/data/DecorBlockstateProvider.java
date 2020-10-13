package com.grim3212.assorted.decor.client.data;

import com.google.common.collect.ImmutableList;
import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.client.model.ColorizerModel.ColorizerLoader;
import com.grim3212.assorted.decor.client.model.ColorizerModelBuilder;
import com.grim3212.assorted.decor.client.model.ColorizerModelProvider;
import com.grim3212.assorted.decor.client.model.ColorizerOBJModel.ColorizerOBJLoader;
import com.grim3212.assorted.decor.common.block.DecorBlocks;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder.Perspective;
import net.minecraftforge.client.model.generators.ModelProvider;
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
		extraModels();

		ColorizerModelBuilder colorizerParent = this.loaderModels.getBuilder("block/colorizer").loader(ColorizerLoader.LOCATION).parts(ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "block/tinted_cube")));
		customLoaderState(DecorBlocks.COLORIZER.get(), new ConfiguredModel(colorizerParent));
		genericBlock(DecorBlocks.COLORIZER.get());

		ColorizerModelBuilder colorizerChairParent = this.loaderModels.getBuilder("block/colorizer_chair").loader(ColorizerLoader.LOCATION).parts(ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "block/chair")));
		customLoaderStateRotate(DecorBlocks.COLORIZER_CHAIR.get(), new ConfiguredModel(colorizerChairParent));
		genericBlock(DecorBlocks.COLORIZER_CHAIR.get());

		ColorizerModelBuilder colorizerSlopeParent = this.loaderModels.getBuilder("block/colorizer_slope").loader(ColorizerOBJLoader.LOCATION).parts(ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "models/block/slope.obj")));
		defaultPerspectiveFlipped(colorizerSlopeParent);
		customLoaderStateRotate(DecorBlocks.COLORIZER_SLOPE.get(), new ConfiguredModel(colorizerSlopeParent));
		genericBlock(DecorBlocks.COLORIZER_SLOPE.get());

		simpleBlock(DecorBlocks.HARDENED_WOOD.get());
		genericBlock(DecorBlocks.HARDENED_WOOD.get());

		this.loaderModels.previousModels();
	}

	private void extraModels() {
		BlockModelBuilder model = this.models().getBuilder(prefix("block/tinted_cube")).parent(this.models().getExistingFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/block"))).texture("particle", new ResourceLocation(AssortedDecor.MODID, "block/colorizer")).texture("stored", new ResourceLocation(AssortedDecor.MODID, "block/colorizer"));

		model.element().from(0, 0, 0).to(16, 16, 16).allFaces((dir, face) -> {
			face.texture("#stored").cullface(dir).tintindex(0);
		});

		defaultPerspective(model);
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
		return loc(name).toString();
	}

	private ResourceLocation loc(String name) {
		return new ResourceLocation(AssortedDecor.MODID, name);
	}

	private void customLoaderState(Block block, ConfiguredModel model) {
		getVariantBuilder(block).partialState().setModels(model);
	}

	private void customLoaderStateRotate(Block block, ConfiguredModel model) {
		getVariantBuilder(block).forAllStatesExcept(state -> {
			Direction facing = state.get(BlockStateProperties.HORIZONTAL_FACING);
			Half half = state.get(BlockStateProperties.HALF);
			int yRot = ((int) facing.rotateY().getHorizontalAngle() + (half == Half.TOP ? 270 : 90)) % 360;
			boolean uvlock = yRot != 0 || half == Half.TOP;
			return ConfiguredModel.builder().modelFile(model.model).rotationY(yRot).rotationX(half == Half.TOP ? 180 : 0).uvLock(uvlock).build();
		}, BlockStateProperties.WATERLOGGED);
	}

	private void defaultPerspective(ModelBuilder<?> model) {
		model.transforms().transform(Perspective.GUI).rotation(30, 225, 0).translation(0, 0, 0).scale(0.625f).end().transform(Perspective.GROUND).rotation(0, 0, 0).translation(0, 3, 0).scale(0.25f).end().transform(Perspective.FIXED).rotation(0, 0, 0).translation(0, 0, 0).scale(0.5f).end().transform(Perspective.THIRDPERSON_RIGHT).rotation(75, 45, 0).translation(0, 2.5f, 0).scale(0.375f).end().transform(Perspective.FIRSTPERSON_RIGHT).rotation(0, 45, 0).translation(0, 0, 0).scale(0.40f).end()
				.transform(Perspective.FIRSTPERSON_LEFT).rotation(0, 225, 0).translation(0, 0, 0).scale(0.40f).end();
	}

	private void defaultPerspectiveFlipped(ModelBuilder<?> model) {
		model.transforms().transform(Perspective.GUI).rotation(30, 30, 0).translation(0, 0, 0).scale(0.625f).end().transform(Perspective.GROUND).rotation(0, 0, 0).translation(0, 3, 0).scale(0.25f).end().transform(Perspective.FIXED).rotation(0, 0, 0).translation(0, 0, 0).scale(0.5f).end().transform(Perspective.THIRDPERSON_RIGHT).rotation(75, 45, 0).translation(0, 2.5f, 0).scale(0.375f).end().transform(Perspective.FIRSTPERSON_RIGHT).rotation(0, 45, 0).translation(0, 0, 0).scale(0.40f).end()
				.transform(Perspective.FIRSTPERSON_LEFT).rotation(0, 225, 0).translation(0, 0, 0).scale(0.40f).end();
	}
}
