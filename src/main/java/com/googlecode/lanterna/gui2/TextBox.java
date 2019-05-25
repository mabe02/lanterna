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
 * Copyright (C) 2010-2019 Martin Berglund
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This component keeps a text content that is editable by the user. A TextBox can be single line or multiline and lets
 * the user navigate the cursor in the text area by using the arrow keys, page up, page down, home and end. For
 * multi-line {@code TextBox}:es, scrollbars will be automatically displayed if needed.
 * <p>
 * Size-wise, a {@code TextBox} should be hard-coded to a particular size, it's not good at guessing how large it should
 * be. You can do this through the constructor.
 */
public class TextBox extends AbstractInteractableComponent<TextBox> {

    /**
     * Enum value to force a {@code TextBox} to be either single line or multi line. This is usually auto-detected if
     * the text box has some initial content by scanning that content for \n characters.
     */
    public enum Style {
        /**
         * The {@code TextBox} contains a single line of text and is typically drawn on one row
         */
        SINGLE_LINE,
        /**
         * The {@code TextBox} contains a none, one or many lines of text and is normally drawn over multiple lines
         */
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
    private final int maxLineLength;
    private int longestRow;
    private Character mask;
    private Pattern validationPattern;

    /**
     * Default constructor, this creates a single-line {@code TextBox} of size 10 which is initially empty
     */
    public TextBox() {
        this(new TerminalSize(10, 1), "", Style.SINGLE_LINE);
    }

    /**
     * Constructor that creates a {@code TextBox} with an initial content and attempting to be big enough to display
     * the whole text at once without scrollbars
     * @param initialContent Initial content of the {@code TextBox}
     */
    public TextBox(String initialContent) {
        this(null, initialContent, initialContent.contains("\n") ? Style.MULTI_LINE : Style.SINGLE_LINE);
    }

    /**
     * Creates a {@code TextBox} that has an initial content and attempting to be big enough to display the whole text
     * at once without scrollbars.
     *
     * @param initialContent Initial content of the {@code TextBox}
     * @param style Forced style instead of auto-detecting
     */
    public TextBox(String initialContent, Style style) {
        this(null, initialContent, style);
    }

    /**
     * Creates a new empty {@code TextBox} with a specific size
     * @param preferredSize Size of the {@code TextBox}
     */
    public TextBox(TerminalSize preferredSize) {
        this(preferredSize, (preferredSize != null && preferredSize.getRows() > 1) ? Style.MULTI_LINE : Style.SINGLE_LINE);
    }

    /**
     * Creates a new empty {@code TextBox} with a specific size and style
     * @param preferredSize Size of the {@code TextBox}
     * @param style Style to use
     */
    public TextBox(TerminalSize preferredSize, Style style) {
        this(preferredSize, "", style);
    }

    /**
     * Creates a new empty {@code TextBox} with a specific size and initial content
     * @param preferredSize Size of the {@code TextBox}
     * @param initialContent Initial content of the {@code TextBox}
     */
    public TextBox(TerminalSize preferredSize, String initialContent) {
        this(preferredSize, initialContent, (preferredSize != null && preferredSize.getRows() > 1) || initialContent.contains("\n") ? Style.MULTI_LINE : Style.SINGLE_LINE);
    }

    /**
     * Main constructor of the {@code TextBox} which decides size, initial content and style
     * @param preferredSize Size of the {@code TextBox}
     * @param initialContent Initial content of the {@code TextBox}
     * @param style Style to use for this {@code TextBox}, instead of auto-detecting
     */
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
        this.mask = null;
        this.validationPattern = null;
        setText(initialContent);

        // Re-adjust caret position
        this.caretPosition = TerminalPosition.TOP_LEFT_CORNER.withColumn(getLine(0).length());

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
    public synchronized TextBox setValidationPattern(Pattern validationPattern) {
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

    /**
     * Updates the text content of the {@code TextBox} to the supplied string.
     * @param text New text to assign to the {@code TextBox}
     * @return Itself
     */
    public synchronized TextBox setText(String text) {
        String[] split = text.split("\n");
        if (split.length == 0) {
            split = new String[] { "" };
        }
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

    @Override
    public TextBoxRenderer getRenderer() {
        return (TextBoxRenderer)super.getRenderer();
    }

    /**
     * Adds a single line to the {@code TextBox} at the end, this only works when in multi-line mode
     * @param line Line to add at the end of the content in this {@code TextBox}
     * @return Itself
     */
    public synchronized TextBox addLine(String line) {
        StringBuilder bob = new StringBuilder();
        for(int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if(c == '\n' && style == Style.MULTI_LINE) {
                String string = bob.toString();
                int lineWidth = TerminalTextUtils.getColumnWidth(string);
                lines.add(string);
                if(longestRow < lineWidth + 1) {
                    longestRow = lineWidth + 1;
                }
                addLine(line.substring(i + 1));
                return this;
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
        int lineWidth = TerminalTextUtils.getColumnWidth(string);
        lines.add(string);
        if(longestRow < lineWidth + 1) {
            longestRow = lineWidth + 1;
        }
        invalidate();
        return this;
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

    /**
     * Returns the position of the caret, as a {@code TerminalPosition} where the row and columns equals the coordinates
     * in a multi-line {@code TextBox} and for single-line {@code TextBox} you can ignore the {@code row} component.
     * @return Position of the text input caret
     */
    public TerminalPosition getCaretPosition() {
        return caretPosition;
    }

    /**
     * Moves the text caret position horizontally to a new position in the {@link TextBox}. For multi-line
     * {@link TextBox}:es, this will move the cursor within the current line. If the position is out of bounds, it is
     * automatically set back into range.
     * @param column Position, in characters, within the {@link TextBox} (on the current line for multi-line
     * {@link TextBox}:es) to where the text cursor should be moved
     * @return Itself
     */
    public synchronized TextBox setCaretPosition(int column) {
        return setCaretPosition(getCaretPosition().getRow(), column);
    }

    /**
     * Moves the text caret position to a new position in the {@link TextBox}. For single-line {@link TextBox}:es, the
     * line component is not used. If one of the positions are out of bounds, it is automatically set back into range.
     * @param line Which line inside the {@link TextBox} to move the caret to (0 being the first line), ignored if the
     *             {@link TextBox} is single-line
     * @param column  What column on the specified line to move the text caret to (0 being the first column)
     * @return Itself
     */
    public synchronized TextBox setCaretPosition(int line, int column) {
        if(line < 0) {
            line = 0;
        }
        else if(line >= lines.size()) {
            line = lines.size() - 1;
        }
        if(column < 0) {
            column = 0;
        }
        else if(column > lines.get(line).length()) {
            column = lines.get(line).length();
        }
        caretPosition = caretPosition.withRow(line).withColumn(column);
        return this;
    }

    /**
     * Returns the text in this {@code TextBox}, for multi-line mode all lines will be concatenated together with \n as
     * separator.
     * @return The text inside this {@code TextBox}
     */
    public synchronized String getText() {
        StringBuilder bob = new StringBuilder(lines.get(0));
        for(int i = 1; i < lines.size(); i++) {
            bob.append("\n").append(lines.get(i));
        }
        return bob.toString();
    }

    /**
     * Helper method, it will return the content of the {@code TextBox} unless it's empty in which case it will return
     * the supplied default value
     * @param defaultValueIfEmpty Value to return if the {@code TextBox} is empty
     * @return Text in the {@code TextBox} or {@code defaultValueIfEmpty} is the {@code TextBox} is empty
     */
    public String getTextOrDefault(String defaultValueIfEmpty) {
        String text = getText();
        if(text.isEmpty()) {
            return defaultValueIfEmpty;
        }
        return text;
    }

    /**
     * Returns the current text mask, meaning the substitute to draw instead of the text inside the {@code TextBox}.
     * This is normally used for password input fields so the password isn't shown
     * @return Current text mask or {@code null} if there is no mask
     */
    public Character getMask() {
        return mask;
    }

    /**
     * Sets the current text mask, meaning the substitute to draw instead of the text inside the {@code TextBox}.
     * This is normally used for password input fields so the password isn't shown
     * @param mask New text mask or {@code null} if there is no mask
     * @return Itself
     */
    public TextBox setMask(Character mask) {
        if(mask != null && TerminalTextUtils.isCharCJK(mask)) {
            throw new IllegalArgumentException("Cannot use a CJK character as a mask");
        }
        this.mask = mask;
        invalidate();
        return this;
    }

    /**
     * Returns {@code true} if this {@code TextBox} is in read-only mode, meaning text input from the user through the
     * keyboard is prevented
     * @return {@code true} if this {@code TextBox} is in read-only mode
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Sets the read-only mode of the {@code TextBox}, meaning text input from the user through the keyboard is
     * prevented. The user can still focus and scroll through the text in this mode.
     * @param readOnly If {@code true} then the {@code TextBox} will switch to read-only mode
     * @return Itself
     */
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
     * @param index Index of the row to return the contents from
     * @return The line at the specified index, as a String
     * @throws IndexOutOfBoundsException if the row index is less than zero or too large
     */
    public synchronized String getLine(int index) {
        return lines.get(index);
    }

    /**
     * Returns the number of lines currently in this TextBox. For single-line TextBox:es, this will always return 1.
     * @return Number of lines of text currently in this TextBox
     */
    public synchronized int getLineCount() {
        return lines.size();
    }

    @Override
    protected TextBoxRenderer createDefaultRenderer() {
        return new DefaultTextBoxRenderer();
    }

    @Override
    public synchronized Result handleKeyStroke(KeyStroke keyStroke) {
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
                    int trueColumnPosition = TerminalTextUtils.getColumnIndex(lines.get(caretPosition.getRow()), caretPosition.getColumn());
                    caretPosition = caretPosition.withRelativeRow(-1);
                    line = lines.get(caretPosition.getRow());
                    if(trueColumnPosition > TerminalTextUtils.getColumnWidth(line)) {
                        caretPosition = caretPosition.withColumn(line.length());
                    }
                    else {
                        caretPosition = caretPosition.withColumn(TerminalTextUtils.getStringCharacterIndex(line, trueColumnPosition));
                    }
                }
                else if(verticalFocusSwitching) {
                    return Result.MOVE_FOCUS_UP;
                }
                return Result.HANDLED;
            case ArrowDown:
                if(caretPosition.getRow() < lines.size() - 1) {
                    int trueColumnPosition = TerminalTextUtils.getColumnIndex(lines.get(caretPosition.getRow()), caretPosition.getColumn());
                    caretPosition = caretPosition.withRelativeRow(1);
                    line = lines.get(caretPosition.getRow());
                    if(trueColumnPosition > TerminalTextUtils.getColumnWidth(line)) {
                        caretPosition = caretPosition.withColumn(line.length());
                    }
                    else {
                        caretPosition = caretPosition.withColumn(TerminalTextUtils.getStringCharacterIndex(line, trueColumnPosition));
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
                if(lines.get(caretPosition.getRow()).length() < caretPosition.getColumn()) {
                    caretPosition = caretPosition.withColumn(lines.get(caretPosition.getRow()).length());
                }
                return Result.HANDLED;
            case PageUp:
                caretPosition = caretPosition.withRelativeRow(-getSize().getRows());
                if(caretPosition.getRow() < 0) {
                    caretPosition = caretPosition.withRow(0);
                }
                if(lines.get(caretPosition.getRow()).length() < caretPosition.getColumn()) {
                    caretPosition = caretPosition.withColumn(lines.get(caretPosition.getRow()).length());
                }
                return Result.HANDLED;
            default:
        }
        return super.handleKeyStroke(keyStroke);
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

    /**
     * Helper interface that doesn't add any new methods but makes coding new text box renderers a little bit more clear
     */
    public interface TextBoxRenderer extends InteractableRenderer<TextBox> {
        TerminalPosition getViewTopLeft();
        void setViewTopLeft(TerminalPosition position);
    }

    /**
     * This is the default text box renderer that is used if you don't override anything. With this renderer, the text
     * box is filled with a solid background color and the text is drawn on top of it. Scrollbars are added for
     * multi-line text whenever the text inside the {@code TextBox} does not fit in the available area.
     */
    public static class DefaultTextBoxRenderer implements TextBoxRenderer {
        private TerminalPosition viewTopLeft;
        private final ScrollBar verticalScrollBar;
        private final ScrollBar horizontalScrollBar;
        private boolean hideScrollBars;
        private Character unusedSpaceCharacter;

        /**
         * Default constructor
         */
        public DefaultTextBoxRenderer() {
            viewTopLeft = TerminalPosition.TOP_LEFT_CORNER;
            verticalScrollBar = new ScrollBar(Direction.VERTICAL);
            horizontalScrollBar = new ScrollBar(Direction.HORIZONTAL);
            hideScrollBars = false;
            unusedSpaceCharacter = null;
        }

        /**
         * Sets the character to represent an empty untyped space in the text box. This will be an empty space by
         * default but you can override it to anything that isn't double-width.
         * @param unusedSpaceCharacter Character to draw in unused space of the {@link TextBox}
         * @throws IllegalArgumentException If unusedSpaceCharacter is a double-width character
         */
        public void setUnusedSpaceCharacter(char unusedSpaceCharacter) {
            if(TerminalTextUtils.isCharDoubleWidth(unusedSpaceCharacter)) {
                throw new IllegalArgumentException("Cannot use a double-width character as the unused space character in a TextBox");
            }
            this.unusedSpaceCharacter = unusedSpaceCharacter;
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

            //Adjust caret position if necessary
            TerminalPosition caretPosition = component.getCaretPosition();
            String line = component.getLine(caretPosition.getRow());
            caretPosition = caretPosition.withColumn(Math.min(caretPosition.getColumn(), line.length()));

            return caretPosition
                    .withColumn(TerminalTextUtils.getColumnIndex(line, caretPosition.getColumn()))
                    .withRelativeColumn(-viewTopLeft.getColumn())
                    .withRelativeRow(-viewTopLeft.getRow());
        }

        @Override
        public TerminalSize getPreferredSize(TextBox component) {
            return new TerminalSize(component.longestRow, component.lines.size());
        }

        /**
         * Controls whether scrollbars should be visible or not when a multi-line {@code TextBox} has more content than
         * it can draw in the area it was assigned (default: false)
         * @param hideScrollBars If {@code true}, don't show scrollbars if the multi-line content is bigger than the
         *                       area
         */
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
                if(textBoxLineCount > realTextArea.getRows() && !drawVerticalScrollBar) {
                    realTextArea = realTextArea.withRelativeColumns(-1);
                    drawVerticalScrollBar = true;
                }
            }

            drawTextArea(graphics.newTextGraphics(TerminalPosition.TOP_LEFT_CORNER, realTextArea), component);

            //Draw scrollbars, if any
            if(drawVerticalScrollBar) {
                verticalScrollBar.onAdded(component.getParent());
                verticalScrollBar.setViewSize(realTextArea.getRows());
                verticalScrollBar.setScrollMaximum(textBoxLineCount);
                verticalScrollBar.setScrollPosition(viewTopLeft.getRow());
                verticalScrollBar.draw(graphics.newTextGraphics(
                        new TerminalPosition(graphics.getSize().getColumns() - 1, 0),
                        new TerminalSize(1, graphics.getSize().getRows() - (drawHorizontalScrollBar ? 1 : 0))));
            }
            if(drawHorizontalScrollBar) {
                horizontalScrollBar.onAdded(component.getParent());
                horizontalScrollBar.setViewSize(realTextArea.getColumns());
                horizontalScrollBar.setScrollMaximum(component.longestRow - 1);
                horizontalScrollBar.setScrollPosition(viewTopLeft.getColumn());
                horizontalScrollBar.draw(graphics.newTextGraphics(
                        new TerminalPosition(0, graphics.getSize().getRows() - 1),
                        new TerminalSize(graphics.getSize().getColumns() - (drawVerticalScrollBar ? 1 : 0), 1)));
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
            ThemeDefinition themeDefinition = component.getThemeDefinition();
            if (component.isFocused()) {
                if(component.isReadOnly()) {
                    graphics.applyThemeStyle(themeDefinition.getSelected());
                }
                else {
                    graphics.applyThemeStyle(themeDefinition.getActive());
                }
            }
            else {
                if(component.isReadOnly()) {
                    graphics.applyThemeStyle(themeDefinition.getInsensitive());
                }
                else {
                    graphics.applyThemeStyle(themeDefinition.getNormal());
                }
            }

            Character fillCharacter = unusedSpaceCharacter;
            if(fillCharacter == null) {
                fillCharacter = themeDefinition.getCharacter("FILL", ' ');
            }
            graphics.fill(fillCharacter);

            if(!component.isReadOnly()) {
                //Adjust caret position if necessary
                TerminalPosition caretPosition = component.getCaretPosition();
                String caretLine = component.getLine(caretPosition.getRow());
                caretPosition = caretPosition.withColumn(Math.min(caretPosition.getColumn(), caretLine.length()));

                //Adjust the view if necessary
                int trueColumnPosition = TerminalTextUtils.getColumnIndex(caretLine, caretPosition.getColumn());
                if (trueColumnPosition < viewTopLeft.getColumn()) {
                    viewTopLeft = viewTopLeft.withColumn(trueColumnPosition);
                }
                else if (trueColumnPosition >= textAreaSize.getColumns() + viewTopLeft.getColumn()) {
                    viewTopLeft = viewTopLeft.withColumn(trueColumnPosition - textAreaSize.getColumns() + 1);
                }
                if (caretPosition.getRow() < viewTopLeft.getRow()) {
                    viewTopLeft = viewTopLeft.withRow(caretPosition.getRow());
                }
                else if (caretPosition.getRow() >= textAreaSize.getRows() + viewTopLeft.getRow()) {
                    viewTopLeft = viewTopLeft.withRow(caretPosition.getRow() - textAreaSize.getRows() + 1);
                }

                //Additional corner-case for CJK characters
                if(trueColumnPosition - viewTopLeft.getColumn() == graphics.getSize().getColumns() - 1) {
                    if(caretLine.length() > caretPosition.getColumn() &&
                            TerminalTextUtils.isCharCJK(caretLine.charAt(caretPosition.getColumn()))) {
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
                if(component.getMask() != null) {
                    StringBuilder builder = new StringBuilder();
                    for(int i = 0; i < line.length(); i++) {
                        builder.append(component.getMask());
                    }
                    line = builder.toString();
                }
                graphics.putString(0, row, TerminalTextUtils.fitString(line, viewTopLeft.getColumn(), textAreaSize.getColumns()));
            }
        }
    }
}
