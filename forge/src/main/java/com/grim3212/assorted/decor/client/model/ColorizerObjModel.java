package com.grim3212.assorted.decor.client.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.client.model.obj.ObjMaterialLibrary;
import net.minecraftforge.client.model.obj.ObjTokenizer;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.function.Function;

public class ColorizerObjModel implements IUnbakedGeometry<ColorizerObjModel> {

    private final ObjModelCopy objModel;

    private ColorizerObjModel(ObjModelCopy objModel) {
        this.objModel = objModel;
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
        this.objModel.resolveParents(modelGetter, context);
    }

    @Nullable
    @Override
    public BakedModel bake(IGeometryBakingContext owner, ModelBaker bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation name) {
        BakedModel bakedColorizer = this.objModel.bake(owner, bakery, spriteGetter, transform, overrides, name);
        return new ColorizerObjBakedModel(bakedColorizer, objModel, owner, spriteGetter.apply(owner.getMaterial("particle")), bakery, spriteGetter, transform, overrides, name);
    }

    public static class ColorizerObjLoader implements IGeometryLoader<ColorizerObjModel>, ResourceManagerReloadListener {
        public static ColorizerObjLoader INSTANCE = new ColorizerObjLoader();

        private final Map<ObjModelCopy.ModelSettings, ColorizerObjModel> modelCache = Maps.newConcurrentMap();
        private final Map<ResourceLocation, ObjMaterialLibrary> materialCache = Maps.newConcurrentMap();

        private ResourceManager manager = Minecraft.getInstance().getResourceManager();

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {
            modelCache.clear();
            materialCache.clear();
            manager = resourceManager;
        }

        @Override
        public ColorizerObjModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) {
            if (!jsonObject.has("model"))
                throw new JsonParseException("Colorizer OBJ Loader requires a 'model' key that points to a valid .OBJ model.");

            String modelLocation = jsonObject.get("model").getAsString();

            boolean automaticCulling = true;
            boolean shadeQuads = true;
            boolean flipV = true;
            boolean emissiveAmbient = true;
            String mtlOverride = null;

            return loadModel(new ObjModelCopy.ModelSettings(new ResourceLocation(modelLocation), automaticCulling, shadeQuads, flipV, emissiveAmbient, mtlOverride), Maps.newHashMap());
        }

        public ColorizerObjModel loadModel(ObjModelCopy.ModelSettings settings) {
            return loadModel(settings, Map.of());
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

        public ObjMaterialLibrary loadMaterialLibrary(ResourceLocation materialLocation) {
            return materialCache.computeIfAbsent(materialLocation, (location) -> {
                Resource resource = manager.getResource(location).orElseThrow();
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
