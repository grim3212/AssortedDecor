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
	}

	@Override
	public void act(DirectoryCache cache) throws IOException {
		Map<ResourceLocation, LootTable.Builder> tables = new HashMap<>();

		for (Block b : blocks) {
			tables.put(b.getRegistryName(), genRegular(b));
		}

		for (Map.Entry<ResourceLocation, LootTable.Builder> e : tables.entrySet()) {
			Path path = getPath(generator.getOutputFolder(), e.getKey());
			IDataProvider.save(GSON, cache, LootTableManager.toJson(e.getValue().setParameterSet(LootParameterSets.BLOCK).build()), path);
		}

		Path doorPath = getPath(generator.getOutputFolder(), DecorBlocks.COLORIZER_DOOR.get().getRegistryName());
		IDataProvider.save(GSON, cache, LootTableManager.toJson(genDoor(DecorBlocks.COLORIZER_DOOR.get()).setParameterSet(LootParameterSets.BLOCK).build()), doorPath);

		Path slabPath = getPath(generator.getOutputFolder(), DecorBlocks.COLORIZER_SLAB.get().getRegistryName());
		IDataProvider.save(GSON, cache, LootTableManager.toJson(genSlab(DecorBlocks.COLORIZER_SLAB.get()).setParameterSet(LootParameterSets.BLOCK).build()), slabPath);
		
		Path verticalSlabPath = getPath(generator.getOutputFolder(), DecorBlocks.COLORIZER_VERTICAL_SLAB.get().getRegistryName());
		IDataProvider.save(GSON, cache, LootTableManager.toJson(genVerticalSlab(DecorBlocks.COLORIZER_VERTICAL_SLAB.get()).setParameterSet(LootParameterSets.BLOCK).build()), verticalSlabPath);
	}

	private static Path getPath(Path root, ResourceLocation id) {
		return root.resolve("data/" + id.getNamespace() + "/loot_tables/blocks/" + id.getPath() + ".json");
	}

	private static LootTable.Builder genRegular(Block b) {
		LootEntry.Builder<?> entry = ItemLootEntry.builder(b);
		LootPool.Builder pool = LootPool.builder().name("main").rolls(ConstantRange.of(1)).addEntry(entry).acceptCondition(SurvivesExplosion.builder());
		return LootTable.builder().addLootPool(pool);
	}

	private static LootTable.Builder genDoor(Block b) {
		BlockStateProperty.Builder halfCondition = BlockStateProperty.builder(b).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withProp(DoorBlock.HALF, DoubleBlockHalf.LOWER));
		LootEntry.Builder<?> entry = ItemLootEntry.builder(b).acceptCondition(halfCondition);
		LootPool.Builder pool = LootPool.builder().name("main").rolls(ConstantRange.of(1)).addEntry(entry).acceptCondition(SurvivesExplosion.builder());
		return LootTable.builder().addLootPool(pool);
	}

	private static LootTable.Builder genSlab(Block b) {
		LootEntry.Builder<?> entry = ItemLootEntry.builder(b).acceptFunction(ExplosionDecay.builder()).acceptFunction(SetCount.builder(ConstantRange.of(2)).acceptCondition(BlockStateProperty.builder(b).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withProp(SlabBlock.TYPE, SlabType.DOUBLE))));
		LootPool.Builder pool = LootPool.builder().name("main").rolls(ConstantRange.of(1)).addEntry(entry);
		return LootTable.builder().addLootPool(pool);
	}
	
	private static LootTable.Builder genVerticalSlab(Block b) {
		LootEntry.Builder<?> entry = ItemLootEntry.builder(b).acceptFunction(ExplosionDecay.builder()).acceptFunction(SetCount.builder(ConstantRange.of(2)).acceptCondition(BlockStateProperty.builder(b).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withProp(ColorizerVerticalSlabBlock.TYPE, VerticalSlabType.DOUBLE))));
		LootPool.Builder pool = LootPool.builder().name("main").rolls(ConstantRange.of(1)).addEntry(entry);
		return LootTable.builder().addLootPool(pool);
	}

	@Override
	public String getName() {
		return "Assorted Decor loot tables";
	}

}
