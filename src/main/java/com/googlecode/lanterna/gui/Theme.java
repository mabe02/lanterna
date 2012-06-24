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

import com.googlecode.lanterna.terminal.Terminal.Color;

/**
 * Extend this class to create your own themes. A {@code Theme} consists of
 * several {@code Theme.Definition}s, one for each {@code Theme.Category} value.
 * When components are setting their colors according to the theme, they do so
 * by calling {@code TextGraphics.applyTheme}.
 * @author Martin
 */
public class Theme
{
    protected Theme()
    {
    }

    protected Definition getDefault()
    {
        return getDialogEmptyArea();
    }

    protected Definition getDialogEmptyArea()
    {
        return new Definition(Color.BLACK, Color.WHITE, false);
    }

    protected Definition getScreenBackground()
    {
        return new Definition(Color.CYAN, Color.BLUE, true);
    }

    protected Definition getShadow()
    {
        return new Definition(Color.BLACK, Color.BLACK, true);
    }

    protected Definition getBorder()
    {
        return new Definition(Color.BLACK, Color.WHITE, true);
    }

    protected Definition getRaisedBorder()
    {
        return new Definition(Color.WHITE, Color.WHITE, true);
    }

    protected Definition getButtonLabelActive()
    {
        return new Definition(Color.YELLOW, Color.BLUE, true);
    }

    protected Definition getButtonLabelInactive()
    {
        return new Definition(Color.BLACK, Color.WHITE, true);
    }

    protected Definition getButtonActive()
    {
        return new Definition(Color.WHITE, Color.BLUE, true);
    }

    protected Definition getButtonInactive()
    {
        return new Definition(Color.BLACK, Color.WHITE, false);
    }
    
    protected Definition getItem()
    {
        return new Definition(Color.BLACK, Color.WHITE, false);
    }
    
    protected Definition getItemSelected()
    {
        return new Definition(Color.WHITE, Color.BLUE, true);
    }

    protected Definition getCheckBox()
    {
        return new Definition(Color.BLACK, Color.WHITE, false);
    }

    protected Definition getCheckBoxSelected()
    {
        return new Definition(Color.WHITE, Color.BLUE, true);
    }

    protected Definition getTextBoxFocused()
    {
        return new Definition(Color.YELLOW, Color.BLUE, true);
    }

    protected Definition getTextBox()
    {
        return new Definition(Color.WHITE, Color.BLUE, true);
    }

    public static Theme getDefaultTheme()
    {
        return new Theme();
    }

    public Theme.Definition getDefinition(Category category)
    {
        switch(category)
        {
            case DialogArea:
                return getDialogEmptyArea();
            case ScreenBackground:
                return getScreenBackground();
            case Shadow:
                return getShadow();
            case RaisedBorder:
                return getRaisedBorder();
            case Border:
                return getBorder();
            case ButtonLabelActive:
                return getButtonLabelActive();
            case ButtonLabelInactive:
                return getButtonLabelInactive();
            case ButtonActive:
                return getButtonActive();
            case ButtonInactive:
                return getButtonInactive();
            case ListItem:
                return getItem();
            case ListItemSelected:
                return getItemSelected();
            case CheckBox:
                return getCheckBox();
            case CheckBoxSelected:
                return getCheckBoxSelected();
            case TextBox:
                return getTextBox();
            case TextBoxFocused:
                return getTextBoxFocused();
        }
        return getDefault();
    }

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

    public class Definition
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
