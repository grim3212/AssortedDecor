package com.grim3212.assorted.decor.client.model;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;

public class DecorCompositeModel implements IBakedModel {

	private final ImmutableList<IBakedModel> models;

	public DecorCompositeModel(ImmutableList<IBakedModel> models) {
		this.models = models;
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
		ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

		for (IBakedModel model : models) {
			builder.addAll(model.getQuads(state, side, rand));
		}

		return builder.build();
	}

	@Override
	public boolean isAmbientOcclusion() {
		return models.get(0).isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return models.get(0).isGui3d();
	}

	@Override
	public boolean func_230044_c_() {
		return models.get(0).func_230044_c_();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return models.get(0).isBuiltInRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return models.get(0).getParticleTexture();
	}

	@Override
	public ItemOverrideList getOverrides() {
		return ItemOverrideList.EMPTY;
	}

}
