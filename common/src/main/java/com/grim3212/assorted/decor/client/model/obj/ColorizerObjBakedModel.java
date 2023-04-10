package com.grim3212.assorted.decor.client.model.obj;

import com.google.common.collect.ImmutableMap;
import com.grim3212.assorted.decor.client.model.ColorizerBakedModel;
import com.grim3212.assorted.decor.client.model.ColorizerBaseBakedModel;
import com.grim3212.assorted.lib.client.model.ItemOverridesExtension;
import com.grim3212.assorted.lib.client.model.loaders.context.IModelBakingContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import java.util.function.Function;

public class ColorizerObjBakedModel extends ColorizerBaseBakedModel<ObjModelCopy> {

    public ColorizerObjBakedModel(IModelBakingContext context, ObjModelCopy objModel, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ResourceLocation name) {
        super(context, objModel, baker, spriteGetter, transform, name);
    }

    @Override
    protected BakedModel generateModel(ImmutableMap<String, String> textures) {
        TextureAtlasSprite texture = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(new ResourceLocation(textures.get("#stored")));
        return this.model.setTexture(texture).bake(this.context, this.bakery, this.spriteGetter, this.transform, this.name);
    }

    @Override
    protected ItemOverridesExtension createItemOverrides(IModelBakingContext context) {
        return new ColorizerBakedModel.ColorizerItemOverrideList(context);
    }
}
