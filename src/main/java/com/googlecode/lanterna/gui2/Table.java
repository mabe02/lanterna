package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.*;

/**
 * Created by martin on 28/06/15.
 */
public class Table extends AbstractComponent<Table> implements Container {
    public enum TableCellBorderStyle {
        None,
        SingleLine,
        DoubleLine,
        EmptySpace,
    }

    private final List<String> columns;
    private final List<List<Component>> rows;

    private TableCellBorderStyle cellBorderStyle;

    private TextColor columnHeaderForegroundColor;
    private TextColor columnHeaderBackgroundColor;
    private EnumSet<SGR> columnHeaderModifiers;  //This isn't immutable, but we should treat it as such and not expose it!

    public Table(String... columnLabels) {
        if(columnLabels.length == 0) {
            throw new IllegalArgumentException("Table needs at least one column");
        }
        this.columns = new ArrayList<String>(Arrays.asList(columnLabels));
        this.rows = new ArrayList<List<Component>>();

        this.cellBorderStyle = TableCellBorderStyle.EmptySpace;

        this.columnHeaderForegroundColor = null;
        this.columnHeaderBackgroundColor = null;
        this.columnHeaderModifiers = EnumSet.of(SGR.UNDERLINE, SGR.BOLD);
    }

    public Table addRow(String... textLabels) {
        List<Label> labels = new ArrayList<Label>();
        for(String label: textLabels) {
            labels.add(new Label(label));
        }
        return addRow(labels);
    }

    public Table addRow(Component... components) {
        return addRow(Arrays.asList(components));
    }

    public Table addRow(Collection<? extends Component> components) {
        return insertRow(getRowCount(), components);
    }

    public Table insertRow(int index, Collection<? extends Component> components) {
        if(components.size() != columns.size()) {
            throw new IllegalArgumentException("Table has " + columns.size() + " columns, row to add has " + components.size());
        }
        rows.add(index, new ArrayList<Component>(components));
        for(Component component: components) {
            if(takeOwnership(component)) continue;
        }
        invalidate();
        return this;
    }

    public int getRowCount() {
        return rows.size();
    }

    public Table removeRow(int index) {
        rows.remove(index);
        return this;
    }

    public Table setCellComponent(int row, int column, Component component) {
        if(row < 0 || row >= getRowCount() ||
                column < 0 || column >= columns.size()) {
            throw new IndexOutOfBoundsException("Table has " + columns.size() + " columns and " + getRowCount() +
                    " rows, cannot set component at column " + column + ", row " + row);
        }
        Component existingComponent = rows.get(row).get(column);
        if(existingComponent == component) {
            return this;
        }
        removeComponent(existingComponent);
        rows.get(row).set(column, component);
        takeOwnership(component);
        return this;
    }

    @Override
    public boolean removeComponent(Component component) {
        return false;
    }

    private boolean takeOwnership(Component component) {
        if(component.getParent() == this) {
            return true;
        }
        else if(component.getParent() != null) {
            component.getParent().removeComponent(component);
        }
        component.onAdded(this);
        return false;
    }

    @Override
    protected ComponentRenderer<Table> createDefaultRenderer() {
        return null;
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public Collection<Component> getChildren() {
        return null;
    }

    @Override
    public Interactable nextFocus(Interactable fromThis) {
        return null;
    }

    @Override
    public Interactable previousFocus(Interactable fromThis) {
        return null;
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        return false;
    }

    @Override
    public void updateLookupMap(InteractableLookupMap interactableLookupMap) {

    }

    @Override
    public boolean isStructureInvalid() {
        return false;
    }
}
