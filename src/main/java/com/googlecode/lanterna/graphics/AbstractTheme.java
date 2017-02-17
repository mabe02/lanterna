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
import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.ComponentRenderer;
import com.googlecode.lanterna.gui2.WindowDecorationRenderer;
import com.googlecode.lanterna.gui2.WindowPostRenderer;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract {@link Theme} implementation that manages a hierarchical tree of theme nodes ties to Class objects.
 * Sub-classes will inherit their theme properties from super-class definitions, the java.lang.Object class is
 * considered the root of the tree and as such is the fallback for all other classes.
 * <p>
 * You normally use this class through {@link PropertyTheme}, which is the default implementation bundled with Lanterna.
 * @author Martin
 */
public abstract class AbstractTheme implements Theme {
    private static final String STYLE_NORMAL = "";
    private static final String STYLE_PRELIGHT = "PRELIGHT";
    private static final String STYLE_SELECTED = "SELECTED";
    private static final String STYLE_ACTIVE = "ACTIVE";
    private static final String STYLE_INSENSITIVE = "INSENSITIVE";
    private static final Pattern STYLE_FORMAT = Pattern.compile("([a-zA-Z]+)(\\[([a-zA-Z0-9-_]+)])?");

    private final ThemeTreeNode rootNode;
    private final WindowPostRenderer windowPostRenderer;
    private final WindowDecorationRenderer windowDecorationRenderer;

    protected AbstractTheme(WindowPostRenderer postRenderer,
                            WindowDecorationRenderer decorationRenderer) {

        this.rootNode = new ThemeTreeNode(Object.class, null);
        this.windowPostRenderer = postRenderer;
        this.windowDecorationRenderer = decorationRenderer;

        rootNode.foregroundMap.put(STYLE_NORMAL, TextColor.ANSI.WHITE);
        rootNode.backgroundMap.put(STYLE_NORMAL, TextColor.ANSI.BLACK);
    }

    protected boolean addStyle(String definition, String style, String value) {
        ThemeTreeNode node = getNode(definition);
        if(node == null) {
            return false;
        }
        node.apply(style, value);
        return true;
    }

    private ThemeTreeNode getNode(String definition) {
        try {
            if(definition == null || definition.trim().isEmpty()) {
                return getNode(Object.class);
            }
            else {
                return getNode(Class.forName(definition));
            }
        }
        catch(ClassNotFoundException e) {
            return null;
        }
    }

    private ThemeTreeNode getNode(Class<?> definition) {
        if(definition == Object.class) {
            return rootNode;
        }
        ThemeTreeNode parent = getNode(definition.getSuperclass());
        if(parent.childMap.containsKey(definition)) {
            return parent.childMap.get(definition);
        }

        ThemeTreeNode node = new ThemeTreeNode(definition, parent);
        parent.childMap.put(definition, node);
        return node;
    }

    @Override
    public ThemeDefinition getDefaultDefinition() {
        return new DefinitionImpl(rootNode);
    }

    @Override
    public ThemeDefinition getDefinition(Class<?> clazz) {
        LinkedList<Class<?>> hierarchy = new LinkedList<Class<?>>();
        while(clazz != null && clazz != Object.class) {
            hierarchy.addFirst(clazz);
            clazz = clazz.getSuperclass();
        }

        ThemeTreeNode node = rootNode;
        for(Class<?> aClass : hierarchy) {
            if(node.childMap.containsKey(aClass)) {
                node = node.childMap.get(aClass);
            }
            else {
                break;
            }
        }
        return new DefinitionImpl(node);
    }

    @Override
    public WindowPostRenderer getWindowPostRenderer() {
        return windowPostRenderer;
    }

    @Override
    public WindowDecorationRenderer getWindowDecorationRenderer() {
        return windowDecorationRenderer;
    }

    protected static Object instanceByClassName(String className) {
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

    /**
     * Returns a list of redundant theme entries in this theme. A redundant entry means that it doesn't need to be
     * specified because there is a parent node in the hierarchy which has the same property so if the redundant entry
     * wasn't there, the parent node would be picked up and the end result would be the same.
     * @return List of redundant theme entries
     */
    public List<String> findRedundantDeclarations() {
        List<String> result = new ArrayList<String>();
        for(ThemeTreeNode node: rootNode.childMap.values()) {
            findRedundantDeclarations(result, node);
        }
        Collections.sort(result);
        return result;
    }

    private void findRedundantDeclarations(List<String> result, ThemeTreeNode node) {
        for(String style: node.foregroundMap.keySet()) {
            String formattedStyle = "[" + style + "]";
            if(formattedStyle.length() == 2) {
                formattedStyle = "";
            }
            TextColor color = node.foregroundMap.get(style);
            TextColor colorFromParent = new StyleImpl(node.parent, style).getForeground();
            if(color.equals(colorFromParent)) {
                result.add(node.clazz.getName() + ".foreground" + formattedStyle);
            }
        }
        for(String style: node.backgroundMap.keySet()) {
            String formattedStyle = "[" + style + "]";
            if(formattedStyle.length() == 2) {
                formattedStyle = "";
            }
            TextColor color = node.backgroundMap.get(style);
            TextColor colorFromParent = new StyleImpl(node.parent, style).getBackground();
            if(color.equals(colorFromParent)) {
                result.add(node.clazz.getName() + ".background" + formattedStyle);
            }
        }
        for(String style: node.sgrMap.keySet()) {
            String formattedStyle = "[" + style + "]";
            if(formattedStyle.length() == 2) {
                formattedStyle = "";
            }
            EnumSet<SGR> sgrs = node.sgrMap.get(style);
            EnumSet<SGR> sgrsFromParent = new StyleImpl(node.parent, style).getSGRs();
            if(sgrs.equals(sgrsFromParent)) {
                result.add(node.clazz.getName() + ".sgr" + formattedStyle);
            }
        }

        for(ThemeTreeNode childNode: node.childMap.values()) {
            findRedundantDeclarations(result, childNode);
        }
    }

    private class DefinitionImpl implements ThemeDefinition {
        final ThemeTreeNode node;

        public DefinitionImpl(ThemeTreeNode node) {
            this.node = node;
        }

        @Override
        public ThemeStyle getNormal() {
            return new StyleImpl(node, STYLE_NORMAL);
        }

        @Override
        public ThemeStyle getPreLight() {
            return new StyleImpl(node, STYLE_PRELIGHT);
        }

        @Override
        public ThemeStyle getSelected() {
            return new StyleImpl(node, STYLE_SELECTED);
        }

        @Override
        public ThemeStyle getActive() {
            return new StyleImpl(node, STYLE_ACTIVE);
        }

        @Override
        public ThemeStyle getInsensitive() {
            return new StyleImpl(node, STYLE_INSENSITIVE);
        }

        @Override
        public ThemeStyle getCustom(String name) {
            return new StyleImpl(node, name);
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
            Character character = node.characterMap.get(name);
            if(character == null) {
                if(node == rootNode) {
                    return fallback;
                }
                else {
                    return new DefinitionImpl(node.parent).getCharacter(name, fallback);
                }
            }
            return character;
        }

        @Override
        public boolean isCursorVisible() {
            Boolean cursorVisible = node.cursorVisible;
            if(cursorVisible == null) {
                if(node == rootNode) {
                    return true;
                }
                else {
                    return new DefinitionImpl(node.parent).isCursorVisible();
                }
            }
            return cursorVisible;
        }

        @Override
        public boolean getBooleanProperty(String name, boolean defaultValue) {
            String propertyValue = node.propertyMap.get(name);
            if(propertyValue == null) {
                if(node == rootNode) {
                    return defaultValue;
                }
                else {
                    return new DefinitionImpl(node.parent).getBooleanProperty(name, defaultValue);
                }
            }
            return Boolean.parseBoolean(propertyValue);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends Component> ComponentRenderer<T> getRenderer(Class<T> type) {
            String rendererClass = node.renderer;
            if(rendererClass == null) {
                if(node == rootNode) {
                    return null;
                }
                else {
                    return new DefinitionImpl(node.parent).getRenderer(type);
                }
            }
            return (ComponentRenderer<T>)instanceByClassName(rendererClass);
        }
    }

    private class StyleImpl implements ThemeStyle {
        private final ThemeTreeNode styleNode;
        private final String name;

        private StyleImpl(ThemeTreeNode node, String name) {
            this.styleNode = node;
            this.name = name;
        }

        @Override
        public TextColor getForeground() {
            ThemeTreeNode node = styleNode;
            while(node != null) {
                if(node.foregroundMap.containsKey(name)) {
                    return node.foregroundMap.get(name);
                }
                node = node.parent;
            }
            TextColor fallback = rootNode.foregroundMap.get(STYLE_NORMAL);
            if(fallback == null) {
                fallback = TextColor.ANSI.WHITE;
            }
            return fallback;
        }

        @Override
        public TextColor getBackground() {
            ThemeTreeNode node = styleNode;
            while(node != null) {
                if(node.backgroundMap.containsKey(name)) {
                    return node.backgroundMap.get(name);
                }
                node = node.parent;
            }
            TextColor fallback = rootNode.backgroundMap.get(STYLE_NORMAL);
            if(fallback == null) {
                fallback = TextColor.ANSI.BLACK;
            }
            return fallback;
        }

        @Override
        public EnumSet<SGR> getSGRs() {
            ThemeTreeNode node = styleNode;
            while(node != null) {
                if(node.sgrMap.containsKey(name)) {
                    return EnumSet.copyOf(node.sgrMap.get(name));
                }
                node = node.parent;
            }
            EnumSet<SGR> fallback = rootNode.sgrMap.get(STYLE_NORMAL);
            if(fallback == null) {
                fallback = EnumSet.noneOf(SGR.class);
            }
            return EnumSet.copyOf(fallback);
        }
    }

    private static class ThemeTreeNode {
        private final Class<?> clazz;
        private final ThemeTreeNode parent;
        private final Map<Class<?>, ThemeTreeNode> childMap;
        private final Map<String, TextColor> foregroundMap;
        private final Map<String, TextColor> backgroundMap;
        private final Map<String, EnumSet<SGR>> sgrMap;
        private final Map<String, Character> characterMap;
        private final Map<String, String> propertyMap;
        private Boolean cursorVisible;
        private String renderer;

        private ThemeTreeNode(Class<?> clazz, ThemeTreeNode parent) {
            this.clazz = clazz;
            this.parent = parent;
            this.childMap = new HashMap<Class<?>, ThemeTreeNode>();
            this.foregroundMap = new HashMap<String, TextColor>();
            this.backgroundMap = new HashMap<String, TextColor>();
            this.sgrMap = new HashMap<String, EnumSet<SGR>>();
            this.characterMap = new HashMap<String, Character>();
            this.propertyMap = new HashMap<String, String>();
            this.cursorVisible = true;
            this.renderer = null;
        }

        private void apply(String style, String value) {
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
