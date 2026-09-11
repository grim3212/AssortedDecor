package com.grim3212.assorted.decor.client.screen;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.common.blocks.blockentity.NeonSignBlockEntity;
import com.grim3212.assorted.decor.common.network.NeonChangeModePacket;
import com.grim3212.assorted.decor.common.network.NeonUpdatePacket;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.TextCursorUtils;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Util;

import java.util.stream.IntStream;

/**
 * The neon sign editor, modelled on vanilla's {@code AbstractSignEditScreen}. Like
 * {@code SignEditScreen}, it previews the board as a flat texture, one of {@link #SIGN_BACKGROUNDS}
 * chosen by {@link NeonSignBlockEntity#mode}, because a screen cannot draw the sign model.
 */
public class NeonSignScreen extends Screen {

    private static final int LINE_HEIGHT = 10;
    private static final int TEXT_COLOR = -1;

    /**
     * Reference to the sign object.
     */
    private final NeonSignBlockEntity tileSign;
    /**
     * The index of the line that is being edited.
     */
    private int editLine;

    private long cursorBlinkStartTime;

    private final int bgWidth = 176;
    private final int bgHeight = 208;
    private TextFieldHelper textInputUtil;
    private final String[] lines;

    public static final Identifier NEON_SIGN_GUI_TEXTURE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/screen/neon_sign.png");

    /**
     * The board preview per {@link NeonSignBlockEntity#mode}: vanilla's 24x26 {@code gui/signs}
     * shape, cut from the matching neon sign entity textures.
     */
    private static final Identifier[] SIGN_BACKGROUNDS = {
            signBackground("neon_sign"), signBackground("neon_sign_white"), signBackground("neon_sign_clear")
    };

    /**
     * The scale vanilla's {@code SignEditScreen} draws a sign board at, and the offset it draws it
     * from. Kept identical so a neon sign is previewed at the same size as a vanilla one.
     */
    private static final float BOARD_SCALE = 3.9F;
    private static final float BOARD_OFFSET_Y = 27.0F;
    private static final int BOARD_TEXTURE_WIDTH = 24;
    private static final int BOARD_TEXTURE_HEIGHT = 26;

    /**
     * Only the board is drawn, not the post, as vanilla crops a wall sign: at {@link #BOARD_SCALE}
     * the post would cover the colour buttons.
     */
    private static final int BOARD_DISPLAYED_HEIGHT = 12;

    private static Identifier signBackground(String name) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/signs/" + name + ".png");
    }

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

        this.cursorBlinkStartTime = Util.getMillis();

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
    }

    private void addSignText(int id) {
        this.textInputUtil.insertText(getFormatting(id).toString());
        this.textInputUtil.setCursorToEnd();
    }

    private void close() {
        this.tileSign.setChanged();
        this.minecraft.gui.setScreen(null);
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
        // ChatFormatting lost its numeric ids; the sixteen colours are still the first sixteen
        // constants, declared in the order those ids used.
        return buttonId >= 1 && buttonId <= 16 ? ChatFormatting.values()[buttonId - 1] : ChatFormatting.RESET;
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        this.textInputUtil.charTyped(event);
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.isUp()) {
            this.editLine = this.editLine - 1 & 3;
            this.textInputUtil.setCursorToEnd();
            return true;
        } else if (event.isDown() || event.isConfirmation()) {
            this.editLine = this.editLine + 1 & 3;
            this.textInputUtil.setCursorToEnd();
            return true;
        } else {
            return this.textInputUtil.keyPressed(event) ? true : super.keyPressed(event);
        }
    }

    /**
     * Draws the 176x208 panel from {@code neon_sign.png}, which every widget is positioned against,
     * in the background stratum so the widgets land on top of it.
     */
    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);

        graphics.blit(RenderPipelines.GUI_TEXTURED, NEON_SIGN_GUI_TEXTURE,
                (this.width - this.bgWidth) / 2, (this.height - this.bgHeight) / 2,
                0.0F, 0.0F, this.bgWidth, this.bgHeight, 256, 256);

        this.extractSignBoard(graphics);
    }

    /**
     * The sign board, centred where {@link #extractSignText} centres the lines. Drawn with the
     * background so the widgets and text land on top.
     */
    private void extractSignBoard(GuiGraphicsExtractor graphics) {
        graphics.pose().pushMatrix();
        graphics.pose().translate((float) this.signOriginX(), (float) this.signOriginY());
        graphics.pose().translate(0.0F, BOARD_OFFSET_Y);
        graphics.pose().scale(BOARD_SCALE, BOARD_SCALE);
        graphics.blit(RenderPipelines.GUI_TEXTURED, this.signBackground(), -12, -13, 0.0F, 0.0F,
                BOARD_TEXTURE_WIDTH, BOARD_DISPLAYED_HEIGHT, BOARD_TEXTURE_WIDTH, BOARD_TEXTURE_HEIGHT);
        graphics.pose().popMatrix();
    }

    private Identifier signBackground() {
        int mode = this.tileSign.mode;
        return SIGN_BACKGROUNDS[mode >= 0 && mode < SIGN_BACKGROUNDS.length ? mode : 0];
    }

    private int signOriginX() {
        return this.width / 2;
    }

    /**
     * Anchored to the panel the buttons are laid out against, so the board and the text keep their
     * place above them at any window size.
     */
    private int signOriginY() {
        return (this.height - this.bgHeight) / 2 + 80;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);

        graphics.centeredText(this.font, this.title, this.width / 2, 40, -1);
        this.extractSignText(graphics);
    }

    private void extractSignText(GuiGraphicsExtractor graphics) {
        int originX = this.signOriginX();
        int originY = this.signOriginY();

        boolean showCursor = TextCursorUtils.isCursorVisible(Util.getMillis() - this.cursorBlinkStartTime);
        int cursorPos = this.textInputUtil.getCursorPos();
        int selectionPos = this.textInputUtil.getSelectionPos();
        int signMidpoint = 4 * LINE_HEIGHT / 2;
        int cursorY = originY + this.editLine * LINE_HEIGHT - signMidpoint;

        for (int i = 0; i < this.lines.length; i++) {
            String line = this.lines[i];
            if (line == null) {
                continue;
            }

            if (this.font.isBidirectional()) {
                line = this.font.bidirectionalShaping(line);
            }

            int x = originX - this.font.width(line) / 2;
            graphics.text(this.font, line, x, originY + i * LINE_HEIGHT - signMidpoint, TEXT_COLOR, false);

            if (i != this.editLine || cursorPos < 0) {
                continue;
            }

            int cursorX = originX + this.font.width(line.substring(0, Math.max(Math.min(cursorPos, line.length()), 0))) - this.font.width(line) / 2;
            if (showCursor) {
                if (cursorPos >= line.length()) {
                    TextCursorUtils.extractAppendCursor(graphics, this.font, cursorX, cursorY, TEXT_COLOR, false);
                } else {
                    TextCursorUtils.extractInsertCursor(graphics, cursorX, cursorY, ARGB.opaque(TEXT_COLOR), LINE_HEIGHT);
                }
            }

            if (selectionPos != cursorPos) {
                int startIndex = Math.min(cursorPos, selectionPos);
                int endIndex = Math.max(cursorPos, selectionPos);
                int startPosX = originX + this.font.width(line.substring(0, startIndex)) - this.font.width(line) / 2;
                int endPosX = originX + this.font.width(line.substring(0, endIndex)) - this.font.width(line) / 2;
                graphics.textHighlight(Math.min(startPosX, endPosX), cursorY, Math.max(startPosX, endPosX), cursorY + LINE_HEIGHT, true);
            }
        }
    }
}
