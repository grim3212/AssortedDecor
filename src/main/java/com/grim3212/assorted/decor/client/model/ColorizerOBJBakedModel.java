package com.grim3212.assorted.decor.client.model;

import java.util.function.Function;

import com.google.common.collect.ImmutableMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;

public class ColorizerOBJBakedModel extends ColorizerBaseBakedModel {

	private final OBJModelCopy objModel;

	public ColorizerOBJBakedModel(IBakedModel bakedColorizer, OBJModelCopy objModel, IModelConfiguration owner, TextureAtlasSprite baseSprite, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation name) {
		super(bakedColorizer, owner, baseSprite, bakery, spriteGetter, transform, overrides, name);
		this.objModel = objModel;
	}

	@Override
	protected IBakedModel generateModel(ImmutableMap<String, String> textures) {
		TextureAtlasSprite texture = Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(new ResourceLocation(textures.get("#stored")));
		return this.objModel.setTexture(texture).bake(this.owner, this.bakery, this.spriteGetter, this.transform, this.overrides, this.name);
	}
}
