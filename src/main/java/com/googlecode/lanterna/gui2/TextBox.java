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
 * Copyright (C) 2010-2015 Martin
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.CJKUtils;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This component keeps a text content that is editable by the user. A TextBox can be single line or multiline and lets
 * the user navigate the cursor in the text area by using the arrow keys.
 */
public class TextBox extends AbstractInteractableComponent<TextBox> {

    public enum Style {
        SINGLE_LINE,
        MULTI_LINE,
        ;
    }

    private final List<String> lines;
    private final Style style;

    private TerminalPosition caretPosition;
    private boolean caretWarp;
    private boolean readOnly;
    private boolean horizontalFocusSwitching;
    private boolean verticalFocusSwitching;
    private int maxLineLength;
    private int longestRow;
    private char unusedSpaceCharacter;
    private Character mask;
    private Pattern validationPattern;

    public TextBox() {
        this(new TerminalSize(10, 1), "", Style.SINGLE_LINE);
    }

    public TextBox(String initialContent) {
        this(null, initialContent, initialContent.contains("\n") ? Style.MULTI_LINE : Style.SINGLE_LINE);
    }

    public TextBox(String initialContent, Style style) {
        this(null, initialContent, style);
    }

    public TextBox(TerminalSize preferredSize) {
        this(preferredSize, (preferredSize != null && preferredSize.getRows() > 1) ? Style.MULTI_LINE : Style.SINGLE_LINE);
    }

    public TextBox(TerminalSize preferredSize, Style style) {
        this(preferredSize, "", style);
    }

    public TextBox(TerminalSize preferredSize, String initialContent) {
        this(preferredSize, initialContent, (preferredSize != null && preferredSize.getRows() > 1) || initialContent.contains("\n") ? Style.MULTI_LINE : Style.SINGLE_LINE);
    }

    public TextBox(TerminalSize preferredSize, String initialContent, Style style) {
        this.lines = new ArrayList<String>();
        this.style = style;
        this.readOnly = false;
        this.caretWarp = false;
        this.verticalFocusSwitching = true;
        this.horizontalFocusSwitching = (style == Style.SINGLE_LINE);
        this.caretPosition = TerminalPosition.TOP_LEFT_CORNER;
        this.maxLineLength = -1;
        this.longestRow = 1;    //To fit the cursor
        this.unusedSpaceCharacter = ' ';
        this.mask = null;
        this.validationPattern = null;
        setText(initialContent);
        if (preferredSize == null) {
            preferredSize = new TerminalSize(Math.max(10, longestRow), lines.size());
        }
        setPreferredSize(preferredSize);
    }

    /**
     * Sets a pattern on which the content of the text box is to be validated. For multi-line TextBox:s, the pattern is
     * checked against each line individually, not the content as a whole. Partial matchings will not be allowed, the
     * whole pattern must match, however, empty lines will always be allowed. When the user tried to modify the content
     * of the TextBox in a way that does not match the pattern, the operation will be silently ignored. If you set this
     * pattern to {@code null}, all validation is turned off.
     * @param validationPattern Pattern to validate the lines in this TextBox against, or {@code null} to disable
     * @return itself
     */
    public TextBox setValidationPattern(Pattern validationPattern) {
        if(validationPattern != null) {
            for(String line: lines) {
                if(!validated(line)) {
                    throw new IllegalStateException("TextBox validation pattern " + validationPattern + " does not match existing content");
                }
            }
        }
        this.validationPattern = validationPattern;
        return this;
    }

    public TextBox setText(String text) {
        synchronized(this) {
            String[] split = text.split("\n");
            lines.clear();
            longestRow = 1;
            for(String line : split) {
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
    }

    @Override
    public TextBoxRenderer getRenderer() {
        return (TextBoxRenderer)super.getRenderer();
    }

    public void addLine(String line) {
        synchronized(this) {
            StringBuilder bob = new StringBuilder();
            for(int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                if(c == '\n' && style == Style.MULTI_LINE) {
                    String string = bob.toString();
                    int lineWidth = CJKUtils.getColumnWidth(string);
                    lines.add(string);
                    if(longestRow < lineWidth + 1) {
                        longestRow = lineWidth + 1;
                    }
                    addLine(line.substring(i + 1));
                    return;
                }
                else if(Character.isISOControl(c)) {
                    continue;
                }

                bob.append(c);
            }
            String string = bob.toString();
            if(!validated(string)) {
                throw new IllegalStateException("TextBox validation pattern " + validationPattern + " does not match the supplied text");
            }
            int lineWidth = CJKUtils.getColumnWidth(string);
            lines.add(string);
            if(longestRow < lineWidth + 1) {
                longestRow = lineWidth + 1;
            }
            invalidate();
        }
    }

    /**
     * Sets if the caret should jump to the beginning of the next line if right arrow is pressed while at the end of a
     * line. Similarly, pressing left arrow at the beginning of a line will make the caret jump to the end of the
     * previous line. This only makes sense for multi-line TextBox:es; for single-line ones it has no effect. By default
     * this is {@code false}.
     * @param caretWarp Whether the caret will warp at the beginning/end of lines
     * @return Itself
     */
    public TextBox setCaretWarp(boolean caretWarp) {
        this.caretWarp = caretWarp;
        return this;
    }

    /**
     * Checks whether caret warp mode is enabled or not. See {@code setCaretWarp} for more details.
     * @return {@code true} if caret warp mode is enabled
     */
    public boolean isCaretWarp() {
        return caretWarp;
    }

    public TerminalPosition getCaretPosition() {
        return caretPosition;
    }

    public String getText() {
        synchronized(this) {
            StringBuilder bob = new StringBuilder(lines.get(0));
            for(int i = 1; i < lines.size(); i++) {
                bob.append("\n").append(lines.get(i));
            }
            return bob.toString();
        }
    }

    public String getTextOrDefault(String defaultValueIfEmpty) {
        String text = getText();
        if(text.isEmpty()) {
            return defaultValueIfEmpty;
        }
        return text;
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

    /**
     * Returns the line on the specific row. For non-multiline TextBox:es, calling this with index set to 0 will return
     * the same as calling {@code getText()}. If the row index is invalid (less than zero or equals or larger than the
     * number of rows), this method will throw IndexOutOfBoundsException.
     * @param index
     * @return The line at the specified index, as a String
     * @throws IndexOutOfBoundsException if the row index is less than zero or too large
     */
    public String getLine(int index) {
        synchronized(this) {
            return lines.get(index);
        }
    }

    /**
     * Returns the number of lines currently in this TextBox. For single-line TextBox:es, this will always return 1.
     * @return Number of lines of text currently in this TextBox
     */
    public int getLineCount() {
        synchronized(this) {
            return lines.size();
        }
    }

    @Override
    protected TextBoxRenderer createDefaultRenderer() {
        return new DefaultTextBoxRenderer();
    }

    @Override
    public Result handleKeyStroke(KeyStroke keyStroke) {
        synchronized(this) {
            if(readOnly) {
                return handleKeyStrokeReadOnly(keyStroke);
            }
            String line = lines.get(caretPosition.getRow());
            switch(keyStroke.getKeyType()) {
                case Character:
                    if(maxLineLength == -1 || maxLineLength > line.length() + 1) {
                        line = line.substring(0, caretPosition.getColumn()) + keyStroke.getCharacter() + line.substring(caretPosition.getColumn());
                        if(validated(line)) {
                            lines.set(caretPosition.getRow(), line);
                            caretPosition = caretPosition.withRelativeColumn(1);
                        }
                    }
                    return Result.HANDLED;
                case Backspace:
                    if(caretPosition.getColumn() > 0) {
                        line = line.substring(0, caretPosition.getColumn() - 1) + line.substring(caretPosition.getColumn());
                        if(validated(line)) {
                            lines.set(caretPosition.getRow(), line);
                            caretPosition = caretPosition.withRelativeColumn(-1);
                        }
                    }
                    else if(style == Style.MULTI_LINE && caretPosition.getRow() > 0) {
                        String concatenatedLines = lines.get(caretPosition.getRow() - 1) + line;
                        if(validated(concatenatedLines)) {
                            lines.remove(caretPosition.getRow());
                            caretPosition = caretPosition.withRelativeRow(-1);
                            caretPosition = caretPosition.withColumn(lines.get(caretPosition.getRow()).length());
                            lines.set(caretPosition.getRow(), concatenatedLines);
                        }
                    }
                    return Result.HANDLED;
                case Delete:
                    if(caretPosition.getColumn() < line.length()) {
                        line = line.substring(0, caretPosition.getColumn()) + line.substring(caretPosition.getColumn() + 1);
                        if(validated(line)) {
                            lines.set(caretPosition.getRow(), line);
                        }
                    }
                    else if(style == Style.MULTI_LINE && caretPosition.getRow() < lines.size() - 1) {
                        String concatenatedLines = line + lines.get(caretPosition.getRow() + 1);
                        if(validated(concatenatedLines)) {
                            lines.set(caretPosition.getRow(), concatenatedLines);
                            lines.remove(caretPosition.getRow() + 1);
                        }
                    }
                    return Result.HANDLED;
                case ArrowLeft:
                    if(caretPosition.getColumn() > 0) {
                        caretPosition = caretPosition.withRelativeColumn(-1);
                    }
                    else if(style == Style.MULTI_LINE && caretWarp && caretPosition.getRow() > 0) {
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
                    else if(style == Style.MULTI_LINE && caretWarp && caretPosition.getRow() < lines.size() - 1) {
                        caretPosition = caretPosition.withRelativeRow(1);
                        caretPosition = caretPosition.withColumn(0);
                    }
                    else if(horizontalFocusSwitching) {
                        return Result.MOVE_FOCUS_RIGHT;
                    }
                    return Result.HANDLED;
                case ArrowUp:
                    if(caretPosition.getRow() > 0) {
                        int trueColumnPosition = CJKUtils.getColumnIndex(lines.get(caretPosition.getRow()), caretPosition.getColumn());
                        caretPosition = caretPosition.withRelativeRow(-1);
                        line = lines.get(caretPosition.getRow());
                        if(trueColumnPosition > CJKUtils.getColumnWidth(line)) {
                            caretPosition = caretPosition.withColumn(line.length());
                        }
                        else {
                            caretPosition = caretPosition.withColumn(CJKUtils.getStringCharacterIndex(line, trueColumnPosition));
                        }
                    }
                    else if(verticalFocusSwitching) {
                        return Result.MOVE_FOCUS_UP;
                    }
                    return Result.HANDLED;
                case ArrowDown:
                    if(caretPosition.getRow() < lines.size() - 1) {
                        int trueColumnPosition = CJKUtils.getColumnIndex(lines.get(caretPosition.getRow()), caretPosition.getColumn());
                        caretPosition = caretPosition.withRelativeRow(1);
                        line = lines.get(caretPosition.getRow());
                        if(trueColumnPosition > CJKUtils.getColumnWidth(line)) {
                            caretPosition = caretPosition.withColumn(line.length());
                        }
                        else {
                            caretPosition = caretPosition.withColumn(CJKUtils.getStringCharacterIndex(line, trueColumnPosition));
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
                    String oldLine = line.substring(0, caretPosition.getColumn());
                    if(validated(newLine) && validated(oldLine)) {
                        lines.set(caretPosition.getRow(), oldLine);
                        lines.add(caretPosition.getRow() + 1, newLine);
                        caretPosition = caretPosition.withColumn(0).withRelativeRow(1);
                    }
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
                default:
            }
            return super.handleKeyStroke(keyStroke);
        }
    }

    private boolean validated(String line) {
        return validationPattern == null || line.isEmpty() || validationPattern.matcher(line).matches();
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
            default:
        }
        return super.handleKeyStroke(keyStroke);
    }

    public static abstract class TextBoxRenderer implements InteractableRenderer<TextBox> {
        public abstract TerminalPosition getViewTopLeft();
        public abstract void setViewTopLeft(TerminalPosition position);
    }

    public static class DefaultTextBoxRenderer extends TextBoxRenderer {
        private TerminalPosition viewTopLeft;
        private ScrollBar verticalScrollBar;
        private ScrollBar horizontalScrollBar;
        private boolean hideScrollBars;

        public DefaultTextBoxRenderer() {
            viewTopLeft = TerminalPosition.TOP_LEFT_CORNER;
            verticalScrollBar = new ScrollBar(Direction.VERTICAL);
            horizontalScrollBar = new ScrollBar(Direction.HORIZONTAL);
            hideScrollBars = false;
        }

        @Override
        public TerminalPosition getViewTopLeft() {
            return viewTopLeft;
        }

        @Override
        public void setViewTopLeft(TerminalPosition position) {
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
            TerminalPosition caretPosition = component.getCaretPosition();
            String line = component.getLine(caretPosition.getRow());
            return caretPosition
                    .withColumn(CJKUtils.getColumnIndex(line, caretPosition.getColumn()))
                    .withRelativeColumn(-viewTopLeft.getColumn())
                    .withRelativeRow(-viewTopLeft.getRow());
        }

        @Override
        public TerminalSize getPreferredSize(TextBox component) {
            return new TerminalSize(component.longestRow, component.lines.size());
        }

        public void setHideScrollBars(boolean hideScrollBars) {
            this.hideScrollBars = hideScrollBars;
        }

        @Override
        public void drawComponent(TextGUIGraphics graphics, TextBox component) {
            TerminalSize realTextArea = graphics.getSize();
            if(realTextArea.getRows() == 0 || realTextArea.getColumns() == 0) {
                return;
            }
            boolean drawVerticalScrollBar = false;
            boolean drawHorizontalScrollBar = false;
            int textBoxLineCount = component.getLineCount();
            if(!hideScrollBars && textBoxLineCount > realTextArea.getRows() && realTextArea.getColumns() > 1) {
                realTextArea = realTextArea.withRelativeColumns(-1);
                drawVerticalScrollBar = true;
            }
            if(!hideScrollBars && component.longestRow > realTextArea.getColumns() && realTextArea.getRows() > 1) {
                realTextArea = realTextArea.withRelativeRows(-1);
                drawHorizontalScrollBar = true;
                if(textBoxLineCount > realTextArea.getRows() && realTextArea.getRows() == graphics.getSize().getRows()) {
                    realTextArea = realTextArea.withRelativeColumns(-1);
                    drawVerticalScrollBar = true;
                }
            }

            drawTextArea(graphics.newTextGraphics(TerminalPosition.TOP_LEFT_CORNER, realTextArea), component);

            //Draw scrollbars, if any
            if(drawVerticalScrollBar) {
                verticalScrollBar.setViewSize(realTextArea.getRows());
                verticalScrollBar.setScrollMaximum(textBoxLineCount);
                verticalScrollBar.setScrollPosition(viewTopLeft.getRow());
                verticalScrollBar.draw(graphics.newTextGraphics(
                        new TerminalPosition(graphics.getSize().getColumns() - 1, 0),
                        new TerminalSize(1, graphics.getSize().getRows() - 1)));
            }
            if(drawHorizontalScrollBar) {
                horizontalScrollBar.setViewSize(realTextArea.getColumns());
                horizontalScrollBar.setScrollMaximum(component.longestRow - 1);
                horizontalScrollBar.setScrollPosition(viewTopLeft.getColumn());
                horizontalScrollBar.draw(graphics.newTextGraphics(
                        new TerminalPosition(0, graphics.getSize().getRows() - 1),
                        new TerminalSize(graphics.getSize().getColumns() - 1, 1)));
            }
        }

        private void drawTextArea(TextGUIGraphics graphics, TextBox component) {
            TerminalSize textAreaSize = graphics.getSize();
            if(viewTopLeft.getColumn() + textAreaSize.getColumns() > component.longestRow) {
                viewTopLeft = viewTopLeft.withColumn(component.longestRow - textAreaSize.getColumns());
                if(viewTopLeft.getColumn() < 0) {
                    viewTopLeft = viewTopLeft.withColumn(0);
                }
            }
            if(viewTopLeft.getRow() + textAreaSize.getRows() > component.getLineCount()) {
                viewTopLeft = viewTopLeft.withRow(component.getLineCount() - textAreaSize.getRows());
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
                TerminalPosition caretPosition = component.getCaretPosition();
                String caretLine = component.getLine(caretPosition.getRow());
                int trueColumnPosition = CJKUtils.getColumnIndex(caretLine, caretPosition.getColumn());
                if (trueColumnPosition < viewTopLeft.getColumn()) {
                    viewTopLeft = viewTopLeft.withColumn(trueColumnPosition);
                }
                else if (trueColumnPosition >= textAreaSize.getColumns() + viewTopLeft.getColumn()) {
                    viewTopLeft = viewTopLeft.withColumn(trueColumnPosition - textAreaSize.getColumns() + 1);
                }
                if (caretPosition.getRow() < viewTopLeft.getRow()) {
                    viewTopLeft = viewTopLeft.withRow(component.getCaretPosition().getRow());
                }
                else if (caretPosition.getRow() >= textAreaSize.getRows() + viewTopLeft.getRow()) {
                    viewTopLeft = viewTopLeft.withRow(caretPosition.getRow() - textAreaSize.getRows() + 1);
                }

                //Additional corner-case for CJK characters
                if(trueColumnPosition - viewTopLeft.getColumn() == graphics.getSize().getColumns() - 1) {
                    if(caretLine.length() > caretPosition.getColumn() &&
                            CJKUtils.isCharCJK(caretLine.charAt(caretPosition.getColumn()))) {
                        viewTopLeft = viewTopLeft.withRelativeColumn(1);
                    }
                }
            }

            for (int row = 0; row < textAreaSize.getRows(); row++) {
                int rowIndex = row + viewTopLeft.getRow();
                if(rowIndex >= component.lines.size()) {
                    continue;
                }
                String line = component.lines.get(rowIndex);
                graphics.putString(0, row, CJKUtils.fitString(line, viewTopLeft.getColumn(), textAreaSize.getColumns()));
            }
        }
    }
}
