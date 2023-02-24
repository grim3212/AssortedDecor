package com.grim3212.assorted.decor.client.model;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.api.colorizer.IColorizer;
import com.grim3212.assorted.decor.foml.baked.ForwardingOBJBakedModel;
import com.grim3212.assorted.decor.foml.baked.OBJBakedModel;
import com.grim3212.assorted.decor.foml.baked.OBJUnbakedModel;
import com.grim3212.assorted.lib.util.NBTHelper;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;
import java.util.function.Supplier;

public class ColorizerObjBakedModel extends ForwardingOBJBakedModel {

    public ColorizerObjBakedModel(OBJUnbakedModel originalModel, ItemTransforms transforms, TextureAtlasSprite sprite, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state, ResourceLocation location) {
        super(originalModel, transforms, sprite, baker, spriteGetter, state, location);
    }

    @Override
    public ResourceLocation defaultTexture() {
        return new ResourceLocation(Constants.MOD_ID, "block/colorizer");
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        if (!(state.getBlock() instanceof IColorizer)) {
            context.fallbackConsumer().accept(Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getModelManager().getMissingModel());
            return;
        }

        Object data = ((RenderAttachedBlockView) blockView).getBlockEntityRenderAttachment(pos);
        if (data instanceof BlockState storedState) {

            if (storedState == null) {
                super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
            } else {
                OBJBakedModel newModel = this.getCachedModel(storedState);
                if (newModel.getMesh() != null) {
                    if (this.currentParticle != newModel.getParticleIcon()) {
                        this.currentParticle = newModel.getParticleIcon();
                    }

                    context.meshConsumer().accept(newModel.getMesh());
                }
            }
        }
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        if (stack.hasTag() && stack.getTag().contains("stored_state")) {
            BakedModel newModel = this.getCachedModel(NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), NBTHelper.getTag(stack, "stored_state")));
            ((FabricBakedModel) newModel).emitItemQuads(stack, randomSupplier, context);
        } else {
            ((FabricBakedModel) this.getCachedModel(Blocks.AIR.defaultBlockState())).emitItemQuads(stack, randomSupplier, context);
        }
    }
}
