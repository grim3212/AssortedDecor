package com.grim3212.assorted.decor.common.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grim3212.assorted.decor.common.block.ColorChangingBlock;
import com.grim3212.assorted.decor.common.block.DecorBlocks;
import com.grim3212.assorted.decor.common.block.FluroBlock;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerVerticalSlabBlock;
import com.grim3212.assorted.decor.common.util.VerticalSlabType;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState.Builder;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.ForgeRegistries;

public class DecorLootProvider implements DataProvider {

	private final DataGenerator generator;
	private final List<Block> blocks = new ArrayList<>();

	public DecorLootProvider(DataGenerator generator) {
		this.generator = generator;

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

	private ResourceLocation key(Block b) {
		return ForgeRegistries.BLOCKS.getKey(b);
	}

	@Override
	public void run(CachedOutput cache) throws IOException {
		Map<ResourceLocation, LootTable.Builder> tables = new HashMap<>();

		for (Block b : blocks) {
			tables.put(key(b), genRegular(b));
		}

		tables.put(DecorBlocks.SIDING_VERTICAL.getId(), genColor(DecorBlocks.SIDING_VERTICAL.get()));
		tables.put(DecorBlocks.SIDING_HORIZONTAL.getId(), genColor(DecorBlocks.SIDING_HORIZONTAL.get()));

		for (Map.Entry<ResourceLocation, LootTable.Builder> e : tables.entrySet()) {
			Path path = getPath(generator.getOutputFolder(), e.getKey());
			DataProvider.saveStable(cache, LootTables.serialize(e.getValue().setParamSet(LootContextParamSets.BLOCK).build()), path);
		}

		Path doorPath = getPath(generator.getOutputFolder(), DecorBlocks.COLORIZER_DOOR.getId());
		DataProvider.saveStable(cache, LootTables.serialize(genDoor(DecorBlocks.COLORIZER_DOOR.get()).setParamSet(LootContextParamSets.BLOCK).build()), doorPath);

		Path slabPath = getPath(generator.getOutputFolder(), DecorBlocks.COLORIZER_SLAB.getId());
		DataProvider.saveStable(cache, LootTables.serialize(genSlab(DecorBlocks.COLORIZER_SLAB.get()).setParamSet(LootContextParamSets.BLOCK).build()), slabPath);

		Path verticalSlabPath = getPath(generator.getOutputFolder(), DecorBlocks.COLORIZER_VERTICAL_SLAB.getId());
		DataProvider.saveStable(cache, LootTables.serialize(genVerticalSlab(DecorBlocks.COLORIZER_VERTICAL_SLAB.get()).setParamSet(LootContextParamSets.BLOCK).build()), verticalSlabPath);

		Path quartzDoorPath = getPath(generator.getOutputFolder(), DecorBlocks.QUARTZ_DOOR.getId());
		DataProvider.saveStable(cache, LootTables.serialize(genDoor(DecorBlocks.QUARTZ_DOOR.get()).setParamSet(LootContextParamSets.BLOCK).build()), quartzDoorPath);

		Path chainLinkDoorPath = getPath(generator.getOutputFolder(), DecorBlocks.CHAIN_LINK_DOOR.getId());
		DataProvider.saveStable(cache, LootTables.serialize(genDoor(DecorBlocks.CHAIN_LINK_DOOR.get()).setParamSet(LootContextParamSets.BLOCK).build()), chainLinkDoorPath);

		Path glassDoorPath = getPath(generator.getOutputFolder(), DecorBlocks.GLASS_DOOR.getId());
		DataProvider.saveStable(cache, LootTables.serialize(genDoor(DecorBlocks.GLASS_DOOR.get()).setParamSet(LootContextParamSets.BLOCK).build()), glassDoorPath);

		Path steelDoorPath = getPath(generator.getOutputFolder(), DecorBlocks.STEEL_DOOR.getId());
		DataProvider.saveStable(cache, LootTables.serialize(genDoor(DecorBlocks.STEEL_DOOR.get()).setParamSet(LootContextParamSets.BLOCK).build()), steelDoorPath);
	}

	private static Path getPath(Path root, ResourceLocation id) {
		return root.resolve("data/" + id.getNamespace() + "/loot_tables/blocks/" + id.getPath() + ".json");
	}

	private static LootTable.Builder genRegular(Block b) {
		LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(b);
		LootPool.Builder pool = LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(1)).add(entry).when(ExplosionCondition.survivesExplosion());
		return LootTable.lootTable().withPool(pool);
	}

	private static LootTable.Builder genDoor(Block b) {
		LootItemBlockStatePropertyCondition.Builder halfCondition = LootItemBlockStatePropertyCondition.hasBlockStateProperties(b).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoorBlock.HALF, DoubleBlockHalf.LOWER));
		LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(b).when(halfCondition);
		LootPool.Builder pool = LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(1)).add(entry).when(ExplosionCondition.survivesExplosion());
		return LootTable.lootTable().withPool(pool);
	}

	private static LootTable.Builder genSlab(Block b) {
		LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(b).apply(ApplyExplosionDecay.explosionDecay()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(b).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, SlabType.DOUBLE))));
		LootPool.Builder pool = LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(1)).add(entry);
		return LootTable.lootTable().withPool(pool);
	}

	private static LootTable.Builder genVerticalSlab(Block b) {
		LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(b).apply(ApplyExplosionDecay.explosionDecay()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(b).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ColorizerVerticalSlabBlock.TYPE, VerticalSlabType.DOUBLE))));
		LootPool.Builder pool = LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(1)).add(entry);
		return LootTable.lootTable().withPool(pool);
	}

	private static LootTable.Builder genColor(Block b) {
		LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(b);
		Builder func = CopyBlockState.copyState(b).copy(ColorChangingBlock.COLOR);
		LootPool.Builder pool = LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(1)).add(entry).when(ExplosionCondition.survivesExplosion()).apply(func);
		return LootTable.lootTable().withPool(pool);
	}

	@Override
	public String getName() {
		return "Assorted Decor loot tables";
	}

}
