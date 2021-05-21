package com.grim3212.assorted.decor.client.data;

import java.util.List;
import java.util.function.Function;

import com.google.common.collect.Lists;
import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.client.model.ColorizerBlockModel;
import com.grim3212.assorted.decor.client.model.ColorizerOBJModel;
import com.grim3212.assorted.decor.common.block.DecorBlocks;
import com.grim3212.assorted.decor.common.block.IlluminationTubeBlock;
import com.grim3212.assorted.decor.common.block.PlanterPotBlock;
import com.grim3212.assorted.decor.common.block.WallClockBlock;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerFireplaceBaseBlock;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerFireplaceBlock;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerLampPost;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerLampPost.LampPart;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerStoolBlock;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerTableBlock;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerVerticalSlabBlock;
import com.grim3212.assorted.decor.common.util.VerticalSlabType;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
		particleOnly(DecorBlocks.NEON_SIGN.get(), new ResourceLocation("block/obsidian"));
		particleOnly(DecorBlocks.NEON_SIGN_WALL.get(), new ResourceLocation("block/obsidian"), DecorBlocks.NEON_SIGN.getId().toString());

		Function<BlockState, ModelFile> modelFunc = (state) -> {
			return state.get(IlluminationTubeBlock.FACING).getAxis().isVertical() ? models().getBuilder(prefix("block/illuminuation_tube")).parent(this.models().getExistingFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/template_torch"))).texture("torch", prefix("block/illumination_tube")) : models().getBuilder(prefix("block/illuminuation_tube_wall")).parent(this.models().getExistingFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/template_torch_wall"))).texture("torch", prefix("block/illumination_tube"));
		};

		getVariantBuilder(DecorBlocks.ILLUMINATION_TUBE.get()).forAllStates(state -> {
			Direction dir = state.get(BlockStateProperties.FACING);
			return ConfiguredModel.builder().modelFile(modelFunc.apply(state)).rotationX(dir == Direction.DOWN ? 180 : 0).rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.getHorizontalAngle()) + 90) % 360).build();
		});

		itemModels().withExistingParent("illumination_tube", "item/generated").texture("layer0", prefix("block/illumination_tube"));

		doorBlock(DecorBlocks.QUARTZ_DOOR.get(), resource("block/quartz_door_bottom"), resource("block/quartz_door_top"));

		extraModels();

		colorizer(DecorBlocks.COLORIZER.get(), new ResourceLocation(AssortedDecor.MODID, "block/tinted_cube"));
		colorizerRotate(DecorBlocks.COLORIZER_CHAIR.get(), new ResourceLocation(AssortedDecor.MODID, "block/chair"));
		colorizerSide(DecorBlocks.COLORIZER_COUNTER.get(), new ResourceLocation(AssortedDecor.MODID, "block/counter"));
		colorizerTable();
		colorizerStool();
		colorizerFence();
		colorizerFenceGate();
		colorizerWall();
		colorizerTrapDoor();
		colorizerDoor();
		colorizerStairs();
		colorizerSlab();
		colorizerVerticalSlab();
		colorizerLampPost();

		ColorizerModelBuilder colorizerBrushModel = this.loaderModels.getBuilder("colorizer_brush").loader(ColorizerBlockModel.Loader.LOCATION).colorizer(new ResourceLocation(AssortedDecor.MODID, "item/brush")).addTexture("handle", new ResourceLocation(AssortedDecor.MODID, "item/brush_handle"));
		itemModels().getBuilder(prefix("item/colorizer_brush")).parent(colorizerBrushModel);

		colorizerOBJ(DecorBlocks.COLORIZER_SLOPE.get(), new ResourceLocation(AssortedDecor.MODID, "models/block/slope.obj"));
		colorizerOBJ(DecorBlocks.COLORIZER_SLOPED_ANGLE.get(), new ResourceLocation(AssortedDecor.MODID, "models/block/sloped_angle.obj"));
		colorizerOBJ(DecorBlocks.COLORIZER_SLOPED_INTERSECTION.get(), new ResourceLocation(AssortedDecor.MODID, "models/block/sloped_intersection.obj"));
		colorizerOBJ(DecorBlocks.COLORIZER_OBLIQUE_SLOPE.get(), new ResourceLocation(AssortedDecor.MODID, "models/block/oblique_slope.obj"));
		colorizerOBJ(DecorBlocks.COLORIZER_CORNER.get(), new ResourceLocation(AssortedDecor.MODID, "models/block/corner.obj"));
		colorizerOBJ(DecorBlocks.COLORIZER_SLANTED_CORNER.get(), new ResourceLocation(AssortedDecor.MODID, "models/block/slanted_corner.obj"));
		colorizerOBJSide(DecorBlocks.COLORIZER_PYRAMID.get(), new ResourceLocation(AssortedDecor.MODID, "models/block/pyramid.obj"));
		colorizerOBJSide(DecorBlocks.COLORIZER_FULL_PYRAMID.get(), new ResourceLocation(AssortedDecor.MODID, "models/block/full_pyramid.obj"));
		colorizerOBJSide(DecorBlocks.COLORIZER_SLOPED_POST.get(), new ResourceLocation(AssortedDecor.MODID, "models/block/sloped_post.obj"));

		ColorizerModelBuilder chimneyModel = getModelBuilder(name(DecorBlocks.COLORIZER_CHIMNEY.get()), new ResourceLocation(AssortedDecor.MODID, "block/chimney")).addTexture("top", new ResourceLocation(AssortedDecor.MODID, "block/chimney_top"));
		getVariantBuilder(DecorBlocks.COLORIZER_CHIMNEY.get()).partialState().setModels(new ConfiguredModel(chimneyModel));
		itemModels().getBuilder(name(DecorBlocks.COLORIZER_CHIMNEY.get())).parent(chimneyModel);

		colorizerFireplace();
		colorizerFirepit();
		colorizerFireringStove();

		pot();

		BlockModelBuilder fluro = this.models().getBuilder(prefix("block/fluro")).parent(this.models().getExistingFile(resource("block/color_cube_all")));
		fluro.texture("all", resource("block/fluro"));

		for (Block b : DecorBlocks.fluroBlocks()) {
			ModelFile fluroModel = models().withExistingParent(name(b), prefix("block/fluro"));

			getVariantBuilder(b).partialState().addModels(new ConfiguredModel(fluroModel));
			itemModels().getBuilder(name(b)).parent(fluroModel);
		}

		calendar();
		wallClock();

		this.loaderModels.previousModels();
	}

	private void extraModels() {
		BlockModelBuilder model = this.models().getBuilder(prefix("block/tinted_cube")).parent(this.models().getExistingFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/block"))).texture("particle", new ResourceLocation(AssortedDecor.MODID, "block/colorizer")).texture("stored", new ResourceLocation(AssortedDecor.MODID, "block/colorizer"));
		model.element().from(0, 0, 0).to(16, 16, 16).allFaces((dir, face) -> {
			face.texture("#stored").cullface(dir).tintindex(0);
		});
		defaultPerspective(model);

		BlockModelBuilder color_cube = this.models().getBuilder(prefix("block/color_cube")).parent(this.models().getExistingFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/block")));
		color_cube.element().from(0, 0, 0).to(16, 16, 16).allFaces((dir, face) -> {
			face.texture("#" + dir.toString()).cullface(dir).tintindex(0);
		});

		BlockModelBuilder color_cube_all = this.models().getBuilder(prefix("block/color_cube_all")).parent(this.models().getExistingFile(resource("block/color_cube")));
		color_cube_all.texture("particle", "#all").texture("down", "#all").texture("up", "#all").texture("north", "#all").texture("south", "#all").texture("west", "#all").texture("east", "#all");
	}

	private void particleOnly(Block b, ResourceLocation particle) {
		particleOnly(b, particle, name(b));
	}

	private void particleOnly(Block b, ResourceLocation particle, String modelOverride) {
		ModelFile f = models().getBuilder(modelOverride).texture("particle", particle);
		simpleBlock(b, f);
	}

	private String prefix(String name) {
		return resource(name).toString();
	}

	private static String name(Block i) {
		return Registry.BLOCK.getKey(i).getPath();
	}

	private void colorizer(Block b, ResourceLocation model) {
		colorizer(ColorizerBlockModel.Loader.LOCATION, b, model, false, false, false, false);
	}

	private void colorizerSide(Block b, ResourceLocation model) {
		colorizer(ColorizerBlockModel.Loader.LOCATION, b, model, true, false, true, true);
	}

	private void colorizerRotate(Block b, ResourceLocation model) {
		colorizer(ColorizerBlockModel.Loader.LOCATION, b, model, false, false, true, false);
	}

	private void colorizerOBJ(Block b, ResourceLocation model) {
		colorizer(ColorizerOBJModel.Loader.LOCATION, b, model, true, true, true, false);
	}

	private void colorizerOBJSide(Block b, ResourceLocation model) {
		colorizer(ColorizerOBJModel.Loader.LOCATION, b, model, true, true, true, true);
	}

	private void colorizer(ResourceLocation loader, Block b, ResourceLocation model, boolean defaultPerspective, boolean defaultPerspectiveFlipped, boolean rotate, boolean side) {
		String name = name(b);

		ColorizerModelBuilder colorizerParent = this.loaderModels.getBuilder(name).loader(loader).texture("particle", new ResourceLocation(AssortedDecor.MODID, "block/colorizer"));
		if (loader == ColorizerOBJModel.Loader.LOCATION) {
			colorizerParent = colorizerParent.objModel(model);
		} else {
			colorizerParent = colorizerParent.colorizer(model);
		}

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

	private ConfiguredModel getModel(String builderName, ResourceLocation model) {
		return new ConfiguredModel(getModelBuilder(builderName, model));
	}

	private ColorizerModelBuilder getModelBuilder(String builderName, ResourceLocation model) {
		return this.loaderModels.getBuilder(builderName).loader(ColorizerBlockModel.Loader.LOCATION).colorizer(model).texture("particle", new ResourceLocation(AssortedDecor.MODID, "block/colorizer"));
	}

	private ResourceLocation resource(String name) {
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
		ConfiguredModel colorizerStoolModel = getModel("colorizer_stool", new ResourceLocation(AssortedDecor.MODID, "block/stool"));
		ConfiguredModel colorizerStoolUpModel = getModel("colorizer_stool_up", new ResourceLocation(AssortedDecor.MODID, "block/stool_up"));
		getVariantBuilder(DecorBlocks.COLORIZER_STOOL.get()).forAllStatesExcept(state -> ConfiguredModel.builder().modelFile(state.get(ColorizerStoolBlock.UP) ? colorizerStoolUpModel.model : colorizerStoolModel.model).rotationX(state.get(BlockStateProperties.FACE).ordinal() * 90).rotationY((((int) state.get(BlockStateProperties.HORIZONTAL_FACING).getHorizontalAngle() + 180) + (state.get(BlockStateProperties.FACE) == AttachFace.CEILING ? 180 : 0)) % 360).build(),
				BlockStateProperties.WATERLOGGED);
		itemModels().getBuilder(name).parent(colorizerStoolModel.model);
	}

	private void calendar() {
		BlockModelBuilder calendarModel = this.models().getBuilder(prefix("block/calendar")).parent(this.models().getExistingFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/block"))).texture("particle", new ResourceLocation(AssortedDecor.MODID, "block/calendar")).texture("all", new ResourceLocation(AssortedDecor.MODID, "block/calendar"));

		calendarModel.element().from(4, 2, 0).to(12, 15, 1).allFaces((dir, face) -> {
			switch (dir) {
				case EAST:
					face.texture("#all").uvs(14, 1, 16, 15);
					break;
				case NORTH:
					face.texture("#all").uvs(0, 0, 4, 16).cullface(Direction.NORTH);
					break;
				case SOUTH:
					face.texture("#all").uvs(2.5F, 0F, 13.5F, 16F);
					break;
				case WEST:
					face.texture("#all").uvs(0, 1, 2, 15);
					break;
				case DOWN:
					face.texture("#all").uvs(12, 2, 4, 0);
					break;
				case UP:
				default:
					face.texture("#all").uvs(4, 0, 12, 2);
					break;
			}
		});

		getVariantBuilder(DecorBlocks.CALENDAR.get()).forAllStates((state -> ConfiguredModel.builder().modelFile(calendarModel).rotationY((int) state.get(BlockStateProperties.HORIZONTAL_FACING).getHorizontalAngle()).build()));
	}

	private void wallClock() {
		BlockModelBuilder defaultWallClockModel = this.models().getBuilder(prefix("block/wall_clock")).parent(this.models().getExistingFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/block"))).texture("particle", new ResourceLocation("block/oak_planks")).texture("back", new ResourceLocation("block/oak_planks")).texture("side", new ResourceLocation("block/oak_planks"));

		defaultWallClockModel.element().from(0, 0, 0).to(2, 16, 16).allFaces((dir, face) -> {
			switch (dir) {
				case EAST:
					face.texture("#front").uvs(0, 0, 16, 16);
					break;
				case NORTH:
					face.texture("#side").uvs(14, 0, 16, 16).cullface(Direction.NORTH);
					break;
				case SOUTH:
					face.texture("#side").uvs(0, 0, 2, 16).cullface(Direction.SOUTH);
					break;
				case WEST:
					face.texture("#back").uvs(0, 0, 16, 16).cullface(Direction.WEST);
					break;
				case DOWN:
					face.texture("#side").uvs(16, 16, 14, 0).cullface(Direction.DOWN);
					break;
				case UP:
				default:
					face.texture("#side").uvs(0, 0, 2, 16).cullface(Direction.UP);
					break;
			}
		});

		List<BlockModelBuilder> clocks = Lists.newArrayList();
		for (int i = 0; i < 64; i++) {
			String name = prefix("block/wall_clock/wall_clock_" + (i + 1));
			clocks.add(this.models().getBuilder(name).parent(defaultWallClockModel).texture("front", name));
		}

		getVariantBuilder(DecorBlocks.WALL_CLOCK.get()).forAllStates((state -> ConfiguredModel.builder().modelFile(clocks.get(state.get(WallClockBlock.TIME))).rotationY(((int) state.get(BlockStateProperties.HORIZONTAL_FACING).getHorizontalAngle() + 90) % 360).build()));
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

		ConfiguredModel colorizerCounterModel = getModel("colorizer_counter", new ResourceLocation(AssortedDecor.MODID, "block/counter"));
		ConfiguredModel colorizerTableNModel = getModel("colorizer_table_n", new ResourceLocation(AssortedDecor.MODID, "block/table_n"));
		ConfiguredModel colorizerTableSEModel = getModel("colorizer_table_se", new ResourceLocation(AssortedDecor.MODID, "block/table_se"));
		ConfiguredModel colorizerTableNWallModel = getModel("colorizer_table_n_wall", new ResourceLocation(AssortedDecor.MODID, "block/table_n_wall"));
		ConfiguredModel colorizerTableSEWallModel = getModel("colorizer_table_se_wall", new ResourceLocation(AssortedDecor.MODID, "block/table_se_wall"));
		ConfiguredModel colorizerTableModel = getModel("colorizer_table", new ResourceLocation(AssortedDecor.MODID, "block/table"));

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
		ConfiguredModel colorizerFenceGateModel = getModel("colorizer_fence_gate", new ResourceLocation(AssortedDecor.MODID, "block/fence_gate"));
		ConfiguredModel colorizerFenceGateOpenModel = getModel("colorizer_fence_gate_open", new ResourceLocation(AssortedDecor.MODID, "block/fence_gate_open"));
		ConfiguredModel colorizerFenceGateWallModel = getModel("colorizer_fence_gate_wall", new ResourceLocation(AssortedDecor.MODID, "block/fence_gate_wall"));
		ConfiguredModel colorizerFenceGateWallOpenModel = getModel("colorizer_fence_gate_wall_open", new ResourceLocation(AssortedDecor.MODID, "block/fence_gate_wall_open"));

		fenceGateBlock(DecorBlocks.COLORIZER_FENCE_GATE.get(), colorizerFenceGateModel.model, colorizerFenceGateOpenModel.model, colorizerFenceGateWallModel.model, colorizerFenceGateWallOpenModel.model);

		itemModels().getBuilder(prefix("item/colorizer_fence_gate")).parent(colorizerFenceGateModel.model);
	}

	private void colorizerFence() {
		ConfiguredModel colorizerFencePostModel = getModel("colorizer_fence_post", new ResourceLocation(AssortedDecor.MODID, "block/fence_post"));
		ConfiguredModel colorizerFenceSideModel = getModel("colorizer_fence_side", new ResourceLocation(AssortedDecor.MODID, "block/fence_side"));
		ConfiguredModel colorizerFenceInventoryModel = getModel("colorizer_fence_inventory", new ResourceLocation(AssortedDecor.MODID, "block/fence_inventory"));

		MultiPartBlockStateBuilder builder = getMultipartBuilder(DecorBlocks.COLORIZER_FENCE.get()).part().modelFile(colorizerFencePostModel.model).addModel().end();
		fourWayMultipart(builder, colorizerFenceSideModel.model);

		itemModels().getBuilder(prefix("item/colorizer_fence")).parent(colorizerFenceInventoryModel.model);
	}

	private void colorizerWall() {
		ConfiguredModel colorizerWallPostModel = getModel("colorizer_wall_post", new ResourceLocation(AssortedDecor.MODID, "block/wall_post"));
		ConfiguredModel colorizerWallSideModel = getModel("colorizer_wall_side", new ResourceLocation(AssortedDecor.MODID, "block/wall_side"));
		ConfiguredModel colorizerWallSideTallModel = getModel("colorizer_wall_side_tall", new ResourceLocation(AssortedDecor.MODID, "block/wall_side_tall"));
		ConfiguredModel colorizerWallInventoryModel = getModel("colorizer_wall_inventory", new ResourceLocation(AssortedDecor.MODID, "block/wall_inventory"));

		wallBlock(DecorBlocks.COLORIZER_WALL.get(), colorizerWallPostModel.model, colorizerWallSideModel.model, colorizerWallSideTallModel.model);
		itemModels().getBuilder(prefix("item/colorizer_wall")).parent(colorizerWallInventoryModel.model);
	}

	private void colorizerTrapDoor() {
		ConfiguredModel colorizerTrapdoorBottomModel = getModel("colorizer_trapdoor_bottom", new ResourceLocation(AssortedDecor.MODID, "block/trapdoor_bottom"));
		ConfiguredModel colorizerTrapdoorOpenModel = getModel("colorizer_trapdoor_open", new ResourceLocation(AssortedDecor.MODID, "block/trapdoor_open"));
		ConfiguredModel colorizerTrapdoorTopModel = getModel("colorizer_trapdoor_top", new ResourceLocation(AssortedDecor.MODID, "block/trapdoor_top"));

		trapdoorBlock(DecorBlocks.COLORIZER_TRAP_DOOR.get(), colorizerTrapdoorBottomModel.model, colorizerTrapdoorTopModel.model, colorizerTrapdoorOpenModel.model, true);
		itemModels().getBuilder(prefix("item/colorizer_trap_door")).parent(colorizerTrapdoorBottomModel.model);
	}

	private void colorizerDoor() {
		ConfiguredModel colorizerDoorBottomModel = getModel("colorizer_door_bottom", new ResourceLocation(AssortedDecor.MODID, "block/door_bottom"));
		ConfiguredModel colorizerDoorBottomRHModel = getModel("colorizer_door_bottom_rh", new ResourceLocation(AssortedDecor.MODID, "block/door_bottom_rh"));
		ConfiguredModel colorizerDoorTopModel = getModel("colorizer_door_top", new ResourceLocation(AssortedDecor.MODID, "block/door_top"));
		ConfiguredModel colorizerDoorTopRHModel = getModel("colorizer_door_top_rh", new ResourceLocation(AssortedDecor.MODID, "block/door_top_rh"));
		ConfiguredModel colorizerDoorItemModel = getModel("colorizer_door", new ResourceLocation(AssortedDecor.MODID, "item/door"));

		doorBlock(DecorBlocks.COLORIZER_DOOR.get(), colorizerDoorBottomModel.model, colorizerDoorBottomRHModel.model, colorizerDoorTopModel.model, colorizerDoorTopRHModel.model);
		itemModels().getBuilder(prefix("item/colorizer_door")).parent(colorizerDoorItemModel.model);
	}

	private void colorizerStairs() {
		ConfiguredModel colorizerStairsModel = getModel("colorizer_stairs", new ResourceLocation(AssortedDecor.MODID, "block/stairs"));
		ConfiguredModel colorizerInnerStairsModel = getModel("colorizer_inner_stairs", new ResourceLocation(AssortedDecor.MODID, "block/inner_stairs"));
		ConfiguredModel colorizerOuterStairsModel = getModel("colorizer_outer_stairs", new ResourceLocation(AssortedDecor.MODID, "block/outer_stairs"));

		stairsBlock(DecorBlocks.COLORIZER_STAIRS.get(), colorizerStairsModel.model, colorizerInnerStairsModel.model, colorizerOuterStairsModel.model);
		itemModels().getBuilder(prefix("item/colorizer_stairs")).parent(colorizerStairsModel.model);
	}

	private void colorizerSlab() {
		ConfiguredModel colorizerSlabModel = getModel("colorizer_slab", new ResourceLocation(AssortedDecor.MODID, "block/slab"));
		ConfiguredModel colorizerSlabTopModel = getModel("colorizer_slab_top", new ResourceLocation(AssortedDecor.MODID, "block/slab_top"));
		ModelFile colorizerModel = models().getExistingFile(new ResourceLocation(AssortedDecor.MODID, "block/colorizer"));

		slabBlock(DecorBlocks.COLORIZER_SLAB.get(), colorizerSlabModel.model, colorizerSlabTopModel.model, colorizerModel);
		itemModels().getBuilder(prefix("item/colorizer_slab")).parent(colorizerSlabModel.model);
	}

	private void colorizerVerticalSlab() {
		ConfiguredModel slabNorthModel = new ConfiguredModel(getModelBuilder("colorizer_vertical_slab_north", new ResourceLocation(AssortedDecor.MODID, "block/vertical_slab")));
		ConfiguredModel slabSouthModel = new ConfiguredModel(getModelBuilder("colorizer_vertical_slab_south", new ResourceLocation(AssortedDecor.MODID, "block/vertical_slab")), 0, 180, false);
		ConfiguredModel slabWestModel = new ConfiguredModel(getModelBuilder("colorizer_vertical_slab_west", new ResourceLocation(AssortedDecor.MODID, "block/vertical_slab")), 0, 270, false);
		ConfiguredModel slabEastModel = new ConfiguredModel(getModelBuilder("colorizer_vertical_slab_east", new ResourceLocation(AssortedDecor.MODID, "block/vertical_slab")), 0, 90, false);
		ModelFile colorizerModel = models().getExistingFile(new ResourceLocation(AssortedDecor.MODID, "block/colorizer"));

		getVariantBuilder(DecorBlocks.COLORIZER_VERTICAL_SLAB.get()).partialState().with(ColorizerVerticalSlabBlock.TYPE, VerticalSlabType.NORTH).addModels(slabNorthModel).partialState().with(ColorizerVerticalSlabBlock.TYPE, VerticalSlabType.SOUTH).addModels(slabSouthModel).partialState().with(ColorizerVerticalSlabBlock.TYPE, VerticalSlabType.WEST).addModels(slabWestModel).partialState().with(ColorizerVerticalSlabBlock.TYPE, VerticalSlabType.EAST).addModels(slabEastModel).partialState()
				.with(ColorizerVerticalSlabBlock.TYPE, VerticalSlabType.DOUBLE).addModels(new ConfiguredModel(colorizerModel));

		itemModels().getBuilder(prefix("item/colorizer_vertical_slab")).parent(slabNorthModel.model);
	}

	private void colorizerLampPost() {
		ConfiguredModel colorizerLampPostBottomModel = getModel("colorizer_lamp_post_bottom", new ResourceLocation(AssortedDecor.MODID, "block/lamp_post_bottom"));
		ConfiguredModel colorizerLampPostMiddleModel = getModel("colorizer_lamp_post_middle", new ResourceLocation(AssortedDecor.MODID, "block/lamp_post_middle"));
		ColorizerModelBuilder colorizerLampPostTopModel = getModelBuilder("colorizer_lamp_post_top", new ResourceLocation(AssortedDecor.MODID, "block/lamp_post_top")).addTexture("lamp", new ResourceLocation("block/glowstone"));
		ColorizerModelBuilder colorizerLampPostInventoryModel = getModelBuilder("colorizer_lamp_post_inventory", new ResourceLocation(AssortedDecor.MODID, "block/lamp_post_inventory")).addTexture("lamp", new ResourceLocation("block/glowstone"));

		getVariantBuilder(DecorBlocks.COLORIZER_LAMP_POST.get()).partialState().with(ColorizerLampPost.PART, LampPart.BOTTOM).addModels(colorizerLampPostBottomModel).partialState().with(ColorizerLampPost.PART, LampPart.MIDDLE).addModels(colorizerLampPostMiddleModel).partialState().with(ColorizerLampPost.PART, LampPart.TOP).addModels(new ConfiguredModel(colorizerLampPostTopModel));
		itemModels().getBuilder(prefix("item/colorizer_lamp_post")).parent(colorizerLampPostInventoryModel);
	}

	private void colorizerFireplace() {
		ColorizerModelBuilder colorizerFireplaceModel = getModelBuilder("colorizer_fireplace", new ResourceLocation(AssortedDecor.MODID, "block/fireplace")).addTexture("wood", new ResourceLocation("block/oak_planks"));
		ColorizerModelBuilder colorizerFireplaceNModel = getModelBuilder("colorizer_fireplace_n", new ResourceLocation(AssortedDecor.MODID, "block/fireplace_n")).addTexture("wood", new ResourceLocation("block/oak_planks"));
		ColorizerModelBuilder colorizerFireplaceNEModel = getModelBuilder("colorizer_fireplace_ne", new ResourceLocation(AssortedDecor.MODID, "block/fireplace_ne")).addTexture("wood", new ResourceLocation("block/oak_planks"));
		ColorizerModelBuilder colorizerFireplaceNSModel = getModelBuilder("colorizer_fireplace_ns", new ResourceLocation(AssortedDecor.MODID, "block/fireplace_ns")).addTexture("wood", new ResourceLocation("block/oak_planks"));
		ModelFile fireModel = models().getExistingFile(new ResourceLocation(AssortedDecor.MODID, "block/fire"));

		MultiPartBlockStateBuilder builder = getMultipartBuilder(DecorBlocks.COLORIZER_FIREPLACE.get()).part().modelFile(colorizerFireplaceModel).addModel().condition(ColorizerFireplaceBlock.EAST, false).condition(ColorizerFireplaceBlock.WEST, false).condition(ColorizerFireplaceBlock.SOUTH, false).condition(ColorizerFireplaceBlock.NORTH, false).end();

		builder.part().modelFile(colorizerFireplaceNModel).uvLock(true).rotationY(90).addModel().condition(ColorizerFireplaceBlock.EAST, true).condition(ColorizerFireplaceBlock.WEST, false).condition(ColorizerFireplaceBlock.SOUTH, false).condition(ColorizerFireplaceBlock.NORTH, false).end();
		builder.part().modelFile(colorizerFireplaceNModel).uvLock(true).rotationY(270).addModel().condition(ColorizerFireplaceBlock.EAST, false).condition(ColorizerFireplaceBlock.WEST, true).condition(ColorizerFireplaceBlock.SOUTH, false).condition(ColorizerFireplaceBlock.NORTH, false).end();
		builder.part().modelFile(colorizerFireplaceNModel).uvLock(true).rotationY(180).addModel().condition(ColorizerFireplaceBlock.EAST, false).condition(ColorizerFireplaceBlock.WEST, false).condition(ColorizerFireplaceBlock.SOUTH, true).condition(ColorizerFireplaceBlock.NORTH, false).end();
		builder.part().modelFile(colorizerFireplaceNModel).uvLock(true).addModel().condition(ColorizerFireplaceBlock.EAST, false).condition(ColorizerFireplaceBlock.WEST, false).condition(ColorizerFireplaceBlock.SOUTH, false).condition(ColorizerFireplaceBlock.NORTH, true).end();

		builder.part().modelFile(colorizerFireplaceNEModel).uvLock(true).addModel().condition(ColorizerFireplaceBlock.EAST, true).condition(ColorizerFireplaceBlock.WEST, false).condition(ColorizerFireplaceBlock.SOUTH, false).condition(ColorizerFireplaceBlock.NORTH, true).end();
		builder.part().modelFile(colorizerFireplaceNEModel).uvLock(true).rotationY(180).addModel().condition(ColorizerFireplaceBlock.EAST, false).condition(ColorizerFireplaceBlock.WEST, true).condition(ColorizerFireplaceBlock.SOUTH, true).condition(ColorizerFireplaceBlock.NORTH, false).end();
		builder.part().modelFile(colorizerFireplaceNEModel).uvLock(true).rotationY(270).addModel().condition(ColorizerFireplaceBlock.EAST, false).condition(ColorizerFireplaceBlock.WEST, true).condition(ColorizerFireplaceBlock.SOUTH, false).condition(ColorizerFireplaceBlock.NORTH, true).end();
		builder.part().modelFile(colorizerFireplaceNEModel).uvLock(true).rotationY(90).addModel().condition(ColorizerFireplaceBlock.EAST, true).condition(ColorizerFireplaceBlock.WEST, false).condition(ColorizerFireplaceBlock.SOUTH, true).condition(ColorizerFireplaceBlock.NORTH, false).end();

		builder.part().modelFile(colorizerFireplaceNSModel).uvLock(true).addModel().condition(ColorizerFireplaceBlock.EAST, true).condition(ColorizerFireplaceBlock.WEST, true).condition(ColorizerFireplaceBlock.SOUTH, false).condition(ColorizerFireplaceBlock.NORTH, false).end();
		builder.part().modelFile(colorizerFireplaceNSModel).uvLock(true).addModel().condition(ColorizerFireplaceBlock.EAST, false).condition(ColorizerFireplaceBlock.WEST, false).condition(ColorizerFireplaceBlock.SOUTH, true).condition(ColorizerFireplaceBlock.NORTH, true).end();
		builder.part().modelFile(colorizerFireplaceNSModel).uvLock(true).addModel().condition(ColorizerFireplaceBlock.EAST, false).condition(ColorizerFireplaceBlock.WEST, true).condition(ColorizerFireplaceBlock.SOUTH, true).condition(ColorizerFireplaceBlock.NORTH, true).end();
		builder.part().modelFile(colorizerFireplaceNSModel).uvLock(true).addModel().condition(ColorizerFireplaceBlock.EAST, true).condition(ColorizerFireplaceBlock.WEST, false).condition(ColorizerFireplaceBlock.SOUTH, true).condition(ColorizerFireplaceBlock.NORTH, true).end();
		builder.part().modelFile(colorizerFireplaceNSModel).uvLock(true).addModel().condition(ColorizerFireplaceBlock.EAST, true).condition(ColorizerFireplaceBlock.WEST, true).condition(ColorizerFireplaceBlock.SOUTH, false).condition(ColorizerFireplaceBlock.NORTH, true).end();
		builder.part().modelFile(colorizerFireplaceNSModel).uvLock(true).addModel().condition(ColorizerFireplaceBlock.EAST, true).condition(ColorizerFireplaceBlock.WEST, true).condition(ColorizerFireplaceBlock.SOUTH, true).condition(ColorizerFireplaceBlock.NORTH, false).end();
		builder.part().modelFile(colorizerFireplaceNSModel).uvLock(true).addModel().condition(ColorizerFireplaceBlock.EAST, true).condition(ColorizerFireplaceBlock.WEST, true).condition(ColorizerFireplaceBlock.SOUTH, true).condition(ColorizerFireplaceBlock.NORTH, true).end();
		builder.part().modelFile(fireModel).addModel().condition(ColorizerFireplaceBaseBlock.ACTIVE, true).end();

		itemModels().getBuilder(prefix("item/colorizer_fireplace")).parent(colorizerFireplaceModel);
	}

	private void colorizerFirepit() {
		ColorizerModelBuilder colorizerFirepitModel = getModelBuilder("colorizer_firepit", new ResourceLocation(AssortedDecor.MODID, "block/firepit")).addTexture("wood", new ResourceLocation("block/oak_planks"));
		ColorizerModelBuilder colorizerFirepitCoveredModel = getModelBuilder("colorizer_firepit_covered", new ResourceLocation(AssortedDecor.MODID, "block/firepit_covered")).addTexture("wood", new ResourceLocation("block/oak_planks")).addTexture("net", new ResourceLocation(AssortedDecor.MODID, "block/net"));
		ModelFile fireModel = models().getExistingFile(new ResourceLocation(AssortedDecor.MODID, "block/fire_high"));

		getMultipartBuilder(DecorBlocks.COLORIZER_FIREPIT.get()).part().modelFile(colorizerFirepitModel).addModel().end().part().modelFile(fireModel).addModel().condition(ColorizerFireplaceBaseBlock.ACTIVE, true).end();
		itemModels().getBuilder(prefix("item/colorizer_firepit")).parent(colorizerFirepitModel);

		getMultipartBuilder(DecorBlocks.COLORIZER_FIREPIT_COVERED.get()).part().modelFile(colorizerFirepitCoveredModel).addModel().end().part().modelFile(fireModel).addModel().condition(ColorizerFireplaceBaseBlock.ACTIVE, true).end();
		itemModels().getBuilder(prefix("item/colorizer_firepit_covered")).parent(colorizerFirepitCoveredModel);
	}

	private void colorizerFireringStove() {
		ColorizerModelBuilder colorizerFireringModel = getModelBuilder("colorizer_firering", new ResourceLocation(AssortedDecor.MODID, "block/firering")).addTexture("wood", new ResourceLocation("block/oak_planks"));
		ColorizerModelBuilder colorizerStoveModel = getModelBuilder("colorizer_stove", new ResourceLocation(AssortedDecor.MODID, "block/stove")).addTexture("wood", new ResourceLocation("block/oak_planks")).addTexture("net", new ResourceLocation(AssortedDecor.MODID, "block/net"));
		ModelFile fireModel = models().getExistingFile(new ResourceLocation(AssortedDecor.MODID, "block/fire"));
		ModelFile fireHighModel = models().getExistingFile(new ResourceLocation(AssortedDecor.MODID, "block/fire_high"));

		getMultipartBuilder(DecorBlocks.COLORIZER_FIRERING.get()).part().modelFile(colorizerFireringModel).addModel().end().part().modelFile(fireModel).addModel().condition(ColorizerFireplaceBaseBlock.ACTIVE, true).end();
		itemModels().getBuilder(prefix("item/colorizer_firering")).parent(colorizerFireringModel);

		getMultipartBuilder(DecorBlocks.COLORIZER_STOVE.get()).part().modelFile(colorizerStoveModel).addModel().end().part().modelFile(fireHighModel).addModel().condition(ColorizerFireplaceBaseBlock.ACTIVE, true).end();
		itemModels().getBuilder(prefix("item/colorizer_stove")).parent(colorizerStoveModel);
	}
}
