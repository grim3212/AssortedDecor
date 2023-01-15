package com.grim3212.assorted.decor.common.creative;

import java.util.Arrays;

import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.block.ColorChangingBlock;
import com.grim3212.assorted.decor.common.block.DecorBlocks;
import com.grim3212.assorted.decor.common.item.DecorItems;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;

public class DecorCreativeTab {

	public static void registerTabs(final CreativeModeTabEvent.Register event) {
		event.registerCreativeModeTab(new ResourceLocation(AssortedDecor.MODID, "tab"), builder -> builder.title(Component.translatable("itemGroup.assorteddecor")).icon(() -> new ItemStack(DecorItems.WALLPAPER.get())).displayItems((enabledFlags, populator, hasPermissions) -> {
			populator.acceptAll(DecorItems.ITEMS.getEntries().stream().map(itm -> new ItemStack(itm.get())).toList());

			Arrays.stream(DyeColor.values()).forEach(x -> {
				if (x == DyeColor.WHITE)
					return;

				populator.accept(ColorChangingBlock.getColorStack(new ItemStack(DecorBlocks.SIDING_VERTICAL.get()), x));
				populator.accept(ColorChangingBlock.getColorStack(new ItemStack(DecorBlocks.SIDING_HORIZONTAL.get()), x));
			});
		}));
	}

}
