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

import java.util.*;

import com.googlecode.lanterna.terminal.Terminal.Color;

/**
 * Extend this class to create your own themes. A {@code Theme} consists of
 * several {@code Theme.Definition}s, one for each {@code Theme.Category} value.
 * When components are setting their colors according to the theme, they do so
 * by calling {@code TextGraphics.applyTheme}.
 * @author Martin
 */
public class Theme {
	private static final Theme DEFAULT_INSTANCE = new Theme();
	
	private static final Definition DEFAULT = new Definition(Color.BLACK, Color.WHITE, false);
	private static final Definition SELECTED = new Definition(Color.WHITE, Color.BLUE, true);
	private Map<Category,Definition> styles = new HashMap<Category,Definition>();
	

    public enum Category
    {
        DIALOG_AREA,
        SCREEN_BACKGROUND,
        SHADOW,
        RAISED_BORDER,
        BORDER,
        BUTTON_ACTIVE,
        BUTTON_INACTIVE,
        BUTTON_LABEL_INACTIVE,
        BUTTON_LABEL_ACTIVE,
        LIST_ITEM,
        LIST_ITEM_SELECTED,
        CHECKBOX,
        CHECKBOX_SELECTED,
        TEXTBOX,
        TEXTBOX_FOCUSED
    }

    protected Theme()
    {
    	styles.put(Category.DIALOG_AREA, DEFAULT);
    	styles.put(Category.SCREEN_BACKGROUND, new Definition(Color.CYAN, Color.BLUE, true));
    	styles.put(Category.SHADOW, new Definition(Color.BLACK, Color.BLACK, true));
    	styles.put(Category.BORDER, new Definition(Color.BLACK, Color.WHITE, true));
    	styles.put(Category.RAISED_BORDER, new Definition(Color.WHITE, Color.WHITE, true));
    	styles.put(Category.BUTTON_LABEL_ACTIVE, new Definition(Color.YELLOW, Color.BLUE, true));
    	styles.put(Category.BUTTON_LABEL_INACTIVE, new Definition(Color.BLACK, Color.WHITE, true));
    	styles.put(Category.BUTTON_ACTIVE, SELECTED);
    	styles.put(Category.BUTTON_INACTIVE, DEFAULT);
    	styles.put(Category.LIST_ITEM, DEFAULT);
    	styles.put(Category.LIST_ITEM_SELECTED, SELECTED);
    	styles.put(Category.CHECKBOX, DEFAULT);
    	styles.put(Category.CHECKBOX_SELECTED, SELECTED);
    	styles.put(Category.TEXTBOX, SELECTED);
    	styles.put(Category.TEXTBOX_FOCUSED, new Definition(Color.YELLOW, Color.BLUE, true));
    }

    protected Definition getDefault()
    {
        return DEFAULT;
    }

    @Deprecated
    protected Definition getDialogEmptyArea()
    {
    	return getDefinition(Category.DIALOG_AREA);
    }

	@Deprecated
	protected Definition getScreenBackground()
    {
    	return getDefinition(Category.SCREEN_BACKGROUND);
    }

    @Deprecated
    protected Definition getShadow()
    {
    	return getDefinition(Category.SHADOW);
    }

    @Deprecated
    protected Definition getBorder()
    {
    	return getDefinition(Category.BORDER);
    }

    @Deprecated
    protected Definition getRaisedBorder()
    {
        return getDefinition(Category.RAISED_BORDER);
    }

    @Deprecated
    protected Definition getButtonLabelActive()
    {
        return getDefinition(Category.BUTTON_LABEL_ACTIVE);
    }

    @Deprecated
    protected Definition getButtonLabelInactive()
    {
        return getDefinition(Category.BUTTON_LABEL_INACTIVE);
    }

    @Deprecated
    protected Definition getButtonActive()
    {
        return getDefinition(Category.BUTTON_ACTIVE);
    }

    @Deprecated
    protected Definition getButtonInactive()
    {
        return getDefinition(Category.BUTTON_INACTIVE);
    }
    
    @Deprecated
    protected Definition getItem()
    {
        return getDefinition(Category.LIST_ITEM);
    }
    
    @Deprecated
    protected Definition getItemSelected()
    {
        return getDefinition(Category.LIST_ITEM_SELECTED);
    }

    @Deprecated
    protected Definition getCheckBox()
    {
        return getDefinition(Category.CHECKBOX);
    }

    @Deprecated
    protected Definition getCheckBoxSelected()
    {
        return getDefinition(Category.CHECKBOX_SELECTED);
    }

    @Deprecated
    protected Definition getTextBoxFocused()
    {
        return getDefinition(Category.TEXTBOX_FOCUSED);
    }

    @Deprecated
    protected Definition getTextBox()
    {
        return getDefinition(Category.TEXTBOX);
    }

    public static Theme getDefaultTheme()
    {
        return DEFAULT_INSTANCE;
    }

    public Theme.Definition getDefinition(Category category)
    {
    	if (styles.containsKey(category))
    		return styles.get(category);
    	
    	return getDefault();
    }

    public static class Definition
    {
        public Color foreground;
        public Color background;
        public boolean highlighted;
        public boolean underlined;

        public Definition(Color foreground, Color background)
        {
            this(foreground, background, false);
        }

        public Definition(Color foreground, Color background, boolean highlighted)
        {
            this(foreground, background, highlighted, false);
        }

        public Definition(Color foreground, Color background, boolean highlighted, boolean underlined)
        {
        	if (foreground == null)
        		throw new IllegalArgumentException("foreground color cannot be null");
        	
        	if (background == null)
        		throw new IllegalArgumentException("background color cannot be null");
        	
            this.foreground = foreground;
            this.background = background;
            this.highlighted = highlighted;
            this.underlined = underlined;
        }
    }
}
