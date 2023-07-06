package com.grim3212.assorted.decor.client.model;

import com.google.common.collect.ImmutableMap;
import com.grim3212.assorted.lib.client.model.ItemOverridesExtension;
import com.grim3212.assorted.lib.client.model.RetexturableBlockModel;
import com.grim3212.assorted.lib.client.model.baked.IDelegatingBakedModel;
import com.grim3212.assorted.lib.client.model.loaders.context.IModelBakingContext;
import com.grim3212.assorted.lib.util.NBTHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ColorizerBakedModel extends ColorizerBaseBakedModel<BlockModel> {
    public ColorizerBakedModel(IModelBakingContext context, BlockModel unbakedColorizer, ModelBaker bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ResourceLocation name) {
        super(context, unbakedColorizer, bakery, spriteGetter, transform, name);
    }

    @Override
    protected BakedModel generateModel(ImmutableMap<String, String> texture) {
        RetexturableBlockModel toBake = RetexturableBlockModel.from(this.model, this.name);
        return toBake.retexture(texture).bake(this.bakery, toBake, this.spriteGetter, this.transform, this.name, true);
    }

    @Override
    protected ItemOverridesExtension createItemOverrides(IModelBakingContext context) {
        return new ColorizerItemOverrideList(context);
    }

    public static final class ColorizerItemOverrideList extends ItemOverridesExtension {

        public ColorizerItemOverrideList(IModelBakingContext context) {
            super(context);
        }

        @Override
        public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int field) {
            BakedModel returnModel = originalModel;
            if (originalModel instanceof IDelegatingBakedModel delegate) {
                returnModel = delegate.getDelegate();
            }

            if (returnModel instanceof ColorizerBaseBakedModel colorizerModel) {
                if (stack.hasTag() && stack.getTag().contains("stored_state")) {
                    return colorizerModel.getCachedModel(NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), NBTHelper.getTag(stack, "stored_state")));
                }

                return colorizerModel.getCachedModel(Blocks.AIR.defaultBlockState());
            }

            return returnModel;
        }
    }
}
