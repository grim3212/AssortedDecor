package com.grim3212.assorted.decor.client.blockentity;

import com.grim3212.assorted.decor.common.block.blockentity.CageBlockEntity;
import com.grim3212.assorted.decor.common.handler.DecorConfig;
import com.grim3212.assorted.decor.common.util.CageLogic;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class CageBlockEntityRenderer implements BlockEntityRenderer<CageBlockEntity> {
	public CageBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
	}

	public void render(CageBlockEntity cage, float partialTicks, PoseStack poseStack, MultiBufferSource source, int combinedLightIn, int combinedOverlayIn) {
		poseStack.pushPose();
		poseStack.translate(0.5D, 0.0D, 0.5D);
		CageLogic cageLogic = cage.getCageLogic();
		ItemStack stack = cage.getItem(0);
		if (!stack.isEmpty()) {
			
			poseStack.translate(0.0D, (double) 0.4F, 0.0D);
			poseStack.mulPose(Vector3f.YP.rotationDegrees((float)Mth.lerp((double)partialTicks, cageLogic.getPrevMobRotation(), cageLogic.getMobRotation()) * DecorConfig.CLIENT.cageEntitySpinMod.get().floatValue()));
			poseStack.translate(0.0D, (double) -0.2F, 0.0D);
			

			Entity cageEntity = cageLogic.getEntity();
			if (cageEntity != null) {

				float f = 0.53125F;
				float f1 = Math.max(cageEntity.getBbWidth(), cageEntity.getBbHeight());
				if ((double) f1 > 1.0D) {
					f /= f1;
				}

				poseStack.scale(f, f, f);
				
				EntityRenderDispatcher entityRendererDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
				entityRendererDispatcher.setRenderShadow(false);
				entityRendererDispatcher.render(cageEntity, 0, 0, 0, Minecraft.getInstance().getFrameTime(), 1, poseStack, source, combinedLightIn);
				
			}
		}

		poseStack.popPose();
	}
}
