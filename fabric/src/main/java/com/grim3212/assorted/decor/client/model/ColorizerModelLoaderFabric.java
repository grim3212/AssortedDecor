package com.grim3212.assorted.decor.client.model;

import com.google.gson.*;
import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.foml.OBJLoaderHelpers;
import com.grim3212.assorted.decor.foml.baked.OBJUnbakedModel;
import com.mojang.datafixers.util.Either;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Optional;


public class ColorizerModelLoaderFabric implements ModelResourceProvider {
    static final Gson GSON = new GsonBuilder().registerTypeAdapter(Either.class, new ColorizerModelLoaderDeserializer()).create();
    public static final ColorizerModelLoaderFabric INSTANCE = new ColorizerModelLoaderFabric();

    @Override
    @Nullable
    public UnbakedModel loadModelResource(ResourceLocation resourceId, ModelProviderContext context) throws ModelProviderException {
        return this.loadResource(resourceId, context, ItemTransforms.NO_TRANSFORMS);
    }

    public UnbakedModel loadResource(ResourceLocation resourceId, ModelProviderContext context, ItemTransforms transforms) throws ModelProviderException {
        if (resourceId.getNamespace().equals(Constants.MOD_ID) && resourceId.getPath().contains("colorizer")) {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

            Optional<Resource> resource = resourceManager.getResource(new ResourceLocation(resourceId.getNamespace(), "models/" + resourceId.getPath() + ".json"));
            if (resource.isPresent()) {
                Either<UnbakedModel, ResourceLocation> colorizerModel = null;

                try (Reader reader = resource.get().openAsReader()) {
                    colorizerModel = (Either<UnbakedModel, ResourceLocation>) GsonHelper.fromJson(GSON, reader, Either.class);
                } catch (IOException e) {
                    Constants.LOG.error("Unable to load Colorizer Model, Source: " + resourceId.toString(), e);
                }

                if (colorizerModel == null) {
                    Constants.LOG.error("Failed to deserialize Colorizer Model, Source: " + resourceId.toString());
                    return null;
                }

                if (colorizerModel.left().isPresent()) {
                    return colorizerModel.left().get();
                }

                if (colorizerModel.right().isPresent()) {
                    ResourceLocation objModelLocation = colorizerModel.right().get();

                    try (Reader reader = resourceManager.getResource(objModelLocation).get().openAsReader()) {
                        OBJUnbakedModel unbakedModel = OBJLoaderHelpers.loadModel(reader, objModelLocation.getNamespace(), resourceManager, transforms);

                        ColorizerObjUnbakedModel objModel = new ColorizerObjUnbakedModel(unbakedModel);
                        if (objModel != null) {
                            return objModel;
                        }
                    } catch (IOException e) {
                        Constants.LOG.error("Unable to load Colorizer OBJ Model file, Source: " + objModelLocation, e);
                    } catch (Exception e) {
                        Constants.LOG.error("Colorizer obj model fail", e);
                        e.printStackTrace();
                    }
                }
            }
        }

        return null;
    }

    public static class ColorizerBlockModelDeserializer extends BlockModel.Deserializer {
        @Override
        public BlockModel deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonobject = json.getAsJsonObject();
            if (!jsonobject.has("loader")) {
                return super.deserialize(json, type, context);
            }

            if (jsonobject.has("colorizer")) {
                BlockModel colorizerModel = context.deserialize(jsonobject.getAsJsonObject("colorizer"), BlockModel.class);
                if (colorizerModel != null) {
                    return new ColorizerFabricUnbakedModel(colorizerModel);
                }
            }

            return super.deserialize(json, type, context);
        }
    }

    public static class ColorizerModelLoaderDeserializer implements JsonDeserializer<Either<UnbakedModel, ResourceLocation>> {

        public ColorizerModelLoaderDeserializer() {
        }

        @Override
        public Either<UnbakedModel, ResourceLocation> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            BlockModel.Deserializer blockModelDeserializer = new BlockModel.Deserializer();

            JsonObject jsonobject = json.getAsJsonObject();
            if (!jsonobject.has("loader")) {
                return Either.left(blockModelDeserializer.deserialize(json, typeOfT, context));
            }

            String loader = jsonobject.get("loader").getAsString();

            if (loader.equals("assorteddecor:models/colorizer_obj")) {
                ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
                ResourceLocation objModelLocation = new ResourceLocation(jsonobject.get("model").getAsString());
                Optional<Resource> objModelResource = resourceManager.getResource(objModelLocation);
                if (!objModelResource.isPresent()) {
                    Constants.LOG.error("Unable to find Colorizer OBJ Model at" + objModelLocation);
                }

                return Either.right(objModelLocation);
            } else if (loader.equals("assorteddecor:models/colorizer")) {
                if (jsonobject.has("colorizer")) {
                    BlockModel colorizerModel = blockModelDeserializer.deserialize(jsonobject.getAsJsonObject("colorizer"), BlockModel.class, context);
                    if (colorizerModel != null) {
                        return Either.left(new ColorizerFabricUnbakedModel(colorizerModel));
                    }
                }
            } else {
                Constants.LOG.error("Unable to deserialize Colorizer Model with loader " + loader);
            }

            return Either.left(blockModelDeserializer.deserialize(json, typeOfT, context));
        }
    }
}
