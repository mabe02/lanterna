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

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.graphics.ThemeDefinition;

/**
 * This GUI element gives a visual indication of how far a process of some sort has progressed at any given time. It's
 * a classic user interface component that most people are familiar with. It works based on a scale expressed as having
 * a <i>minimum</i>, a <i>maximum</i> and a current <i>value</i> somewhere along that range. When the current
 * <i>value</i> is the same as the <i>minimum</i>, the progress indication is empty, at 0%. If the <i>value</i> is the
 * same as the <i>maximum</i>, the progress indication is filled, at 100%. Any <i>value</i> in between the
 * <i>minimum</i> and the <i>maximum</i> will be indicated proportionally to where on this range between <i>minimum</i>
 * and <i>maximum</i> it is.
 * <p>
 * In order to add a label to the progress bar, for example to print the % completed, this class supports adding a
 * format specification. This label format, before drawing, will be passed in through a {@code String.format(..)} with
 * the current progress of <i>value</i> from <i>minimum</i> to <i>maximum</i> expressed as a {@code float} passed in as
 * a single vararg parameter. This parameter will be scaled from 0.0f to 100.0f. By default, the label format is set to
 * "%2.0f%%" which becomes a simple percentage string when formatted.
 * @author Martin
 */
public class ProgressBar extends AbstractComponent<ProgressBar> {

    private int min;
    private int max;
    private int value;
    private int preferredWidth;
    private String labelFormat;

    /**
     * Creates a new progress bar initially defined with a range from 0 to 100. The
     */
    public ProgressBar() {
        this(0, 100);
    }

    /**
     * Creates a new progress bar with a defined range of minimum to maximum
     * @param min The minimum value of this progress bar
     * @param max The maximum value of this progress bar
     */
    public ProgressBar(int min, int max) {
        this(min, max, 0);
    }

    /**
     * Creates a new progress bar with a defined range of minimum to maximum and also with a hint as to how wide the
     * progress bar should be drawn
     * @param min The minimum value of this progress bar
     * @param max The maximum value of this progress bar
     * @param preferredWidth Width size hint, in number of columns, for this progress bar. The renderer may choose to
     *                       not use this hint. 0 or less means that there is no hint.
     */
    public ProgressBar(int min, int max, int preferredWidth) {
        if(min > max) {
            min = max;
        }
        this.min = min;
        this.max = max;
        this.value = min;
        this.labelFormat = "%2.0f%%";

        if(preferredWidth < 1) {
            preferredWidth = 1;
        }
        this.preferredWidth = preferredWidth;
    }

    /**
     * Returns the current <i>minimum</i> value for this progress bar
     * @return The <i>minimum</i> value of this progress bar
     */
    public int getMin() {
        return min;
    }

    /**
     * Updates the <i>minimum</i> value of this progress bar. If the current <i>maximum</i> and/or <i>value</i> are
     * smaller than this new <i>minimum</i>, they are automatically adjusted so that the range is still valid.
     * @param min New <i>minimum</i> value to assign to this progress bar
     * @return Itself
     */
    public synchronized ProgressBar setMin(int min) {
        if(min > max) {
            setMax(min);
        }
        if(min > value) {
            setValue(min);
        }
        if(this.min != min) {
            this.min = min;
            invalidate();
        }
        return this;
    }

    /**
     * Returns the current <i>maximum</i> value for this progress bar
     * @return The <i>maximum</i> value of this progress bar
     */
    public int getMax() {
        return max;
    }

    /**
     * Updates the <i>maximum</i> value of this progress bar. If the current <i>minimum</i> and/or <i>value</i> are
     * greater than this new <i>maximum</i>, they are automatically adjusted so that the range is still valid.
     * @param max New <i>maximum</i> value to assign to this progress bar
     * @return Itself
     */
    public synchronized ProgressBar setMax(int max) {
        if(max < min) {
            setMin(max);
        }
        if(max < value) {
            setValue(max);
        }
        if(this.max != max) {
            this.max = max;
            invalidate();
        }
        return this;
    }

    /**
     * Returns the current <i>value</i> of this progress bar, which represents how complete the progress indication is.
     * @return The progress value of this progress bar
     */
    public int getValue() {
        return value;
    }

    /**
     * Updates the <i>value</i> of this progress bar, which will update the visual state. If the value passed in is
     * outside the <i>minimum-maximum</i> range, it is automatically adjusted.
     * @param value New value of the progress bar
     * @return Itself
     */
    public synchronized ProgressBar setValue(int value) {
        if(value < min) {
            value = min;
        }
        if(value > max) {
            value = max;
        }
        if(this.value != value) {
            this.value = value;
            invalidate();
        }
        return this;
    }

    /**
     * Returns the preferred width of the progress bar component, in number of columns. If 0 or less, it should be
     * interpreted as no preference on width and it's up to the renderer to decide.
     * @return Preferred width this progress bar would like to have, or 0 (or less) if no preference
     */
    public int getPreferredWidth() {
        return preferredWidth;
    }

    /**
     * Updated the preferred width hint, which tells the renderer how wide this progress bar would like to be. If called
     * with 0 (or less), it means no preference on width and the renderer will have to figure out on its own how wide
     * to make it.
     * @param preferredWidth New preferred width in number of columns, or 0 if no preference
     */
    public void setPreferredWidth(int preferredWidth) {
        this.preferredWidth = preferredWidth;
    }

    /**
     * Returns the current label format string which is the template for what the progress bar would like to be the
     * label printed. Exactly how this label is printed depends on the renderer, but the default renderer will print
     * the label centered in the middle of the progress indication.
     * @return The label format template string this progress bar is currently using
     */
    public String getLabelFormat() {
        return labelFormat;
    }

    /**
     * Sets the label format this progress bar should use when the component is drawn. The string would be compatible
     * with {@code String.format(..)}, the class will pass the string through that method and pass in the current
     * progress as a single vararg parameter (passed in as a {@code float} in the range of 0.0f to 100.0f). Setting this
     * format string to null or empty string will turn off the label rendering.
     * @param labelFormat Label format to use when drawing the progress bar, or {@code null} to disable
     * @return Itself
     */
    public synchronized ProgressBar setLabelFormat(String labelFormat) {
        this.labelFormat = labelFormat;
        invalidate();
        return this;
    }

    /**
     * Returns the current progress of this progress bar's <i>value</i> from <i>minimum</i> to <i>maximum</i>, expressed
     * as a float from 0.0f to 1.0f.
     * @return current progress of this progress bar expressed as a float from 0.0f to 1.0f.
     */
    public synchronized float getProgress() {
        return (float)(value - min) / (float)max;
    }

    /**
     * Returns the label of this progress bar formatted through {@code String.format(..)} with the current progress
     * value.
     * @return The progress bar label formatted with the current progress
     */
    public synchronized String getFormattedLabel() {
        if(labelFormat == null) {
            return "";
        }
        return String.format(labelFormat, getProgress() * 100.0f);
    }

    @Override
    protected ComponentRenderer<ProgressBar> createDefaultRenderer() {
        return new DefaultProgressBarRenderer();
    }

    /**
     * Default implementation of the progress bar GUI component renderer. This renderer will draw the progress bar
     * on a single line and gradually fill up the space with a different color as the progress is increasing.
     */
    public static class DefaultProgressBarRenderer implements ComponentRenderer<ProgressBar> {
        @Override
        public TerminalSize getPreferredSize(ProgressBar component) {
            int preferredWidth = component.getPreferredWidth();
            if(preferredWidth > 0) {
                return new TerminalSize(preferredWidth, 1);
            }
            else if(component.getLabelFormat() != null && !component.getLabelFormat().trim().isEmpty()) {
                return new TerminalSize(TerminalTextUtils.getColumnWidth(String.format(component.getLabelFormat(), 100.0f)) + 2, 1);
            }
            else {
                return new TerminalSize(10, 1);
            }
        }

        @Override
        public void drawComponent(TextGUIGraphics graphics, ProgressBar component) {
            TerminalSize size = graphics.getSize();
            if(size.getRows() == 0 || size.getColumns() == 0) {
                return;
            }
            ThemeDefinition themeDefinition = component.getThemeDefinition();
            int columnOfProgress = (int)(component.getProgress() * size.getColumns());
            String label = component.getFormattedLabel();
            int labelRow = size.getRows() / 2;

            // Adjust label so it fits inside the component
            int labelWidth = TerminalTextUtils.getColumnWidth(label);

            // Can't be too smart about this, because of CJK characters
            if(labelWidth > size.getColumns()) {
                boolean tail = true;
                while (labelWidth > size.getColumns()) {
                    if(tail) {
                        label = label.substring(0, label.length() - 1);
                    }
                    else {
                        label = label.substring(1);
                    }
                    tail = !tail;
                    labelWidth = TerminalTextUtils.getColumnWidth(label);
                }
            }
            int labelStartPosition = (size.getColumns() - labelWidth) / 2;

            for(int row = 0; row < size.getRows(); row++) {
                graphics.applyThemeStyle(themeDefinition.getActive());
                for(int column = 0; column < size.getColumns(); column++) {
                    if(column == columnOfProgress) {
                        graphics.applyThemeStyle(themeDefinition.getNormal());
                    }
                    if(row == labelRow && column >= labelStartPosition && column < labelStartPosition + labelWidth) {
                        char character = label.charAt(TerminalTextUtils.getStringCharacterIndex(label, column - labelStartPosition));
                        graphics.setCharacter(column, row, character);
                        if(TerminalTextUtils.isCharDoubleWidth(character)) {
                            column++;
                            if(column == columnOfProgress) {
                                graphics.applyThemeStyle(themeDefinition.getNormal());
                            }
                        }
                    }
                    else {
                        graphics.setCharacter(column, row, themeDefinition.getCharacter("FILLER", ' '));
                    }
                }
            }
        }
    }

    /**
     * This progress bar renderer implementation takes slightly more space (three rows) and draws a slightly more
     * complicates progress bar with fixed measurers to mark 25%, 50% and 75%. Maybe you have seen this one before
     * somewhere?
     */
    public static class LargeProgressBarRenderer implements ComponentRenderer<ProgressBar> {
        @Override
        public TerminalSize getPreferredSize(ProgressBar component) {
            int preferredWidth = component.getPreferredWidth();
            if(preferredWidth > 0) {
                return new TerminalSize(preferredWidth, 3);
            }
            else {
                return new TerminalSize(42, 3);
            }
        }

        @Override
        public void drawComponent(TextGUIGraphics graphics, ProgressBar component) {
            TerminalSize size = graphics.getSize();
            if(size.getRows() == 0 || size.getColumns() == 0) {
                return;
            }
            ThemeDefinition themeDefinition = component.getThemeDefinition();
            int columnOfProgress = (int)(component.getProgress() * (size.getColumns() - 4));
            int mark25 = -1;
            int mark50 = -1;
            int mark75 = -1;

            if(size.getColumns() > 9) {
                mark50 = (size.getColumns() - 2) / 2;
            }
            if(size.getColumns() > 16) {
                mark25 = (size.getColumns() - 2) / 4;
                mark75 = mark50 + mark25;
            }

            // Draw header, if there are at least 3 rows available
            int rowOffset = 0;
            if(size.getRows() >= 3) {
                graphics.applyThemeStyle(themeDefinition.getNormal());
                graphics.drawLine(0, 0, size.getColumns(), 0, ' ');
                if(size.getColumns() > 1) {
                    graphics.setCharacter(1, 0, '0');
                }
                if(mark25 != -1) {
                    if(component.getProgress() < 0.25f) {
                        graphics.applyThemeStyle(themeDefinition.getInsensitive());
                    }
                    graphics.putString(1 + mark25, 0, "25");
                }
                if(mark50 != -1) {
                    if(component.getProgress() < 0.50f) {
                        graphics.applyThemeStyle(themeDefinition.getInsensitive());
                    }
                    graphics.putString(1 + mark50, 0, "50");
                }
                if(mark75 != -1) {
                    if(component.getProgress() < 0.75f) {
                        graphics.applyThemeStyle(themeDefinition.getInsensitive());
                    }
                    graphics.putString(1 + mark75, 0, "75");
                }
                if(size.getColumns() >= 7) {
                    if(component.getProgress() < 1.0f) {
                        graphics.applyThemeStyle(themeDefinition.getInsensitive());
                    }
                    graphics.putString(size.getColumns() - 3, 0, "100");
                }
                rowOffset++;
            }

            // Draw the main indicator
            for(int i = 0; i < Math.max(1, size.getRows() - 2); i++) {
                graphics.applyThemeStyle(themeDefinition.getNormal());
                graphics.drawLine(0, rowOffset, size.getColumns(), rowOffset, ' ');
                if (size.getColumns() > 2) {
                    graphics.setCharacter(1, rowOffset, Symbols.SINGLE_LINE_VERTICAL);
                }
                if (size.getColumns() > 3) {
                    graphics.setCharacter(size.getColumns() - 2, rowOffset, Symbols.SINGLE_LINE_VERTICAL);
                }
                if (size.getColumns() > 4) {
                    graphics.applyThemeStyle(themeDefinition.getActive());
                    for(int columnOffset = 2; columnOffset < size.getColumns() - 2; columnOffset++) {
                        if(columnOfProgress + 2 == columnOffset) {
                            graphics.applyThemeStyle(themeDefinition.getNormal());
                        }
                        if(mark25 == columnOffset - 1) {
                            graphics.setCharacter(columnOffset, rowOffset, Symbols.SINGLE_LINE_VERTICAL);
                        }
                        else if(mark50 == columnOffset - 1) {
                            graphics.setCharacter(columnOffset, rowOffset, Symbols.SINGLE_LINE_VERTICAL);
                        }
                        else if(mark75 == columnOffset - 1) {
                            graphics.setCharacter(columnOffset, rowOffset, Symbols.SINGLE_LINE_VERTICAL);
                        }
                        else {
                            graphics.setCharacter(columnOffset, rowOffset, ' ');
                        }
                    }
                }

                if(((int)(component.getProgress() * ((size.getColumns() - 4) * 2))) % 2 == 1) {
                    graphics.applyThemeStyle(themeDefinition.getPreLight());
                    graphics.setCharacter(columnOfProgress + 2, rowOffset, '|');
                }

                rowOffset++;
            }

            // Draw footer if there are at least 2 rows, this one is easy
            if(size.getRows() >= 2) {
                graphics.applyThemeStyle(themeDefinition.getNormal());
                graphics.drawLine(0, rowOffset, size.getColumns(), rowOffset, Symbols.SINGLE_LINE_T_UP);
                graphics.setCharacter(0, rowOffset, ' ');
                if (size.getColumns() > 1) {
                    graphics.setCharacter(size.getColumns() - 1, rowOffset, ' ');
                }
                if (size.getColumns() > 2) {
                    graphics.setCharacter(1, rowOffset, Symbols.SINGLE_LINE_BOTTOM_LEFT_CORNER);
                }
                if (size.getColumns() > 3) {
                    graphics.setCharacter(size.getColumns() - 2, rowOffset, Symbols.SINGLE_LINE_BOTTOM_RIGHT_CORNER);
                }
            }
        }
    }
}
