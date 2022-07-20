package com.grim3212.assorted.decor.client.proxy;

import com.grim3212.assorted.decor.client.blockentity.CalendarBlockEntityRenderer;
import com.grim3212.assorted.decor.client.blockentity.NeonSignBlockEntityRenderer;
import com.grim3212.assorted.decor.client.model.ColorizerBlockModel;
import com.grim3212.assorted.decor.client.model.ColorizerObjModel;
import com.grim3212.assorted.decor.client.render.entity.FrameRenderer;
import com.grim3212.assorted.decor.client.render.entity.WallpaperRenderer;
import com.grim3212.assorted.decor.client.screen.NeonSignScreen;
import com.grim3212.assorted.decor.common.block.ColorChangingBlock;
import com.grim3212.assorted.decor.common.block.DecorBlocks;
import com.grim3212.assorted.decor.common.block.FluroBlock;
import com.grim3212.assorted.decor.common.block.blockentity.ColorizerBlockEntity;
import com.grim3212.assorted.decor.common.block.blockentity.DecorBlockEntityTypes;
import com.grim3212.assorted.decor.common.block.blockentity.NeonSignBlockEntity;
import com.grim3212.assorted.decor.common.entity.DecorEntityTypes;
import com.grim3212.assorted.decor.common.item.DecorItems;
import com.grim3212.assorted.decor.common.proxy.IProxy;
import com.grim3212.assorted.decor.common.util.NBTHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientProxy implements IProxy {

	@Override
	public void starting() {
		final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(this::setupClient);
		modBus.addListener(this::registerRenderers);
		modBus.addListener(this::loadComplete);
		modBus.addListener(this::registerLoaders);
	}

	private void registerLoaders(final ModelEvent.RegisterGeometryLoaders event) {
		event.register("models/colorizer", ColorizerBlockModel.Loader.INSTANCE);
		event.register("models/colorizer_obj", ColorizerObjModel.ColorizerObjLoader.INSTANCE);
	}

	@Override
	public void produceSmoke(Level world, BlockPos pos, double xMod, double yMod, double zMod, int amount, boolean makelarge) {
		for (int i = 0; i < amount; i++) {
			double xVar = (world.random.nextDouble() - 0.5D) / 5.0D;
			double yVar = (world.random.nextDouble() - 0.5D) / 5.0D;
			double zVar = (world.random.nextDouble() - 0.5D) / 5.0D;
			world.addParticle(makelarge ? ParticleTypes.LARGE_SMOKE : ParticleTypes.SMOKE, pos.getX() + xMod + xVar, pos.getY() + yMod + yVar, pos.getZ() + zMod + zVar, 0.0D, 0.0D, 0.0D);
		}
	}

	private void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(DecorEntityTypes.WALLPAPER.get(), WallpaperRenderer::new);
		event.registerEntityRenderer(DecorEntityTypes.FRAME.get(), FrameRenderer::new);
	}

	private void setupClient(final FMLClientSetupEvent event) {
		BlockEntityRenderers.register(DecorBlockEntityTypes.NEON_SIGN.get(), NeonSignBlockEntityRenderer::new);
		BlockEntityRenderers.register(DecorBlockEntityTypes.CALENDAR.get(), CalendarBlockEntityRenderer::new);
	}

	public void loadComplete(final FMLLoadCompleteEvent event) {
		event.enqueueWork(() -> {
			ItemColors items = Minecraft.getInstance().getItemColors();
			BlockColors blocks = Minecraft.getInstance().getBlockColors();

			blocks.register(new BlockColor() {
				@Override
				public int getColor(BlockState state, BlockAndTintGetter worldIn, BlockPos pos, int tint) {
					if (pos != null) {
						BlockEntity te = worldIn.getBlockEntity(pos);
						if (te != null && te instanceof ColorizerBlockEntity) {
							return Minecraft.getInstance().getBlockColors().getColor(((ColorizerBlockEntity) te).getStoredBlockState(), worldIn, pos, tint);
						}
					}
					return 16777215;
				}
			}, DecorBlocks.colorizerBlocks());

			items.register(new ItemColor() {
				@Override
				public int getColor(ItemStack stack, int tint) {
					if (stack != null && stack.hasTag()) {
						if (stack.getTag().contains("stored_state")) {
							BlockState stored = NbtUtils.readBlockState(NBTHelper.getTag(stack, "stored_state"));
							ItemStack colorStack = new ItemStack(stored.getBlock());
							if (colorStack.getItem() != null) {
								return Minecraft.getInstance().getItemColors().getColor(colorStack, tint);
							}
						}
					}
					return 16777215;
				}
			}, DecorBlocks.colorizerBlocks());

			items.register(new ItemColor() {
				@Override
				public int getColor(ItemStack stack, int tint) {
					if (stack != null && stack.hasTag()) {
						if (stack.getTag().contains("stored_state")) {
							BlockState stored = NbtUtils.readBlockState(NBTHelper.getTag(stack, "stored_state"));
							ItemStack colorStack = new ItemStack(stored.getBlock());
							if (colorStack.getItem() != null && !(colorStack.getItem() instanceof AirItem)) {
								return Minecraft.getInstance().getItemColors().getColor(colorStack, tint);
							}
						}
					}
					return 16777215;
				}
			}, DecorItems.COLORIZER_BRUSH.get());

			blocks.register(new BlockColor() {
				@Override
				public int getColor(BlockState state, BlockAndTintGetter worldIn, BlockPos pos, int tint) {
					return state.getBlock().defaultMaterialColor().col;
				}
			}, FluroBlock.FLURO_BY_DYE.entrySet().stream().map((x) -> x.getValue().get()).toArray(Block[]::new));

			items.register(new ItemColor() {
				@Override
				public int getColor(ItemStack stack, int tint) {
					Block b = Block.byItem(stack.getItem());
					if (b != Blocks.AIR) {
						return b.defaultMaterialColor().col;
					}
					return 16777215;
				}
			}, FluroBlock.FLURO_BY_DYE.entrySet().stream().map((x) -> x.getValue().get()).toArray(Block[]::new));

			blocks.register(new BlockColor() {
				@Override
				public int getColor(BlockState state, BlockAndTintGetter worldIn, BlockPos pos, int tint) {
					return state.getValue(ColorChangingBlock.COLOR).getMaterialColor().col;
				}
			}, DecorBlocks.SIDING_HORIZONTAL.get(), DecorBlocks.SIDING_VERTICAL.get());

			items.register(new ItemColor() {
				@Override
				public int getColor(ItemStack stack, int tint) {
					if (stack != null && stack.hasTag() && stack.getTag().contains("BlockStateTag")) {
						CompoundTag blockState = NBTHelper.getTag(stack.getTag(), "BlockStateTag");
						if (blockState.contains("color")) {
							DyeColor color = DyeColor.byName(NBTHelper.getString(blockState, "color"), DyeColor.WHITE);
							return color.getMaterialColor().col;
						}
					}
					return 16777215;
				}
			}, DecorBlocks.SIDING_HORIZONTAL.get(), DecorBlocks.SIDING_VERTICAL.get());
		});
	}

	@Override
	public Player getClientPlayer() {
		return Minecraft.getInstance().player;
	}

	@Override
	public void openNeonSign(NeonSignBlockEntity tile) {
		Minecraft.getInstance().setScreen(new NeonSignScreen(tile));
	}

	@Override
	public void handleOpenNeonSign(BlockPos pos) {
		BlockEntity tileentity = Minecraft.getInstance().player.getCommandSenderWorld().getBlockEntity(pos);

		// Make sure TileEntity exists
		if (!(tileentity instanceof NeonSignBlockEntity)) {
			tileentity = new NeonSignBlockEntity(pos, tileentity.getBlockState());
			tileentity.setLevel(Minecraft.getInstance().player.getCommandSenderWorld());
		}

		openNeonSign((NeonSignBlockEntity) tileentity);
	}
}
