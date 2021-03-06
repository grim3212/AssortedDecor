package com.grim3212.assorted.decor.client.proxy;

import com.grim3212.assorted.decor.client.model.ColorizerBlockModel;
import com.grim3212.assorted.decor.client.model.ColorizerOBJModel;
import com.grim3212.assorted.decor.client.render.entity.FrameRenderer;
import com.grim3212.assorted.decor.client.render.entity.WallpaperRenderer;
import com.grim3212.assorted.decor.client.screen.NeonSignScreen;
import com.grim3212.assorted.decor.client.tileentity.CalendarTileEntityRenderer;
import com.grim3212.assorted.decor.client.tileentity.NeonSignTileEntityRenderer;
import com.grim3212.assorted.decor.common.block.DecorBlocks;
import com.grim3212.assorted.decor.common.block.tileentity.ColorizerTileEntity;
import com.grim3212.assorted.decor.common.block.tileentity.DecorTileEntityTypes;
import com.grim3212.assorted.decor.common.block.tileentity.NeonSignTileEntity;
import com.grim3212.assorted.decor.common.entity.DecorEntityTypes;
import com.grim3212.assorted.decor.common.item.DecorItems;
import com.grim3212.assorted.decor.common.proxy.IProxy;
import com.grim3212.assorted.decor.common.util.NBTHelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AirItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientProxy implements IProxy {

	@Override
	public void starting() {
		final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(this::setupClient);
		modBus.addListener(this::loadComplete);

		if (Minecraft.getInstance() != null) {
			ModelLoaderRegistry.registerLoader(ColorizerBlockModel.Loader.LOCATION, ColorizerBlockModel.Loader.INSTANCE);
			ModelLoaderRegistry.registerLoader(ColorizerOBJModel.Loader.LOCATION, ColorizerOBJModel.Loader.INSTANCE);
		}
	}

	@Override
	public void produceSmoke(World world, BlockPos pos, double xMod, double yMod, double zMod, int amount, boolean makelarge) {
		for (int i = 0; i < amount; i++) {
			double xVar = (world.random.nextDouble() - 0.5D) / 5.0D;
			double yVar = (world.random.nextDouble() - 0.5D) / 5.0D;
			double zVar = (world.random.nextDouble() - 0.5D) / 5.0D;
			world.addParticle(makelarge ? ParticleTypes.LARGE_SMOKE : ParticleTypes.SMOKE, pos.getX() + xMod + xVar, pos.getY() + yMod + yVar, pos.getZ() + zMod + zVar, 0.0D, 0.0D, 0.0D);
		}
	}

	private void setupClient(final FMLClientSetupEvent event) {
		for (Block b : DecorBlocks.colorizerBlocks()) {
			RenderTypeLookup.setRenderLayer(b, RenderType.translucent());
		}

		RenderTypeLookup.setRenderLayer(DecorBlocks.ILLUMINATION_TUBE.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(DecorBlocks.QUARTZ_DOOR.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(DecorBlocks.WALL_CLOCK.get(), RenderType.cutout());

		RenderingRegistry.registerEntityRenderingHandler(DecorEntityTypes.WALLPAPER.get(), WallpaperRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(DecorEntityTypes.FRAME.get(), FrameRenderer::new);

		ClientRegistry.bindTileEntityRenderer(DecorTileEntityTypes.NEON_SIGN.get(), NeonSignTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(DecorTileEntityTypes.CALENDAR.get(), CalendarTileEntityRenderer::new);
	}

	public void loadComplete(final FMLLoadCompleteEvent event) {
		event.enqueueWork(() -> {
			ItemColors items = Minecraft.getInstance().getItemColors();
			BlockColors blocks = Minecraft.getInstance().getBlockColors();

			blocks.register(new IBlockColor() {
				@Override
				public int getColor(BlockState state, IBlockDisplayReader worldIn, BlockPos pos, int tint) {
					if (pos != null) {
						TileEntity te = worldIn.getBlockEntity(pos);
						if (te != null && te instanceof ColorizerTileEntity) {
							return Minecraft.getInstance().getBlockColors().getColor(((ColorizerTileEntity) te).getStoredBlockState(), worldIn, pos, tint);
						}
					}
					return 16777215;
				}
			}, DecorBlocks.colorizerBlocks());

			items.register(new IItemColor() {
				@Override
				public int getColor(ItemStack stack, int tint) {
					if (stack != null && stack.hasTag()) {
						if (stack.getTag().contains("stored_state")) {
							BlockState stored = NBTUtil.readBlockState(NBTHelper.getTag(stack, "stored_state"));
							ItemStack colorStack = new ItemStack(stored.getBlock());
							if (colorStack.getItem() != null) {
								return Minecraft.getInstance().getItemColors().getColor(colorStack, tint);
							}
						}
					}
					return 16777215;
				}
			}, DecorBlocks.colorizerBlocks());

			items.register(new IItemColor() {
				@Override
				public int getColor(ItemStack stack, int tint) {
					if (stack != null && stack.hasTag()) {
						if (stack.getTag().contains("stored_state")) {
							BlockState stored = NBTUtil.readBlockState(NBTHelper.getTag(stack, "stored_state"));
							ItemStack colorStack = new ItemStack(stored.getBlock());
							if (colorStack.getItem() != null && !(colorStack.getItem() instanceof AirItem)) {
								return Minecraft.getInstance().getItemColors().getColor(colorStack, tint);
							}
						}
					}
					return 16777215;
				}
			}, DecorItems.COLORIZER_BRUSH.get());

			blocks.register(new IBlockColor() {
				@Override
				public int getColor(BlockState state, IBlockDisplayReader worldIn, BlockPos pos, int tint) {
					return state.getBlock().defaultMaterialColor().col;
				}
			}, DecorBlocks.fluroBlocks());

			items.register(new IItemColor() {
				@Override
				public int getColor(ItemStack stack, int tint) {
					Block b = Block.byItem(stack.getItem());
					if (b != Blocks.AIR) {
						return b.defaultMaterialColor().col;
					}
					return 16777215;
				}
			}, DecorBlocks.fluroBlocks());
		});
	}

	@Override
	public PlayerEntity getClientPlayer() {
		return Minecraft.getInstance().player;
	}

	@Override
	public void openNeonSign(NeonSignTileEntity tile) {
		Minecraft.getInstance().setScreen(new NeonSignScreen(tile));
	}

	@Override
	public void handleOpenNeonSign(BlockPos pos) {
		TileEntity tileentity = Minecraft.getInstance().player.getCommandSenderWorld().getBlockEntity(pos);

		// Make sure TileEntity exists
		if (!(tileentity instanceof NeonSignTileEntity)) {
			tileentity = new NeonSignTileEntity();
			tileentity.setLevelAndPosition(Minecraft.getInstance().player.getCommandSenderWorld(), pos);
		}

		openNeonSign((NeonSignTileEntity) tileentity);
	}
}
