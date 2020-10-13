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
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
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
import net.minecraftforge.client.model.ModelLoader;
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
	protected final ImmutableList<OBJModelCopy> modelParts;

	public ColorizerOBJModel(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation, ResourceLocation texture, ImmutableList<ResourceLocation> parts) {
		this.owner = owner;
		this.bakery = bakery;
		this.spriteGetter = spriteGetter;
		this.transform = modelTransform;
		this.overrides = overrides;
		this.modelLocation = modelLocation;
		this.textureLocation = texture;

		this.baseModel = OBJModelCopy.loadModel(defaultSettings(parts.get(0)));

		ImmutableList.Builder<OBJModelCopy> builder = ImmutableList.builder();
		for (int i = 1; i < parts.size(); i++) {
			builder.add(OBJModelCopy.loadModel(defaultSettings(parts.get(i))));
		}
		this.modelParts = builder.build();
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
		ImmutableList.Builder<IBakedModel> builder = ImmutableList.builder();
		builder.add(this.baseModel.setTexture(sprite).bake(owner, bakery, spriteGetter, transform, overrides, modelLocation));
		for (OBJModelCopy model : this.modelParts)
			builder.add(model.bake(owner, bakery, spriteGetter, transform, overrides, modelLocation));

		return new DecorCompositeModel(builder.build());
	}

	/**
	 * Generates the model defined in the json and then also merges extra models to
	 * it
	 * 
	 * @param state
	 * @param texture
	 * @param models
	 * @return
	 */
	protected IBakedModel generateModel(ImmutableMap<String, String> texture, OBJModelCopy... models) {
		ImmutableList.Builder<IBakedModel> builder = ImmutableList.builder();
		builder.add(this.generateModel(texture));
		for (OBJModelCopy model : models)
			builder.add(model.bake(owner, bakery, spriteGetter, transform, overrides, modelLocation));

		return new DecorCompositeModel(builder.build());
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
		private final ImmutableList<ResourceLocation> parts;

		RawColorizerModel(ResourceLocation texture, ImmutableList<ResourceLocation> parts) {
			this.texture = texture;
			this.parts = parts;
		}

		public RawColorizerModel withTexture(ResourceLocation newTexture) {
			return new RawColorizerModel(newTexture, this.parts);
		}

		@Override
		public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
			return new ColorizerOBJModel(owner, bakery, spriteGetter, modelTransform, overrides, modelLocation, this.texture, this.parts);
		}

		@Override
		public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
			return ImmutableList.of(ModelLoaderRegistry.blockMaterial(texture));
		}
	}

	public static class ColorizerOBJLoader implements IModelLoader<RawColorizerModel> {
		public static final ResourceLocation LOCATION = new ResourceLocation(AssortedDecor.MODID, "models/colorizer_obj");

		@Override
		public void onResourceManagerReload(IResourceManager resourceManager) {
		}

		@Override
		public RawColorizerModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
			ImmutableList.Builder<ResourceLocation> modelLocations = ImmutableList.builder();

			ResourceLocation base = new ResourceLocation(AssortedDecor.MODID, "block/colorizer");

			if (modelContents.has("texture"))
				base = new ResourceLocation(modelContents.get("texture").getAsString());

			if (!modelContents.has("parts"))
				throw new UnsupportedOperationException("Model location not found for a ColorizerOBJModel");

			String[] models = modelContents.get("parts").getAsString().replaceAll("\\[|\\]|\"", "").split(",");

			for (String model : models) {
				AssortedDecor.LOGGER.info("Adding model : " + model);
				modelLocations.add(new ResourceLocation(model));
			}

			ImmutableList<ResourceLocation> immutableModels = modelLocations.build();
			for (int i = 1; i < immutableModels.size(); i++) {
				// Load the extra models and this should load the sub-model textures
				ModelLoader.instance().getModelOrLogError(immutableModels.get(i), "Model couldn't be found " + immutableModels.get(i));
			}

			return new RawColorizerModel(base, immutableModels);
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
