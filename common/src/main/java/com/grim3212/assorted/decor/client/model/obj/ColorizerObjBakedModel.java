package com.grim3212.assorted.decor.client.model.obj;

import com.google.common.collect.ImmutableMap;
import com.grim3212.assorted.decor.client.model.ColorizerBaseBakedModel;
import com.grim3212.assorted.lib.client.model.loaders.context.IModelBakingContext;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;

public class ColorizerObjBakedModel extends ColorizerBaseBakedModel<ObjModelCopy> {

    public ColorizerObjBakedModel(IModelBakingContext context, ObjModelCopy objModel, ModelBaker baker, ModelState transform, Identifier name) {
        super(context, objModel, baker, transform, name);
    }

    // The stored sprite is pushed onto the shared ObjModelCopy and then baked out of it, which is how
    // the 1.20.1 version worked and is kept so the parse cache in ColorizerObjModel.Loader can stay
    // keyed by ModelSettings. It is not thread safe: the specification is shared between every
    // colorizer model that names the same .obj file, so two of them baking different stored states at
    // once can read each other's texture. Worth revisiting if OBJ colorizers come out mis-textured.
    @Override
    protected BlockStateModel generateModel(ImmutableMap<String, String> textures) {
        // Sprites come from the baker now rather than from a Minecraft-wide atlas lookup; the block
        // atlas is not addressable by id at bake time any more.
        Material.Baked stored = this.bakery.materials().get(new Material(Identifier.parse(textures.get("stored"))), this.debugName);
        return this.model.setTexture(stored.sprite()).bake(this.context, this.bakery, this.transform, this.name);
    }
}
