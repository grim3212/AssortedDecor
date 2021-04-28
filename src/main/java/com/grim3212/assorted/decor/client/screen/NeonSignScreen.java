package com.grim3212.assorted.decor.client.screen;

import java.util.stream.IntStream;

import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.client.handler.NeonSignStitchHandler;
import com.grim3212.assorted.decor.common.block.NeonSignStandingBlock;
import com.grim3212.assorted.decor.common.block.tileentity.NeonSignTileEntity;
import com.grim3212.assorted.decor.common.network.NeonChangeModePacket;
import com.grim3212.assorted.decor.common.network.NeonUpdatePacket;
import com.grim3212.assorted.decor.common.network.PacketHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.client.gui.fonts.TextInputUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.SignTileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NeonSignScreen extends Screen {
	private final SignTileEntityRenderer.SignModel signModel = new SignTileEntityRenderer.SignModel();
	/** Reference to the sign object. */
	private final NeonSignTileEntity tileSign;
	/** Counts the number of screen updates. */
	private int updateCounter;
	/** The index of the line that is being edited. */
	private int editLine;

	private final int bgWidth = 176;
	private final int bgHeight = 208;
	private TextInputUtil textInputUtil;
	private final String[] lines;

	public static final ResourceLocation NEON_SIGN_GUI_TEXTURE = new ResourceLocation(AssortedDecor.MODID, "textures/gui/screen/neon_sign.png");

	public NeonSignScreen(NeonSignTileEntity teSign) {
		super(new TranslationTextComponent("sign.edit"));
		this.tileSign = teSign;
		this.lines = IntStream.range(0, 4).mapToObj(teSign::getText).map(ITextComponent::getString).toArray((p_243354_0_) -> {
			return new String[p_243354_0_];
		});
	}

	@Override
	public void init() {
		int x = (width - bgWidth) / 2;
		int y = (height - bgHeight) / 2;

		this.buttons.clear();
		this.children.clear();

		this.textInputUtil = new TextInputUtil(() -> {
			return this.lines[this.editLine];
		}, (s) -> {
			this.lines[this.editLine] = s;
			this.tileSign.setText(this.editLine, new StringTextComponent(s));
		}, TextInputUtil.getClipboardTextSupplier(this.minecraft), TextInputUtil.getClipboardTextSetter(this.minecraft), (s) -> {
			return this.minecraft.fontRenderer.getStringWidth(s) <= 90;
		});

		this.minecraft.keyboardListener.enableRepeatEvents(true);
		this.addButton(new Button(x + (bgWidth - 154) / 2, y + 179, 154, 20, new TranslationTextComponent("gui.done"), btn -> {
			this.close();
		}));

		for (int l = 0; l < 11; l++) {
			final int id = l + 1;
			this.addButton(new NeonButton(x + 11 + 14 * l, y + 136, new StringTextComponent(""), 176, l * 14, btn -> {
				addSignText(id);
			}));
		}

		for (int i1 = 11; i1 < 16; i1++) {
			final int id = i1 + 1;
			this.addButton(new NeonButton(x + 11 + 14 * (i1 - 11), y + 150, new StringTextComponent(""), 176, i1 * 14, btn -> {
				addSignText(id);
			}));
		}

		this.addButton(new NeonButton(x + 11 + 70, y + 150, new TranslationTextComponent("screen.assorteddecor.neon_sign.bold"), 204, 0, btn -> {
			addSignText(17);
		}));
		this.addButton(new NeonButton(x + 11 + 84, y + 150, new TranslationTextComponent("screen.assorteddecor.neon_sign.italic"), 204, 14, btn -> {
			addSignText(18);
		}));
		this.addButton(new NeonButton(x + 11 + 98, y + 150, new TranslationTextComponent("screen.assorteddecor.neon_sign.underline"), 204, 28, btn -> {
			addSignText(19);
		}));
		this.addButton(new NeonButton(x + 11 + 112, y + 150, new TranslationTextComponent("screen.assorteddecor.neon_sign.strikethrough"), 204, 42, btn -> {
			addSignText(20);
		}));
		this.addButton(new NeonButton(x + 11 + 126, y + 150, new TranslationTextComponent("screen.assorteddecor.neon_sign.random"), 204, 56, btn -> {
			addSignText(21);
		}));
		this.addButton(new NeonButton(x + 11 + 140, y + 150, new TranslationTextComponent("screen.assorteddecor.neon_sign.reset"), 204, 70, btn -> {
			addSignText(22);
		}));
		this.addButton(new NeonButton(x + 11, y + 164, new StringTextComponent(""), 0, 208, 51, true, btn -> {
			NeonSignScreen.this.tileSign.mode = 0;
			PacketHandler.sendToServer(new NeonChangeModePacket(0, NeonSignScreen.this.tileSign.getPos()));
		}));
		this.addButton(new NeonButton(x + 11 + 51, y + 164, new StringTextComponent(""), 51, 208, 52, true, btn -> {
			NeonSignScreen.this.tileSign.mode = 1;
			PacketHandler.sendToServer(new NeonChangeModePacket(1, NeonSignScreen.this.tileSign.getPos()));
		}));
		this.addButton(new NeonButton(x + 11 + 103, y + 164, new StringTextComponent(""), 103, 208, 51, true, btn -> {
			NeonSignScreen.this.tileSign.mode = 2;
			PacketHandler.sendToServer(new NeonChangeModePacket(2, NeonSignScreen.this.tileSign.getPos()));
		}));
	}

	private void addSignText(int id) {
		this.textInputUtil.putText(getFormatting(id).toString());
		this.textInputUtil.moveCursorToEnd();
	}

	private void close() {
		this.tileSign.markDirty();
		this.minecraft.displayGuiScreen((Screen) null);
	}

	@Override
	public void closeScreen() {
		this.close();
	}

	@Override
	public void onClose() {
		this.minecraft.keyboardListener.enableRepeatEvents(false);

		// Update lines on server side
		PacketHandler.sendToServer(new NeonUpdatePacket(this.tileSign.getPos(), this.tileSign.signText));
	}

	@Override
	public void tick() {
		++this.updateCounter;
		if (!this.tileSign.getType().isValidBlock(this.tileSign.getBlockState().getBlock())) {
			this.close();
		}
	}

	private TextFormatting getFormatting(int buttonId) {
		switch (buttonId) {
		case 11:
			return TextFormatting.GREEN;
		case 12:
			return TextFormatting.AQUA;
		case 13:
			return TextFormatting.RED;
		case 14:
			return TextFormatting.LIGHT_PURPLE;
		case 15:
			return TextFormatting.YELLOW;
		case 16:
			return TextFormatting.WHITE;
		case 17:
			return TextFormatting.BOLD;
		case 18:
			return TextFormatting.ITALIC;
		case 19:
			return TextFormatting.UNDERLINE;
		case 20:
			return TextFormatting.STRIKETHROUGH;
		case 21:
			return TextFormatting.OBFUSCATED;
		case 22:
			return TextFormatting.RESET;
		}
		return TextFormatting.fromColorIndex(buttonId - 1);
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		this.textInputUtil.putChar(codePoint);
		return true;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 265) {
			this.editLine = this.editLine - 1 & 3;
			this.textInputUtil.moveCursorToEnd();
			return true;
		} else if (keyCode != 264 && keyCode != 257 && keyCode != 335) {
			return this.textInputUtil.specialKeyPressed(keyCode) ? true : super.keyPressed(keyCode, scanCode, modifiers);
		} else {
			this.editLine = this.editLine + 1 & 3;
			this.textInputUtil.moveCursorToEnd();
			return true;
		}
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		RenderHelper.setupGuiFlatDiffuseLighting();
		this.renderBackground(matrixStack);
		drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 40, 16777215);
		matrixStack.push();
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
		matrixStack.push();
		matrixStack.scale(f1, -f1, -f1);
		IRenderTypeBuffer.Impl irendertypebuffer$impl = this.minecraft.getRenderTypeBuffers().getBufferSource();
		RenderMaterial rendermaterial = new RenderMaterial(Atlases.SIGN_ATLAS, NeonSignStitchHandler.getSignTexture(tileSign.mode));
		IVertexBuilder ivertexbuilder = rendermaterial.getBuffer(irendertypebuffer$impl, this.signModel::getRenderType);
		this.signModel.signBoard.render(matrixStack, ivertexbuilder, 15728880, OverlayTexture.NO_OVERLAY);
		if (flag) {
			this.signModel.signStick.render(matrixStack, ivertexbuilder, 15728880, OverlayTexture.NO_OVERLAY);
		}

		matrixStack.pop();
		float f2 = 0.010416667F;
		matrixStack.translate(0.0D, (double) 0.33333334F, (double) 0.046666667F);
		matrixStack.scale(f2, -f2, f2);
		int j = this.textInputUtil.getSelectionEnd();
		int k = this.textInputUtil.getSelectionStart();
		int l = this.editLine * 10 - this.lines.length * 5;
		Matrix4f matrix4f = matrixStack.getLast().getMatrix();

		for (int i1 = 0; i1 < this.lines.length; ++i1) {
			String s = this.lines[i1];
			if (s != null) {
				if (this.font.getBidiFlag()) {
					s = this.font.bidiReorder(s);
				}

				float f3 = (float) (-this.minecraft.fontRenderer.getStringWidth(s) / 2);
				this.minecraft.fontRenderer.drawText(matrixStack, this.tileSign.getText(i1), f3, (float) (i1 * 10 - this.lines.length * 5), 16777215);
				if (i1 == this.editLine && j >= 0 && flag1) {
					int j1 = this.minecraft.fontRenderer.getStringWidth(s.substring(0, Math.max(Math.min(j, s.length()), 0)));
					int k1 = j1 - this.minecraft.fontRenderer.getStringWidth(s) / 2;
					if (j >= s.length()) {
						this.minecraft.fontRenderer.drawString(matrixStack, "_", (float) k1, (float) l, 16777215);
					}
				}
			}
		}

		irendertypebuffer$impl.finish();

		for (int i3 = 0; i3 < this.lines.length; ++i3) {
			String s1 = this.lines[i3];
			if (s1 != null && i3 == this.editLine && j >= 0) {
				int j3 = this.minecraft.fontRenderer.getStringWidth(s1.substring(0, Math.max(Math.min(j, s1.length()), 0)));
				int k3 = j3 - this.minecraft.fontRenderer.getStringWidth(s1) / 2;
				if (flag1 && j < s1.length()) {
					fill(matrixStack, k3, l - 1, k3 + 1, l + 9, -16777216 | 16777215);
				}

				if (k != j) {
					int l3 = Math.min(j, k);
					int l1 = Math.max(j, k);
					int i2 = this.minecraft.fontRenderer.getStringWidth(s1.substring(0, l3)) - this.minecraft.fontRenderer.getStringWidth(s1) / 2;
					int j2 = this.minecraft.fontRenderer.getStringWidth(s1.substring(0, l1)) - this.minecraft.fontRenderer.getStringWidth(s1) / 2;
					int k2 = Math.min(i2, j2);
					int l2 = Math.max(i2, j2);
					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder bufferbuilder = tessellator.getBuffer();
					RenderSystem.disableTexture();
					RenderSystem.enableColorLogicOp();
					RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
					bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
					bufferbuilder.pos(matrix4f, (float) k2, (float) (l + 9), 0.0F).color(0, 0, 255, 255).endVertex();
					bufferbuilder.pos(matrix4f, (float) l2, (float) (l + 9), 0.0F).color(0, 0, 255, 255).endVertex();
					bufferbuilder.pos(matrix4f, (float) l2, (float) l, 0.0F).color(0, 0, 255, 255).endVertex();
					bufferbuilder.pos(matrix4f, (float) k2, (float) l, 0.0F).color(0, 0, 255, 255).endVertex();
					bufferbuilder.finishDrawing();
					WorldVertexBufferUploader.draw(bufferbuilder);
					RenderSystem.disableColorLogicOp();
					RenderSystem.enableTexture();
				}
			}
		}

		matrixStack.pop();
		RenderHelper.setupGui3DDiffuseLighting();
		matrixStack.push();
		matrixStack.translate(0, 0, 100);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		matrixStack.pop();
	}
}