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

import net.minecraft.block.Block;
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
import net.minecraft.loot.conditions.SurvivesExplosion;
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
		blocks.add(DecorBlocks.COLORIZER_SLAB.get());
		blocks.add(DecorBlocks.COLORIZER_DOOR.get());
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
	}

	private static Path getPath(Path root, ResourceLocation id) {
		return root.resolve("data/" + id.getNamespace() + "/loot_tables/blocks/" + id.getPath() + ".json");
	}

	private static LootTable.Builder genRegular(Block b) {
		LootEntry.Builder<?> entry = ItemLootEntry.builder(b);
		LootPool.Builder pool = LootPool.builder().name("main").rolls(ConstantRange.of(1)).addEntry(entry).acceptCondition(SurvivesExplosion.builder());
		return LootTable.builder().addLootPool(pool);
	}

	@Override
	public String getName() {
		return "Assorted Storage loot tables";
	}

}
