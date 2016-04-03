package com.googlecode.lanterna.bundle;

import com.googlecode.lanterna.graphics.PropertiesTheme;
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
        registerPropTheme("default", loadPropTheme("default-theme.properties"));
        registerPropTheme("bigsnake", loadPropTheme("bigsnake-theme.properties"));
        registerPropTheme("businessmachine", loadPropTheme("businessmachine-theme.properties"));
        registerPropTheme("conqueror", loadPropTheme("conqueror-theme.properties"));
        registerPropTheme("defrost", loadPropTheme("defrost-theme.properties"));
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
        if(REGISTERED_THEMES.putIfAbsent(name, theme) != theme) {
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
            registerTheme(name, new PropertiesTheme(properties));
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
