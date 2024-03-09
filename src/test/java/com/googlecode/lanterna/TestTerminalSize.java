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
public class TestTerminalSize {

    TerminalSize ts(int x, int y) {
        return TerminalSize.of(x, y);
    }
    void assertSameSystemIdentityHashCode(Object a, Object b) {
        assertEquals(System.identityHashCode(a), System.identityHashCode(b));
    }
    void assertNotSameSystemIdentityHashCode(Object a, Object b) {
        assertNotEquals(System.identityHashCode(a), System.identityHashCode(b));
    }
    
    @Test
    public void test_instantiation() {
        assertTrue(ts(12, 14).getColumns() == 12);
        assertTrue(ts(12, 14).getRows() == 14);
        assertTrue(ts(12, 14).columns == 12);
        assertTrue(ts(12, 14).rows == 14);
    }
    @Test
    public void testSystemIdentityHashCodeSame() {
        TerminalSize a = TerminalSize.of(23, 34);
        assertSameSystemIdentityHashCode(a, a);
    }
    @Test
    public void testSystemIdentityHashCodeDifferent() {
        TerminalSize a = TerminalSize.of(23, 34);
        TerminalSize b = TerminalSize.of(23, 34);
        assertNotSameSystemIdentityHashCode(a, b);
    }
    @Test
    public void testObjectChurnReduced_of() {
        assertSameSystemIdentityHashCode(ts(0, 0), ts(0, 0));
        assertSameSystemIdentityHashCode(TerminalSize.OF_0x0, ts(0, 0));
        assertSameSystemIdentityHashCode(TerminalSize.OF_0x1, ts(0, 1));
        assertSameSystemIdentityHashCode(TerminalSize.OF_1x0, ts(1, 0));
        assertSameSystemIdentityHashCode(TerminalSize.OF_1x1, ts(1, 1));
    }
    @Test
    public void testObjectChurnReduced_as() {
        TerminalSize p = ts(35, 40);
        assertSameSystemIdentityHashCode(p, p.as(35, 40));
        assertSameSystemIdentityHashCode(p, p.as(ts(35, 40)));
    }
    @Test
    public void testObjectChurnReduced_plus() {
        assertSameSystemIdentityHashCode(ts(0, 1), ts(0, 0).plus(0, 1));
        assertSameSystemIdentityHashCode(ts(1, 1), ts(0, 0).plus(0 , 1).plus(1, 0));
        assertSameSystemIdentityHashCode(ts(1, 1), ts(0, 0).plus(1 , 1));

        TerminalSize p = ts(35, 40);
        assertSameSystemIdentityHashCode(p, p.plus(0, 0));
    }
    @Test
    public void testObjectChurnReduced_minus() {
        assertSameSystemIdentityHashCode(ts(0, 0), ts(1, 1).minus(1, 1));
        assertSameSystemIdentityHashCode(ts(1, 1), ts(2, 2).minus(0, 1).minus(1, 0));

        TerminalSize p = ts(35, 40);
        assertSameSystemIdentityHashCode(p, p.minus(0, 0));
    }
    @Test
    public void testObjectChurnReduced_divide() {
        TerminalSize p = ts(35, 40);
        assertSameSystemIdentityHashCode(p, p.divide(1, 1));
    }
    @Test
    public void testObjectChurnReduced_multiply() {
        TerminalSize p = ts(35, 40);
        assertSameSystemIdentityHashCode(p, p.multiply(1, 1));
    }
    // ------------------------------------------------------
    
    @Test
    public void test_withColumn() {
        TerminalSize a = ts(35, 40);
        assertTrue(a.getColumns() == 35);
        assertTrue(a.withColumns(20).equals(20, 40));
    }
    @Test
    public void test_withRow() {
        TerminalSize a = ts(35, 40);
        assertTrue(a.getRows() == 40);
        assertTrue(a.withRows(15).equals(35, 15));
    }
    @Test
    public void test_withRelativeColumn() {
        TerminalSize a = ts(35, 40);
        assertTrue(a.getColumns() == 35);
        assertTrue(a.withRelativeColumns(5).equals(40, 40));
    }
    @Test
    public void test_withRelativeRow() {
        TerminalSize a = ts(35, 40);
        assertTrue(a.getRows() == 40);
        assertTrue(a.withRelativeRows(-21).equals(35, 19));
    }
    
    @Test
    public void test_withRelative() {
        assertTrue(ts(33, 40).withRelative(ts(2, 3)).equals(35, 43));
        
        assertTrue(ts(33, 40).withRelative(2, 3).equals(35, 43));
        assertTrue(ts(33, 40).withRelative(-2, -3).equals(31, 37));
    }
    
    @Test
    public void testObjectChurnReduced_with() {
        TerminalSize p = ts(35, 40);
        assertSameSystemIdentityHashCode(p, p.with(p));
    }
    
    @Test
    public void test_plus() {
        assertTrue(ts(3, 5).plus(2).equals(5, 7));
        assertTrue(ts(0, 0).plus(1).equals(1, 1));
        assertTrue(ts(3, 2).plus(0).equals(3, 2));
        assertTrue(ts(3, 5).plus(-1).equals(2, 4));
        assertTrue(ts(3, 5).plus(-2).equals(1, 3));
        
        assertTrue(ts(11, 22).plus(3, 5).equals(14, 27));
        assertTrue(ts(0, 0).plus(1, 2).equals(1, 2));
        assertTrue(ts(3, 5).plus(-3, 3).equals(0, 8));
        assertTrue(ts(3, 5).plus(0, 5).equals(3, 10));
        
        assertTrue(ts(11, 22).plus(ts(3, 5)).equals(14, 27));
        assertTrue(ts(0, 0).plus(ts(1, 2)).equals(1, 2));
        assertTrue(ts(3, 5).plus(ts(3, 3)).equals(6, 8));
        assertTrue(ts(3, 5).plus(ts(0, 5)).equals(3, 10));
    }
    @Test
    public void test_minus() {
        assertTrue(ts(3, 5).minus(2).equals(1, 3));
        assertTrue(ts(3, 2).minus(0).equals(3, 2));
        assertTrue(ts(3, 5).minus(-1).equals(4, 6));
        assertTrue(ts(3, 5).minus(-2).equals(5, 7));

        assertTrue(ts(11, 22).minus(3, 2).equals(8, 20));
        assertTrue(ts(11, 22).minus(-3, -2).equals(14, 24));
        assertTrue(ts(11, 22).minus(0, 5).equals(11, 17));
        assertTrue(ts(11, 22).minus(1, 0).equals(10, 22));
        
        assertTrue(ts(11, 22).minus(ts(3, 2)).equals(8, 20));
        assertTrue(ts(11, 22).minus(ts(0, 5)).equals(11, 17));
        assertTrue(ts(11, 22).minus(ts(1, 0)).equals(10, 22));
    }
    @Test
    public void test_multiply() {
        assertTrue(ts(11, 22).multiply(0).equals(0, 0));
        assertTrue(ts(11, 22).multiply(-0).equals(0, 0));
        assertTrue(ts(11, 22).multiply(0).equals(0, 0));
        assertTrue(ts(11, 22).multiply(1).equals(11, 22));
        assertTrue(ts(11, 22).multiply(3).equals(33, 66));
        
        assertTrue(ts(11, 22).multiply(1, 2).equals(11, 44));
        assertTrue(ts(11, 22).multiply(0, 5).equals(0, 110));
        assertTrue(ts(11, 22).multiply(1, 0).equals(11, 0));
        
        assertTrue(ts(11, 22).multiply(ts(1, 2)).equals(11, 44));
        assertTrue(ts(11, 22).multiply(ts(0, 5)).equals(0, 110));
        assertTrue(ts(11, 22).multiply(ts(1, 0)).equals(11, 0));
    }
    @Test
    public void test_divide() {
        assertTrue(ts(11, 22).divide(1, 2).equals(11, 11));
        assertTrue(ts(11, 22).divide(1, 5).equals(11, 4));
        assertTrue(ts(20, 22).divide(4, 1).equals(5, 22));
        assertTrue(ts(20, 30).divide(1, 4).equals(20, 7));
    }
    @Test
    public void test_min() {
        assertTrue(ts(5, 8).min(ts(9, 7)).equals(5, 7));
        assertTrue(ts(5, 8).min(ts(4, 9)).equals(4, 8));
    }
    @Test
    public void test_max() {
        assertTrue(ts(5, 8).min(ts(9, 7)).equals(5, 7));
        assertTrue(ts(5, 8).min(ts(4, 9)).equals(4, 8));
        assertTrue(ts(0, 0).min(ts(4, 9)).equals(0, 0));
    }
    @Test
    public void test_compareTo() {
        assertTrue(ts(5, 6).compareTo(ts(5, 7)) < 0);
        assertTrue(ts(5, 6).compareTo(ts(5, 6)) == 0);
        assertTrue(ts(5, 6).compareTo(ts(5, 5)) > 0);
        
        assertTrue(ts(5, 6).compareTo(ts(6, 6)) < 0);
        assertTrue(ts(5, 6).compareTo(ts(5, 6)) == 0);
        assertTrue(ts(5, 6).compareTo(ts(4, 6)) > 0);
    }
    @Test
    public void test_hashCode() {
        TerminalSize a = ts(7, 7);
        TerminalSize b = ts(7, 7);
        assertNotSameSystemIdentityHashCode(a, b);
        assertTrue(a.hashCode() == b.hashCode());
        assertFalse(ts(7, 7).hashCode() == ts(3, 7).hashCode());
    }
    @Test
    public void test_equals() {
        TerminalSize a = ts(8, 9);
        TerminalSize b = ts(8, 9);
        assertNotSameSystemIdentityHashCode(a, b);
        assertTrue(a.equals(b));
        assertTrue(a.equals(8, 9));
        TerminalSize c = ts(4, 9);
        assertFalse(c.equals(a));
        assertFalse(c.equals(5, 9));
    }

}
