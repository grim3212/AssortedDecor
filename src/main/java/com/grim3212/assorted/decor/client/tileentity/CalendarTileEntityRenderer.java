package com.grim3212.assorted.decor.client.tileentity;

import com.grim3212.assorted.decor.common.block.CalendarBlock;
import com.grim3212.assorted.decor.common.block.tileentity.CalendarTileEntity;
import com.grim3212.assorted.decor.common.util.DateHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class CalendarTileEntityRenderer implements BlockEntityRenderer<CalendarTileEntity> {

	private final Font font;

	public CalendarTileEntityRenderer(BlockEntityRendererProvider.Context context) {
		this.font = context.getFont();
	}

	@Override
	public void render(CalendarTileEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
		matrixStackIn.pushPose();

		float f1 = 0.6666667F;
		float f2 = tileEntityIn.getBlockState().getValue(CalendarBlock.FACING).toYRot();

		matrixStackIn.translate(0.5F, 0.5F, 0.5F);
		matrixStackIn.mulPose(new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), -f2, true));
		matrixStackIn.translate(0.0F, -0.3125F, -0.4375F);

		float f3 = 0.015F * f1;
		matrixStackIn.translate(0.0F, 0.19F * f1, 0.01F * f1);
		matrixStackIn.scale(f3, -f3, f3);

		String s = DateHandler.calculateDate(Minecraft.getInstance().level.getDayTime(), 1);
		String as[] = s.split(",");
		for (int k = 0; k < as.length; k++) {
			String s1 = as[k];
			this.font.draw(matrixStackIn, s1, -this.font.width(s1) / 2, k * 10 - as.length * 5, 0);
		}

		matrixStackIn.popPose();
	}
}
