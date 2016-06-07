package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.graphics.ThemeDefinition;

/**
 * Created by Martin on 2016-06-05.
 */
public class ProgressBar extends AbstractComponent<ProgressBar> {

    private int min;
    private int max;
    private int value;
    private int preferredWidth;
    private String labelFormat;

    public ProgressBar() {
        this(0, 100);
    }

    public ProgressBar(int min, int max) {
        this(min, max, 42);
    }

    public ProgressBar(int min, int max, int preferredWidth) {
        if(min > max) {
            min = max;
        }
        this.min = min;
        this.max = max;
        this.value = min;

        if(preferredWidth < 1) {
            preferredWidth = 1;
        }
        this.preferredWidth = preferredWidth;
    }

    public int getMin() {
        return min;
    }

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

    public int getMax() {
        return max;
    }

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

    public int getValue() {
        return value;
    }

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

    public int getPreferredWidth() {
        return preferredWidth;
    }

    public String getLabelFormat() {
        return labelFormat;
    }

    public synchronized ProgressBar setLabelFormat(String labelFormat) {
        this.labelFormat = labelFormat;
        invalidate();
        return this;
    }

    public float getProgress() {
        return (float)(value - min) / (float)max;
    }

    public String getFormattedLabel() {
        if(labelFormat == null) {
            return "";
        }
        return String.format(labelFormat, getProgress() * 100.0f);
    }

    @Override
    protected ComponentRenderer<ProgressBar> createDefaultRenderer() {
        return new DefaultProgressBarRenderer();
    }

    public static class DefaultProgressBarRenderer implements ComponentRenderer<ProgressBar> {
        @Override
        public TerminalSize getPreferredSize(ProgressBar component) {
            return new TerminalSize(component.getPreferredWidth(), 1);
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
                    if(column >= labelStartPosition && column < labelStartPosition + labelWidth) {
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

    public static class LargeProgressBarRenderer implements ComponentRenderer<ProgressBar> {
        @Override
        public TerminalSize getPreferredSize(ProgressBar component) {
            return new TerminalSize(component.getPreferredWidth(), 3);
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
