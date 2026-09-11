package com.grim3212.assorted.decor.client.blockentity;

import com.grim3212.assorted.decor.api.util.DateHandler;
import com.grim3212.assorted.decor.common.blocks.CalendarBlock;
import com.grim3212.assorted.decor.common.blocks.blockentity.CalendarBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/** Draws the calendar's date as text, submitted through {@link SubmitNodeCollector#submitText}. */
public class CalendarBlockEntityRenderer implements BlockEntityRenderer<CalendarBlockEntity, CalendarBlockEntityRenderer.CalendarRenderState> {

    private static final int TEXT_COLOR = ARGB.opaque(0);

    private final Font font;

    public CalendarBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.font();
    }

    @Override
    public CalendarRenderState createRenderState() {
        return new CalendarRenderState();
    }

    @Override
    public void extractRenderState(CalendarBlockEntity blockEntity, CalendarRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        state.yRot = blockEntity.getBlockState().getValue(CalendarBlock.FACING).toYRot();

        // Level#getDayTime is gone; world time is driven by data defined WorldClocks now and the
        // overworld clock is the one the old day time came from.
        String[] lines = blockEntity.getLevel() != null ? DateHandler.calculateDate(blockEntity.getLevel().getOverworldClockTime(), 1).split(",") : new String[0];
        state.lines = new FormattedCharSequence[lines.length];
        state.lineOffsets = new float[lines.length];
        for (int i = 0; i < lines.length; i++) {
            state.lines[i] = FormattedCharSequence.forward(lines[i], Style.EMPTY);
            state.lineOffsets[i] = (float) -this.font.width(lines[i]) / 2;
        }
    }

    @Override
    public void submit(CalendarRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();

        float f1 = 0.6666667F;

        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.yRot));
        poseStack.translate(0.0F, -0.3125F, -0.4375F);

        float f3 = 0.015F * f1;
        poseStack.translate(0.0F, 0.19F * f1, 0.01F * f1);
        poseStack.scale(f3, -f3, f3);

        for (int k = 0; k < state.lines.length; k++) {
            submitNodeCollector.submitText(poseStack, state.lineOffsets[k], k * 10 - state.lines.length * 5, state.lines[k], false, Font.DisplayMode.POLYGON_OFFSET, state.lightCoords, TEXT_COLOR, 0, 0);
        }

        poseStack.popPose();
    }

    public static class CalendarRenderState extends BlockEntityRenderState {
        public float yRot;
        public FormattedCharSequence[] lines = new FormattedCharSequence[0];
        public float[] lineOffsets = new float[0];
    }
}
