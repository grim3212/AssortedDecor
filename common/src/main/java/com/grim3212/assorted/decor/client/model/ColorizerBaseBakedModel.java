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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * TODO(26.2): reached through a model json loader, this class collapses to its unseeded output.
 *  What it used to do: a custom model loader returned a whole {@code BakedModel}, so the colorizer
 *  could pick a different set of quads per draw from the block entity's model data.
 *  Why it is at risk: {@code UnbakedGeometry#bake} has to return a {@code QuadCollection}, so
 *  AssortedLib's {@code ForgeModelGeometryToSpecificationPlatformDelegator} (and the Fabric
 *  equivalent) flatten whatever a specification bakes into a single quad collection at bake time,
 *  with empty model data - which for a colorizer means the "no stored block" texture, everywhere.
 *  The per position behaviour only survives if the baked model reaches the blockstate layer intact,
 *  which in 26.2 means a {@code CustomUnbakedBlockStateModel} registered from the blockstate json
 *  (NeoForge's {@code RegisterBlockStateModels} / Fabric's own registry) rather than a model json
 *  loader. AssortedLib's {@code ForgeBakedModelDelegate} / {@code FabricBakedModelDelegate} already
 *  route {@link #collectParts(RandomSource, IBlockModelData, List)} correctly once the model gets
 *  there, so what is missing is the blockstate side entry point, in the library and in the mod's
 *  generated blockstate json - not this class.
 *  <p>
 *  Note also that the {@link ModelBaker} is held past baking, as it was in 1.20.1, because a stored
 *  block state is only known at render time and there is no bounded set of them to bake eagerly. On
 *  the flattening path above that never matters - the model is collected from immediately after it is
 *  baked - but a blockstate side wrapper would bake children long after the model manager has moved
 *  on, and that is the thing to check first if the colorizers misbehave once one exists.
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

    protected final Map<BlockState, BlockStateModel> cache = new HashMap<>();
    protected BlockStateModel EMPTY;

    public BlockStateModel getCachedModel(BlockState blockState) {
        if (blockState == null || blockState == Blocks.AIR.defaultBlockState()) {
            if (EMPTY == null) {
                EMPTY = generateModel(textures(DEFAULT_TEXTURE));
            }
            return EMPTY;
        }

        if (!this.cache.containsKey(blockState)) {
            String texture;
            if (blockState.getBlock() == Blocks.GRASS_BLOCK) {
                texture = "minecraft:block/grass_block_top";
            } else if (blockState.getBlock() == Blocks.PODZOL) {
                texture = "minecraft:block/dirt_podzol_top";
            } else if (blockState.getBlock() == Blocks.MYCELIUM) {
                texture = "minecraft:block/mycelium_top";
            } else {
                // BlockModelShaper is gone; the particle sprite of a block state is answered by the
                // baked block state models the ModelManager holds.
                texture = Minecraft.getInstance().getModelManager().getBlockStateModelSet().getParticleMaterial(blockState).sprite().contents().name().toString();
            }

            this.cache.put(blockState, generateModel(textures(texture)));
        }

        return this.cache.get(blockState);
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
        BlockState blockState = Blocks.AIR.defaultBlockState();
        if (extraData.hasProperty(DecorModelProperties.BLOCK_STATE)) {
            blockState = extraData.getData(DecorModelProperties.BLOCK_STATE);
        }

        collectCachedParts(this.getCachedModel(blockState), random, output);
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
