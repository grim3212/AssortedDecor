package com.grim3212.assorted.decor.client.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Either;

import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverride;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;

public class RetexturableBlockModel extends BlockModel {
	private final Map<String, RenderMaterial> replacements = new HashMap<>();

	public static RetexturableBlockModel from(BlockModel parent) {
		RetexturableBlockModel model = new RetexturableBlockModel(parent.getParentLocation(), parent.getElements(), parent.textures, parent.ambientOcclusion, parent.getGuiLight(), parent.getAllTransforms(), parent.getOverrides());
		model.customData.copyFrom(parent.customData);
		return model;
	}

	public RetexturableBlockModel(ResourceLocation parentLocation, List<BlockPart> elements, Map<String, Either<RenderMaterial, String>> textures, boolean ambientOcclusion, GuiLight guiLight, ItemCameraTransforms cameraTransforms, List<ItemOverride> overrides) {
		super(parentLocation, elements, textures, ambientOcclusion, guiLight, cameraTransforms, overrides);
	}

	@Override
	public RenderMaterial resolveTextureName(String nameIn) {
		if (this.replacements.containsKey(nameIn)) {
			return this.replacements.get(nameIn);
		}

		return super.resolveTextureName(nameIn);
	}

	public void replaceTexture(String name, RenderMaterial texture) {
		this.replacements.put(name, texture);
	}

	public void replaceTexture(String name, ResourceLocation texture) {
		this.replacements.put(name, new RenderMaterial(PlayerContainer.LOCATION_BLOCKS_TEXTURE, texture));
	}

	public RetexturableBlockModel retexture(ImmutableMap<String, String> textures) {
		textures.forEach((name, texture) -> replaceTexture(name, new ResourceLocation(texture)));
		return this;
	}
}
