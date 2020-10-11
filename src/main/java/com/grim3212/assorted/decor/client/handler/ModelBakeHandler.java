package com.grim3212.assorted.decor.client.handler;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.grim3212.assorted.decor.common.block.DecorBlocks;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class ModelBakeHandler {

	public static void onModelBakeEvent(final ModelBakeEvent event) {
		Map<ResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();

		try {
			for (Block b : colorizerBlocks()) {
				ResourceLocation resourceLocation = ForgeRegistries.BLOCKS.getKey(b);
				ResourceLocation unbakedModelLoc = new ResourceLocation(resourceLocation.getNamespace(), "block/" + resourceLocation.getPath());

				BlockModel model = (BlockModel) event.getModelLoader().getUnbakedModel(unbakedModelLoc);
				//IBakedModel customModel = new ColorizerModel(event.getModelLoader(), model, model.bakeModel(event.getModelLoader(), model, ModelLoader.defaultTextureGetter(), ModelRotation.X180_Y180, unbakedModelLoc, true));

				b.getStateContainer().getValidStates().forEach(state -> {
				//	modelRegistry.put(BlockModelShapes.getModelLocation(state), customModel);
				});

				//modelRegistry.put(new ModelResourceLocation(resourceLocation, "inventory"), customModel);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static List<Block> colorizerBlocks() {
		return Arrays.asList(DecorBlocks.COLORIZER.get(), DecorBlocks.COLORIZER_CHAIR.get());
	}
}
