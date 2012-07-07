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
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.ACS;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This component is designed for displaying large chunks of text. If the text
 * is larger than the component, it will display scrollbars and letting the
 * user scroll through the text using the arrow keys.
 * @author mberglun
 */
public class TextArea  extends AbstractInteractableComponent
{
    private final List<String> lines;
    private final TerminalSize preferredSize;
    private final int longestLine;
    
    private TerminalSize lastSize;
    private int scrollTopIndex;
    private int scrollLeftIndex;

    public TextArea(String text) {       
        this(new TerminalSize(0, 0), text);
    }
    
    public TextArea(TerminalSize preferredSize, String text) {
        if(text == null)
            text = "";
        
        this.lines = new ArrayList<String>();
        this.preferredSize = preferredSize;
        this.scrollTopIndex = 0;
        this.scrollLeftIndex = 0;
        this.lastSize = null;
        lines.addAll(Arrays.asList(text.split("\n")));
        
        int longestLine = 0;
        for(String line: lines)
            if(line.replace("\t", "    ").length() > longestLine)
                longestLine = line.replace("\t", "    ").length();
        this.longestLine = longestLine;
    }

    public TerminalSize getPreferredSize()
    {
        return new TerminalSize(
                preferredSize.getColumns() > 0 ? preferredSize.getColumns() : longestLine + 1,
                preferredSize.getRows() > 0 ? preferredSize.getRows() : lines.size() + 1);
    }

    public void repaint(TextGraphics graphics)
    {
        lastSize = new TerminalSize(graphics.getWidth(), graphics.getHeight());
        
        //Do we need to recalculate the scroll position? 
        //This code would be triggered by resizing the window when the scroll
        //position is at the bottom
        if(lines.size() > graphics.getHeight() &&
                lines.size() - scrollTopIndex < graphics.getHeight()) {
            scrollTopIndex = lines.size() - graphics.getHeight();
        }
        if(longestLine > graphics.getWidth() &&
                longestLine - scrollLeftIndex < graphics.getWidth()) {
            scrollLeftIndex = longestLine - graphics.getWidth();
        }
        
        graphics.applyTheme(Theme.Category.ListItem);
        graphics.fillArea(' ');

        for(int i = scrollTopIndex; i < lines.size(); i++) {
            if(i - scrollTopIndex >= graphics.getHeight())
                break;

            graphics.applyTheme(Theme.Category.ListItem);
            printItem(graphics, 0, 0 + i - scrollTopIndex, lines.get(i));
        }

        if(lines.size() > graphics.getHeight()) {
            graphics.applyTheme(Theme.Category.DialogArea);
            graphics.drawString(graphics.getWidth() - 1, 0, ACS.ARROW_UP + "");

            graphics.applyTheme(Theme.Category.DialogArea);
            for(int i = 1; i < graphics.getHeight() - 1; i++)
                graphics.drawString(graphics.getWidth() - 1, i, ACS.BLOCK_MIDDLE + "");

            graphics.applyTheme(Theme.Category.DialogArea);
            graphics.drawString(graphics.getWidth() - 1, graphics.getHeight() - 1, ACS.ARROW_DOWN + "");
            
            //Finally print the 'tick'
            int scrollableSize = lines.size() - graphics.getHeight();
            double position = (double)scrollTopIndex / ((double)scrollableSize);
            int tickPosition = (int)(((double)graphics.getHeight() - 3.0) * position);

            graphics.applyTheme(Theme.Category.Shadow);
            graphics.drawString(graphics.getWidth() - 1, 1 + tickPosition, " ");
        }
        if(longestLine > graphics.getWidth()) {
            graphics.applyTheme(Theme.Category.DialogArea);
            graphics.drawString(0, graphics.getHeight() - 1, ACS.ARROW_LEFT + "");

            graphics.applyTheme(Theme.Category.DialogArea);
            for(int i = 1; i < graphics.getWidth() - 2; i++)
                graphics.drawString(i, graphics.getHeight() - 1, ACS.BLOCK_MIDDLE + "");

            graphics.applyTheme(Theme.Category.DialogArea);
            graphics.drawString(graphics.getWidth() - 2, graphics.getHeight() - 1, ACS.ARROW_RIGHT + "");
            
            //Finally print the 'tick'
            int scrollableSize = longestLine - graphics.getWidth();
            double position = (double)scrollLeftIndex / ((double)scrollableSize);
            int tickPosition = (int)(((double)graphics.getWidth() - 4.0) * position);

            graphics.applyTheme(Theme.Category.Shadow);
            graphics.drawString(1 + tickPosition, graphics.getHeight() - 1, " ");
        }
            
        setHotspot(graphics.translateToGlobalCoordinates(new TerminalPosition(0, 0)));
    }

    public Result keyboardInteraction(Key key)
    {
        try {
            switch(key.getKind()) {
                case Tab:
                case Enter:
                    return Result.NEXT_INTERACTABLE_RIGHT;

                case ReverseTab:
                    return Result.PREVIOUS_INTERACTABLE_LEFT;

                case ArrowRight:
                    if(lastSize != null && scrollLeftIndex < longestLine - lastSize.getColumns())
                        scrollLeftIndex++;
                    break;

                case ArrowLeft:
                    if(scrollLeftIndex > 0)
                        scrollLeftIndex--;
                    break;

                case ArrowDown:
                    if(lastSize != null && scrollTopIndex < lines.size() - lastSize.getRows())
                        scrollTopIndex++;
                    break;

                case ArrowUp:
                    if(scrollTopIndex > 0)
                        scrollTopIndex--;
                    break;
            }
            return Result.DO_NOTHING;
        }
        finally {
            invalidate();
        }
    }

    @Override
    public boolean isScrollable() {
        return true;
    }

    private void printItem(TextGraphics graphics, int x, int y, String text)
    {
        //TODO: fix this
        text = text.replace("\t", "    ");
        
        if(scrollLeftIndex >= text.length())
            text = "";
        else
            text = text.substring(scrollLeftIndex);
        
        if(text.length() > graphics.getWidth())
            text = text.substring(0, graphics.getWidth());
        graphics.drawString(x, y, text);
    }

}
