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

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

public abstract class ColorizerBaseBakedModel extends BakedModelWrapper<BakedModel> {
	protected final ModelBakery bakery;
	protected final Function<Material, TextureAtlasSprite> spriteGetter;
	protected final ModelState transform;
	protected final ResourceLocation name;
	protected final IModelConfiguration owner;
	protected final ItemOverrides overrides;
	protected final TextureAtlasSprite baseSprite;

	public ColorizerBaseBakedModel(BakedModel bakedColorizer, IModelConfiguration owner, TextureAtlasSprite baseSprite, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation name) {
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
		BlockState blockState = Blocks.AIR.defaultBlockState();
		if (extraData.getData(ColorizerTileEntity.BLOCK_STATE) != null) {
			blockState = extraData.getData(ColorizerTileEntity.BLOCK_STATE);
		}

		return this.getCachedModel(blockState).getQuads(state, side, rand, extraData);
	}

	protected final Map<BlockState, BakedModel> cache = new HashMap<BlockState, BakedModel>();

	public BakedModel getCachedModel(BlockState blockState) {
		if (!this.cache.containsKey(blockState)) {
			ImmutableMap.Builder<String, String> newTexture = ImmutableMap.builder();

			String texture = "";
			if (blockState == Blocks.AIR.defaultBlockState()) {
				texture = "assorteddecor:block/colorizer";
			} else if (blockState.getBlock() == Blocks.GRASS_BLOCK) {
				texture = "minecraft:block/grass_block_top";
			} else if (blockState.getBlock() == Blocks.PODZOL) {
				texture = "minecraft:block/dirt_podzol_top";
			} else if (blockState.getBlock() == Blocks.MYCELIUM) {
				texture = "minecraft:block/mycelium_top";
			} else {
				BlockModelShaper blockModel = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper();
				TextureAtlasSprite blockTexture = blockModel.getParticleIcon(blockState);

				texture = blockTexture.getName().toString();
			}

			newTexture.put("particle", texture);
			newTexture.put("#stored", texture);
			this.cache.put(blockState, generateModel(newTexture.build()));
		}

		return this.cache.get(blockState);
	}

	protected abstract BakedModel generateModel(ImmutableMap<String, String> texture);

	@Override
	public TextureAtlasSprite getParticleIcon(IModelData data) {
		BlockState state = data.getData(ColorizerTileEntity.BLOCK_STATE);
		if (state == null) {
			return this.baseSprite;
		} else if (state == Blocks.AIR.defaultBlockState()) {
			return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(new ResourceLocation(AssortedDecor.MODID, "block/colorizer"));
		}
		return Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(state);
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return this.getParticleIcon(EmptyModelData.INSTANCE);
	}

	public final ColorizerItemOverrideList INSTANCE = new ColorizerItemOverrideList();

	@Override
	public ItemOverrides getOverrides() {
		return INSTANCE;
	}

	public static final class ColorizerItemOverrideList extends ItemOverrides {

		@Override
		public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int field) {
			ColorizerBaseBakedModel colorizerModel = (ColorizerBaseBakedModel) originalModel;

			if (stack.hasTag() && stack.getTag().contains("stored_state")) {
				return new PerspectiveMapWrapper(colorizerModel.getCachedModel(NbtUtils.readBlockState(NBTHelper.getTag(stack, "stored_state"))), PerspectiveMapWrapper.getTransforms(colorizerModel.getTransforms()));
			}

			return new PerspectiveMapWrapper(colorizerModel.getCachedModel(Blocks.AIR.defaultBlockState()), PerspectiveMapWrapper.getTransforms(colorizerModel.getTransforms()));
		}
	}
}
