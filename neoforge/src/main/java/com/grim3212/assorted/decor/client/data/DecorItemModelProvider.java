package com.grim3212.assorted.decor.client.data;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.client.color.ColorizerItemTintSource;
import com.grim3212.assorted.decor.client.model.ColorizerItemModel;
import com.grim3212.assorted.decor.common.items.DecorItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.stream.Stream;

/**
 * Forge's {@code ItemModelProvider} / {@code ItemModelBuilder} / {@code ExistingFileHelper} /
 * {@code ForgeRegistries} are all gone, and so is the idea that an item model is one json: an item
 * points at a data driven {@code ItemModel} in {@code assets/<ns>/items/}, which names the
 * {@code assets/<ns>/models/} geometry to draw. {@link ItemModelGenerators} writes both halves, so
 * {@code generatedItem} is just {@link ItemModelGenerators#generateFlatItem}.
 * <p>
 * Block items are not listed here at all - they belong to {@link DecorBlockstateProvider}, which
 * either points each one at its block model or registers a flat sprite for it. Because one
 * {@link ModelProvider} writes both halves, the two providers are kept apart by narrowing what each
 * claims to know about; see {@link DecorBlockstateProvider} for the other side of the split.
 */
public class DecorItemModelProvider extends ModelProvider {

    public DecorItemModelProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted Decor item models";
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return super.getKnownItems().filter(holder -> !(holder.value() instanceof BlockItem));
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        generatedItem(itemModels, DecorItems.WALLPAPER.get());
        generatedItem(itemModels, DecorItems.WOOD_FRAME.get());
        generatedItem(itemModels, DecorItems.IRON_FRAME.get());
        generatedItem(itemModels, DecorItems.UNFIRED_PLANTER_POT.get());
        generatedItem(itemModels, DecorItems.UNFIRED_CLAY_DECORATION.get());
        // DecorItems.NEON_SIGN is a StandingAndWallBlockItem, so it is a BlockItem and belongs to
        // DecorBlockstateProvider; listing it here too would write items/neon_sign.json twice.
        generatedItem(itemModels, DecorItems.TARBALL.get());
        generatedItem(itemModels, DecorItems.ASPHALT.get());
        generatedItem(itemModels, DecorItems.CHAIN_LINK.get());

        handheldItem(itemModels, DecorItems.PAINT_ROLLER.get());
        DecorItems.PAINT_ROLLER_COLORS.forEach((color, roller) -> handheldItem(itemModels, roller.get()));

        colorizerBrush(itemModels);
    }

    /**
     * The brush is the one item in this mod drawn by the colorizer model loader: its bristles read the
     * {@code #stored} texture of whatever block state it has picked up. The 1.20.1 provider built this
     * from {@code DecorBlockstateProvider}, because the colorizer builder belonged to the second
     * {@code ModelProvider}; a custom loader is a {@link ModelTemplate} now, so it belongs here with
     * the rest of the item models.
     * <p>
     * The tint source is what replaced the deleted {@code registerItemColor} handler: an item's tints
     * are a list in its item model json and a source's position in that list is the tint index it
     * answers for. Index 0 is the index {@code item/brush} stamps on its bristle faces.
     */
    private void colorizerBrush(ItemModelGenerators itemModels) {
        Item item = DecorItems.COLORIZER_BRUSH.get();
        ModelTemplate template = DecorBlockstateProvider.colorizerTemplate(
                resource("item/brush"), builder -> builder.addTexture("handle", resource("block/brush_handle")));

        Identifier model = template.create(modelId("item/" + name(item)), DecorBlockstateProvider.colorizerParticle(), itemModels.modelOutput);
        // ItemModelUtils#tintedModel would emit a minecraft:model, which bakes the colorizer's
        // geometry once with no stored block and so always draws an empty brush.
        itemModels.itemModelOutput.accept(item, new ColorizerItemModel.Unbaked(model, List.of(new ColorizerItemTintSource())));
    }

    private void generatedItem(ItemModelGenerators itemModels, Item item) {
        flatItem(itemModels, item, ModelTemplates.FLAT_ITEM);
    }

    private void handheldItem(ItemModelGenerators itemModels, Item item) {
        flatItem(itemModels, item, ModelTemplates.FLAT_HANDHELD_ITEM);
    }

    /**
     * Every flat item in this mod keeps its texture at {@code item/<id>}, which is what
     * {@link ItemModelGenerators#generateFlatItem} derives on its own, so there is no need for the
     * hand built {@link TextureMapping} that a texture subfolder would force.
     */
    private void flatItem(ItemModelGenerators itemModels, Item item, ModelTemplate template) {
        itemModels.generateFlatItem(item, template);
    }

    private static String name(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).getPath();
    }

    private static Identifier modelId(String path) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, path);
    }

    private static Identifier resource(String path) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, path);
    }
}
