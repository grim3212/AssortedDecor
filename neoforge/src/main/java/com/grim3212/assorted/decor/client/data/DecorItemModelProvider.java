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

/** Item models for everything but block items, which {@link DecorBlockstateProvider} models. */
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
        generatedItem(itemModels, DecorItems.GATE_GRATING.get());
        generatedItem(itemModels, DecorItems.GARAGE_PANEL.get());
        handheldItem(itemModels, DecorItems.GATE_TRUMPET.get());
        generatedItem(itemModels, DecorItems.GARAGE_REMOTE.get());

        handheldItem(itemModels, DecorItems.PAINT_ROLLER.get());
        DecorItems.PAINT_ROLLER_COLORS.forEach((color, roller) -> handheldItem(itemModels, roller.get()));

        colorizerBrush(itemModels);
    }

    /**
     * The brush, drawn by the colorizer model loader: its bristles take the {@code #stored} texture
     * of the block it has picked up. Tint index 0, which {@code item/brush} stamps on the bristle
     * faces, colours a tinted stored block.
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
