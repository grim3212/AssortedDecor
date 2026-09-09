package com.grim3212.assorted.decor.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

/**
 * A widget records itself into a {@link GuiGraphicsExtractor} now instead of drawing: {@code Button}
 * is abstract and the hook is {@code extractContents}, called from {@code AbstractButton} after the
 * base widget has already worked out whether the mouse is over it. The manual bounds test, the
 * {@code RenderSystem} blend setup (the GUI pipeline owns blending) and the hover counter that
 * hand-rolled a tooltip delay are all gone with it - a widget carries a {@link Tooltip} which the
 * screen shows on its own schedule.
 */
public class NeonButton extends Button {

    private final int texX;
    private final int texY;
    private final boolean changeHoverDir;

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
        super(x, y, width != -1 ? width : 14, 14, buttonText, onPress, Button.DEFAULT_NARRATION);
        this.texX = texX;
        this.texY = texY;
        this.changeHoverDir = changeHoverDir;

        if (!buttonText.getString().isEmpty()) {
            this.setTooltip(Tooltip.create(buttonText));
        }
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        int i = this.getTextureY(this.isHovered());
        graphics.blit(RenderPipelines.GUI_TEXTURED, NeonSignScreen.NEON_SIGN_GUI_TEXTURE, this.getX(), this.getY(), (float) (this.texX + (this.changeHoverDir ? 0 : this.width * (i - 1))), (float) (this.texY + (this.changeHoverDir ? this.height * (i - 1) : 0)), this.width, this.height, 256, 256);
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
}
