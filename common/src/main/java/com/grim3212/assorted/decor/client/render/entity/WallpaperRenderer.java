package com.grim3212.assorted.decor.client.render.entity;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.client.DecorClient;
import com.grim3212.assorted.decor.common.entity.WallpaperEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;

/**
 * Like {@link FrameRenderer}, the wallpaper's quads are loose geometry with no typed submit call, so
 * they are handed to {@link SubmitNodeCollector#submitCustomGeometry}. The four
 * {@code if (direction == ...)} arms the 1.20.1 version carried emitted byte identical vertices - the
 * facing only ever mattered through the yaw rotation applied before the geometry - so they are
 * collapsed here. The one arm that genuinely differed, the top edge on a NORTH facing wallpaper, is
 * kept.
 */
public class WallpaperRenderer extends EntityRenderer<WallpaperEntity, WallpaperRenderer.WallpaperRenderState> {

    private static final Identifier wallpaperTexture = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/entity/wallpapers.png");

    private static final float MAX_UV = 0.0625F;
    private static final float SIDE_UV = 0.00195313F;

    public WallpaperRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public WallpaperRenderState createRenderState() {
        return new WallpaperRenderState();
    }

    @Override
    public void extractRenderState(WallpaperEntity entity, WallpaperRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);

        state.direction = entity.getDirection();
        state.blockUp = entity.isBlockUp;
        state.blockDown = entity.isBlockDown;
        state.blockLeft = entity.isBlockLeft;
        state.blockRight = entity.isBlockRight;

        state.minU = entity.getWallpaperID() / 16 / 16.0F;
        state.minV = entity.getWallpaperID() % 16 / 16.0F;

        int[] color = entity.getWallpaperColor();
        state.color = ARGB.color(color[0], color[1], color[2]);
        // The wallpaper is lit from the block it is stuck to rather than from its own light probe,
        // which is what LevelRenderer#getLightColor used to answer.
        state.wallpaperLightCoords = LightCoordsUtil.getLightCoords(entity.level(), entity.getPos());
    }

    @Override
    public void submit(WallpaperRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        super.submit(state, poseStack, submitNodeCollector, camera);

        if (!state.direction.getAxis().isHorizontal()) {
            return;
        }

        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(180 - state.direction.get2DDataValue() * 90));
        poseStack.scale(0.03125F, 0.03125F, 0.03125F);
        submitWallpaper(state, poseStack, submitNodeCollector);

        poseStack.popPose();
    }

    private void submitWallpaper(WallpaperRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.entitySolid(wallpaperTexture), (pose, buffer) -> {
            float minX = -16.0F;
            float minY = -16.0F;
            float minZ = 0.0F;

            float maxX = 16.0F;
            float maxY = 16.0F;
            float maxZ = DecorClient.CLIENT_CONFIG.wallpaperWidth.get().floatValue();

            float minU = state.minU;
            float minV = state.minV;

            int color = state.color;
            int light = state.wallpaperLightCoords;

            vertex(pose, buffer, minX, minY, minZ, color, minU + MAX_UV, minV + MAX_UV, light, 0.0F, 0.0F, 1.0F);
            vertex(pose, buffer, minX, maxY, minZ, color, minU + MAX_UV, minV, light, 0.0F, 0.0F, 1.0F);
            vertex(pose, buffer, maxX, maxY, minZ, color, minU, minV, light, 0.0F, 0.0F, 1.0F);
            vertex(pose, buffer, maxX, minY, minZ, color, minU, minV + MAX_UV, light, 0.0F, 0.0F, 1.0F);

            if (!state.blockLeft) {
                vertex(pose, buffer, minX, maxY, maxZ, color, minU + MAX_UV, minV, light, -1.0F, 0.0F, 0.0F);
                vertex(pose, buffer, minX, maxY, minZ, color, minU + MAX_UV, minV, light, -1.0F, 0.0F, 0.0F);
                vertex(pose, buffer, minX, minY, minZ, color, minU + MAX_UV - SIDE_UV, minV + MAX_UV, light, -1.0F, 0.0F, 0.0F);
                vertex(pose, buffer, minX, minY, maxZ, color, minU + MAX_UV - SIDE_UV, minV + MAX_UV, light, -1.0F, 0.0F, 0.0F);
            }

            if (!state.blockUp) {
                if (state.direction == Direction.NORTH) {
                    vertex(pose, buffer, minX, maxY, minZ, color, minU + MAX_UV, minV, light, 0.0F, 1.0F, 0.0F);
                    vertex(pose, buffer, minX, maxY, maxZ, color, minU + MAX_UV, minV + SIDE_UV, light, 0.0F, 1.0F, 0.0F);
                    vertex(pose, buffer, maxX, maxY, maxZ, color, minU, minV + SIDE_UV, light, 0.0F, 1.0F, 0.0F);
                    vertex(pose, buffer, maxX, maxY, minZ, color, minU, minV, light, 0.0F, 1.0F, 0.0F);
                } else {
                    vertex(pose, buffer, maxX, maxY, maxZ, color, minU, minV + SIDE_UV, light, 0.0F, 1.0F, 0.0F);
                    vertex(pose, buffer, maxX, maxY, minZ, color, minU, minV, light, 0.0F, 1.0F, 0.0F);
                    vertex(pose, buffer, minX, maxY, minZ, color, minU + MAX_UV, minV, light, 0.0F, 1.0F, 0.0F);
                    vertex(pose, buffer, minX, maxY, maxZ, color, minU + MAX_UV, minV + SIDE_UV, light, 0.0F, 1.0F, 0.0F);
                }
            }

            if (!state.blockRight) {
                vertex(pose, buffer, maxX, minY, maxZ, color, minU + SIDE_UV, minV + MAX_UV, light, 1.0F, 0.0F, 0.0F);
                vertex(pose, buffer, maxX, minY, minZ, color, minU, minV + MAX_UV, light, 1.0F, 0.0F, 0.0F);
                vertex(pose, buffer, maxX, maxY, minZ, color, minU, minV, light, 1.0F, 0.0F, 0.0F);
                vertex(pose, buffer, maxX, maxY, maxZ, color, minU + SIDE_UV, minV, light, 1.0F, 0.0F, 0.0F);
            }

            if (!state.blockDown) {
                vertex(pose, buffer, minX, minY, maxZ, color, minU + MAX_UV, minV + MAX_UV - SIDE_UV, light, 0.0F, -1.0F, 0.0F);
                vertex(pose, buffer, minX, minY, minZ, color, minU + MAX_UV, minV + MAX_UV, light, 0.0F, -1.0F, 0.0F);
                vertex(pose, buffer, maxX, minY, minZ, color, minU, minV + MAX_UV, light, 0.0F, -1.0F, 0.0F);
                vertex(pose, buffer, maxX, minY, maxZ, color, minU, minV + MAX_UV - SIDE_UV, light, 0.0F, -1.0F, 0.0F);
            }
        });
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer buffer, float x, float y, float z, int color, float u, float v, int lightCoords, float nx, float ny, float nz) {
        buffer.addVertex(pose, x, y, z).setColor(color).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightCoords).setNormal(pose, nx, ny, nz);
    }

    public static class WallpaperRenderState extends EntityRenderState {
        public Direction direction = Direction.NORTH;
        public boolean blockUp;
        public boolean blockDown;
        public boolean blockLeft;
        public boolean blockRight;
        public float minU;
        public float minV;
        public int color = -1;
        public int wallpaperLightCoords;
    }
}
