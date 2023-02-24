package com.grim3212.assorted.decor.data;

import com.grim3212.assorted.decor.Constants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ForgeItemTagProvider extends ItemTagsProvider {

    private final DecorCommonTagProvider.ItemTagProvider commonItems;

    public ForgeItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, TagsProvider<Block> blockTags, ExistingFileHelper existingFileHelper) {
        super(output, lookup, blockTags, Constants.MOD_ID, existingFileHelper);
        this.commonItems = new DecorCommonTagProvider.ItemTagProvider(output, lookup, blockTags);
    }

    @Override
    protected void addTags(Provider provider) {
        this.commonItems.addCommonTags(this::tag, (pair) -> this.copy(pair.getA(), pair.getB()));
    }

    @Override
    public String getName() {
        return "Assorted Decor item tags";
    }
}
