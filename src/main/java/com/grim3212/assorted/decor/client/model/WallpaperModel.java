package com.grim3212.assorted.decor.client.model;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.grim3212.assorted.decor.AssortedDecor;
import com.mojang.datafixers.util.Pair;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ItemTextureQuadConverter;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.geometry.IModelGeometry;

public class WallpaperModel implements IBakedModel {

	private final List<ResourceLocation> SPRITES;

	public WallpaperModel() {
		Builder<ResourceLocation> builder = ImmutableList.<ResourceLocation>builder();
		for (int i = 0; i < 24; i++) {
			builder.add(new ResourceLocation(AssortedDecor.MODID, "block/wallpaper/wallpaper_" + i));
		}
		SPRITES = builder.build();
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
		Builder<BakedQuad> quads = ImmutableList.<BakedQuad>builder();

		int mainTex = 0;
		TextureAtlasSprite sprite = getSprite(mainTex);

		quads.add(ItemTextureQuadConverter.genQuad(TransformationMatrix.identity(), 0, 0, 16, 16, 0f, sprite, Direction.SOUTH, 0xffffff, 1));
		quads.add(ItemTextureQuadConverter.genQuad(TransformationMatrix.identity(), 0, 0, 16, 16, 0f, sprite, Direction.NORTH, 0xffffff, 1));

		quads.add(ItemTextureQuadConverter.genQuad(ModelRotation.X0_Y90.getRotation(), 0, 0, 16, 16, 0f, sprite, Direction.SOUTH, 0xffffff, 1));
		quads.add(ItemTextureQuadConverter.genQuad(ModelRotation.X0_Y90.getRotation(), 0, 0, 16, 16, 0f, sprite, Direction.NORTH, 0xffffff, 1));

		quads.add(ItemTextureQuadConverter.genQuad(ModelRotation.X0_Y90.getRotation(), 0, 0, 16, 16, -0.999f, sprite, Direction.SOUTH, 0xffffff, 1));
		quads.add(ItemTextureQuadConverter.genQuad(ModelRotation.X0_Y90.getRotation(), 0, 0, 16, 16, -0.999f, sprite, Direction.NORTH, 0xffffff, 1));

		quads.add(ItemTextureQuadConverter.genQuad(TransformationMatrix.identity(), 0, 0, 16, 16, 0.999f, sprite, Direction.SOUTH, 0xffffff, 1));
		quads.add(ItemTextureQuadConverter.genQuad(TransformationMatrix.identity(), 0, 0, 16, 16, 0.999f, sprite, Direction.NORTH, 0xffffff, 1));

		return quads.build();
	}

	public TextureAtlasSprite getSprite(int i) {
		return Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(SPRITES.get(i));
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
		return getQuads(state, side, rand, EmptyModelData.INSTANCE);
	}

	@Override
	public boolean isAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}

	@Override
	public boolean func_230044_c_() {
		return false;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture(IModelData data) {
		return IBakedModel.super.getParticleTexture(data);
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return getSprite(0);
	}

	@Override
	public ItemOverrideList getOverrides() {
		return ItemOverrideList.EMPTY;
	}

	public static class RawWallpaperModel implements IModelGeometry<RawWallpaperModel> {

		@Override
		public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
			return new WallpaperModel();
		}

		@Override
		public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
			return ImmutableList.of();
		}
	}

	public static class WallpaperLoader implements IModelLoader<RawWallpaperModel> {
		public static final ResourceLocation LOCATION = new ResourceLocation(AssortedDecor.MODID, "models/wallpaper");

		@Override
		public void onResourceManagerReload(IResourceManager resourceManager) {
		}

		@Override
		public RawWallpaperModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
			return new RawWallpaperModel();
		}
	}
}
