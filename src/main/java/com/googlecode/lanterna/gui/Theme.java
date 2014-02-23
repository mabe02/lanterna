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

import java.util.EnumMap;
import java.util.Map;

import com.googlecode.lanterna.terminal.TextColor;

/**
 * Extend this class to create your own themes. A {@code Theme} consists of several
 * {@code Theme.Definition}s, one for each {@code Theme.Category} value. When components are setting
 * their colors according to the theme, they do so by calling {@code TextGraphics.applyTheme}.
 *
 * @author Martin
 */
public class Theme {
    private static final Definition DEFAULT = new Definition(TextColor.ANSI.BLACK, TextColor.ANSI.WHITE, false);
    private static final Definition SELECTED = new Definition(TextColor.ANSI.WHITE, TextColor.ANSI.BLUE, true);
    private Map<Category, Definition> styles = new EnumMap<Category, Definition>(Category.class);
    private static final Theme DEFAULT_INSTANCE = new Theme();

    /**
     * Represents things which can be styled.
     */
    public enum Category {
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
        TEXTBOX_FOCUSED,
        PROGRESS_BAR_COMPLETED,
        PROGRESS_BAR_REMAINING
    }

    protected Theme() {
        setDefinition(Category.DIALOG_AREA, DEFAULT);
        setDefinition(Category.SCREEN_BACKGROUND, new Definition(TextColor.ANSI.CYAN, TextColor.ANSI.BLUE, true));
        setDefinition(Category.SHADOW, new Definition(TextColor.ANSI.BLACK, TextColor.ANSI.BLACK, true));
        setDefinition(Category.BORDER, new Definition(TextColor.ANSI.BLACK, TextColor.ANSI.WHITE, true));
        setDefinition(Category.RAISED_BORDER, new Definition(TextColor.ANSI.WHITE, TextColor.ANSI.WHITE, true));
        setDefinition(Category.BUTTON_LABEL_ACTIVE, new Definition(TextColor.ANSI.YELLOW, TextColor.ANSI.BLUE, true));
        setDefinition(Category.BUTTON_LABEL_INACTIVE, new Definition(TextColor.ANSI.BLACK, TextColor.ANSI.WHITE, true));
        setDefinition(Category.BUTTON_ACTIVE, SELECTED);
        setDefinition(Category.BUTTON_INACTIVE, DEFAULT);
        setDefinition(Category.LIST_ITEM, DEFAULT);
        setDefinition(Category.LIST_ITEM_SELECTED, SELECTED);
        setDefinition(Category.CHECKBOX, DEFAULT);
        setDefinition(Category.CHECKBOX_SELECTED, SELECTED);
        setDefinition(Category.TEXTBOX, SELECTED);
        setDefinition(Category.TEXTBOX_FOCUSED, new Definition(TextColor.ANSI.YELLOW, TextColor.ANSI.BLUE, true));
        setDefinition(Category.PROGRESS_BAR_COMPLETED, new Definition(TextColor.ANSI.GREEN, TextColor.ANSI.BLACK, false));
        setDefinition(Category.PROGRESS_BAR_REMAINING, new Definition(TextColor.ANSI.RED, TextColor.ANSI.BLACK, false));
    }

    public Theme.Definition getDefinition(Category category) {
        if (styles.containsKey(category) && styles.get(category) != null) {
            return styles.get(category);
        }

        return getDefault();
    }

    protected void setDefinition(Category category, Definition def) {
        if (def == null) {
            styles.remove(category);
        } else {
            styles.put(category, def);
        }
    }

    /**
     * Gets the default style to use when no Category-specific style is set.
     */
    protected Definition getDefaultStyle() {
        return DEFAULT;
    }

    /**
     * @deprecated use
     * {@code getDefaultStyle()} instead
     */
    @Deprecated
    protected Definition getDefault() {
        return getDefaultStyle();
    }

    /**
     * @deprecated use {@code getDefinition} instead.
     */
    @Deprecated
    protected Definition getDialogEmptyArea() {
        return getDefinition(Category.DIALOG_AREA);
    }

    /**
     * @deprecated use {@code getDefinition} instead.
     */
    @Deprecated
    protected Definition getScreenBackground() {
        return getDefinition(Category.SCREEN_BACKGROUND);
    }

    /**
     * @deprecated use {@code getDefinition} instead.
     */
    @Deprecated
    protected Definition getShadow() {
        return getDefinition(Category.SHADOW);
    }

    /**
     * @deprecated use {@code getDefinition} instead.
     */
    @Deprecated
    protected Definition getBorder() {
        return getDefinition(Category.BORDER);
    }

    /**
     * @deprecated use {@code getDefinition} instead.
     */
    @Deprecated
    protected Definition getRaisedBorder() {
        return getDefinition(Category.RAISED_BORDER);
    }

    /**
     * @deprecated use {@code getDefinition} instead.
     */
    @Deprecated
    protected Definition getButtonLabelActive() {
        return getDefinition(Category.BUTTON_LABEL_ACTIVE);
    }

    /**
     * @deprecated use {@code getDefinition} instead.
     */
    @Deprecated
    protected Definition getButtonLabelInactive() {
        return getDefinition(Category.BUTTON_LABEL_INACTIVE);
    }

    /**
     * @deprecated use {@code getDefinition} instead.
     */
    @Deprecated
    protected Definition getButtonActive() {
        return getDefinition(Category.BUTTON_ACTIVE);
    }

    /**
     * @deprecated use {@code getDefinition} instead.
     */
    @Deprecated
    protected Definition getButtonInactive() {
        return getDefinition(Category.BUTTON_INACTIVE);
    }

    /**
     * @deprecated use {@code getDefinition} instead.
     */
    @Deprecated
    protected Definition getItem() {
        return getDefinition(Category.LIST_ITEM);
    }

    /**
     * @deprecated use {@code getDefinition} instead.
     */
    @Deprecated
    protected Definition getItemSelected() {
        return getDefinition(Category.LIST_ITEM_SELECTED);
    }

    /**
     * @deprecated use {@code getDefinition} instead.
     */
    @Deprecated
    protected Definition getCheckBox() {
        return getDefinition(Category.CHECKBOX);
    }

    /**
     * @deprecated use {@code getDefinition} instead.
     */
    @Deprecated
    protected Definition getCheckBoxSelected() {
        return getDefinition(Category.CHECKBOX_SELECTED);
    }

    /**
     * @deprecated use {@code getDefinition} instead.
     */
    @Deprecated
    protected Definition getTextBoxFocused() {
        return getDefinition(Category.TEXTBOX_FOCUSED);
    }

    /**
     * @deprecated use {@code getDefinition} instead.
     */
    @Deprecated
    protected Definition getTextBox() {
        return getDefinition(Category.TEXTBOX);
    }

    public static Theme getDefaultTheme() {
        return DEFAULT_INSTANCE;
    }

    /**
     * A style definition encompassing colors and effects.
     */
    public static class Definition {

        private TextColor foreground;
        private TextColor background;
        private boolean highlighted;
        private boolean underlined;

        public Definition(TextColor foreground, TextColor background) {
            this(foreground, background, false);
        }

        public Definition(TextColor foreground, TextColor background, boolean highlighted) {
            this(foreground, background, highlighted, false);
        }

        public Definition(TextColor foreground, TextColor background, boolean highlighted, boolean underlined) {
            if (foreground == null) {
                throw new IllegalArgumentException("foreground TextColor cannot be null");
            }

            if (background == null) {
                throw new IllegalArgumentException("background TextColor cannot be null");
            }

            this.foreground = foreground;
            this.background = background;
            this.highlighted = highlighted;
            this.underlined = underlined;
        }

        public TextColor foreground() {
            return foreground;
        }

        public TextColor background() {
            return background;
        }

        public boolean isHighlighted() {
            return highlighted;
        }

        public boolean isUnderlined() {
            return underlined;
        }
    }
}
