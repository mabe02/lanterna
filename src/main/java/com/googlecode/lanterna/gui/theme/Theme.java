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

package com.googlecode.lanterna.gui.theme;

import com.googlecode.lanterna.terminal.Terminal.Color;

/**
 * Extend this class to create your own themes
 * @author Martin
 */
public class Theme
{
    protected Theme()
    {
    }

    protected Item getDefault()
    {
        return getDialogEmptyArea();
    }

    protected Item getDialogEmptyArea()
    {
        return new Item(Color.BLACK, Color.WHITE, false);
    }

    protected Item getScreenBackground()
    {
        return new Item(Color.CYAN, Color.BLUE, true);
    }

    protected Item getShadow()
    {
        return new Item(Color.BLACK, Color.BLACK, true);
    }

    protected Item getBorder()
    {
        return new Item(Color.WHITE, Color.WHITE, true);
    }

    protected Item getButtonLabelActive()
    {
        return new Item(Color.YELLOW, Color.BLUE, true);
    }

    protected Item getButtonLabelInactive()
    {
        return new Item(Color.BLACK, Color.WHITE, true);
    }

    protected Item getButtonActive()
    {
        return new Item(Color.WHITE, Color.BLUE, true);
    }

    protected Item getButtonInactive()
    {
        return new Item(Color.BLACK, Color.WHITE, false);
    }
    
    protected Item getItem()
    {
        return new Item(Color.BLACK, Color.WHITE, false);
    }
    
    protected Item getItemSelected()
    {
        return new Item(Color.WHITE, Color.BLUE, true);
    }

    protected Item getCheckBox()
    {
        return new Item(Color.BLACK, Color.WHITE, false);
    }

    protected Item getCheckBoxSelected()
    {
        return new Item(Color.WHITE, Color.BLUE, true);
    }

    protected Item getTextBoxFocused()
    {
        return new Item(Color.YELLOW, Color.BLUE, true);
    }

    protected Item getTextBox()
    {
        return new Item(Color.WHITE, Color.BLUE, true);
    }

    public static Theme getDefaultTheme()
    {
        return new Theme();
    }

    public Theme.Item getItem(Category category)
    {
        switch(category)
        {
            case DefaultDialog:
                return getDialogEmptyArea();
            case ScreenBackground:
                return getScreenBackground();
            case Shadow:
                return getShadow();
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
            case Item:
                return getItem();
            case ItemSelected:
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
        DefaultDialog,
        ScreenBackground,
        Shadow,
        Border,
        ButtonActive,
        ButtonInactive,
        ButtonLabelInactive,
        ButtonLabelActive,
        Item,
        ItemSelected,
        CheckBox,
        CheckBoxSelected,
        TextBox,
        TextBoxFocused
    }

    public class Item
    {
        public Color foreground;
        public Color background;
        public boolean highlighted;
        public boolean underlined;

        public Item(Color foreground, Color background)
        {
            this(foreground, background, false);
        }

        public Item(Color foreground, Color background, boolean highlighted)
        {
            this(foreground, background, highlighted, false);
        }

        public Item(Color foreground, Color background, boolean highlighted, boolean underlined)
        {
            this.foreground = foreground;
            this.background = background;
            this.highlighted = highlighted;
            this.underlined = underlined;
        }
    }

    public static final Color TITLE_FG = Color.YELLOW;
    public static final Color TITLE_BG = Color.WHITE;
    public static final boolean TITLE_HL = true;

    

    public static final Color BUTTON_KEY_ACTIVE_FG = Color.WHITE;
    public static final Color BUTTON_KEY_ACTIVE_BG = Color.BLUE;
    public static final boolean BUTTON_KEY_ACTIVE_HL = true;

    public static final Color BUTTON_KEY_INACTIVE_FG = Color.RED;
    public static final Color BUTTON_KEY_INACTIVE_BG = Color.WHITE;
    public static final boolean BUTTON_KEY_INACTIVE_HL = false;

    public static final Color INPUTBOX_FG = Color.BLACK;
    public static final Color INPUTBOX_BG = Color.WHITE;
    public static final boolean INPUTBOX_HL = false;

    public static final Color INPUTBOX_BORDER_FG = Color.BLACK;
    public static final Color INPUTBOX_BORDER_BG = Color.WHITE;
    public static final boolean INPUTBOX_BORDER_HL = false;

    public static final Color SEARCHBOX_FG = Color.BLACK;
    public static final Color SEARCHBOX_BG = Color.WHITE;
    public static final boolean SEARCHBOX_HL = false;

    public static final Color SEARCHBOX_TITLE_FG = Color.YELLOW;
    public static final Color SEARCHBOX_TITLE_BG = Color.WHITE;
    public static final boolean SEARCHBOX_TITLE_HL = true;

    public static final Color SEARCHBOX_BORDER_FG = Color.WHITE;
    public static final Color SEARCHBOX_BORDER_BG = Color.WHITE;
    public static final boolean SEARCHBOX_BORDER_HL = true;

    public static final Color POSITION_INDICATOR_FG = Color.YELLOW;
    public static final Color POSITION_INDICATOR_BG = Color.WHITE;
    public static final boolean POSITION_INDICATOR_HL = true;

    public static final Color MENUBOX_FG = Color.BLACK;
    public static final Color MENUBOX_BG = Color.WHITE;
    public static final boolean MENUBOX_HL = false;

    public static final Color MENUBOX_BORDER_FG = Color.WHITE;
    public static final Color MENUBOX_BORDER_BG = Color.WHITE;
    public static final boolean MENUBOX_BORDER_HL = true;

    public static final Color TAG_FG = Color.YELLOW;
    public static final Color TAG_BG = Color.WHITE;
    public static final boolean TAG_HL = true;

    public static final Color TAG_SELECTED_FG = Color.YELLOW;
    public static final Color TAG_SELECTED_BG = Color.BLUE;
    public static final boolean TAG_SELECTED_HL = true;

    public static final Color TAG_KEY_FG = Color.YELLOW;
    public static final Color TAG_KEY_BG = Color.WHITE;
    public static final boolean TAG_KEY_HL = true;

    public static final Color TAG_KEY_SELECTED_FG = Color.YELLOW;
    public static final Color TAG_KEY_SELECTED_BG = Color.BLUE;
    public static final boolean TAG_KEY_SELECTED_HL = true;

    public static final Color UARROW_FG = Color.GREEN;
    public static final Color UARROW_BG = Color.WHITE;
    public static final boolean UARROW_HL = true;

    public static final Color DARROW_FG = Color.GREEN;
    public static final Color DARROW_BG = Color.WHITE;
    public static final boolean DARROW_HL = true;


}
