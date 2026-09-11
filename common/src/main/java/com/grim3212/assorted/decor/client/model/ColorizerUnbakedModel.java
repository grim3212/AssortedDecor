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
import net.minecraft.client.resources.model.ResolvableModel;
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
     * Marks the shape template, which {@link ColorizerBakedModel} fetches with {@code getModel}.
     * Nothing else references it, so without this every colorizer bakes to the missing model.
     */
    @Override
    public void resolveDependencies(ResolvableModel.Resolver resolver) {
        resolver.markDependency(this.unbakedColorizer.parent());
    }

    /**
     * The {@code "colorizer"} object of a colorizer model json: the parent to take the shape from
     * and the texture slots it still needs. Only these two fields are read, because a json model
     * cannot be deserialized from outside its package; the parent resolves through the {@link
     * ModelBaker}.
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
