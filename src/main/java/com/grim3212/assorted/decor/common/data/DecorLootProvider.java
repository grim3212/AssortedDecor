package com.grim3212.assorted.decor.common.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.grim3212.assorted.decor.common.block.ColorChangingBlock;
import com.grim3212.assorted.decor.common.block.DecorBlocks;
import com.grim3212.assorted.decor.common.block.FluroBlock;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerVerticalSlabBlock;
import com.grim3212.assorted.decor.common.util.VerticalSlabType;
import com.mojang.datafixers.util.Pair;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class DecorLootProvider extends LootTableProvider {

	public DecorLootProvider(DataGenerator generator) {
		super(generator);
	}

	@Override
	protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
		map.forEach((location, lootTable) -> {
			LootTables.validate(validationtracker, location, lootTable);
		});
	}

	@Override
	protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
		return ImmutableList.of(Pair.of(BlockTables::new, LootContextParamSets.BLOCK));
	}

	@Override
	public String getName() {
		return "Assorted Decor loot tables";
	}

	private class BlockTables extends BlockLoot {

		private final List<Block> blocks = new ArrayList<>();

		public BlockTables() {
			blocks.add(DecorBlocks.PLANTER_POT.get());
			blocks.add(DecorBlocks.CLAY_DECORATION.get());
			blocks.add(DecorBlocks.BONE_DECORATION.get());
			blocks.add(DecorBlocks.PAPER_LANTERN.get());
			blocks.add(DecorBlocks.BONE_LANTERN.get());
			blocks.add(DecorBlocks.IRON_LANTERN.get());
			blocks.add(DecorBlocks.SIDEWALK.get());
			blocks.add(DecorBlocks.ROADWAY.get());
			blocks.add(DecorBlocks.ROADWAY_LIGHT.get());
			blocks.add(DecorBlocks.ROADWAY_MANHOLE.get());
			blocks.add(DecorBlocks.CHAIN_LINK_FENCE.get());
			blocks.add(DecorBlocks.FOUNTAIN.get());
			blocks.add(DecorBlocks.DECORATIVE_STONE.get());
			blocks.add(DecorBlocks.STONE_PATH.get());
			blocks.add(DecorBlocks.CAGE.get());

			DecorBlocks.ROADWAY_COLORS.forEach((c, r) -> blocks.add(r.get()));

			blocks.add(DecorBlocks.COLORIZER.get());
			blocks.add(DecorBlocks.COLORIZER_CHAIR.get());
			blocks.add(DecorBlocks.COLORIZER_TABLE.get());
			blocks.add(DecorBlocks.COLORIZER_STOOL.get());
			blocks.add(DecorBlocks.COLORIZER_COUNTER.get());
			blocks.add(DecorBlocks.COLORIZER_FENCE.get());
			blocks.add(DecorBlocks.COLORIZER_FENCE_GATE.get());
			blocks.add(DecorBlocks.COLORIZER_WALL.get());
			blocks.add(DecorBlocks.COLORIZER_STAIRS.get());
			blocks.add(DecorBlocks.COLORIZER_TRAP_DOOR.get());
			blocks.add(DecorBlocks.COLORIZER_LAMP_POST.get());
			blocks.add(DecorBlocks.COLORIZER_SLOPE.get());
			blocks.add(DecorBlocks.COLORIZER_SLOPED_ANGLE.get());
			blocks.add(DecorBlocks.COLORIZER_SLOPED_INTERSECTION.get());
			blocks.add(DecorBlocks.COLORIZER_SLOPED_POST.get());
			blocks.add(DecorBlocks.COLORIZER_OBLIQUE_SLOPE.get());
			blocks.add(DecorBlocks.COLORIZER_CORNER.get());
			blocks.add(DecorBlocks.COLORIZER_SLANTED_CORNER.get());
			blocks.add(DecorBlocks.COLORIZER_PYRAMID.get());
			blocks.add(DecorBlocks.COLORIZER_FULL_PYRAMID.get());

			blocks.add(DecorBlocks.COLORIZER_CHIMNEY.get());
			blocks.add(DecorBlocks.COLORIZER_FIREPLACE.get());
			blocks.add(DecorBlocks.COLORIZER_FIREPIT.get());
			blocks.add(DecorBlocks.COLORIZER_FIREPIT_COVERED.get());
			blocks.add(DecorBlocks.COLORIZER_FIRERING.get());
			blocks.add(DecorBlocks.COLORIZER_STOVE.get());

			blocks.add(DecorBlocks.NEON_SIGN.get());

			blocks.add(DecorBlocks.ILLUMINATION_TUBE.get());
			blocks.add(DecorBlocks.ILLUMINATION_PLATE.get());
			blocks.add(DecorBlocks.CALENDAR.get());
			blocks.add(DecorBlocks.WALL_CLOCK.get());

			FluroBlock.FLURO_BY_DYE.entrySet().stream().forEach((x) -> blocks.add(x.getValue().get()));
		}

		@Override
		protected void addTables() {
			for (Block b : blocks) {
				this.dropSelf(b);
			}

			this.add(DecorBlocks.COLORIZER_DOOR.get(), BlockLoot::createDoorTable);
			this.add(DecorBlocks.QUARTZ_DOOR.get(), BlockLoot::createDoorTable);
			this.add(DecorBlocks.CHAIN_LINK_DOOR.get(), BlockLoot::createDoorTable);
			this.add(DecorBlocks.GLASS_DOOR.get(), BlockLoot::createDoorTable);
			this.add(DecorBlocks.STEEL_DOOR.get(), BlockLoot::createDoorTable);

			this.add(DecorBlocks.COLORIZER_SLAB.get(), BlockLoot::createSlabItemTable);
			this.add(DecorBlocks.COLORIZER_VERTICAL_SLAB.get(), BlockTables::createVerticalSlabItemTable);

			this.add(DecorBlocks.SIDING_VERTICAL.get(), BlockTables::createColorTable);
			this.add(DecorBlocks.SIDING_HORIZONTAL.get(), BlockTables::createColorTable);

		}

		private static LootTable.Builder createVerticalSlabItemTable(Block b) {
			return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(applyExplosionDecay(b, LootItem.lootTableItem(b).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(b).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ColorizerVerticalSlabBlock.TYPE, VerticalSlabType.DOUBLE)))))));
		}

		private static LootTable.Builder createColorTable(Block b) {
			LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(b);
			CopyBlockState.Builder func = CopyBlockState.copyState(b).copy(ColorChangingBlock.COLOR);
			LootPool.Builder pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(entry).when(ExplosionCondition.survivesExplosion()).apply(func);
			return LootTable.lootTable().withPool(pool);
		}

		@Override
		protected Iterable<Block> getKnownBlocks() {
			return DecorBlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList());
		}
	}

}
