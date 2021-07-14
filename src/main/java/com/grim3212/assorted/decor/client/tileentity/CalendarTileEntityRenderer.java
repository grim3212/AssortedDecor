package com.grim3212.assorted.decor.client.tileentity;

import com.grim3212.assorted.decor.common.block.CalendarBlock;
import com.grim3212.assorted.decor.common.block.tileentity.CalendarTileEntity;
import com.grim3212.assorted.decor.common.util.DateHandler;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

public class CalendarTileEntityRenderer extends TileEntityRenderer<CalendarTileEntity> {

	public CalendarTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void render(CalendarTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		matrixStackIn.pushPose();

		float f1 = 0.6666667F;
		float f2 = tileEntityIn.getBlockState().getValue(CalendarBlock.FACING).toYRot();
		
		matrixStackIn.translate(0.5F, 0.5F, 0.5F);
		matrixStackIn.mulPose(new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), -f2, true));
		matrixStackIn.translate(0.0F, -0.3125F, -0.4375F);

		float f3 = 0.015F * f1;
		matrixStackIn.translate(0.0F, 0.19F * f1, 0.01F * f1);
		matrixStackIn.scale(f3, -f3, f3);

		FontRenderer fontrenderer = this.renderer.getFont();
		String s = DateHandler.calculateDate(Minecraft.getInstance().level.getDayTime(), 1);
		String as[] = s.split(",");
		for (int k = 0; k < as.length; k++) {
			String s1 = as[k];
			fontrenderer.draw(matrixStackIn, s1, -fontrenderer.width(s1) / 2, k * 10 - as.length * 5, 0);
		}

		
		matrixStackIn.popPose();
	}
}
