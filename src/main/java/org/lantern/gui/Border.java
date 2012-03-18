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
 * Copyright (C) 2010-2011 mabe02
 */

package org.lantern.gui;

import org.lantern.gui.theme.Theme;
import org.lantern.terminal.ACS;
import org.lantern.terminal.Terminal.Color;
import org.lantern.terminal.TerminalPosition;
import org.lantern.terminal.TerminalSize;

/**
 *
 * @author mabe02
 */
public abstract class Border
{
    public void drawBorder(TextGraphics graphics)
    {
        drawBorder(graphics, new TerminalSize(graphics.getWidth(), graphics.getHeight()));
    }

    public abstract void drawBorder(TextGraphics graphics, TerminalSize actualSize);
    public abstract TerminalSize getInnerAreaSize(int width, int height);
    public abstract TerminalPosition getInnerAreaLocation(int width, int height);
    public abstract TerminalSize surroundAreaSize(TerminalSize TerminalSize);

    public static class Standard extends Border
    {
        private Color foreground;
        private Color background;
        private boolean bold;

        public Standard() {
            this.foreground = Color.DEFAULT;
            this.background = Color.DEFAULT;
            this.bold = false;
        }

        public Standard(Color foreground, Color background, boolean bold) {
            this.foreground = foreground;
            this.background = background;
            this.bold = bold;
        }

        @Override
        public void drawBorder(TextGraphics graphics, TerminalSize actualSize)
        {
            graphics.setForegroundColor(foreground);
            graphics.setBackgroundColor(background);
            graphics.setBoldMask(false);
            final int width = actualSize.getColumns();
            final int height = actualSize.getRows();

            //Top
            graphics.drawString(0, 0, ACS.ULCORNER + "");            
            for(int x = 0; x < width - 2; x++)
                graphics.drawString(x, 0, ACS.HLINE + "");
            graphics.drawString(width - 1, 0, ACS.URCORNER + "");

            //Each row
            for(int i = 0; i < height - 2; i++) {
                graphics.drawString(0, i + 1, ACS.VLINE + "");
                graphics.drawString(0 + width - 1, i + 1, ACS.VLINE + "");
            }

            //Bottom
            graphics.drawString(0, height - 1, ACS.LLCORNER + "");
            for(int x = 0; x < width - 2; x++)
                graphics.drawString(x + 1, height - 1, ACS.HLINE + "");
            graphics.drawString(width - 1, height - 1, ACS.LRCORNER + "");
        }

        @Override
        public TerminalPosition getInnerAreaLocation(int width, int height)
        {
            if(width > 2 && height > 2)
                return new TerminalPosition(2, 2);
            else
                return new TerminalPosition(0,0);
        }

        @Override
        public TerminalSize getInnerAreaSize(int width, int height)
        {
            if(width > 2 && height > 2)
                return new TerminalSize(width - 4, height - 3);
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
        public void drawBorder(TextGraphics graphics, TerminalSize actualSize)
        {
            final int width = actualSize.getColumns();
            final int height = actualSize.getRows();
            final Theme.Item upperLeft;
            final Theme.Item lowerRight;

            if(raised) {
                upperLeft = graphics.getTheme().getItem(Theme.Category.Border);
                lowerRight = graphics.getTheme().getItem(Theme.Category.DefaultDialog);
            }
            else {
                upperLeft = graphics.getTheme().getItem(Theme.Category.DefaultDialog);
                lowerRight = graphics.getTheme().getItem(Theme.Category.Border);
            }

            //Top
            graphics.applyThemeItem(upperLeft);
            graphics.drawString(0, 0, ACS.ULCORNER + "");
            for(int i = 0; i < width - 2; i++)
                graphics.drawString(1 + i, 0, ACS.HLINE + "");
            graphics.applyThemeItem(lowerRight);
            graphics.drawString(width - 1, 0, ACS.URCORNER + "");

            //Each row
            for(int i = 0; i < height - 2; i++) {
                graphics.applyThemeItem(upperLeft);
                graphics.drawString(0, i + 1, ACS.VLINE + "");
                graphics.applyThemeItem(lowerRight);
                graphics.drawString(width - 1, i + 1, ACS.VLINE + "");
            }

            //Bottom
            graphics.applyThemeItem(upperLeft);
            graphics.drawString(0, height - 1, ACS.LLCORNER + "");
            graphics.applyThemeItem(lowerRight);
            for(int i = 0; i < width - 2; i++)
                graphics.drawString(1 + i, height - 1, ACS.HLINE + "");
            graphics.drawString(width - 1, height - 1, ACS.LRCORNER + "");
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
        public void drawBorder(TextGraphics graphics, TerminalSize actualSize)
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
