package com.grim3212.assorted.decor.client.tileentity;

import com.grim3212.assorted.decor.client.handler.NeonSignStitchHandler;
import com.grim3212.assorted.decor.common.block.NeonSignStandingBlock;
import com.grim3212.assorted.decor.common.block.NeonSignWallBlock;
import com.grim3212.assorted.decor.common.block.tileentity.NeonSignTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.block.StandingSignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.tileentity.SignTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.vector.Vector3f;

public class NeonSignTileEntityRenderer extends TileEntityRenderer<NeonSignTileEntity> {

	private final SignTileEntityRenderer.SignModel model = new SignTileEntityRenderer.SignModel();

	public NeonSignTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void render(NeonSignTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
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
		RenderMaterial rendermaterial = new RenderMaterial(Atlases.SIGN_SHEET, NeonSignStitchHandler.getSignTexture(tileEntityIn.mode));
		IVertexBuilder ivertexbuilder = rendermaterial.buffer(bufferIn, this.model::renderType);
		this.model.sign.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
		this.model.stick.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
		matrixStackIn.popPose();
		FontRenderer fontrenderer = this.renderer.getFont();
		float f2 = 0.010416667F;
		matrixStackIn.translate(0.0D, (double) 0.33333334F, (double) 0.046666667F);
		matrixStackIn.scale(f2, -f2, f2);
		int j1 = 20;

		for (int k1 = 0; k1 < 4; ++k1) {
			String s = tileEntityIn.getText(k1).getString();
			fontrenderer.draw(matrixStackIn, tileEntityIn.getText(k1), -fontrenderer.width(s) / 2, (float) (k1 * 10 - j1), 16777215);
		}

		// Clear sign render text on both sides
		if (tileEntityIn.mode == 2 && !(blockstate.getBlock() instanceof NeonSignWallBlock)) {
			matrixStackIn.translate(0.0D, 0.0D, -9.0D);
			matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F));

			for (int k1 = 0; k1 < 4; ++k1) {
				String s = tileEntityIn.getText(k1).getString();
				fontrenderer.draw(matrixStackIn, tileEntityIn.getText(k1), -fontrenderer.width(s) / 2, (float) (k1 * 10 - j1), 16777215);
			}
		}

		matrixStackIn.popPose();
	}
}
