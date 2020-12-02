package com.grim3212.assorted.decor.client.tileentity;

import java.util.List;

import com.grim3212.assorted.decor.client.handler.NeonSignStitchHandler;
import com.grim3212.assorted.decor.common.block.NeonSignStandingBlock;
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
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.tileentity.SignTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.vector.Vector3f;

public class NeonSignTileEntityRenderer extends TileEntityRenderer<NeonSignTileEntity> {

	private final SignTileEntityRenderer.SignModel model = new SignTileEntityRenderer.SignModel();

	public NeonSignTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void render(NeonSignTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		BlockState blockstate = tileEntityIn.getBlockState();
		matrixStackIn.push();
		float f = 0.6666667F;
		if (blockstate.getBlock() instanceof NeonSignStandingBlock) {
			matrixStackIn.translate(0.5D, 0.5D, 0.5D);
			float f1 = -((float) (blockstate.get(StandingSignBlock.ROTATION) * 360) / 16.0F);
			matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f1));
			this.model.signStick.showModel = true;
		} else {
			matrixStackIn.translate(0.5D, 0.5D, 0.5D);
			float f4 = -blockstate.get(WallSignBlock.FACING).getHorizontalAngle();
			matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f4));
			matrixStackIn.translate(0.0D, -0.3125D, -0.4375D);
			this.model.signStick.showModel = false;
		}

		matrixStackIn.push();
		matrixStackIn.scale(f, -f, -f);
		RenderMaterial rendermaterial = new RenderMaterial(Atlases.SIGN_ATLAS, NeonSignStitchHandler.getSignTexture(tileEntityIn.mode));
		IVertexBuilder ivertexbuilder = rendermaterial.getBuffer(bufferIn, this.model::getRenderType);
		this.model.signBoard.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
		this.model.signStick.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
		matrixStackIn.pop();
		FontRenderer fontrenderer = this.renderDispatcher.getFontRenderer();
		float f2 = 0.010416667F;
		matrixStackIn.translate(0.0D, (double) 0.33333334F, (double) 0.046666667F);
		matrixStackIn.scale(f2, -f2, f2);
		int i = tileEntityIn.getTextColor().getTextColor();
		double d0 = 0.4D;
		int j = (int) ((double) NativeImage.getRed(i) * d0);
		int k = (int) ((double) NativeImage.getGreen(i) * d0);
		int l = (int) ((double) NativeImage.getBlue(i) * d0);
		int i1 = NativeImage.getCombined(0, l, k, j);
		int j1 = 20;

		for (int k1 = 0; k1 < 4; ++k1) {
			IReorderingProcessor ireorderingprocessor = tileEntityIn.func_242686_a(k1, (p_243502_1_) -> {
				List<IReorderingProcessor> list = fontrenderer.trimStringToWidth(p_243502_1_, 90);
				return list.isEmpty() ? IReorderingProcessor.field_242232_a : list.get(0);
			});
			if (ireorderingprocessor != null) {
				float f3 = (float) (-fontrenderer.func_243245_a(ireorderingprocessor) / 2);
				fontrenderer.func_238416_a_(ireorderingprocessor, f3, (float) (k1 * 10 - j1), i1, false, matrixStackIn.getLast().getMatrix(), bufferIn, false, 0, combinedLightIn);
			}
		}

		matrixStackIn.pop();
	}
}
