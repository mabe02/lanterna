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
package com.googlecode.lanterna.bundle;

import com.googlecode.lanterna.graphics.PropertyTheme;
import com.googlecode.lanterna.graphics.Theme;
import com.googlecode.lanterna.gui2.AbstractTextGUI;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Catalog of available themes, this class will initially contain the themes bundled with Lanterna but it is possible to
 * add additional themes as well.
 */
public class LanternaThemes {
    private LanternaThemes() {
        // No instantiation
    }

    private static final ConcurrentHashMap<String, Theme> REGISTERED_THEMES = new ConcurrentHashMap<String, Theme>();

    static {
        registerTheme("default", new DefaultTheme());
        registerPropTheme("bigsnake", loadPropTheme("bigsnake-theme.properties"));
        registerPropTheme("businessmachine", loadPropTheme("businessmachine-theme.properties"));
        registerPropTheme("conqueror", loadPropTheme("conqueror-theme.properties"));
        registerPropTheme("defrost", loadPropTheme("defrost-theme.properties"));
        registerPropTheme("blaster", loadPropTheme("blaster-theme.properties"));
    }

    /**
     * Returns a collection of all themes registered with this class, by their name. To get the associated {@link Theme}
     * object, please use {@link #getRegisteredTheme(String)}.
     * @return Collection of theme names
     */
    public static Collection<String> getRegisteredThemes() {
        return new ArrayList<String>(REGISTERED_THEMES.keySet());
    }

    /**
     * Returns the {@link Theme} registered with this class under {@code name}, or {@code null} if there is no such
     * registration.
     * @param name Name of the theme to retrieve
     * @return {@link Theme} registered with the supplied name, or {@code null} if none
     */
    public static Theme getRegisteredTheme(String name) {
        return REGISTERED_THEMES.get(name);
    }

    /**
     * Registers a {@link Theme} with this class under a certain name so that calling
     * {@link #getRegisteredTheme(String)} on that name will return this theme and calling
     * {@link #getRegisteredThemes()} will return a collection including this name.
     * @param name Name to register the theme under
     * @param theme Theme to register with this name
     */
    public static void registerTheme(String name, Theme theme) {
        if(theme == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        else if(name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        Theme result = REGISTERED_THEMES.putIfAbsent(name, theme);
        if(result != null && result != theme) {
            throw new IllegalArgumentException("There is already a theme registered with the name '" + name + "'");
        }
    }

    /**
     * Returns lanterna's default theme which is used if no other theme is selected.
     * @return Lanterna's default theme, as a {@link Theme}
     */
    public static Theme getDefaultTheme() {
        return REGISTERED_THEMES.get("default");
    }

    private static void registerPropTheme(String name, Properties properties) {
        if(properties != null) {
            registerTheme(name, new PropertyTheme(properties, false));
        }
    }

    private static Properties loadPropTheme(String resourceFileName) {
        Properties properties = new Properties();
        try {
            ClassLoader classLoader = AbstractTextGUI.class.getClassLoader();
            InputStream resourceAsStream = classLoader.getResourceAsStream(resourceFileName);
            if(resourceAsStream == null) {
                resourceAsStream = new FileInputStream("src/main/resources/" + resourceFileName);
            }
            properties.load(resourceAsStream);
            resourceAsStream.close();
            return properties;
        }
        catch(IOException e) {
            if("default-theme.properties".equals(resourceFileName)) {
                throw new RuntimeException("Unable to load the default theme", e);
            }
            return null;
        }
    }
}
