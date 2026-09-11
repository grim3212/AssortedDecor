package com.grim3212.assorted.decor.client.screen;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.common.inventory.CageContainer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/** The cage screen. It adds only the background; the base screen draws the rest. */
public class CageScreen extends AbstractContainerScreen<CageContainer> {

    private static final Identifier CAGE_GUI_TEXTURE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/container/cage.png");

    public CageScreen(CageContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);

        int i = this.leftPos;
        int j = this.topPos;
        graphics.blit(RenderPipelines.GUI_TEXTURED, CAGE_GUI_TEXTURE, i, j, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
    }
}
