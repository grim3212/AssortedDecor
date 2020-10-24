package com.grim3212.assorted.decor.client.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.block.tileentity.ColorizerTileEntity;
import com.grim3212.assorted.decor.common.util.NBTHelper;
import com.mojang.datafixers.util.Pair;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.client.model.obj.OBJModel;

public class ColorizerOBJModel implements IDynamicBakedModel {

	protected final IModelConfiguration owner;
	protected final Function<RenderMaterial, TextureAtlasSprite> spriteGetter;
	protected final ResourceLocation modelLocation;
	protected final IModelTransform transform;
	protected final ModelBakery bakery;
	protected final ItemOverrideList overrides;

	protected final ResourceLocation textureLocation;
	protected final OBJModelCopy baseModel;

	public ColorizerOBJModel(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation, ResourceLocation texture) {
		this.owner = owner;
		this.bakery = bakery;
		this.spriteGetter = spriteGetter;
		this.transform = modelTransform;
		this.overrides = overrides;
		this.modelLocation = modelLocation;
		this.textureLocation = texture;

		this.baseModel = OBJModelCopy.loadModel(defaultSettings(modelLocation));
	}

	private OBJModel.ModelSettings defaultSettings(ResourceLocation loc) {
		return new OBJModel.ModelSettings(loc, true, true, true, true, null);
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
		if (extraData.getData(ColorizerTileEntity.BLOCK_STATE) != null) {
			BlockState blockState = extraData.getData(ColorizerTileEntity.BLOCK_STATE);
			return this.getCachedModel(blockState).getQuads(state, side, rand, extraData);
		}

		return ImmutableList.of();
	}

	protected final Map<BlockState, IBakedModel> cache = new HashMap<BlockState, IBakedModel>();

	public IBakedModel getCachedModel(BlockState blockState) {
		if (!this.cache.containsKey(blockState)) {
			Minecraft mc = Minecraft.getInstance();
			Function<ResourceLocation, TextureAtlasSprite> textures = mc.getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
			BlockModelShapes blockModel = mc.getBlockRendererDispatcher().getBlockModelShapes();
			TextureAtlasSprite blockTexture = blockModel.getTexture(blockState);

			String texture = "";
			if (blockState == Blocks.AIR.getDefaultState()) {
				texture = "assorteddecor:block/colorizer";
			} else if (blockState.getBlock() == Blocks.GRASS_BLOCK) {
				texture = "minecraft:block/grass_block_top";
			} else if (blockState.getBlock() == Blocks.PODZOL) {
				texture = "minecraft:block/dirt_podzol_top";
			} else if (blockState.getBlock() == Blocks.MYCELIUM) {
				texture = "minecraft:block/mycelium_top";
			}

			if (texture.isEmpty()) {
				this.cache.put(blockState, generateModel(blockTexture));
			} else {
				this.cache.put(blockState, generateModel(textures.apply(new ResourceLocation(texture))));
			}

		}

		return this.cache.get(blockState);
	}

	/**
	 * Generate the model defined in json that is a combination of all models
	 * defined
	 * 
	 * @param state
	 * @param texture
	 * @return
	 */
	protected IBakedModel generateModel(TextureAtlasSprite sprite) {
		return this.baseModel.setTexture(sprite).bake(owner, bakery, spriteGetter, transform, overrides, modelLocation);
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
		return true;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture(IModelData data) {
		BlockModelShapes blockModel = Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes();
		if (data.getData(ColorizerTileEntity.BLOCK_STATE) != Blocks.AIR.getDefaultState()) {
			return blockModel.getTexture(data.getData(ColorizerTileEntity.BLOCK_STATE));
		}
		return Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(new ResourceLocation(AssortedDecor.MODID, "block/colorizer"));
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return this.getParticleTexture(EmptyModelData.INSTANCE);
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return this.getBaseBakedModel().getItemCameraTransforms();
	}

	private IBakedModel getBaseBakedModel() {
		return this.baseModel.bake(owner, bakery, spriteGetter, transform, overrides, modelLocation);
	}

	public final ColorizerOBJItemOverrideHandler INSTANCE = new ColorizerOBJItemOverrideHandler();

	@Override
	public ItemOverrideList getOverrides() {
		return INSTANCE;
	}

	public static class RawColorizerModel implements IModelGeometry<RawColorizerModel> {

		private final ResourceLocation texture;
		private final ResourceLocation model;
		private final ImmutableList<ResourceLocation> extraTextures;

		RawColorizerModel(ResourceLocation texture, ResourceLocation model) {
			this(texture, model, ImmutableList.of());
		}

		RawColorizerModel(ResourceLocation texture, ResourceLocation model, ImmutableList<ResourceLocation> extraTextures) {
			this.texture = texture;
			this.model = model;
			this.extraTextures = extraTextures;
		}

		public RawColorizerModel withTexture(ResourceLocation newTexture) {
			return new RawColorizerModel(newTexture, this.model);
		}

		@Override
		public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
			return new ColorizerOBJModel(owner, bakery, spriteGetter, modelTransform, overrides, this.model, this.texture);
		}

		@Override
		public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
			ImmutableList.Builder<RenderMaterial> builder = ImmutableList.builder();
			builder.add(ModelLoaderRegistry.blockMaterial(texture));

			for (ResourceLocation tex : extraTextures) {
				builder.add(ModelLoaderRegistry.blockMaterial(tex));
			}

			return builder.build();
		}
	}

	public static class ColorizerOBJLoader implements IModelLoader<RawColorizerModel> {
		public static final ResourceLocation LOCATION = new ResourceLocation(AssortedDecor.MODID, "models/colorizer_obj");

		@Override
		public void onResourceManagerReload(IResourceManager resourceManager) {
		}

		@Override
		public RawColorizerModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
			ResourceLocation model = null;
			ImmutableList.Builder<ResourceLocation> extraTextures = ImmutableList.builder();

			ResourceLocation base = new ResourceLocation(AssortedDecor.MODID, "block/colorizer");

			if (modelContents.has("texture"))
				base = new ResourceLocation(modelContents.get("texture").getAsString());

			if (!modelContents.has("model"))
				throw new UnsupportedOperationException("Model location not found for a ColorizerOBJModel");
			model = new ResourceLocation(modelContents.get("model").getAsString());

			if (modelContents.has("extraTextures")) {
				JsonArray textures = modelContents.get("extraTextures").getAsJsonArray();

				for (JsonElement tex : textures) {
					String texLoc = tex.getAsString();
					AssortedDecor.LOGGER.info("Adding extra texture : " + texLoc);
					extraTextures.add(new ResourceLocation(texLoc));
				}
			}

			return new RawColorizerModel(base, model, extraTextures.build());
		}
	}

	public final class ColorizerOBJItemOverrideHandler extends ItemOverrideList {

		@Override
		public IBakedModel func_239290_a_(IBakedModel originalModel, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
			ColorizerOBJModel colorizerModel = (ColorizerOBJModel) originalModel;

			if (stack.hasTag() && stack.getTag().contains("stored_state")) {
				return new PerspectiveMapWrapper(colorizerModel.getCachedModel(NBTUtil.readBlockState(NBTHelper.getTag(stack, "stored_state"))), PerspectiveMapWrapper.getTransforms(colorizerModel.getItemCameraTransforms()));
			}

			return new PerspectiveMapWrapper(colorizerModel.getCachedModel(Blocks.AIR.getDefaultState()), PerspectiveMapWrapper.getTransforms(colorizerModel.getItemCameraTransforms()));
		}
	}
}
