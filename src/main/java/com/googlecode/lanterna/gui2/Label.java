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
 * Copyright (C) 2010-2017 Martin Berglund
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.ThemeDefinition;

import java.util.EnumSet;
import java.util.List;

/**
 * Label is a simple read-only text display component. It supports customized colors and multi-line text.
 * @author Martin
 */
public class Label extends AbstractComponent<Label> {
    private String[] lines;
    private Integer labelWidth;
    private TerminalSize labelSize;
    private TextColor foregroundColor;
    private TextColor backgroundColor;
    private final EnumSet<SGR> additionalStyles;

    /**
     * Main constructor, creates a new Label displaying a specific text.
     * @param text Text the label will display
     */
    public Label(String text) {
        this.lines = null;
        this.labelSize = TerminalSize.ZERO;
        this.labelWidth = 0;
        this.foregroundColor = null;
        this.backgroundColor = null;
        this.additionalStyles = EnumSet.noneOf(SGR.class);
        setText(text);
    }

    /**
     * Protected access to set the internal representation of the text in this label, to be used by sub-classes of label
     * in certain cases where {@code setText(..)} doesn't work. In general, you probably want to stick to
     * {@code setText(..)} instead of this method unless you have a good reason not to.
     * @param lines New lines this label will display
     */
    protected void setLines(String[] lines) {
        this.lines = lines;
    }

    /**
     * Updates the text this label is displaying
     * @param text New text to display
     */
    public synchronized void setText(String text) {
        setLines(splitIntoMultipleLines(text));
        this.labelSize = getBounds(lines, labelSize);
        invalidate();
    }

    /**
     * Returns the text this label is displaying. Multi-line labels will have their text concatenated with \n, even if
     * they were originally set using multi-line text having \r\n as line terminators.
     * @return String of the text this label is displaying
     */
    public synchronized String getText() {
        if(lines.length == 0) {
            return "";
        }
        StringBuilder bob = new StringBuilder(lines[0]);
        for(int i = 1; i < lines.length; i++) {
            bob.append("\n").append(lines[i]);
        }
        return bob.toString();
    }

    /**
     * Utility method for taking a string and turning it into an array of lines. This method is used in order to deal
     * with line endings consistently.
     * @param text Text to split
     * @return Array of strings that forms the lines of the original string
     */
    protected String[] splitIntoMultipleLines(String text) {
        return text.replace("\r", "").split("\n");
    }

    /**
     * Returns the area, in terminal columns and rows, required to fully draw the lines passed in.
     * @param lines Lines to measure the size of
     * @param currentBounds Optional (can pass {@code null}) terminal size to use for storing the output values. If the
     *                      method is called many times and always returning the same value, passing in an external
     *                      reference of this size will avoid creating new {@code TerminalSize} objects every time
     * @return Size that is required to draw the lines
     */
    protected TerminalSize getBounds(String[] lines, TerminalSize currentBounds) {
        if(currentBounds == null) {
            currentBounds = TerminalSize.ZERO;
        }
        currentBounds = currentBounds.withRows(lines.length);
        if(labelWidth == null || labelWidth == 0) {
            int preferredWidth = 0;
            for(String line : lines) {
                int lineWidth = TerminalTextUtils.getColumnWidth(line);
                if(preferredWidth < lineWidth) {
                    preferredWidth = lineWidth;
                }
            }
            currentBounds = currentBounds.withColumns(preferredWidth);
        }
        else {
            List<String> wordWrapped = TerminalTextUtils.getWordWrappedText(labelWidth, lines);
            currentBounds = currentBounds.withColumns(labelWidth).withRows(wordWrapped.size());
        }
        return currentBounds;
    }

    /**
     * Overrides the current theme's foreground color and use the one specified. If called with {@code null}, the
     * override is cleared and the theme is used again.
     * @param foregroundColor Foreground color to use when drawing the label, if {@code null} then use the theme's
     *                        default
     * @return Itself
     */
    public synchronized Label setForegroundColor(TextColor foregroundColor) {
        this.foregroundColor = foregroundColor;
        return this;
    }

    /**
     * Returns the foreground color used when drawing the label, or {@code null} if the color is read from the current
     * theme.
     * @return Foreground color used when drawing the label, or {@code null} if the color is read from the current
     * theme.
     */
    public TextColor getForegroundColor() {
        return foregroundColor;
    }

    /**
     * Overrides the current theme's background color and use the one specified. If called with {@code null}, the
     * override is cleared and the theme is used again.
     * @param backgroundColor Background color to use when drawing the label, if {@code null} then use the theme's
     *                        default
     * @return Itself
     */
    public synchronized Label setBackgroundColor(TextColor backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    /**
     * Returns the background color used when drawing the label, or {@code null} if the color is read from the current
     * theme.
     * @return Background color used when drawing the label, or {@code null} if the color is read from the current
     * theme.
     */
    public TextColor getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Adds an additional SGR style to use when drawing the label, in case it wasn't enabled by the theme
     * @param sgr SGR style to enable for this label
     * @return Itself
     */
    public synchronized Label addStyle(SGR sgr) {
        additionalStyles.add(sgr);
        return this;
    }

    /**
     * Removes an additional SGR style used when drawing the label, previously added by {@code addStyle(..)}. If the
     * style you are trying to remove is specified by the theme, calling this method will have no effect.
     * @param sgr SGR style to remove
     * @return Itself
     */
    public synchronized Label removeStyle(SGR sgr) {
        additionalStyles.remove(sgr);
        return this;
    }

    /**
     * Use this method to limit how wide the label can grow. If set to {@code null} there is no limit but if set to a
     * positive integer then the preferred size will be calculated using word wrapping for lines that are longer than
     * this label width. This may make the label increase in height as new rows may be requested. Please note that some
     * layout managers might assign more space to the label and because of this the wrapping might not be as you expect
     * it. If set to 0, the label will request the same space as if set to {@code null}, but when drawing it will apply
     * word wrapping instead of truncation in order to fit the label inside the designated area if it's smaller than
     * what was requested. By default this is set to 0.
     *
     * @param labelWidth Either {@code null} or 0 for no limit on how wide the label can be, where 0 indicates word
     *                   wrapping should be used if the assigned area is smaller than the requested size, or a positive
     *                   integer setting the requested maximum width at what point word wrapping will begin
     * @return Itself
     */
    public synchronized Label setLabelWidth(Integer labelWidth) {
        this.labelWidth = labelWidth;
        return this;
    }

    /**
     * Returns the limit how wide the label can grow. If set to {@code null} or 0 there is no limit but if set to a
     * positive integer then the preferred size will be calculated using word wrapping for lines that are longer than
     * the label width. This may make the label increase in height as new rows may be requested. Please note that some
     * layout managers might assign more space to the label and because of this the wrapping might not be as you expect
     * it. If set to 0, the label will request the same space as if set to {@code null}, but when drawing it will apply
     * word wrapping instead of truncation in order to fit the label inside the designated area if it's smaller than
     * what was requested.
     * @return Either {@code null} or 0 for no limit on how wide the label can be, where 0 indicates word
     *         wrapping should be used if the assigned area is smaller than the requested size, or a positive
     *         integer setting the requested maximum width at what point word wrapping will begin
     */
    public Integer getLabelWidth() {
        return labelWidth;
    }

    @Override
    protected ComponentRenderer<Label> createDefaultRenderer() {
        return new ComponentRenderer<Label>() {
            @Override
            public TerminalSize getPreferredSize(Label Label) {
                return labelSize;
            }

            @Override
            public void drawComponent(TextGUIGraphics graphics, Label component) {
                ThemeDefinition themeDefinition = component.getThemeDefinition();
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

                String[] linesToDraw;
                if(component.getLabelWidth() == null) {
                    linesToDraw = component.lines;
                }
                else {
                    linesToDraw = TerminalTextUtils.getWordWrappedText(graphics.getSize().getColumns(), component.lines).toArray(new String[0]);
                }

                for(int row = 0; row < Math.min(graphics.getSize().getRows(), linesToDraw.length); row++) {
                    String line = linesToDraw[row];
                    if(graphics.getSize().getColumns() >= labelSize.getColumns()) {
                        graphics.putString(0, row, line);
                    }
                    else {
                        int availableColumns = graphics.getSize().getColumns();
                        String fitString = TerminalTextUtils.fitString(line, availableColumns);
                        graphics.putString(0, row, fitString);
                    }
                }
            }
        };
    }
}
