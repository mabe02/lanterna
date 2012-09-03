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
 * Copyright (C) 2010-2012 Martin
 */

package com.googlecode.lanterna.gui.component;

import com.googlecode.lanterna.gui.TextGraphics;
import com.googlecode.lanterna.gui.Theme;
import com.googlecode.lanterna.gui.Theme.Category;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.util.Arrays;

/**
 *
 * @author Martin
 */
public class Label extends AbstractComponent
{
    private String []text;
    private int height;
    private int width;
    private int forceWidth;
    private Boolean textBold;
    private Terminal.Color textColor;
    private Theme.Category style;

    public Label()
    {
        this("");
    }

    public Label(String text)
    {
        this(text, -1);
    }

    public Label(String text, Terminal.Color textColor)
    {
        this(text, textColor, null);
    }

    public Label(String text, Boolean textBold)
    {
        this(text, null, textBold);
    }

    public Label(String text, Terminal.Color textColor, Boolean textBold)
    {
        this(text, -1, textColor, textBold);
    }
    
    public Label(String text, int fixedWidth)
    {
        this(text, fixedWidth, null, null);
    }

    public Label(String text, int fixedWidth, Terminal.Color color, Boolean textBold)
    {
        if(text == null)
            this.text = new String[] { "null" };
        else
            this.text = text.split("\n");
        this.textColor = color;
        this.textBold = textBold;
        this.height = 0;
        this.width = 0;
        this.forceWidth = fixedWidth;
        this.style = Theme.Category.DIALOG_AREA;
        updateMetrics();
    }

    @Override
    protected TerminalSize calculatePreferredSize() {
        if(forceWidth == -1)
            return new TerminalSize(width, height);
        else
            return new TerminalSize(forceWidth, height);
    }
    
    @Override
    public void repaint(TextGraphics graphics)
    {
        graphics.applyTheme(graphics.getTheme().getDefinition(style));
        graphics = transformAccordingToAlignment(graphics, calculatePreferredSize());
        
        if(textColor != null)
            graphics.setForegroundColor(textColor);
        if(textBold != null) {
            if(textBold)
                graphics.setBoldMask(true);
            else
                graphics.setBoldMask(false);
        }
        
        if(text.length == 0)
            return;

        int leftPosition = 0;        
        for(int i = 0; i < text.length; i++) {
            if(forceWidth > -1) {
                if(text[i].length() > forceWidth)
                    graphics.drawString(leftPosition, i, text[i].substring(0, forceWidth - 3) + "...");
                else
                    graphics.drawString(leftPosition, i, text[i]);
            }
            else
                graphics.drawString(leftPosition, i, text[i]);
        }
    }

    public void setText(String text) {
        this.text = text.split("\n");
        updateMetrics();
        invalidate();
    }

    public String getText() {
        StringBuilder sb = new StringBuilder();
        for(String line: text)
            sb.append(line).append("\n");
        sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }

    public String[] getLines()
    {
        return Arrays.copyOf(text, text.length);
    }

    public void setStyle(Category style)
    {
        this.style = style;
        invalidate();
    }

    public Category getStyle()
    {
        return style;
    }

    public Color getTextColor()
    {
        return textColor;
    }

    public void setTextColor(Color textColor)
    {
        this.textColor = textColor;
        invalidate();
    }

    private void updateMetrics()
    {
        height = text.length;
        if(height == 0)
            height = 1;

        width = 0;
        for(String line: text) {
            if(line.length() > width)
                width = line.length();
        }
    }
}
