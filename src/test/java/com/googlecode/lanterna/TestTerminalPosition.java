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
public class TestTerminalPosition {

    TerminalPosition tp(int x, int y) {
        return TerminalPosition.of(x, y);
    }
    void assertSameSystemIdentityHashCode(Object a, Object b) {
        assertEquals(System.identityHashCode(a), System.identityHashCode(b));
    }
    void assertNotSameSystemIdentityHashCode(Object a, Object b) {
        assertNotEquals(System.identityHashCode(a), System.identityHashCode(b));
    }
    
    @Test
    public void test_instantiation() {
        assertTrue(tp(12, 14).getColumn() == 12);
        assertTrue(tp(12, 14).getRow() == 14);
        assertTrue(tp(12, 14).x() == 12);
        assertTrue(tp(12, 14).y() == 14);
    }
    @Test
    public void testSystemIdentityHashCodeSame() {
        TerminalPosition a = TerminalPosition.of(23, 34);
        assertSameSystemIdentityHashCode(a, a);
    }
    @Test
    public void testSystemIdentityHashCodeDifferent() {
        TerminalPosition a = TerminalPosition.of(23, 34);
        TerminalPosition b = TerminalPosition.of(23, 34);
        assertNotSameSystemIdentityHashCode(a, b);
    }
    @Test
    public void testObjectChurnReduced_of() {
        assertSameSystemIdentityHashCode(tp(0, 0), tp(0, 0));
        assertSameSystemIdentityHashCode(TerminalPosition.OF_0x0, tp(0, 0));
        assertSameSystemIdentityHashCode(TerminalPosition.OF_0x1, tp(0, 1));
        assertSameSystemIdentityHashCode(TerminalPosition.OF_1x0, tp(1, 0));
        assertSameSystemIdentityHashCode(TerminalPosition.OF_1x1, tp(1, 1));
    }
    @Test
    public void testObjectChurnReduced_as() {
        TerminalPosition p = tp(35, 40);
        assertSameSystemIdentityHashCode(p, p.as(35, 40));
        assertSameSystemIdentityHashCode(p, p.as(tp(35, 40)));
    }
    @Test
    public void testObjectChurnReduced_plus() {
        assertSameSystemIdentityHashCode(tp(0, 1), tp(0, 0).plus(0, 1));
        assertSameSystemIdentityHashCode(tp(1, 1), tp(0, 0).plus(0 , 1).plus(1, 0));
        assertSameSystemIdentityHashCode(tp(1, 1), tp(0, 0).plus(1 , 1));

        TerminalPosition p = tp(35, 40);
        assertSameSystemIdentityHashCode(p, p.plus(0, 0));
    }
    @Test
    public void testObjectChurnReduced_minus() {
        assertSameSystemIdentityHashCode(tp(0, 0), tp(1, 1).minus(1, 1));
        assertSameSystemIdentityHashCode(tp(1, 1), tp(2, 2).minus(0, 1).minus(1, 0));

        TerminalPosition p = tp(35, 40);
        assertSameSystemIdentityHashCode(p, p.minus(0, 0));
    }
    @Test
    public void testObjectChurnReduced_divide() {
        TerminalPosition p = tp(35, 40);
        assertSameSystemIdentityHashCode(p, p.divide(1, 1));
    }
    @Test
    public void testObjectChurnReduced_multiply() {
        TerminalPosition p = tp(35, 40);
        assertSameSystemIdentityHashCode(p, p.multiply(1, 1));
    }
    // ------------------------------------------------------
    
    @Test
    public void test_withColumn() {
        TerminalPosition a = tp(35, 40);
        assertTrue(a.getColumn() == 35);
        assertTrue(a.withColumn(20).equals(20, 40));
    }
    @Test
    public void test_withRow() {
        TerminalPosition a = tp(35, 40);
        assertTrue(a.getRow() == 40);
        assertTrue(a.withRow(15).equals(35, 15));
    }
    @Test
    public void test_withRelativeColumn() {
        TerminalPosition a = tp(35, 40);
        assertTrue(a.getColumn() == 35);
        assertTrue(a.withRelativeColumn(5).equals(40, 40));
    }
    @Test
    public void test_withRelativeRow() {
        TerminalPosition a = tp(35, 40);
        assertTrue(a.getRow() == 40);
        assertTrue(a.withRelativeRow(-21).equals(35, 19));
    }
    
    @Test
    public void test_withRelative() {
        assertTrue(tp(33, 40).withRelative(tp(2, 3)).equals(35, 43));
        assertTrue(tp(33, 40).withRelative(tp(-2, -3)).equals(31, 37));
        assertTrue(tp(33, 40).withRelative(2, 3).equals(35, 43));
        assertTrue(tp(33, 40).withRelative(-2, -3).equals(31, 37));
    }
    
    @Test
    public void testObjectChurnReduced_with() {
        TerminalPosition p = tp(35, 40);
        assertSameSystemIdentityHashCode(p, p.with(p));
    }
    
    @Test
    public void test_plus() {
        assertTrue(tp(3, 5).plus(2).equals(5, 7));
        assertTrue(tp(0, 0).plus(1).equals(1, 1));
        assertTrue(tp(3, 2).plus(0).equals(3, 2));
        assertTrue(tp(3, 5).plus(-1).equals(2, 4));
        assertTrue(tp(3, 5).plus(-2).equals(1, 3));
        
        assertTrue(tp(11, 22).plus(3, 5).equals(14, 27));
        assertTrue(tp(0, 0).plus(1, 2).equals(1, 2));
        assertTrue(tp(3, 5).plus(-4, 3).equals(-1, 8));
        assertTrue(tp(3, 5).plus(0, 5).equals(3, 10));
        
        assertTrue(tp(11, 22).plus(tp(3, 5)).equals(14, 27));
        assertTrue(tp(0, 0).plus(tp(1, 2)).equals(1, 2));
        assertTrue(tp(3, 5).plus(tp(-4, 3)).equals(-1, 8));
        assertTrue(tp(3, 5).plus(tp(0, 5)).equals(3, 10));
    }
    @Test
    public void test_minus() {
        assertTrue(tp(3, 5).minus(2).equals(1, 3));
        assertTrue(tp(0, 0).minus(1).equals(-1, -1));
        assertTrue(tp(3, 2).minus(0).equals(3, 2));
        assertTrue(tp(3, 5).minus(-1).equals(4, 6));
        assertTrue(tp(3, 5).minus(-2).equals(5, 7));

        assertTrue(tp(11, 22).minus(3, 2).equals(8, 20));
        assertTrue(tp(11, 22).minus(-3, -2).equals(14, 24));
        assertTrue(tp(11, 22).minus(0, 5).equals(11, 17));
        assertTrue(tp(11, 22).minus(1, 0).equals(10, 22));
        
        assertTrue(tp(11, 22).minus(tp(3, 2)).equals(8, 20));
        assertTrue(tp(11, 22).minus(tp(-3, -2)).equals(14, 24));
        assertTrue(tp(11, 22).minus(tp(0, 5)).equals(11, 17));
        assertTrue(tp(11, 22).minus(tp(1, 0)).equals(10, 22));
    }
    @Test
    public void test_multiply() {
        assertTrue(tp(11, 22).multiply(-3).equals(-33, -66));
        assertTrue(tp(11, 22).multiply(-1).equals(-11, -22));
        assertTrue(tp(11, 22).multiply(-0).equals(0, 0));
        assertTrue(tp(11, 22).multiply(0).equals(0, 0));
        assertTrue(tp(11, 22).multiply(1).equals(11, 22));
        assertTrue(tp(11, 22).multiply(3).equals(33, 66));
        
        assertTrue(tp(11, 22).multiply(1, 2).equals(11, 44));
        assertTrue(tp(11, 22).multiply(-3, -2).equals(-33, -44));
        assertTrue(tp(11, 22).multiply(0, 5).equals(0, 110));
        assertTrue(tp(11, 22).multiply(1, 0).equals(11, 0));
        
        assertTrue(tp(11, 22).multiply(1, 2).equals(11, 44));
        assertTrue(tp(11, 22).multiply(-3, -2).equals(-33, -44));
        assertTrue(tp(11, 22).multiply(0, 5).equals(0, 110));
        assertTrue(tp(11, 22).multiply(1, 0).equals(11, 0));
    }
    @Test
    public void test_divide() {
        assertTrue(tp(11, 22).divide(1, 2).equals(11, 11));
        assertTrue(tp(11, 22).divide(1, 5).equals(11, 4));
        assertTrue(tp(20, 22).divide(4, 1).equals(5, 22));
        assertTrue(tp(20, 30).divide(1, 4).equals(20, 7));
    }
    @Test
    public void test_abs() {
        assertTrue(tp(-5, 7).abs().equals(5, 7));
        assertTrue(tp(-7, -9).abs().equals(7, 9));
        assertTrue(tp(11, -11).abs().equals(11, 11));
    }
    @Test
    public void test_min() {
        assertTrue(tp(5, 8).min(tp(9, 7)).equals(5, 7));
        assertTrue(tp(5, 8).min(tp(4, 9)).equals(4, 8));
        assertTrue(tp(0, 0).min(tp(-4, 9)).equals(-4, 0));
    }
    @Test
    public void test_max() {
        assertTrue(tp(5, 8).min(tp(9, 7)).equals(5, 7));
        assertTrue(tp(5, 8).min(tp(4, 9)).equals(4, 8));
        assertTrue(tp(0, 0).min(tp(4, 9)).equals(0, 0));
    }
    @Test
    public void test_compareTo() {
        assertTrue(tp(5, 6).compareTo(tp(5, 7)) < 0);
        assertTrue(tp(5, 6).compareTo(tp(5, 6)) == 0);
        assertTrue(tp(5, 6).compareTo(tp(5, 5)) > 0);
        
        assertTrue(tp(5, 6).compareTo(tp(6, 6)) < 0);
        assertTrue(tp(5, 6).compareTo(tp(5, 6)) == 0);
        assertTrue(tp(5, 6).compareTo(tp(4, 6)) > 0);
    }
    @Test
    public void test_hashCode() {
        TerminalPosition a = tp(7, 7);
        TerminalPosition b = tp(7, 7);
        assertNotSameSystemIdentityHashCode(a, b);
        assertTrue(a.hashCode() == b.hashCode());
        assertFalse(tp(7, 7).hashCode() == tp(3, 7).hashCode());
    }
    @Test
    public void test_equals() {
        TerminalPosition a = tp(8, 9);
        TerminalPosition b = tp(8, 9);
        assertNotSameSystemIdentityHashCode(a, b);
        assertTrue(a.equals(b));
        assertTrue(a.equals(8, 9));
        TerminalPosition c = tp(4, 9);
        assertFalse(c.equals(a));
        assertFalse(c.equals(5, 9));
    }

}
