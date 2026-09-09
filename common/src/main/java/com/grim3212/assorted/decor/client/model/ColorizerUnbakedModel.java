package com.grim3212.assorted.decor.client.model;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecification;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecificationLoader;
import com.grim3212.assorted.lib.client.model.loaders.context.IModelBakingContext;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;

import java.util.Map;

public class ColorizerUnbakedModel implements IModelSpecification<ColorizerUnbakedModel> {

    public static final Identifier LOADER_NAME = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "colorizer");

    private final Colorizer unbakedColorizer;

    private ColorizerUnbakedModel(Colorizer unbakedColorizer) {
        this.unbakedColorizer = unbakedColorizer;
    }

    @Override
    public BlockStateModel bake(IModelBakingContext context, ModelBaker baker, ModelState modelState, Identifier modelLocation) {
        return new ColorizerBakedModel(context, this.unbakedColorizer, baker, modelState, modelLocation);
    }

    /**
     * The {@code "colorizer"} object of a colorizer model json: a parent model to inherit the shape
     * from plus any texture slots that parent still needs filled in.
     * <p>
     * It used to be deserialized into a whole {@code BlockModel}. A 26.2 json model is a
     * {@code CuboidModel} record whose element and face deserializers are package private, so it
     * cannot be read from a foreign {@link JsonDeserializationContext}; the two fields the colorizer
     * models actually use are read directly instead, and the parent is resolved through the
     * {@link ModelBaker} at bake time like any other model reference.
     */
    public record Colorizer(Identifier parent, ImmutableMap<String, String> textures) {
    }

    public static final class Loader implements IModelSpecificationLoader<ColorizerUnbakedModel> {
        public static final Loader INSTANCE = new Loader();

        @Override
        public ColorizerUnbakedModel read(JsonDeserializationContext deserializationContext, JsonObject jsonObject) {
            if (!jsonObject.has("colorizer"))
                throw new JsonParseException("Colorizer Loader requires a 'colorizer' key holding the model to colorize.");

            JsonObject colorizer = GsonHelper.getAsJsonObject(jsonObject, "colorizer");
            Identifier parent = Identifier.parse(GsonHelper.getAsString(colorizer, "parent"));

            ImmutableMap.Builder<String, String> textures = ImmutableMap.builder();
            if (colorizer.has("textures")) {
                for (Map.Entry<String, JsonElement> entry : GsonHelper.getAsJsonObject(colorizer, "textures").entrySet()) {
                    textures.put(entry.getKey(), entry.getValue().getAsString());
                }
            }

            return new ColorizerUnbakedModel(new Colorizer(parent, textures.build()));
        }
    }
}
