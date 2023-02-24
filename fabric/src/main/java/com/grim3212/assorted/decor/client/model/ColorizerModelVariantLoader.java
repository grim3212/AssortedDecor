package com.grim3212.assorted.decor.client.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.lib.mixin.client.AccessorBlockModel;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.util.Optional;

public class ColorizerModelVariantLoader implements ModelVariantProvider {
    public static final ColorizerModelVariantLoader INSTANCE = new ColorizerModelVariantLoader();

    @Override
    public @Nullable UnbakedModel loadModelVariant(ModelResourceLocation modelId, ModelProviderContext context) throws ModelProviderException {
        if (modelId.getNamespace().equals(Constants.MOD_ID) && modelId.getPath().contains("colorizer") && modelId.getVariant().equals("inventory")) {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            ResourceLocation modelPath = new ResourceLocation(modelId.getNamespace(), "models/item/" + modelId.getPath() + ".json");
            Optional<Resource> itemModelResource = resourceManager.getResource(modelPath);

            if (itemModelResource.isPresent()) {
                try (Reader reader = itemModelResource.get().openAsReader()) {
                    JsonObject rawModel = GsonHelper.parse(reader);

                    JsonElement parent = rawModel.get("parent");
                    if (parent instanceof JsonPrimitive && ((JsonPrimitive) parent).isString()) {
                        ResourceLocation parentPath = new ResourceLocation(parent.getAsString());
                        Optional<Resource> parentResource = resourceManager.getResource(new ResourceLocation(parentPath.getNamespace(), "models/" + parentPath.getPath() + ".json"));

                        if (parentResource.isPresent()) {
                            try (Reader parentReader = parentResource.get().openAsReader()) {
                                JsonObject rawParentModel = GsonHelper.parse(parentReader);

                                if (!rawParentModel.has("model")) {
                                    // Break out early as it is probably not a model we care about
                                    return null;
                                }

                                if (!rawParentModel.get("model").getAsString().endsWith(".obj")) {
                                    // Break out early as it is probably not a model we care about
                                    return null;
                                }

                                ItemTransforms transformation = ItemTransforms.NO_TRANSFORMS;
                                if (rawParentModel.has("display")) {
                                    JsonObject rawTransform = GsonHelper.getAsJsonObject(rawParentModel, "display");
                                    transformation = AccessorBlockModel.assortedlib_getGson().fromJson(rawTransform, ItemTransforms.class);
                                }

                                return ColorizerModelLoaderFabric.INSTANCE.loadResource(parentPath, context, transformation);
                            } catch (Exception e) {
                                // Silently ignore general IllegalStateExceptions, as all vanilla models in a registered namespace would
                                // otherwise spew the console with this error.
                                if (!(e instanceof IllegalStateException)) {
                                    Constants.LOG.error("Unable to load OBJ Model, Source: " + modelId.toString(), e);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    // Silently ignore general IllegalStateExceptions, as all vanilla models in a registered namespace would
                    // otherwise spew the console with this error.
                    if (!(e instanceof IllegalStateException)) {
                        Constants.LOG.error("Unable to load OBJ Model, Source: " + modelId.toString(), e);
                    }
                }
            }
        }

        return null;
    }
}
