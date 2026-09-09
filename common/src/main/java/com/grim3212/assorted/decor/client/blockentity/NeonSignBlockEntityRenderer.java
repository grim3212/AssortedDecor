package com.grim3212.assorted.decor.client.blockentity;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.common.blocks.NeonSignStandingBlock;
import com.grim3212.assorted.decor.common.blocks.NeonSignWallBlock;
import com.grim3212.assorted.decor.common.blocks.blockentity.NeonSignBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * TODO(26.2): the sign board itself is no longer drawn here, only its text.
 *  What this used to do: build a {@code SignRenderer.SignModel} out of the entity model set, pick one
 *  of the three {@code textures/model/neon_sign*.png} sprites off the {@code Sheets.SIGN_SHEET} atlas
 *  according to {@link NeonSignBlockEntity#mode} and render the board plus the stick before drawing
 *  the text on top.
 *  Why it cannot be expressed: 26.2 deleted {@code SignRenderer} outright. Its replacement,
 *  {@code AbstractSignRenderer}, only submits text - vanilla signs render their board from an ordinary
 *  <em>block model</em> now, and {@code Sheets.SIGN_SHEET}, {@code Material#buffer} and
 *  {@code SignRenderer.SignModel} are all gone with it. Restoring the board therefore needs a
 *  {@code models/block/neon_sign.json} with real geometry (the mod currently ships one carrying only a
 *  particle texture), which is a datagen change rather than a renderer one. Note also that {@code mode}
 *  lives in the block entity's NBT rather than in the block state, and a block model is chosen per
 *  block state, so the three board textures cannot be selected from the model json either - the mode
 *  would have to become a block state property, or the board be submitted here as custom geometry
 *  against a mod-owned atlas.
 */
public class NeonSignBlockEntityRenderer implements BlockEntityRenderer<NeonSignBlockEntity, NeonSignBlockEntityRenderer.NeonSignRenderState> {

    public static final Identifier NEON_SIGN_TEXTURE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "model/neon_sign");
    public static final Identifier NEON_SIGN_CLEAR_TEXTURE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "model/neon_sign_clear");
    public static final Identifier NEON_SIGN_WHITE_TEXTURE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "model/neon_sign_white");
    public static final Identifier VANILLA_SIGN = Identifier.parse("entity/signs/oak");

    private static final int TEXT_COLOR = -1;
    private static final int LINE_COUNT = 4;

    private final Font font;

    public NeonSignBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.font();
    }

    @Override
    public NeonSignRenderState createRenderState() {
        return new NeonSignRenderState();
    }

    @Override
    public void extractRenderState(NeonSignBlockEntity blockEntity, NeonSignRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        BlockState blockstate = blockEntity.getBlockState();
        state.standing = blockstate.getBlock() instanceof NeonSignStandingBlock;
        state.wall = blockstate.getBlock() instanceof NeonSignWallBlock;
        if (state.standing) {
            state.yRot = -((float) (blockstate.getValue(StandingSignBlock.ROTATION) * 360) / 16.0F);
        } else {
            state.yRot = -blockstate.getValue(WallSignBlock.FACING).toYRot();
        }

        state.doubleSided = blockEntity.mode == 2 && !state.wall;

        for (int line = 0; line < LINE_COUNT; line++) {
            state.lines[line] = blockEntity.getText(line).getVisualOrderText();
            state.lineOffsets[line] = (float) -this.font.width(blockEntity.getText(line)) / 2;
        }
    }

    @Override
    public void submit(NeonSignRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot));
        if (!state.standing) {
            poseStack.translate(0.0D, -0.3125D, -0.4375D);
        }

        float f2 = 0.010416667F;
        poseStack.translate(0.0D, (double) 0.33333334F, (double) 0.046666667F);
        poseStack.scale(f2, -f2, f2);

        submitText(state, poseStack, submitNodeCollector);

        // Clear sign renders text on both sides
        if (state.doubleSided) {
            poseStack.translate(0.0D, 0.0D, -9.0D);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            submitText(state, poseStack, submitNodeCollector);
        }

        poseStack.popPose();
    }

    private static void submitText(NeonSignRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        int j1 = 20;

        for (int line = 0; line < LINE_COUNT; line++) {
            if (state.lines[line] == null) {
                continue;
            }

            submitNodeCollector.submitText(poseStack, state.lineOffsets[line], (float) (line * 10 - j1), state.lines[line], false, Font.DisplayMode.POLYGON_OFFSET, state.lightCoords, TEXT_COLOR, 0, 0);
        }
    }

    public static Identifier getSignTexture(int mode) {
        switch (mode) {
            case 0:
                return NEON_SIGN_TEXTURE;
            case 1:
                return NEON_SIGN_WHITE_TEXTURE;
            case 2:
                return NEON_SIGN_CLEAR_TEXTURE;
            default:
                return VANILLA_SIGN;
        }
    }

    public static class NeonSignRenderState extends BlockEntityRenderState {
        public boolean standing;
        public boolean wall;
        public boolean doubleSided;
        public float yRot;
        public final FormattedCharSequence[] lines = new FormattedCharSequence[LINE_COUNT];
        public final float[] lineOffsets = new float[LINE_COUNT];
    }
}
