package com.grim3212.assorted.decor.client.data;

import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.block.DecorBlocks;
import com.grim3212.assorted.decor.common.item.DecorItems;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class DecorItemModelProvider extends ItemModelProvider {

	public DecorItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, AssortedDecor.MODID, existingFileHelper);
	}

	@Override
	public String getName() {
		return "Assorted Decor item models";
	}

	@Override
	protected void registerModels() {
		generatedItem(DecorItems.WALLPAPER.get());
		generatedItem(DecorItems.WOOD_FRAME.get());
		generatedItem(DecorItems.IRON_FRAME.get());
		generatedItem(DecorItems.UNFIRED_PLANTER_POT.get());
		generatedItem(DecorItems.UNFIRED_CLAY_DECORATION.get());
		generatedItem(DecorItems.NEON_SIGN.get());
		generatedItem(DecorBlocks.CALENDAR.get().asItem());
		generatedItem(DecorBlocks.WALL_CLOCK.get().asItem());
		generatedItem(DecorBlocks.QUARTZ_DOOR.get().asItem());
		generatedItem(DecorBlocks.CHAIN_LINK_DOOR.get().asItem());
		generatedItem(DecorBlocks.GLASS_DOOR.get().asItem());
		generatedItem(DecorBlocks.STEEL_DOOR.get().asItem());
		generatedItem(DecorItems.TARBALL.get());
		generatedItem(DecorItems.ASPHALT.get());
		generatedItem(DecorItems.CHAIN_LINK.get());

		String chainFenceName = name(DecorBlocks.CHAIN_LINK_FENCE.get().asItem());
		withExistingParent(chainFenceName, "item/generated").texture("layer0", prefix("block/chain_link_door_bottom"));

		handheldItem(DecorItems.PAINT_ROLLER.get());

		DecorItems.PAINT_ROLLER_COLORS.forEach((c, r) -> handheldItem(r.get()));
	}

	private ItemModelBuilder handheldItem(String name) {
		return withExistingParent(name, "item/handheld").texture("layer0", prefix("item/" + name));
	}

	private ItemModelBuilder handheldItem(Item i) {
		return handheldItem(name(i));
	}

	private ItemModelBuilder generatedItem(String name) {
		return withExistingParent(name, "item/generated").texture("layer0", prefix("item/" + name));
	}

	private ItemModelBuilder generatedItem(Item i) {
		return generatedItem(name(i));
	}

	private static String name(Item i) {
		return ForgeRegistries.ITEMS.getKey(i).getPath();
	}

	private ResourceLocation prefix(String name) {
		return new ResourceLocation(AssortedDecor.MODID, name);
	}
}
