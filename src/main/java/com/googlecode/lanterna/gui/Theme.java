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
        DialogArea,
        ScreenBackground,
        Shadow,
        RaisedBorder,
        Border,
        ButtonActive,
        ButtonInactive,
        ButtonLabelInactive,
        ButtonLabelActive,
        ListItem,
        ListItemSelected,
        CheckBox,
        CheckBoxSelected,
        TextBox,
        TextBoxFocused
    }

    protected Theme()
    {
    	styles.put(Category.DialogArea, DEFAULT);
    	styles.put(Category.ScreenBackground, new Definition(Color.CYAN, Color.BLUE, true));
    	styles.put(Category.Shadow, new Definition(Color.BLACK, Color.BLACK, true));
    	styles.put(Category.Border, new Definition(Color.BLACK, Color.WHITE, true));
    	styles.put(Category.RaisedBorder, new Definition(Color.WHITE, Color.WHITE, true));
    	styles.put(Category.ButtonLabelActive, new Definition(Color.YELLOW, Color.BLUE, true));
    	styles.put(Category.ButtonLabelInactive, new Definition(Color.BLACK, Color.WHITE, true));
    	styles.put(Category.ButtonActive, SELECTED);
    	styles.put(Category.ButtonInactive, DEFAULT);
    	styles.put(Category.ListItem, DEFAULT);
    	styles.put(Category.ListItemSelected, SELECTED);
    	styles.put(Category.CheckBox, DEFAULT);
    	styles.put(Category.CheckBoxSelected, SELECTED);
    	styles.put(Category.TextBox, SELECTED);
    	styles.put(Category.TextBoxFocused, new Definition(Color.YELLOW, Color.BLUE, true));
    }

    protected Definition getDefault()
    {
        return DEFAULT;
    }

    @Deprecated
    protected Definition getDialogEmptyArea()
    {
    	return getDefinition(Category.DialogArea);
    }

	@Deprecated
	protected Definition getScreenBackground()
    {
    	return getDefinition(Category.ScreenBackground);
    }

    @Deprecated
    protected Definition getShadow()
    {
    	return getDefinition(Category.Shadow);
    }

    @Deprecated
    protected Definition getBorder()
    {
    	return getDefinition(Category.Border);
    }

    @Deprecated
    protected Definition getRaisedBorder()
    {
        return getDefinition(Category.RaisedBorder);
    }

    @Deprecated
    protected Definition getButtonLabelActive()
    {
        return getDefinition(Category.ButtonLabelActive);
    }

    @Deprecated
    protected Definition getButtonLabelInactive()
    {
        return getDefinition(Category.ButtonLabelInactive);
    }

    @Deprecated
    protected Definition getButtonActive()
    {
        return getDefinition(Category.ButtonActive);
    }

    @Deprecated
    protected Definition getButtonInactive()
    {
        return getDefinition(Category.ButtonInactive);
    }
    
    @Deprecated
    protected Definition getItem()
    {
        return getDefinition(Category.ListItem);
    }
    
    @Deprecated
    protected Definition getItemSelected()
    {
        return getDefinition(Category.ListItemSelected);
    }

    @Deprecated
    protected Definition getCheckBox()
    {
        return getDefinition(Category.CheckBox);
    }

    @Deprecated
    protected Definition getCheckBoxSelected()
    {
        return getDefinition(Category.CheckBoxSelected);
    }

    @Deprecated
    protected Definition getTextBoxFocused()
    {
        return getDefinition(Category.TextBoxFocused);
    }

    @Deprecated
    protected Definition getTextBox()
    {
        return getDefinition(Category.TextBox);
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
            this.foreground = foreground;
            this.background = background;
            this.highlighted = highlighted;
            this.underlined = underlined;
        }
    }
}
