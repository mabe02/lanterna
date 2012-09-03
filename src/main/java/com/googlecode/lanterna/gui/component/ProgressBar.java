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
import com.googlecode.lanterna.gui.Theme.Category;
import com.googlecode.lanterna.terminal.ACS;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;

/**
 *
 * @author Martin
 */
public class ProgressBar extends AbstractComponent
{
    private final int preferredWidth;
    private double progress;
    private char fill_complete_char = ACS.BLOCK_SOLID;
    private char fill_remaining_char = ' ';
    private boolean show_percentage = true;

    public ProgressBar(int preferredWidth)
    {
        this.preferredWidth = preferredWidth;
        this.progress = 0.0;
    }

    @Override
    protected TerminalSize calculatePreferredSize() {
        return new TerminalSize(preferredWidth, 1);
    }

    @Override
    public void repaint(TextGraphics graphics)
    {
    	int bar_start = isCompletedPercentageShown() ? 5 : 0;
        int total_width = graphics.getWidth() - bar_start;
        int highlighted_blocks = (int) (total_width * progress);
        
        if (isCompletedPercentageShown()) {
        	graphics.applyTheme(Category.BUTTON_LABEL_INACTIVE);
        	Integer percentage = (int) Math.round(progress * 100);
        	String perc_str;
        	if (percentage == 100)
        		perc_str = percentage + "%";
        	else if (percentage >= 10)
        		perc_str = " " + percentage + "%";
        	else
        		perc_str = "  " + percentage + "%";
        	
        	graphics.drawString(0, 0, perc_str);
        }
        
        graphics.applyTheme(Category.PROGRESS_BAR_COMPLETED);
        graphics.fillRectangle(fill_complete_char, new TerminalPosition(bar_start, 0), new TerminalSize(bar_start + highlighted_blocks, 1));
        graphics.applyTheme(Category.PROGRESS_BAR_REMAINING);
        graphics.fillRectangle(fill_remaining_char, new TerminalPosition(bar_start + highlighted_blocks, 0), new TerminalSize(total_width - highlighted_blocks, 1));
    }

    public double getProgress()
    {
        return progress;
    }

    public void setProgress(double progress)
    {
        this.progress = progress;
    }
    
    /**
    Sets the character used to fill the portion of the progress bar indicating the completed portion.
    */
    public void setCompleteFillChar(char fill) {
    	fill_complete_char = fill;
    }
    
    public char getCompleteFillChar() {
    	return fill_complete_char;
    }
    
    /**
    Sets the character used to fill the portion of the progress bar indicating the incomplete portion.
    */
    public void setRemainingFillChar(char fill) {
    	fill_remaining_char = fill;
    }
    
    public char getRemainingFillChar() {
    	return fill_remaining_char;
    }
    
    /**
    Controls whether the completion percentage will be shown to the left of the progress bar.
    */
    public void setCompletedPercentageShown(boolean flag) {
    	show_percentage = flag;
    }
    
    public boolean isCompletedPercentageShown() {
    	return show_percentage;
    }
    
}
