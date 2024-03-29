package com.grim3212.assorted.decor.client.data;

import com.google.common.collect.Lists;
import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.api.util.VerticalSlabType;
import com.grim3212.assorted.decor.client.model.ColorizerUnbakedModel;
import com.grim3212.assorted.decor.client.model.obj.ColorizerObjModel;
import com.grim3212.assorted.decor.common.blocks.*;
import com.grim3212.assorted.decor.common.blocks.colorizer.*;
import com.grim3212.assorted.decor.common.blocks.colorizer.ColorizerLampPost.LampPart;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.client.model.generators.ModelBuilder.FaceRotation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DecorBlockstateProvider extends BlockStateProvider {

    private final ColorizerModelProvider loaderModels;

    public DecorBlockstateProvider(PackOutput output, ExistingFileHelper exFileHelper, ColorizerModelProvider loader) {
        super(output, Constants.MOD_ID, exFileHelper);
        this.loaderModels = loader;
    }

    @Override
    public String getName() {
        return "Assorted Decor block states";
    }

    @Override
    protected void registerStatesAndModels() {
        extraModels();

        genericBlock(DecorBlocks.SIDEWALK.get());
        genericBlock(DecorBlocks.STONE_PATH.get());
        genericBlock(DecorBlocks.DECORATIVE_STONE.get());
        genericCutoutBlock(DecorBlocks.CAGE.get());

        genericRoadway(DecorBlocks.ROADWAY.get());
        genericSiding(DecorBlocks.SIDING_HORIZONTAL.get());
        genericSiding(DecorBlocks.SIDING_VERTICAL.get());

        DecorBlocks.ROADWAY_COLORS.entrySet().stream().filter((e) -> e.getKey() != DyeColor.WHITE).map((e) -> e.getValue()).collect(Collectors.toList()).forEach((r) -> genericRoadway(r.get()));

        getVariantBuilder(DecorBlocks.ROADWAY_COLORS.get(DyeColor.WHITE).get()).forAllStates(state -> {
            int type = state.getValue(RoadwayWhiteBlock.TYPE);
            return ConfiguredModel.builder().modelFile(roadwayModel("roadway_white_" + type, resource("block/roadways/roadway_white_" + type))).build();
        });
        itemModels().withExistingParent(name(DecorBlocks.ROADWAY_COLORS.get(DyeColor.WHITE).get()), prefix("block/roadway_white_11"));

        getVariantBuilder(DecorBlocks.ROADWAY_LIGHT.get()).forAllStates(state -> {
            boolean active = state.getValue(RoadwayLightBlock.ACTIVE);
            return ConfiguredModel.builder().modelFile(roadwayModel("roadway_light_" + (active ? "on" : "off"), resource("block/roadways/roadway_light_" + (active ? "on" : "off")))).build();
        });
        itemModels().withExistingParent(name(DecorBlocks.ROADWAY_LIGHT.get()), prefix("block/roadway_light_on"));

        getVariantBuilder(DecorBlocks.ROADWAY_MANHOLE.get()).forAllStates(state -> {
            boolean open = state.getValue(RoadwayManholeBlock.OPEN);
            return ConfiguredModel.builder().modelFile(models().cubeBottomTop("roadway_manhole_" + (open ? "open" : "closed"), resource("block/roadways/roadway_side"), resource("block/roadways/roadway_" + (open ? "manhole_bottom" : "manhole_closed")), resource("block/roadways/roadway_manhole_" + (open ? "open" : "closed")))).build();
        });
        itemModels().withExistingParent(name(DecorBlocks.ROADWAY_MANHOLE.get()), prefix("block/roadway_manhole_closed"));

        particleOnly(DecorBlocks.NEON_SIGN.get(), new ResourceLocation("block/obsidian"));
        particleOnly(DecorBlocks.NEON_SIGN_WALL.get(), new ResourceLocation("block/obsidian"), DecorBlocks.NEON_SIGN.getId().toString());

        Function<BlockState, ModelFile> modelFunc = (state) -> {
            return state.getValue(IlluminationTubeBlock.FACING).getAxis().isVertical() ? models().getBuilder(prefix("block/illuminuation_tube")).parent(this.models().getExistingFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/template_torch"))).texture("torch", prefix("block/illumination_tube"))
                    : models().getBuilder(prefix("block/illuminuation_tube_wall")).parent(this.models().getExistingFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/template_torch_wall"))).texture("torch", prefix("block/illumination_tube"));
        };

        getVariantBuilder(DecorBlocks.ILLUMINATION_TUBE.get()).forAllStatesExcept(state -> {
            Direction dir = state.getValue(BlockStateProperties.FACING);
            return ConfiguredModel.builder().modelFile(modelFunc.apply(state)).rotationX(dir == Direction.DOWN ? 180 : 0).rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 90) % 360).build();
        }, BlockStateProperties.WATERLOGGED);

        itemModels().withExistingParent("illumination_tube", "item/generated").texture("layer0", prefix("block/illumination_tube"));

        getVariantBuilder(DecorBlocks.CLAY_DECORATION.get()).forAllStatesExcept(state -> {
            int decoration = state.getValue(ClayDecorationBlock.DECORATION);
            return ConfiguredModel.builder().modelFile(crossModel("clay_decoration_" + decoration, prefix("block/decorations/clay_decoration_" + decoration))).build();
        }, BlockStateProperties.WATERLOGGED);
        itemModels().withExistingParent("clay_decoration", "item/generated").texture("layer0", prefix("block/decorations/clay_decoration_0"));

        getVariantBuilder(DecorBlocks.BONE_DECORATION.get()).forAllStatesExcept(state -> {
            int decoration = state.getValue(BoneDecorationBlock.DECORATION);
            return ConfiguredModel.builder().modelFile(crossModel("bone_decoration_" + decoration, prefix("block/decorations/bone_decoration_" + decoration))).build();
        }, BlockStateProperties.WATERLOGGED);
        itemModels().withExistingParent("bone_decoration", "item/generated").texture("layer0", prefix("block/decorations/bone_decoration_0"));

        cross(DecorBlocks.PAPER_LANTERN.get());
        cross(DecorBlocks.BONE_LANTERN.get());
        cross(DecorBlocks.IRON_LANTERN.get());

        illuminationPlate();

        doorBlock(DecorBlocks.QUARTZ_DOOR.get(), resource("block/quartz_door_bottom"), resource("block/quartz_door_top"));
        doorBlock(DecorBlocks.CHAIN_LINK_DOOR.get(), resource("block/chain_link_door_bottom"), resource("block/chain_link_door_top"));
        doorBlock(DecorBlocks.GLASS_DOOR.get(), resource("block/glass_door_bottom"), resource("block/glass_door_top"));
        doorBlock(DecorBlocks.STEEL_DOOR.get(), resource("block/steel_door_bottom"), resource("block/steel_door_top"));

        paneBlock(DecorBlocks.CHAIN_LINK_FENCE.get(), resource("block/chain_link_door_bottom"), resource("block/chain_link_door_bottom"));

        colorizer(DecorBlocks.COLORIZER.get(), new ResourceLocation(Constants.MOD_ID, "block/tinted_cube"));
        colorizerRotate(DecorBlocks.COLORIZER_CHAIR.get(), new ResourceLocation(Constants.MOD_ID, "block/chair"));
        colorizerSide(DecorBlocks.COLORIZER_COUNTER.get(), new ResourceLocation(Constants.MOD_ID, "block/counter"));
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

        getModelBuilder(prefix("item/colorizer_brush"), new ResourceLocation(Constants.MOD_ID, "item/brush")).addTexture("handle", new ResourceLocation(Constants.MOD_ID, "item/brush_handle"));

        colorizerOBJ(DecorBlocks.COLORIZER_SLOPE.get(), new ResourceLocation(Constants.MOD_ID, "models/block/slope.obj"));
        colorizerOBJ(DecorBlocks.COLORIZER_SLOPED_ANGLE.get(), new ResourceLocation(Constants.MOD_ID, "models/block/sloped_angle.obj"));
        colorizerOBJ(DecorBlocks.COLORIZER_SLOPED_INTERSECTION.get(), new ResourceLocation(Constants.MOD_ID, "models/block/sloped_intersection.obj"));
        colorizerOBJ(DecorBlocks.COLORIZER_OBLIQUE_SLOPE.get(), new ResourceLocation(Constants.MOD_ID, "models/block/oblique_slope.obj"));
        colorizerOBJ(DecorBlocks.COLORIZER_CORNER.get(), new ResourceLocation(Constants.MOD_ID, "models/block/corner.obj"));
        colorizerOBJ(DecorBlocks.COLORIZER_SLANTED_CORNER.get(), new ResourceLocation(Constants.MOD_ID, "models/block/slanted_corner.obj"));
        colorizerOBJSide(DecorBlocks.COLORIZER_PYRAMID.get(), new ResourceLocation(Constants.MOD_ID, "models/block/pyramid.obj"));
        colorizerOBJSide(DecorBlocks.COLORIZER_FULL_PYRAMID.get(), new ResourceLocation(Constants.MOD_ID, "models/block/full_pyramid.obj"));
        colorizerOBJSide(DecorBlocks.COLORIZER_SLOPED_POST.get(), new ResourceLocation(Constants.MOD_ID, "models/block/sloped_post.obj"));

        ColorizerModelBuilder chimneyModel = getModelBuilder(name(DecorBlocks.COLORIZER_CHIMNEY.get()), new ResourceLocation(Constants.MOD_ID, "block/chimney")).addTexture("top", new ResourceLocation(Constants.MOD_ID, "block/chimney_top"));
        getVariantBuilder(DecorBlocks.COLORIZER_CHIMNEY.get()).partialState().setModels(new ConfiguredModel(chimneyModel));
        itemModels().getBuilder(name(DecorBlocks.COLORIZER_CHIMNEY.get())).parent(chimneyModel);

        colorizerFireplace();
        colorizerFirepit();
        colorizerFireringStove();

        pot();

        BlockModelBuilder fluro = this.models().getBuilder(prefix("block/fluro")).parent(this.models().getExistingFile(resource("block/color_cube_all")));
        fluro.texture("all", resource("block/fluro"));

        FluroBlock.FLURO_BY_DYE.entrySet().stream().forEach((x) -> {
            Block b = x.getValue().get();
            ModelFile fluroModel = models().withExistingParent(name(b), prefix("block/fluro"));

            getVariantBuilder(b).partialState().addModels(new ConfiguredModel(fluroModel));
            itemModels().getBuilder(name(b)).parent(fluroModel);
        });

        calendar();
        wallClock();
        fountain();

        this.loaderModels.previousModels();
    }

    private void genericRoadway(Block b) {
        String name = name(b);
        getVariantBuilder(b).partialState().setModels(ConfiguredModel.builder().modelFile(roadwayModel(name, resource("block/roadways/" + name))).build());
        itemModels().withExistingParent(name, prefix("block/" + name));
    }

    private void genericSiding(Block b) {
        String name = name(b);
        getVariantBuilder(b).partialState().setModels(ConfiguredModel.builder().modelFile(models().withExistingParent(name, resource("block/color_cube_bottom_top")).texture("side", resource("block/" + name)).texture("bottom", resource("block/siding_top_bottom")).texture("top", resource("block/siding_top_bottom"))).build());
        itemModels().withExistingParent(name, prefix("block/" + name));
    }

    private void fountain() {
        String name = name(DecorBlocks.FOUNTAIN.get());
        getVariantBuilder(DecorBlocks.FOUNTAIN.get()).partialState().setModels(ConfiguredModel.builder().modelFile(models().cubeBottomTop(name, new ResourceLocation("block/furnace_side"), new ResourceLocation("block/furnace_top"), resource("block/fountain_top"))).build());
        itemModels().withExistingParent(name, prefix("block/" + name));
    }

    private BlockModelBuilder roadwayModel(String name, ResourceLocation top) {
        return models().cubeBottomTop(name, resource("block/roadways/roadway_side"), resource("block/roadways/roadway_bottom"), top);
    }

    private void genericBlock(Block b) {
        String name = name(b);
        simpleBlock(b);
        itemModels().withExistingParent(name, prefix("block/" + name));
    }

    private void genericCutoutBlock(Block b) {
        String name = name(b);
        simpleBlock(b, models().cubeAll(name, blockTexture(b)));
        itemModels().withExistingParent(name, prefix("block/" + name));
    }

    private BlockModelBuilder crossModel(String name, String texture) {
        return models().cross(name, new ResourceLocation(texture));
    }

    private void cross(Block b) {
        String s = name(b);
        ResourceLocation texture = blockTexture(b);

        getVariantBuilder(b).partialState().setModels(new ConfiguredModel(models().cross(s, texture)));
        itemModels().withExistingParent(s, "item/generated").texture("layer0", texture);
    }

    private void illuminationPlate() {
        BlockModelBuilder plateModel = this.models().getBuilder(prefix("block/illumination_plate")).parent(this.models().getExistingFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/block"))).texture("particle", prefix("block/illumination_plate")).texture("texture", prefix("block/illumination_plate"));
        plateModel.element().from(4, 0, 4).to(12, 2, 12).allFaces((dir, face) -> {
            switch (dir) {
                case EAST:
                    face.texture("#texture").uvs(4, 14, 12, 16);
                    break;
                case NORTH:
                    face.texture("#texture").uvs(4, 14, 12, 16);
                    break;
                case SOUTH:
                    face.texture("#texture").uvs(4, 14, 12, 16);
                    break;
                case WEST:
                    face.texture("#texture").uvs(4, 14, 12, 16);
                    break;
                case DOWN:
                    face.texture("#texture").uvs(12, 12, 4, 4).cullface(Direction.DOWN);
                    break;
                case UP:
                default:
                    face.texture("#texture").uvs(4, 4, 12, 12);
                    break;
            }
        });

        BlockModelBuilder plateWallModel = this.models().getBuilder(prefix("block/illumination_plate_wall")).parent(this.models().getExistingFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/block"))).texture("particle", prefix("block/illumination_plate")).texture("texture", prefix("block/illumination_plate"));
        plateWallModel.element().from(0, 4, 4).to(2, 12, 12).allFaces((dir, face) -> {
            switch (dir) {
                case EAST:
                    face.texture("#texture").uvs(4, 4, 12, 12);
                    break;
                case NORTH:
                    face.texture("#texture").uvs(14, 4, 16, 12);
                    break;
                case SOUTH:
                    face.texture("#texture").uvs(0, 4, 2, 12);
                    break;
                case WEST:
                    face.texture("#texture").uvs(4, 4, 12, 12).cullface(Direction.WEST);
                    break;
                case DOWN:
                    face.texture("#texture").uvs(12, 16, 4, 14).rotation(FaceRotation.COUNTERCLOCKWISE_90);
                    break;
                case UP:
                default:
                    face.texture("#texture").uvs(4, 14, 12, 16).rotation(FaceRotation.CLOCKWISE_90);
                    break;
            }
        });

        Function<BlockState, ModelFile> modelFunc = (state) -> {
            return state.getValue(IlluminationTubeBlock.FACING).getAxis().isVertical() ? plateModel : plateWallModel;
        };

        getVariantBuilder(DecorBlocks.ILLUMINATION_PLATE.get()).forAllStatesExcept(state -> {
            Direction dir = state.getValue(BlockStateProperties.FACING);
            return ConfiguredModel.builder().modelFile(modelFunc.apply(state)).rotationX(dir == Direction.DOWN ? 180 : 0).rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 90) % 360).build();
        }, BlockStateProperties.WATERLOGGED);

        itemModels().getBuilder(name(DecorBlocks.ILLUMINATION_PLATE.get())).parent(plateWallModel);
    }

    private void extraModels() {
        BlockModelBuilder model = this.models().getBuilder(prefix("block/tinted_cube")).parent(this.models().getExistingFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/block"))).texture("particle", new ResourceLocation(Constants.MOD_ID, "block/colorizer")).texture("stored", new ResourceLocation(Constants.MOD_ID, "block/colorizer"));
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

        BlockModelBuilder color_cube_bottom_top = this.models().getBuilder(prefix("block/color_cube_bottom_top")).parent(this.models().getExistingFile(resource("block/color_cube")));
        color_cube_bottom_top.texture("particle", "#side").texture("down", "#bottom").texture("up", "#top").texture("north", "#side").texture("south", "#side").texture("west", "#side").texture("east", "#side");
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
        return ForgeRegistries.BLOCKS.getKey(i).getPath();
    }

    private void colorizer(Block b, ResourceLocation model) {
        colorizer(ColorizerUnbakedModel.LOADER_NAME, b, model, false, false, false, false);
    }

    private void colorizerSide(Block b, ResourceLocation model) {
        colorizer(ColorizerUnbakedModel.LOADER_NAME, b, model, true, false, true, true);
    }

    private void colorizerRotate(Block b, ResourceLocation model) {
        colorizer(ColorizerUnbakedModel.LOADER_NAME, b, model, false, false, true, false);
    }

    private void colorizerOBJ(Block b, ResourceLocation model) {
        colorizer(ColorizerObjModel.LOADER_NAME, b, model, true, true, true, false);
    }

    private void colorizerOBJSide(Block b, ResourceLocation model) {
        colorizer(ColorizerObjModel.LOADER_NAME, b, model, true, true, true, true);
    }

    private void colorizer(ResourceLocation loader, Block b, ResourceLocation model, boolean defaultPerspective, boolean defaultPerspectiveFlipped, boolean rotate, boolean side) {
        String name = name(b);

        ColorizerModelBuilder colorizerParent = this.loaderModels.getBuilder(name).loader(loader).texture("particle", new ResourceLocation(Constants.MOD_ID, "block/colorizer"));
        if (loader.equals(ColorizerObjModel.LOADER_NAME)) {
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
        return this.loaderModels.getBuilder(builderName).loader(ColorizerUnbakedModel.LOADER_NAME).colorizer(model).texture("particle", new ResourceLocation(Constants.MOD_ID, "block/colorizer"));
    }

    private ResourceLocation resource(String name) {
        return new ResourceLocation(Constants.MOD_ID, name);
    }

    private void customLoaderState(Block block, ConfiguredModel model) {
        getVariantBuilder(block).partialState().setModels(model);
    }

    private void customLoaderStateRotate(Block block, ConfiguredModel model) {
        getVariantBuilder(block).forAllStatesExcept(state -> {
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            Half half = state.getValue(BlockStateProperties.HALF);
            int yRot = ((int) facing.getClockWise().toYRot() + (half == Half.TOP ? 270 : 90)) % 360;
            boolean uvlock = yRot != 0 || half == Half.TOP;
            return ConfiguredModel.builder().modelFile(model.model).rotationY(yRot).rotationX(half == Half.TOP ? 180 : 0).uvLock(uvlock).build();
        }, BlockStateProperties.WATERLOGGED);
    }

    private void customLoaderStateSide(Block block, ConfiguredModel model) {
        getVariantBuilder(block).forAllStatesExcept(state -> ConfiguredModel.builder().modelFile(model.model).rotationX(state.getValue(BlockStateProperties.ATTACH_FACE).ordinal() * 90).rotationY((((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) + (state.getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.CEILING ? 180 : 0)) % 360).build(), BlockStateProperties.WATERLOGGED);
    }

    private void defaultPerspective(ModelBuilder<?> model) {
        model.transforms().transform(ItemDisplayContext.GUI).rotation(30, 225, 0).translation(0, 0, 0).scale(0.625f).end().transform(ItemDisplayContext.GROUND).rotation(0, 0, 0).translation(0, 3, 0).scale(0.25f).end().transform(ItemDisplayContext.FIXED).rotation(0, 0, 0).translation(0, 0, 0).scale(0.5f).end().transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).rotation(75, 45, 0).translation(0, 2.5f, 0).scale(0.375f).end().transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).rotation(0, 45, 0).translation(0, 0, 0)
                .scale(0.40f).end().transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).rotation(0, 225, 0).translation(0, 0, 0).scale(0.40f).end();
    }

    private void defaultPerspectiveFlipped(ModelBuilder<?> model) {
        model.transforms().transform(ItemDisplayContext.GUI).rotation(30, 30, 0).translation(0, 0, 0).scale(0.625f).end().transform(ItemDisplayContext.GROUND).rotation(0, 0, 0).translation(0, 3, 0).scale(0.25f).end().transform(ItemDisplayContext.FIXED).rotation(0, 0, 0).translation(0, 0, 0).scale(0.5f).end().transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).rotation(75, 45, 0).translation(0, 2.5f, 0).scale(0.375f).end().transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).rotation(0, 45, 0).translation(0, 0, 0)
                .scale(0.40f).end().transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).rotation(0, 225, 0).translation(0, 0, 0).scale(0.40f).end();
    }

    private void colorizerStool() {
        String name = name(DecorBlocks.COLORIZER_STOOL.get());
        ConfiguredModel colorizerStoolModel = getModel("colorizer_stool", new ResourceLocation(Constants.MOD_ID, "block/stool"));
        ConfiguredModel colorizerStoolUpModel = getModel("colorizer_stool_up", new ResourceLocation(Constants.MOD_ID, "block/stool_up"));
        getVariantBuilder(DecorBlocks.COLORIZER_STOOL.get()).forAllStatesExcept(state -> ConfiguredModel.builder().modelFile(state.getValue(ColorizerStoolBlock.UP) ? colorizerStoolUpModel.model : colorizerStoolModel.model).rotationX(state.getValue(BlockStateProperties.ATTACH_FACE).ordinal() * 90).rotationY((((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) + (state.getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.CEILING ? 180 : 0)) % 360).build(),
                BlockStateProperties.WATERLOGGED);
        itemModels().getBuilder(name).parent(colorizerStoolModel.model);
    }

    private void calendar() {
        BlockModelBuilder calendarModel = this.models().getBuilder(prefix("block/calendar")).parent(this.models().getExistingFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/block"))).texture("particle", new ResourceLocation(Constants.MOD_ID, "block/calendar")).texture("all", new ResourceLocation(Constants.MOD_ID, "block/calendar"));

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

        getVariantBuilder(DecorBlocks.CALENDAR.get()).forAllStates((state -> ConfiguredModel.builder().modelFile(calendarModel).rotationY((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot()).build()));
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

        getVariantBuilder(DecorBlocks.WALL_CLOCK.get()).forAllStates((state -> ConfiguredModel.builder().modelFile(clocks.get(state.getValue(WallClockBlock.TIME))).rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 90) % 360).build()));
    }

    private void pot() {
        BlockModelBuilder potModel = this.models().getBuilder(prefix("block/planter_pot_down")).parent(this.models().getExistingFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/block"))).texture("particle", new ResourceLocation(Constants.MOD_ID, "block/planter_pot")).texture("side", new ResourceLocation(Constants.MOD_ID, "block/planter_pot"));

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

        String[] textures = new String[]{"dirt", "sand", "gravel", "clay", "farmland", "netherrack", "soul_sand"};
        getVariantBuilder(DecorBlocks.PLANTER_POT.get()).forAllStates(state -> {
            boolean down = state.getValue(PlanterPotBlock.DOWN);
            int top = state.getValue(PlanterPotBlock.TOP);

            BlockModelBuilder newModel;

            if (down) {
                newModel = this.models().getBuilder(prefix("block/planter_pot_down_" + top)).parent(this.models().getExistingFile(potModel.getLocation())).texture("top", mcLoc(ModelProvider.BLOCK_FOLDER + "/" + textures[top]));
            } else {
                newModel = this.models().getBuilder(prefix("block/planter_pot_" + top)).parent(this.models().getExistingFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/cube_top"))).texture("particle", new ResourceLocation(Constants.MOD_ID, "block/planter_pot")).texture("side", new ResourceLocation(Constants.MOD_ID, "block/planter_pot")).texture("top", mcLoc(ModelProvider.BLOCK_FOLDER + "/" + textures[top]));
            }

            return ConfiguredModel.builder().modelFile(models().getExistingFile(newModel.getLocation())).build();
        });

        itemModels().withExistingParent(prefix("item/planter_pot"), prefix("block/planter_pot_0"));
    }

    private void colorizerTable() {
        String name = name(DecorBlocks.COLORIZER_TABLE.get());

        ConfiguredModel colorizerCounterModel = getModel("colorizer_counter", new ResourceLocation(Constants.MOD_ID, "block/counter"));
        ConfiguredModel colorizerTableNModel = getModel("colorizer_table_n", new ResourceLocation(Constants.MOD_ID, "block/table_n"));
        ConfiguredModel colorizerTableSEModel = getModel("colorizer_table_se", new ResourceLocation(Constants.MOD_ID, "block/table_se"));
        ConfiguredModel colorizerTableNWallModel = getModel("colorizer_table_n_wall", new ResourceLocation(Constants.MOD_ID, "block/table_n_wall"));
        ConfiguredModel colorizerTableSEWallModel = getModel("colorizer_table_se_wall", new ResourceLocation(Constants.MOD_ID, "block/table_se_wall"));
        ConfiguredModel colorizerTableModel = getModel("colorizer_table", new ResourceLocation(Constants.MOD_ID, "block/table"));

        getVariantBuilder(DecorBlocks.COLORIZER_TABLE.get()).forAllStatesExcept(state -> {
            boolean east = state.getValue(ColorizerTableBlock.EAST);
            boolean north = state.getValue(ColorizerTableBlock.NORTH);
            boolean south = state.getValue(ColorizerTableBlock.SOUTH);
            boolean west = state.getValue(ColorizerTableBlock.WEST);
            boolean up = state.getValue(ColorizerTableBlock.UP);
            boolean down = state.getValue(ColorizerTableBlock.DOWN);

            AttachFace face = state.getValue(BlockStateProperties.ATTACH_FACE);
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);

            int numConnections = countConnections(east, north, south, west, up, down);

            if (numConnections >= 3) {
                return ConfiguredModel.builder().modelFile(colorizerCounterModel.model).rotationX(face.ordinal() * 90).rotationY((((int) facing.toYRot() + 180)) % 360).uvLock(true).build();
            } else if (numConnections == 2) {
                boolean oppositeEnds = (north && south) || (west && east) || (up && down);

                if (oppositeEnds) {
                    return ConfiguredModel.builder().modelFile(colorizerCounterModel.model).rotationX(face.ordinal() * 90).rotationY((((int) facing.toYRot() + 180)) % 360).uvLock(true).build();
                } else {
                    int rotY = west && south ? 90 : north && west ? 180 : north && east ? 270 : 0;
                    int rotX = 0;

                    if (face == AttachFace.CEILING) {
                        rotY += 90;
                    } else if (face == AttachFace.WALL) {
                        rotY = ((((int) facing.toYRot() + 180)) % 360) + 270;

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
                    rotY = (((int) facing.toYRot() + 180)) % 360;
                    rotY += up ? 180 : down ? 0 : 270;
                    boolean flag = (south && facing == Direction.EAST) || (east && facing == Direction.NORTH) || (north && facing == Direction.WEST) || (west && facing == Direction.SOUTH);

                    rotX = up ? 180 : flag ? 180 : 0;

                    if (!(up || down)) {
                        return ConfiguredModel.builder().modelFile(colorizerTableNWallModel.model).rotationX(rotX).rotationY(rotY).uvLock(true).build();
                    }
                }

                return ConfiguredModel.builder().modelFile(colorizerTableNModel.model).rotationX((face.ordinal() * 90) + rotX).rotationY(rotY).uvLock(true).build();
            }

            return ConfiguredModel.builder().modelFile(colorizerTableModel.model).rotationX(face.ordinal() * 90).rotationY((((int) facing.toYRot() + 180)) % 360).uvLock(true).build();
        }, BlockStateProperties.WATERLOGGED);

        itemModels().getBuilder(name).parent(colorizerTableModel.model);
    }

    private void colorizerFenceGate() {
        ConfiguredModel colorizerFenceGateModel = getModel("colorizer_fence_gate", new ResourceLocation(Constants.MOD_ID, "block/fence_gate"));
        ConfiguredModel colorizerFenceGateOpenModel = getModel("colorizer_fence_gate_open", new ResourceLocation(Constants.MOD_ID, "block/fence_gate_open"));
        ConfiguredModel colorizerFenceGateWallModel = getModel("colorizer_fence_gate_wall", new ResourceLocation(Constants.MOD_ID, "block/fence_gate_wall"));
        ConfiguredModel colorizerFenceGateWallOpenModel = getModel("colorizer_fence_gate_wall_open", new ResourceLocation(Constants.MOD_ID, "block/fence_gate_wall_open"));

        fenceGateBlock(DecorBlocks.COLORIZER_FENCE_GATE.get(), colorizerFenceGateModel.model, colorizerFenceGateOpenModel.model, colorizerFenceGateWallModel.model, colorizerFenceGateWallOpenModel.model);

        itemModels().getBuilder(prefix("item/colorizer_fence_gate")).parent(colorizerFenceGateModel.model);
    }

    private void colorizerFence() {
        ConfiguredModel colorizerFencePostModel = getModel("colorizer_fence_post", new ResourceLocation(Constants.MOD_ID, "block/fence_post"));
        ConfiguredModel colorizerFenceSideModel = getModel("colorizer_fence_side", new ResourceLocation(Constants.MOD_ID, "block/fence_side"));


        MultiPartBlockStateBuilder builder = getMultipartBuilder(DecorBlocks.COLORIZER_FENCE.get()).part().modelFile(colorizerFencePostModel.model).addModel().end();
        fourWayMultipart(builder, colorizerFenceSideModel.model);

        getModel(prefix("item/colorizer_fence"), new ResourceLocation(Constants.MOD_ID, "item/fence_inventory"));
    }

    private void colorizerWall() {
        ConfiguredModel colorizerWallPostModel = getModel("colorizer_wall_post", new ResourceLocation(Constants.MOD_ID, "block/wall_post"));
        ConfiguredModel colorizerWallSideModel = getModel("colorizer_wall_side", new ResourceLocation(Constants.MOD_ID, "block/wall_side"));
        ConfiguredModel colorizerWallSideTallModel = getModel("colorizer_wall_side_tall", new ResourceLocation(Constants.MOD_ID, "block/wall_side_tall"));

        wallBlock(DecorBlocks.COLORIZER_WALL.get(), colorizerWallPostModel.model, colorizerWallSideModel.model, colorizerWallSideTallModel.model);

        getModel(prefix("item/colorizer_wall"), new ResourceLocation(Constants.MOD_ID, "item/wall_inventory"));
    }

    private void colorizerTrapDoor() {
        ConfiguredModel colorizerTrapdoorBottomModel = getModel("colorizer_trapdoor_bottom", new ResourceLocation(Constants.MOD_ID, "block/trapdoor_bottom"));
        ConfiguredModel colorizerTrapdoorOpenModel = getModel("colorizer_trapdoor_open", new ResourceLocation(Constants.MOD_ID, "block/trapdoor_open"));
        ConfiguredModel colorizerTrapdoorTopModel = getModel("colorizer_trapdoor_top", new ResourceLocation(Constants.MOD_ID, "block/trapdoor_top"));

        trapdoorBlock(DecorBlocks.COLORIZER_TRAP_DOOR.get(), colorizerTrapdoorBottomModel.model, colorizerTrapdoorTopModel.model, colorizerTrapdoorOpenModel.model, true);
        itemModels().getBuilder(prefix("item/colorizer_trap_door")).parent(colorizerTrapdoorBottomModel.model);
    }

    private void colorizerDoor() {
        ConfiguredModel colorizerDoorBottomLeftModel = getModel("colorizer_door_bottom_left", new ResourceLocation(Constants.MOD_ID, "block/door_bottom_left"));
        ConfiguredModel colorizerDoorBottomRightModel = getModel("colorizer_door_bottom_right", new ResourceLocation(Constants.MOD_ID, "block/door_bottom_right"));
        ConfiguredModel colorizerDoorBottomLeftOpenModel = getModel("colorizer_door_bottom_left_open", new ResourceLocation(Constants.MOD_ID, "block/door_bottom_left_open"));
        ConfiguredModel colorizerDoorBottomRightOpenModel = getModel("colorizer_door_bottom_right_open", new ResourceLocation(Constants.MOD_ID, "block/door_bottom_right_open"));
        ConfiguredModel colorizerDoorTopLeftModel = getModel("colorizer_door_top_left", new ResourceLocation(Constants.MOD_ID, "block/door_top_left"));
        ConfiguredModel colorizerDoorTopRightModel = getModel("colorizer_door_top_right", new ResourceLocation(Constants.MOD_ID, "block/door_top_right"));
        ConfiguredModel colorizerDoorTopLeftOpenModel = getModel("colorizer_door_top_left_open", new ResourceLocation(Constants.MOD_ID, "block/door_top_left_open"));
        ConfiguredModel colorizerDoorTopRightOpenModel = getModel("colorizer_door_top_right_open", new ResourceLocation(Constants.MOD_ID, "block/door_top_right_open"));

        doorBlock(DecorBlocks.COLORIZER_DOOR.get(), colorizerDoorBottomLeftModel.model, colorizerDoorBottomLeftOpenModel.model, colorizerDoorBottomRightModel.model, colorizerDoorBottomRightOpenModel.model, colorizerDoorTopLeftModel.model, colorizerDoorTopLeftOpenModel.model, colorizerDoorTopRightModel.model, colorizerDoorTopRightOpenModel.model);
        getModel(prefix("item/colorizer_door"), new ResourceLocation(Constants.MOD_ID, "item/door"));
    }

    private void colorizerStairs() {
        ConfiguredModel colorizerStairsModel = getModel("colorizer_stairs", new ResourceLocation(Constants.MOD_ID, "block/stairs"));
        ConfiguredModel colorizerInnerStairsModel = getModel("colorizer_inner_stairs", new ResourceLocation(Constants.MOD_ID, "block/inner_stairs"));
        ConfiguredModel colorizerOuterStairsModel = getModel("colorizer_outer_stairs", new ResourceLocation(Constants.MOD_ID, "block/outer_stairs"));

        stairsBlock(DecorBlocks.COLORIZER_STAIRS.get(), colorizerStairsModel.model, colorizerInnerStairsModel.model, colorizerOuterStairsModel.model);
        itemModels().getBuilder(prefix("item/colorizer_stairs")).parent(colorizerStairsModel.model);
    }

    private void colorizerSlab() {
        ConfiguredModel colorizerSlabModel = getModel("colorizer_slab", new ResourceLocation(Constants.MOD_ID, "block/slab"));
        ConfiguredModel colorizerSlabTopModel = getModel("colorizer_slab_top", new ResourceLocation(Constants.MOD_ID, "block/slab_top"));
        ModelFile colorizerModel = models().getExistingFile(new ResourceLocation(Constants.MOD_ID, "block/colorizer"));

        slabBlock(DecorBlocks.COLORIZER_SLAB.get(), colorizerSlabModel.model, colorizerSlabTopModel.model, colorizerModel);
        itemModels().getBuilder(prefix("item/colorizer_slab")).parent(colorizerSlabModel.model);
    }

    private void colorizerVerticalSlab() {
        ConfiguredModel slabNorthModel = new ConfiguredModel(getModelBuilder("colorizer_vertical_slab_north", new ResourceLocation(Constants.MOD_ID, "block/vertical_slab")));
        ConfiguredModel slabSouthModel = new ConfiguredModel(getModelBuilder("colorizer_vertical_slab_south", new ResourceLocation(Constants.MOD_ID, "block/vertical_slab")), 0, 180, false);
        ConfiguredModel slabWestModel = new ConfiguredModel(getModelBuilder("colorizer_vertical_slab_west", new ResourceLocation(Constants.MOD_ID, "block/vertical_slab")), 0, 270, false);
        ConfiguredModel slabEastModel = new ConfiguredModel(getModelBuilder("colorizer_vertical_slab_east", new ResourceLocation(Constants.MOD_ID, "block/vertical_slab")), 0, 90, false);
        ModelFile colorizerModel = models().getExistingFile(new ResourceLocation(Constants.MOD_ID, "block/colorizer"));

        getVariantBuilder(DecorBlocks.COLORIZER_VERTICAL_SLAB.get()).partialState().with(ColorizerVerticalSlabBlock.TYPE, VerticalSlabType.NORTH).addModels(slabNorthModel).partialState().with(ColorizerVerticalSlabBlock.TYPE, VerticalSlabType.SOUTH).addModels(slabSouthModel).partialState().with(ColorizerVerticalSlabBlock.TYPE, VerticalSlabType.WEST).addModels(slabWestModel).partialState().with(ColorizerVerticalSlabBlock.TYPE, VerticalSlabType.EAST).addModels(slabEastModel).partialState()
                .with(ColorizerVerticalSlabBlock.TYPE, VerticalSlabType.DOUBLE).addModels(new ConfiguredModel(colorizerModel));

        itemModels().getBuilder(prefix("item/colorizer_vertical_slab")).parent(slabNorthModel.model);
    }

    private void colorizerLampPost() {
        ConfiguredModel colorizerLampPostBottomModel = getModel("colorizer_lamp_post_bottom", new ResourceLocation(Constants.MOD_ID, "block/lamp_post_bottom"));
        ConfiguredModel colorizerLampPostMiddleModel = getModel("colorizer_lamp_post_middle", new ResourceLocation(Constants.MOD_ID, "block/lamp_post_middle"));
        ColorizerModelBuilder colorizerLampPostTopModel = getModelBuilder("colorizer_lamp_post_top", new ResourceLocation(Constants.MOD_ID, "block/lamp_post_top")).addTexture("lamp", new ResourceLocation("block/glowstone"));

        getVariantBuilder(DecorBlocks.COLORIZER_LAMP_POST.get()).partialState().with(ColorizerLampPost.PART, LampPart.BOTTOM).addModels(colorizerLampPostBottomModel).partialState().with(ColorizerLampPost.PART, LampPart.MIDDLE).addModels(colorizerLampPostMiddleModel).partialState().with(ColorizerLampPost.PART, LampPart.TOP).addModels(new ConfiguredModel(colorizerLampPostTopModel));

        getModelBuilder(prefix("item/colorizer_lamp_post"), new ResourceLocation(Constants.MOD_ID, "item/lamp_post_inventory")).addTexture("lamp", new ResourceLocation("block/glowstone"));
    }

    private void colorizerFireplace() {
        ColorizerModelBuilder colorizerFireplaceModel = getModelBuilder("colorizer_fireplace", new ResourceLocation(Constants.MOD_ID, "block/fireplace")).addTexture("wood", new ResourceLocation("block/oak_planks"));
        ColorizerModelBuilder colorizerFireplaceNModel = getModelBuilder("colorizer_fireplace_n", new ResourceLocation(Constants.MOD_ID, "block/fireplace_n")).addTexture("wood", new ResourceLocation("block/oak_planks"));
        ColorizerModelBuilder colorizerFireplaceNEModel = getModelBuilder("colorizer_fireplace_ne", new ResourceLocation(Constants.MOD_ID, "block/fireplace_ne")).addTexture("wood", new ResourceLocation("block/oak_planks"));
        ColorizerModelBuilder colorizerFireplaceNSModel = getModelBuilder("colorizer_fireplace_ns", new ResourceLocation(Constants.MOD_ID, "block/fireplace_ns")).addTexture("wood", new ResourceLocation("block/oak_planks"));
        ModelFile fireModel = models().getExistingFile(new ResourceLocation(Constants.MOD_ID, "block/fire"));

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
        ColorizerModelBuilder colorizerFirepitModel = getModelBuilder("colorizer_firepit", new ResourceLocation(Constants.MOD_ID, "block/firepit")).addTexture("wood", new ResourceLocation("block/oak_planks"));
        ColorizerModelBuilder colorizerFirepitCoveredModel = getModelBuilder("colorizer_firepit_covered", new ResourceLocation(Constants.MOD_ID, "block/firepit_covered")).addTexture("wood", new ResourceLocation("block/oak_planks")).addTexture("net", new ResourceLocation(Constants.MOD_ID, "block/net"));
        ModelFile fireModel = models().getExistingFile(new ResourceLocation(Constants.MOD_ID, "block/fire_high"));

        getMultipartBuilder(DecorBlocks.COLORIZER_FIREPIT.get()).part().modelFile(colorizerFirepitModel).addModel().end().part().modelFile(fireModel).addModel().condition(ColorizerFireplaceBaseBlock.ACTIVE, true).end();
        itemModels().getBuilder(prefix("item/colorizer_firepit")).parent(colorizerFirepitModel);

        getMultipartBuilder(DecorBlocks.COLORIZER_FIREPIT_COVERED.get()).part().modelFile(colorizerFirepitCoveredModel).addModel().end().part().modelFile(fireModel).addModel().condition(ColorizerFireplaceBaseBlock.ACTIVE, true).end();
        itemModels().getBuilder(prefix("item/colorizer_firepit_covered")).parent(colorizerFirepitCoveredModel);
    }

    private void colorizerFireringStove() {
        ColorizerModelBuilder colorizerFireringModel = getModelBuilder("colorizer_firering", new ResourceLocation(Constants.MOD_ID, "block/firering")).addTexture("wood", new ResourceLocation("block/oak_planks"));
        ColorizerModelBuilder colorizerStoveModel = getModelBuilder("colorizer_stove", new ResourceLocation(Constants.MOD_ID, "block/stove")).addTexture("wood", new ResourceLocation("block/oak_planks")).addTexture("net", new ResourceLocation(Constants.MOD_ID, "block/net"));
        ModelFile fireModel = models().getExistingFile(new ResourceLocation(Constants.MOD_ID, "block/fire"));
        ModelFile fireHighModel = models().getExistingFile(new ResourceLocation(Constants.MOD_ID, "block/fire_high"));

        getMultipartBuilder(DecorBlocks.COLORIZER_FIRERING.get()).part().modelFile(colorizerFireringModel).addModel().end().part().modelFile(fireModel).addModel().condition(ColorizerFireplaceBaseBlock.ACTIVE, true).end();
        itemModels().getBuilder(prefix("item/colorizer_firering")).parent(colorizerFireringModel);

        getMultipartBuilder(DecorBlocks.COLORIZER_STOVE.get()).part().modelFile(colorizerStoveModel).addModel().end().part().modelFile(fireHighModel).addModel().condition(ColorizerFireplaceBaseBlock.ACTIVE, true).end();
        itemModels().getBuilder(prefix("item/colorizer_stove")).parent(colorizerStoveModel);
    }
}
