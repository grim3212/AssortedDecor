package com.grim3212.assorted.decor.client.data;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.api.util.VerticalSlabType;
import com.grim3212.assorted.decor.client.color.BlockMapColorItemTintSource;
import com.grim3212.assorted.decor.client.color.ColorizerItemTintSource;
import com.grim3212.assorted.decor.client.color.SidingItemTintSource;
import com.grim3212.assorted.decor.common.blocks.BoneDecorationBlock;
import com.grim3212.assorted.decor.common.blocks.ClayDecorationBlock;
import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.blocks.FluroBlock;
import com.grim3212.assorted.decor.common.blocks.IlluminationTubeBlock;
import com.grim3212.assorted.decor.common.blocks.PlanterPotBlock;
import com.grim3212.assorted.decor.common.blocks.RoadwayLightBlock;
import com.grim3212.assorted.decor.common.blocks.RoadwayManholeBlock;
import com.grim3212.assorted.decor.common.blocks.RoadwayWhiteBlock;
import com.grim3212.assorted.decor.common.blocks.WallClockBlock;
import com.grim3212.assorted.decor.common.blocks.colorizer.ColorizerFireplaceBaseBlock;
import com.grim3212.assorted.decor.common.blocks.colorizer.ColorizerFireplaceBlock;
import com.grim3212.assorted.decor.common.blocks.colorizer.ColorizerLampPost;
import com.grim3212.assorted.decor.common.blocks.colorizer.ColorizerLampPost.LampPart;
import com.grim3212.assorted.decor.common.blocks.colorizer.ColorizerStoolBlock;
import com.grim3212.assorted.decor.common.blocks.colorizer.ColorizerTableBlock;
import com.grim3212.assorted.decor.common.blocks.colorizer.ColorizerVerticalSlabBlock;
import com.grim3212.assorted.decor.common.items.DecorItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.blockstates.PropertyValueList;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Forge's {@code BlockStateProvider}, its {@code ModelFile} / {@code ConfiguredModel} builders and the
 * {@code ExistingFileHelper} / {@code ForgeRegistries} it leaned on are all gone. Block states and
 * block models come from vanilla's {@link ModelProvider} now, which hands a
 * {@link BlockModelGenerators} to {@link #registerModels}; a block state is a
 * {@link MultiVariantGenerator} or {@link MultiPartGenerator} built from {@link PropertyDispatch}es
 * rather than a per state lambda, and an item model is a {@code ClientItem} written to
 * {@code assets/assorteddecor/items/} that names the geometry to draw rather than a
 * {@code models/item/*.json} with a {@code parent}.
 * <p>
 * The colorizer models used to belong to a second provider ({@code ColorizerModelProvider}) because a
 * Forge {@code BlockStateProvider} could only emit {@code BlockModelBuilder}s and a custom loader
 * needed its own builder type. A custom loader is written by a {@link ModelTemplate} now (see
 * {@link ColorizerModelBuilder}), so that provider is deleted and this class writes those models
 * itself.
 * <p>
 * <b>{@code render_type} is not written, and there is nothing to replace it with.</b> The chunk layer
 * a quad draws in is derived while baking from the transparency of the sprite it uses, with
 * {@code Material#forceTranslucent} as the only override, so the cutout and translucent blocks in this
 * mod select their layer from their own textures.
 * <p>
 * The item half of this provider is {@link DecorItemModelProvider}; because one {@link ModelProvider}
 * writes both halves, the two are kept apart by narrowing what each claims to know about. This one
 * owns every block plus every block item.
 */
public class DecorBlockstateProvider extends ModelProvider {

    /**
     * The slot every colorizer shape reads its face texture from. {@link TextureSlot} has no
     * {@code equals}, so it has to be created exactly once and shared.
     */
    private static final TextureSlot STORED = TextureSlot.create("stored");

    private static final Identifier MC_BLOCK = Identifier.withDefaultNamespace("block/block");
    private static final Identifier TINTED_CUBE = resource("block/tinted_cube");

    /** Every colorizer model draws {@code block/colorizer} as its particle. */
    private static final Material COLORIZER_PARTICLE = texture("block/colorizer");

    /**
     * The shape {@code colorizer} itself inherits: a full cube whose faces all read {@code #stored}
     * and all carry tint index 0, which is where the colorizer's {@code BlockTintSource} colours it.
     */
    private static final ModelTemplate TINTED_CUBE_TEMPLATE = defaultPerspective(ExtendedModelTemplateBuilder.builder()
            .parent(MC_BLOCK)
            .requiredTextureSlot(TextureSlot.PARTICLE)
            .requiredTextureSlot(STORED)
            .element(e -> e.from(0, 0, 0).to(16, 16, 16).allFaces((dir, face) -> face.texture(STORED).cullface(dir).tintindex(0))))
            .build();

    /**
     * The 1.20.1 provider built a three step parent chain for the tinted full cubes:
     * {@code color_cube} carried the elements and referenced {@code #down} / {@code #up} / ... ,
     * {@code color_cube_all} pointed all six of those at {@code #all}, and {@code color_cube_bottom_top}
     * pointed them at {@code #side} / {@code #top} / {@code #bottom}. A {@link ModelTemplate} writes a
     * {@code textures} block of {@link Material}s and a {@link Material} is an {@link Identifier}, so
     * it cannot emit the {@code "#all"} slot-to-slot references the middle two models were made of.
     * The two leaves are therefore spelled out directly instead, which is where the elements were
     * always going to end up anyway; {@code block/color_cube*} are no longer generated and nothing
     * outside this provider ever referenced them.
     */
    private static final ModelTemplate COLOR_CUBE_ALL = ExtendedModelTemplateBuilder.builder()
            .parent(MC_BLOCK)
            .requiredTextureSlot(TextureSlot.PARTICLE)
            .requiredTextureSlot(TextureSlot.ALL)
            .element(e -> e.from(0, 0, 0).to(16, 16, 16).allFaces((dir, face) -> face.texture(TextureSlot.ALL).cullface(dir).tintindex(0)))
            .build();

    private static final ModelTemplate COLOR_CUBE_BOTTOM_TOP = ExtendedModelTemplateBuilder.builder()
            .parent(MC_BLOCK)
            .requiredTextureSlot(TextureSlot.PARTICLE)
            .requiredTextureSlot(TextureSlot.SIDE)
            .requiredTextureSlot(TextureSlot.TOP)
            .requiredTextureSlot(TextureSlot.BOTTOM)
            .element(e -> e.from(0, 0, 0).to(16, 16, 16).allFaces((dir, face) -> face.texture(switch (dir) {
                case DOWN -> TextureSlot.BOTTOM;
                case UP -> TextureSlot.TOP;
                default -> TextureSlot.SIDE;
            }).cullface(dir).tintindex(0)))
            .build();

    private static final ModelTemplate ILLUMINATION_PLATE_FLOOR = ExtendedModelTemplateBuilder.builder()
            .parent(MC_BLOCK)
            .requiredTextureSlot(TextureSlot.PARTICLE)
            .requiredTextureSlot(TextureSlot.TEXTURE)
            .element(e -> e.from(4, 0, 4).to(12, 2, 12).allFaces((dir, face) -> {
                switch (dir) {
                    case EAST, NORTH, SOUTH, WEST -> face.texture(TextureSlot.TEXTURE).uvs(4, 14, 12, 16);
                    case DOWN -> face.texture(TextureSlot.TEXTURE).uvs(12, 12, 4, 4).cullface(Direction.DOWN);
                    case UP -> face.texture(TextureSlot.TEXTURE).uvs(4, 4, 12, 12);
                }
            }))
            .build();

    private static final ModelTemplate ILLUMINATION_PLATE_WALL = ExtendedModelTemplateBuilder.builder()
            .parent(MC_BLOCK)
            .requiredTextureSlot(TextureSlot.PARTICLE)
            .requiredTextureSlot(TextureSlot.TEXTURE)
            .element(e -> e.from(0, 4, 4).to(2, 12, 12).allFaces((dir, face) -> {
                switch (dir) {
                    case EAST -> face.texture(TextureSlot.TEXTURE).uvs(4, 4, 12, 12);
                    case NORTH -> face.texture(TextureSlot.TEXTURE).uvs(14, 4, 16, 12);
                    case SOUTH -> face.texture(TextureSlot.TEXTURE).uvs(0, 4, 2, 12);
                    case WEST -> face.texture(TextureSlot.TEXTURE).uvs(4, 4, 12, 12).cullface(Direction.WEST);
                    case DOWN -> face.texture(TextureSlot.TEXTURE).uvs(12, 16, 4, 14).rotation(com.mojang.math.Quadrant.R270);
                    case UP -> face.texture(TextureSlot.TEXTURE).uvs(4, 14, 12, 16).rotation(com.mojang.math.Quadrant.R90);
                }
            }))
            .build();

    private static final ModelTemplate CALENDAR = ExtendedModelTemplateBuilder.builder()
            .parent(MC_BLOCK)
            .requiredTextureSlot(TextureSlot.PARTICLE)
            .requiredTextureSlot(TextureSlot.ALL)
            .element(e -> e.from(4, 2, 0).to(12, 15, 1).allFaces((dir, face) -> {
                switch (dir) {
                    case EAST -> face.texture(TextureSlot.ALL).uvs(14, 1, 16, 15);
                    case NORTH -> face.texture(TextureSlot.ALL).uvs(0, 0, 4, 16).cullface(Direction.NORTH);
                    case SOUTH -> face.texture(TextureSlot.ALL).uvs(2.5F, 0F, 13.5F, 16F);
                    case WEST -> face.texture(TextureSlot.ALL).uvs(0, 1, 2, 15);
                    case DOWN -> face.texture(TextureSlot.ALL).uvs(12, 2, 4, 0);
                    case UP -> face.texture(TextureSlot.ALL).uvs(4, 0, 12, 2);
                }
            }))
            .build();

    /**
     * The wall clock's body. {@code front} is deliberately not a required slot: the base model never
     * fills it in, the sixty four dial models below do.
     */
    private static final ModelTemplate WALL_CLOCK_BODY = ExtendedModelTemplateBuilder.builder()
            .parent(MC_BLOCK)
            .requiredTextureSlot(TextureSlot.PARTICLE)
            .requiredTextureSlot(TextureSlot.BACK)
            .requiredTextureSlot(TextureSlot.SIDE)
            .element(e -> e.from(0, 0, 0).to(2, 16, 16).allFaces((dir, face) -> {
                switch (dir) {
                    case EAST -> face.texture(TextureSlot.FRONT).uvs(0, 0, 16, 16);
                    case NORTH -> face.texture(TextureSlot.SIDE).uvs(14, 0, 16, 16).cullface(Direction.NORTH);
                    case SOUTH -> face.texture(TextureSlot.SIDE).uvs(0, 0, 2, 16).cullface(Direction.SOUTH);
                    case WEST -> face.texture(TextureSlot.BACK).uvs(0, 0, 16, 16).cullface(Direction.WEST);
                    case DOWN -> face.texture(TextureSlot.SIDE).uvs(16, 16, 14, 0).cullface(Direction.DOWN);
                    case UP -> face.texture(TextureSlot.SIDE).uvs(0, 0, 2, 16).cullface(Direction.UP);
                }
            }))
            .build();

    private static final ModelTemplate WALL_CLOCK_DIAL = ExtendedModelTemplateBuilder.builder()
            .parent(resource("block/wall_clock"))
            .requiredTextureSlot(TextureSlot.FRONT)
            .build();

    /**
     * The planter pot with a hole in the bottom. {@code top} is filled in per soil by the seven models
     * that inherit this one.
     */
    private static final ModelTemplate PLANTER_POT_DOWN = ExtendedModelTemplateBuilder.builder()
            .parent(MC_BLOCK)
            .requiredTextureSlot(TextureSlot.PARTICLE)
            .requiredTextureSlot(TextureSlot.SIDE)
            .element(e -> e.from(3, 0, 3).to(13, 16, 13).allFaces((dir, face) -> {
                switch (dir) {
                    case EAST, NORTH, SOUTH, WEST -> face.texture(TextureSlot.SIDE).uvs(3, 0, 13, 16);
                    case DOWN -> face.texture(TextureSlot.SIDE).cullface(Direction.DOWN).uvs(13, 13, 3, 3);
                    case UP -> face.texture(TextureSlot.TOP).cullface(Direction.UP).uvs(3, 3, 13, 13);
                }
            }))
            .build();

    private static final ModelTemplate PLANTER_POT_DOWN_SOIL = ExtendedModelTemplateBuilder.builder()
            .parent(resource("block/planter_pot_down"))
            .requiredTextureSlot(TextureSlot.TOP)
            .build();

    /** The soils a planter pot's surface can be, indexed by {@link PlanterPotBlock#TOP}. */
    private static final String[] PLANTER_POT_SOILS = {"dirt", "sand", "gravel", "clay", "farmland", "netherrack", "soul_sand"};

    public DecorBlockstateProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted Decor block states";
    }

    /**
     * Only the block items belong here; every other item is {@link DecorItemModelProvider}'s, so the
     * two providers never write the same file.
     */
    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return super.getKnownItems().filter(holder -> holder.value() instanceof BlockItem);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        TINTED_CUBE_TEMPLATE.create(TINTED_CUBE, new TextureMapping()
                .put(TextureSlot.PARTICLE, COLORIZER_PARTICLE)
                .put(STORED, COLORIZER_PARTICLE), blockModels.modelOutput);

        blockModels.createTrivialCube(DecorBlocks.SIDEWALK.get());
        blockModels.createTrivialCube(DecorBlocks.STONE_PATH.get());
        blockModels.createTrivialCube(DecorBlocks.DECORATIVE_STONE.get());
        blockModels.createTrivialCube(DecorBlocks.CAGE.get());

        roadway(blockModels, DecorBlocks.ROADWAY.get());
        DecorBlocks.ROADWAY_COLORS.forEach((color, roadway) -> {
            if (color != DyeColor.WHITE) {
                roadway(blockModels, roadway.get());
            }
        });
        roadwayWhite(blockModels);
        roadwayLight(blockModels);
        roadwayManhole(blockModels);

        siding(blockModels, DecorBlocks.SIDING_HORIZONTAL.get());
        siding(blockModels, DecorBlocks.SIDING_VERTICAL.get());

        neonSigns(blockModels);
        illuminationTube(blockModels);
        illuminationPlate(blockModels);

        decoration(blockModels, DecorBlocks.CLAY_DECORATION.get(), ClayDecorationBlock.DECORATION, "clay_decoration");
        decoration(blockModels, DecorBlocks.BONE_DECORATION.get(), BoneDecorationBlock.DECORATION, "bone_decoration");

        cross(blockModels, DecorBlocks.PAPER_LANTERN.get());
        cross(blockModels, DecorBlocks.BONE_LANTERN.get());
        cross(blockModels, DecorBlocks.IRON_LANTERN.get());

        blockModels.createDoor(DecorBlocks.QUARTZ_DOOR.get());
        blockModels.createDoor(DecorBlocks.CHAIN_LINK_DOOR.get());
        blockModels.createDoor(DecorBlocks.GLASS_DOOR.get());
        blockModels.createDoor(DecorBlocks.STEEL_DOOR.get());

        chainLinkFence(blockModels);

        colorizer(blockModels, DecorBlocks.COLORIZER.get(), TINTED_CUBE);
        colorizerRotate(blockModels, DecorBlocks.COLORIZER_CHAIR.get(), resource("block/chair"));
        Identifier counterModel = colorizerSide(blockModels, DecorBlocks.COLORIZER_COUNTER.get(), resource("block/counter"));
        colorizerTable(blockModels, counterModel);
        colorizerStool(blockModels);
        colorizerFence(blockModels);
        colorizerFenceGate(blockModels);
        colorizerWall(blockModels);
        colorizerTrapDoor(blockModels);
        colorizerDoor(blockModels);
        colorizerStairs(blockModels);
        colorizerSlab(blockModels);
        colorizerVerticalSlab(blockModels);
        colorizerLampPost(blockModels);

        colorizerObj(blockModels, DecorBlocks.COLORIZER_SLOPE.get(), resource("models/block/slope.obj"));
        colorizerObj(blockModels, DecorBlocks.COLORIZER_SLOPED_ANGLE.get(), resource("models/block/sloped_angle.obj"));
        colorizerObj(blockModels, DecorBlocks.COLORIZER_SLOPED_INTERSECTION.get(), resource("models/block/sloped_intersection.obj"));
        colorizerObj(blockModels, DecorBlocks.COLORIZER_OBLIQUE_SLOPE.get(), resource("models/block/oblique_slope.obj"));
        colorizerObj(blockModels, DecorBlocks.COLORIZER_CORNER.get(), resource("models/block/corner.obj"));
        colorizerObj(blockModels, DecorBlocks.COLORIZER_SLANTED_CORNER.get(), resource("models/block/slanted_corner.obj"));
        colorizerObjSide(blockModels, DecorBlocks.COLORIZER_PYRAMID.get(), resource("models/block/pyramid.obj"));
        colorizerObjSide(blockModels, DecorBlocks.COLORIZER_FULL_PYRAMID.get(), resource("models/block/full_pyramid.obj"));
        colorizerObjSide(blockModels, DecorBlocks.COLORIZER_SLOPED_POST.get(), resource("models/block/sloped_post.obj"));

        colorizerChimney(blockModels);
        colorizerFireplace(blockModels);
        colorizerFirepit(blockModels);
        colorizerFireringStove(blockModels);

        planterPot(blockModels);
        fluro(blockModels);
        calendar(blockModels);
        wallClock(blockModels);
        fountain(blockModels);
    }

    // ------------------------------------------------------------------ plain blocks

    /**
     * A plain roadway: one cube model, plus the blockstate and item model pointing at it.
     * <p>
     * The blockstate and item halves were missing, which datagen catches rather than shipping -
     * {@code ModelProvider} fails the run with "Missing blockstate definitions for: ..." listing
     * every block it was never given one for. The white, light and manhole variants below always
     * emitted theirs; only this shared helper did not.
     */
    private void roadway(BlockModelGenerators blockModels, Block b) {
        String name = name(b);
        Identifier model = roadwayModel(blockModels, name, texture("block/roadways/" + name));

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b, BlockModelGenerators.plainVariant(model)));
        blockModels.registerSimpleItemModel(b, model);
    }

    private Identifier roadwayModel(BlockModelGenerators blockModels, String name, Material top) {
        Identifier model = ModelTemplates.CUBE_BOTTOM_TOP.create(resource("block/" + name), new TextureMapping()
                .put(TextureSlot.SIDE, texture("block/roadways/roadway_side"))
                .put(TextureSlot.BOTTOM, texture("block/roadways/roadway_bottom"))
                .put(TextureSlot.TOP, top), blockModels.modelOutput);
        return model;
    }

    private void roadwayWhite(BlockModelGenerators blockModels) {
        Block b = DecorBlocks.ROADWAY_COLORS.get(DyeColor.WHITE).get();
        List<Identifier> models = new ArrayList<>();
        for (int type = 0; type <= 11; type++) {
            models.add(roadwayModel(blockModels, "roadway_white_" + type, texture("block/roadways/roadway_white_" + type)));
        }

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(RoadwayWhiteBlock.TYPE).generate(type -> BlockModelGenerators.plainVariant(models.get(type)))));
        blockModels.registerSimpleItemModel(b, models.get(11));
    }

    private void roadwayLight(BlockModelGenerators blockModels) {
        Block b = DecorBlocks.ROADWAY_LIGHT.get();
        Identifier on = roadwayModel(blockModels, "roadway_light_on", texture("block/roadways/roadway_light_on"));
        Identifier off = roadwayModel(blockModels, "roadway_light_off", texture("block/roadways/roadway_light_off"));

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(BlockModelGenerators.createBooleanModelDispatch(RoadwayLightBlock.ACTIVE, BlockModelGenerators.plainVariant(on), BlockModelGenerators.plainVariant(off))));
        blockModels.registerSimpleItemModel(b, on);
    }

    private void roadwayManhole(BlockModelGenerators blockModels) {
        Block b = DecorBlocks.ROADWAY_MANHOLE.get();
        Identifier open = manholeModel(blockModels, "roadway_manhole_open", "block/roadways/roadway_manhole_bottom", "block/roadways/roadway_manhole_open");
        Identifier closed = manholeModel(blockModels, "roadway_manhole_closed", "block/roadways/roadway_manhole_closed", "block/roadways/roadway_manhole_closed");

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(BlockModelGenerators.createBooleanModelDispatch(RoadwayManholeBlock.OPEN, BlockModelGenerators.plainVariant(open), BlockModelGenerators.plainVariant(closed))));
        blockModels.registerSimpleItemModel(b, closed);
    }

    private Identifier manholeModel(BlockModelGenerators blockModels, String name, String bottom, String top) {
        return ModelTemplates.CUBE_BOTTOM_TOP.create(resource("block/" + name), new TextureMapping()
                .put(TextureSlot.SIDE, texture("block/roadways/roadway_side"))
                .put(TextureSlot.BOTTOM, texture(bottom))
                .put(TextureSlot.TOP, texture(top)), blockModels.modelOutput);
    }

    /**
     * A siding block, tinted by the dye recorded in its block state. The item carries
     * {@link SidingItemTintSource} at index 0 - the index the model stamps on every face - which is
     * what replaced the deleted {@code registerItemColor} handler.
     */
    private void siding(BlockModelGenerators blockModels, Block b) {
        String name = name(b);
        Material side = texture("block/" + name);
        Material topBottom = texture("block/siding_top_bottom");

        Identifier model = COLOR_CUBE_BOTTOM_TOP.create(resource("block/" + name), new TextureMapping()
                .put(TextureSlot.PARTICLE, side)
                .put(TextureSlot.SIDE, side)
                .put(TextureSlot.TOP, topBottom)
                .put(TextureSlot.BOTTOM, topBottom), blockModels.modelOutput);

        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(b, BlockModelGenerators.plainVariant(model)));
        blockModels.registerSimpleTintedItemModel(b, model, new SidingItemTintSource());
    }

    private void fountain(BlockModelGenerators blockModels) {
        Block b = DecorBlocks.FOUNTAIN.get();
        Identifier model = ModelTemplates.CUBE_BOTTOM_TOP.create(resource("block/" + name(b)), new TextureMapping()
                .put(TextureSlot.SIDE, new Material(Identifier.withDefaultNamespace("block/furnace_side")))
                .put(TextureSlot.BOTTOM, new Material(Identifier.withDefaultNamespace("block/furnace_top")))
                .put(TextureSlot.TOP, texture("block/fountain_top")), blockModels.modelOutput);

        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(b, BlockModelGenerators.plainVariant(model)));
    }

    /**
     * The sixteen fluro blocks share one model and are told apart by
     * {@link BlockMapColorItemTintSource} / the block tint source registered in {@code DecorClient}.
     * The 1.20.1 provider emitted an extra per colour model whose only content was a {@code parent}
     * pointing at the shared one; that indirection is dropped.
     */
    private void fluro(BlockModelGenerators blockModels) {
        Identifier model = COLOR_CUBE_ALL.create(resource("block/fluro"), new TextureMapping()
                .put(TextureSlot.PARTICLE, texture("block/fluro"))
                .put(TextureSlot.ALL, texture("block/fluro")), blockModels.modelOutput);

        FluroBlock.FLURO_BY_DYE.values().forEach(supplier -> {
            Block b = supplier.get();
            blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(b, BlockModelGenerators.plainVariant(model)));
            blockModels.registerSimpleTintedItemModel(b, model, new BlockMapColorItemTintSource());
        });
    }

    /**
     * Both neon signs draw nothing but a particle - the board itself is submitted by
     * {@code NeonSignBlockEntityRenderer} - and the wall form shares the standing form's model, as it
     * did in 1.20.1.
     * <p>
     * Neither block registers an item of its own; the sign is placed by {@code DecorItems.NEON_SIGN},
     * a {@code StandingAndWallBlockItem}. That makes it a {@link BlockItem}, so it belongs to this
     * provider rather than to {@link DecorItemModelProvider} - both would otherwise write
     * {@code items/neon_sign.json}, this one pointing at the particle-only block model and that one at
     * the flat sprite. It is a flat sprite, as it was in 1.20.1.
     */
    private void neonSigns(BlockModelGenerators blockModels) {
        Identifier model = ModelTemplates.PARTICLE_ONLY.create(resource("block/" + name(DecorBlocks.NEON_SIGN.get())),
                TextureMapping.particle(new Material(Identifier.withDefaultNamespace("block/obsidian"))), blockModels.modelOutput);

        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(DecorBlocks.NEON_SIGN.get(), BlockModelGenerators.plainVariant(model)));
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(DecorBlocks.NEON_SIGN_WALL.get(), BlockModelGenerators.plainVariant(model)));

        Item sign = DecorItems.NEON_SIGN.get();
        Identifier itemModel = ModelTemplates.FLAT_ITEM.create(resource("item/neon_sign"),
                TextureMapping.layer0(texture("item/neon_sign")), blockModels.modelOutput);
        blockModels.registerSimpleItemModel(sign, itemModel);
    }

    /**
     * The tube is a torch: a standing model for the vertical facings and a wall model for the
     * horizontal ones. The 1.20.1 provider's {@code rotationY(((int) toYRot() + 90) % 360)} is
     * {@link BlockModelGenerators#ROTATION_TORCH} value for value, which is how vanilla's own wall
     * torches are oriented, so the rotation is spelled that way here.
     * <p>
     * The two models were named {@code block/illuminuation_tube[_wall]} in 1.20.1 - a typo. They are
     * generated at the spelling the block actually has; nothing referenced the old names.
     */
    private void illuminationTube(BlockModelGenerators blockModels) {
        Block b = DecorBlocks.ILLUMINATION_TUBE.get();
        Material tube = texture("block/illumination_tube");
        Identifier standing = ModelTemplates.TORCH.create(resource("block/illumination_tube"), TextureMapping.torch(tube), blockModels.modelOutput);
        Identifier wall = ModelTemplates.WALL_TORCH.create(resource("block/illumination_tube_wall"), TextureMapping.torch(tube), blockModels.modelOutput);

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(IlluminationTubeBlock.FACING).generate(dir -> orientTorch(standing, wall, dir))));

        Identifier itemModel = ModelTemplates.FLAT_ITEM.create(resource("item/" + name(b)), TextureMapping.layer0(tube), blockModels.modelOutput);
        blockModels.registerSimpleItemModel(b, itemModel);
    }

    private void illuminationPlate(BlockModelGenerators blockModels) {
        Block b = DecorBlocks.ILLUMINATION_PLATE.get();
        Material plate = texture("block/illumination_plate");
        TextureMapping textures = new TextureMapping().put(TextureSlot.PARTICLE, plate).put(TextureSlot.TEXTURE, plate);

        Identifier floor = ILLUMINATION_PLATE_FLOOR.create(resource("block/illumination_plate"), textures, blockModels.modelOutput);
        Identifier wall = ILLUMINATION_PLATE_WALL.create(resource("block/illumination_plate_wall"), textures, blockModels.modelOutput);

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(IlluminationTubeBlock.FACING).generate(dir -> orientTorch(floor, wall, dir))));

        blockModels.registerSimpleItemModel(b, wall);
    }

    /**
     * A clay or bone decoration: one crossed-quad model per {@code decoration} value, with the item
     * showing the first of them as a flat sprite.
     */
    private void decoration(BlockModelGenerators blockModels, Block b, Property<Integer> property, String name) {
        Map<Integer, Identifier> models = new HashMap<>();
        for (int decoration : property.getPossibleValues()) {
            models.put(decoration, ModelTemplates.CROSS.create(resource("block/" + name + "_" + decoration),
                    TextureMapping.cross(texture("block/decorations/" + name + "_" + decoration)), blockModels.modelOutput));
        }

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(property).generate(decoration -> BlockModelGenerators.plainVariant(models.get(decoration)))));

        Identifier itemModel = ModelTemplates.FLAT_ITEM.create(resource("item/" + name),
                TextureMapping.layer0(texture("block/decorations/" + name + "_0")), blockModels.modelOutput);
        blockModels.registerSimpleItemModel(b, itemModel);
    }

    /**
     * A lantern drawn as two crossed quads, with a flat item sprite over the same texture.
     * <p>
     * Vanilla's {@code createCrossBlock} takes a {@code PlantType} and registers a
     * {@code PlantType}-specific item model, so the two halves are spelled out instead. The 1.20.1
     * version also declared {@code cutout}; the layer comes from the texture now.
     */
    private void cross(BlockModelGenerators blockModels, Block b) {
        Material tex = texture("block/" + name(b));
        Identifier model = ModelTemplates.CROSS.create(b, TextureMapping.cross(tex), blockModels.modelOutput);
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(b, BlockModelGenerators.plainVariant(model)));

        Identifier itemModel = ModelTemplates.FLAT_ITEM.create(resource("item/" + name(b)), TextureMapping.layer0(tex), blockModels.modelOutput);
        blockModels.registerSimpleItemModel(b, itemModel);
    }

    /**
     * The chain link fence is an {@code IronBarsBlock}, which is a glass pane in blockstate terms.
     * Both the pane face and the edge use the door's lower texture, exactly as in 1.20.1.
     */
    private void chainLinkFence(BlockModelGenerators blockModels) {
        Block b = DecorBlocks.CHAIN_LINK_FENCE.get();
        Material link = texture("block/chain_link_door_bottom");
        TextureMapping textures = new TextureMapping().put(TextureSlot.PANE, link).put(TextureSlot.EDGE, link);

        MultiVariant post = BlockModelGenerators.plainVariant(ModelTemplates.STAINED_GLASS_PANE_POST.create(b, textures, blockModels.modelOutput));
        MultiVariant side = BlockModelGenerators.plainVariant(ModelTemplates.STAINED_GLASS_PANE_SIDE.create(b, textures, blockModels.modelOutput));
        MultiVariant sideAlt = BlockModelGenerators.plainVariant(ModelTemplates.STAINED_GLASS_PANE_SIDE_ALT.create(b, textures, blockModels.modelOutput));
        MultiVariant noSide = BlockModelGenerators.plainVariant(ModelTemplates.STAINED_GLASS_PANE_NOSIDE.create(b, textures, blockModels.modelOutput));
        MultiVariant noSideAlt = BlockModelGenerators.plainVariant(ModelTemplates.STAINED_GLASS_PANE_NOSIDE_ALT.create(b, textures, blockModels.modelOutput));

        blockModels.blockStateOutput.accept(MultiPartGenerator.multiPart(b)
                .with(post)
                .with(BlockModelGenerators.condition().term(BlockStateProperties.NORTH, true), side)
                .with(BlockModelGenerators.condition().term(BlockStateProperties.EAST, true), side.with(BlockModelGenerators.Y_ROT_90))
                .with(BlockModelGenerators.condition().term(BlockStateProperties.SOUTH, true), sideAlt)
                .with(BlockModelGenerators.condition().term(BlockStateProperties.WEST, true), sideAlt.with(BlockModelGenerators.Y_ROT_90))
                .with(BlockModelGenerators.condition().term(BlockStateProperties.NORTH, false), noSide)
                .with(BlockModelGenerators.condition().term(BlockStateProperties.EAST, false), noSideAlt)
                .with(BlockModelGenerators.condition().term(BlockStateProperties.SOUTH, false), noSideAlt.with(BlockModelGenerators.Y_ROT_90))
                .with(BlockModelGenerators.condition().term(BlockStateProperties.WEST, false), noSide.with(BlockModelGenerators.Y_ROT_270)));

        Identifier itemModel = ModelTemplates.FLAT_ITEM.create(resource("item/" + name(b)), TextureMapping.layer0(link), blockModels.modelOutput);
        blockModels.registerSimpleItemModel(b, itemModel);
    }

    private void calendar(BlockModelGenerators blockModels) {
        Block b = DecorBlocks.CALENDAR.get();
        Material calendar = texture("block/calendar");
        Identifier model = CALENDAR.create(resource("block/calendar"), new TextureMapping()
                .put(TextureSlot.PARTICLE, calendar).put(TextureSlot.ALL, calendar), blockModels.modelOutput);

        // rotationY(toYRot()) - south 0, west 90, north 180, east 270 - is ROTATION_HORIZONTAL_FACING_ALT.
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b, BlockModelGenerators.plainVariant(model))
                .with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING_ALT));

        blockModels.registerSimpleItemModel(b, ModelTemplates.FLAT_ITEM.create(resource("item/" + name(b)),
                TextureMapping.layer0(texture("item/" + name(b))), blockModels.modelOutput));
    }

    private void wallClock(BlockModelGenerators blockModels) {
        Block b = DecorBlocks.WALL_CLOCK.get();
        Material planks = new Material(Identifier.withDefaultNamespace("block/oak_planks"));
        WALL_CLOCK_BODY.create(resource("block/wall_clock"), new TextureMapping()
                .put(TextureSlot.PARTICLE, planks).put(TextureSlot.BACK, planks).put(TextureSlot.SIDE, planks), blockModels.modelOutput);

        List<Identifier> dials = new ArrayList<>();
        for (int i = 0; i < 64; i++) {
            Identifier dial = resource("block/wall_clock/wall_clock_" + (i + 1));
            dials.add(WALL_CLOCK_DIAL.create(dial, new TextureMapping().put(TextureSlot.FRONT, new Material(dial)), blockModels.modelOutput));
        }

        // rotationY((toYRot() + 90) % 360) - east 0, south 90, west 180, north 270 - is ROTATION_TORCH.
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(WallClockBlock.TIME).generate(time -> BlockModelGenerators.plainVariant(dials.get(time))))
                .with(BlockModelGenerators.ROTATION_TORCH));

        blockModels.registerSimpleItemModel(b, ModelTemplates.FLAT_ITEM.create(resource("item/" + name(b)),
                TextureMapping.layer0(texture("item/" + name(b))), blockModels.modelOutput));
    }

    private void planterPot(BlockModelGenerators blockModels) {
        Block b = DecorBlocks.PLANTER_POT.get();
        Material pot = texture("block/planter_pot");
        PLANTER_POT_DOWN.create(resource("block/planter_pot_down"), new TextureMapping()
                .put(TextureSlot.PARTICLE, pot).put(TextureSlot.SIDE, pot), blockModels.modelOutput);

        List<Identifier> down = new ArrayList<>();
        List<Identifier> up = new ArrayList<>();
        for (int top = 0; top < PLANTER_POT_SOILS.length; top++) {
            Material soil = new Material(Identifier.withDefaultNamespace("block/" + PLANTER_POT_SOILS[top]));
            down.add(PLANTER_POT_DOWN_SOIL.create(resource("block/planter_pot_down_" + top),
                    new TextureMapping().put(TextureSlot.TOP, soil), blockModels.modelOutput));
            up.add(ModelTemplates.CUBE_TOP.create(resource("block/planter_pot_" + top), new TextureMapping()
                    .put(TextureSlot.PARTICLE, pot).put(TextureSlot.SIDE, pot).put(TextureSlot.TOP, soil), blockModels.modelOutput));
        }

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(PlanterPotBlock.TOP, PlanterPotBlock.DOWN)
                        .generate((top, isDown) -> BlockModelGenerators.plainVariant(isDown ? down.get(top) : up.get(top)))));

        blockModels.registerSimpleItemModel(b, up.get(0));
    }

    // ------------------------------------------------------------------ colorizers

    /**
     * A colorizer whose model is the same for every block state.
     */
    private Identifier colorizer(BlockModelGenerators blockModels, Block b, Identifier parent) {
        Identifier model = colorizerModel(blockModels, "block/" + name(b), parent, builder -> {});
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(b, BlockModelGenerators.plainVariant(model)));
        colorizerItem(blockModels, b, model);
        return model;
    }

    /**
     * A colorizer placed against a face, rotated by {@code HALF} and its horizontal facing - the chair
     * and every OBJ slope shape.
     */
    private Identifier colorizerRotate(BlockModelGenerators blockModels, Block b, Identifier parent) {
        return colorizerRotateState(blockModels, b, colorizerModel(blockModels, "block/" + name(b), parent, builder -> {}));
    }

    private Identifier colorizerRotateState(BlockModelGenerators blockModels, Block b, Identifier model) {
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HALF).generate((facing, half) -> {
                    int yRot = ((int) facing.getClockWise().toYRot() + (half == Half.TOP ? 270 : 90)) % 360;
                    boolean uvLock = yRot != 0 || half == Half.TOP;
                    return variant(model, half == Half.TOP ? 180 : 0, yRot, uvLock);
                })));
        colorizerItem(blockModels, b, model);
        return model;
    }

    /**
     * A colorizer attached to any of the six faces - the counter, the pyramids and the sloped post.
     */
    private Identifier colorizerSide(BlockModelGenerators blockModels, Block b, Identifier parent) {
        return colorizerSideState(blockModels, b, colorizerModel(blockModels, "block/" + name(b), parent,
                builder -> {}, DecorBlockstateProvider::defaultPerspective));
    }

    private Identifier colorizerSideState(BlockModelGenerators blockModels, Block b, Identifier model) {
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING)
                        .generate((face, facing) -> variant(model, face.ordinal() * 90, sideYRot(face, facing), false))));
        colorizerItem(blockModels, b, model);
        return model;
    }

    private void colorizerObj(BlockModelGenerators blockModels, Block b, Identifier objModel) {
        colorizerRotateState(blockModels, b, colorizerObjModel(blockModels, "block/" + name(b), objModel));
    }

    private void colorizerObjSide(BlockModelGenerators blockModels, Block b, Identifier objModel) {
        colorizerSideState(blockModels, b, colorizerObjModel(blockModels, "block/" + name(b), objModel));
    }

    private void colorizerChimney(BlockModelGenerators blockModels) {
        Block b = DecorBlocks.COLORIZER_CHIMNEY.get();
        Identifier model = colorizerModel(blockModels, "block/" + name(b), resource("block/chimney"),
                builder -> builder.addTexture("top", resource("block/chimney_top")));

        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(b, BlockModelGenerators.plainVariant(model)));
        colorizerItem(blockModels, b, model);
    }

    private void colorizerStool(BlockModelGenerators blockModels) {
        Block b = DecorBlocks.COLORIZER_STOOL.get();
        Identifier stool = colorizerModel(blockModels, "block/colorizer_stool", resource("block/stool"), builder -> {});
        Identifier stoolUp = colorizerModel(blockModels, "block/colorizer_stool_up", resource("block/stool_up"), builder -> {});

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(ColorizerStoolBlock.UP, BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING)
                        .generate((up, face, facing) -> variant(up ? stoolUp : stool, face.ordinal() * 90, sideYRot(face, facing), false))));

        colorizerItem(blockModels, b, stool);
    }

    /**
     * The table, whose model is chosen from six connection flags plus the face it is attached to and
     * the direction it faces - eight properties, three more than a {@link PropertyDispatch} can carry.
     * The 1.20.1 provider used Forge's {@code forAllStatesExcept}, so the port keeps that shape through
     * {@link #forEachState}, and the branch logic is copied across unchanged.
     * <p>
     * <b>The Y rotations are normalised.</b> Several branches added to an already reduced angle and
     * produced 360, 450 or 540; the 1.20.1 output contains 62 such variants and {@code BlockModelRotation}
     * indexed straight off {@code y / 90}, so those states rendered at the wrong angle. A
     * {@link com.mojang.math.Quadrant} only has four values, so they now normalise to 0, 90 and 180.
     */
    private void colorizerTable(BlockModelGenerators blockModels, Identifier counterModel) {
        Block b = DecorBlocks.COLORIZER_TABLE.get();
        Identifier tableN = colorizerModel(blockModels, "block/colorizer_table_n", resource("block/table_n"), builder -> {});
        Identifier tableSE = colorizerModel(blockModels, "block/colorizer_table_se", resource("block/table_se"), builder -> {});
        Identifier tableNWall = colorizerModel(blockModels, "block/colorizer_table_n_wall", resource("block/table_n_wall"), builder -> {});
        Identifier tableSEWall = colorizerModel(blockModels, "block/colorizer_table_se_wall", resource("block/table_se_wall"), builder -> {});
        Identifier table = colorizerModel(blockModels, "block/colorizer_table", resource("block/table"), builder -> {});

        blockModels.blockStateOutput.accept(forEachState(b, state -> {
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
                return variant(counterModel, face.ordinal() * 90, ((int) facing.toYRot() + 180) % 360, true);
            } else if (numConnections == 2) {
                boolean oppositeEnds = (north && south) || (west && east) || (up && down);

                if (oppositeEnds) {
                    return variant(counterModel, face.ordinal() * 90, ((int) facing.toYRot() + 180) % 360, true);
                }

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

                    return variant(tableSEWall, rotX, rotY, true);
                }

                return variant(tableSE, (face.ordinal() * 90) + rotX, rotY, true);
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
                        return variant(tableNWall, rotX, rotY, true);
                    }
                }

                return variant(tableN, (face.ordinal() * 90) + rotX, rotY, true);
            }

            return variant(table, face.ordinal() * 90, ((int) facing.toYRot() + 180) % 360, true);
        }, ColorizerTableBlock.WATERLOGGED));

        colorizerItem(blockModels, b, table);
    }

    private void colorizerFence(BlockModelGenerators blockModels) {
        Block b = DecorBlocks.COLORIZER_FENCE.get();
        MultiVariant post = BlockModelGenerators.plainVariant(colorizerModel(blockModels, "block/colorizer_fence_post", resource("block/fence_post"), builder -> {}));
        MultiVariant side = BlockModelGenerators.plainVariant(colorizerModel(blockModels, "block/colorizer_fence_side", resource("block/fence_side"), builder -> {}));

        blockModels.blockStateOutput.accept(BlockModelGenerators.createFence(b, post, side));

        colorizerItem(blockModels, b, colorizerModel(blockModels, "item/colorizer_fence", resource("item/fence_inventory"), builder -> {}));
    }

    private void colorizerFenceGate(BlockModelGenerators blockModels) {
        Block b = DecorBlocks.COLORIZER_FENCE_GATE.get();
        Identifier closed = colorizerModel(blockModels, "block/colorizer_fence_gate", resource("block/fence_gate"), builder -> {});
        Identifier open = colorizerModel(blockModels, "block/colorizer_fence_gate_open", resource("block/fence_gate_open"), builder -> {});
        Identifier closedWall = colorizerModel(blockModels, "block/colorizer_fence_gate_wall", resource("block/fence_gate_wall"), builder -> {});
        Identifier openWall = colorizerModel(blockModels, "block/colorizer_fence_gate_wall_open", resource("block/fence_gate_wall_open"), builder -> {});

        blockModels.blockStateOutput.accept(BlockModelGenerators.createFenceGate(b,
                BlockModelGenerators.plainVariant(open), BlockModelGenerators.plainVariant(closed),
                BlockModelGenerators.plainVariant(openWall), BlockModelGenerators.plainVariant(closedWall), true));

        colorizerItem(blockModels, b, closed);
    }

    private void colorizerWall(BlockModelGenerators blockModels) {
        Block b = DecorBlocks.COLORIZER_WALL.get();
        MultiVariant post = BlockModelGenerators.plainVariant(colorizerModel(blockModels, "block/colorizer_wall_post", resource("block/wall_post"), builder -> {}));
        MultiVariant lowSide = BlockModelGenerators.plainVariant(colorizerModel(blockModels, "block/colorizer_wall_side", resource("block/wall_side"), builder -> {}));
        MultiVariant tallSide = BlockModelGenerators.plainVariant(colorizerModel(blockModels, "block/colorizer_wall_side_tall", resource("block/wall_side_tall"), builder -> {}));

        blockModels.blockStateOutput.accept(BlockModelGenerators.createWall(b, post, lowSide, tallSide));

        colorizerItem(blockModels, b, colorizerModel(blockModels, "item/colorizer_wall", resource("item/wall_inventory"), builder -> {}));
    }

    private void colorizerTrapDoor(BlockModelGenerators blockModels) {
        Block b = DecorBlocks.COLORIZER_TRAP_DOOR.get();
        Identifier bottom = colorizerModel(blockModels, "block/colorizer_trapdoor_bottom", resource("block/trapdoor_bottom"), builder -> {});
        Identifier top = colorizerModel(blockModels, "block/colorizer_trapdoor_top", resource("block/trapdoor_top"), builder -> {});
        Identifier open = colorizerModel(blockModels, "block/colorizer_trapdoor_open", resource("block/trapdoor_open"), builder -> {});

        blockModels.blockStateOutput.accept(BlockModelGenerators.createOrientableTrapdoor(b,
                BlockModelGenerators.plainVariant(top), BlockModelGenerators.plainVariant(bottom), BlockModelGenerators.plainVariant(open)));

        colorizerItem(blockModels, b, bottom);
    }

    private void colorizerDoor(BlockModelGenerators blockModels) {
        Block b = DecorBlocks.COLORIZER_DOOR.get();
        MultiVariant bottomLeft = colorizerDoorPart(blockModels, "door_bottom_left");
        MultiVariant bottomLeftOpen = colorizerDoorPart(blockModels, "door_bottom_left_open");
        MultiVariant bottomRight = colorizerDoorPart(blockModels, "door_bottom_right");
        MultiVariant bottomRightOpen = colorizerDoorPart(blockModels, "door_bottom_right_open");
        MultiVariant topLeft = colorizerDoorPart(blockModels, "door_top_left");
        MultiVariant topLeftOpen = colorizerDoorPart(blockModels, "door_top_left_open");
        MultiVariant topRight = colorizerDoorPart(blockModels, "door_top_right");
        MultiVariant topRightOpen = colorizerDoorPart(blockModels, "door_top_right_open");

        blockModels.blockStateOutput.accept(BlockModelGenerators.createDoor(b,
                bottomLeft, bottomLeftOpen, bottomRight, bottomRightOpen, topLeft, topLeftOpen, topRight, topRightOpen));

        colorizerItem(blockModels, b, colorizerModel(blockModels, "item/colorizer_door", resource("item/door"), builder -> {}));
    }

    private MultiVariant colorizerDoorPart(BlockModelGenerators blockModels, String part) {
        return BlockModelGenerators.plainVariant(colorizerModel(blockModels, "block/colorizer_" + part, resource("block/" + part), builder -> {}));
    }

    private void colorizerStairs(BlockModelGenerators blockModels) {
        Block b = DecorBlocks.COLORIZER_STAIRS.get();
        Identifier straight = colorizerModel(blockModels, "block/colorizer_stairs", resource("block/stairs"), builder -> {});
        Identifier inner = colorizerModel(blockModels, "block/colorizer_inner_stairs", resource("block/inner_stairs"), builder -> {});
        Identifier outer = colorizerModel(blockModels, "block/colorizer_outer_stairs", resource("block/outer_stairs"), builder -> {});

        blockModels.blockStateOutput.accept(BlockModelGenerators.createStairs(b,
                BlockModelGenerators.plainVariant(inner), BlockModelGenerators.plainVariant(straight), BlockModelGenerators.plainVariant(outer)));

        colorizerItem(blockModels, b, straight);
    }

    private void colorizerSlab(BlockModelGenerators blockModels) {
        Block b = DecorBlocks.COLORIZER_SLAB.get();
        Identifier bottom = colorizerModel(blockModels, "block/colorizer_slab", resource("block/slab"), builder -> {});
        Identifier top = colorizerModel(blockModels, "block/colorizer_slab_top", resource("block/slab_top"), builder -> {});

        blockModels.blockStateOutput.accept(BlockModelGenerators.createSlab(b,
                BlockModelGenerators.plainVariant(bottom), BlockModelGenerators.plainVariant(top),
                BlockModelGenerators.plainVariant(resource("block/colorizer"))));

        colorizerItem(blockModels, b, bottom);
    }

    /**
     * The vertical slab. In 1.20.1 the four facings were four identical model files that differed only
     * in the Y rotation the blockstate applied to them; one model plus the rotation says the same
     * thing, so only {@code block/colorizer_vertical_slab} is generated now.
     */
    private void colorizerVerticalSlab(BlockModelGenerators blockModels) {
        Block b = DecorBlocks.COLORIZER_VERTICAL_SLAB.get();
        Identifier slab = colorizerModel(blockModels, "block/colorizer_vertical_slab", resource("block/vertical_slab"), builder -> {});
        MultiVariant north = BlockModelGenerators.plainVariant(slab);

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(ColorizerVerticalSlabBlock.TYPE).generate(type -> switch (type) {
                    case NORTH -> north;
                    case SOUTH -> north.with(BlockModelGenerators.Y_ROT_180);
                    case WEST -> north.with(BlockModelGenerators.Y_ROT_270);
                    case EAST -> north.with(BlockModelGenerators.Y_ROT_90);
                    case VerticalSlabType.DOUBLE -> BlockModelGenerators.plainVariant(resource("block/colorizer"));
                })));

        colorizerItem(blockModels, b, slab);
    }

    private void colorizerLampPost(BlockModelGenerators blockModels) {
        Block b = DecorBlocks.COLORIZER_LAMP_POST.get();
        Identifier bottom = colorizerModel(blockModels, "block/colorizer_lamp_post_bottom", resource("block/lamp_post_bottom"), builder -> {});
        Identifier middle = colorizerModel(blockModels, "block/colorizer_lamp_post_middle", resource("block/lamp_post_middle"), builder -> {});
        Identifier top = colorizerModel(blockModels, "block/colorizer_lamp_post_top", resource("block/lamp_post_top"),
                builder -> builder.addTexture("lamp", Identifier.withDefaultNamespace("block/glowstone")));

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(ColorizerLampPost.PART).generate(part -> BlockModelGenerators.plainVariant(switch (part) {
                    case LampPart.BOTTOM -> bottom;
                    case LampPart.MIDDLE -> middle;
                    case LampPart.TOP -> top;
                }))));

        colorizerItem(blockModels, b, colorizerModel(blockModels, "item/colorizer_lamp_post", resource("item/lamp_post_inventory"),
                builder -> builder.addTexture("lamp", Identifier.withDefaultNamespace("block/glowstone"))));
    }

    private void colorizerFireplace(BlockModelGenerators blockModels) {
        Block b = DecorBlocks.COLORIZER_FIREPLACE.get();
        MultiVariant plain = fireplacePart(blockModels, "colorizer_fireplace", "fireplace");
        MultiVariant n = fireplacePart(blockModels, "colorizer_fireplace_n", "fireplace_n");
        MultiVariant ne = fireplacePart(blockModels, "colorizer_fireplace_ne", "fireplace_ne");
        MultiVariant ns = fireplacePart(blockModels, "colorizer_fireplace_ns", "fireplace_ns");
        MultiVariant fire = BlockModelGenerators.plainVariant(resource("block/fire"));

        MultiPartGenerator generator = MultiPartGenerator.multiPart(b)
                .with(fireplaceCondition(false, false, false, false), plain)

                .with(fireplaceCondition(true, false, false, false), n.with(BlockModelGenerators.UV_LOCK).with(BlockModelGenerators.Y_ROT_90))
                .with(fireplaceCondition(false, true, false, false), n.with(BlockModelGenerators.UV_LOCK).with(BlockModelGenerators.Y_ROT_270))
                .with(fireplaceCondition(false, false, true, false), n.with(BlockModelGenerators.UV_LOCK).with(BlockModelGenerators.Y_ROT_180))
                .with(fireplaceCondition(false, false, false, true), n.with(BlockModelGenerators.UV_LOCK))

                .with(fireplaceCondition(true, false, false, true), ne.with(BlockModelGenerators.UV_LOCK))
                .with(fireplaceCondition(false, true, true, false), ne.with(BlockModelGenerators.UV_LOCK).with(BlockModelGenerators.Y_ROT_180))
                .with(fireplaceCondition(false, true, false, true), ne.with(BlockModelGenerators.UV_LOCK).with(BlockModelGenerators.Y_ROT_270))
                .with(fireplaceCondition(true, false, true, false), ne.with(BlockModelGenerators.UV_LOCK).with(BlockModelGenerators.Y_ROT_90))

                .with(fireplaceCondition(true, true, false, false), ns.with(BlockModelGenerators.UV_LOCK))
                .with(fireplaceCondition(false, false, true, true), ns.with(BlockModelGenerators.UV_LOCK))
                .with(fireplaceCondition(false, true, true, true), ns.with(BlockModelGenerators.UV_LOCK))
                .with(fireplaceCondition(true, false, true, true), ns.with(BlockModelGenerators.UV_LOCK))
                .with(fireplaceCondition(true, true, false, true), ns.with(BlockModelGenerators.UV_LOCK))
                .with(fireplaceCondition(true, true, true, false), ns.with(BlockModelGenerators.UV_LOCK))
                .with(fireplaceCondition(true, true, true, true), ns.with(BlockModelGenerators.UV_LOCK))

                .with(BlockModelGenerators.condition().term(ColorizerFireplaceBaseBlock.ACTIVE, true), fire);

        blockModels.blockStateOutput.accept(generator);
        colorizerItem(blockModels, b, resource("block/colorizer_fireplace"));
    }

    private MultiVariant fireplacePart(BlockModelGenerators blockModels, String name, String parent) {
        return BlockModelGenerators.plainVariant(colorizerModel(blockModels, "block/" + name, resource("block/" + parent),
                builder -> builder.addTexture("wood", Identifier.withDefaultNamespace("block/oak_planks"))));
    }

    private static net.minecraft.client.data.models.blockstates.ConditionBuilder fireplaceCondition(boolean east, boolean west, boolean south, boolean north) {
        return BlockModelGenerators.condition()
                .term(ColorizerFireplaceBlock.EAST, east)
                .term(ColorizerFireplaceBlock.WEST, west)
                .term(ColorizerFireplaceBlock.SOUTH, south)
                .term(ColorizerFireplaceBlock.NORTH, north);
    }

    private void colorizerFirepit(BlockModelGenerators blockModels) {
        Identifier firepit = colorizerModel(blockModels, "block/colorizer_firepit", resource("block/firepit"),
                builder -> builder.addTexture("wood", Identifier.withDefaultNamespace("block/oak_planks")));
        Identifier covered = colorizerModel(blockModels, "block/colorizer_firepit_covered", resource("block/firepit_covered"),
                builder -> builder.addTexture("wood", Identifier.withDefaultNamespace("block/oak_planks")).addTexture("net", resource("block/net")));

        fireplaceBase(blockModels, DecorBlocks.COLORIZER_FIREPIT.get(), firepit, resource("block/fire_high"));
        fireplaceBase(blockModels, DecorBlocks.COLORIZER_FIREPIT_COVERED.get(), covered, resource("block/fire_high"));
    }

    private void colorizerFireringStove(BlockModelGenerators blockModels) {
        Identifier firering = colorizerModel(blockModels, "block/colorizer_firering", resource("block/firering"),
                builder -> builder.addTexture("wood", Identifier.withDefaultNamespace("block/oak_planks")));
        Identifier stove = colorizerModel(blockModels, "block/colorizer_stove", resource("block/stove"),
                builder -> builder.addTexture("wood", Identifier.withDefaultNamespace("block/oak_planks")).addTexture("net", resource("block/net")));

        fireplaceBase(blockModels, DecorBlocks.COLORIZER_FIRERING.get(), firering, resource("block/fire"));
        fireplaceBase(blockModels, DecorBlocks.COLORIZER_STOVE.get(), stove, resource("block/fire_high"));
    }

    /**
     * A fire pit, fire ring or stove: the body always draws, and the flame is a second part shown
     * while the block is active.
     */
    private void fireplaceBase(BlockModelGenerators blockModels, Block b, Identifier body, Identifier fire) {
        blockModels.blockStateOutput.accept(MultiPartGenerator.multiPart(b)
                .with(BlockModelGenerators.plainVariant(body))
                .with(BlockModelGenerators.condition().term(ColorizerFireplaceBaseBlock.ACTIVE, true), BlockModelGenerators.plainVariant(fire)));

        colorizerItem(blockModels, b, body);
    }

    // ------------------------------------------------------------------ colorizer plumbing

    /**
     * The tint source is what replaced the deleted {@code registerItemColor} handler: an item's tints
     * are a list in its item model json now, and a source's position in that list is the tint index it
     * answers for. Index 0 is the index every colorizer shape stamps on its faces.
     */
    private void colorizerItem(BlockModelGenerators blockModels, Block b, Identifier model) {
        blockModels.registerSimpleTintedItemModel(b, model, new ColorizerItemTintSource());
    }

    private Identifier colorizerModel(BlockModelGenerators blockModels, String path, Identifier parent, Consumer<ColorizerModelBuilder> extra) {
        return colorizerModel(blockModels, path, parent, extra, Function.identity());
    }

    private Identifier colorizerModel(BlockModelGenerators blockModels, String path, Identifier parent, Consumer<ColorizerModelBuilder> extra,
                                      Function<ExtendedModelTemplateBuilder, ExtendedModelTemplateBuilder> perspective) {
        return perspective.apply(colorizerBuilder(parent, extra)).build().create(resource(path), colorizerParticle(), blockModels.modelOutput);
    }

    private Identifier colorizerObjModel(BlockModelGenerators blockModels, String path, Identifier objModel) {
        ExtendedModelTemplateBuilder builder = ExtendedModelTemplateBuilder.builder()
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .customLoader(ColorizerObjModelBuilder::begin, loader -> loader.objModel(objModel));

        return defaultPerspectiveFlipped(builder).build().create(resource(path), colorizerParticle(), blockModels.modelOutput);
    }

    private static ExtendedModelTemplateBuilder colorizerBuilder(Identifier parent, Consumer<ColorizerModelBuilder> extra) {
        return ExtendedModelTemplateBuilder.builder()
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .customLoader(ColorizerModelBuilder::begin, loader -> extra.accept(loader.colorizer(parent)));
    }

    /**
     * Builds the colorizer loader template {@link DecorItemModelProvider} needs for the brush, which is
     * the one colorizer model that belongs to an item rather than a block.
     */
    static ModelTemplate colorizerTemplate(Identifier parent, Consumer<ColorizerModelBuilder> extra) {
        return colorizerBuilder(parent, extra).build();
    }

    static TextureMapping colorizerParticle() {
        return new TextureMapping().put(TextureSlot.PARTICLE, COLORIZER_PARTICLE);
    }

    /**
     * The block sized item perspectives the 1.20.1 provider stamped on {@code tinted_cube} and on the
     * counter. They were an inline {@code transforms()} block on the old builder and are template
     * transforms now; the values are copied across unchanged.
     */
    private static ExtendedModelTemplateBuilder defaultPerspective(ExtendedModelTemplateBuilder builder) {
        return perspectives(builder, 225.0F);
    }

    /**
     * The same perspectives with the GUI view turned the other way, which is what every OBJ colorizer
     * used.
     */
    private static ExtendedModelTemplateBuilder defaultPerspectiveFlipped(ExtendedModelTemplateBuilder builder) {
        return perspectives(builder, 30.0F);
    }

    private static ExtendedModelTemplateBuilder perspectives(ExtendedModelTemplateBuilder builder, float guiYRot) {
        return builder
                .transform(ItemDisplayContext.GUI, t -> t.rotation(30.0F, guiYRot, 0.0F).translation(0.0F, 0.0F, 0.0F).scale(0.625F))
                .transform(ItemDisplayContext.GROUND, t -> t.rotation(0.0F, 0.0F, 0.0F).translation(0.0F, 3.0F, 0.0F).scale(0.25F))
                .transform(ItemDisplayContext.FIXED, t -> t.rotation(0.0F, 0.0F, 0.0F).translation(0.0F, 0.0F, 0.0F).scale(0.5F))
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, t -> t.rotation(75.0F, 45.0F, 0.0F).translation(0.0F, 2.5F, 0.0F).scale(0.375F))
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, t -> t.rotation(0.0F, 45.0F, 0.0F).translation(0.0F, 0.0F, 0.0F).scale(0.4F))
                .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, t -> t.rotation(0.0F, 225.0F, 0.0F).translation(0.0F, 0.0F, 0.0F).scale(0.4F));
    }

    // ------------------------------------------------------------------ helpers

    private static int countConnections(boolean... connections) {
        int numTrue = 0;
        for (boolean connection : connections) {
            numTrue += connection ? 1 : 0;
        }
        return numTrue;
    }

    private static int sideYRot(AttachFace face, Direction facing) {
        return (((int) facing.toYRot() + 180) + (face == AttachFace.CEILING ? 180 : 0)) % 360;
    }

    /**
     * A torch style facing: the vertical facings draw the standing model (flipped for {@code DOWN}) and
     * the horizontal ones draw the wall model turned to face outwards.
     */
    private static MultiVariant orientTorch(Identifier standing, Identifier wall, Direction dir) {
        if (dir == Direction.DOWN) {
            return BlockModelGenerators.plainVariant(standing).with(BlockModelGenerators.X_ROT_180);
        }

        if (dir == Direction.UP) {
            return BlockModelGenerators.plainVariant(standing);
        }

        return BlockModelGenerators.plainVariant(wall).with(yRot(((int) dir.toYRot() + 90) % 360));
    }

    /**
     * A model with an explicit rotation, the shape {@code ConfiguredModel.builder().rotationX(..)
     * .rotationY(..).uvLock(..)} used to take. Angles are reduced modulo 360 because a
     * {@link com.mojang.math.Quadrant} only has four values.
     */
    private static MultiVariant variant(Identifier model, int xRot, int yRot, boolean uvLock) {
        MultiVariant variant = BlockModelGenerators.plainVariant(model).with(xRot(xRot)).with(yRot(yRot));
        return uvLock ? variant.with(BlockModelGenerators.UV_LOCK) : variant;
    }

    private static VariantMutator xRot(int degrees) {
        return switch (Math.floorMod(degrees, 360) / 90) {
            case 1 -> BlockModelGenerators.X_ROT_90;
            case 2 -> BlockModelGenerators.X_ROT_180;
            case 3 -> BlockModelGenerators.X_ROT_270;
            default -> BlockModelGenerators.NOP;
        };
    }

    private static VariantMutator yRot(int degrees) {
        return switch (Math.floorMod(degrees, 360) / 90) {
            case 1 -> BlockModelGenerators.Y_ROT_90;
            case 2 -> BlockModelGenerators.Y_ROT_180;
            case 3 -> BlockModelGenerators.Y_ROT_270;
            default -> BlockModelGenerators.NOP;
        };
    }

    /**
     * Forge's {@code forAllStatesExcept}: one variant per block state, keyed by every property except
     * the ignored ones. {@link PropertyDispatch} carries at most five properties, and the table needs
     * eight, so this is the only shape that can express it.
     */
    private static BlockModelDefinitionGenerator forEachState(Block block, Function<BlockState, MultiVariant> factory, Property<?>... ignored) {
        List<Property<?>> ignoredList = List.of(ignored);
        return new BlockModelDefinitionGenerator() {
            @Override
            public Block block() {
                return block;
            }

            @Override
            public BlockStateModelDispatcher create() {
                Map<String, BlockStateModel.Unbaked> variants = new HashMap<>();
                for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                    PropertyValueList key = PropertyValueList.EMPTY;
                    for (Property<?> property : state.getProperties()) {
                        if (!ignoredList.contains(property)) {
                            key = key.extend(property.value(state));
                        }
                    }
                    variants.putIfAbsent(key.getKey(), factory.apply(state).toUnbaked());
                }
                return new BlockStateModelDispatcher(Optional.of(new BlockStateModelDispatcher.SimpleModelSelectors(variants)), Optional.empty());
            }
        };
    }

    private static String name(Block b) {
        return BuiltInRegistries.BLOCK.getKey(b).getPath();
    }

    private static Identifier resource(String path) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, path);
    }

    private static Material texture(String path) {
        return new Material(resource(path));
    }
}
