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
public class TextBox extends AbstractInteractableComponent<TextBox.TextBoxRenderer> {

    public static enum Style {
        SINGLE_LINE,
        MULTILINE,
        ;
    }

    private final List<String> lines;
    private final Style style;

    private TerminalPosition caretPosition;
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
                else {
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
                else {
                    return Result.MOVE_FOCUS_RIGHT;
                }
                return Result.HANDLED;
            case ArrowUp:
                if(style == Style.SINGLE_LINE) {
                    return Result.MOVE_FOCUS_UP;
                }
                if(caretPosition.getRow() > 0) {
                    caretPosition = caretPosition.withRelativeRow(-1);
                    line = lines.get(caretPosition.getRow());
                    if(caretPosition.getColumn() > line.length()) {
                        caretPosition = caretPosition.withColumn(line.length());
                    }
                }
                return Result.HANDLED;
            case ArrowDown:
                if(style == Style.SINGLE_LINE) {
                    return Result.MOVE_FOCUS_DOWN;
                }
                if(caretPosition.getRow() < lines.size() - 1) {
                    caretPosition = caretPosition.withRelativeRow(1);
                }
                line = lines.get(caretPosition.getRow());
                if(caretPosition.getColumn() > line.length()) {
                    caretPosition = caretPosition.withColumn(line.length());
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

    public static abstract class TextBoxRenderer implements InteractableRenderer<TextBox> {
    }

    public static class DefaultTextBoxRenderer extends TextBoxRenderer {
        private TerminalPosition viewTopLeft;

        public DefaultTextBoxRenderer() {
            viewTopLeft = TerminalPosition.TOP_LEFT_CORNER;
        }

        @Override
        public TerminalPosition getCursorLocation(TextBox component) {
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
            if (component.isFocused()) {
                graphics.applyThemeStyle(graphics.getThemeDefinition(TextBox.class).getActive());
            }
            else {
                graphics.applyThemeStyle(graphics.getThemeDefinition(TextBox.class).getNormal());
            }
            graphics.fill(component.unusedSpaceCharacter);

            //Adjust the view if necessary
            if(component.caretPosition.getColumn() < viewTopLeft.getColumn()) {
                viewTopLeft = viewTopLeft.withColumn(component.caretPosition.getColumn());
            }
            else if(component.caretPosition.getColumn() >= graphics.getSize().getColumns() + viewTopLeft.getColumn()) {
                viewTopLeft = viewTopLeft.withColumn(component.caretPosition.getColumn() - graphics.getSize().getColumns() + 1);
            }
            if(component.caretPosition.getRow() < viewTopLeft.getRow()) {
                viewTopLeft = viewTopLeft.withRow(component.getCaretPosition().getRow());
            }
            else if(component.caretPosition.getRow() >= graphics.getSize().getRows() + viewTopLeft.getRow()) {
                viewTopLeft = viewTopLeft.withRow(component.caretPosition.getRow() - graphics.getSize().getRows() + 1);
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
