package com.grim3212.assorted.decor.gametest;

import net.minecraft.locale.Language;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.server.MinecraftServer;
import com.grim3212.assorted.lib.platform.Services;
import java.io.BufferedReader;
import java.io.IOException;
import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.helpers.DecorCreativeItems;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.decor.gametest.DecorTestSupport.*;

/**
 * What the mod ships: models and names, colorizer item models, loader keys both loaders read, and recipes that load.
 */
final class AssetTests {

    private AssetTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("mod_assets_are_complete", AssetTests::modAssetsAreComplete);
        out.accept("colorizer_items_draw_their_stored_block", AssetTests::colorizerItemsDrawTheirStoredBlock);
        out.accept("loader_models_are_read_on_both_loaders", AssetTests::loaderModelsAreReadOnBothLoaders);
        out.accept("every_recipe_loads_or_is_conditioned_off", AssetTests::everyRecipeLoadsOrIsConditionedOff);
        out.accept("every_item_tag_has_a_name", AssetTests::everyItemTagHasAName);
    }

    /**
     * The creative tab is registered, and every block and item this mod adds has a model and a
     * name. Missing models and missing lang keys were the single most repeated bug of the port, and
     * a headless server can see both: the mod's own assets are on its classpath even though it
     * never loads them.
     * <p>
     * Everything missing is reported in one message rather than failing on the first, so fixing
     * them is one pass and not a loop.
     */
    private static void modAssetsAreComplete(GameTestHelper helper) {
        helper.assertTrue(BuiltInRegistries.CREATIVE_MODE_TAB.containsKey(DecorCreativeItems.CREATIVE_TAB_KEY),
                "the Assorted Decor creative tab is not registered");

        JsonObject lang = readJson("/assets/" + Constants.MOD_ID + "/lang/en_us.json");
        helper.assertTrue(lang != null, "/assets/" + Constants.MOD_ID + "/lang/en_us.json is not on the classpath");
        helper.assertTrue(lang.has("itemGroup." + Constants.MOD_ID), "the creative tab has no name in en_us.json");

        List<String> missing = new ArrayList<>();

        for (Map.Entry<ResourceKey<Block>, Block> entry : BuiltInRegistries.BLOCK.entrySet()) {
            Identifier id = entry.getKey().identifier();
            if (!Constants.MOD_ID.equals(id.getNamespace())) {
                continue;
            }
            if (!resourceExists("/assets/" + id.getNamespace() + "/blockstates/" + id.getPath() + ".json")) {
                missing.add("blockstate for " + id);
            }
            if (!hasName(lang, entry.getValue().getDescriptionId())) {
                missing.add("name " + entry.getValue().getDescriptionId() + " for block " + id);
            }
        }

        for (Map.Entry<ResourceKey<Item>, Item> entry : BuiltInRegistries.ITEM.entrySet()) {
            Identifier id = entry.getKey().identifier();
            if (!Constants.MOD_ID.equals(id.getNamespace())) {
                continue;
            }
            if (!resourceExists("/assets/" + id.getNamespace() + "/items/" + id.getPath() + ".json")) {
                missing.add("item model for " + id);
            }
            if (!hasName(lang, entry.getValue().getDescriptionId())) {
                missing.add("name " + entry.getValue().getDescriptionId() + " for item " + id);
            }
        }

        helper.assertTrue(missing.isEmpty(), missing.size() + " missing assets: " + String.join("; ", missing));
        helper.succeed();
    }

    /**
     * Every colorizer's item draws the block it holds: its item json has to name the
     * {@code assorteddecor:colorizer} item model type. A plain {@code minecraft:model} over the same
     * block model bakes one quad collection at load - the colorizer's empty state - and nothing warns.
     * Read off the shipped jsons, which a headless server has on its classpath. What the model then
     * draws, and the particle it throws, are checked in a real client by {@code DecorClientGameTests}.
     */
    private static void colorizerItemsDrawTheirStoredBlock(GameTestHelper helper) {
        List<String> wrong = new ArrayList<>();
        int checked = 0;

        for (IRegistryObject<? extends Block> registered : DecorBlocks.colorizerBlocks()) {
            Item item = registered.get().asItem();
            if (item == Items.AIR) {
                continue;
            }
            checked++;

            Identifier id = BuiltInRegistries.ITEM.getKey(item);
            JsonObject json = readJson("/assets/" + id.getNamespace() + "/items/" + id.getPath() + ".json");
            JsonObject model = json == null ? null : json.getAsJsonObject("model");
            String type = model != null && model.has("type") ? model.get("type").getAsString() : null;
            if (!"assorteddecor:colorizer".equals(type)) {
                wrong.add(id + " is drawn by " + type);
            }
        }

        helper.assertTrue(checked > 20, "only " + checked + " colorizer items were found to check");
        helper.assertTrue(wrong.isEmpty(), wrong.size() + " colorizer item(s) cannot draw their stored block: " + String.join("; ", wrong));
        helper.succeed();
    }

    /**
     * Every custom blockstate model and every loader model this mod's blocks use is read by both
     * loaders. The jsons are generated once and shared, but NeoForge reads a variant's custom type from
     * {@code "type"} and a model's loader from {@code "loader"}, while Fabric reads both from
     * {@code "fabric:type"} and ignores the others. A json carrying only NeoForge's key loads on Fabric
     * as a plain static model - every colorizer drawing its empty state - and nothing warns.
     */
    private static void loaderModelsAreReadOnBothLoaders(GameTestHelper helper) {
        List<String> wrong = new ArrayList<>();
        List<String> checkedModels = new ArrayList<>();
        int customVariants = 0;

        for (Map.Entry<ResourceKey<Block>, Block> entry : BuiltInRegistries.BLOCK.entrySet()) {
            Identifier id = entry.getKey().identifier();
            if (!Constants.MOD_ID.equals(id.getNamespace())) {
                continue;
            }
            JsonObject blockstate = readJson("/assets/" + id.getNamespace() + "/blockstates/" + id.getPath() + ".json");
            if (blockstate == null) {
                continue;
            }

            for (JsonObject variant : blockstateVariants(blockstate)) {
                if (variant.has("type")) {
                    customVariants++;
                    if (!variant.get("type").equals(variant.get("fabric:type"))) {
                        wrong.add("blockstate " + id.getPath() + " has type " + variant.get("type") + " but fabric:type " + variant.get("fabric:type"));
                    }
                }

                String model = variant.has("model") ? variant.get("model").getAsString() : null;
                if (model == null || checkedModels.contains(model) || !model.startsWith(Constants.MOD_ID + ":")) {
                    continue;
                }
                checkedModels.add(model);

                Identifier modelId = Identifier.parse(model);
                JsonObject json = readJson("/assets/" + modelId.getNamespace() + "/models/" + modelId.getPath() + ".json");
                if (json != null && json.has("loader") && !json.get("loader").equals(json.get("fabric:type"))) {
                    wrong.add("model " + model + " has loader " + json.get("loader") + " but fabric:type " + json.get("fabric:type"));
                }
            }
        }

        helper.assertTrue(customVariants > 0, "no custom blockstate variants were found to check");
        helper.assertTrue(wrong.isEmpty(), wrong.size() + " json(s) Fabric would read as static: " + String.join("; ", wrong));
        helper.succeed();
    }

    /**
     * Every recipe file this mod ships either loaded, or carries this loader's load conditions and was
     * skipped by them. A file with neither failed to parse. On Fabric that was every conditional
     * recipe for a while: Fabric's datagen wrote them without conditions, and the NeoForge copy that
     * shadowed it carries a key Fabric ignores - so only this loader's own key counts.
     */
    private static void everyRecipeLoadsOrIsConditionedOff(GameTestHelper helper) {
        MinecraftServer server = helper.getLevel().getServer();
        FileToIdConverter recipes = FileToIdConverter.json("recipe");
        String conditionsKey = Services.PLATFORM.getPlatformName().equals("Fabric") ? "fabric:load_conditions" : "neoforge:conditions";
        List<String> failed = new ArrayList<>();

        recipes.listMatchingResources(server.getResourceManager()).forEach((file, resource) -> {
            Identifier id = recipes.fileToId(file);
            if (!id.getNamespace().equals(Constants.MOD_ID) || server.getRecipeManager().byKey(ResourceKey.create(Registries.RECIPE, id)).isPresent()) {
                return;
            }

            try (BufferedReader reader = resource.openAsReader()) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                if (!json.has(conditionsKey)) {
                    failed.add(id.toString());
                }
            } catch (IOException e) {
                failed.add(id + " (" + e.getMessage() + ")");
            }
        });

        helper.assertTrue(failed.isEmpty(), failed.size() + " recipes failed to load without being conditioned off: " + String.join(", ", failed.subList(0, Math.min(10, failed.size()))));
        helper.succeed();
    }

    /**
     * Every item tag outside minecraft has a name. Recipe viewers show it in place of the raw id,
     * and it is the check Fabric API runs at dev startup ("Untranslated Item Tags detected"), made
     * to fail here: the key is {@code tag.item.<namespace>.<path>} with each '/' in the path turned
     * into '.'. Both loaders load every mod's lang file on a dedicated server and name the standard
     * c: tags themselves, so whatever is still missing is one of ours.
     */
    private static void everyItemTagHasAName(GameTestHelper helper) {
        Language language = Language.getInstance();
        List<String> missing = helper.getLevel().registryAccess().lookupOrThrow(Registries.ITEM).getTags()
                .map(tag -> tag.key().location())
                .filter(id -> !"minecraft".equals(id.getNamespace()))
                .map(id -> "tag.item." + id.getNamespace() + "." + id.getPath().replace('/', '.'))
                .filter(key -> !language.has(key))
                .sorted()
                .toList();
        helper.assertTrue(missing.isEmpty(), "item tags with no name in any lang file: " + missing);
        helper.succeed();
    }
}
