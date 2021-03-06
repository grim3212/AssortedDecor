package com.grim3212.assorted.decor.common.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grim3212.assorted.decor.common.block.DecorBlocks;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerVerticalSlabBlock;
import com.grim3212.assorted.decor.common.util.VerticalSlabType;

import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableManager;
import net.minecraft.loot.conditions.BlockStateProperty;
import net.minecraft.loot.conditions.SurvivesExplosion;
import net.minecraft.loot.functions.ExplosionDecay;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.ResourceLocation;

public class DecorLootProvider implements IDataProvider {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private final DataGenerator generator;
	private final List<Block> blocks = new ArrayList<>();

	public DecorLootProvider(DataGenerator generator) {
		this.generator = generator;

		blocks.add(DecorBlocks.PLANTER_POT.get());

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
		blocks.add(DecorBlocks.CALENDAR.get());
		blocks.add(DecorBlocks.WALL_CLOCK.get());

		for (Block b : DecorBlocks.fluroBlocks())
			blocks.add(b);
	}

	@Override
	public void run(DirectoryCache cache) throws IOException {
		Map<ResourceLocation, LootTable.Builder> tables = new HashMap<>();

		for (Block b : blocks) {
			tables.put(b.getRegistryName(), genRegular(b));
		}

		for (Map.Entry<ResourceLocation, LootTable.Builder> e : tables.entrySet()) {
			Path path = getPath(generator.getOutputFolder(), e.getKey());
			IDataProvider.save(GSON, cache, LootTableManager.serialize(e.getValue().setParamSet(LootParameterSets.BLOCK).build()), path);
		}

		Path doorPath = getPath(generator.getOutputFolder(), DecorBlocks.COLORIZER_DOOR.get().getRegistryName());
		IDataProvider.save(GSON, cache, LootTableManager.serialize(genDoor(DecorBlocks.COLORIZER_DOOR.get()).setParamSet(LootParameterSets.BLOCK).build()), doorPath);

		Path slabPath = getPath(generator.getOutputFolder(), DecorBlocks.COLORIZER_SLAB.get().getRegistryName());
		IDataProvider.save(GSON, cache, LootTableManager.serialize(genSlab(DecorBlocks.COLORIZER_SLAB.get()).setParamSet(LootParameterSets.BLOCK).build()), slabPath);

		Path verticalSlabPath = getPath(generator.getOutputFolder(), DecorBlocks.COLORIZER_VERTICAL_SLAB.get().getRegistryName());
		IDataProvider.save(GSON, cache, LootTableManager.serialize(genVerticalSlab(DecorBlocks.COLORIZER_VERTICAL_SLAB.get()).setParamSet(LootParameterSets.BLOCK).build()), verticalSlabPath);

		Path quartzDoorPath = getPath(generator.getOutputFolder(), DecorBlocks.QUARTZ_DOOR.get().getRegistryName());
		IDataProvider.save(GSON, cache, LootTableManager.serialize(genDoor(DecorBlocks.QUARTZ_DOOR.get()).setParamSet(LootParameterSets.BLOCK).build()), quartzDoorPath);
	}

	private static Path getPath(Path root, ResourceLocation id) {
		return root.resolve("data/" + id.getNamespace() + "/loot_tables/blocks/" + id.getPath() + ".json");
	}

	private static LootTable.Builder genRegular(Block b) {
		LootEntry.Builder<?> entry = ItemLootEntry.lootTableItem(b);
		LootPool.Builder pool = LootPool.lootPool().name("main").setRolls(ConstantRange.exactly(1)).add(entry).when(SurvivesExplosion.survivesExplosion());
		return LootTable.lootTable().withPool(pool);
	}

	private static LootTable.Builder genDoor(Block b) {
		BlockStateProperty.Builder halfCondition = BlockStateProperty.hasBlockStateProperties(b).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoorBlock.HALF, DoubleBlockHalf.LOWER));
		LootEntry.Builder<?> entry = ItemLootEntry.lootTableItem(b).when(halfCondition);
		LootPool.Builder pool = LootPool.lootPool().name("main").setRolls(ConstantRange.exactly(1)).add(entry).when(SurvivesExplosion.survivesExplosion());
		return LootTable.lootTable().withPool(pool);
	}

	private static LootTable.Builder genSlab(Block b) {
		LootEntry.Builder<?> entry = ItemLootEntry.lootTableItem(b).apply(ExplosionDecay.explosionDecay()).apply(SetCount.setCount(ConstantRange.exactly(2)).when(BlockStateProperty.hasBlockStateProperties(b).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, SlabType.DOUBLE))));
		LootPool.Builder pool = LootPool.lootPool().name("main").setRolls(ConstantRange.exactly(1)).add(entry);
		return LootTable.lootTable().withPool(pool);
	}

	private static LootTable.Builder genVerticalSlab(Block b) {
		LootEntry.Builder<?> entry = ItemLootEntry.lootTableItem(b).apply(ExplosionDecay.explosionDecay()).apply(SetCount.setCount(ConstantRange.exactly(2)).when(BlockStateProperty.hasBlockStateProperties(b).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ColorizerVerticalSlabBlock.TYPE, VerticalSlabType.DOUBLE))));
		LootPool.Builder pool = LootPool.lootPool().name("main").setRolls(ConstantRange.exactly(1)).add(entry);
		return LootTable.lootTable().withPool(pool);
	}

	@Override
	public String getName() {
		return "Assorted Decor loot tables";
	}

}
