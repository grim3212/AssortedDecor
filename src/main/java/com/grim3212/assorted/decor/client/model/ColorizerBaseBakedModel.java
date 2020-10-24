package com.grim3212.assorted.decor.client.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.block.tileentity.ColorizerTileEntity;
import com.grim3212.assorted.decor.common.util.NBTHelper;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

public abstract class ColorizerBaseBakedModel extends BakedModelWrapper<IBakedModel> {
	protected final ModelBakery bakery;
	protected final Function<RenderMaterial, TextureAtlasSprite> spriteGetter;
	protected final IModelTransform transform;
	protected final ResourceLocation name;
	protected final IModelConfiguration owner;
	protected final ItemOverrideList overrides;
	protected final TextureAtlasSprite baseSprite;

	public ColorizerBaseBakedModel(IBakedModel bakedColorizer, IModelConfiguration owner, TextureAtlasSprite baseSprite, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation name) {
		super(bakedColorizer);
		this.bakery = bakery;
		this.spriteGetter = spriteGetter;
		this.transform = transform;
		this.name = name;
		this.owner = owner;
		this.overrides = overrides;
		this.baseSprite = baseSprite;
	}

	@Nonnull
	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand) {
		return getQuads(state, side, rand, EmptyModelData.INSTANCE);
	}

	@Nonnull
	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
		BlockState blockState = Blocks.AIR.getDefaultState();
		if (extraData.getData(ColorizerTileEntity.BLOCK_STATE) != null) {
			blockState = extraData.getData(ColorizerTileEntity.BLOCK_STATE);
		}
		return this.getCachedModel(blockState).getQuads(state, side, rand, extraData);
	}

	protected final Map<BlockState, IBakedModel> cache = new HashMap<BlockState, IBakedModel>();

	public IBakedModel getCachedModel(BlockState blockState) {
		if (!this.cache.containsKey(blockState)) {
			ImmutableMap.Builder<String, String> newTexture = ImmutableMap.builder();

			String texture = "";
			if (blockState == Blocks.AIR.getDefaultState()) {
				texture = "assorteddecor:block/colorizer";
			} else if (blockState.getBlock() == Blocks.GRASS_BLOCK) {
				texture = "minecraft:block/grass_block_top";
			} else if (blockState.getBlock() == Blocks.PODZOL) {
				texture = "minecraft:block/dirt_podzol_top";
			} else if (blockState.getBlock() == Blocks.MYCELIUM) {
				texture = "minecraft:block/mycelium_top";
			} else {
				BlockModelShapes blockModel = Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes();
				TextureAtlasSprite blockTexture = blockModel.getTexture(blockState);

				texture = blockTexture.getName().toString();
			}

			newTexture.put("particle", texture);
			newTexture.put("#stored", texture);
			this.cache.put(blockState, generateModel(newTexture.build()));
		}

		return this.cache.get(blockState);
	}

	protected abstract IBakedModel generateModel(ImmutableMap<String, String> texture);

	@Override
	public TextureAtlasSprite getParticleTexture(IModelData data) {
		if (data.getData(ColorizerTileEntity.BLOCK_STATE) == null) {
			return this.baseSprite;
		} else if (data.getData(ColorizerTileEntity.BLOCK_STATE) == Blocks.AIR.getDefaultState()) {
			return Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(new ResourceLocation(AssortedDecor.MODID, "block/colorizer"));
		}

		BlockModelShapes blockModel = Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes();
		return blockModel.getTexture(data.getData(ColorizerTileEntity.BLOCK_STATE));
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return this.getParticleTexture(EmptyModelData.INSTANCE);
	}

	public final ColorizerItemOverrideList INSTANCE = new ColorizerItemOverrideList();

	@Override
	public ItemOverrideList getOverrides() {
		return INSTANCE;
	}

	public static final class ColorizerItemOverrideList extends ItemOverrideList {

		@Override
		public IBakedModel func_239290_a_(IBakedModel originalModel, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
			ColorizerBaseBakedModel colorizerModel = (ColorizerBaseBakedModel) originalModel;

			if (stack.hasTag() && stack.getTag().contains("stored_state")) {
				return new PerspectiveMapWrapper(colorizerModel.getCachedModel(NBTUtil.readBlockState(NBTHelper.getTag(stack, "stored_state"))), PerspectiveMapWrapper.getTransforms(colorizerModel.getItemCameraTransforms()));
			}

			return new PerspectiveMapWrapper(colorizerModel.getCachedModel(Blocks.AIR.getDefaultState()), PerspectiveMapWrapper.getTransforms(colorizerModel.getItemCameraTransforms()));
		}
	}
}
