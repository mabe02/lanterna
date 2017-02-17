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
 * Copyright (C) 2010-2017 Martin Berglund
 */
package com.googlecode.lanterna.graphics;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.table.Table;

import java.util.*;

/**
 * Very basic implementation of {@link Theme} that allows you to quickly define a theme in code. It is a very simple
 * implementation that doesn't implement any intelligent fallback based on class hierarchy or package names. If a
 * particular class has not been defined with an explicit override, it will get the default theme style definition.
 *
 * @author Martin
 */
public class SimpleTheme implements Theme {

    /**
     * Helper method that will quickly setup a new theme with some sensible component overrides.
     * @param activeIsBold Should focused components also use bold SGR style?
     * @param baseForeground The base foreground color of the theme
     * @param baseBackground The base background color of the theme
     * @param editableForeground Foreground color for editable components, or editable areas of components
     * @param editableBackground Background color for editable components, or editable areas of components
     * @param selectedForeground Foreground color for the selection marker when a component has multiple selection states
     * @param selectedBackground Background color for the selection marker when a component has multiple selection states
     * @param guiBackground Background color of the GUI, if this theme is assigned to the {@link TextGUI}
     * @return Assembled {@link SimpleTheme} using the parameters from above
     */
    public static SimpleTheme makeTheme(
            boolean activeIsBold,
            TextColor baseForeground,
            TextColor baseBackground,
            TextColor editableForeground,
            TextColor editableBackground,
            TextColor selectedForeground,
            TextColor selectedBackground,
            TextColor guiBackground) {

        SGR[] activeStyle = activeIsBold ? new SGR[]{SGR.BOLD} : new SGR[0];

        SimpleTheme theme = new SimpleTheme(baseForeground, baseBackground);
        theme.getDefaultDefinition().setSelected(baseBackground, baseForeground, activeStyle);
        theme.getDefaultDefinition().setActive(selectedForeground, selectedBackground, activeStyle);

        theme.addOverride(AbstractBorder.class, baseForeground, baseBackground)
                .setSelected(baseForeground, baseBackground, activeStyle);
        theme.addOverride(AbstractListBox.class, baseForeground, baseBackground)
                .setSelected(selectedForeground, selectedBackground, activeStyle);
        theme.addOverride(Button.class, baseForeground, baseBackground)
                .setActive(selectedForeground, selectedBackground, activeStyle)
                .setSelected(selectedForeground, selectedBackground, activeStyle);
        theme.addOverride(CheckBox.class, baseForeground, baseBackground)
                .setActive(selectedForeground, selectedBackground, activeStyle)
                .setPreLight(selectedForeground, selectedBackground, activeStyle)
                .setSelected(selectedForeground, selectedBackground, activeStyle);
        theme.addOverride(CheckBoxList.class, baseForeground, baseBackground)
                .setActive(selectedForeground, selectedBackground, activeStyle);
        theme.addOverride(ComboBox.class, baseForeground, baseBackground)
                .setActive(editableForeground, editableBackground, activeStyle)
                .setPreLight(editableForeground, editableBackground);
        theme.addOverride(DefaultWindowDecorationRenderer.class, baseForeground, baseBackground)
                .setActive(baseForeground, baseBackground, activeStyle);
        theme.addOverride(GUIBackdrop.class, baseForeground, guiBackground);
        theme.addOverride(RadioBoxList.class, baseForeground, baseBackground)
                .setActive(selectedForeground, selectedBackground, activeStyle);
        theme.addOverride(Table.class, baseForeground, baseBackground)
                .setActive(editableForeground, editableBackground, activeStyle)
                .setSelected(baseForeground, baseBackground);
        theme.addOverride(TextBox.class, editableForeground, editableBackground)
                .setActive(editableForeground, editableBackground, activeStyle)
                .setSelected(editableForeground, editableBackground, activeStyle);

        theme.setWindowPostRenderer(new WindowShadowRenderer());

        return theme;
    }

    private final Definition defaultDefinition;
    private final Map<Class<?>, Definition> overrideDefinitions;
    private WindowPostRenderer windowPostRenderer;
    private WindowDecorationRenderer windowDecorationRenderer;

    /**
     * Creates a new {@link SimpleTheme} object that uses the supplied constructor arguments as the default style
     * @param foreground Color to use as the foreground unless overridden
     * @param background Color to use as the background unless overridden
     * @param styles Extra SGR styles to apply unless overridden
     */
    public SimpleTheme(TextColor foreground, TextColor background, SGR... styles) {
        this.defaultDefinition = new Definition(new Style(foreground, background, styles));
        this.overrideDefinitions = new HashMap<Class<?>, Definition>();
        this.windowPostRenderer = null;
        this.windowDecorationRenderer = null;
    }

    @Override
    public synchronized Definition getDefaultDefinition() {
        return defaultDefinition;
    }

    @Override
    public synchronized Definition getDefinition(Class<?> clazz) {
        Definition definition = overrideDefinitions.get(clazz);
        if(definition == null) {
            return getDefaultDefinition();
        }
        return definition;
    }

    /**
     * Adds an override for a particular class, or overwrites a previously defined override.
     * @param clazz Class to override the theme for
     * @param foreground Color to use as the foreground color for this override style
     * @param background Color to use as the background color for this override style
     * @param styles SGR styles to apply for this override
     * @return The newly created {@link Definition} that corresponds to this override.
     */
    public synchronized Definition addOverride(Class<?> clazz, TextColor foreground, TextColor background, SGR... styles) {
        Definition definition = new Definition(new Style(foreground, background, styles));
        overrideDefinitions.put(clazz, definition);
        return definition;
    }

    @Override
    public synchronized WindowPostRenderer getWindowPostRenderer() {
        return windowPostRenderer;
    }

    /**
     * Changes the {@link WindowPostRenderer} this theme will return. If called with {@code null}, the theme returns no
     * post renderer and the GUI system will use whatever is the default.
     * @param windowPostRenderer Post-renderer to use along with this theme, or {@code null} to remove
     * @return Itself
     */
    public synchronized SimpleTheme setWindowPostRenderer(WindowPostRenderer windowPostRenderer) {
        this.windowPostRenderer = windowPostRenderer;
        return this;
    }

    @Override
    public synchronized WindowDecorationRenderer getWindowDecorationRenderer() {
        return windowDecorationRenderer;
    }

    /**
     * Changes the {@link WindowDecorationRenderer} this theme will return. If called with {@code null}, the theme
     * returns no decoration renderer and the GUI system will use whatever is the default.
     * @param windowDecorationRenderer Decoration renderer to use along with this theme, or {@code null} to remove
     * @return Itself
     */
    public synchronized SimpleTheme setWindowDecorationRenderer(WindowDecorationRenderer windowDecorationRenderer) {
        this.windowDecorationRenderer = windowDecorationRenderer;
        return this;
    }

    public interface RendererProvider<T extends Component> {
        ComponentRenderer<T> getRenderer(Class<T> type);
    }

    /**
     * Internal class inside {@link SimpleTheme} used to allow basic editing of the default style and the optional
     * overrides.
     */
    public static class Definition implements ThemeDefinition {
        private final ThemeStyle normal;
        private ThemeStyle preLight;
        private ThemeStyle selected;
        private ThemeStyle active;
        private ThemeStyle insensitive;
        private final Map<String, ThemeStyle> customStyles;
        private final Properties properties;
        private final Map<String, Character> characterMap;
        private final Map<Class<?>, RendererProvider<?>> componentRendererMap;
        private boolean cursorVisible;

        private Definition(ThemeStyle normal) {
            this.normal = normal;
            this.preLight = null;
            this.selected = null;
            this.active = null;
            this.insensitive = null;
            this.customStyles = new HashMap<String, ThemeStyle>();
            this.properties = new Properties();
            this.characterMap = new HashMap<String, Character>();
            this.componentRendererMap = new HashMap<Class<?>, RendererProvider<?>>();
            this.cursorVisible = true;
        }

        @Override
        public synchronized ThemeStyle getNormal() {
            return normal;
        }

        @Override
        public synchronized ThemeStyle getPreLight() {
            if(preLight == null) {
                return normal;
            }
            return preLight;
        }

        /**
         * Sets the theme definition style "prelight"
         * @param foreground Foreground color for this style
         * @param background Background color for this style
         * @param styles SGR styles to use
         * @return Itself
         */
        public synchronized Definition setPreLight(TextColor foreground, TextColor background, SGR... styles) {
            this.preLight = new Style(foreground, background, styles);
            return this;
        }

        @Override
        public synchronized ThemeStyle getSelected() {
            if(selected == null) {
                return normal;
            }
            return selected;
        }

        /**
         * Sets the theme definition style "selected"
         * @param foreground Foreground color for this style
         * @param background Background color for this style
         * @param styles SGR styles to use
         * @return Itself
         */
        public synchronized Definition setSelected(TextColor foreground, TextColor background, SGR... styles) {
            this.selected = new Style(foreground, background, styles);
            return this;
        }

        @Override
        public synchronized ThemeStyle getActive() {
            if(active == null) {
                return normal;
            }
            return active;
        }

        /**
         * Sets the theme definition style "active"
         * @param foreground Foreground color for this style
         * @param background Background color for this style
         * @param styles SGR styles to use
         * @return Itself
         */
        public synchronized Definition setActive(TextColor foreground, TextColor background, SGR... styles) {
            this.active = new Style(foreground, background, styles);
            return this;
        }

        @Override
        public synchronized ThemeStyle getInsensitive() {
            if(insensitive == null) {
                return normal;
            }
            return insensitive;
        }

        /**
         * Sets the theme definition style "insensitive"
         * @param foreground Foreground color for this style
         * @param background Background color for this style
         * @param styles SGR styles to use
         * @return Itself
         */
        public synchronized Definition setInsensitive(TextColor foreground, TextColor background, SGR... styles) {
            this.insensitive = new Style(foreground, background, styles);
            return this;
        }

        @Override
        public synchronized ThemeStyle getCustom(String name) {
            return customStyles.get(name);
        }

        @Override
        public synchronized ThemeStyle getCustom(String name, ThemeStyle defaultValue) {
            ThemeStyle themeStyle = customStyles.get(name);
            if(themeStyle == null) {
                return defaultValue;
            }
            return themeStyle;
        }

        /**
         * Adds a custom definition style to the theme using the supplied name. This will be returned using the matching
         * call to {@link Definition#getCustom(String)}.
         * @param name Name of the custom style
         * @param foreground Foreground color for this style
         * @param background Background color for this style
         * @param styles SGR styles to use
         * @return Itself
         */
        public synchronized Definition setCustom(String name, TextColor foreground, TextColor background, SGR... styles) {
            customStyles.put(name, new Style(foreground, background, styles));
            return this;
        }

        @Override
        public synchronized boolean getBooleanProperty(String name, boolean defaultValue) {
            return Boolean.parseBoolean(properties.getProperty(name, Boolean.toString(defaultValue)));
        }

        /**
         * Attaches a boolean value property to this {@link SimpleTheme} that will be returned if calling
         * {@link Definition#getBooleanProperty(String, boolean)} with the same name.
         * @param name Name of the property
         * @param value Value to attach to the property name
         * @return Itself
         */
        public synchronized Definition setBooleanProperty(String name, boolean value) {
            properties.setProperty(name, Boolean.toString(value));
            return this;
        }

        @Override
        public synchronized boolean isCursorVisible() {
            return cursorVisible;
        }

        /**
         * Sets the value that suggests if the cursor should be visible or not (it's still up to the component renderer
         * if it's going to honour this or not).
         * @param cursorVisible If {@code true} then this theme definition would like the text cursor to be displayed,
         *                      {@code false} if not.
         * @return Itself
         */
        public synchronized Definition setCursorVisible(boolean cursorVisible) {
            this.cursorVisible = cursorVisible;
            return this;
        }

        @Override
        public synchronized char getCharacter(String name, char fallback) {
            Character character = characterMap.get(name);
            if(character == null) {
                return fallback;
            }
            return character;
        }

        /**
         * Stores a character value in this definition under a specific name. This is used to customize the appearance
         * of certain components. It is returned with call to {@link Definition#getCharacter(String, char)} with the
         * same name.
         * @param name Symbolic name for the character
         * @param character Character to attach to the symbolic name
         * @return Itself
         */
        public synchronized Definition setCharacter(String name, char character) {
            characterMap.put(name, character);
            return this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public synchronized <T extends Component> ComponentRenderer<T> getRenderer(Class<T> type) {
            RendererProvider<T> rendererProvider = (RendererProvider<T>)componentRendererMap.get(type);
            if(rendererProvider == null) {
                return null;
            }
            return rendererProvider.getRenderer(type);
        }

        /**
         * Registered a callback to get a custom {@link ComponentRenderer} for a particular class. Use this to make a
         * certain component (built-in or external) to use a custom renderer.
         * @param type Class for which to invoke the callback and return the {@link ComponentRenderer}
         * @param rendererProvider Callback to invoke when getting a {@link ComponentRenderer}
         * @param <T> Type of class
         * @return Itself
         */
        public synchronized <T extends Component> Definition setRenderer(Class<T> type, RendererProvider<T> rendererProvider) {
            if(rendererProvider == null) {
                componentRendererMap.remove(type);
            }
            else {
                componentRendererMap.put(type, rendererProvider);
            }
            return this;
        }
    }

    private static class Style implements ThemeStyle {
        private TextColor foreground;
        private TextColor background;
        private EnumSet<SGR> sgrs;

        private Style(TextColor foreground, TextColor background, SGR... sgrs) {
            if(foreground == null) {
                throw new IllegalArgumentException("Cannot set SimpleTheme's style foreground to null");
            }
            if(background == null) {
                throw new IllegalArgumentException("Cannot set SimpleTheme's style background to null");
            }
            this.foreground = foreground;
            this.background = background;
            this.sgrs = EnumSet.noneOf(SGR.class);
            this.sgrs.addAll(Arrays.asList(sgrs));
        }

        @Override
        public synchronized TextColor getForeground() {
            return foreground;
        }

        @Override
        public synchronized TextColor getBackground() {
            return background;
        }

        @Override
        public synchronized EnumSet<SGR> getSGRs() {
            return EnumSet.copyOf(sgrs);
        }
    }
}
