package com.grim3212.assorted.decor.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.tags.VanillaItemTagsProvider;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class FabricItemTagProvider extends VanillaItemTagsProvider {

    private final DecorCommonTagProvider.ItemTagProvider commonItems;

    public FabricItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, TagsProvider<Block> blockTagsProvider) {
        super(output, lookup, blockTagsProvider);
        this.commonItems = new DecorCommonTagProvider.ItemTagProvider(output, lookup, blockTagsProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        this.commonItems.addCommonTags(this::tag, (pair) -> this.copy(pair.getA(), pair.getB()));
    }
}
