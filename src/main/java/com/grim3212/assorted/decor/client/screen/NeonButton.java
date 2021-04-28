package com.grim3212.assorted.decor.client.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class NeonButton extends Button {

	private int texX;
	private int texY;
	private int hoverCount;
	private boolean changeHoverDir = false;

	public NeonButton(int x, int y, ITextComponent buttonText, int texX, int texY, Button.IPressable onPress) {
		this(x, y, buttonText, texX, texY, -1, false, onPress);
	}

	public NeonButton(int x, int y, ITextComponent buttonText, int texX, int texY, boolean changeHoverDir, Button.IPressable onPress) {
		this(x, y, buttonText, texX, texY, -1, changeHoverDir, onPress);
	}

	public NeonButton(int x, int y, ITextComponent buttonText, int texX, int texY, int width, Button.IPressable onPress) {
		this(x, y, buttonText, texX, texY, width, false, onPress);
	}

	public NeonButton(int x, int y, ITextComponent buttonText, int texX, int texY, int width, boolean changeHoverDir, Button.IPressable onPress) {
		super(x, y, 14, 14, buttonText, onPress);
		this.texX = texX;
		this.texY = texY;
		this.changeHoverDir = changeHoverDir;
		if (width != -1)
			this.width = width;
	}
	
	@Override
	public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (this.visible) {
			Minecraft mc = Minecraft.getInstance();
			mc.getTextureManager().bindTexture(NeonSignScreen.NEON_SIGN_GUI_TEXTURE);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			int i = this.getYImage(this.isHovered);
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			this.blit(matrixStack, this.x, this.y, texX + (changeHoverDir ? 0 : width * (i - 1)), texY + (changeHoverDir ? height * (i - 1) : 0), this.width, this.height);
			this.renderBg(matrixStack, mc, mouseX, mouseY);
		}
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.render(matrixStack, mouseX, mouseY, partialTicks);

		if (this.isMouseOver(mouseX, mouseY)) {
			this.hoverCount++;
		} else if (!this.isMouseOver(mouseX, mouseY) && this.hoverCount > 0) {
			this.hoverCount = 0;
		}

		if (this.hoverCount > 30 && !this.getMessage().getString().isEmpty()) {
			Minecraft mc = Minecraft.getInstance();
			GuiUtils.drawHoveringText(matrixStack, ImmutableList.of(this.getMessage()), x, y, mc.currentScreen.width, mc.currentScreen.height, 120, mc.fontRenderer);
		}
	}

}
