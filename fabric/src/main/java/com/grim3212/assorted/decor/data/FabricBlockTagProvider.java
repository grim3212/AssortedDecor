package com.grim3212.assorted.decor.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.VanillaBlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class FabricBlockTagProvider extends VanillaBlockTagsProvider {

    private final DecorCommonTagProvider.BlockTagProvider commonBlocks;

    public FabricBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
        this.commonBlocks = new DecorCommonTagProvider.BlockTagProvider(output, lookup);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        this.commonBlocks.addCommonTags(this::tag);
    }
}
