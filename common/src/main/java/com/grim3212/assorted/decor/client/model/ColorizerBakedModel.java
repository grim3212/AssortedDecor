package com.grim3212.assorted.decor.client.model;

import com.google.common.collect.ImmutableMap;
import com.grim3212.assorted.lib.client.model.loaders.context.IModelBakingContext;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;

/**
 * The json flavour of the colorizer: the {@code "colorizer"} object names a parent model, and every
 * stored block state re-bakes that parent with the stored block's texture in the {@code stored} slot.
 * <p>
 * There is no mutable json model object to copy and retexture any more - {@code BlockModel} is a
 * {@code CuboidModel} record whose textures are a {@link TextureSlots.Data}, and its element and face
 * deserializers are package private, so an inline geometry object could not be re-read here either.
 * The parent chain is walked directly instead, which is what vanilla model inheritance does: the
 * overrides go in child-first, the parent's own slots after them, and
 * {@link ResolvedModel#findTopGeometry} supplies the geometry to bake against the resolved slots.
 */
public class ColorizerBakedModel extends ColorizerBaseBakedModel<ColorizerUnbakedModel.Colorizer> {

    public ColorizerBakedModel(IModelBakingContext context, ColorizerUnbakedModel.Colorizer unbakedColorizer, ModelBaker bakery, ModelState transform, Identifier name) {
        super(context, unbakedColorizer, bakery, transform, name);
    }

    @Override
    protected BlockStateModel generateModel(ImmutableMap<String, String> textures) {
        ResolvedModel parent = this.bakery.getModel(this.model.parent());

        TextureSlots.Data.Builder overrides = new TextureSlots.Data.Builder();
        this.model.textures().forEach((slot, texture) -> overrides.addTexture(slot, new Material(Identifier.parse(texture))));
        textures.forEach((slot, texture) -> overrides.addTexture(slot, new Material(Identifier.parse(texture))));

        TextureSlots.Resolver resolver = new TextureSlots.Resolver();
        resolver.addLast(overrides.build());
        for (ResolvedModel current = parent; current != null; current = current.parent()) {
            resolver.addLast(current.wrapped().textureSlots());
        }

        TextureSlots slots = resolver.resolve(this.debugName);

        QuadCollection quads = ResolvedModel.findTopGeometry(parent).bake(slots, this.bakery, this.transform, this.debugName);
        boolean ambientOcclusion = ResolvedModel.findTopAmbientOcclusion(parent);
        Material.Baked particle = this.bakery.materials().resolveSlot(slots, "particle", this.debugName);

        return new SingleVariant(new SimpleModelWrapper(quads, ambientOcclusion, particle));
    }

    // The item override list that used to live here is now
    // com.grim3212.assorted.decor.client.model.ColorizerItemModel: ItemOverrides was deleted, and an
    // item's model is a codec registered ItemModel type whose update() is handed the ItemStack. It
    // reads the same "stored_state" tag and reaches this same cache, so an item and a placed block of
    // the same colorizer draw identically.
}
