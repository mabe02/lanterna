package com.googlecode.lanterna.bundle;

import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Scanner;

/**
 * To ensure our bundled default theme matches the theme definition file in resources
 */
public class DefaultThemeTest {
    @Test
    public void ensureResourceFileDefaultTestIsTheSameAsTheEmbeddedTest() throws NoSuchFieldException, IllegalAccessException, IOException {
        String embeddedDefinition = getEmbeddedDefinition();
        String resourceDefinition = getResourceDefinition();
        Assert.assertEquals(resourceDefinition, embeddedDefinition);
    }

    private String getEmbeddedDefinition() throws NoSuchFieldException, IllegalAccessException {
        Field definitionField = DefaultTheme.class.getDeclaredField("definition");
        definitionField.setAccessible(true);
        return (String)definitionField.get(null);
    }

    private String getResourceDefinition() throws IOException {
        ClassLoader classLoader = DefaultThemeTest.class.getClassLoader();
        InputStream resourceAsStream = null;
        try {
            resourceAsStream = classLoader.getResourceAsStream("default-theme.properties");
            if (resourceAsStream == null) {
                resourceAsStream = new FileInputStream("src/main/resources/default-theme.properties");
            }

            // https://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
            Scanner s = new Scanner(resourceAsStream).useDelimiter("\\A");
            String definition = s.hasNext() ? s.next() : "";

            // Normalize line endings to LF
            definition = definition.replace("\r\n", "\n");

            return definition;
        }
        finally {
            if(resourceAsStream != null) {
                resourceAsStream.close();
            }
        }
    }
}
