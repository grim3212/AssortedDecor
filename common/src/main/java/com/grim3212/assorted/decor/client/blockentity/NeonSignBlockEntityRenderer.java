package com.grim3212.assorted.decor.client.blockentity;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.common.blocks.NeonSignStandingBlock;
import com.grim3212.assorted.decor.common.blocks.NeonSignWallBlock;
import com.grim3212.assorted.decor.common.blocks.blockentity.NeonSignBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Draws a neon sign: the board, its post and the four lines of text. The board texture depends on
 * {@link NeonSignBlockEntity#mode}, which a block model cannot see, so the board is a {@link
 * ModelPart} baked straight from a {@link LayerDefinition} (the vanilla sign mesh, matching the
 * 64x32 textures).
 */
public class NeonSignBlockEntityRenderer implements BlockEntityRenderer<NeonSignBlockEntity, NeonSignBlockEntityRenderer.NeonSignRenderState> {

    public static final Identifier NEON_SIGN_TEXTURE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "model/neon_sign");
    public static final Identifier NEON_SIGN_CLEAR_TEXTURE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "model/neon_sign_clear");
    public static final Identifier NEON_SIGN_WHITE_TEXTURE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "model/neon_sign_white");
    public static final Identifier VANILLA_SIGN = Identifier.parse("entity/signs/oak");

    private static final int TEXT_COLOR = -1;
    private static final int LINE_COUNT = 4;

    /**
     * The scale the 1.20.1 renderer drew the board at. Y and Z are negated because a sign mesh is
     * modelled upside down and facing away, exactly as vanilla did it.
     */
    private static final float BOARD_SCALE = 0.6666667F;

    private final Font font;
    private final ModelPart board;
    private final ModelPart stick;

    public NeonSignBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.font();

        ModelPart root = createSignLayer().bakeRoot();
        this.board = root.getChild("sign");
        this.stick = root.getChild("stick");
    }

    /**
     * The vanilla sign mesh: a 24x12x2 board and a 2x14x2 post on a 64x32 sheet.
     */
    private static LayerDefinition createSignLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("sign", CubeListBuilder.create().texOffs(0, 0).addBox(-12.0F, -14.0F, -1.0F, 24.0F, 12.0F, 2.0F), PartPose.ZERO);
        root.addOrReplaceChild("stick", CubeListBuilder.create().texOffs(0, 14).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 14.0F, 2.0F), PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 32);
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
        state.boardTexture = signTextureFile(blockEntity.mode);

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

        submitBoard(state, poseStack, submitNodeCollector);

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

    /**
     * The board is drawn straight off its texture file rather than through an atlas sprite: the three
     * mode textures are 64x32 entity sheets, and the sign atlas they used to be looked up on
     * ({@code Sheets.SIGN_SHEET}) no longer exists.
     */
    private void submitBoard(NeonSignRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        RenderType renderType = RenderTypes.entityCutout(state.boardTexture);

        poseStack.pushPose();
        poseStack.scale(BOARD_SCALE, -BOARD_SCALE, -BOARD_SCALE);

        submitNodeCollector.submitModelPart(this.board, poseStack, renderType, state.lightCoords, OverlayTexture.NO_OVERLAY, null);
        // A wall sign has nothing to stand on, so its post is left off, as it was in 1.20.1 through
        // SignModel#stick's visible flag.
        if (state.standing) {
            submitNodeCollector.submitModelPart(this.stick, poseStack, renderType, state.lightCoords, OverlayTexture.NO_OVERLAY, null);
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

    /**
     * The same choice as {@link #getSignTexture(int)}, as the path of a texture file - what a
     * {@link RenderType} wants, as opposed to the sprite name an atlas material wanted.
     */
    private static Identifier signTextureFile(int mode) {
        Identifier sprite = getSignTexture(mode);
        return sprite.withPath(path -> "textures/" + path + ".png");
    }

    public static class NeonSignRenderState extends BlockEntityRenderState {
        public boolean standing;
        public boolean wall;
        public boolean doubleSided;
        public float yRot;
        public Identifier boardTexture = NEON_SIGN_TEXTURE;
        public final FormattedCharSequence[] lines = new FormattedCharSequence[LINE_COUNT];
        public final float[] lineOffsets = new float[LINE_COUNT];
    }
}
