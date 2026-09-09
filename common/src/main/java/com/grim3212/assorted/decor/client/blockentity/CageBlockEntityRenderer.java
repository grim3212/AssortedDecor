package com.grim3212.assorted.decor.client.blockentity;

import com.grim3212.assorted.decor.client.DecorClient;
import com.grim3212.assorted.decor.common.blocks.blockentity.CageBlockEntity;
import com.grim3212.assorted.decor.common.helpers.CageLogic;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * The caged entity is extracted into its own {@link EntityRenderState} while the level is still
 * reachable and submitted from that, mirroring how vanilla's {@code SpawnerRenderer} draws the mob
 * spinning inside a spawner.
 */
public class CageBlockEntityRenderer implements BlockEntityRenderer<CageBlockEntity, CageBlockEntityRenderer.CageRenderState> {

    private final EntityRenderDispatcher entityRenderer;

    public CageBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.entityRenderer = ctx.entityRenderer();
    }

    @Override
    public CageRenderState createRenderState() {
        return new CageRenderState();
    }

    @Override
    public void extractRenderState(CageBlockEntity blockEntity, CageRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        state.displayEntity = null;
        if (blockEntity.getItemStackStorageHandler().getStackInSlot(0).isEmpty()) {
            return;
        }

        CageLogic cageLogic = blockEntity.getCageLogic();
        Entity cageEntity = cageLogic.getEntity();
        if (cageEntity == null) {
            return;
        }

        state.displayEntity = this.entityRenderer.extractEntity(cageEntity, partialTicks);
        state.displayEntity.lightCoords = state.lightCoords;
        // The cage never drew a shadow; there is no setRenderShadow switch on the dispatcher any
        // more, the shadow is a set of pieces baked into the extracted state.
        state.displayEntity.shadowRadius = 0.0F;
        state.displayEntity.shadowPieces.clear();

        state.spin = (float) Mth.lerp((double) partialTicks, cageLogic.getPrevMobRotation(), cageLogic.getMobRotation()) * DecorClient.CLIENT_CONFIG.cageSpinMod.get().floatValue();

        state.scale = 0.53125F;
        float maxLength = Math.max(cageEntity.getBbWidth(), cageEntity.getBbHeight());
        if (maxLength > 1.0F) {
            state.scale /= maxLength;
        }
    }

    @Override
    public void submit(CageRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.displayEntity == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.translate(0.0D, (double) 0.4F, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.spin));
        poseStack.translate(0.0D, (double) -0.2F, 0.0D);
        poseStack.scale(state.scale, state.scale, state.scale);

        this.entityRenderer.submit(state.displayEntity, camera, 0.0D, 0.0D, 0.0D, poseStack, submitNodeCollector);

        poseStack.popPose();
    }

    public static class CageRenderState extends BlockEntityRenderState {
        public @Nullable EntityRenderState displayEntity;
        public float spin;
        public float scale;
    }
}
