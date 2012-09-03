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

package com.googlecode.lanterna.gui;

import com.googlecode.lanterna.terminal.ACS;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;

/**
 * Class responsible for defining and rendering a border around a component. The
 * actuals border implementations are available through subclasses.
 * @author Martin
 */
public abstract class Border
{
    public abstract void drawBorder(TextGraphics graphics, TerminalSize actualSize, String title);
    public abstract TerminalSize getInnerAreaSize(int width, int height);
    public abstract TerminalPosition getInnerAreaLocation(int width, int height);
    public abstract TerminalSize surroundAreaSize(TerminalSize TerminalSize);

    public static class Standard extends Border
    {

        public Standard() {
        }

        @Override
        public void drawBorder(TextGraphics graphics, TerminalSize actualSize, String title)
        {
            graphics.applyTheme(graphics.getTheme().getDefinition(Theme.Category.BORDER));
            final int width = actualSize.getColumns();
            final int height = actualSize.getRows();
            
            //Top
            graphics.drawString(0, 0, ACS.ULCORNER + "");     
            for(int x = 1; x < width - 1; x++)
                graphics.drawString(x, 0, ACS.HLINE + "");
            graphics.drawString(width - 1, 0, ACS.URCORNER + "");

            //Each row
            for(int i = 1; i < height - 1; i++) {
                graphics.drawString(0, i, ACS.VLINE + "");
                graphics.drawString(0 + width - 1, i, ACS.VLINE + "");
            }

            //Bottom
            graphics.drawString(0, height - 1, ACS.LLCORNER + "");
            for(int x = 1; x < width - 1; x++)
                graphics.drawString(x, height - 1, ACS.HLINE + "");
            graphics.drawString(width - 1, height - 1, ACS.LRCORNER + "");
            
            // Write the title
            graphics.applyTheme(graphics.getTheme().getDefinition(Theme.Category.DIALOG_AREA));
            graphics.setBoldMask(true);
            graphics.drawString(2, 0, title);
        }

        @Override
        public TerminalPosition getInnerAreaLocation(int width, int height)
        {
            if(width > 2 && height > 2)
                return new TerminalPosition(2, 1);
            else
                return new TerminalPosition(0,0);
        }

        @Override
        public TerminalSize getInnerAreaSize(int width, int height)
        {
            if(width > 2 && height > 2)
                return new TerminalSize(width - 4, height - 2);
            else
                return new TerminalSize(width, height);
        }

        @Override
        public TerminalSize surroundAreaSize(TerminalSize TerminalSize)
        {
            return new TerminalSize(TerminalSize.getColumns() == Integer.MAX_VALUE ? Integer.MAX_VALUE : TerminalSize.getColumns() + 4,
                    TerminalSize.getRows() == Integer.MAX_VALUE ? Integer.MAX_VALUE : TerminalSize.getRows() + 2);
        }
    }

    public static class Bevel extends Border
    {
        private boolean raised;

        public Bevel(boolean raised) {
            this.raised = raised;
        }

        @Override
        public void drawBorder(TextGraphics graphics, TerminalSize actualSize, String title)
        {
            final int width = actualSize.getColumns();
            final int height = actualSize.getRows();
            final Theme.Definition upperLeft;
            final Theme.Definition lowerRight;

            if(raised) {
                upperLeft = graphics.getTheme().getDefinition(Theme.Category.RAISED_BORDER);
                lowerRight = graphics.getTheme().getDefinition(Theme.Category.BORDER);
            }
            else {
                upperLeft = graphics.getTheme().getDefinition(Theme.Category.BORDER);
                lowerRight = graphics.getTheme().getDefinition(Theme.Category.RAISED_BORDER);
            }

            //Top
            graphics.applyTheme(upperLeft);
            graphics.drawString(0, 0, ACS.ULCORNER + "");
            for(int i = 1; i < width - 1; i++)
                graphics.drawString(i, 0, ACS.HLINE + "");
            graphics.applyTheme(lowerRight);
            graphics.drawString(width - 1, 0, ACS.URCORNER + "");

            //Each row
            for(int i = 1; i < height - 1; i++) {
                graphics.applyTheme(upperLeft);
                graphics.drawString(0, i, ACS.VLINE + "");
                graphics.applyTheme(lowerRight);
                graphics.drawString(width - 1, i, ACS.VLINE + "");
            }

            //Bottom
            graphics.applyTheme(upperLeft);
            graphics.drawString(0, height - 1, ACS.LLCORNER + "");
            graphics.applyTheme(lowerRight);
            for(int i = 1; i < width - 1; i++)
                graphics.drawString(i, height - 1, ACS.HLINE + "");
            graphics.drawString(width - 1, height - 1, ACS.LRCORNER + "");
            
            // Write the title
            graphics.applyTheme(graphics.getTheme().getDefinition(Theme.Category.DIALOG_AREA));
            graphics.setBoldMask(true);
            graphics.drawString(2, 0, title);
        }

        @Override
        public TerminalPosition getInnerAreaLocation(int width, int height)
        {
            if(width > 2 && height > 2)
                return new TerminalPosition(2, 1);
            else
                return new TerminalPosition(0,0);
        }

        @Override
        public TerminalSize getInnerAreaSize(int width, int height)
        {
            if(width > 2 && height > 2)
                return new TerminalSize(width - 4, height - 2);
            else
                return new TerminalSize(width, height);
        }

        @Override
        public TerminalSize surroundAreaSize(TerminalSize TerminalSize)
        {
            return new TerminalSize(TerminalSize.getColumns() == Integer.MAX_VALUE ? Integer.MAX_VALUE : TerminalSize.getColumns() + 4,
                    TerminalSize.getRows() == Integer.MAX_VALUE ? Integer.MAX_VALUE : TerminalSize.getRows() + 2);
        }
    }

    public static class Invisible extends Border
    {
        @Override
        public void drawBorder(TextGraphics graphics, TerminalSize actualSize, String title)
        {
        }

        @Override
        public TerminalPosition getInnerAreaLocation(int width, int height)
        {
            return new TerminalPosition(0,0);
        }

        @Override
        public TerminalSize getInnerAreaSize(int width, int height)
        {
            return new TerminalSize(width, height);
        }

        @Override
        public TerminalSize surroundAreaSize(TerminalSize TerminalSize)
        {
            return TerminalSize;
        }
    }
}
