package com.grim3212.assorted.decor.client.model;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.common.properties.DecorModelProperties;
import com.grim3212.assorted.lib.client.model.baked.IDataAwareBakedModel;
import com.grim3212.assorted.lib.client.model.data.IBlockModelData;
import com.grim3212.assorted.lib.client.model.data.IModelDataBuilder;
import com.grim3212.assorted.lib.platform.ClientServices;
import com.grim3212.assorted.lib.util.NBTHelper;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4fc;

import java.util.ArrayList;
import java.util.List;

/**
 * The item model of a colorizer: the shape it was crafted as, drawn with the texture of the block the
 * stack has absorbed.
 * <p>
 * 1.20.1 did this with a {@code ColorizerItemOverrideList} hanging off the baked model, which read the
 * {@code stored_state} tag in {@code resolve} and returned a different {@code BakedModel}.
 * {@code ItemOverrides} is gone, but the replacement can still be driven per stack: an
 * {@link ItemModel} is a codec-registered type whose {@link #update} is handed the {@link ItemStack},
 * so the stored state is read there and the matching quads are pushed into the render state. What is
 * <em>not</em> possible is baking on demand from {@code update}, so the colorizer's own lazily baked
 * cache is reached through {@link IDataAwareBakedModel}, exactly as the block side reaches it, by
 * handing it model data carrying the stored state.
 * <p>
 * The stock {@code minecraft:model} type cannot do this: it bakes one {@code QuadCollection} at load
 * time (see {@link CuboidItemModelWrapper}), which for a colorizer is its empty state.
 */
public class ColorizerItemModel implements ItemModel {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "colorizer");

    /**
     * An item is drawn off no position, so its random source only has to be stable.
     */
    private static final long ITEM_SEED = 42L;

    private final BlockStateModel model;
    private final ModelRenderProperties properties;
    private final List<ItemTintSource> tints;

    private ColorizerItemModel(BlockStateModel model, ModelRenderProperties properties, List<ItemTintSource> tints) {
        this.model = model;
        this.properties = properties;
        this.tints = tints;
    }

    @Override
    public void update(ItemStackRenderState output, ItemStack item, ItemModelResolver resolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
        BlockState stored = storedState(item);

        output.appendModelIdentityElement(this);
        // Two colorizer stacks holding different blocks draw differently, so the stored state is part
        // of the identity the render state is cached under.
        output.appendModelIdentityElement(stored);

        ItemStackRenderState.LayerRenderState layer = output.newLayer();

        if (item.hasFoil()) {
            layer.setFoilType(ItemStackRenderState.FoilType.STANDARD);
            output.setAnimated();
            output.appendModelIdentityElement(ItemStackRenderState.FoilType.STANDARD);
        }

        if (!this.tints.isEmpty()) {
            var tintLayers = layer.tintLayers();
            for (ItemTintSource tint : this.tints) {
                int color = tint.calculate(item, level, owner == null ? null : owner.asLivingEntity());
                tintLayers.add(color);
                output.appendModelIdentityElement(color);
            }
        }

        List<BakedQuad> quads = layer.prepareQuadList();
        int materialFlags = collectQuads(this.model, stored, quads);

        // A render state is reused between frames unless it says it is animated, so a colorizer
        // holding an animated block - a stored magma block, prismarine, a command block - would
        // otherwise freeze on whichever frame of the sprite it was first drawn with, while the
        // placed block and the one in hand kept ticking. Which stored blocks are animated is only
        // known once the quads are collected, so this is asked per stack rather than at bake.
        if ((materialFlags & BakedQuad.FLAG_ANIMATED) != 0) {
            output.setAnimated();
        }

        layer.setExtents(() -> CuboidItemModelWrapper.computeExtents(quads));
        this.properties.applyToLayer(layer, displayContext);
    }

    /**
     * @return the union of the collected parts' {@linkplain BakedQuad.MaterialFlags material flags}.
     */
    private static int collectQuads(BlockStateModel model, BlockState stored, List<BakedQuad> output) {
        List<BlockStateModelPart> parts = new ArrayList<>();
        RandomSource random = RandomSource.create(ITEM_SEED);

        if (model instanceof IDataAwareBakedModel dataAware) {
            dataAware.collectParts(random, IModelDataBuilder.create().withInitial(DecorModelProperties.BLOCK_STATE, stored).build(), parts);
        } else {
            collectPlainParts(model, random, parts);
        }

        int materialFlags = 0;
        for (BlockStateModelPart part : parts) {
            materialFlags |= part.materialFlags();
            output.addAll(part.getQuads(null));
            for (Direction direction : Direction.values()) {
                output.addAll(part.getQuads(direction));
            }
        }

        return materialFlags;
    }

    // Deprecated by NeoForge in favour of a level/pos aware overload that only exists in its patched
    // jar. An item has neither, so this is the only overload that applies.
    @SuppressWarnings("deprecation")
    private static void collectPlainParts(BlockStateModel model, RandomSource random, List<BlockStateModelPart> parts) {
        model.collectParts(random, parts);
    }

    private static BlockState storedState(ItemStack stack) {
        if (!NBTHelper.hasTag(stack, "stored_state")) {
            return Blocks.AIR.defaultBlockState();
        }

        return NbtUtils.readBlockState(BuiltInRegistries.BLOCK, NBTHelper.getTag(stack, "stored_state"));
    }

    /**
     * @param model The colorizer <em>block</em> model to draw - the json carrying the
     *              {@code assorteddecor:colorizer} loader, which is the same model the blockstate
     *              points at.
     * @param tints Item tint sources, as on a vanilla {@code minecraft:model}. A colorizer wants
     *              {@code assorteddecor:colorizer} here so that a stored block which is itself tinted
     *              - grass, leaves - comes out the right colour.
     */
    public record Unbaked(Identifier model, List<ItemTintSource> tints) implements ItemModel.Unbaked {

        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Identifier.CODEC.fieldOf("model").forGetter(Unbaked::model),
                ItemTintSources.CODEC.listOf().optionalFieldOf("tints", List.of()).forGetter(Unbaked::tints)
        ).apply(instance, Unbaked::new));

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public void resolveDependencies(ResolvableModel.Resolver resolver) {
            resolver.markDependency(this.model);
        }

        @Override
        public ItemModel bake(ItemModel.BakingContext context, Matrix4fc transformation) {
            ModelBaker baker = context.blockModelBaker();

            // The same entry point the blockstate side uses, so a colorizer item and the placed block
            // share one baked model and one cache of stored-state variants.
            BlockStateModel baked = ClientServices.CLIENT.bakeSpecificationModel(baker, this.model, BlockModelRotation.IDENTITY);

            // Transforms, gui light and the particle come off the resolved model, which walks up into
            // the shape template the colorizer json names as its parent. That is where the display
            // block making a colorizer sit correctly in the inventory actually lives.
            ResolvedModel resolved = baker.getModel(this.model);
            ModelRenderProperties properties = ModelRenderProperties.fromResolvedModel(baker, resolved, resolved.getTopTextureSlots());

            return new ColorizerItemModel(baked, properties, this.tints);
        }
    }
}
