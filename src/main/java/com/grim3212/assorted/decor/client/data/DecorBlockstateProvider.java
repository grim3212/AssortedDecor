package com.grim3212.assorted.decor.client.data;

import com.google.common.collect.ImmutableList;
import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.client.model.ColorizerModel.ColorizerLoader;
import com.grim3212.assorted.decor.client.model.ColorizerModelBuilder;
import com.grim3212.assorted.decor.client.model.ColorizerModelProvider;
import com.grim3212.assorted.decor.client.model.ColorizerOBJModel.ColorizerOBJLoader;
import com.grim3212.assorted.decor.common.block.ColorizerStoolBlock;
import com.grim3212.assorted.decor.common.block.ColorizerTableBlock;
import com.grim3212.assorted.decor.common.block.DecorBlocks;
import com.grim3212.assorted.decor.common.block.PlanterPotBlock;

import net.minecraft.block.Block;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.AttachFace;
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
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
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

		colorizer(DecorBlocks.COLORIZER.get(), ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "block/tinted_cube")));
		colorizerRotate(DecorBlocks.COLORIZER_CHAIR.get(), ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "block/chair")));
		colorizerSide(DecorBlocks.COLORIZER_COUNTER.get(), ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "block/counter")));
		colorizerTable();
		colorizerStool();
		colorizerFence();
		colorizerFenceGate();

		colorizerOBJ(DecorBlocks.COLORIZER_SLOPE.get(), ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "models/block/slope.obj")));
		colorizerOBJ(DecorBlocks.COLORIZER_SLOPED_ANGLE.get(), ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "models/block/sloped_angle.obj")));
		colorizerOBJ(DecorBlocks.COLORIZER_SLOPED_INTERSECTION.get(), ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "models/block/sloped_intersection.obj")));
		colorizerOBJ(DecorBlocks.COLORIZER_OBLIQUE_SLOPE.get(), ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "models/block/oblique_slope.obj")));
		colorizerOBJ(DecorBlocks.COLORIZER_CORNER.get(), ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "models/block/corner.obj")));
		colorizerOBJ(DecorBlocks.COLORIZER_SLANTED_CORNER.get(), ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "models/block/slanted_corner.obj")));
		colorizerOBJSide(DecorBlocks.COLORIZER_PYRAMID.get(), ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "models/block/pyramid.obj")));
		colorizerOBJSide(DecorBlocks.COLORIZER_FULL_PYRAMID.get(), ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "models/block/full_pyramid.obj")));
		colorizerOBJSide(DecorBlocks.COLORIZER_SLOPED_POST.get(), ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "models/block/sloped_post.obj")));

		simpleBlock(DecorBlocks.HARDENED_WOOD.get());
		genericBlock(DecorBlocks.HARDENED_WOOD.get());

		pot();

		this.loaderModels.previousModels();
	}

	private void extraModels() {
		BlockModelBuilder model = this.models().getBuilder(prefix("block/tinted_cube")).parent(this.models().getExistingFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/block"))).texture("particle", new ResourceLocation(AssortedDecor.MODID, "block/colorizer")).texture("stored", new ResourceLocation(AssortedDecor.MODID, "block/colorizer"));

		model.element().from(0, 0, 0).to(16, 16, 16).allFaces((dir, face) -> {
			face.texture("#stored").cullface(dir).tintindex(0);
		});

		defaultPerspective(model);
	}

	private String prefix(String name) {
		return loc(name).toString();
	}

	private static String name(Block i) {
		return Registry.BLOCK.getKey(i).getPath();
	}

	private ItemModelBuilder genericBlock(Block b) {
		String name = name(b);
		return itemModels().withExistingParent(name, prefix("block/" + name));
	}

	private void colorizer(Block b, ImmutableList<ResourceLocation> parts) {
		colorizer(ColorizerLoader.LOCATION, b, parts, false, false, false, false);
	}

	private void colorizerSide(Block b, ImmutableList<ResourceLocation> parts) {
		colorizer(ColorizerLoader.LOCATION, b, parts, true, false, true, true);
	}

	private void colorizerRotate(Block b, ImmutableList<ResourceLocation> parts) {
		colorizer(ColorizerLoader.LOCATION, b, parts, false, false, true, false);
	}

	private void colorizerOBJ(Block b, ImmutableList<ResourceLocation> parts) {
		colorizer(ColorizerOBJLoader.LOCATION, b, parts, true, true, true, false);
	}

	private void colorizerOBJSide(Block b, ImmutableList<ResourceLocation> parts) {
		colorizer(ColorizerOBJLoader.LOCATION, b, parts, true, true, true, true);
	}

	private void colorizer(ResourceLocation loader, Block b, ImmutableList<ResourceLocation> parts, boolean defaultPerspective, boolean defaultPerspectiveFlipped, boolean rotate, boolean side) {
		String name = name(b);
		ColorizerModelBuilder colorizerParent = this.loaderModels.getBuilder(name).loader(loader).parts(parts);
		if (defaultPerspective) {
			if (defaultPerspectiveFlipped) {
				defaultPerspectiveFlipped(colorizerParent);
			} else {
				defaultPerspective(colorizerParent);
			}
		}
		ConfiguredModel colorizerModel = new ConfiguredModel(colorizerParent);
		if (rotate) {
			if (side) {
				customLoaderStateSide(b, colorizerModel);
			} else {
				customLoaderStateRotate(b, colorizerModel);
			}
		} else {
			customLoaderState(b, colorizerModel);
		}

		itemModels().getBuilder(name).parent(colorizerModel.model);
	}

	private int countConnections(boolean... connections) {
		int numTrue = 0;
		for (boolean connection : connections) {
			numTrue += connection ? 1 : 0;
		}
		return numTrue;
	}

	private ConfiguredModel getModel(String builderName, ImmutableList<ResourceLocation> parts) {
		ColorizerModelBuilder colorizerParent = this.loaderModels.getBuilder(builderName).loader(ColorizerLoader.LOCATION).parts(parts);
		return new ConfiguredModel(colorizerParent);
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

	private void customLoaderStateSide(Block block, ConfiguredModel model) {
		getVariantBuilder(block).forAllStatesExcept(state -> ConfiguredModel.builder().modelFile(model.model).rotationX(state.get(BlockStateProperties.FACE).ordinal() * 90).rotationY((((int) state.get(BlockStateProperties.HORIZONTAL_FACING).getHorizontalAngle() + 180) + (state.get(BlockStateProperties.FACE) == AttachFace.CEILING ? 180 : 0)) % 360).build(), BlockStateProperties.WATERLOGGED);
	}

	private void defaultPerspective(ModelBuilder<?> model) {
		model.transforms().transform(Perspective.GUI).rotation(30, 225, 0).translation(0, 0, 0).scale(0.625f).end().transform(Perspective.GROUND).rotation(0, 0, 0).translation(0, 3, 0).scale(0.25f).end().transform(Perspective.FIXED).rotation(0, 0, 0).translation(0, 0, 0).scale(0.5f).end().transform(Perspective.THIRDPERSON_RIGHT).rotation(75, 45, 0).translation(0, 2.5f, 0).scale(0.375f).end().transform(Perspective.FIRSTPERSON_RIGHT).rotation(0, 45, 0).translation(0, 0, 0).scale(0.40f).end()
				.transform(Perspective.FIRSTPERSON_LEFT).rotation(0, 225, 0).translation(0, 0, 0).scale(0.40f).end();
	}

	private void defaultPerspectiveFlipped(ModelBuilder<?> model) {
		model.transforms().transform(Perspective.GUI).rotation(30, 30, 0).translation(0, 0, 0).scale(0.625f).end().transform(Perspective.GROUND).rotation(0, 0, 0).translation(0, 3, 0).scale(0.25f).end().transform(Perspective.FIXED).rotation(0, 0, 0).translation(0, 0, 0).scale(0.5f).end().transform(Perspective.THIRDPERSON_RIGHT).rotation(75, 45, 0).translation(0, 2.5f, 0).scale(0.375f).end().transform(Perspective.FIRSTPERSON_RIGHT).rotation(0, 45, 0).translation(0, 0, 0).scale(0.40f).end()
				.transform(Perspective.FIRSTPERSON_LEFT).rotation(0, 225, 0).translation(0, 0, 0).scale(0.40f).end();
	}

	private void colorizerStool() {
		String name = name(DecorBlocks.COLORIZER_STOOL.get());
		ConfiguredModel colorizerStoolModel = getModel("colorizer_stool", ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "block/stool")));
		ConfiguredModel colorizerStoolUpModel = getModel("colorizer_stool_up", ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "block/stool_up")));
		getVariantBuilder(DecorBlocks.COLORIZER_STOOL.get()).forAllStatesExcept(state -> ConfiguredModel.builder().modelFile(state.get(ColorizerStoolBlock.UP) ? colorizerStoolUpModel.model : colorizerStoolModel.model).rotationX(state.get(BlockStateProperties.FACE).ordinal() * 90).rotationY((((int) state.get(BlockStateProperties.HORIZONTAL_FACING).getHorizontalAngle() + 180) + (state.get(BlockStateProperties.FACE) == AttachFace.CEILING ? 180 : 0)) % 360).build(),
				BlockStateProperties.WATERLOGGED);
		itemModels().getBuilder(name).parent(colorizerStoolModel.model);
	}

	private void pot() {
		BlockModelBuilder potModel = this.models().getBuilder(prefix("block/planter_pot_down")).parent(this.models().getExistingFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/block"))).texture("particle", new ResourceLocation(AssortedDecor.MODID, "block/planter_pot")).texture("side", new ResourceLocation(AssortedDecor.MODID, "block/planter_pot"));

		potModel.element().from(3, 0, 3).to(13, 16, 13).allFaces((dir, face) -> {
			switch (dir) {
			case EAST:
			case NORTH:
			case SOUTH:
			case WEST:
				face.texture("#side").uvs(3, 0, 13, 16);
				break;
			case DOWN:
				face.texture("#side").cullface(Direction.DOWN).uvs(13, 13, 3, 3);
				break;
			case UP:
			default:
				face.texture("#top").cullface(Direction.UP).uvs(3, 3, 13, 13);
				break;
			}
		});

		String[] textures = new String[] { "dirt", "sand", "gravel", "clay", "farmland", "netherrack", "soul_sand" };
		getVariantBuilder(DecorBlocks.PLANTER_POT.get()).forAllStates(state -> {
			boolean down = state.get(PlanterPotBlock.DOWN);
			int top = state.get(PlanterPotBlock.TOP);

			BlockModelBuilder newModel;

			if (down) {
				newModel = this.models().getBuilder(prefix("block/planter_pot_down_" + top)).parent(this.models().getExistingFile(potModel.getLocation())).texture("top", mcLoc(ModelProvider.BLOCK_FOLDER + "/" + textures[top]));
			} else {
				newModel = this.models().getBuilder(prefix("block/planter_pot_" + top)).parent(this.models().getExistingFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/cube_top"))).texture("particle", new ResourceLocation(AssortedDecor.MODID, "block/planter_pot")).texture("side", new ResourceLocation(AssortedDecor.MODID, "block/planter_pot")).texture("top", mcLoc(ModelProvider.BLOCK_FOLDER + "/" + textures[top]));
			}

			return ConfiguredModel.builder().modelFile(models().getExistingFile(newModel.getLocation())).build();
		});

		itemModels().withExistingParent(prefix("item/planter_pot"), prefix("block/planter_pot_0"));
	}

	private void colorizerTable() {
		String name = name(DecorBlocks.COLORIZER_TABLE.get());

		ConfiguredModel colorizerCounterModel = getModel("colorizer_counter", ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "block/counter")));
		ConfiguredModel colorizerTableNModel = getModel("colorizer_table_n", ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "block/table_n")));
		ConfiguredModel colorizerTableSEModel = getModel("colorizer_table_se", ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "block/table_se")));
		ConfiguredModel colorizerTableNWallModel = getModel("colorizer_table_n_wall", ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "block/table_n_wall")));
		ConfiguredModel colorizerTableSEWallModel = getModel("colorizer_table_se_wall", ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "block/table_se_wall")));
		ConfiguredModel colorizerTableModel = getModel("colorizer_table", ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "block/table")));

		getVariantBuilder(DecorBlocks.COLORIZER_TABLE.get()).forAllStatesExcept(state -> {
			boolean east = state.get(ColorizerTableBlock.EAST);
			boolean north = state.get(ColorizerTableBlock.NORTH);
			boolean south = state.get(ColorizerTableBlock.SOUTH);
			boolean west = state.get(ColorizerTableBlock.WEST);
			boolean up = state.get(ColorizerTableBlock.UP);
			boolean down = state.get(ColorizerTableBlock.DOWN);

			AttachFace face = state.get(BlockStateProperties.FACE);
			Direction facing = state.get(BlockStateProperties.HORIZONTAL_FACING);

			int numConnections = countConnections(east, north, south, west, up, down);

			if (numConnections >= 3) {
				return ConfiguredModel.builder().modelFile(colorizerCounterModel.model).rotationX(face.ordinal() * 90).rotationY((((int) facing.getHorizontalAngle() + 180)) % 360).uvLock(true).build();
			} else if (numConnections == 2) {
				boolean oppositeEnds = (north && south) || (west && east) || (up && down);

				if (oppositeEnds) {
					return ConfiguredModel.builder().modelFile(colorizerCounterModel.model).rotationX(face.ordinal() * 90).rotationY((((int) facing.getHorizontalAngle() + 180)) % 360).uvLock(true).build();
				} else {
					int rotY = west && south ? 90 : north && west ? 180 : north && east ? 270 : 0;
					int rotX = 0;

					if (face == AttachFace.CEILING) {
						rotY += 90;
					} else if (face == AttachFace.WALL) {
						rotY = ((((int) facing.getHorizontalAngle() + 180)) % 360) + 270;

						if (facing == Direction.NORTH)
							rotX = down && east ? 0 : down && west ? 270 : up && west ? 180 : 90;
						else if (facing == Direction.SOUTH)
							rotX = down && east ? 270 : down && west ? 0 : up && west ? 90 : 180;
						else if (facing == Direction.EAST)
							rotX = south && up ? 90 : north && up ? 180 : north && down ? 270 : 0;
						else if (facing == Direction.WEST)
							rotX = south && up ? 180 : north && up ? 90 : north && down ? 0 : 270;

						return ConfiguredModel.builder().modelFile(colorizerTableSEWallModel.model).rotationX(rotX).rotationY(rotY).uvLock(true).build();
					}

					return ConfiguredModel.builder().modelFile(colorizerTableSEModel.model).rotationX((face.ordinal() * 90) + rotX).rotationY(rotY).uvLock(true).build();
				}
			} else if (numConnections == 1) {
				int rotY = west ? 270 : east ? 90 : south ? 180 : 0;
				int rotX = 0;

				if (face == AttachFace.CEILING) {
					rotY += 180;
				} else if (face == AttachFace.WALL) {
					rotY = (((int) facing.getHorizontalAngle() + 180)) % 360;
					rotY += up ? 180 : down ? 0 : 270;
					boolean flag = (south && facing == Direction.EAST) || (east && facing == Direction.NORTH) || (north && facing == Direction.WEST) || (west && facing == Direction.SOUTH);

					rotX = up ? 180 : flag ? 180 : 0;

					if (!(up || down)) {
						return ConfiguredModel.builder().modelFile(colorizerTableNWallModel.model).rotationX(rotX).rotationY(rotY).uvLock(true).build();
					}
				}

				return ConfiguredModel.builder().modelFile(colorizerTableNModel.model).rotationX((face.ordinal() * 90) + rotX).rotationY(rotY).uvLock(true).build();
			}

			return ConfiguredModel.builder().modelFile(colorizerTableModel.model).rotationX(face.ordinal() * 90).rotationY((((int) facing.getHorizontalAngle() + 180)) % 360).uvLock(true).build();
		}, BlockStateProperties.WATERLOGGED);

		itemModels().getBuilder(name).parent(colorizerTableModel.model);
	}

	private void colorizerFenceGate() {
		ConfiguredModel colorizerFenceGateModel = getModel("colorizer_fence_gate", ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "block/fence_gate")));
		ConfiguredModel colorizerFenceGateOpenModel = getModel("colorizer_fence_gate_open", ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "block/fence_gate_open")));
		ConfiguredModel colorizerFenceGateWallModel = getModel("colorizer_fence_gate_wall", ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "block/fence_gate_wall")));
		ConfiguredModel colorizerFenceGateWallOpenModel = getModel("colorizer_fence_gate_wall_open", ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "block/fence_gate_wall_open")));

		getVariantBuilder(DecorBlocks.COLORIZER_FENCE_GATE.get()).forAllStatesExcept(state -> {
			ModelFile model = colorizerFenceGateModel.model;
			if (state.get(FenceGateBlock.IN_WALL)) {
				model = colorizerFenceGateWallModel.model;
			}
			if (state.get(FenceGateBlock.OPEN)) {
				model = model == colorizerFenceGateWallModel.model ? colorizerFenceGateWallOpenModel.model : colorizerFenceGateOpenModel.model;
			}
			return ConfiguredModel.builder().modelFile(model).rotationY((int) state.get(FenceGateBlock.HORIZONTAL_FACING).getHorizontalAngle()).uvLock(true).build();
		}, FenceGateBlock.POWERED);

		itemModels().getBuilder(prefix("item/colorizer_fence_gate")).parent(colorizerFenceGateModel.model);
	}

	private void colorizerFence() {
		ConfiguredModel colorizerFencePostModel = getModel("colorizer_fence_post", ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "block/fence_post")));
		ConfiguredModel colorizerFenceSideModel = getModel("colorizer_fence_side", ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "block/fence_side")));
		ConfiguredModel colorizerFenceInventoryModel = getModel("colorizer_fence_inventory", ImmutableList.of(new ResourceLocation(AssortedDecor.MODID, "block/fence_inventory")));

		MultiPartBlockStateBuilder builder = getMultipartBuilder(DecorBlocks.COLORIZER_FENCE.get()).part().modelFile(colorizerFencePostModel.model).addModel().end();
		fourWayMultipart(builder, colorizerFenceSideModel.model);

		itemModels().getBuilder(prefix("item/colorizer_fence")).parent(colorizerFenceInventoryModel.model);
	}
}
