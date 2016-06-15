package com.googlecode.lanterna.graphics;

import com.googlecode.lanterna.bundle.LanternaThemes;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;

/**
 * Created by Martin on 2016-06-11.
 */
public class RedundantThemeDeclarationsTest {
    @Test
    @Ignore
    public void noThemeDeclarationsAreRedundant() {
        for(String theme: LanternaThemes.getRegisteredThemes()) {
            Theme registeredTheme = LanternaThemes.getRegisteredTheme(theme);
            System.out.println("Checking theme '" + theme + "' for redundant declarations...");
            Assert.assertEquals(Collections.EMPTY_LIST, ((PropertyTheme)registeredTheme).findRedundantDeclarations());
        }
    }
}
