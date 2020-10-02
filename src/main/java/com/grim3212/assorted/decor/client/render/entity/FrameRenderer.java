package com.grim3212.assorted.decor.client.render.entity;

import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.entity.FrameEntity;
import com.grim3212.assorted.decor.common.entity.FrameEntity.FrameType;
import com.grim3212.assorted.decor.common.item.FrameItem.FrameMaterial;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FrameRenderer extends EntityRenderer<FrameEntity> {

	private static final ResourceLocation framesTexture = new ResourceLocation(AssortedDecor.MODID, "textures/entity/frames.png");

	public FrameRenderer(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	public void render(FrameEntity entityIn, float entityInYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		matrixStackIn.push();

		matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F - entityInYaw));
		renderBeams(entityIn, entityInYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);

		matrixStackIn.pop();
	}

	private void renderBeams(FrameEntity entityIn, float entityInYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		FrameType frame = entityIn.getCurrentFrame();

		IVertexBuilder builder = bufferIn.getBuffer(RenderType.getEntitySolid(framesTexture));
		MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast();
		Matrix4f matrix4f = matrixstack$entry.getMatrix();
		Matrix3f matrix3f = matrixstack$entry.getNormal();

		matrixStackIn.scale(frame.sizeX / 256.0F + 0.001F, frame.sizeY / 256.0F + 0.001F, 0.0625F);

		float xPos = -8.0F;
		float yPos = -8.0F;

		int[] planks = frame.planks;
		FrameRender[] renderFrames = FrameRender.values();

		for (int i = 0; i < planks.length; i++) {
			int currentPlank = planks[i];
			float zFront = renderFrames[currentPlank].zFront;
			float zBack = renderFrames[currentPlank].zBack;

			int mod = entityIn.getMaterial() == FrameMaterial.WOOD ? 0 : 1;

			float u1 = 0.5F * mod;
			float u2 = 0.5F * (mod + renderFrames[currentPlank].texSize);
			float u3 = 0.5F * (mod + 0.5F);

			if (!frame.isCollidable) {
				u3 = 0.5F * (mod + 1.0F);
			}

			float sizeX = frame.sizeX / 16.0F;
			float sizeY = frame.sizeY / 16.0F;
			float f1 = (float) Math.sqrt(Math.pow((renderFrames[currentPlank].x2 - renderFrames[currentPlank].x1) * sizeX, 2.0D) + Math.pow((renderFrames[currentPlank].y2 - renderFrames[currentPlank].y1) * sizeY, 2.0D));
			float f2 = (float) Math.sqrt(Math.pow((renderFrames[currentPlank].x4 - renderFrames[currentPlank].x3) * sizeX, 2.0D) + Math.pow((renderFrames[currentPlank].y4 - renderFrames[currentPlank].y3) * sizeY, 2.0D));
			float f3 = (f2 - f1) / (f2 * 2.0F);
			float v1 = f2 / 32.0F;
			float v2 = f3 * v1;
			float v3 = (1.0F - f3) * v1;

			float red = entityIn.getFrameColor()[0] / 255.0f;
			float green = entityIn.getFrameColor()[1] / 255.0f;
			float blue = entityIn.getFrameColor()[2] / 255.0f;

			int light = WorldRenderer.getCombinedLight(entityIn.world, entityIn.getHangingPosition());

			builder.pos(matrix4f, xPos + renderFrames[currentPlank].x1, yPos + renderFrames[currentPlank].y1, zFront).color(red, green, blue, 255).tex(u1, v2).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 0.0F, -1.0F).endVertex();
			builder.pos(matrix4f, xPos + renderFrames[currentPlank].x2, yPos + renderFrames[currentPlank].y2, zFront).color(red, green, blue, 255).tex(u1, v3).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 0.0F, -1.0F).endVertex();
			builder.pos(matrix4f, xPos + renderFrames[currentPlank].x3, yPos + renderFrames[currentPlank].y3, zFront).color(red, green, blue, 255).tex(u2, v1).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 0.0F, -1.0F).endVertex();
			builder.pos(matrix4f, xPos + renderFrames[currentPlank].x4, yPos + renderFrames[currentPlank].y4, zFront).color(red, green, blue, 255).tex(u2, 0F).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 0.0F, -1.0F).endVertex();

			builder.pos(matrix4f, xPos + renderFrames[currentPlank].x4, yPos + renderFrames[currentPlank].y4, zBack).color(red, green, blue, 255).tex(u2, 0F).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
			builder.pos(matrix4f, xPos + renderFrames[currentPlank].x3, yPos + renderFrames[currentPlank].y3, zBack).color(red, green, blue, 255).tex(u2, v1).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
			builder.pos(matrix4f, xPos + renderFrames[currentPlank].x2, yPos + renderFrames[currentPlank].y2, zBack).color(red, green, blue, 255).tex(u1, v3).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
			builder.pos(matrix4f, xPos + renderFrames[currentPlank].x1, yPos + renderFrames[currentPlank].y1, zBack).color(red, green, blue, 255).tex(u1, v2).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();

			builder.pos(matrix4f, xPos + renderFrames[currentPlank].x2, yPos + renderFrames[currentPlank].y2, zFront).color(red, green, blue, 255).tex(u3, 0F).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
			builder.pos(matrix4f, xPos + renderFrames[currentPlank].x1, yPos + renderFrames[currentPlank].y1, zFront).color(red, green, blue, 255).tex(u3, v1).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
			builder.pos(matrix4f, xPos + renderFrames[currentPlank].x1, yPos + renderFrames[currentPlank].y1, zBack).color(red, green, blue, 255).tex(u1, v1).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
			builder.pos(matrix4f, xPos + renderFrames[currentPlank].x2, yPos + renderFrames[currentPlank].y2, zBack).color(red, green, blue, 255).tex(u1, 0F).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();

			builder.pos(matrix4f, xPos + renderFrames[currentPlank].x3, yPos + renderFrames[currentPlank].y3, zFront).color(red, green, blue, 255).tex(u3, v1 / 3.0F).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
			builder.pos(matrix4f, xPos + renderFrames[currentPlank].x2, yPos + renderFrames[currentPlank].y2, zFront).color(red, green, blue, 255).tex(u3, 0F).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
			builder.pos(matrix4f, xPos + renderFrames[currentPlank].x2, yPos + renderFrames[currentPlank].y2, zBack).color(red, green, blue, 255).tex(u1, 0F).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
			builder.pos(matrix4f, xPos + renderFrames[currentPlank].x3, yPos + renderFrames[currentPlank].y3, zBack).color(red, green, blue, 255).tex(u1, v1 / 3.0F).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();

			builder.pos(matrix4f, xPos + renderFrames[currentPlank].x4, yPos + renderFrames[currentPlank].y4, zFront).color(red, green, blue, 255).tex(u3, 0F).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
			builder.pos(matrix4f, xPos + renderFrames[currentPlank].x3, yPos + renderFrames[currentPlank].y3, zFront).color(red, green, blue, 255).tex(u3, v1).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
			builder.pos(matrix4f, xPos + renderFrames[currentPlank].x3, yPos + renderFrames[currentPlank].y3, zBack).color(red, green, blue, 255).tex(u1, v1).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
			builder.pos(matrix4f, xPos + renderFrames[currentPlank].x4, yPos + renderFrames[currentPlank].y4, zBack).color(red, green, blue, 255).tex(u1, 0F).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();

			builder.pos(matrix4f, xPos + renderFrames[currentPlank].x1, yPos + renderFrames[currentPlank].y1, zFront).color(red, green, blue, 255).tex(u3, v1 / 3.0F).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
			builder.pos(matrix4f, xPos + renderFrames[currentPlank].x4, yPos + renderFrames[currentPlank].y4, zFront).color(red, green, blue, 255).tex(u3, 0F).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
			builder.pos(matrix4f, xPos + renderFrames[currentPlank].x4, yPos + renderFrames[currentPlank].y4, zBack).color(red, green, blue, 255).tex(u1, 0F).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
			builder.pos(matrix4f, xPos + renderFrames[currentPlank].x1, yPos + renderFrames[currentPlank].y1, zBack).color(red, green, blue, 255).tex(u1, v1 / 3.0F).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();

		}
	}

	@Override
	public ResourceLocation getEntityTexture(FrameEntity entity) {
		return framesTexture;
	}

	private static enum FrameRender {
		plank_00(2, 2, 14, 2, 16, 0, 0, 0, 0.5F, -0.3F, 4.0F),
		plank_01(2, 14, 2, 2, 0, 0, 0, 16, 0.5F, -0.3F, 4.0F),
		plank_02(14, 14, 2, 14, 0, 16, 16, 16, 0.5F, -0.3F, 4.0F),
		plank_03(14, 2, 14, 14, 16, 16, 16, 0, 0.5F, -0.3F, 4.0F),
		plank_04(6, 2, 6, 14, 10, 14, 10, 2, 1.0F, -0.3F, 4.0F),
		plank_05(2, 10, 14, 10, 14, 6, 2, 6, 1.0F, -0.1F, 3.8F),
		plank_06(14, 12, 4, 2, 2, 2, 14, 14, 0.5F, -0.3F, 4.0F),
		plank_07(2, 4, 12, 14, 14, 14, 2, 2, 0.5F, -0.3F, 4.0F),
		plank_08(12, 2, 2, 12, 2, 14, 14, 2, 0.5F, -0.1F, 3.8F),
		plank_09(4, 14, 14, 4, 14, 2, 2, 14, 0.5F, -0.1F, 3.8F),
		plank_10(1, 2, 15, 2, 16, 0, 0, 0, 0.5F, -0.3F, 4.0F),
		plank_11(1, 14, 1, 2, 0, 0, 0, 16, 0.5F, -0.3F, 4.0F),
		plank_12(15, 14, 1, 14, 0, 16, 16, 16, 0.5F, -0.3F, 4.0F),
		plank_13(15, 2, 15, 14, 16, 16, 16, 0, 0.5F, -0.3F, 4.0F),
		plank_14(15, 12, 3, 2, 1, 2, 15, 14, 0.5F, -0.3F, 4.0F),
		plank_15(1, 4, 13, 14, 15, 14, 1, 2, 0.5F, -0.3F, 4.0F),
		plank_16(13, 2, 1, 12, 1, 14, 15, 2, 0.5F, -0.1F, 3.8F),
		plank_17(3, 14, 15, 4, 15, 2, 1, 14, 0.5F, -0.1F, 3.8F),
		plank_18(14, 10, 6, 2, 2, 2, 14, 14, 1.0F, -0.3F, 4.0F),
		plank_19(10, 2, 2, 10, 2, 14, 14, 2, 1.0F, -0.3F, 4.0F),
		plank_20(4, 4, 12, 4, 16, 0, 0, 0, 0.5F, -0.3F, 4.0F),
		plank_21(4, 12, 4, 4, 0, 0, 0, 16, 0.5F, -0.3F, 4.0F),
		plank_22(12, 12, 4, 12, 0, 16, 16, 16, 0.5F, -0.3F, 4.0F),
		plank_23(12, 4, 12, 12, 16, 16, 16, 0, 0.5F, -0.3F, 4.0F),
		plank_24(2, 4, 14, 4, 16, 0, 0, 0, 0.5F, -0.3F, 4.0F),
		plank_25(2, 12, 2, 4, 0, 0, 0, 16, 0.5F, -0.3F, 4.0F),
		plank_26(14, 12, 2, 12, 0, 16, 16, 16, 0.5F, -0.3F, 4.0F),
		plank_27(14, 4, 14, 12, 16, 16, 16, 0, 0.5F, -0.3F, 4.0F),
		plank_28(4, 2, 12, 2, 16, 0, 0, 0, 0.5F, -0.3F, 4.0F),
		plank_29(4, 14, 4, 2, 0, 0, 0, 16, 0.5F, -0.3F, 4.0F),
		plank_30(12, 14, 4, 14, 0, 16, 16, 16, 0.5F, -0.3F, 4.0F),
		plank_31(12, 2, 12, 14, 16, 16, 16, 0, 0.5F, -0.3F, 4.0F),
		plank_32(14, 15, 2, 15, 0, 16, 16, 16, 0.5F, -0.3F, 9.0F),
		plank_33(2, 15, 2, 0, 0, 0, 0, 16, 0.5F, -0.3F, 9.0F),
		plank_34(14, 0, 14, 15, 16, 16, 16, 0, 0.5F, -0.3F, 9.0F),
		plank_35(15, 15, 1, 15, 0, 16, 16, 16, 0.5F, -0.3F, 9.0F),
		plank_36(1, 15, 1, 0, 0, 0, 0, 16, 0.5F, -0.3F, 9.0F),
		plank_37(15, 0, 15, 15, 16, 16, 16, 0, 0.5F, -0.3F, 9.0F),
		plank_38(4, 0, 4, 16, 12, 16, 12, 0, 1.0F, -0.3F, 7.7F),
		plank_39(16, 4, 0, 4, 0, 12, 16, 12, 1.0F, 0.1F, 7.3F),
		plank_40(2, 6, 10, 14, 14, 14, 2, 2, 1.0F, -0.3F, 4.0F),
		plank_41(6, 14, 14, 6, 14, 2, 2, 14, 1.0F, -0.3F, 4.0F),
		plank_42(12, 8, 8, 4, 4, 4, 12, 12, 0.5F, -0.3F, 4.0F),
		plank_43(4, 8, 8, 12, 12, 12, 4, 4, 0.5F, -0.3F, 4.0F),
		plank_44(8, 4, 4, 8, 4, 12, 12, 4, 0.5F, -0.3F, 4.0F),
		plank_45(8, 12, 12, 8, 12, 4, 4, 12, 0.5F, -0.3F, 4.0F),
		plank_46(16, 12, 4, 0, 0, 0, 16, 16, 0.5F, -0.3F, 4.0F),
		plank_47(0, 4, 12, 16, 16, 16, 0, 0, 0.5F, -0.3F, 4.0F),
		plank_48(12, 0, 0, 12, 0, 16, 16, 0, 0.5F, -0.1F, 3.8F),
		plank_49(4, 16, 16, 4, 16, 0, 0, 16, 0.5F, -0.1F, 3.8F),
		plank_50(16, 12, 0, 12, 0, 16, 16, 16, 0.5F, -0.3F, 4.0F),
		plank_51(0, 4, 16, 4, 16, 0, 0, 0, 0.5F, -0.3F, 4.0F),
		plank_52(4, 16, 4, 0, 0, 0, 0, 16, 0.5F, -0.3F, 4.0F),
		plank_53(12, 0, 12, 16, 16, 16, 16, 0, 0.5F, -0.3F, 4.0F),
		plank_54(16, 14, 2, 0, 0, 0, 16, 16, 0.5F, -0.3F, 4.0F),
		plank_55(0, 2, 14, 16, 16, 16, 0, 0, 0.5F, -0.3F, 4.0F),
		plank_56(14, 0, 0, 14, 0, 16, 16, 0, 0.5F, -0.1F, 3.8F),
		plank_57(2, 16, 16, 2, 16, 0, 0, 16, 0.5F, -0.1F, 3.8F);

		public final int x1;
		public final int x2;
		public final int x3;
		public final int x4;
		public final int y1;
		public final int y2;
		public final int y3;
		public final int y4;
		public final float texSize;
		public final float zFront;
		public final float zBack;

		private FrameRender(int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, float var11, float var12, float var13) {
			this.x1 = var3;
			this.x2 = var5;
			this.x3 = var7;
			this.x4 = var9;
			this.y1 = var4;
			this.y2 = var6;
			this.y3 = var8;
			this.y4 = var10;
			this.texSize = var11;
			this.zFront = var12;
			this.zBack = var13;
		}
	}
}