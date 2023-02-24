package com.grim3212.assorted.decor.foml.baked;

import com.grim3212.assorted.decor.foml.FOMLMaterial;
import com.mojang.math.Transformation;
import de.javagl.obj.FloatTuple;
import de.javagl.obj.Mtl;
import de.javagl.obj.Obj;
import de.javagl.obj.ObjSplitting;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

public class OBJUnbakedModel implements UnbakedModel {

    public final Obj obj;
    public final Map<String, FOMLMaterial> mtls;
    public final ItemTransforms transform;
    protected Material sprite;

    public OBJUnbakedModel(Obj obj, Map<String, FOMLMaterial> mtls, ItemTransforms transform) {
        this.obj = obj;
        this.mtls = mtls;
        this.transform = transform == null ? ItemTransforms.NO_TRANSFORMS : transform;

        Mtl mtl = this.findMtlForName("sprite");
        if (mtl == null) {
            Iterator<FOMLMaterial> iterator = mtls.values().iterator();
            while (iterator.hasNext()) {
                mtl = iterator.next();
                if (mtl != null) {
                    break;
                }
            }
        }
        this.sprite = mtl == null || mtl.getMapKd() == null ? getDefaultSprite() : new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(mtl.getMapKd()));
    }

    protected OBJUnbakedModel(Obj obj, Map<String, FOMLMaterial> mtls, ItemTransforms transform, Material sprite) {
        this.obj = obj;
        this.mtls = mtls;
        this.transform = transform == null ? ItemTransforms.NO_TRANSFORMS : transform;
        this.sprite = sprite;
    }

    public Material getDefaultSprite() {
        return new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation());
    }

    private FOMLMaterial findMtlForName(String name) {
        return mtls.get(name);
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Collections.emptySet();
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> function) {
    }

    protected OBJUnbakedModel setSprite(ResourceLocation location) {
        return new OBJUnbakedModel(this.obj, this.mtls, this.transform, new Material(TextureAtlas.LOCATION_BLOCKS, location));
    }

    @Override
    public BakedModel bake(ModelBaker loader, Function<Material, TextureAtlasSprite> textureGetter, ModelState bakeSettings, ResourceLocation modelId) {
        return this.bakeOBJ(loader, textureGetter, bakeSettings, modelId);
    }

    public OBJBakedModel bakeOBJ(ModelBaker loader, Function<Material, TextureAtlasSprite> textureGetter, ModelState bakeSettings, ResourceLocation modelId) {
        Renderer renderer = RendererAccess.INSTANCE.getRenderer();
        Mesh mesh = null;

        if (renderer != null) {
            Map<String, Obj> materialGroups = ObjSplitting.splitByMaterialGroups(obj);
            MeshBuilder builder = renderer.meshBuilder();
            QuadEmitter emitter = builder.getEmitter();

            for (Map.Entry<String, Obj> entry : materialGroups.entrySet()) {
                String matName = entry.getKey();
                Obj matGroupObj = entry.getValue();

                FOMLMaterial mtl = findMtlForName(matName);
                int color = -1;

                TextureAtlasSprite mtlSprite = textureGetter.apply(this.sprite);

                if (mtl != null) {
                    FloatTuple diffuseColor = mtl.getKd();

                    if (mtl.useDiffuseColor()) {
                        color = 0xFF000000;

                        for (int i = 0; i < 3; ++i) {
                            color |= (int) (255 * diffuseColor.get(i)) << (16 - 8 * i);
                        }
                    }

                    if (mtl.getMapKd() != null) {
                        mtlSprite = getMtlSprite(textureGetter, new ResourceLocation(mtl.getMapKd()));
                    }
                }

                for (int i = 0; i < matGroupObj.getNumFaces(); i++) {
                    FloatTuple floatTuple;
                    Vector3f vertex;
                    FloatTuple normal;
                    int v;
                    for (v = 0; v < matGroupObj.getFace(i).getNumVertices(); v++) {
                        floatTuple = matGroupObj.getVertex(matGroupObj.getFace(i).getVertexIndex(v));
                        vertex = new Vector3f(floatTuple.getX(), floatTuple.getY(), floatTuple.getZ());
                        normal = matGroupObj.getNormal(matGroupObj.getFace(i).getNormalIndex(v));

                        addVertex(i, v, vertex, normal, emitter, matGroupObj, false, bakeSettings);

                        // Special conversion of triangles to quads: re-add third vertex as the fourth vertex
                        // Moved into the loop so that `vertex` and `normal` are guaranteed to exist
                        if (v == 2 && matGroupObj.getFace(i).getNumVertices() == 3) {
                            addVertex(i, 3, vertex, normal, emitter, matGroupObj, true, bakeSettings);
                        }
                    }

                    emitter.spriteColor(0, color, color, color, color);
                    emitter.material(RendererAccess.INSTANCE.getRenderer().materialFinder().find());
                    emitter.colorIndex(mtl.getTintIndex());
                    emitter.nominalFace(emitter.lightFace());
                    emitter.spriteBake(0, mtlSprite, MutableQuadView.BAKE_NORMALIZED | (bakeSettings.isUvLocked() ? MutableQuadView.BAKE_LOCK_UV : 0));

                    emitter.emit();
                }
            }


            mesh = builder.build();
        }

        return new OBJBakedModel(mesh, transform, textureGetter.apply(this.sprite), loader, textureGetter, bakeSettings, modelId);
    }

    private void addVertex(int faceIndex, int vertIndex, Vector3f vertex, FloatTuple normal, QuadEmitter emitter, Obj matGroup, boolean degenerate, ModelState bakeSettings) {
        int textureCoordIndex = vertIndex;
        if (degenerate)
            textureCoordIndex--;

        if (bakeSettings.getRotation() != Transformation.identity() && !degenerate) {
            vertex.add(-0.5F, -0.5F, -0.5F);
            vertex.rotate(bakeSettings.getRotation().getLeftRotation());
            vertex.add(0.5f, 0.5f, 0.5f);
        }

        emitter.pos(vertIndex, vertex.x(), vertex.y(), vertex.z());
        emitter.normal(vertIndex, normal.getX(), normal.getY(), normal.getZ());

        if (obj.getNumTexCoords() > 0) {
            FloatTuple text = matGroup.getTexCoord(matGroup.getFace(faceIndex).getTexCoordIndex(textureCoordIndex));

            emitter.sprite(vertIndex, 0, text.getX(), text.getY());
        } else {
            emitter.nominalFace(Direction.getNearest(normal.getX(), normal.getY(), normal.getZ()));
        }
    }

    private static TextureAtlasSprite getMtlSprite(Function<Material, TextureAtlasSprite> textureGetter, ResourceLocation name) {
        return textureGetter.apply(new Material(TextureAtlas.LOCATION_BLOCKS, name));
    }
}
