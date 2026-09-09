package com.grim3212.assorted.decor.client.data;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.grim3212.assorted.decor.client.model.ColorizerUnbakedModel;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.generators.template.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Writes the {@code assorteddecor:colorizer} loader block into a colorizer model json.
 * <p>
 * Forge's {@code ModelBuilder} / {@code ModelProvider} pair is gone, so this is no longer a builder
 * hanging off a second {@code ModelProvider} owning its own model map - which is why
 * {@code ColorizerModelProvider} was deleted along with it. {@link CustomLoaderBuilder} is still the
 * hook, but it plugs into {@link ExtendedModelTemplateBuilder#customLoader} and contributes to the
 * json a {@link net.minecraft.client.data.models.model.ModelTemplate} emits, so it is constructed
 * with the loader id plus whether the loader tolerates inline vanilla elements (it does not - it
 * replaces the geometry outright) and it has to be able to deep copy itself, because a
 * {@code ModelTemplate} is immutable.
 * <p>
 * The {@code loader} key and the shape of the {@code colorizer} object are unchanged;
 * {@code UnbakedModelParser} still reads {@code loader}, the {@code Identifier} it names is still what
 * the model loader registry is keyed by, and {@link ColorizerUnbakedModel.Loader} still reads a
 * {@code parent} plus a {@code textures} map out of {@code colorizer}.
 * <p>
 * The OBJ half of the 1.20.1 builder lives in {@link ColorizerObjModelBuilder}: it is a different
 * loader id reading a different key, and a {@link CustomLoaderBuilder} carries exactly one loader id.
 */
public class ColorizerModelBuilder extends CustomLoaderBuilder {

    public static ColorizerModelBuilder begin() {
        return new ColorizerModelBuilder();
    }

    private @Nullable Identifier colorizer;
    private final Map<String, Identifier> textures = Maps.newLinkedHashMap();

    protected ColorizerModelBuilder() {
        super(ColorizerUnbakedModel.LOADER_NAME, false);
    }

    /**
     * The model whose shape and remaining texture slots the colorizer inherits.
     */
    public ColorizerModelBuilder colorizer(Identifier colorizer) {
        Preconditions.checkNotNull(colorizer, "colorizer must not be null");
        this.colorizer = colorizer;
        return this;
    }

    /**
     * A texture slot filled in on the inherited model. The slot name carries no leading {@code #}:
     * that marks a reference to another slot, and {@code TextureSlots#getMaterial} strips it before
     * looking a slot up, so {@code "#stored"} would declare a slot nothing could ever find.
     */
    public ColorizerModelBuilder addTexture(String name, Identifier texture) {
        Preconditions.checkNotNull(texture, "texture must not be null");
        this.textures.put(name, texture);
        return this;
    }

    @Override
    protected CustomLoaderBuilder copyInternal() {
        ColorizerModelBuilder copy = new ColorizerModelBuilder();
        copy.colorizer = this.colorizer;
        copy.textures.putAll(this.textures);
        return copy;
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        json = super.toJson(json);

        Preconditions.checkNotNull(colorizer, "colorizer must not be null");

        JsonObject colorizerObj = new JsonObject();
        colorizerObj.addProperty("parent", colorizer.toString());

        if (!this.textures.isEmpty()) {
            JsonObject textureObj = new JsonObject();
            this.textures.forEach((k, v) -> textureObj.addProperty(k, v.toString()));
            colorizerObj.add("textures", textureObj);
        }

        json.add("colorizer", colorizerObj);
        return json;
    }
}
