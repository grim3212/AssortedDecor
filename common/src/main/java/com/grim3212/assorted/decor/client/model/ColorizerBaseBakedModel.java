package com.grim3212.assorted.decor.client.model;

import com.google.common.collect.ImmutableMap;
import com.grim3212.assorted.decor.common.properties.DecorModelProperties;
import com.grim3212.assorted.lib.client.model.baked.IDataAwareBakedModel;
import com.grim3212.assorted.lib.client.model.data.IBlockModelData;
import com.grim3212.assorted.lib.client.model.loaders.context.IModelBakingContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A colorizer takes its geometry from a fixed shape and its texture from whatever block state has
 * been stored in the block entity, so one baked colorizer owns a cache of models keyed by that
 * stored state.
 * <p>
 * The base type is {@link IDataAwareBakedModel} - a {@code BlockStateModel} that additionally sees
 * the block entity's model data - because {@code BakedModel} is gone and the vanilla replacement,
 * {@link BlockStateModel#collectParts(RandomSource, List)}, receives no level, position or model
 * data. Everything the 1.20.1 version answered about how to <em>draw</em> the model went with it:
 * ambient occlusion, gui light and the particle sprite are properties of the individual
 * {@linkplain BlockStateModelPart parts} now, item transforms belong to the item pipeline, and a
 * model no longer picks a {@code RenderType} at all - the chunk layer is derived per quad from
 * {@code BakedQuad.MaterialInfo#layer()}.
 * <p>
 * <b>This model only survives if it is reached from the blockstate side.</b> A model json loader can
 * only contribute geometry - {@code UnbakedGeometry#bake} returns a {@code QuadCollection} - so
 * AssortedLib's {@code ForgeModelGeometryToSpecificationPlatformDelegator} and its Fabric equivalent
 * flatten whatever a specification bakes, once, against empty model data. For a colorizer that means
 * the "no stored block" texture everywhere. The blockstate json therefore names
 * {@code assortedlib:specification} instead of a plain variant, which bakes this model whole and
 * hands it to {@code ForgeBakedModelDelegate} / {@code FabricBakedModelDelegate}; those route
 * {@link #collectParts(RandomSource, IBlockModelData, List)} with the block entity's data. The item
 * side reaches the same instance through {@code ColorizerItemModel}, passing the stack's stored state
 * as model data.
 * <p>
 * The {@link ModelBaker} is deliberately held past baking, as it was in 1.20.1: a stored block state
 * is only known while rendering and there is no bounded set of them to bake eagerly. It stays usable
 * because the bakery's resolved models and atlas preparations live as long as the baked models do -
 * a resource reload rebuilds both together.
 */
public abstract class ColorizerBaseBakedModel<T> implements IDataAwareBakedModel {

    private static final String DEFAULT_TEXTURE = "assorteddecor:block/colorizer";

    protected final T model;
    protected final ModelBaker bakery;
    protected final ModelState transform;
    protected final Identifier name;
    protected final ModelDebugName debugName;
    protected final IModelBakingContext context;

    private final Material.Baked particle;

    public ColorizerBaseBakedModel(IModelBakingContext context, T model, ModelBaker bakery, ModelState transform, Identifier name) {
        this.context = context;
        this.model = model;
        this.bakery = bakery;
        this.transform = transform;
        this.name = name;
        this.debugName = name::toString;

        Material particleMaterial = context.getMaterial("particle").orElse(null);
        this.particle = particleMaterial != null ? bakery.materials().get(particleMaterial, this.debugName) : bakery.materials().reportMissingReference("particle", this.debugName);
    }

    /**
     * Concurrent because it is filled during rendering, not during baking: the stored states are only
     * known once chunks are being built, and section compilation runs on several threads at once.
     */
    protected final Map<BlockState, BlockStateModel> cache = new ConcurrentHashMap<>();
    protected final Map<BlockState, Material.Baked> particleCache = new ConcurrentHashMap<>();
    protected volatile BlockStateModel EMPTY;

    public BlockStateModel getCachedModel(BlockState blockState) {
        if (isEmpty(blockState)) {
            BlockStateModel empty = EMPTY;
            if (empty == null) {
                EMPTY = empty = generateModel(textures(DEFAULT_TEXTURE));
            }
            return empty;
        }

        return this.cache.computeIfAbsent(blockState, state -> generateModel(textures(storedTexture(state))));
    }

    // Resolved from the stored texture rather than read off the cached model: only the json colorizer
    // bakes it into a "particle" slot, the OBJ one pushes the sprite straight onto the geometry.
    public Material.Baked getCachedParticle(BlockState blockState) {
        if (isEmpty(blockState)) {
            return this.particle;
        }

        return this.particleCache.computeIfAbsent(blockState, state -> this.bakery.materials().get(new Material(Identifier.parse(storedTexture(state))), this.debugName));
    }

    private static boolean isEmpty(BlockState blockState) {
        return blockState == null || blockState == Blocks.AIR.defaultBlockState();
    }

    // Grass, podzol and mycelium are special cased: their particle sprite is the side texture, not the
    // top one that reads as the block's colour.
    private static String storedTexture(BlockState state) {
        if (state.getBlock() == Blocks.GRASS_BLOCK) {
            return "minecraft:block/grass_block_top";
        } else if (state.getBlock() == Blocks.PODZOL) {
            return "minecraft:block/dirt_podzol_top";
        } else if (state.getBlock() == Blocks.MYCELIUM) {
            return "minecraft:block/mycelium_top";
        }

        // BlockModelShaper is gone; the particle sprite is answered by the ModelManager's baked models.
        return Minecraft.getInstance().getModelManager().getBlockStateModelSet().getParticleMaterial(state).sprite().contents().name().toString();
    }

    /**
     * The texture slot overrides for a stored block texture. The slot is named {@code stored}, not
     * {@code #stored}: a leading {@code #} marks a <em>reference</em> to another slot and
     * {@link net.minecraft.client.resources.model.sprite.TextureSlots#getMaterial} strips it before
     * looking a slot up, so a slot declared as {@code #stored} would never be found.
     */
    private static ImmutableMap<String, String> textures(String texture) {
        return ImmutableMap.of("particle", texture, "stored", texture);
    }

    protected abstract BlockStateModel generateModel(ImmutableMap<String, String> texture);

    @Override
    public void collectParts(@NotNull RandomSource random, @NotNull IBlockModelData extraData, @NotNull List<BlockStateModelPart> output) {
        collectCachedParts(this.getCachedModel(storedState(extraData)), random, output);
    }

    // Break and hit particles read the sprite, not the geometry; without this they show the model
    // json's own particle slot - the unset colorizer texture - whatever the block entity has stored.
    @Override
    public Material.Baked particleMaterial(@NotNull IBlockModelData extraData) {
        return this.getCachedParticle(storedState(extraData));
    }

    private static BlockState storedState(IBlockModelData extraData) {
        if (extraData.hasProperty(DecorModelProperties.BLOCK_STATE)) {
            return extraData.getData(DecorModelProperties.BLOCK_STATE);
        }

        return Blocks.AIR.defaultBlockState();
    }

    // Deprecated by NeoForge in favour of a level/pos aware overload that only exists in its patched
    // jar; the cached models are plain vanilla BlockStateModels, so this is the only way to reach
    // their parts.
    @SuppressWarnings("deprecation")
    private static void collectCachedParts(BlockStateModel model, RandomSource random, List<BlockStateModelPart> output) {
        model.collectParts(random, output);
    }

    // Deprecated by NeoForge in favour of level/pos aware overloads that only exist in its patched
    // jar; vanilla still declares these abstract, so they have to be implemented here.
    @SuppressWarnings("deprecation")
    @Override
    public Material.Baked particleMaterial() {
        return this.particle;
    }

    /**
     * The union of the flags of every model baked so far, plus the empty one. A colorizer bakes its
     * children lazily as stored states are encountered, so there is no complete set to report; the
     * flags of a state that has not been seen yet cannot be known without baking it.
     */
    @SuppressWarnings("deprecation")
    @Override
    public @BakedQuad.MaterialFlags int materialFlags() {
        int flags = this.getCachedModel(Blocks.AIR.defaultBlockState()).materialFlags();
        for (BlockStateModel cached : this.cache.values()) {
            flags |= cached.materialFlags();
        }

        return flags;
    }
}
