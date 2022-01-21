package com.grim3212.assorted.decor.client.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import joptsimple.internal.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.math.Transformation;
import net.minecraft.world.phys.Vec2;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;
import net.minecraftforge.client.model.geometry.IMultipartModelGeometry;
import net.minecraftforge.client.model.obj.LineReader;
import net.minecraftforge.client.model.obj.MaterialLibrary;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.model.obj.OBJModel.ModelSettings;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;

public class OBJModelCopy implements IMultipartModelGeometry<OBJModelCopy> {
	private static Vector4f COLOR_WHITE = new Vector4f(1, 1, 1, 1);
	private static Vec2[] DEFAULT_COORDS = { new Vec2(0, 0), new Vec2(0, 1), new Vec2(1, 1), new Vec2(1, 0), };

	private final Map<String, ModelGroup> parts = Maps.newHashMap();

	private final List<Vector3f> positions = Lists.newArrayList();
	private final List<Vec2> texCoords = Lists.newArrayList();
	private final List<Vector3f> normals = Lists.newArrayList();
	private final List<Vector4f> colors = Lists.newArrayList();

	public final boolean detectCullableFaces;
	public final boolean diffuseLighting;
	public final boolean flipV;
	public final boolean ambientToFullbright;

	public final ResourceLocation modelLocation;

	@Nullable
	public final String materialLibraryOverrideLocation;

	private TextureAtlasSprite texture;

	private static ResourceManager manager = Minecraft.getInstance().getResourceManager();

	public static OBJModelCopy loadModel(OBJModel.ModelSettings settings) {
		try (Resource resource = manager.getResource(settings.modelLocation()); LineReader rdr = new LineReader(resource)) {
			return new OBJModelCopy(rdr, settings);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Could not find OBJ model", e);
		} catch (Exception e) {
			throw new RuntimeException("Could not read OBJ model", e);
		}
	}

	OBJModelCopy(LineReader reader, ModelSettings settings) throws IOException {
		this.modelLocation = settings.modelLocation();
		this.detectCullableFaces = settings.detectCullableFaces();
		this.diffuseLighting = settings.diffuseLighting();
		this.flipV = settings.flipV();
		this.ambientToFullbright = settings.ambientToFullbright();
		this.materialLibraryOverrideLocation = settings.materialLibraryOverrideLocation();

		// for relative references to material libraries
		String modelDomain = modelLocation.getNamespace();
		String modelPath = modelLocation.getPath();
		int lastSlash = modelPath.lastIndexOf('/');
		if (lastSlash >= 0)
			modelPath = modelPath.substring(0, lastSlash + 1); // include the '/'
		else
			modelPath = "";

		MaterialLibrary mtllib = MaterialLibrary.EMPTY;
		MaterialLibrary.Material currentMat = null;
		String currentSmoothingGroup = null;
		ModelGroup currentGroup = null;
		ModelObject currentObject = null;
		ModelMesh currentMesh = null;

		boolean objAboveGroup = false;

		if (materialLibraryOverrideLocation != null) {
			String lib = materialLibraryOverrideLocation;
			if (lib.contains(":"))
				mtllib = OBJLoader.INSTANCE.loadMaterialLibrary(new ResourceLocation(lib));
			else
				mtllib = OBJLoader.INSTANCE.loadMaterialLibrary(new ResourceLocation(modelDomain, modelPath + lib));
		}

		String[] line;
		while ((line = reader.readAndSplitLine(true)) != null) {
			switch (line[0]) {
			case "mtllib": // Loads material library
			{
				if (materialLibraryOverrideLocation != null)
					break;

				String lib = line[1];
				if (lib.contains(":"))
					mtllib = OBJLoader.INSTANCE.loadMaterialLibrary(new ResourceLocation(lib));
				else
					mtllib = OBJLoader.INSTANCE.loadMaterialLibrary(new ResourceLocation(modelDomain, modelPath + lib));
				break;
			}

			case "usemtl": // Sets the current material (starts new mesh)
			{
				String mat = Strings.join(Arrays.copyOfRange(line, 1, line.length), " ");
				MaterialLibrary.Material newMat = mtllib.getMaterial(mat);
				if (!Objects.equals(newMat, currentMat)) {
					currentMat = newMat;
					if (currentMesh != null && currentMesh.mat == null && currentMesh.faces.size() == 0) {
						currentMesh.mat = currentMat;
					} else {
						// Start new mesh
						currentMesh = null;
					}
				}
				break;
			}

			case "v": // Vertex
				positions.add(OBJModel.parseVector4To3(line));
				break;
			case "vt": // Vertex texcoord
				texCoords.add(OBJModel.parseVector2(line));
				break;
			case "vn": // Vertex normal
				normals.add(OBJModel.parseVector3(line));
				break;
			case "vc": // Vertex color (non-standard)
				colors.add(OBJModel.parseVector4(line));
				break;

			case "f": // Face
			{
				if (currentMesh == null) {
					currentMesh = new ModelMesh(currentMat, currentSmoothingGroup);
					if (currentObject != null) {
						currentObject.meshes.add(currentMesh);
					} else {
						if (currentGroup == null) {
							currentGroup = new ModelGroup("");
							parts.put("", currentGroup);
						}
						currentGroup.meshes.add(currentMesh);
					}
				}

				int[][] vertices = new int[line.length - 1][];
				for (int i = 0; i < vertices.length; i++) {
					String vertexData = line[i + 1];
					String[] vertexParts = vertexData.split("/");
					int[] vertex = Arrays.stream(vertexParts).mapToInt(num -> Strings.isNullOrEmpty(num) ? 0 : Integer.parseInt(num)).toArray();
					if (vertex[0] < 0)
						vertex[0] = positions.size() + vertex[0];
					else
						vertex[0]--;
					if (vertex.length > 1) {
						if (vertex[1] < 0)
							vertex[1] = texCoords.size() + vertex[1];
						else
							vertex[1]--;
						if (vertex.length > 2) {
							if (vertex[2] < 0)
								vertex[2] = normals.size() + vertex[2];
							else
								vertex[2]--;
							if (vertex.length > 3) {
								if (vertex[3] < 0)
									vertex[3] = colors.size() + vertex[3];
								else
									vertex[3]--;
							}
						}
					}
					vertices[i] = vertex;
				}

				currentMesh.faces.add(vertices);

				break;
			}

			case "s": // Smoothing group (starts new mesh)
			{
				String smoothingGroup = "off".equals(line[1]) ? null : line[1];
				if (!Objects.equals(currentSmoothingGroup, smoothingGroup)) {
					currentSmoothingGroup = smoothingGroup;
					if (currentMesh != null && currentMesh.smoothingGroup == null && currentMesh.faces.size() == 0) {
						currentMesh.smoothingGroup = currentSmoothingGroup;
					} else {
						// Start new mesh
						currentMesh = null;
					}
				}
				break;
			}

			case "g": {
				String name = line[1];
				if (objAboveGroup) {
					currentObject = new ModelObject(currentGroup.name() + "/" + name);
					currentGroup.parts.put(name, currentObject);
				} else {
					currentGroup = new ModelGroup(name);
					parts.put(name, currentGroup);
					currentObject = null;
				}
				// Start new mesh
				currentMesh = null;
				break;
			}

			case "o": {
				String name = line[1];
				if (objAboveGroup || currentGroup == null) {
					objAboveGroup = true;

					currentGroup = new ModelGroup(name);
					parts.put(name, currentGroup);
					currentObject = null;
				} else {
					currentObject = new ModelObject(currentGroup.name() + "/" + name);
					currentGroup.parts.put(name, currentObject);
				}
				// Start new mesh
				currentMesh = null;
				break;
			}
			}
		}
	}

	public OBJModelCopy setTexture(TextureAtlasSprite sprite) {
		this.texture = sprite;
		return this;
	}

	public TextureAtlasSprite getTexture() {
		if (this.texture != null) {
			return this.texture;
		}
		return null;
	}

	@Override
	public Collection<? extends IModelGeometryPart> getParts() {
		return parts.values();
	}

	@Override
	public Optional<? extends IModelGeometryPart> getPart(String name) {
		return Optional.ofNullable(parts.get(name));
	}

	private Pair<BakedQuad, Direction> makeQuad(int[][] indices, int tintIndex, Vector4f colorTint, Vector4f ambientColor, TextureAtlasSprite texture, Transformation transform) {
		boolean needsNormalRecalculation = false;
		for (int[] ints : indices) {
			needsNormalRecalculation |= ints.length < 3;
		}
		Vector3f faceNormal = new Vector3f(0, 0, 0);
		if (needsNormalRecalculation) {
			Vector3f a = positions.get(indices[0][0]);
			Vector3f ab = positions.get(indices[1][0]);
			Vector3f ac = positions.get(indices[2][0]);
			Vector3f abs = ab.copy();
			abs.sub(a);
			Vector3f acs = ac.copy();
			acs.sub(a);
			abs.cross(acs);
			abs.normalize();
			faceNormal = abs;
		}

		Vector4f[] pos = new Vector4f[4];
		Vector3f[] norm = new Vector3f[4];

		BakedQuadBuilder builder = new BakedQuadBuilder(texture);

		builder.setQuadTint(tintIndex);

		Vec2 uv2 = new Vec2(0, 0);
		if (ambientToFullbright) {
			int fakeLight = (int) ((ambientColor.x() + ambientColor.y() + ambientColor.z()) * 15 / 3.0f);
			uv2 = new Vec2((fakeLight << 4) / 32767.0f, (fakeLight << 4) / 32767.0f);
			builder.setApplyDiffuseLighting(fakeLight == 0);
		} else {
			builder.setApplyDiffuseLighting(diffuseLighting);
		}

		boolean hasTransform = !transform.isIdentity();
		// The incoming transform is referenced on the center of the block, but our
		// coords are referenced on the corner
		Transformation transformation = hasTransform ? transform.blockCenterToCorner() : transform;

		for (int i = 0; i < 4; i++) {
			int[] index = indices[Math.min(i, indices.length - 1)];
			Vector3f pos0 = positions.get(index[0]);
			Vector4f position = new Vector4f(pos0);
			Vec2 texCoord = index.length >= 2 && texCoords.size() > 0 ? texCoords.get(index[1]) : DEFAULT_COORDS[i];
			Vector3f norm0 = !needsNormalRecalculation && index.length >= 3 && normals.size() > 0 ? normals.get(index[2]) : faceNormal;
			Vector3f normal = norm0;
			Vector4f color = index.length >= 4 && colors.size() > 0 ? colors.get(index[3]) : COLOR_WHITE;
			if (hasTransform) {
				normal = norm0.copy();
				transformation.transformPosition(position);
				transformation.transformNormal(normal);
			}
			;
			Vector4f tintedColor = new Vector4f(color.x() * colorTint.x(), color.y() * colorTint.y(), color.z() * colorTint.z(), color.w() * colorTint.w());
			putVertexData(builder, position, texCoord, normal, tintedColor, uv2, texture);
			pos[i] = position;
			norm[i] = normal;
		}

		builder.setQuadOrientation(Direction.getNearest(norm[0].x(), norm[0].y(), norm[0].z()));

		Direction cull = null;
		if (detectCullableFaces) {
			if (Mth.equal(pos[0].x(), 0) && // vertex.position.x
					Mth.equal(pos[1].x(), 0) && Mth.equal(pos[2].x(), 0) && Mth.equal(pos[3].x(), 0) && norm[0].x() < 0) // vertex.normal.x
			{
				cull = Direction.WEST;
			} else if (Mth.equal(pos[0].x(), 1) && // vertex.position.x
					Mth.equal(pos[1].x(), 1) && Mth.equal(pos[2].x(), 1) && Mth.equal(pos[3].x(), 1) && norm[0].x() > 0) // vertex.normal.x
			{
				cull = Direction.EAST;
			} else if (Mth.equal(pos[0].z(), 0) && // vertex.position.z
					Mth.equal(pos[1].z(), 0) && Mth.equal(pos[2].z(), 0) && Mth.equal(pos[3].z(), 0) && norm[0].z() < 0) // vertex.normal.z
			{
				cull = Direction.NORTH; // can never remember
			} else if (Mth.equal(pos[0].z(), 1) && // vertex.position.z
					Mth.equal(pos[1].z(), 1) && Mth.equal(pos[2].z(), 1) && Mth.equal(pos[3].z(), 1) && norm[0].z() > 0) // vertex.normal.z
			{
				cull = Direction.SOUTH;
			} else if (Mth.equal(pos[0].y(), 0) && // vertex.position.y
					Mth.equal(pos[1].y(), 0) && Mth.equal(pos[2].y(), 0) && Mth.equal(pos[3].y(), 0) && norm[0].y() < 0) // vertex.normal.z
			{
				cull = Direction.DOWN; // can never remember
			} else if (Mth.equal(pos[0].y(), 1) && // vertex.position.y
					Mth.equal(pos[1].y(), 1) && Mth.equal(pos[2].y(), 1) && Mth.equal(pos[3].y(), 1) && norm[0].y() > 0) // vertex.normal.y
			{
				cull = Direction.UP;
			}
		}

		return Pair.of(builder.build(), cull);
	}

	private void putVertexData(IVertexConsumer consumer, Vector4f position0, Vec2 texCoord0, Vector3f normal0, Vector4f color0, Vec2 uv2, TextureAtlasSprite texture) {
		ImmutableList<VertexFormatElement> elements = consumer.getVertexFormat().getElements();
		for (int j = 0; j < elements.size(); j++) {
			VertexFormatElement e = elements.get(j);
			switch (e.getUsage()) {
			case POSITION:
				consumer.put(j, position0.x(), position0.y(), position0.z(), position0.w());
				break;
			case COLOR:
				consumer.put(j, color0.x(), color0.y(), color0.z(), color0.w());
				break;
			case UV:
				switch (e.getIndex()) {
				case 0:
					consumer.put(j, texture.getU(texCoord0.x * 16), texture.getV((flipV ? (1 - texCoord0.y) : texCoord0.y) * 16));
					break;
				case 2:
					consumer.put(j, uv2.x, uv2.y);
					break;
				default:
					consumer.put(j);
					break;
				}
				break;
			case NORMAL:
				consumer.put(j, normal0.x(), normal0.y(), normal0.z());
				break;
			default:
				consumer.put(j);
				break;
			}
		}
	}

	public class ModelObject implements IModelGeometryPart {
		public final String name;

		List<ModelMesh> meshes = Lists.newArrayList();

		ModelObject(String name) {
			this.name = name;
		}

		@Override
		public String name() {
			return name;
		}

		@Override
		public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation) {
			for (ModelMesh mesh : meshes) {
				MaterialLibrary.Material mat = mesh.mat;
				if (mat == null)
					continue;

				TextureAtlasSprite texture = getTexture();
				if (texture == null)
					texture = spriteGetter.apply(ModelLoaderRegistry.resolveTexture(mat.diffuseColorMap, owner));

				int tintIndex = mat.diffuseTintIndex;
				Vector4f colorTint = mat.diffuseColor;

				for (int[][] face : mesh.faces) {
					Pair<BakedQuad, Direction> quad = makeQuad(face, tintIndex, colorTint, mat.ambientColor, texture, modelTransform.getRotation());
					if (quad.getRight() == null)
						modelBuilder.addGeneralQuad(quad.getLeft());
					else
						modelBuilder.addFaceQuad(quad.getRight(), quad.getLeft());
				}
			}
		}

		@Override
		public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<com.mojang.datafixers.util.Pair<String, String>> missingTextureErrors) {
			return meshes.stream().map(mesh -> ModelLoaderRegistry.resolveTexture(mesh.mat.diffuseColorMap, owner)).collect(Collectors.toSet());
		}
	}

	public class ModelGroup extends ModelObject {
		final Map<String, ModelObject> parts = Maps.newHashMap();

		ModelGroup(String name) {
			super(name);
		}

		public Collection<? extends IModelGeometryPart> getParts() {
			return parts.values();
		}

		@Override
		public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation) {
			super.addQuads(owner, modelBuilder, bakery, spriteGetter, modelTransform, modelLocation);

			getParts().stream().filter(part -> owner.getPartVisibility(part)).forEach(part -> part.addQuads(owner, modelBuilder, bakery, spriteGetter, modelTransform, modelLocation));
		}

		@Override
		public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<com.mojang.datafixers.util.Pair<String, String>> missingTextureErrors) {
			Set<Material> combined = Sets.newHashSet();
			combined.addAll(super.getTextures(owner, modelGetter, missingTextureErrors));
			for (IModelGeometryPart part : getParts())
				combined.addAll(part.getTextures(owner, modelGetter, missingTextureErrors));
			return combined;
		}
	}

	private class ModelMesh {
		@Nullable
		public MaterialLibrary.Material mat;
		@Nullable
		public String smoothingGroup;
		public final List<int[][]> faces = Lists.newArrayList();

		public ModelMesh(@Nullable MaterialLibrary.Material currentMat, @Nullable String currentSmoothingGroup) {
			this.mat = currentMat;
			this.smoothingGroup = currentSmoothingGroup;
		}
	}
}
