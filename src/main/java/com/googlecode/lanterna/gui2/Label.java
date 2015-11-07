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
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.ThemeDefinition;

import java.util.EnumSet;

/**
 * Label is a simple read-only text display component that can show multi-line text in multiple colors
 * @author Martin
 */
public class Label extends AbstractComponent<Label> {
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
            int lineWidth = CJKUtils.getColumnWidth(line);
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
    protected ComponentRenderer<Label> createDefaultRenderer() {
        return new ComponentRenderer<Label>() {
            @Override
            public TerminalSize getPreferredSize(Label Label) {
                return preferredSize;
            }

            @Override
            public void drawComponent(TextGUIGraphics graphics, Label component) {
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
                for(int row = 0; row < Math.min(graphics.getSize().getRows(), preferredSize.getRows()); row++) {
                    String line = lines[row];
                    if(graphics.getSize().getColumns() >= preferredSize.getColumns()) {
                        graphics.putString(0, row, line);
                    }
                    else {
                        int availableColumns = graphics.getSize().getColumns();
                        String fitString = CJKUtils.fitString(line, availableColumns);
                        graphics.putString(0, row, fitString);
                    }
                }
            }
        };
    }
}
