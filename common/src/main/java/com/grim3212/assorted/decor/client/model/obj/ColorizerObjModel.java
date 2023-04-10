package com.grim3212.assorted.decor.client.model.obj;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecification;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecificationLoader;
import com.grim3212.assorted.lib.client.model.loaders.context.IModelBakingContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.function.Function;

public class ColorizerObjModel implements IModelSpecification<ColorizerObjModel> {

    public static final ResourceLocation LOADER_NAME = new ResourceLocation(Constants.MOD_ID, "colorizer_obj");

    private ObjModelCopy unbakedColorizer;

    private ColorizerObjModel(ObjModelCopy unbakedColorizer) {
        this.unbakedColorizer = unbakedColorizer;
    }

    @Override
    public BakedModel bake(IModelBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ResourceLocation modelLocation) {
        return new ColorizerObjBakedModel(context, unbakedColorizer, baker, spriteGetter, modelState, modelLocation);
    }

    public static class Loader implements IModelSpecificationLoader<ColorizerObjModel> {
        public static Loader INSTANCE = new Loader();

        private final Map<ObjModelCopy.ModelSettings, ColorizerObjModel> modelCache = Maps.newConcurrentMap();
        private final Map<ResourceLocation, ObjMaterialLibrary> materialCache = Maps.newConcurrentMap();

        private ResourceManager manager;

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {
            modelCache.clear();
            materialCache.clear();
            manager = resourceManager;
        }

        @Override
        public ColorizerObjModel read(JsonDeserializationContext deserializationContext, JsonObject jsonObject) {
            if (!jsonObject.has("model"))
                throw new JsonParseException("OBJ Loader requires a 'model' key that points to a valid .OBJ model.");

            if (manager == null)
                manager = Minecraft.getInstance().getResourceManager();

            String modelLocation = jsonObject.get("model").getAsString();

            boolean automaticCulling = GsonHelper.getAsBoolean(jsonObject, "automatic_culling", true);
            boolean shadeQuads = GsonHelper.getAsBoolean(jsonObject, "shade_quads", true);
            boolean flipV = GsonHelper.getAsBoolean(jsonObject, "flip_v", false);
            boolean emissiveAmbient = GsonHelper.getAsBoolean(jsonObject, "emissive_ambient", true);
            String mtlOverride = GsonHelper.getAsString(jsonObject, "mtl_override", null);

            // TODO: Deprecated names. To be removed in 1.20
            var deprecationWarningsBuilder = ImmutableMap.<String, String>builder();
            if (jsonObject.has("detectCullableFaces")) {
                automaticCulling = GsonHelper.getAsBoolean(jsonObject, "detectCullableFaces");
                deprecationWarningsBuilder.put("detectCullableFaces", "automatic_culling");
            }
            if (jsonObject.has("diffuseLighting")) {
                shadeQuads = GsonHelper.getAsBoolean(jsonObject, "diffuseLighting");
                deprecationWarningsBuilder.put("diffuseLighting", "shade_quads");
            }
            if (jsonObject.has("flip-v")) {
                flipV = GsonHelper.getAsBoolean(jsonObject, "flip-v");
                deprecationWarningsBuilder.put("flip-v", "flip_v");
            }
            if (jsonObject.has("ambientToFullbright")) {
                emissiveAmbient = GsonHelper.getAsBoolean(jsonObject, "ambientToFullbright");
                deprecationWarningsBuilder.put("ambientToFullbright", "emissive_ambient");
            }
            if (jsonObject.has("materialLibraryOverride")) {
                mtlOverride = GsonHelper.getAsString(jsonObject, "materialLibraryOverride");
                deprecationWarningsBuilder.put("materialLibraryOverride", "mtl_override");
            }

            return loadModel(new ObjModelCopy.ModelSettings(new ResourceLocation(modelLocation), automaticCulling, shadeQuads, flipV, emissiveAmbient, mtlOverride), deprecationWarningsBuilder.build());
        }

        public ColorizerObjModel loadModel(ObjModelCopy.ModelSettings settings) {
            return loadModel(settings, Map.of());
        }

        public ColorizerObjModel loadModel(Resource resource, ObjModelCopy.ModelSettings settings) {
            return loadModel(resource, settings, Map.of());
        }

        private ColorizerObjModel loadModel(ObjModelCopy.ModelSettings settings, Map<String, String> deprecationWarnings) {
            return modelCache.computeIfAbsent(settings, (data) -> {
                Resource resource = manager.getResource(settings.modelLocation()).orElseThrow();
                try (ObjTokenizer tokenizer = new ObjTokenizer(resource.open())) {
                    return new ColorizerObjModel(ObjModelCopy.parse(tokenizer, settings, deprecationWarnings));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException("Could not find OBJ model", e);
                } catch (Exception e) {
                    throw new RuntimeException("Could not read OBJ model", e);
                }
            });
        }

        private ColorizerObjModel loadModel(Resource resource, ObjModelCopy.ModelSettings settings, Map<String, String> deprecationWarnings) {
            return modelCache.computeIfAbsent(settings, (data) -> {
                try (ObjTokenizer tokenizer = new ObjTokenizer(resource.open())) {
                    return new ColorizerObjModel(ObjModelCopy.parse(tokenizer, settings, deprecationWarnings));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException("Could not find OBJ model", e);
                } catch (Exception e) {
                    throw new RuntimeException("Could not read OBJ model", e);
                }
            });
        }

        public ObjMaterialLibrary loadMaterialLibrary(ResourceLocation materialLocation) {
            return loadMaterialLibrary(materialLocation, null);
        }

        public ObjMaterialLibrary loadMaterialLibrary(ResourceLocation materialLocation, @Nullable ResourceManager resourceManager) {
            return materialCache.computeIfAbsent(materialLocation, (location) -> {
                Resource resource = resourceManager != null ? resourceManager.getResource(location).orElseThrow() : manager.getResource(location).orElseThrow();
                try (ObjTokenizer rdr = new ObjTokenizer(resource.open())) {
                    return new ObjMaterialLibrary(rdr);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException("Could not find OBJ material library", e);
                } catch (Exception e) {
                    throw new RuntimeException("Could not read OBJ material library", e);
                }
            });
        }
    }
}
