package com.grim3212.assorted.decor.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class NeonButton extends Button {

    private int texX;
    private int texY;
    private int hoverCount;
    private boolean changeHoverDir = false;

    public NeonButton(int x, int y, Component buttonText, int texX, int texY, Button.OnPress onPress) {
        this(x, y, buttonText, texX, texY, -1, false, onPress);
    }

    public NeonButton(int x, int y, Component buttonText, int texX, int texY, boolean changeHoverDir, Button.OnPress onPress) {
        this(x, y, buttonText, texX, texY, -1, changeHoverDir, onPress);
    }

    public NeonButton(int x, int y, Component buttonText, int texX, int texY, int width, Button.OnPress onPress) {
        this(x, y, buttonText, texX, texY, width, false, onPress);
    }

    public NeonButton(int x, int y, Component buttonText, int texX, int texY, int width, boolean changeHoverDir, Button.OnPress onPress) {
        super(x, y, 14, 14, buttonText, onPress, Button.DEFAULT_NARRATION);
        this.texX = texX;
        this.texY = texY;
        this.changeHoverDir = changeHoverDir;
        if (width != -1)
            this.width = width;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            RenderSystem.setShaderTexture(0, NeonSignScreen.NEON_SIGN_GUI_TEXTURE);
            RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
            int i = this.getTextureY(this.isHovered);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            guiGraphics.blit(NeonSignScreen.NEON_SIGN_GUI_TEXTURE, this.getX(), this.getY(), texX + (changeHoverDir ? 0 : width * (i - 1)), texY + (changeHoverDir ? height * (i - 1) : 0), this.width, this.height);
        }
    }

    private int getTextureY(boolean isHovered) {
        int i = 1;
        if (!this.active) {
            i = 0;
        } else if (isHovered) {
            i = 2;
        }

        return i;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        if (this.isMouseOver(mouseX, mouseY)) {
            this.hoverCount++;
        } else if (!this.isMouseOver(mouseX, mouseY) && this.hoverCount > 0) {
            this.hoverCount = 0;
        }

        if (this.hoverCount > 30 && !this.getMessage().getString().isEmpty()) {
            Minecraft mc = Minecraft.getInstance();
            guiGraphics.renderTooltip(mc.font, getMessage(), getX(), getY());
        }
    }

}
