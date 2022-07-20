package com.grim3212.assorted.decor.client.model;

import java.util.function.Function;

import com.google.common.collect.ImmutableMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;

public class ColorizerObjBakedModel extends ColorizerBaseBakedModel {

	private final ObjModelCopy objModel;

	public ColorizerObjBakedModel(BakedModel bakedColorizer, ObjModelCopy objModel, IGeometryBakingContext owner, TextureAtlasSprite baseSprite, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation name) {
		super(bakedColorizer, owner, baseSprite, bakery, spriteGetter, transform, overrides, name);
		this.objModel = objModel;
	}

	@Override
	protected BakedModel generateModel(ImmutableMap<String, String> textures) {
		TextureAtlasSprite texture = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(new ResourceLocation(textures.get("#stored")));
		return this.objModel.setTexture(texture).bake(this.owner, this.bakery, this.spriteGetter, this.transform, this.overrides, this.name);
	}
}
