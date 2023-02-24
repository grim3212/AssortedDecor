package com.grim3212.assorted.decor.foml.baked;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class ForwardingOBJBakedModel extends OBJBakedModel {

    protected final OBJUnbakedModel originalUnbakedModel;

    public ForwardingOBJBakedModel(OBJUnbakedModel originalUnbakedModel, ItemTransforms transforms, TextureAtlasSprite sprite, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state, ResourceLocation location) {
        super(null, transforms, sprite, baker, spriteGetter, state, location);
        this.originalUnbakedModel = originalUnbakedModel;
    }

    public abstract ResourceLocation defaultTexture();

    protected final Map<BlockState, OBJBakedModel> cache = new HashMap<BlockState, OBJBakedModel>();
    protected OBJBakedModel EMPTY;

    public OBJBakedModel getCachedModel(BlockState blockState) {
        if (blockState == null || blockState == Blocks.AIR.defaultBlockState()) {
            if (EMPTY == null) {
                ImmutableMap.Builder<String, String> newTexture = ImmutableMap.builder();
                String texture = defaultTexture().toString();
                newTexture.put("particle", texture);
                newTexture.put("#stored", texture);
                EMPTY = generateModel(newTexture.build());
            }
            return EMPTY;
        }

        if (!this.cache.containsKey(blockState)) {
            ImmutableMap.Builder<String, String> newTexture = ImmutableMap.builder();

            String texture = "";
            if (blockState.getBlock() == Blocks.GRASS_BLOCK) {
                texture = "minecraft:block/grass_block_top";
            } else if (blockState.getBlock() == Blocks.PODZOL) {
                texture = "minecraft:block/dirt_podzol_top";
            } else if (blockState.getBlock() == Blocks.MYCELIUM) {
                texture = "minecraft:block/mycelium_top";
            } else {
                BlockModelShaper blockModel = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper();
                TextureAtlasSprite blockTexture = blockModel.getParticleIcon(blockState);
                texture = blockTexture.contents().name().toString();
            }

            newTexture.put("particle", texture);
            newTexture.put("#stored", texture);
            this.cache.put(blockState, generateModel(newTexture.build()));
        }
        return this.cache.get(blockState);
    }

    protected OBJBakedModel generateModel(ImmutableMap<String, String> textures) {
        return this.originalUnbakedModel.setSprite(new ResourceLocation(textures.get("#stored"))).bakeOBJ(this.baker, this.spriteGetter, this.state, this.location);
    }
}
