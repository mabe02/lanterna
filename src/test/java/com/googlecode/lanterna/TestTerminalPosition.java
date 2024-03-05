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
        TerminalPosition p = tp(35, 80);
        assertSameSystemIdentityHashCode(p, p.as(35, 80));
    }
    @Test
    public void testObjectChurnReduced_plus() {
        assertSameSystemIdentityHashCode(tp(0, 1), tp(0, 0).plus(0, 1));
        assertSameSystemIdentityHashCode(tp(1, 1), tp(0, 0).plus(0 , 1).plus(1, 0));
        assertSameSystemIdentityHashCode(tp(1, 1), tp(0, 0).plus(1 , 1));

        TerminalPosition p = tp(35, 80);
        assertSameSystemIdentityHashCode(p, p.plus(0, 0));
    }
    @Test
    public void testObjectChurnReduced_minus() {
        assertSameSystemIdentityHashCode(tp(0, 0), tp(1, 1).minus(1, 1));
        assertSameSystemIdentityHashCode(tp(1, 1), tp(2, 2).minus(0, 1).minus(1, 0));

        TerminalPosition p = tp(35, 80);
        assertSameSystemIdentityHashCode(p, p.minus(0, 0));
    }
    @Test
    public void testObjectChurnReduced_divide() {
        TerminalPosition p = tp(35, 80);
        assertSameSystemIdentityHashCode(p, p.divide(1, 1));
    }
    @Test
    public void testObjectChurnReduced_multiply() {
        TerminalPosition p = tp(35, 80);
        assertSameSystemIdentityHashCode(p, p.multiply(1, 1));
    }

}
