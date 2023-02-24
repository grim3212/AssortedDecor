package com.grim3212.assorted.decor.foml.baked;


import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class OBJBakedModel implements BakedModel, FabricBakedModel {

    protected final Mesh mesh;
    protected final ItemTransforms transformation;
    protected TextureAtlasSprite currentParticle;
    private List<BakedQuad>[] cachedQuads = null;

    protected final ModelBaker baker;
    protected final Function<Material, TextureAtlasSprite> spriteGetter;
    protected final ModelState state;
    protected final ResourceLocation location;

    public OBJBakedModel(Mesh mesh, ItemTransforms transformation, TextureAtlasSprite sprite, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state, ResourceLocation location) {
        this.mesh = mesh;
        this.transformation = transformation;
        this.currentParticle = sprite;
        this.baker = baker;
        this.spriteGetter = spriteGetter;
        this.state = state;
        this.location = location;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
        if (cachedQuads == null) cachedQuads = ModelHelper.toQuadLists(mesh);
        return cachedQuads[direction == null ? 6 : direction.get3DDataValue()];
    }

    public Mesh getMesh() {
        return mesh;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        if (mesh != null) {
            context.meshConsumer().accept(mesh);
        }
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        if (mesh != null) {
            context.meshConsumer().accept(mesh);
        }
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.currentParticle;
    }

    @Override
    public ItemTransforms getTransforms() {
        return transformation;
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

}
