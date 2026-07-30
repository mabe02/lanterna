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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * A {@code PojoTableModel} is an extension of {@link TableModel} that allows rows to be represented
 * as Plain Old Java Objects (POJOs), following the Java Swing MVC (Model-View-Controller) pattern
 * more closely.
 *
 * <p>Instead of managing individual column values separately, you can work with your own domain
 * objects directly. A {@link Function} (row mapper) is provided at construction time to extract
 * the individual column values from each POJO when the table needs to render them.</p>
 *
 * <p>All existing {@link TableModel} methods (e.g. {@code addRow(V... values)},
 * {@code getRow(int index)}) remain fully functional for backward compatibility. The POJO-based
 * rows added via {@link #addRow(Object)} are tracked separately and can be retrieved via
 * {@link #getPojoRow(int)} or {@link #getPojoRows()}.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * PojoTableModel<Person, String> model = new PojoTableModel<>(
 *     person -> Arrays.asList(person.getName(), person.getSurname(), String.valueOf(person.getAge())),
 *     "Name", "Surname", "Age"
 * );
 *
 * model.addRow(new Person("Tunar", "Bayramov", 25));
 * model.addRow(new Person("Martin", "Berglund", 40));
 *
 * Person first = model.getPojoRow(0); // returns Person("Tunar", ...)
 * List<Person> all = model.getPojoRows(); // returns all POJOs
 * }</pre>
 *
 * @param <R> The type of the POJO (row object) stored in this model
 * @param <V> The type of the individual cell values used for rendering (e.g. {@code String})
 * @author Contribution for Issue #658
 */
public class PojoTableModel<R, V> extends TableModel<V> {

    /**
     * Keeps a POJO reference (or {@code null} for legacy rows) for every row stored in the
     * parent {@link TableModel}. The two lists are always kept in sync:
     * {@code pojoRows.size() == getRowCount()} at all times.
     */
    private final List<R> pojoRows;

    /**
     * Converts a POJO of type {@code R} into a list of cell values of type {@code V}.
     * The list must have the same number of elements as there are columns in this model.
     */
    private final Function<R, List<V>> rowMapper;

    /**
     * Flag set to {@code true} for the duration of a POJO-initiated {@code insertRow} call so
     * that the overridden {@link #insertRow(int, java.util.Collection)} knows NOT to add an
     * extra {@code null} into {@link #pojoRows} (the POJO-path methods handle that themselves).
     */
    private boolean insertingPojo = false;

    /**
     * Creates a new {@code PojoTableModel} with the specified column labels and a row mapper
     * function that knows how to extract cell values from a POJO.
     *
     * @param rowMapper     A function that maps a POJO of type {@code R} to a {@link List} of
     *                      cell values. The list size must match the number of column labels
     *                      provided. Must not be {@code null}.
     * @param columnLabels  Variable-length array of column header labels. At least one label
     *                      must be provided.
     * @throws NullPointerException     if {@code rowMapper} is {@code null}
     * @throws IllegalArgumentException if no column labels are provided
     */
    public PojoTableModel(Function<R, List<V>> rowMapper, String... columnLabels) {
        this(rowMapper, Arrays.asList(columnLabels));
    }

    /**
     * Creates a new {@code PojoTableModel} with the specified column labels and a row mapper
     * function that knows how to extract cell values from a POJO.
     *
     * @param rowMapper     A function that maps a POJO of type {@code R} to a {@link List} of
     *                      cell values. The list size must match the number of column labels
     *                      provided. Must not be {@code null}.
     * @param columnLabels  List of column header labels. At least one label must be provided.
     * @throws NullPointerException     if {@code rowMapper} is {@code null}
     * @throws IllegalArgumentException if no column labels are provided
     */
    public PojoTableModel(Function<R, List<V>> rowMapper, List<String> columnLabels) {
        super(columnLabels);
        this.rowMapper = Objects.requireNonNull(rowMapper, "rowMapper must not be null");
        this.pojoRows = new ArrayList<>();
    }

    /**
     * Adds a new row to the table model using a POJO as the data source. The row mapper
     * provided at construction time is used to extract the individual column values from
     * the POJO.
     *
     * <p>The POJO itself is stored internally and can be retrieved later via
     * {@link #getPojoRow(int)} or {@link #getPojoRows()}.</p>
     *
     * @param pojo The POJO to add as a new row. May be {@code null} if your domain requires it,
     *             but note that the row mapper will be called with {@code null} in that case.
     * @return Itself, for method chaining
     */
    public synchronized PojoTableModel<R, V> addRow(R pojo) {
        List<V> columnValues = rowMapper.apply(pojo);
        insertingPojo = true;
        try {
            super.addRow(columnValues);
        } finally {
            insertingPojo = false;
        }
        pojoRows.add(pojo);
        return this;
    }

    /**
     * Inserts a new POJO-backed row into the table model at a specific index. The row mapper
     * provided at construction time is used to extract the individual column values.
     *
     * @param index Index at which to insert the new row. {@code 0} means first, and
     *              {@link #getRowCount()} appends at the end.
     * @param pojo  The POJO to insert as a new row.
     * @return Itself, for method chaining
     */
    public synchronized PojoTableModel<R, V> insertRow(int index, R pojo) {
        List<V> columnValues = rowMapper.apply(pojo);
        insertingPojo = true;
        try {
            super.insertRow(index, columnValues);
        } finally {
            insertingPojo = false;
        }
        pojoRows.add(index, pojo);
        return this;
    }

    /**
     * Override the Collection-based {@code insertRow} — the single choke-point for ALL
     * {@link TableModel#addRow} variants — so that legacy (non-POJO) calls also keep
     * {@link #pojoRows} in sync by inserting a {@code null} placeholder at the same index.
     *
     * <p>When a POJO-path method is executing it sets {@link #insertingPojo} to {@code true}
     * before delegating here, so we know NOT to add a second entry.</p>
     *
     * @param index  Index at which to insert the new row
     * @param values Cell values for the new row
     * @return Itself
     */
    @Override
    public synchronized TableModel<V> insertRow(int index, java.util.Collection<V> values) {
        super.insertRow(index, values);
        if (!insertingPojo) {
            // Legacy call — insert a null placeholder so pojoRows stays in sync
            pojoRows.add(index, null);
        }
        return this;
    }

    /**
     * Removes the row at the specified index from the model. If the row was added via
     * {@link #addRow(Object)}, the corresponding POJO is also removed from the internal
     * POJO list.
     *
     * @param index Index of the row to remove
     * @return Itself, for method chaining
     */
    @Override
    public synchronized PojoTableModel<R, V> removeRow(int index) {
        super.removeRow(index);
        // pojoRows is always the same size as the parent row list, so the index is always valid
        pojoRows.remove(index);
        return this;
    }

    /**
     * Removes all rows from the model, including any stored POJO references.
     *
     * @return Itself, for method chaining
     */
    @Override
    public synchronized PojoTableModel<R, V> clear() {
        super.clear();
        pojoRows.clear();
        return this;
    }

    /**
     * Returns the POJO stored at the given row index, or {@code null} if the row at that index
     * was added via the legacy {@link TableModel#addRow(Object[])} / {@link TableModel#addRow(java.util.Collection)}
     * API (i.e. not POJO-backed).
     *
     * @param index The row index to retrieve
     * @return The POJO at the given index, or {@code null} for non-POJO rows
     * @throws IndexOutOfBoundsException if {@code index} is out of range
     */
    public synchronized R getPojoRow(int index) {
        if (index < 0 || index >= getRowCount()) {
            throw new IndexOutOfBoundsException(
                    "Row index " + index + " is out of bounds (row count: " + getRowCount() + ")");
        }
        if (index < pojoRows.size()) {
            return pojoRows.get(index);
        }
        return null;
    }

    /**
     * Returns an unmodifiable view of all POJOs stored in this model, in insertion order.
     * Rows that were added via the legacy API are represented as {@code null} entries in the
     * returned list.
     *
     * @return An unmodifiable list of POJOs (may contain {@code null} for non-POJO rows)
     */
    public synchronized List<R> getPojoRows() {
        return Collections.unmodifiableList(new ArrayList<>(pojoRows));
    }

    /**
     * Returns the index of the first occurrence of the given POJO in this model, or {@code -1}
     * if it is not found. Uses {@link Object#equals(Object)} for comparison.
     *
     * @param pojo The POJO to search for
     * @return The row index of the first matching POJO, or {@code -1} if not found
     */
    public synchronized int indexOfPojo(R pojo) {
        return pojoRows.indexOf(pojo);
    }

    /**
     * Removes the first row whose backing POJO equals the given object (as determined by
     * {@link Object#equals(Object)}). Does nothing if the POJO is not found.
     *
     * @param pojo The POJO whose row should be removed
     * @return Itself, for method chaining
     */
    public synchronized PojoTableModel<R, V> removePojoRow(R pojo) {
        int index = indexOfPojo(pojo);
        if (index >= 0) {
            removeRow(index);
        }
        return this;
    }

    /**
     * Updates the row at the given index by replacing its backing POJO and re-applying the row
     * mapper to refresh the cell values. This is the POJO equivalent of calling
     * {@link TableModel#setCell(int, int, Object)} for every column.
     *
     * @param index  The row index to update
     * @param pojo   The new POJO to store at this row
     * @return Itself, for method chaining
     * @throws IndexOutOfBoundsException if {@code index} is out of range
     */
    public synchronized PojoTableModel<R, V> setPojoRow(int index, R pojo) {
        if (index < 0 || index >= getRowCount()) {
            throw new IndexOutOfBoundsException(
                    "Row index " + index + " is out of bounds (row count: " + getRowCount() + ")");
        }
        List<V> columnValues = rowMapper.apply(pojo);
        for (int col = 0; col < columnValues.size() && col < getColumnCount(); col++) {
            setCell(col, index, columnValues.get(col));
        }
        // Extend pojoRows list with nulls if needed (for rows added via legacy API)
        while (pojoRows.size() <= index) {
            pojoRows.add(null);
        }
        pojoRows.set(index, pojo);
        return this;
    }
}
