package com.googlecode.lanterna.graphics;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.WindowDecorationRenderer;
import com.googlecode.lanterna.gui2.WindowPostRenderer;

import java.util.Properties;

/**
 * {@link Theme} implementation that stores the theme definition in a regular java Properties object. The format is:
 * <pre>
 *     foreground = black
 *     background = white
 *     sgr =
 *     com.mypackage.mycomponent.MyClass.foreground = yellow
 *     com.mypackage.mycomponent.MyClass.background = white
 *     com.mypackage.mycomponent.MyClass.sgr =
 *     com.mypackage.mycomponent.MyClass.foreground[ACTIVE] = red
 *     com.mypackage.mycomponent.MyClass.background[ACTIVE] = black
 *     com.mypackage.mycomponent.MyClass.sgr[ACTIVE] = bold
 *     ...
 * </pre>
 *
 * See the documentation on {@link Theme} for further information about different style categories that can be assigned.
 * The foreground, background and sgr entries without a class specifier will be tied to the global fallback and is used
 * if the libraries tries to apply a theme style that isn't specified in the Properties object and there is no other
 * superclass specified either.
 *
 */
public class PropertyTheme extends AbstractTheme {
    /**
     * Creates a new {@code PropertyTheme} that is initialized by the properties passed in. If the properties refer to
     * a class that cannot be resolved, it will throw {@code IllegalArgumentException}.
     * @param properties Properties to initialize this theme with
     */
    public PropertyTheme(Properties properties) {
        this(properties, false);
    }

    /**
     * Creates a new {@code PropertyTheme} that is initialized by the properties value and optionally prevents it from
     * throwing an exception if there are invalid definitions in the properties object.
     * @param properties Properties to initialize this theme with
     * @param ignoreUnknownClasses If {@code true}, will not throw an exception if there is an invalid entry in the
     *                             properties object
     */
    public PropertyTheme(Properties properties, boolean ignoreUnknownClasses) {

        super(stringToClass(properties.getProperty("postrenderer", ""), WindowPostRenderer.class),
                stringToClass(properties.getProperty("windowdecoration", ""), WindowDecorationRenderer.class));

        for(String key: properties.stringPropertyNames()) {
            String definition = getDefinition(key);
            if(!addStyle(definition, getStyle(key), properties.getProperty(key))) {
                if(!ignoreUnknownClasses) {
                    throw new IllegalArgumentException("Unknown class encountered when parsing theme: '" + definition + "'");
                }
            }
        }
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
}
