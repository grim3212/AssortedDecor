package com.grim3212.assorted.decor.client.data;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.grim3212.assorted.decor.client.model.obj.ColorizerObjModel;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.generators.template.CustomLoaderBuilder;
import org.jetbrains.annotations.Nullable;

/**
 * Writes the {@code assorteddecor:colorizer_obj} loader block into a colorizer model json - the
 * {@code model} key naming the {@code .obj} file whose geometry the colorizer takes on.
 * <p>
 * In 1.20.1 this was the second branch of one {@code ColorizerModelBuilder}. A
 * {@link CustomLoaderBuilder} carries exactly one loader id, and the two colorizer loaders are
 * separate ids reading separate keys, so the branch becomes its own builder.
 * <p>
 * The 1.20.1 builder also wrote a top level {@code textures} object alongside {@code model}. It is
 * dropped: {@link ColorizerObjModel.Loader#read} never looked at it, and the textures a colorizer OBJ
 * model actually needs are the ordinary model {@code textures} block that the
 * {@link net.minecraft.client.data.models.model.ModelTemplate} writes from its
 * {@link net.minecraft.client.data.models.model.TextureMapping}.
 */
public class ColorizerObjModelBuilder extends CustomLoaderBuilder {

    public static ColorizerObjModelBuilder begin() {
        return new ColorizerObjModelBuilder();
    }

    private @Nullable Identifier model;

    protected ColorizerObjModelBuilder() {
        super(ColorizerObjModel.LOADER_NAME, false);
    }

    /**
     * The {@code .obj} resource this colorizer takes its geometry from. Note this is a plain resource
     * path ({@code models/block/slope.obj}), not a model id.
     */
    public ColorizerObjModelBuilder objModel(Identifier model) {
        Preconditions.checkNotNull(model, "model must not be null");
        this.model = model;
        return this;
    }

    @Override
    protected CustomLoaderBuilder copyInternal() {
        ColorizerObjModelBuilder copy = new ColorizerObjModelBuilder();
        copy.model = this.model;
        return copy;
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        json = super.toJson(json);

        Preconditions.checkNotNull(model, "model must not be null");

        json.addProperty("model", model.toString());
        return json;
    }
}
