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
package com.googlecode.lanterna;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author ginkoblongata
 */
public class TestTerminalRectangle {

    TerminalRectangle tr(int x, int y, int w, int h) {
        return TerminalRectangle.of(x, y, w, h);
    }
    void assertSameSystemIdentityHashCode(Object a, Object b) {
        assertEquals(System.identityHashCode(a), System.identityHashCode(b));
    }
    void assertNotSameSystemIdentityHashCode(Object a, Object b) {
        assertNotEquals(System.identityHashCode(a), System.identityHashCode(b));
    }
    
    @Test
    public void test_instantiation() {
        assertTrue(tr(12, 14, 20, 40).x() == 12);
        assertTrue(tr(12, 14, 20, 40).y() == 14);
        assertTrue(tr(12, 14, 20, 40).width() == 20);
        assertTrue(tr(12, 14, 20, 40).height() == 40);
    }
    @Test
    public void testSystemIdentityHashCodeSame() {
        TerminalRectangle a = TerminalRectangle.of(23, 34, 4, 8);
        assertSameSystemIdentityHashCode(a, a);
    }
    @Test
    public void testSystemIdentityHashCodeDifferent() {
        TerminalRectangle a = TerminalRectangle.of(23, 34, 4, 8);
        TerminalRectangle b = TerminalRectangle.of(23, 34, 4, 8);
        assertNotSameSystemIdentityHashCode(a, b);
    }
    @Test
    public void testObjectChurnReduced_of() {
        assertSameSystemIdentityHashCode(tr(0, 0, 1, 1), tr(0, 0, 1, 1));
        assertSameSystemIdentityHashCode(TerminalRectangle.OF_0x0, tr(0, 0, 0, 0));
        assertSameSystemIdentityHashCode(TerminalRectangle.OF_0x1, tr(0, 0, 0, 1));
        assertSameSystemIdentityHashCode(TerminalRectangle.OF_1x0, tr(0, 0, 1, 0));
        assertSameSystemIdentityHashCode(TerminalRectangle.OF_1x1, tr(0, 0, 1, 1));
    }
    @Test
    public void testObjectChurnReduced_as() {
        TerminalRectangle r = tr(35, 40, 20, 30);
        assertSameSystemIdentityHashCode(r, r.as(35, 40, 20, 30));
        assertSameSystemIdentityHashCode(r, r.as(TerminalPosition.of(35, 40), TerminalSize.of(20, 30)));
        assertSameSystemIdentityHashCode(r, r.as(tr(35, 40, 20, 30)));
    }
    
    @Test
    public void test_withWidth() {
        TerminalRectangle a = tr(0, 0, 35, 40);
        assertTrue(a.width() == 35);
        assertTrue(a.withWidth(20).equals(0, 0, 20, 40));
    }
    @Test
    public void test_withHeight() {
        TerminalRectangle a = tr(0, 0, 35, 40);
        assertTrue(a.height() == 40);
        assertTrue(a.withHeight(15).equals(0, 0, 35, 15));
    }
    
    
    @Test
    public void test_hashCode() {
        TerminalRectangle a = tr(0, 0, 7, 7);
        TerminalRectangle b = tr(0, 0, 7, 7);
        assertNotSameSystemIdentityHashCode(a, b);
        assertTrue(a.hashCode() == b.hashCode());
        assertFalse(tr(0, 0, 7, 7).hashCode() == tr(0, 0, 3, 7).hashCode());
    }
    @Test
    public void test_equals() {
        TerminalRectangle a = tr(0, 0, 8, 9);
        TerminalRectangle b = tr(0, 0, 8, 9);
        assertNotSameSystemIdentityHashCode(a, b);
        assertTrue(a.equals(b));
        assertTrue(a.equals(0, 0, 8, 9));
        TerminalRectangle c = tr(0,0, 4, 9);
        assertFalse(c.equals(a));
        assertFalse(c.equals(0, 0, 5, 9));
    }

}
