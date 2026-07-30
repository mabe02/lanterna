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
 * Copyright (C) 2010-2024 Martin Berglund
 */
package com.googlecode.lanterna.gui2.table;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link PojoTableModel}, verifying that POJO-based row management works
 * correctly alongside the existing {@link TableModel} API (backward compatibility).
 */
public class PojoTableModelTest {

    // -------------------------------------------------------------------
    // Simple POJO used across tests
    // -------------------------------------------------------------------

    /**
     * A minimal domain object representing a person, used as the row type in these tests.
     */
    static class Person {
        final String name;
        final String surname;
        final int age;

        Person(String name, String surname, int age) {
            this.name = name;
            this.surname = surname;
            this.age = age;
        }

        /** Column values returned by the row mapper: Name, Surname, Age */
        List<String> toColumns() {
            return Arrays.asList(name, surname, String.valueOf(age));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Person)) return false;
            Person p = (Person) o;
            return age == p.age && Objects.equals(name, p.name) && Objects.equals(surname, p.surname);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, surname, age);
        }

        @Override
        public String toString() {
            return name + " " + surname + " (" + age + ")";
        }
    }

    // -------------------------------------------------------------------
    // Test setup
    // -------------------------------------------------------------------

    private PojoTableModel<Person, String> model;
    private final Person tunar   = new Person("Tunar",  "Bayramov",  25);
    private final Person martin  = new Person("Martin", "Berglund",  40);
    private final Person alice   = new Person("Alice",  "Wonderland", 30);

    @Before
    public void setUp() {
        model = new PojoTableModel<>(Person::toColumns, "Name", "Surname", "Age");
    }

    // -------------------------------------------------------------------
    // Construction
    // -------------------------------------------------------------------

    @Test
    public void testConstructorWithVarargs() {
        PojoTableModel<Person, String> m =
                new PojoTableModel<>(Person::toColumns, "Name", "Surname", "Age");
        assertEquals(3, m.getColumnCount());
        assertEquals(0, m.getRowCount());
    }

    @Test
    public void testConstructorWithList() {
        List<String> labels = Arrays.asList("Name", "Surname", "Age");
        PojoTableModel<Person, String> m = new PojoTableModel<>(Person::toColumns, labels);
        assertEquals(3, m.getColumnCount());
        assertEquals(0, m.getRowCount());
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorNullRowMapperThrows() {
        new PojoTableModel<>(null, "Name");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNoColumnsThrows() {
        new PojoTableModel<>(Person::toColumns);
    }

    // -------------------------------------------------------------------
    // addRow(R pojo)
    // -------------------------------------------------------------------

    @Test
    public void testAddRowIncrementsRowCount() {
        assertEquals(0, model.getRowCount());
        model.addRow(tunar);
        assertEquals(1, model.getRowCount());
        model.addRow(martin);
        assertEquals(2, model.getRowCount());
    }

    @Test
    public void testAddRowPopulatesCellValues() {
        model.addRow(tunar);
        // Verify that the parent TableModel cells reflect the POJO's fields
        assertEquals("Tunar",   model.getCell(0, 0));
        assertEquals("Bayramov", model.getCell(1, 0));
        assertEquals("25",      model.getCell(2, 0));
    }

    @Test
    public void testAddMultipleRowsPreservesOrder() {
        model.addRow(tunar);
        model.addRow(martin);
        model.addRow(alice);

        assertEquals(tunar,  model.getPojoRow(0));
        assertEquals(martin, model.getPojoRow(1));
        assertEquals(alice,  model.getPojoRow(2));
    }

    // -------------------------------------------------------------------
    // getPojoRow(int)
    // -------------------------------------------------------------------

    @Test
    public void testGetPojoRowReturnsSamePojo() {
        model.addRow(tunar);
        assertSame(tunar, model.getPojoRow(0));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetPojoRowOutOfBoundsThrows() {
        model.addRow(tunar);
        model.getPojoRow(1); // only row 0 exists
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetPojoRowNegativeIndexThrows() {
        model.getPojoRow(-1);
    }

    // -------------------------------------------------------------------
    // getPojoRows()
    // -------------------------------------------------------------------

    @Test
    public void testGetPojoRowsReturnsAllInOrder() {
        model.addRow(tunar);
        model.addRow(martin);
        List<Person> all = model.getPojoRows();
        assertEquals(2, all.size());
        assertEquals(tunar,  all.get(0));
        assertEquals(martin, all.get(1));
    }

    @Test
    public void testGetPojoRowsReturnsUnmodifiableCopy() {
        model.addRow(tunar);
        List<Person> snapshot = model.getPojoRows();
        try {
            snapshot.add(martin);
            fail("Should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            // correct
        }
        // Adding to snapshot must not affect model
        assertEquals(1, model.getRowCount());
    }

    @Test
    public void testGetPojoRowsIsEmptyInitially() {
        assertTrue(model.getPojoRows().isEmpty());
    }

    // -------------------------------------------------------------------
    // insertRow(int, R)
    // -------------------------------------------------------------------

    @Test
    public void testInsertRowAtBeginning() {
        model.addRow(martin);
        model.insertRow(0, tunar);

        assertEquals(2, model.getRowCount());
        assertEquals(tunar,  model.getPojoRow(0));
        assertEquals(martin, model.getPojoRow(1));
        assertEquals("Tunar", model.getCell(0, 0));
    }

    @Test
    public void testInsertRowInMiddle() {
        model.addRow(tunar);
        model.addRow(alice);
        model.insertRow(1, martin);

        assertEquals(3, model.getRowCount());
        assertEquals(tunar,  model.getPojoRow(0));
        assertEquals(martin, model.getPojoRow(1));
        assertEquals(alice,  model.getPojoRow(2));
    }

    // -------------------------------------------------------------------
    // removeRow(int)
    // -------------------------------------------------------------------

    @Test
    public void testRemoveRowDecrementsRowCount() {
        model.addRow(tunar);
        model.addRow(martin);
        model.removeRow(0);
        assertEquals(1, model.getRowCount());
        assertEquals(martin, model.getPojoRow(0));
    }

    @Test
    public void testRemoveRowUpdatesPojoList() {
        model.addRow(tunar);
        model.addRow(martin);
        model.addRow(alice);
        model.removeRow(1); // remove martin

        assertEquals(2, model.getRowCount());
        assertEquals(tunar, model.getPojoRow(0));
        assertEquals(alice, model.getPojoRow(1));
    }

    // -------------------------------------------------------------------
    // removePojoRow(R)
    // -------------------------------------------------------------------

    @Test
    public void testRemovePojoRowByObject() {
        model.addRow(tunar);
        model.addRow(martin);
        model.removePojoRow(tunar);

        assertEquals(1, model.getRowCount());
        assertEquals(martin, model.getPojoRow(0));
    }

    @Test
    public void testRemovePojoRowNotFoundDoesNothing() {
        model.addRow(tunar);
        model.removePojoRow(martin); // martin not in model
        assertEquals(1, model.getRowCount());
    }

    // -------------------------------------------------------------------
    // indexOfPojo(R)
    // -------------------------------------------------------------------

    @Test
    public void testIndexOfPojoFound() {
        model.addRow(tunar);
        model.addRow(martin);
        assertEquals(1, model.indexOfPojo(martin));
    }

    @Test
    public void testIndexOfPojoNotFound() {
        model.addRow(tunar);
        assertEquals(-1, model.indexOfPojo(alice));
    }

    // -------------------------------------------------------------------
    // setPojoRow(int, R)
    // -------------------------------------------------------------------

    @Test
    public void testSetPojoRowUpdatesCellsAndPojo() {
        model.addRow(tunar);
        model.setPojoRow(0, martin);

        assertEquals(martin, model.getPojoRow(0));
        assertEquals("Martin",   model.getCell(0, 0));
        assertEquals("Berglund", model.getCell(1, 0));
        assertEquals("40",       model.getCell(2, 0));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetPojoRowOutOfBoundsThrows() {
        model.setPojoRow(0, tunar); // model is empty
    }

    // -------------------------------------------------------------------
    // clear()
    // -------------------------------------------------------------------

    @Test
    public void testClearRemovesAllRowsAndPojos() {
        model.addRow(tunar);
        model.addRow(martin);
        model.clear();

        assertEquals(0, model.getRowCount());
        assertTrue(model.getPojoRows().isEmpty());
    }

    // -------------------------------------------------------------------
    // Backward compatibility — legacy TableModel API still works
    // -------------------------------------------------------------------

    @Test
    public void testLegacyAddRowVarargs() {
        // Adding rows via the inherited TableModel API should still work
        model.addRow("Legacy", "User", "99");
        assertEquals(1, model.getRowCount());
        assertEquals("Legacy", model.getCell(0, 0));
        // getPojoRow returns null for non-POJO rows
        assertNull(model.getPojoRow(0));
    }

    @Test
    public void testMixedPojoAndLegacyRows() {
        model.addRow(tunar);                        // POJO row at index 0
        model.addRow("Legacy", "User", "99");       // legacy row at index 1
        model.addRow(alice);                         // POJO row at index 2

        assertEquals(3, model.getRowCount());
        assertEquals(tunar, model.getPojoRow(0));
        assertNull(model.getPojoRow(1));            // legacy row
        assertEquals(alice, model.getPojoRow(2));

        // Cell values of the POJO rows are correct
        assertEquals("Tunar", model.getCell(0, 0));
        assertEquals("Legacy", model.getCell(0, 1));
        assertEquals("Alice", model.getCell(0, 2));
    }

    @Test
    public void testGetRowStillReturnsListForPojoRow() {
        model.addRow(tunar);
        List<String> row = model.getRow(0);
        assertEquals(Arrays.asList("Tunar", "Bayramov", "25"), row);
    }

    // -------------------------------------------------------------------
    // Listener integration (inherited from TableModel)
    // -------------------------------------------------------------------

    @Test
    public void testListenerFiredOnAddRow() {
        final int[] addedIndex = {-1};
        model.addListener(new TableModel.Listener<String>() {
            @Override public void onRowAdded(TableModel<String> m, int index) { addedIndex[0] = index; }
            @Override public void onRowRemoved(TableModel<String> m, int index, List<String> r) {}
            @Override public void onColumnAdded(TableModel<String> m, int index) {}
            @Override public void onColumnRemoved(TableModel<String> m, int index, String h, List<String> c) {}
            @Override public void onCellChanged(TableModel<String> m, int row, int col, String o, String n) {}
        });

        model.addRow(tunar);
        assertEquals(0, addedIndex[0]);
    }

    @Test
    public void testListenerFiredOnRemoveRow() {
        model.addRow(tunar);
        final int[] removedIndex = {-1};
        model.addListener(new TableModel.Listener<String>() {
            @Override public void onRowAdded(TableModel<String> m, int index) {}
            @Override public void onRowRemoved(TableModel<String> m, int index, List<String> r) { removedIndex[0] = index; }
            @Override public void onColumnAdded(TableModel<String> m, int index) {}
            @Override public void onColumnRemoved(TableModel<String> m, int index, String h, List<String> c) {}
            @Override public void onCellChanged(TableModel<String> m, int row, int col, String o, String n) {}
        });

        model.removeRow(0);
        assertEquals(0, removedIndex[0]);
    }
}
