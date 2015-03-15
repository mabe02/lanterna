/*
 * This file is part of lanterna (http://code.google.com/p/lanterna/).
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
 * Copyright (C) 2010-2014 Martin
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.CJKUtils;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by martin on 05/10/14.
 */
public class TextBox extends AbstractInteractableComponent<TextBox> {

    public static enum Style {
        SINGLE_LINE,
        MULTILINE,
        ;
    }

    private final List<String> lines;
    private final Style style;

    private TerminalPosition caretPosition;
    private boolean readOnly;
    private boolean horizontalFocusSwitching;
    private boolean verticalFocusSwitching;
    private int maxLineLength;
    private int longestRow;
    private char unusedSpaceCharacter;
    private Character mask;

    public TextBox() {
        this(new TerminalSize(10, 1), "", Style.SINGLE_LINE);
    }

    public TextBox(String initialContent) {
        this(null, initialContent, initialContent.contains("\n") ? Style.MULTILINE : Style.SINGLE_LINE);
    }

    public TextBox(String initialContent, Style style) {
        this(null, initialContent, style);
    }

    public TextBox(TerminalSize preferredSize) {
        this(preferredSize, preferredSize.getRows() == 1 ? Style.SINGLE_LINE : Style.MULTILINE);
    }

    public TextBox(TerminalSize preferredSize, Style style) {
        this(preferredSize, "", style);
    }

    public TextBox(TerminalSize preferredSize, String initialContent) {
        this(preferredSize, initialContent, (preferredSize.getRows() == 1 && !initialContent.contains("\n")) ? Style.SINGLE_LINE : Style.MULTILINE);
    }

    public TextBox(TerminalSize preferredSize, String initialContent, Style style) {
        this.lines = new ArrayList<String>();
        this.style = style;
        this.readOnly = false;
        this.verticalFocusSwitching = true;
        this.horizontalFocusSwitching = true;
        this.caretPosition = TerminalPosition.TOP_LEFT_CORNER;
        this.maxLineLength = -1;
        this.longestRow = 1;    //To fit the cursor
        this.unusedSpaceCharacter = ' ';
        this.mask = null;
        setText(initialContent);
        if (preferredSize == null) {
            preferredSize = new TerminalSize(longestRow, lines.size());
        }
        setPreferredSize(preferredSize);
    }

    public TextBox setText(String text) {
        String[] split = text.split("\n");
        lines.clear();
        longestRow = 1;
        for (String line : split) {
            addLine(line);
        }
        if(caretPosition.getRow() > lines.size() - 1) {
            caretPosition = caretPosition.withRow(lines.size() - 1);
        }
        if(caretPosition.getColumn() > lines.get(caretPosition.getRow()).length()) {
            caretPosition = caretPosition.withColumn(lines.get(caretPosition.getRow()).length());
        }
        invalidate();
        return this;
    }

    @Override
    protected TextBoxRenderer getRenderer() {
        return (TextBoxRenderer)super.getRenderer(); //To change body of generated methods, choose Tools | Templates.
    }

    public void addLine(String line) {
        StringBuilder bob = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\n' && style == Style.MULTILINE) {
                String string = bob.toString();
                int lineWidth = CJKUtils.getTrueWidth(string);
                lines.add(string);
                if (longestRow < lineWidth + 1) {
                    longestRow = lineWidth + 1;
                }
                addLine(line.substring(i + 1));
                return;
            }
            else if (Character.isISOControl(c)) {
                continue;
            }

            bob.append(c);
        }
        String string = bob.toString();
        int lineWidth = CJKUtils.getTrueWidth(string);
        lines.add(string);
        if (longestRow < lineWidth + 1) {
            longestRow = lineWidth + 1;
        }
    }

    public TerminalPosition getCaretPosition() {
        return caretPosition;
    }

    public String getText() {
        StringBuilder bob = new StringBuilder(lines.get(0));
        for(int i = 1; i < lines.size(); i++) {
            bob.append("\n").append(lines.get(i));
        }
        return bob.toString();
    }

    public Character getMask() {
        return mask;
    }

    public TextBox setMask(Character mask) {
        if(mask != null && CJKUtils.isCharCJK(mask)) {
            throw new IllegalArgumentException("Cannot use a CJK character as a mask");
        }
        this.mask = mask;
        invalidate();
        return this;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public TextBox setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        invalidate();
        return this;
    }

    /**
     * If {@code true}, the component will switch to the next available component above if the cursor is at the top of
     * the TextBox and the user presses the 'up' array key, or switch to the next available component below if the
     * cursor is at the bottom of the TextBox and the user presses the 'down' array key. The means that for single-line
     * TextBox:es, pressing up and down will always switch focus.
     * @return {@code true} if vertical focus switching is enabled
     */
    public boolean isVerticalFocusSwitching() {
        return verticalFocusSwitching;
    }

    /**
     * If set to {@code true}, the component will switch to the next available component above if the cursor is at the
     * top of the TextBox and the user presses the 'up' array key, or switch to the next available component below if
     * the cursor is at the bottom of the TextBox and the user presses the 'down' array key. The means that for
     * single-line TextBox:es, pressing up and down will always switch focus with this mode enabled.
     * @param verticalFocusSwitching If called with true, vertical focus switching will be enabled
     * @return Itself
     */
    public TextBox setVerticalFocusSwitching(boolean verticalFocusSwitching) {
        this.verticalFocusSwitching = verticalFocusSwitching;
        return this;
    }

    /**
     * If {@code true}, the TextBox will switch focus to the next available component to the left if the cursor in the
     * TextBox is at the left-most position (index 0) on the row and the user pressed the 'left' arrow key, or vice
     * versa for pressing the 'right' arrow key when the cursor in at the right-most position of the current row.
     * @return {@code true} if horizontal focus switching is enabled
     */
    public boolean isHorizontalFocusSwitching() {
        return horizontalFocusSwitching;
    }

    /**
     * If set to {@code true}, the TextBox will switch focus to the next available component to the left if the cursor
     * in the TextBox is at the left-most position (index 0) on the row and the user pressed the 'left' arrow key, or
     * vice versa for pressing the 'right' arrow key when the cursor in at the right-most position of the current row.
     * @param horizontalFocusSwitching If called with true, horizontal focus switching will be enabled
     * @return Itself
     */
    public TextBox setHorizontalFocusSwitching(boolean horizontalFocusSwitching) {
        this.horizontalFocusSwitching = horizontalFocusSwitching;
        return this;
    }

    public String getLine(int index) {
        return lines.get(index);
    }

    public int getLineCount() {
        return lines.size();
    }

    @Override
    protected TextBoxRenderer createDefaultRenderer() {
        return new DefaultTextBoxRenderer();
    }

    @Override
    public Result handleKeyStroke(KeyStroke keyStroke) {
        if(readOnly) {
            return handleKeyStrokeReadOnly(keyStroke);
        }
        String line = lines.get(caretPosition.getRow());
        switch (keyStroke.getKeyType()) {
            case Character:
                if(maxLineLength == -1 || maxLineLength > line.length() + 1) {
                    line = line.substring(0, caretPosition.getColumn()) + keyStroke.getCharacter() + line.substring(caretPosition.getColumn());
                    lines.set(caretPosition.getRow(), line);
                    caretPosition = caretPosition.withRelativeColumn(1);
                }
                return Result.HANDLED;
            case Backspace:
                if(caretPosition.getColumn() > 0) {
                    line = line.substring(0, caretPosition.getColumn() - 1) + line.substring(caretPosition.getColumn());
                    lines.set(caretPosition.getRow(), line);
                    caretPosition = caretPosition.withRelativeColumn(-1);
                }
                else if(style == Style.MULTILINE && caretPosition.getRow() > 0) {
                    lines.remove(caretPosition.getRow());
                    caretPosition = caretPosition.withRelativeRow(-1);
                    caretPosition = caretPosition.withColumn(lines.get(caretPosition.getRow()).length());
                    lines.set(caretPosition.getRow(), lines.get(caretPosition.getRow()) + line);
                }
                return Result.HANDLED;
            case Delete:
                if(caretPosition.getColumn() < line.length()) {
                    line = line.substring(0, caretPosition.getColumn()) + line.substring(caretPosition.getColumn() + 1);
                    lines.set(caretPosition.getRow(), line);
                }
                else if(style == Style.MULTILINE && caretPosition.getRow() < lines.size() - 1) {
                    lines.set(caretPosition.getRow(), line + lines.get(caretPosition.getRow() + 1));
                    lines.remove(caretPosition.getRow() + 1);
                }
                return Result.HANDLED;
            case ArrowLeft:
                if(caretPosition.getColumn() > 0) {
                    caretPosition = caretPosition.withRelativeColumn(-1);
                }
                else if(style == Style.MULTILINE && caretPosition.getRow() > 0) {
                    caretPosition = caretPosition.withRelativeRow(-1);
                    caretPosition = caretPosition.withColumn(lines.get(caretPosition.getRow()).length());
                }
                else if(horizontalFocusSwitching) {
                    return Result.MOVE_FOCUS_LEFT;
                }
                return Result.HANDLED;
            case ArrowRight:
                if(caretPosition.getColumn() < lines.get(caretPosition.getRow()).length()) {
                    caretPosition = caretPosition.withRelativeColumn(1);
                }
                else if(style == Style.MULTILINE && caretPosition.getRow() < lines.size() - 1) {
                    caretPosition = caretPosition.withRelativeRow(1);
                    caretPosition = caretPosition.withColumn(0);
                }
                else if(horizontalFocusSwitching) {
                    return Result.MOVE_FOCUS_RIGHT;
                }
                return Result.HANDLED;
            case ArrowUp:
                if(caretPosition.getRow() > 0) {
                    caretPosition = caretPosition.withRelativeRow(-1);
                    line = lines.get(caretPosition.getRow());
                    if(caretPosition.getColumn() > line.length()) {
                        caretPosition = caretPosition.withColumn(line.length());
                    }
                }
                else if(verticalFocusSwitching) {
                    return Result.MOVE_FOCUS_UP;
                }
                return Result.HANDLED;
            case ArrowDown:
                if(caretPosition.getRow() < lines.size() - 1) {
                    caretPosition = caretPosition.withRelativeRow(1);
                    line = lines.get(caretPosition.getRow());
                    if(caretPosition.getColumn() > line.length()) {
                        caretPosition = caretPosition.withColumn(line.length());
                    }
                }
                else if(verticalFocusSwitching) {
                    return Result.MOVE_FOCUS_DOWN;
                }
                return Result.HANDLED;
            case End:
                caretPosition = caretPosition.withColumn(line.length());
                return Result.HANDLED;
            case Enter:
                if(style == Style.SINGLE_LINE) {
                    return Result.MOVE_FOCUS_NEXT;
                }
                String newLine = line.substring(caretPosition.getColumn());
                lines.set(caretPosition.getRow(), line.substring(0, caretPosition.getColumn()));
                lines.add(caretPosition.getRow() + 1, newLine);
                caretPosition = caretPosition.withColumn(0).withRelativeRow(1);
                return Result.HANDLED;
            case Home:
                caretPosition = caretPosition.withColumn(0);
                return Result.HANDLED;
            case PageDown:
                caretPosition = caretPosition.withRelativeRow(getSize().getRows());
                if(caretPosition.getRow() > lines.size() - 1) {
                    caretPosition = caretPosition.withRow(lines.size() - 1);
                }
                return Result.HANDLED;
            case PageUp:
                caretPosition = caretPosition.withRelativeRow(-getSize().getRows());
                if(caretPosition.getRow() < 0) {
                    caretPosition = caretPosition.withRow(0);
                }
                return Result.HANDLED;
        }
        return super.handleKeyStroke(keyStroke);
    }

    private Result handleKeyStrokeReadOnly(KeyStroke keyStroke) {
        switch (keyStroke.getKeyType()) {
            case ArrowLeft:
                if(getRenderer().getViewTopLeft().getColumn() == 0 && horizontalFocusSwitching) {
                    return Result.MOVE_FOCUS_LEFT;
                }
                getRenderer().setViewTopLeft(getRenderer().getViewTopLeft().withRelativeColumn(-1));
                return Result.HANDLED;
            case ArrowRight:
                if(getRenderer().getViewTopLeft().getColumn() + getSize().getColumns() == longestRow && horizontalFocusSwitching) {
                    return Result.MOVE_FOCUS_RIGHT;
                }
                getRenderer().setViewTopLeft(getRenderer().getViewTopLeft().withRelativeColumn(1));
                return Result.HANDLED;
            case ArrowUp:
                if(getRenderer().getViewTopLeft().getRow() == 0 && verticalFocusSwitching) {
                    return Result.MOVE_FOCUS_UP;
                }
                getRenderer().setViewTopLeft(getRenderer().getViewTopLeft().withRelativeRow(-1));
                return Result.HANDLED;
            case ArrowDown:
                if(getRenderer().getViewTopLeft().getRow() + getSize().getRows() == lines.size() && verticalFocusSwitching) {
                    return Result.MOVE_FOCUS_DOWN;
                }
                getRenderer().setViewTopLeft(getRenderer().getViewTopLeft().withRelativeRow(1));
                return Result.HANDLED;
            case Home:
                getRenderer().setViewTopLeft(TerminalPosition.TOP_LEFT_CORNER);
                return Result.HANDLED;
            case End:
                getRenderer().setViewTopLeft(TerminalPosition.TOP_LEFT_CORNER.withRow(getLineCount() - getSize().getRows()));
                return Result.HANDLED;
            case PageDown:
                getRenderer().setViewTopLeft(getRenderer().getViewTopLeft().withRelativeRow(getSize().getRows()));
                return Result.HANDLED;
            case PageUp:
                getRenderer().setViewTopLeft(getRenderer().getViewTopLeft().withRelativeRow(-getSize().getRows()));
                return Result.HANDLED;
        }
        return super.handleKeyStroke(keyStroke);
    }

    public static abstract class TextBoxRenderer implements InteractableRenderer<TextBox> {
        protected abstract TerminalPosition getViewTopLeft();
        protected abstract void setViewTopLeft(TerminalPosition position);
    }

    public static class DefaultTextBoxRenderer extends TextBoxRenderer {
        private TerminalPosition viewTopLeft;

        public DefaultTextBoxRenderer() {
            viewTopLeft = TerminalPosition.TOP_LEFT_CORNER;
        }

        @Override
        protected TerminalPosition getViewTopLeft() {
            return viewTopLeft;
        }

        @Override
        protected void setViewTopLeft(TerminalPosition position) {
            if(position.getColumn() < 0) {
                position = position.withColumn(0);
            }
            if(position.getRow() < 0) {
                position = position.withRow(0);
            }
            viewTopLeft = position;
        }

        @Override
        public TerminalPosition getCursorLocation(TextBox component) {
            if(component.isReadOnly()) {
                return null;
            }
            return component
                    .getCaretPosition()
                    .withRelativeColumn(-viewTopLeft.getColumn())
                    .withRelativeRow(-viewTopLeft.getRow());
        }

        @Override
        public TerminalSize getPreferredSize(TextBox component) {
            return new TerminalSize(component.longestRow, component.lines.size());
        }

        @Override
        public void drawComponent(TextGUIGraphics graphics, TextBox component) {
            if(viewTopLeft.getColumn() + graphics.getSize().getColumns() > component.longestRow) {
                viewTopLeft = viewTopLeft.withColumn(component.longestRow - graphics.getSize().getColumns());
                if(viewTopLeft.getColumn() < 0) {
                    viewTopLeft = viewTopLeft.withColumn(0);
                }
            }
            if(viewTopLeft.getRow() + graphics.getSize().getRows() > component.getLineCount()) {
                viewTopLeft = viewTopLeft.withRow(component.getLineCount() - graphics.getSize().getRows());
                if(viewTopLeft.getRow() < 0) {
                    viewTopLeft = viewTopLeft.withRow(0);
                }
            }
            if (component.isFocused()) {
                graphics.applyThemeStyle(graphics.getThemeDefinition(TextBox.class).getActive());
            }
            else {
                graphics.applyThemeStyle(graphics.getThemeDefinition(TextBox.class).getNormal());
            }
            graphics.fill(component.unusedSpaceCharacter);

            if(!component.isReadOnly()) {
                //Adjust the view if necessary
                if (component.caretPosition.getColumn() < viewTopLeft.getColumn()) {
                    viewTopLeft = viewTopLeft.withColumn(component.caretPosition.getColumn());
                }
                else if (component.caretPosition.getColumn() >= graphics.getSize().getColumns() + viewTopLeft.getColumn()) {
                    viewTopLeft = viewTopLeft.withColumn(component.caretPosition.getColumn() - graphics.getSize().getColumns() + 1);
                }
                if (component.caretPosition.getRow() < viewTopLeft.getRow()) {
                    viewTopLeft = viewTopLeft.withRow(component.getCaretPosition().getRow());
                }
                else if (component.caretPosition.getRow() >= graphics.getSize().getRows() + viewTopLeft.getRow()) {
                    viewTopLeft = viewTopLeft.withRow(component.caretPosition.getRow() - graphics.getSize().getRows() + 1);
                }
            }

            for (int row = 0; row < graphics.getSize().getRows(); row++) {
                int rowIndex = row + viewTopLeft.getRow();
                if(rowIndex >= component.lines.size()) {
                    continue;
                }
                String line = component.lines.get(rowIndex);
                if(line.length() > viewTopLeft.getColumn()) {
                    String string = line.substring(viewTopLeft.getColumn());
                    if(component.mask != null) {
                        string = string.replaceAll(".", component.mask + "");
                    }
                    graphics.putString(0, row, string);
                }
            }
        }
    }
}
