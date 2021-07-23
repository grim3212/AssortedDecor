package com.grim3212.assorted.decor.client.tileentity;

import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.client.handler.NeonSignStitchHandler;
import com.grim3212.assorted.decor.common.block.NeonSignStandingBlock;
import com.grim3212.assorted.decor.common.block.NeonSignWallBlock;
import com.grim3212.assorted.decor.common.block.tileentity.NeonSignTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer.SignModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.BlockState;

public class NeonSignTileEntityRenderer implements BlockEntityRenderer<NeonSignTileEntity> {

	private final SignRenderer.SignModel model;
	private final Font font;

	public NeonSignTileEntityRenderer(BlockEntityRendererProvider.Context context) {
		this.model = new SignModel(context.bakeLayer(new ModelLayerLocation(new ResourceLocation(AssortedDecor.MODID, "sign/neon_sign"), "main")));
		this.font = context.getFont();
	}

	@Override
	public void render(NeonSignTileEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
		BlockState blockstate = tileEntityIn.getBlockState();
		matrixStackIn.pushPose();
		float f = 0.6666667F;
		if (blockstate.getBlock() instanceof NeonSignStandingBlock) {
			matrixStackIn.translate(0.5D, 0.5D, 0.5D);
			float f1 = -((float) (blockstate.getValue(StandingSignBlock.ROTATION) * 360) / 16.0F);
			matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f1));
			this.model.stick.visible = true;
		} else {
			matrixStackIn.translate(0.5D, 0.5D, 0.5D);
			float f4 = -blockstate.getValue(WallSignBlock.FACING).toYRot();
			matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f4));
			matrixStackIn.translate(0.0D, -0.3125D, -0.4375D);
			this.model.stick.visible = false;
		}

		matrixStackIn.pushPose();
		matrixStackIn.scale(f, -f, -f);
		Material rendermaterial = new Material(Sheets.SIGN_SHEET, NeonSignStitchHandler.getSignTexture(tileEntityIn.mode));
		VertexConsumer ivertexbuilder = rendermaterial.buffer(bufferIn, this.model::renderType);
		this.model.root.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
		this.model.stick.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
		matrixStackIn.popPose();
		float f2 = 0.010416667F;
		matrixStackIn.translate(0.0D, (double) 0.33333334F, (double) 0.046666667F);
		matrixStackIn.scale(f2, -f2, f2);
		int j1 = 20;

		for (int k1 = 0; k1 < 4; ++k1) {
			String s = tileEntityIn.getText(k1).getString();
			this.font.draw(matrixStackIn, tileEntityIn.getText(k1), -this.font.width(s) / 2, (float) (k1 * 10 - j1), 16777215);
		}

		// Clear sign render text on both sides
		if (tileEntityIn.mode == 2 && !(blockstate.getBlock() instanceof NeonSignWallBlock)) {
			matrixStackIn.translate(0.0D, 0.0D, -9.0D);
			matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F));

			for (int k1 = 0; k1 < 4; ++k1) {
				String s = tileEntityIn.getText(k1).getString();
				this.font.draw(matrixStackIn, tileEntityIn.getText(k1), -this.font.width(s) / 2, (float) (k1 * 10 - j1), 16777215);
			}
		}

		matrixStackIn.popPose();
	}
}
