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
		matrixStackIn.push();

		matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F - entityYaw));
		matrixStackIn.scale(0.03125F, 0.03125F, 0.03125F);
		renderWallpaper(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);

		matrixStackIn.pop();
	}

	public void renderWallpaper(WallpaperEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		IVertexBuilder builder = bufferIn.getBuffer(RenderType.getEntitySolid(wallpaperTexture));
		MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast();
		Matrix4f matrix4f = matrixstack$entry.getMatrix();
		Matrix3f matrix3f = matrixstack$entry.getNormal();

		int x = MathHelper.floor(entityIn.getHangingPosition().getX());
		int y = MathHelper.floor(entityIn.getHangingPosition().getY());
		int z = MathHelper.floor(entityIn.getHangingPosition().getZ());

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
		
		int light = WorldRenderer.getCombinedLight(entityIn.world, new BlockPos(x, y, z));

		if (entityIn.getHorizontalFacing() == Direction.NORTH) {
			builder.pos(matrix4f, minX, minY, minZ).color(red, green, blue, 255).tex(minU + maxUV, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
			builder.pos(matrix4f, minX, maxY, minZ).color(red, green, blue, 255).tex(minU + maxUV, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
			builder.pos(matrix4f, maxX, maxY, minZ).color(red, green, blue, 255).tex(minU, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
			builder.pos(matrix4f, maxX, minY, minZ).color(red, green, blue, 255).tex(minU, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		} else if (entityIn.getHorizontalFacing() == Direction.SOUTH) {
			builder.pos(matrix4f, minX, minY, minZ).color(red, green, blue, 255).tex(minU + maxUV, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
			builder.pos(matrix4f, minX, maxY, minZ).color(red, green, blue, 255).tex(minU + maxUV, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
			builder.pos(matrix4f, maxX, maxY, minZ).color(red, green, blue, 255).tex(minU, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
			builder.pos(matrix4f, maxX, minY, minZ).color(red, green, blue, 255).tex(minU, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		} else if (entityIn.getHorizontalFacing() == Direction.WEST) {
			builder.pos(matrix4f, minX, minY, minZ).color(red, green, blue, 255).tex(minU + maxUV, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
			builder.pos(matrix4f, minX, maxY, minZ).color(red, green, blue, 255).tex(minU + maxUV, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
			builder.pos(matrix4f, maxX, maxY, minZ).color(red, green, blue, 255).tex(minU, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
			builder.pos(matrix4f, maxX, minY, minZ).color(red, green, blue, 255).tex(minU, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		} else if (entityIn.getHorizontalFacing() == Direction.EAST) {
			builder.pos(matrix4f, minX, minY, minZ).color(red, green, blue, 255).tex(minU + maxUV, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
			builder.pos(matrix4f, minX, maxY, minZ).color(red, green, blue, 255).tex(minU + maxUV, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
			builder.pos(matrix4f, maxX, maxY, minZ).color(red, green, blue, 255).tex(minU, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
			builder.pos(matrix4f, maxX, minY, minZ).color(red, green, blue, 255).tex(minU, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		}

		if (!entityIn.isBlockLeft) {
			if (entityIn.getHorizontalFacing() == Direction.NORTH) {
				builder.pos(matrix4f, minX, maxY, maxZ).color(red, green, blue, 255).tex(minU + maxUV, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
				builder.pos(matrix4f, minX, maxY, minZ).color(red, green, blue, 255).tex(minU + maxUV, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
				builder.pos(matrix4f, minX, minY, minZ).color(red, green, blue, 255).tex(minU + maxUV - sideUV, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
				builder.pos(matrix4f, minX, minY, maxZ).color(red, green, blue, 255).tex(minU + maxUV - sideUV, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
			} else if (entityIn.getHorizontalFacing() == Direction.SOUTH) {
				builder.pos(matrix4f, minX, maxY, maxZ).color(red, green, blue, 255).tex(minU + maxUV, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
				builder.pos(matrix4f, minX, maxY, minZ).color(red, green, blue, 255).tex(minU + maxUV, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
				builder.pos(matrix4f, minX, minY, minZ).color(red, green, blue, 255).tex(minU + maxUV - sideUV, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
				builder.pos(matrix4f, minX, minY, maxZ).color(red, green, blue, 255).tex(minU + maxUV - sideUV, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
			} else if (entityIn.getHorizontalFacing() == Direction.WEST) {
				builder.pos(matrix4f, minX, maxY, maxZ).color(red, green, blue, 255).tex(minU + maxUV, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
				builder.pos(matrix4f, minX, maxY, minZ).color(red, green, blue, 255).tex(minU + maxUV, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
				builder.pos(matrix4f, minX, minY, minZ).color(red, green, blue, 255).tex(minU + maxUV - sideUV, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
				builder.pos(matrix4f, minX, minY, maxZ).color(red, green, blue, 255).tex(minU + maxUV - sideUV, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
			} else if (entityIn.getHorizontalFacing() == Direction.EAST) {
				builder.pos(matrix4f, minX, maxY, maxZ).color(red, green, blue, 255).tex(minU + maxUV, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
				builder.pos(matrix4f, minX, maxY, minZ).color(red, green, blue, 255).tex(minU + maxUV, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
				builder.pos(matrix4f, minX, minY, minZ).color(red, green, blue, 255).tex(minU + maxUV - sideUV, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
				builder.pos(matrix4f, minX, minY, maxZ).color(red, green, blue, 255).tex(minU + maxUV - sideUV, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
			}
		}
		if (!entityIn.isBlockUp) {
			if (entityIn.getHorizontalFacing() == Direction.NORTH) {
				builder.pos(matrix4f, minX, maxY, minZ).color(red, green, blue, 255).tex(minU + maxUV, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
				builder.pos(matrix4f, minX, maxY, maxZ).color(red, green, blue, 255).tex(minU + maxUV, minV + sideUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
				builder.pos(matrix4f, maxX, maxY, maxZ).color(red, green, blue, 255).tex(minU, minV + sideUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
				builder.pos(matrix4f, maxX, maxY, minZ).color(red, green, blue, 255).tex(minU, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
			} else if (entityIn.getHorizontalFacing() == Direction.SOUTH) {
				builder.pos(matrix4f, maxX, maxY, maxZ).color(red, green, blue, 255).tex(minU, minV + sideUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
				builder.pos(matrix4f, maxX, maxY, minZ).color(red, green, blue, 255).tex(minU, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
				builder.pos(matrix4f, minX, maxY, minZ).color(red, green, blue, 255).tex(minU + maxUV, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
				builder.pos(matrix4f, minX, maxY, maxZ).color(red, green, blue, 255).tex(minU + maxUV, minV + sideUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
			} else if (entityIn.getHorizontalFacing() == Direction.WEST) {
				builder.pos(matrix4f, maxX, maxY, maxZ).color(red, green, blue, 255).tex(minU, minV + sideUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
				builder.pos(matrix4f, maxX, maxY, minZ).color(red, green, blue, 255).tex(minU, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
				builder.pos(matrix4f, minX, maxY, minZ).color(red, green, blue, 255).tex(minU + maxUV, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
				builder.pos(matrix4f, minX, maxY, maxZ).color(red, green, blue, 255).tex(minU + maxUV, minV + sideUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
			} else if (entityIn.getHorizontalFacing() == Direction.EAST) {
				builder.pos(matrix4f, maxX, maxY, maxZ).color(red, green, blue, 255).tex(minU, minV + sideUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
				builder.pos(matrix4f, maxX, maxY, minZ).color(red, green, blue, 255).tex(minU, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
				builder.pos(matrix4f, minX, maxY, minZ).color(red, green, blue, 255).tex(minU + maxUV, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
				builder.pos(matrix4f, minX, maxY, maxZ).color(red, green, blue, 255).tex(minU + maxUV, minV + sideUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
			}
		}
		if (!entityIn.isBlockRight) {
			if (entityIn.getHorizontalFacing() == Direction.NORTH) {
				builder.pos(matrix4f, maxX, minY, maxZ).color(red, green, blue, 255).tex(minU + sideUV, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
				builder.pos(matrix4f, maxX, minY, minZ).color(red, green, blue, 255).tex(minU, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
				builder.pos(matrix4f, maxX, maxY, minZ).color(red, green, blue, 255).tex(minU, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
				builder.pos(matrix4f, maxX, maxY, maxZ).color(red, green, blue, 255).tex(minU + sideUV, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
			} else if (entityIn.getHorizontalFacing() == Direction.SOUTH) {
				builder.pos(matrix4f, maxX, minY, maxZ).color(red, green, blue, 255).tex(minU + sideUV, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
				builder.pos(matrix4f, maxX, minY, minZ).color(red, green, blue, 255).tex(minU, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
				builder.pos(matrix4f, maxX, maxY, minZ).color(red, green, blue, 255).tex(minU, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
				builder.pos(matrix4f, maxX, maxY, maxZ).color(red, green, blue, 255).tex(minU + sideUV, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
			} else if (entityIn.getHorizontalFacing() == Direction.WEST) {
				builder.pos(matrix4f, maxX, minY, maxZ).color(red, green, blue, 255).tex(minU + sideUV, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
				builder.pos(matrix4f, maxX, minY, minZ).color(red, green, blue, 255).tex(minU, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
				builder.pos(matrix4f, maxX, maxY, minZ).color(red, green, blue, 255).tex(minU, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
				builder.pos(matrix4f, maxX, maxY, maxZ).color(red, green, blue, 255).tex(minU + sideUV, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
			} else if (entityIn.getHorizontalFacing() == Direction.EAST) {
				builder.pos(matrix4f, maxX, minY, maxZ).color(red, green, blue, 255).tex(minU + sideUV, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
				builder.pos(matrix4f, maxX, minY, minZ).color(red, green, blue, 255).tex(minU, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
				builder.pos(matrix4f, maxX, maxY, minZ).color(red, green, blue, 255).tex(minU, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
				builder.pos(matrix4f, maxX, maxY, maxZ).color(red, green, blue, 255).tex(minU + sideUV, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
			}
		}
		if (!entityIn.isBlockDown) {
			if (entityIn.getHorizontalFacing() == Direction.NORTH) {
				builder.pos(matrix4f, minX, minY, maxZ).color(red, green, blue, 255).tex(minU + maxUV, minV + maxUV - sideUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
				builder.pos(matrix4f, minX, minY, minZ).color(red, green, blue, 255).tex(minU + maxUV, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
				builder.pos(matrix4f, maxX, minY, minZ).color(red, green, blue, 255).tex(minU, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
				builder.pos(matrix4f, maxX, minY, maxZ).color(red, green, blue, 255).tex(minU, minV + maxUV - sideUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
			} else if (entityIn.getHorizontalFacing() == Direction.SOUTH) {
				builder.pos(matrix4f, minX, minY, maxZ).color(red, green, blue, 255).tex(minU + maxUV, minV + maxUV - sideUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
				builder.pos(matrix4f, minX, minY, minZ).color(red, green, blue, 255).tex(minU + maxUV, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
				builder.pos(matrix4f, maxX, minY, minZ).color(red, green, blue, 255).tex(minU, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
				builder.pos(matrix4f, maxX, minY, maxZ).color(red, green, blue, 255).tex(minU, minV + maxUV - sideUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
			} else if (entityIn.getHorizontalFacing() == Direction.WEST) {
				builder.pos(matrix4f, minX, minY, maxZ).color(red, green, blue, 255).tex(minU + maxUV, minV + maxUV - sideUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
				builder.pos(matrix4f, minX, minY, minZ).color(red, green, blue, 255).tex(minU + maxUV, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
				builder.pos(matrix4f, maxX, minY, minZ).color(red, green, blue, 255).tex(minU, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
				builder.pos(matrix4f, maxX, minY, maxZ).color(red, green, blue, 255).tex(minU, minV + maxUV - sideUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
			} else if (entityIn.getHorizontalFacing() == Direction.EAST) {
				builder.pos(matrix4f, minX, minY, maxZ).color(red, green, blue, 255).tex(minU + maxUV, minV + maxUV - sideUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
				builder.pos(matrix4f, minX, minY, minZ).color(red, green, blue, 255).tex(minU + maxUV, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
				builder.pos(matrix4f, maxX, minY, minZ).color(red, green, blue, 255).tex(minU, minV + maxUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
				builder.pos(matrix4f, maxX, minY, maxZ).color(red, green, blue, 255).tex(minU, minV + maxUV - sideUV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
			}
		}
	}

	@Override
	public ResourceLocation getEntityTexture(WallpaperEntity entity) {
		return wallpaperTexture;
	}
}
