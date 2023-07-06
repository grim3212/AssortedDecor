package com.grim3212.assorted.decor.client.screen;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.common.inventory.CageContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CageScreen extends AbstractContainerScreen<CageContainer> {

    private static final ResourceLocation CAGE_GUI_TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/gui/container/cage.png");

    public CageScreen(CageContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {
        RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, CAGE_GUI_TEXTURE);
        int i = this.leftPos;
        int j = this.topPos;
        guiGraphics.blit(CAGE_GUI_TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }
}
