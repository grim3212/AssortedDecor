package com.grim3212.assorted.decor.client.proxy;

import com.grim3212.assorted.decor.client.model.ColorizerModel;
import com.grim3212.assorted.decor.client.model.ColorizerOBJModel;
import com.grim3212.assorted.decor.client.render.entity.FrameRenderer;
import com.grim3212.assorted.decor.client.render.entity.WallpaperRenderer;
import com.grim3212.assorted.decor.common.block.DecorBlocks;
import com.grim3212.assorted.decor.common.block.tileentity.ColorizerTileEntity;
import com.grim3212.assorted.decor.common.entity.DecorEntityTypes;
import com.grim3212.assorted.decor.common.proxy.IProxy;
import com.grim3212.assorted.decor.common.util.NBTHelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

public class ClientProxy implements IProxy {

	@Override
	public void starting() {
		final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(this::setupClient);
		modBus.addListener(this::loadComplete);

		if (Minecraft.getInstance() != null) {
			ModelLoaderRegistry.registerLoader(ColorizerModel.ColorizerLoader.LOCATION, new ColorizerModel.ColorizerLoader());
			ModelLoaderRegistry.registerLoader(ColorizerOBJModel.ColorizerOBJLoader.LOCATION, new ColorizerOBJModel.ColorizerOBJLoader());
		}
	}

	private void setupClient(final FMLClientSetupEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(DecorEntityTypes.WALLPAPER.get(), WallpaperRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(DecorEntityTypes.FRAME.get(), FrameRenderer::new);
	}

	public void loadComplete(final FMLLoadCompleteEvent event) {
		event.enqueueWork(() -> {
			ItemColors items = Minecraft.getInstance().getItemColors();
			BlockColors blocks = Minecraft.getInstance().getBlockColors();

			blocks.register(new IBlockColor() {
				@Override
				public int getColor(BlockState state, IBlockDisplayReader worldIn, BlockPos pos, int tint) {
					if (pos != null) {
						TileEntity te = worldIn.getTileEntity(pos);
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
						if (stack.getTag().contains("registryName")) {
							Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(NBTHelper.getString(stack, "registryName")));
							ItemStack colorStack = new ItemStack(block);
							if (colorStack.getItem() != null) {
								return Minecraft.getInstance().getItemColors().getColor(colorStack, tint);
							}
						}
					}
					return 16777215;
				}
			}, DecorBlocks.colorizerBlocks());
		});
	}
}
