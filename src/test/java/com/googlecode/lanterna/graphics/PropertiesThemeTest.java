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
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import static org.junit.Assert.*;

public class PropertiesThemeTest {

    @Test
    public void emptyPropertiesGivesValidResults() {
        Properties properties = new Properties();
        PropertiesTheme theme = new PropertiesTheme(properties);
        ThemeDefinition definition = theme.getDefaultDefinition();
        assertNotNull(definition.getNormal());
        assertNotNull(definition.getActive());
        assertNotNull(definition.getInsensitive());
        assertNotNull(definition.getPreLight());
        assertNotNull(definition.getSelected());
        assertNull(definition.getCustom("DOESNTEXIST"));

        ThemeStyle style = definition.getNormal();
        assertNotNull(style.getForeground());
        assertNotNull(style.getBackground());
        assertNotNull(style.getSGRs());
    }

    @Test
    public void defaultThemeWorks() throws IOException {
        InputStream inputStream = new FileInputStream("src/main/resources/default-theme.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        inputStream.close();

        PropertiesTheme theme = new PropertiesTheme(properties);
        ThemeDefinition defaultDefinition = theme.getDefaultDefinition();
        assertEquals(TextColor.ANSI.WHITE, defaultDefinition.getNormal().getForeground());
        assertEquals(TextColor.ANSI.BLACK, defaultDefinition.getNormal().getBackground());
        assertEquals(0, defaultDefinition.getNormal().getSGRs().size());
        assertEquals(TextColor.ANSI.YELLOW, defaultDefinition.getSelected().getForeground());
        assertEquals(TextColor.ANSI.BLUE, defaultDefinition.getSelected().getBackground());
        assertEquals(Collections.singletonList(SGR.BOLD), new ArrayList<SGR>(defaultDefinition.getSelected().getSGRs()));
    }

    @Test
    public void classWithoutThemePicksUpParentPackagesTheme() {
        Properties properties = new Properties();
        properties.setProperty("com.googlecode.lanterna.foreground", "yellow");
        PropertiesTheme theme = new PropertiesTheme(properties);
        ThemeDefinition definition = theme.getDefinition(PropertiesThemeTest.class);
        assertEquals(TextColor.ANSI.YELLOW, definition.getNormal().getForeground());
    }
}