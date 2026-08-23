package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.io.IOException;

import static java.lang.Math.min;

public class ScrollControllerTest extends TestBase {
    public static void main(final String[] args) throws IOException, InterruptedException {
        new ScrollControllerTest().run(args);
    }

    @Override
    public void init(final WindowBasedTextGUI textGUI) {
        final BasicWindow cbWindow = new BasicWindow("Checkerboard as ScrollController");
        final ScrollViewer sv = new ScrollViewer();

        sv.setPreferredSize(new TerminalSize(Checkerboard.CELL_W * 6 + 2,
                                             Checkerboard.CELL_H * 6 + 1));

        sv.setComponent(new Checkerboard());

        cbWindow.setComponent(
            Panels.vertical(
                sv,
                new CheckBox("Let Content Control Scrolling").addListener(
                    checked -> {
                        final int offsetX = sv.getHorizontalOffset();
                        final int offsetY = sv.getVerticalOffset();

                        sv.setCanContentControlScrolling(checked);

                        sv.setHorizontalOffset(offsetX);
                        sv.setVerticalOffset(offsetY);
                    }
                )
            )
        );

        textGUI.addWindow(cbWindow);
    }


    private final static class Checkerboard extends AbstractScrollableComponent<Checkerboard> implements Interactable {
        final static int DIM_XY = 20;
        final static int CELL_W = 6;
        final static int CELL_H = 2;

        @Override
        public void lineUp() {
            setVerticalOffset(getVerticalOffset() - CELL_H + (getVerticalOffset() % CELL_H));
        }

        @Override
        public void lineDown() {
            setVerticalOffset(getVerticalOffset() + CELL_H - (getVerticalOffset() % CELL_H));
        }

        @Override
        public void lineLeft() {
            setHorizontalOffset(getHorizontalOffset() - CELL_W + (getHorizontalOffset() % CELL_W));
        }

        @Override
        public void lineRight() {
            setHorizontalOffset(getHorizontalOffset() + CELL_W - (getHorizontalOffset() % CELL_W));
        }

        @Override
        protected ComponentRenderer<Checkerboard> createDefaultRenderer() {
            return new ComponentRenderer<Checkerboard>() {
                final TextColor BLACK = TextColor.Factory.fromString("#191919");
                final TextColor WHITE = TextColor.ANSI.WHITE;

                @Override
                public TerminalSize getPreferredSize(final Checkerboard c) {
                    return new TerminalSize(120, 40);
                }

                @Override
                public void drawComponent(final TextGUIGraphics g, final Checkerboard c) {
                    final int w = CELL_W * DIM_XY;
                    final int h = CELL_H * DIM_XY;

                    final boolean s = c.getScrollOwner() != null && c.getScrollOwner().getCanContentControlScrolling();

                    final int xOffset = s ? getHorizontalOffset() : 0;
                    final int yOffset = s ? getVerticalOffset() : 0;
                    final int vWidth = s ? getViewportWidth() : getExtentWidth();
                    final int vHeight = s ? getViewportHeight() : getExtentHeight();

                    final int dX = -(xOffset % CELL_W);
                    final int dY = -(yOffset % CELL_H);

                    for (int x = xOffset, m = min(xOffset + vWidth, w) + xOffset % CELL_W; x <= m; x += CELL_W * 2) {
                        final int col = x / CELL_W;
                        final boolean shift = col % 2 != 0;

                        for (int y = yOffset, n = min(yOffset + vHeight, h) + yOffset % CELL_H; y <= n; y+= CELL_H) {
                            final int row = y / CELL_H;

                            final TextColor bg1 = (row % 2 == 0) == shift ? BLACK : WHITE;
                            final TextColor bg2 = (row % 2 == 0) == shift ? WHITE : BLACK;

                            g.fillRectangle(
                                new TerminalPosition(x + dX - xOffset, y + dY - yOffset),
                                new TerminalSize(CELL_W, CELL_H),
                                TextCharacter.fromCharacter(' ', WHITE, bg1)
                            );
                            g.fillRectangle(
                                new TerminalPosition(x + CELL_W + dX - xOffset, y + dY - yOffset),
                                new TerminalSize(CELL_W, CELL_H),
                                TextCharacter.fromCharacter(' ', WHITE, bg2)
                            );
                        }
                    }
                }
            };
        }

        @Override
        public TerminalPosition getCursorLocation() {
            return null;
        }

        @Override
        public Result handleInput(final KeyStroke keyStroke) {
            final boolean scrollable = getScrollOwner() != null;

            // Skip the keystroke if ctrl, alt or shift was down
            if(!keyStroke.isAltDown() && !keyStroke.isCtrlDown()) {
                if(!keyStroke.isShiftDown()) {
                    switch(keyStroke.getKeyType()) {
                        case ARROW_DOWN:
                            return scrollable ? Result.UNHANDLED : Result.MOVE_FOCUS_DOWN;
                        case ARROW_LEFT:
                            return scrollable ? Result.UNHANDLED : Result.MOVE_FOCUS_LEFT;
                        case ARROW_RIGHT:
                            return scrollable ? Result.UNHANDLED : Result.MOVE_FOCUS_RIGHT;
                        case ARROW_UP:
                            return scrollable ? Result.UNHANDLED : Result.MOVE_FOCUS_UP;
                        case TAB:
                            return Result.MOVE_FOCUS_NEXT;
                        case REVERSE_TAB:
                            return Result.MOVE_FOCUS_PREVIOUS;
                        case MOUSE_EVENT:
                            getBasePane().setFocusedInteractable(this);
                            return Result.HANDLED;
                        default:
                    }
                }
                // On Mac at least, Shift+Tab is ReverseTab, and isShiftDown() always reports `true`.
                if (keyStroke.getKeyType() == KeyType.REVERSE_TAB) {
                    return Result.MOVE_FOCUS_PREVIOUS;
                }
            }
            return Result.UNHANDLED;
        }

        @Override
        public Interactable takeFocus() {
            return this;
        }

        @Override
        public void onEnterFocus(final FocusChangeDirection direction, final Interactable previouslyInFocus) {
        }

        @Override
        public void onLeaveFocus(final FocusChangeDirection direction, final Interactable nextInFocus) {
        }

        @Override
        public boolean isFocused() {
            return true;
        }

        @Override
        public Interactable setInputFilter(final InputFilter inputFilter) {
            return this;
        }

        @Override
        public InputFilter getInputFilter() {
            return null;
        }

        @Override
        public Interactable setEnabled(final boolean enabled) {
            return this;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public boolean isFocusable() {
            return true;
        }
    }
}
