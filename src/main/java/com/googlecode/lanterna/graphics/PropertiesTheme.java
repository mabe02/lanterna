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
 * Copyright (C) 2010-2016 Martin
 */
package com.googlecode.lanterna.graphics;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.ComponentRenderer;
import com.googlecode.lanterna.gui2.WindowDecorationRenderer;
import com.googlecode.lanterna.gui2.WindowPostRenderer;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This implementation of Theme reads its definitions from a {@code Properties} object.
 * @deprecated Use {@link PropertyTheme} instead, is behaves more like you would expect
 * @author Martin
 */
@Deprecated
public final class PropertiesTheme implements Theme {
    private static final String STYLE_NORMAL = "";
    private static final String STYLE_PRELIGHT = "PRELIGHT";
    private static final String STYLE_SELECTED = "SELECTED";
    private static final String STYLE_ACTIVE = "ACTIVE";
    private static final String STYLE_INSENSITIVE = "INSENSITIVE";
    private static final Pattern STYLE_FORMAT = Pattern.compile("([a-zA-Z]+)(\\[([a-zA-Z0-9-_]+)\\])?");

    private final ThemeTreeNode rootNode;
    private final WindowPostRenderer windowPostRenderer;
    private final WindowDecorationRenderer windowDecorationRenderer;

    /**
     * Creates a new {@code PropertiesTheme} that is initialized by the properties value
     * @param properties Properties to initialize this theme with
     */
    public PropertiesTheme(Properties properties) {
        rootNode = new ThemeTreeNode(null);
        rootNode.foregroundMap.put(STYLE_NORMAL, TextColor.ANSI.WHITE);
        rootNode.backgroundMap.put(STYLE_NORMAL, TextColor.ANSI.BLACK);
        windowPostRenderer = (WindowPostRenderer)instanceByClassName(properties.getProperty("postrenderer", ""));
        windowDecorationRenderer = (WindowDecorationRenderer)instanceByClassName(properties.getProperty("windowdecoration", ""));

        for(String key: properties.stringPropertyNames()) {
            String definition = getDefinition(key);
            ThemeTreeNode node = getNode(definition);
            node.apply(getStyle(key), properties.getProperty(key));
        }
    }

    private ThemeTreeNode getNode(String definition) {
        ThemeTreeNode parentNode;
        if(definition.equals("")) {
            return rootNode;
        }
        else if(definition.contains(".")) {
            String parent = definition.substring(0, definition.lastIndexOf("."));
            parentNode = getNode(parent);
            definition = definition.substring(definition.lastIndexOf(".") + 1);
        }
        else {
            parentNode = rootNode;
        }
        if(!parentNode.childMap.containsKey(definition)) {
            parentNode.childMap.put(definition, new ThemeTreeNode(parentNode));
        }
        return parentNode.childMap.get(definition);
    }

    private String getDefinition(String propertyName) {
        if(!propertyName.contains(".")) {
            return "";
        }
        else {
            return propertyName.substring(0, propertyName.lastIndexOf("."));
        }
    }

    private String getStyle(String propertyName) {
        if(!propertyName.contains(".")) {
            return propertyName;
        }
        else {
            return propertyName.substring(propertyName.lastIndexOf(".") + 1);
        }
    }

    @Override
    public ThemeDefinition getDefaultDefinition() {
        return new DefinitionImpl(Collections.singletonList(rootNode));
    }

    @Override
    public ThemeDefinition getDefinition(Class<?> clazz) {
        String name = clazz.getName();
        List<ThemeTreeNode> path = new ArrayList<ThemeTreeNode>();
        ThemeTreeNode currentNode = rootNode;
        while(!name.equals("")) {
            path.add(currentNode);
            String nextNodeName = name;
            if(nextNodeName.contains(".")) {
                nextNodeName = nextNodeName.substring(0, name.indexOf("."));
                name = name.substring(name.indexOf(".") + 1);
            }
            if(currentNode.childMap.containsKey(nextNodeName)) {
                currentNode = currentNode.childMap.get(nextNodeName);
            }
            else {
                break;
            }
        }
        return new DefinitionImpl(path);
    }

    @Override
    public WindowPostRenderer getWindowPostRenderer() {
        return windowPostRenderer;
    }

    @Override
    public WindowDecorationRenderer getWindowDecorationRenderer() {
        return windowDecorationRenderer;
    }

    private static Object instanceByClassName(String className) {
        if(className == null || className.trim().isEmpty()) {
            return null;
        }
        try {
            return Class.forName(className).newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private class DefinitionImpl implements ThemeDefinition {
        final List<ThemeTreeNode> path;

        DefinitionImpl(List<ThemeTreeNode> path) {
            this.path = path;
        }

        @Override
        public ThemeStyle getNormal() {
            return new StyleImpl(path, STYLE_NORMAL);
        }

        @Override
        public ThemeStyle getPreLight() {
            return new StyleImpl(path, STYLE_PRELIGHT);
        }

        @Override
        public ThemeStyle getSelected() {
            return new StyleImpl(path, STYLE_SELECTED);
        }

        @Override
        public ThemeStyle getActive() {
            return new StyleImpl(path, STYLE_ACTIVE);
        }

        @Override
        public ThemeStyle getInsensitive() {
            return new StyleImpl(path, STYLE_INSENSITIVE);
        }

        @Override
        public ThemeStyle getCustom(String name) {
            ThemeTreeNode lastElement = path.get(path.size() - 1);
            if(lastElement.sgrMap.containsKey(name) ||
                    lastElement.foregroundMap.containsKey(name) ||
                    lastElement.backgroundMap.containsKey(name)) {
                return new StyleImpl(path, name);
            }
            return null;
        }

        @Override
        public ThemeStyle getCustom(String name, ThemeStyle defaultValue) {
            ThemeStyle customStyle = getCustom(name);
            if(customStyle == null) {
                customStyle = defaultValue;
            }
            return customStyle;
        }

        @Override
        public char getCharacter(String name, char fallback) {
            Character character = path.get(path.size() - 1).characterMap.get(name);
            if(character == null) {
                return fallback;
            }
            return character;
        }

        @Override
        public boolean isCursorVisible() {
            return path.get(path.size() - 1).cursorVisible;
        }

        @Override
        public boolean getBooleanProperty(String name, boolean defaultValue) {
            String propertyValue = path.get(path.size() - 1).propertyMap.get(name);
            if(propertyValue == null) {
                return defaultValue;
            }
            return Boolean.parseBoolean(propertyValue);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends Component> ComponentRenderer<T> getRenderer(Class<T> type) {
            return (ComponentRenderer<T>)instanceByClassName(path.get(path.size() - 1).renderer);
        }
    }

    private class StyleImpl implements ThemeStyle {
        private final List<ThemeTreeNode> path;
        private final String name;

        private StyleImpl(List<ThemeTreeNode> path, String name) {
            this.path = path;
            this.name = name;
        }

        @Override
        public TextColor getForeground() {
            ListIterator<ThemeTreeNode> iterator = path.listIterator(path.size());
            while(iterator.hasPrevious()) {
                ThemeTreeNode node = iterator.previous();
                if(node.foregroundMap.containsKey(name)) {
                    return node.foregroundMap.get(name);
                }
            }
            if(!name.equals(STYLE_NORMAL)) {
                return new StyleImpl(path, STYLE_NORMAL).getForeground();
            }
            return TextColor.ANSI.WHITE;
        }

        @Override
        public TextColor getBackground() {
            ListIterator<ThemeTreeNode> iterator = path.listIterator(path.size());
            while(iterator.hasPrevious()) {
                ThemeTreeNode node = iterator.previous();
                if(node.backgroundMap.containsKey(name)) {
                    return node.backgroundMap.get(name);
                }
            }
            if(!name.equals(STYLE_NORMAL)) {
                return new StyleImpl(path, STYLE_NORMAL).getBackground();
            }
            return TextColor.ANSI.BLACK;
        }

        @Override
        public EnumSet<SGR> getSGRs() {
            ListIterator<ThemeTreeNode> iterator = path.listIterator(path.size());
            while(iterator.hasPrevious()) {
                ThemeTreeNode node = iterator.previous();
                if(node.sgrMap.containsKey(name)) {
                    return EnumSet.copyOf(node.sgrMap.get(name));
                }
            }
            if(!name.equals(STYLE_NORMAL)) {
                return EnumSet.copyOf(new StyleImpl(path, STYLE_NORMAL).getSGRs());
            }
            return EnumSet.noneOf(SGR.class);
        }
    }

    private static class ThemeTreeNode {
        //private final ThemeTreeNode parent;
        private final Map<String, ThemeTreeNode> childMap;
        private final Map<String, TextColor> foregroundMap;
        private final Map<String, TextColor> backgroundMap;
        private final Map<String, EnumSet<SGR>> sgrMap;
        private final Map<String, Character> characterMap;
        private final Map<String, String> propertyMap;
        private boolean cursorVisible;
        private String renderer;

        private ThemeTreeNode(ThemeTreeNode parent) {
            //this.parent = parent;
            childMap = new HashMap<String, ThemeTreeNode>();
            foregroundMap = new HashMap<String, TextColor>();
            backgroundMap = new HashMap<String, TextColor>();
            sgrMap = new HashMap<String, EnumSet<SGR>>();
            characterMap = new HashMap<String, Character>();
            propertyMap = new HashMap<String, String>();
            cursorVisible = true;
            renderer = null;
        }

        public void apply(String style, String value) {
            value = value.trim();
            Matcher matcher = STYLE_FORMAT.matcher(style);
            if(!matcher.matches()) {
                throw new IllegalArgumentException("Unknown style declaration: " + style);
            }
            String styleComponent = matcher.group(1);
            String group = matcher.groupCount() > 2 ? matcher.group(3) : null;
            if(styleComponent.toLowerCase().trim().equals("foreground")) {
                foregroundMap.put(getCategory(group), parseValue(value));
            }
            else if(styleComponent.toLowerCase().trim().equals("background")) {
                backgroundMap.put(getCategory(group), parseValue(value));
            }
            else if(styleComponent.toLowerCase().trim().equals("sgr")) {
                sgrMap.put(getCategory(group), parseSGR(value));
            }
            else if(styleComponent.toLowerCase().trim().equals("char")) {
                characterMap.put(getCategory(group), value.isEmpty() ? ' ' : value.charAt(0));
            }
            else if(styleComponent.toLowerCase().trim().equals("cursor")) {
                cursorVisible = Boolean.parseBoolean(value);
            }
            else if(styleComponent.toLowerCase().trim().equals("property")) {
                propertyMap.put(getCategory(group), value.isEmpty() ? null : value.trim());
            }
            else if(styleComponent.toLowerCase().trim().equals("renderer")) {
                renderer = value.trim().isEmpty() ? null : value.trim();
            }
            else if(styleComponent.toLowerCase().trim().equals("postrenderer") ||
                    styleComponent.toLowerCase().trim().equals("windowdecoration")) {
                // Don't do anything with this now, we might use it later
            }
            else {
                throw new IllegalArgumentException("Unknown style component \"" + styleComponent + "\" in style \"" + style + "\"");
            }
        }

        private TextColor parseValue(String value) {
            return TextColor.Factory.fromString(value);
        }

        private EnumSet<SGR> parseSGR(String value) {
            value = value.trim();
            String[] sgrEntries = value.split(",");
            EnumSet<SGR> sgrSet = EnumSet.noneOf(SGR.class);
            for(String entry: sgrEntries) {
                entry = entry.trim().toUpperCase();
                if(!entry.isEmpty()) {
                    try {
                        sgrSet.add(SGR.valueOf(entry));
                    }
                    catch(IllegalArgumentException e) {
                        throw new IllegalArgumentException("Unknown SGR code \"" + entry + "\"", e);
                    }
                }
            }
            return sgrSet;
        }

        private String getCategory(String group) {
            if(group == null) {
                return STYLE_NORMAL;
            }
            for(String style: Arrays.asList(STYLE_ACTIVE, STYLE_INSENSITIVE, STYLE_PRELIGHT, STYLE_NORMAL, STYLE_SELECTED)) {
                if(group.toUpperCase().equals(style)) {
                    return style;
                }
            }
            return group;
        }
    }
}
