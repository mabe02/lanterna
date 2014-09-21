package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.CJKUtils;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.ThemeDefinition;

import java.util.EnumSet;

/**
 * Label is a simple read-only text display component that can show multi-line text in multiple colors
 * @author Martin
 */
public class Label extends AbstractComponent {
    private String[] lines;
    private TerminalSize preferredSize;
    private TextColor foregroundColor;
    private TextColor backgroundColor;
    private final EnumSet<SGR> additionalStyles;

    public Label(String text) {
        this.lines = null;
        this.preferredSize = TerminalSize.ZERO;
        this.foregroundColor = null;
        this.backgroundColor = null;
        this.additionalStyles = EnumSet.noneOf(SGR.class);
        setText(text);
    }

    protected void setLines(String[] lines) {
        this.lines = lines;
    }

    public void setText(String text) {
        setLines(splitIntoMultipleLines(text));
        this.preferredSize = getBounds(lines, preferredSize);
        invalidate();
    }

    protected String[] splitIntoMultipleLines(String text) {
        return text.replace("\r", "").split("\n");
    }

    protected TerminalSize getBounds(String[] lines, TerminalSize currentBounds) {
        if(currentBounds == null) {
            currentBounds = TerminalSize.ZERO;
        }
        currentBounds = currentBounds.withRows(lines.length);
        int preferredWidth = 0;
        for(String line: lines) {
            int lineWidth = CJKUtils.getTrueWidth(line);
            if(preferredWidth < lineWidth) {
                preferredWidth = lineWidth;
            }
        }
        currentBounds = currentBounds.withColumns(preferredWidth);
        return currentBounds;
    }

    public void setForegroundColor(TextColor foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public TextColor getForegroundColor() {
        return foregroundColor;
    }

    public void setBackgroundColor(TextColor backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public TextColor getBackgroundColor() {
        return backgroundColor;
    }

    public void addStyle(SGR sgr) {
        additionalStyles.add(sgr);
    }

    public void removeStyle(SGR sgr) {
        additionalStyles.remove(sgr);
    }

    @Override
    public TerminalSize getPreferredSize() {
        return preferredSize;
    }

    @Override
    public void drawComponent(TextGUIGraphics graphics) {
        ThemeDefinition themeDefinition = graphics.getThemeDefinition(Label.class);
        graphics.applyThemeStyle(themeDefinition.getNormal());
        if(foregroundColor != null) {
            graphics.setForegroundColor(foregroundColor);
        }
        if(backgroundColor != null) {
            graphics.setBackgroundColor(backgroundColor);
        }
        for(SGR sgr: additionalStyles) {
            graphics.enableModifiers(sgr);
        }
        for(int row = 0; row < Math.min(graphics.getSize().getRows(), preferredSize.getColumns()); row++) {
            String line = lines[row];
            if(graphics.getSize().getColumns() >= preferredSize.getColumns()) {
                graphics.putString(0, row, line);
            }
            else {
                int remainingColumns = graphics.getSize().getColumns();
                for(int i = 0; i < line.length() && remainingColumns > 0; i++) {
                    char c = line.charAt(i);
                    int width = CJKUtils.isCharCJK(c) ? 2 : 1;
                    if(remainingColumns >= width) {
                        graphics.setPosition(graphics.getSize().getColumns() - remainingColumns, row);
                        graphics.setCharacter(c);
                        remainingColumns -= width;
                    }
                }
            }
        }
    }
}
