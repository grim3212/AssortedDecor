package com.grim3212.assorted.decor.client.render.entity;

import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.entity.WallpaperEntity;
import com.grim3212.assorted.decor.common.handler.DecorConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WallpaperRenderer extends EntityRenderer<WallpaperEntity> {

	private static final ResourceLocation wallpaperTexture = new ResourceLocation(AssortedDecor.MODID, "textures/entity/wallpapers.png");

	public WallpaperRenderer(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	public void render(WallpaperEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		matrixStackIn.pushPose();

		matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F - entityYaw));
		matrixStackIn.scale(0.03125F, 0.03125F, 0.03125F);
		renderWallpaper(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);

		matrixStackIn.popPose();
	}

	public void renderWallpaper(WallpaperEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		IVertexBuilder builder = bufferIn.getBuffer(RenderType.entitySolid(wallpaperTexture));
		MatrixStack.Entry matrixstack$entry = matrixStackIn.last();
		Matrix4f matrix4f = matrixstack$entry.pose();
		Matrix3f matrix3f = matrixstack$entry.normal();

		int x = MathHelper.floor(entityIn.getPos().getX());
		int y = MathHelper.floor(entityIn.getPos().getY());
		int z = MathHelper.floor(entityIn.getPos().getZ());

		float minX = -16.0F;
		float minY = -16.0F;
		float minZ = 0.0F;

		float maxX = 16.0F;
		float maxY = 16.0F;
		float maxZ = DecorConfig.CLIENT.widthWallpaper.get().floatValue();

		float minU = entityIn.getWallpaperID() / 16 / 16.0F;
		float minV = entityIn.getWallpaperID() % 16 / 16.0F;

		float maxUV = 0.0625F;
		float sideUV = 0.00195313F;

		float red = entityIn.getWallpaperColor()[0] / 255.0f;
		float green = entityIn.getWallpaperColor()[1] / 255.0f;
		float blue = entityIn.getWallpaperColor()[2] / 255.0f;
		
		int light = WorldRenderer.getLightColor(entityIn.level, new BlockPos(x, y, z));

		if (entityIn.getDirection() == Direction.NORTH) {
			builder.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, 255).uv(minU + maxUV, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
			builder.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, 255).uv(minU + maxUV, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
			builder.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, 255).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
			builder.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, 255).uv(minU, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		} else if (entityIn.getDirection() == Direction.SOUTH) {
			builder.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, 255).uv(minU + maxUV, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
			builder.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, 255).uv(minU + maxUV, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
			builder.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, 255).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
			builder.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, 255).uv(minU, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		} else if (entityIn.getDirection() == Direction.WEST) {
			builder.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, 255).uv(minU + maxUV, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
			builder.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, 255).uv(minU + maxUV, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
			builder.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, 255).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
			builder.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, 255).uv(minU, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		} else if (entityIn.getDirection() == Direction.EAST) {
			builder.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, 255).uv(minU + maxUV, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
			builder.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, 255).uv(minU + maxUV, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
			builder.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, 255).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
			builder.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, 255).uv(minU, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		}

		if (!entityIn.isBlockLeft) {
			if (entityIn.getDirection() == Direction.NORTH) {
				builder.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, 255).uv(minU + maxUV, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, 255).uv(minU + maxUV, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, 255).uv(minU + maxUV - sideUV, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, 255).uv(minU + maxUV - sideUV, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
			} else if (entityIn.getDirection() == Direction.SOUTH) {
				builder.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, 255).uv(minU + maxUV, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, 255).uv(minU + maxUV, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, 255).uv(minU + maxUV - sideUV, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, 255).uv(minU + maxUV - sideUV, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
			} else if (entityIn.getDirection() == Direction.WEST) {
				builder.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, 255).uv(minU + maxUV, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, 255).uv(minU + maxUV, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, 255).uv(minU + maxUV - sideUV, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, 255).uv(minU + maxUV - sideUV, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
			} else if (entityIn.getDirection() == Direction.EAST) {
				builder.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, 255).uv(minU + maxUV, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, 255).uv(minU + maxUV, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, 255).uv(minU + maxUV - sideUV, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, 255).uv(minU + maxUV - sideUV, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
			}
		}
		if (!entityIn.isBlockUp) {
			if (entityIn.getDirection() == Direction.NORTH) {
				builder.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, 255).uv(minU + maxUV, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, 255).uv(minU + maxUV, minV + sideUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, 255).uv(minU, minV + sideUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, 255).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
			} else if (entityIn.getDirection() == Direction.SOUTH) {
				builder.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, 255).uv(minU, minV + sideUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, 255).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, 255).uv(minU + maxUV, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, 255).uv(minU + maxUV, minV + sideUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
			} else if (entityIn.getDirection() == Direction.WEST) {
				builder.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, 255).uv(minU, minV + sideUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, 255).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, 255).uv(minU + maxUV, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, 255).uv(minU + maxUV, minV + sideUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
			} else if (entityIn.getDirection() == Direction.EAST) {
				builder.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, 255).uv(minU, minV + sideUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, 255).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, 255).uv(minU + maxUV, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, 255).uv(minU + maxUV, minV + sideUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
			}
		}
		if (!entityIn.isBlockRight) {
			if (entityIn.getDirection() == Direction.NORTH) {
				builder.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, 255).uv(minU + sideUV, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, 255).uv(minU, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, 255).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, 255).uv(minU + sideUV, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
			} else if (entityIn.getDirection() == Direction.SOUTH) {
				builder.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, 255).uv(minU + sideUV, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, 255).uv(minU, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, 255).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, 255).uv(minU + sideUV, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
			} else if (entityIn.getDirection() == Direction.WEST) {
				builder.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, 255).uv(minU + sideUV, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, 255).uv(minU, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, 255).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, 255).uv(minU + sideUV, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
			} else if (entityIn.getDirection() == Direction.EAST) {
				builder.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, 255).uv(minU + sideUV, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, 255).uv(minU, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, 255).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, 255).uv(minU + sideUV, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
			}
		}
		if (!entityIn.isBlockDown) {
			if (entityIn.getDirection() == Direction.NORTH) {
				builder.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, 255).uv(minU + maxUV, minV + maxUV - sideUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, 255).uv(minU + maxUV, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, 255).uv(minU, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, 255).uv(minU, minV + maxUV - sideUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
			} else if (entityIn.getDirection() == Direction.SOUTH) {
				builder.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, 255).uv(minU + maxUV, minV + maxUV - sideUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, 255).uv(minU + maxUV, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, 255).uv(minU, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, 255).uv(minU, minV + maxUV - sideUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
			} else if (entityIn.getDirection() == Direction.WEST) {
				builder.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, 255).uv(minU + maxUV, minV + maxUV - sideUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, 255).uv(minU + maxUV, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, 255).uv(minU, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, 255).uv(minU, minV + maxUV - sideUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
			} else if (entityIn.getDirection() == Direction.EAST) {
				builder.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, 255).uv(minU + maxUV, minV + maxUV - sideUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, 255).uv(minU + maxUV, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, 255).uv(minU, minV + maxUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
				builder.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, 255).uv(minU, minV + maxUV - sideUV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
			}
		}
	}

	@Override
	public ResourceLocation getTextureLocation(WallpaperEntity entity) {
		return wallpaperTexture;
	}
}
