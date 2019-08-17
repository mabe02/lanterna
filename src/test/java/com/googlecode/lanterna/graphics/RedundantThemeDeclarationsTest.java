/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
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
 * Copyright (C) 2010-2020 Martin Berglund
 */
package com.googlecode.lanterna.graphics;

import com.googlecode.lanterna.bundle.LanternaThemes;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class RedundantThemeDeclarationsTest {
    @Test
    public void noThemeDeclarationsAreRedundant() {
        for(String theme: LanternaThemes.getRegisteredThemes()) {
            Theme registeredTheme = LanternaThemes.getRegisteredTheme(theme);
            List<String> redundantDeclarations = ((PropertyTheme) registeredTheme).findRedundantDeclarations();
            try {
                Assert.assertEquals(Collections.emptyList(), redundantDeclarations);
            }
            catch(AssertionError e) {
                System.out.println("Redundant definitions in theme '" + theme + "':");
                for(String declaration: redundantDeclarations) {
                    System.out.println(declaration);
                }
                throw e;
            }
        }
    }
}
