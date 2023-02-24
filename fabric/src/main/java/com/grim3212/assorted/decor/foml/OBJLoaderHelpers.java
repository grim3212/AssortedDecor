package com.grim3212.assorted.decor.foml;

import com.grim3212.assorted.decor.foml.baked.OBJUnbakedModel;
import com.grim3212.assorted.lib.LibConstants;
import de.javagl.obj.Obj;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OBJLoaderHelpers {

    public static OBJUnbakedModel loadModel(Reader reader, String modid, ResourceManager manager, ItemTransforms transform) {
        OBJUnbakedModel model;

        try {
            Obj obj = ObjUtils.convertToRenderable(ObjReader.read(reader));
            model = new OBJUnbakedModel(ObjUtils.triangulate(obj), loadMTL(manager, modid, obj.getMtlFileNames()), transform);
        } catch (IOException e) {
            LibConstants.LOG.error("Could not read obj model!", e);
            return null;
        }

        return model;
    }

    private static Map<String, FOMLMaterial> loadMTL(ResourceManager manager, String modid, List<String> mtlNames) throws IOException {
        Map<String, FOMLMaterial> mtls = new LinkedHashMap<>();

        for (String name : mtlNames) {
            ResourceLocation resourceId = new ResourceLocation(modid, "models/" + name);

            Optional<Resource> resource = manager.getResource(resourceId);

            // Use 1.0.0 MTL path as a fallback
            if (!resource.isPresent()) {
                resourceId = new ResourceLocation(modid, "models/block/" + name);
                resource = manager.getResource(resourceId);
            }

            // Continue with normal resource loading code
            if (resource.isPresent()) {
                MtlReader.read(resource.get().openAsReader()).forEach(mtl -> {
                    mtls.put(mtl.getName(), mtl);
                });
            } else {
                LibConstants.LOG.warn("Warning, a model specifies an MTL File but it could not be found! Source: " + modid + ":" + name);
            }
        }

        return mtls;
    }


}
