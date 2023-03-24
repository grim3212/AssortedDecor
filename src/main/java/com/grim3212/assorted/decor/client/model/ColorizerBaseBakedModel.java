package com.grim3212.assorted.decor.client.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableMap;
import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.block.blockentity.ColorizerBlockEntity;
import com.grim3212.assorted.decor.common.util.NBTHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;

public abstract class ColorizerBaseBakedModel extends BakedModelWrapper<BakedModel> {
	public static final ModelProperty<BlockState> BLOCK_STATE = new ModelProperty<>();
	
	protected final ModelBaker bakery;
	protected final Function<Material, TextureAtlasSprite> spriteGetter;
	protected final ModelState transform;
	protected final ResourceLocation name;
	protected final IGeometryBakingContext owner;
	protected final ItemOverrides overrides;
	protected final TextureAtlasSprite baseSprite;

	public ColorizerBaseBakedModel(BakedModel bakedColorizer, IGeometryBakingContext owner, TextureAtlasSprite baseSprite, ModelBaker bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation name) {
		super(bakedColorizer);
		this.bakery = bakery;
		this.spriteGetter = spriteGetter;
		this.transform = transform;
		this.name = name;
		this.owner = owner;
		this.overrides = overrides;
		this.baseSprite = baseSprite;
	}
	
	@Override
	public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
		if (level.getBlockEntity(pos)instanceof ColorizerBlockEntity colorizer) {
			return ModelData.builder()
                    .with(BLOCK_STATE, colorizer.getStoredBlockState())
                    .build();
        }
		
		return super.getModelData(level, pos, state, modelData);
	}

	@Nonnull
	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand) {
		return getQuads(state, side, rand, ModelData.EMPTY, RenderType.translucent());
	}

	@Nonnull
	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand, @Nonnull ModelData extraData, @Nullable RenderType renderType) {
		BlockState blockState = Blocks.AIR.defaultBlockState();
		if (extraData.get(BLOCK_STATE) != null) {
			blockState = extraData.get(BLOCK_STATE);
		}

		return this.getCachedModel(blockState).getQuads(state, side, rand, extraData, RenderType.translucent());
	}

	@Override
	public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
		return ChunkRenderTypeSet.of(RenderType.translucent());
	}

	protected final Map<BlockState, BakedModel> cache = new HashMap<BlockState, BakedModel>();
	protected BakedModel EMPTY;

	public BakedModel getCachedModel(BlockState blockState) {
		if (blockState == null || blockState == Blocks.AIR.defaultBlockState()) {
			if (EMPTY == null) {
				ImmutableMap.Builder<String, String> newTexture = ImmutableMap.builder();
				String texture = "assorteddecor:block/colorizer";
				newTexture.put("particle", texture);
				newTexture.put("#stored", texture);
				EMPTY = generateModel(newTexture.build());
			}
			return EMPTY;
		}

		if (!this.cache.containsKey(blockState)) {
			ImmutableMap.Builder<String, String> newTexture = ImmutableMap.builder();

			String texture = "";
			if (blockState.getBlock() == Blocks.GRASS_BLOCK) {
				texture = "minecraft:block/grass_block_top";
			} else if (blockState.getBlock() == Blocks.PODZOL) {
				texture = "minecraft:block/dirt_podzol_top";
			} else if (blockState.getBlock() == Blocks.MYCELIUM) {
				texture = "minecraft:block/mycelium_top";
			} else {
				BlockModelShaper blockModel = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper();
				TextureAtlasSprite blockTexture = blockModel.getParticleIcon(blockState);
				texture = blockTexture.contents().name().toString();
			}

			newTexture.put("particle", texture);
			newTexture.put("#stored", texture);
			this.cache.put(blockState, generateModel(newTexture.build()));
		}

		return this.cache.get(blockState);
	}

	protected abstract BakedModel generateModel(ImmutableMap<String, String> texture);

	@Override
	public TextureAtlasSprite getParticleIcon(ModelData data) {
		BlockState state = data.get(BLOCK_STATE);
		if (state == null) {
			return this.baseSprite;
		} else if (state == Blocks.AIR.defaultBlockState()) {
			return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(new ResourceLocation(AssortedDecor.MODID, "block/colorizer"));
		}
		return Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(state);
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return this.getParticleIcon(ModelData.EMPTY);
	}

	public final ColorizerItemOverrideList INSTANCE = new ColorizerItemOverrideList();

	@Override
	public ItemOverrides getOverrides() {
		return INSTANCE;
	}

	public static final class ColorizerItemOverrideList extends ItemOverrides {

		@Override
		public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int field) {
			ColorizerBaseBakedModel bridgeModel = (ColorizerBaseBakedModel) originalModel;

			if (stack.hasTag() && stack.getTag().contains("stored_state")) {
				return bridgeModel.getCachedModel(NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), NBTHelper.getTag(stack, "stored_state")));
			}

			return bridgeModel.getCachedModel(Blocks.AIR.defaultBlockState());
		}
	}
}
