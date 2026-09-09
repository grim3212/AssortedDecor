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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Util;

import java.util.stream.IntStream;

/**
 * Rebuilt on the retained-mode GUI, following vanilla's {@code AbstractSignEditScreen}: the screen
 * records elements into a {@link GuiGraphicsExtractor} from {@code extractRenderState} instead of
 * drawing from {@code render}, and the cursor and selection are drawn by
 * {@link TextCursorUtils} / {@link GuiGraphicsExtractor#textHighlight} rather than by a hand-built
 * {@code BufferBuilder} with a logic-op blend.
 * <p>
 * TODO(26.2): the 3D preview of the sign itself is gone.
 *  What it used to do: build a {@code SignRenderer.SignModel}, pick the board texture for the current
 *  {@link NeonSignBlockEntity#mode} off the {@code Sheets.SIGN_SHEET} atlas and render it into the
 *  screen through the {@code MultiBufferSource} behind {@code GuiGraphics#pose()}, with the editable
 *  text drawn on top of it in the same 3D pose.
 *  Why it cannot be expressed: {@code SignRenderer}, {@code Sheets.SIGN_SHEET} and
 *  {@code Material#buffer} were all deleted, and a screen has no {@code MultiBufferSource} to write
 *  into any more. Vanilla solved the same problem by dropping the model and blitting a flat
 *  {@code textures/gui/signs/<wood>.png} instead (see {@code SignEditScreen}); doing that here needs a
 *  new 24x26 GUI texture per neon sign mode, which the mod does not ship - the existing
 *  {@code textures/model/neon_sign*.png} are 64x32 entity-model sheets with a different layout. The
 *  text is laid out on its own until such a texture exists.
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

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);

        graphics.centeredText(this.font, this.title, this.width / 2, 40, -1);
        this.extractSignText(graphics);
    }

    private void extractSignText(GuiGraphicsExtractor graphics) {
        // Anchored to the same panel the buttons are laid out against, so the text keeps its place
        // above them at any window size.
        int originX = this.width / 2;
        int originY = (this.height - this.bgHeight) / 2 + 80;

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
