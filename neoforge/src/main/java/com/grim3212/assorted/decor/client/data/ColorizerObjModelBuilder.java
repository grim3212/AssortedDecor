package com.grim3212.assorted.decor.client.data;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.grim3212.assorted.decor.client.model.obj.ColorizerObjModel;
import net.minecraft.resources.Identifier;
import com.grim3212.assorted.lib.client.data.LibCustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.template.CustomLoaderBuilder;
import org.jetbrains.annotations.Nullable;

/**
 * Writes the {@code assorteddecor:colorizer_obj} loader block into a colorizer model json: the
 * {@code model} key names the {@code .obj} file whose geometry the colorizer takes. Its textures go
 * in the ordinary model {@code textures} block.
 */
public class ColorizerObjModelBuilder extends LibCustomLoaderBuilder {

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
