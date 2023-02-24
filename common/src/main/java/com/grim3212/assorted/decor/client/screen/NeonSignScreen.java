package com.grim3212.assorted.decor.client.screen;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.client.blockentity.NeonSignBlockEntityRenderer;
import com.grim3212.assorted.decor.common.blocks.NeonSignStandingBlock;
import com.grim3212.assorted.decor.common.blocks.blockentity.NeonSignBlockEntity;
import com.grim3212.assorted.decor.common.network.NeonChangeModePacket;
import com.grim3212.assorted.decor.common.network.NeonUpdatePacket;
import com.grim3212.assorted.lib.platform.Services;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.joml.Matrix4f;

import java.util.stream.IntStream;

public class NeonSignScreen extends Screen {
    private SignRenderer.SignModel signModel;
    /**
     * Reference to the sign object.
     */
    private final NeonSignBlockEntity tileSign;
    /**
     * Counts the number of screen updates.
     */
    private int updateCounter;
    /**
     * The index of the line that is being edited.
     */
    private int editLine;

    private final int bgWidth = 176;
    private final int bgHeight = 208;
    private TextFieldHelper textInputUtil;
    private final String[] lines;

    public static final ResourceLocation NEON_SIGN_GUI_TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/gui/screen/neon_sign.png");

    public NeonSignScreen(NeonSignBlockEntity teSign) {
        super(Component.translatable("sign.edit"));
        this.tileSign = teSign;
        this.lines = IntStream.range(0, 4).mapToObj(teSign::getText).map(Component::getString).toArray((p_243354_0_) -> {
            return new String[p_243354_0_];
        });
    }

    @Override
    public void init() {
        int x = (width - bgWidth) / 2;
        int y = (height - bgHeight) / 2;

        this.textInputUtil = new TextFieldHelper(() -> {
            return this.lines[this.editLine];
        }, (s) -> {
            this.lines[this.editLine] = s;
            this.tileSign.setText(this.editLine, Component.literal(s));
        }, TextFieldHelper.createClipboardGetter(this.minecraft), TextFieldHelper.createClipboardSetter(this.minecraft), (s) -> {
            return this.minecraft.font.width(s) <= 90;
        });

        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), btn -> {
            this.close();
        }).bounds(x + (bgWidth - 154) / 2, y + 179, 154, 20).build());

        for (int l = 0; l < 11; l++) {
            final int id = l + 1;
            this.addRenderableWidget(new NeonButton(x + 11 + 14 * l, y + 136, Component.literal(""), 176, l * 14, btn -> {
                addSignText(id);
            }));
        }

        for (int i1 = 11; i1 < 16; i1++) {
            final int id = i1 + 1;
            this.addRenderableWidget(new NeonButton(x + 11 + 14 * (i1 - 11), y + 150, Component.literal(""), 176, i1 * 14, btn -> {
                addSignText(id);
            }));
        }

        this.addRenderableWidget(new NeonButton(x + 11 + 70, y + 150, Component.translatable("screen.assorteddecor.neon_sign.bold"), 204, 0, btn -> {
            addSignText(17);
        }));
        this.addRenderableWidget(new NeonButton(x + 11 + 84, y + 150, Component.translatable("screen.assorteddecor.neon_sign.italic"), 204, 14, btn -> {
            addSignText(18);
        }));
        this.addRenderableWidget(new NeonButton(x + 11 + 98, y + 150, Component.translatable("screen.assorteddecor.neon_sign.underline"), 204, 28, btn -> {
            addSignText(19);
        }));
        this.addRenderableWidget(new NeonButton(x + 11 + 112, y + 150, Component.translatable("screen.assorteddecor.neon_sign.strikethrough"), 204, 42, btn -> {
            addSignText(20);
        }));
        this.addRenderableWidget(new NeonButton(x + 11 + 126, y + 150, Component.translatable("screen.assorteddecor.neon_sign.random"), 204, 56, btn -> {
            addSignText(21);
        }));
        this.addRenderableWidget(new NeonButton(x + 11 + 140, y + 150, Component.translatable("screen.assorteddecor.neon_sign.reset"), 204, 70, btn -> {
            addSignText(22);
        }));
        this.addRenderableWidget(new NeonButton(x + 11, y + 164, Component.literal(""), 0, 208, 51, true, btn -> {
            NeonSignScreen.this.tileSign.mode = 0;
            Services.NETWORK.sendToServer(new NeonChangeModePacket(0, NeonSignScreen.this.tileSign.getBlockPos()));
        }));
        this.addRenderableWidget(new NeonButton(x + 11 + 51, y + 164, Component.literal(""), 51, 208, 52, true, btn -> {
            NeonSignScreen.this.tileSign.mode = 1;
            Services.NETWORK.sendToServer(new NeonChangeModePacket(1, NeonSignScreen.this.tileSign.getBlockPos()));
        }));
        this.addRenderableWidget(new NeonButton(x + 11 + 103, y + 164, Component.literal(""), 103, 208, 51, true, btn -> {
            NeonSignScreen.this.tileSign.mode = 2;
            Services.NETWORK.sendToServer(new NeonChangeModePacket(2, NeonSignScreen.this.tileSign.getBlockPos()));
        }));

        this.signModel = SignRenderer.createSignModel(this.minecraft.getEntityModels(), WoodType.OAK);
    }

    private void addSignText(int id) {
        this.textInputUtil.insertText(getFormatting(id).toString());
        this.textInputUtil.setCursorToEnd();
    }

    private void close() {
        this.tileSign.setChanged();
        this.minecraft.setScreen((Screen) null);
    }

    @Override
    public void onClose() {
        this.close();
    }

    @Override
    public void removed() {
        // Update lines on server side
        Services.NETWORK.sendToServer(new NeonUpdatePacket(this.tileSign.getBlockPos(), this.tileSign.signText));
    }

    @Override
    public void tick() {
        ++this.updateCounter;
        if (!this.tileSign.getType().isValid(this.tileSign.getBlockState())) {
            this.close();
        }
    }

    private ChatFormatting getFormatting(int buttonId) {
        switch (buttonId) {
            case 11:
                return ChatFormatting.GREEN;
            case 12:
                return ChatFormatting.AQUA;
            case 13:
                return ChatFormatting.RED;
            case 14:
                return ChatFormatting.LIGHT_PURPLE;
            case 15:
                return ChatFormatting.YELLOW;
            case 16:
                return ChatFormatting.WHITE;
            case 17:
                return ChatFormatting.BOLD;
            case 18:
                return ChatFormatting.ITALIC;
            case 19:
                return ChatFormatting.UNDERLINE;
            case 20:
                return ChatFormatting.STRIKETHROUGH;
            case 21:
                return ChatFormatting.OBFUSCATED;
            case 22:
                return ChatFormatting.RESET;
        }
        return ChatFormatting.getById(buttonId - 1);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        this.textInputUtil.charTyped(codePoint);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 265) {
            this.editLine = this.editLine - 1 & 3;
            this.textInputUtil.setCursorToEnd();
            return true;
        } else if (keyCode != 264 && keyCode != 257 && keyCode != 335) {
            return this.textInputUtil.keyPressed(keyCode) ? true : super.keyPressed(keyCode, scanCode, modifiers);
        } else {
            this.editLine = this.editLine + 1 & 3;
            this.textInputUtil.setCursorToEnd();
            return true;
        }
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Lighting.setupForFlatItems();
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 40, 16777215);
        matrixStack.pushPose();
        matrixStack.translate((double) (this.width / 2), 0.0D, 50.0D);
        float f = 93.75F;
        matrixStack.scale(f, -f, f);
        matrixStack.translate(0.0D, -1.3125D, 0.0D);
        BlockState blockstate = this.tileSign.getBlockState();
        boolean flag = blockstate.getBlock() instanceof NeonSignStandingBlock;
        if (!flag) {
            matrixStack.translate(0.0D, -0.3125D, 0.0D);
        }

        boolean flag1 = this.updateCounter / 6 % 2 == 0;
        float f1 = 0.6666667F;
        matrixStack.pushPose();
        matrixStack.scale(f1, -f1, -f1);
        MultiBufferSource.BufferSource irendertypebuffer$impl = this.minecraft.renderBuffers().bufferSource();
        Material rendermaterial = new Material(Sheets.SIGN_SHEET, NeonSignBlockEntityRenderer.getSignTexture(tileSign.mode));
        VertexConsumer ivertexbuilder = rendermaterial.buffer(irendertypebuffer$impl, this.signModel::renderType);
        this.signModel.root.render(matrixStack, ivertexbuilder, 15728880, OverlayTexture.NO_OVERLAY);
        if (flag) {
            this.signModel.stick.render(matrixStack, ivertexbuilder, 15728880, OverlayTexture.NO_OVERLAY);
        }

        matrixStack.popPose();
        float f2 = 0.010416667F;
        matrixStack.translate(0.0D, (double) 0.33333334F, (double) 0.046666667F);
        matrixStack.scale(f2, -f2, f2);
        int j = this.textInputUtil.getCursorPos();
        int k = this.textInputUtil.getSelectionPos();
        int l = this.editLine * 10 - this.lines.length * 5;
        Matrix4f matrix4f = matrixStack.last().pose();

        for (int i1 = 0; i1 < this.lines.length; ++i1) {
            String s = this.lines[i1];
            if (s != null) {
                if (this.font.isBidirectional()) {
                    s = this.font.bidirectionalShaping(s);
                }

                float f3 = (float) (-this.minecraft.font.width(s) / 2);
                this.minecraft.font.draw(matrixStack, this.tileSign.getText(i1), f3, (float) (i1 * 10 - this.lines.length * 5), 16777215);
                if (i1 == this.editLine && j >= 0 && flag1) {
                    int j1 = this.minecraft.font.width(s.substring(0, Math.max(Math.min(j, s.length()), 0)));
                    int k1 = j1 - this.minecraft.font.width(s) / 2;
                    if (j >= s.length()) {
                        this.minecraft.font.draw(matrixStack, "_", (float) k1, (float) l, 16777215);
                    }
                }
            }
        }

        irendertypebuffer$impl.endBatch();

        for (int i3 = 0; i3 < this.lines.length; ++i3) {
            String s1 = this.lines[i3];
            if (s1 != null && i3 == this.editLine && j >= 0) {
                int j3 = this.minecraft.font.width(s1.substring(0, Math.max(Math.min(j, s1.length()), 0)));
                int k3 = j3 - this.minecraft.font.width(s1) / 2;
                if (flag1 && j < s1.length()) {
                    fill(matrixStack, k3, l - 1, k3 + 1, l + 9, -16777216 | 16777215);
                }

                if (k != j) {
                    int l3 = Math.min(j, k);
                    int l1 = Math.max(j, k);
                    int i2 = this.minecraft.font.width(s1.substring(0, l3)) - this.minecraft.font.width(s1) / 2;
                    int j2 = this.minecraft.font.width(s1.substring(0, l1)) - this.minecraft.font.width(s1) / 2;
                    int k2 = Math.min(i2, j2);
                    int l2 = Math.max(i2, j2);
                    Tesselator tessellator = Tesselator.getInstance();
                    BufferBuilder bufferbuilder = tessellator.getBuilder();
                    RenderSystem.disableTexture();
                    RenderSystem.enableColorLogicOp();
                    RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
                    bufferbuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
                    bufferbuilder.vertex(matrix4f, (float) k2, (float) (l + 9), 0.0F).color(0, 0, 255, 255).endVertex();
                    bufferbuilder.vertex(matrix4f, (float) l2, (float) (l + 9), 0.0F).color(0, 0, 255, 255).endVertex();
                    bufferbuilder.vertex(matrix4f, (float) l2, (float) l, 0.0F).color(0, 0, 255, 255).endVertex();
                    bufferbuilder.vertex(matrix4f, (float) k2, (float) l, 0.0F).color(0, 0, 255, 255).endVertex();
                    BufferUploader.drawWithShader(bufferbuilder.end());
                    RenderSystem.disableColorLogicOp();
                    RenderSystem.enableTexture();
                }
            }
        }

        matrixStack.popPose();
        Lighting.setupFor3DItems();
        matrixStack.pushPose();
        matrixStack.translate(0, 0, 100);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        matrixStack.popPose();
    }
}