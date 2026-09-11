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
 * The {@code assorteddecor:colorizer} item model type: the colorizer's shape drawn with the texture
 * of the block the stack has stored, through AssortedLib's {@link DataAwareItemModel}.
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
     * @param model the colorizer block model, the same json the blockstate points at
     * @param tints item tint sources; {@code assorteddecor:colorizer} tints a stored grass or leaf
     * block
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
