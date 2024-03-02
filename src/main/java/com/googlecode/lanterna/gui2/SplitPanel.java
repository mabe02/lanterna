/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
 *
 * lanterna is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2010-2020 Martin Berglund
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.*;
import com.googlecode.lanterna.input.*;

import java.util.*;

/**
 * @author ginkoblongata
 */
public class SplitPanel extends Panel {

    private final Component compA;
    private final ImageComponent thumb;
    private final Component compB;

    private boolean isHorizontal;
    private double ratio = 0.5;

    public static SplitPanel ofHorizontal(Component left, Component right) {
        SplitPanel split = new SplitPanel(left, right, true);
        return split;
    }

    public static SplitPanel ofVertical(Component top, Component bottom) {
        SplitPanel split = new SplitPanel(top, bottom, false);
        return split;
    }

    /**
     *
     */
    protected SplitPanel(Component a, Component b, boolean isHorizontal) {
        this.compA = a;
        this.compB = b;
        this.isHorizontal = isHorizontal;
        thumb = makeThumb();
        setLayoutManager(new ScrollPanelLayoutManager());
        setRatio(10, 10);

        addComponent(a);
        addComponent(thumb);
        addComponent(b);
    }

    ImageComponent makeThumb() {
        ImageComponent imageComponent = new ImageComponent() {
            TerminalSize aSize;
            TerminalSize bSize;
            TerminalSize tSize;
            TerminalPosition down = null;
            TerminalPosition drag = null;

            @Override
            public Result handleKeyStroke(KeyStroke keyStroke) {
                Result result;
                if (keyStroke instanceof MouseAction) {
                    result = handleMouseAction((MouseAction) keyStroke);
                }
                // TODO: Implement keyboard based resizing
                else {
                    result = super.handleKeyStroke(keyStroke);
                }
                return result;
            }

            private Result handleMouseAction(MouseAction mouseAction) {
                if (mouseAction.isMouseDown()) {
                    aSize = compA.getSize();
                    bSize = compB.getSize();
                    tSize = thumb.getSize();
                    down = mouseAction.getPosition();
                }
                if (mouseAction.isMouseDrag()) {
                    drag = mouseAction.getPosition();

                    // xxxxxxxxxxxxxxxxxxxxx
                    // this is a hack, should not be needed if the pane drag
                    // only on mouse down'd comp stuff was completely working
                    if (down == null) {
                        down = drag;
                    }
                    // xxxxxxxxxxxxxxxxxxxxx

                    int delta = isHorizontal ? drag.minus(down).getColumn() : drag.minus(down).getRow();
                    // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
                    if (isHorizontal) {
                        int a = Math.max(1, tSize.getColumns() + aSize.getColumns() + delta);
                        int b = Math.max(1, bSize.getColumns() - delta);
                        setRatio(a, b);
                    } else {
                        int a = Math.max(1, tSize.getRows() + aSize.getRows() + delta);
                        int b = Math.max(1, bSize.getRows() - delta);
                        setRatio(a, b);
                    }
                    // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
                }
                if (mouseAction.isMouseUp()) {
                    down = null;
                    drag = null;
                }
                return Result.HANDLED;
            }
        };
        return imageComponent;
    }

    class ScrollPanelLayoutManager implements LayoutManager {

        boolean hasChanged;

        public ScrollPanelLayoutManager() {
            hasChanged = true;
        }


        @Override
        public TerminalSize getPreferredSize(List<Component> components) {
            TerminalSize sizeA = compA.getPreferredSize();
            int aWidth = sizeA.getColumns();
            int aHeight = sizeA.getRows();
            TerminalSize sizeB = compB.getPreferredSize();
            int bWidth = sizeB.getColumns();
            int bHeight = sizeB.getRows();

            int tWidth = thumb.getPreferredSize().getColumns();
            int tHeight = thumb.getPreferredSize().getRows();

            if (isHorizontal) {
                return new TerminalSize(aWidth + tWidth + bWidth, Math.max(aHeight, Math.max(tHeight, bHeight)));
            } else {
                return new TerminalSize(Math.max(aWidth, Math.max(tWidth, bWidth)), aHeight + tHeight + bHeight);
            }
        }

        @Override
        public void doLayout(TerminalSize area, List<Component> components) {
            TerminalSize size = getSize();

            // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
            // TODO: themed
            int length = isHorizontal ? size.getRows() : size.getColumns();
            TerminalSize tsize = new TerminalSize(isHorizontal ? 1 : length, !isHorizontal ? 1 : length);
            TextImage textImage = new BasicTextImage(tsize);
            Theme theme = getTheme();
            ThemeDefinition themeDefinition = theme.getDefaultDefinition();
            ThemeStyle themeStyle = themeDefinition.getNormal();

            TextCharacter thumbRenderer = TextCharacter.fromCharacter(
                    isHorizontal ? Symbols.SINGLE_LINE_VERTICAL : Symbols.SINGLE_LINE_HORIZONTAL,
                    themeStyle.getForeground(),
                    themeStyle.getBackground());
            if (thumb.isFocused()) {
                thumbRenderer = thumbRenderer.withModifier(SGR.BOLD);
            }

            textImage.setAll(thumbRenderer);
            thumb.setTextImage(textImage);
            // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

            int tWidth = thumb.getPreferredSize().getColumns();
            int tHeight = thumb.getPreferredSize().getRows();

            int w = size.getColumns();
            int h = size.getRows();

            if (isHorizontal) {
                w -= tWidth;
            } else {
                h -= tHeight;
            }

            TerminalSize compAPrevSize = compA.getSize();
            TerminalSize compBPrevSize = compB.getSize();
            TerminalSize thumbPrevSize = thumb.getSize();
            TerminalPosition compAPrevPos = compA.getPosition();
            TerminalPosition compBPrevPos = compB.getPosition();
            TerminalPosition thumbPrevPos = thumb.getPosition();

            if (isHorizontal) {
                int leftWidth = Math.max(0, (int) (w * ratio));
                int leftHeight = Math.max(0, Math.min(compA.getPreferredSize().getRows(), h));

                int rightWidth = Math.max(0, w - leftWidth);
                int rightHeight = Math.max(0, Math.min(compB.getPreferredSize().getRows(), h));

                compA.setSize(new TerminalSize(leftWidth, leftHeight));
                thumb.setSize(thumb.getPreferredSize());
                compB.setSize(new TerminalSize(rightWidth, rightHeight));

                compA.setPosition(new TerminalPosition(0, 0));
                thumb.setPosition(new TerminalPosition(leftWidth, h / 2 - tHeight / 2));
                compB.setPosition(new TerminalPosition(leftWidth + tWidth, 0));
            } else {
                int leftWidth = Math.max(0, Math.min(compA.getPreferredSize().getColumns(), w));
                int leftHeight = Math.max(0, (int) (h * ratio));

                int rightWidth = Math.max(0, Math.min(compB.getPreferredSize().getColumns(), w));
                int rightHeight = Math.max(0, h - leftHeight);

                compA.setSize(new TerminalSize(leftWidth, leftHeight));
                thumb.setSize(thumb.getPreferredSize());
                compB.setSize(new TerminalSize(rightWidth, rightHeight));

                compA.setPosition(new TerminalPosition(0, 0));
                thumb.setPosition(new TerminalPosition(w / 2 - tWidth / 2, leftHeight));
                compB.setPosition(new TerminalPosition(0, leftHeight + tHeight));
            }

            hasChanged = !compAPrevPos.equals(compA.getPosition()) ||
                    !compAPrevSize.equals(compA.getSize()) ||
                    !compBPrevPos.equals(compB.getPosition()) ||
                    !compBPrevSize.equals(compB.getSize()) ||
                    !thumbPrevPos.equals(thumb.getPosition()) ||
                    !thumbPrevSize.equals(thumb.getSize());
        }

        @Override
        public boolean hasChanged() {
            return hasChanged;
        }
    }

    /*
     * Use whatever sizing.
     *
     *
     */
    public void setRatio(int left, int right) {
        if (left == 0 || right == 0) {
            ratio = 0.5;
        }
        else {
            int total = Math.abs(left) + Math.abs(right);
            ratio = (double) left / (double) total;
        }
    }

    public void setThumbVisible(boolean visible) {
        thumb.setVisible(visible);

        if (visible) {
            this.setPreferredSize(null);
        } else {
            thumb.setPreferredSize(new TerminalSize(1, 1));
        }
    }

    @Override
    public boolean isInvalid() {
        return super.isInvalid();
    }
}

