package com.grim3212.assorted.decor.common.block;

import com.grim3212.assorted.decor.common.block.tileentity.ColorizerTileEntity;
import com.grim3212.assorted.decor.common.handler.DecorConfig;
import com.grim3212.assorted.decor.common.util.NBTHelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;

public class ColorizerBlock extends Block implements IColorizer {

	public ColorizerBlock(Properties properties) {
		super(properties.hardnessAndResistance(1.5f, 12.0f).variableOpacity().notSolid());
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
		return this.getStoredState(reader, pos) != Blocks.AIR.getDefaultState() ? this.getStoredState(reader, pos).propagatesSkylightDown(reader, pos) : super.propagatesSkylightDown(state, reader, pos);
	}

	@Override
	public VoxelShape getRayTraceShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
		return this.getStoredState(reader, pos) != Blocks.AIR.getDefaultState() ? this.getStoredState(reader, pos).getRaytraceShape(reader, pos, context) : super.getRayTraceShape(state, reader, pos, context);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return this.getStoredState(worldIn, pos) != Blocks.AIR.getDefaultState() ? this.getStoredState(worldIn, pos).getAmbientOcclusionLightValue(worldIn, pos) : super.getAmbientOcclusionLightValue(state, worldIn, pos);
	}

	protected BlockState getStoredState(IBlockReader worldIn, BlockPos pos) {
		TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof ColorizerTileEntity) {
			return ((ColorizerTileEntity) te).getStoredBlockState();
		}
		return Blocks.AIR.getDefaultState();
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		super.onBlockHarvested(worldIn, pos, state, player);
		if (DecorConfig.COMMON.consumeBlock.get()) {
			if (!player.abilities.isCreativeMode) {
				if (this.getStoredState(worldIn, pos) != Blocks.AIR.getDefaultState()) {
					BlockState blockState = this.getStoredState(worldIn, pos);
					spawnAsEntity(worldIn, pos, new ItemStack(blockState.getBlock(), 1));
				}
			}
		}
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		ItemStack itemstack = new ItemStack(this);
		NBTHelper.putTag(itemstack, "stored_state", NBTUtil.writeBlockState(Blocks.AIR.getDefaultState()));
		return itemstack;
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		TileEntity tileentity = worldIn.getTileEntity(pos);

		ItemStack heldItem = player.getHeldItem(hand);

		if (!heldItem.isEmpty() && tileentity instanceof ColorizerTileEntity) {

			// if (heldItem.getItem() == DecorItems.brush) {
			// if (this.tryUseBrush(worldIn, player, hand, pos)) {
			// return ActionResultType.SUCCESS;
			// }
			// }

			ColorizerTileEntity te = (ColorizerTileEntity) tileentity;
			Block block = Block.getBlockFromItem(heldItem.getItem());

			if (block != null && !(block instanceof ColorizerBlock)) {
				// if (BlockHelper.getUsableBlocks().contains(block.getDefaultState())) {
				// Can only set blockstate if it contains nothing or if
				// in creative mode
				if (te.getStoredBlockState() == Blocks.AIR.getDefaultState() || player.abilities.isCreativeMode) {
					BlockState placeState = block.getStateForPlacement(new BlockItemUseContext(new ItemUseContext(player, hand, hit)));

					setColorizer(worldIn, pos, state, placeState, player, hand, true);

					SoundType placeSound = placeState.getSoundType(worldIn, pos, player);

					worldIn.playSound(player, pos, placeSound.getPlaceSound(), SoundCategory.BLOCKS, (placeSound.getVolume() + 1.0F) / 2.0F, placeSound.getPitch() * 0.8F);

					return ActionResultType.SUCCESS;
				} else if (te.getStoredBlockState() != Blocks.AIR.getDefaultState()) {
					return ActionResultType.FAIL;
				}
				// } else {
				// return ActionResultType.FAIL;
				// }
			} else {
				return ActionResultType.FAIL;
			}
		}

		return ActionResultType.PASS;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new ColorizerTileEntity();
	}

	@Override
	public boolean clearColorizer(World worldIn, BlockPos pos, BlockState state, PlayerEntity player, Hand hand) {
		TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof ColorizerTileEntity) {
			ColorizerTileEntity tileColorizer = (ColorizerTileEntity) te;
			BlockState storedState = tileColorizer.getStoredBlockState();

			// Can only clear a filled colorizer
			if (storedState != Blocks.AIR.getDefaultState()) {

				if (DecorConfig.COMMON.consumeBlock.get() && !player.abilities.isCreativeMode) {
					ItemEntity blockDropped = new ItemEntity(worldIn, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), new ItemStack(tileColorizer.getStoredBlockState().getBlock(), 1));
					if (!worldIn.isRemote) {
						worldIn.addEntity(blockDropped);
						if (!(player instanceof FakePlayer)) {
							blockDropped.onCollideWithPlayer(player);
						}
					}
				}

				// Clear Self
				if (setColorizer(worldIn, pos, state, null, player, hand, false)) {
					SoundType placeSound = state.getSoundType(worldIn, pos, player);

					worldIn.playSound(player, pos, placeSound.getPlaceSound(), SoundCategory.BLOCKS, (placeSound.getVolume() + 1.0F) / 2.0F, placeSound.getPitch() * 0.8F);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean setColorizer(World worldIn, BlockPos pos, BlockState state, BlockState toSetState, PlayerEntity player, Hand hand, boolean consumeItem) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if (tileentity instanceof ColorizerTileEntity) {
			ColorizerTileEntity te = (ColorizerTileEntity) tileentity;
			te.setStoredBlockState(toSetState != null ? toSetState : Blocks.AIR.getDefaultState());

			// Remove an item if config allows and we are not resetting
			// colorizer
			if (DecorConfig.COMMON.consumeBlock.get() && toSetState != null && consumeItem) {
				if (!player.abilities.isCreativeMode)
					player.getHeldItem(hand).shrink(1);
			}

			return true;
		}
		return false;
	}

	public boolean tryUseBrush(World world, PlayerEntity player, Hand hand, BlockPos pos) {
		if (player.isSneaking()) {
			if (world.isRemote) {
				TileEntity tileentity = world.getTileEntity(pos);

				if (tileentity instanceof ColorizerTileEntity) {
					BlockState storedState = ((ColorizerTileEntity) tileentity).getStoredBlockState();

					ItemStack storedstack = new ItemStack(storedState.getBlock(), 1);
					// if (storedstack.getItem() != null)
					// player.sendMessage(new
					// TranslationTextComponent("grimpack.decor.brush.stored",
					// storedstack.getDisplayName()));
					// else
					// player.sendMessage(new
					// TranslationTextComponent("grimpack.decor.brush.empty"));
				}
				return true;
			}
		}

		BlockState targetBlockState = world.getBlockState(pos);

		if (targetBlockState.getBlock() instanceof IColorizer) {
			if (((IColorizer) targetBlockState.getBlock()).clearColorizer(world, pos, targetBlockState, player, hand)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean addLandingEffects(BlockState state, ServerWorld worldObj, BlockPos blockPosition, BlockState iblockstate, LivingEntity entity, int numberOfParticles) {
		TileEntity tileentity = (TileEntity) worldObj.getTileEntity(blockPosition);
		if (tileentity instanceof ColorizerTileEntity) {
			ColorizerTileEntity te = (ColorizerTileEntity) tileentity;
			if (te.getStoredBlockState() == Blocks.AIR.getDefaultState()) {
				return super.addLandingEffects(state, worldObj, blockPosition, iblockstate, entity, numberOfParticles);
			} else {
				worldObj.spawnParticle(new BlockParticleData(ParticleTypes.BLOCK, te.getStoredBlockState()), entity.getPosX(), entity.getPosY(), entity.getPosZ(), numberOfParticles, 0.0D, 0.0D, 0.0D, 0.15000000596046448D);
			}
		}
		return true;
	}
}
