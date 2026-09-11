package com.grim3212.assorted.decor.client.model;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.common.properties.DecorModelProperties;
import com.grim3212.assorted.lib.client.model.data.IBlockModelData;
import com.grim3212.assorted.lib.client.model.data.IModelDataBuilder;
import com.grim3212.assorted.lib.client.model.item.DataAwareItemModel;
import com.grim3212.assorted.lib.util.NBTHelper;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4fc;

import java.util.List;

/**
 * The item model of a colorizer: the shape it was crafted as, drawn with the texture of the block the
 * stack has absorbed.
 * <p>
 * 1.20.1 did this with a {@code ColorizerItemOverrideList} hanging off the baked model, which read the
 * {@code stored_state} tag in {@code resolve} and returned a different {@code BakedModel}.
 * {@code ItemOverrides} is gone; this registers the {@code assorteddecor:colorizer} item model type,
 * which reads the stack's stored state and draws through AssortedLib's {@link DataAwareItemModel} -
 * the same model AssortedTech's bridge item uses.
 */
public final class ColorizerItemModel {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "colorizer");

    private ColorizerItemModel() {
    }

    private static BlockState storedState(ItemStack stack) {
        if (!NBTHelper.hasTag(stack, "stored_state")) {
            return Blocks.AIR.defaultBlockState();
        }

        return NbtUtils.readBlockState(BuiltInRegistries.BLOCK, NBTHelper.getTag(stack, "stored_state"));
    }

    private static IBlockModelData modelData(ItemStack stack) {
        return IModelDataBuilder.create().withInitial(DecorModelProperties.BLOCK_STATE, storedState(stack)).build();
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
            return DataAwareItemModel.bake(context, this.model, this.tints, ColorizerItemModel::modelData, ColorizerItemModel::storedState);
        }
    }
}
