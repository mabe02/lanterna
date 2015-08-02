package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.*;

/**
 * Table class is a special container that keeps child components in a well-defined grid with text labels above. Column
 * and row sizes are adjusted so that each column is as wide as its widest cell and each row is as tall as its tallest
 * cell.
 */
public class Table extends AbstractComponent<Table> implements Container {
    private final TableModel tableModel;
    private TextColor columnHeaderForegroundColor;
    private TextColor columnHeaderBackgroundColor;
    private EnumSet<SGR> columnHeaderModifiers;  //This isn't immutable, but we should treat it as such and not expose it!

    public Table(String... columnLabels) {
        if(columnLabels.length == 0) {
            throw new IllegalArgumentException("Table needs at least one column");
        }
        this.tableModel = new TableModel(columnLabels);
        this.columnHeaderForegroundColor = null;
        this.columnHeaderBackgroundColor = null;
        this.columnHeaderModifiers = EnumSet.of(SGR.UNDERLINE, SGR.BOLD);

        for(int columnIndex = 0; columnIndex < columnLabels.length; columnIndex++) {
            getRenderer().columnAdded(this, columnIndex);
        }
        getRenderer().labelsUpdated(this);
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
        synchronized (tableModel) {
            tableModel.insertRow(index, components);
            getRenderer().rowAdded(this, index);
            int columnIndex = 0;
            for (Component component : components) {
                takeOwnership(component, index, columnIndex++);
            }
            invalidate();
            return this;
        }
    }

    public Table addColumn(String label, String... textLabelsInColumn) {
        ArrayList<Component> components = new ArrayList<Component>();
        for(String rowTextLabel: textLabelsInColumn) {
            components.add(new Label(rowTextLabel));
        }
        return addColumn(label, components.toArray(new Component[components.size()]));
    }

    public Table addColumn(String label, Component... componentsInColumn) {
        insertColumn(getColumnCount(), label, componentsInColumn);
        return this;
    }

    public Table insertColumn(int columnIndex, String label, Component... componentsInColumn) {
        synchronized (tableModel) {
            tableModel.insertColumn(columnIndex, label, componentsInColumn);
            getRenderer().columnAdded(this, columnIndex);
            for(int row = 0; row < componentsInColumn.length; row++) {
                Component component = componentsInColumn[row];
                takeOwnership(component, row, columnIndex);
            }
            invalidate();
            return this;
        }
    }

    public int getRowCount() {
        synchronized (tableModel) {
            return tableModel.getRowCount();
        }
    }

    public int getColumnCount() {
        synchronized (tableModel) {
            return tableModel.getColumnCount();
        }
    }

    public List<Component> getRow(int index) {
        synchronized (tableModel) {
            return tableModel.getRow(index);
        }
    }

    public String getLabel(int columnIndex) {
        return tableModel.columns.get(columnIndex);
    }

    public Table removeRow(int index) {
        synchronized (tableModel) {
            tableModel.removeRow(index);
            getRenderer().rowRemoved(this, index);
            invalidate();
            return this;
        }
    }

    public Table removeColumn(int index) {
        synchronized (tableModel) {
            tableModel.removeColumn(index);
            getRenderer().columnRemoved(this, index);
            invalidate();
            return this;
        }
    }

    public Table setCellComponent(int row, int column, Component component) {
        synchronized (tableModel) {
            tableModel.setCellComponent(row, column, component);
            takeOwnership(component, row, column);
            invalidate();
            return this;
        }
    }

    @Override
    public boolean containsComponent(Component component) {
        return component != null && component.hasParent(this);
    }

    @Override
    public boolean removeComponent(Component component) {
        synchronized (tableModel) {
            Integer[] location = tableModel.lookupMap.get(component);
            if(location != null) {
                tableModel.removeComponent(component);
                getRenderer().componentRemoved(this, location[1], location[0]);
                return true;
            }
            return false;
        }
    }

    private void takeOwnership(Component component, int row, int column) {
        if(component == null) {
            getRenderer().componentRemoved(this, column, row);
            return;
        }
        getRenderer().componentAdded(this, component, column, row);
        if(component.getParent() == this) {
            return;
        }
        else if(component.getParent() != null) {
            component.getParent().removeComponent(component);
        }
        component.onAdded(this);
    }

    @Override
    protected TableRenderer createDefaultRenderer() {
        return new DefaultTableRenderer();
    }

    @Override
    protected TableRenderer getRenderer() {
        return (TableRenderer)super.getRenderer();
    }

    @Override
    public int getChildCount() {
        return getChildren().size();
    }

    @Override
    public Collection<Component> getChildren() {
        return tableModel.getAllComponents();
    }

    @Override
    public Interactable nextFocus(Interactable fromThis) {
        boolean chooseNextAvailable = (fromThis == null);

        List<Component> components = tableModel.getAllComponentsSorted();
        for (Component component : components) {
            if (chooseNextAvailable) {
                if (component instanceof Interactable) {
                    return (Interactable) component;
                }
                else if (component instanceof Container) {
                    Interactable firstInteractable = ((Container)(component)).nextFocus(null);
                    if (firstInteractable != null) {
                        return firstInteractable;
                    }
                }
                continue;
            }

            if (component == fromThis) {
                chooseNextAvailable = true;
                continue;
            }

            if (component instanceof Container) {
                Container container = (Container) component;
                if (fromThis.isInside(container)) {
                    Interactable next = container.nextFocus(fromThis);
                    if (next == null) {
                        chooseNextAvailable = true;
                    } else {
                        return next;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Interactable previousFocus(Interactable fromThis) {
        boolean chooseNextAvailable = (fromThis == null);

        List<Component> revComponents = tableModel.getAllComponentsSorted();
        Collections.reverse(revComponents);

        for (Component component : revComponents) {
            if (chooseNextAvailable) {
                if (component instanceof Interactable) {
                    return (Interactable) component;
                }
                if (component instanceof Container) {
                    Interactable lastInteractable = ((Container)(component)).previousFocus(null);
                    if (lastInteractable != null) {
                        return lastInteractable;
                    }
                }
                continue;
            }

            if (component == fromThis) {
                chooseNextAvailable = true;
                continue;
            }

            if (component instanceof Container) {
                Container container = (Container) component;
                if (fromThis.isInside(container)) {
                    Interactable next = container.previousFocus(fromThis);
                    if (next == null) {
                        chooseNextAvailable = true;
                    } else {
                        return next;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        return false;
    }

    @Override
    public void updateLookupMap(InteractableLookupMap interactableLookupMap) {
        for(Component component: getChildren()) {
            if(component instanceof Container) {
                ((Container)component).updateLookupMap(interactableLookupMap);
            }
            else if(component instanceof Interactable) {
                interactableLookupMap.add((Interactable)component);
            }
        }
    }

    public interface TableRenderer extends ComponentRenderer<Table> {
        void componentAdded(Table table, Component component, int column, int row);
        void componentRemoved(Table table, int column, int row);
        void rowAdded(Table table, int row);
        void rowRemoved(Table table, int row);
        void columnAdded(Table table, int column);
        void columnRemoved(Table table, int column);
        void labelsUpdated(Table table);
    }

    /**
     * Describing how table cells are separated
     */
    public enum TableCellBorderStyle {
        None,
        SingleLine,
        DoubleLine,
        EmptySpace,
    }

    public static class DefaultTableRenderer implements TableRenderer {
        private final List<Integer> columnWidths;
        private final List<Integer> rowHeights;

        private TableCellBorderStyle headerVerticalBorderStyle;
        private TableCellBorderStyle headerHorizontalBorderStyle;
        private TableCellBorderStyle cellVerticalBorderStyle;
        private TableCellBorderStyle cellHorizontalBorderStyle;

        public DefaultTableRenderer() {
            columnWidths = new ArrayList<Integer>();
            rowHeights = new ArrayList<Integer>();
            headerVerticalBorderStyle = TableCellBorderStyle.None;
            headerHorizontalBorderStyle = TableCellBorderStyle.EmptySpace;
            cellVerticalBorderStyle = TableCellBorderStyle.None;
            cellHorizontalBorderStyle = TableCellBorderStyle.EmptySpace;
        }

        /**
         * Sets the style to be used when separating the table header row from the actual "data" cells below. This will
         * cause a new line to be added under the header labels, unless set to {@code TableCellBorderStyle.None}.
         * @param headerVerticalBorderStyle Style to use to separate Table header from body
         */
        public void setHeaderVerticalBorderStyle(TableCellBorderStyle headerVerticalBorderStyle) {
            this.headerVerticalBorderStyle = headerVerticalBorderStyle;
        }

        /**
         * Sets the style to be used when separating the table header labels from each other. This will cause a new
         * column to be added in between each label, unless set to {@code TableCellBorderStyle.None}.
         * @param headerHorizontalBorderStyle Style to use when separating header columns horizontally
         */
        public void setHeaderHorizontalBorderStyle(TableCellBorderStyle headerHorizontalBorderStyle) {
            this.headerHorizontalBorderStyle = headerHorizontalBorderStyle;
        }

        /**
         * Sets the style to be used when vertically separating table cells from each other. This will cause a new line
         * to be added between every row, unless set to {@code TableCellBorderStyle.None}.
         * @param cellVerticalBorderStyle Style to use to separate table cells vertically
         */
        public void setCellVerticalBorderStyle(TableCellBorderStyle cellVerticalBorderStyle) {
            this.cellVerticalBorderStyle = cellVerticalBorderStyle;
        }

        /**
         * Sets the style to be used when horizontally separating table cells from each other. This will cause a new
         * column to be added between every row, unless set to {@code TableCellBorderStyle.None}.
         * @param cellHorizontalBorderStyle Style to use to separate table cells horizontally
         */
        public void setCellHorizontalBorderStyle(TableCellBorderStyle cellHorizontalBorderStyle) {
            this.cellHorizontalBorderStyle = cellHorizontalBorderStyle;
        }

        private boolean isHorizontallySpaced() {
            return headerHorizontalBorderStyle != TableCellBorderStyle.None ||
                    cellHorizontalBorderStyle != TableCellBorderStyle.None;
        }

        @Override
        public void rowAdded(Table table, int row) {
            rowHeights.add(row, 0);
        }

        @Override
        public void rowRemoved(Table table, int row) {
            rowHeights.remove(row);
        }

        @Override
        public void columnAdded(Table table, int column) {
            columnWidths.add(column, CJKUtils.getTrueWidth(table.getLabel(column)));
        }

        @Override
        public void columnRemoved(Table table, int column) {
            columnWidths.remove(column);
        }

        @Override
        public void componentAdded(Table table, Component component, int column, int row) {
            TerminalSize preferredSize = component.getPreferredSize();
            if(preferredSize.getColumns() > columnWidths.get(column)) {
                columnWidths.set(column, preferredSize.getColumns());
            }
            if(preferredSize.getRows() > rowHeights.get(row)) {
                rowHeights.set(row, preferredSize.getRows());
            }
        }

        @Override
        public void componentRemoved(Table table, int column, int row) {
            int largestHeight = 0;
            for(Component component: table.getRow(row)) {
                if(component != null) {
                    int preferredSizeRows = component.getPreferredSize().getRows();
                    if(preferredSizeRows > largestHeight) {
                        largestHeight = preferredSizeRows;
                    }
                }
            }
            rowHeights.set(row, largestHeight);

            int largestWidth = 0;
            for(int rowIndex = 0; rowIndex < table.getRowCount(); rowIndex++) {
                Component component = table.getRow(rowIndex).get(column);
                if(component != null) {
                    int preferredSizeColumns = component.getPreferredSize().getColumns();
                    if(preferredSizeColumns > largestWidth) {
                        largestWidth = preferredSizeColumns;
                    }
                }
            }
            columnWidths.set(column, largestWidth);
        }

        @Override
        public void labelsUpdated(Table table) {
            for(int column = 0; column < table.getColumnCount(); column++) {
                columnWidths.set(column, Math.max(columnWidths.get(column), CJKUtils.getTrueWidth(table.getLabel(column))));
            }
        }

        @Override
        public TerminalSize getPreferredSize(Table component) {
            if(columnWidths.isEmpty() || rowHeights.isEmpty()) {
                return TerminalSize.ZERO;
            }
            int preferredColumns = 0;
            for(Integer columnWidth: columnWidths) {
                preferredColumns += columnWidth;
            }

            int preferredRows = 0;
            for(Integer rowHeight: rowHeights) {
                preferredRows += rowHeight;
            }
            preferredRows++;    //Header

            if(headerVerticalBorderStyle != TableCellBorderStyle.None) {
                preferredRows++;    //Spacing between header and body
            }
            if(cellVerticalBorderStyle != TableCellBorderStyle.None) {
                if(!rowHeights.isEmpty()) {
                    preferredRows += rowHeights.size() - 1; //Vertical space between cells
                }
            }
            if(isHorizontallySpaced()) {
                if(!columnWidths.isEmpty()) {
                    preferredColumns += columnWidths.size() - 1;    //Spacing between the columns
                }
            }
            return new TerminalSize(preferredColumns, preferredRows);
        }

        @Override
        public void drawComponent(TextGUIGraphics graphics, Table table) {
            TerminalSize area = graphics.getSize();

            //Don't even bother
            if(area.getRows() == 0 || area.getColumns() == 0) {
                return;
            }

            graphics.applyThemeStyle(graphics.getThemeDefinition(Table.class).getNormal());
            if(table.columnHeaderForegroundColor != null) {
                graphics.setForegroundColor(table.columnHeaderForegroundColor);
            }
            if(table.columnHeaderBackgroundColor != null) {
                graphics.setBackgroundColor(table.columnHeaderBackgroundColor);
            }
            int topPosition = 0;
            int leftPosition = 0;
            for(int index = 0; index < columnWidths.size(); index++) {
                String label = table.tableModel.columns.get(index);
                graphics.putString(leftPosition, 0, label, table.columnHeaderModifiers);
                leftPosition += columnWidths.get(index);
                if(headerHorizontalBorderStyle != TableCellBorderStyle.None) {
                    graphics.setCharacter(leftPosition, 0, getVerticalCharacter(headerHorizontalBorderStyle));
                    leftPosition++;
                }
            }
            topPosition++;

            if(headerVerticalBorderStyle != TableCellBorderStyle.None) {
                leftPosition = 0;
                for(int i = 0; i < columnWidths.size(); i++) {
                    if(i > 0) {
                        graphics.setCharacter(
                                leftPosition,
                                topPosition,
                                getJunctionCharacter(
                                        headerVerticalBorderStyle,
                                        headerHorizontalBorderStyle,
                                        cellHorizontalBorderStyle));
                        leftPosition++;
                    }
                    int columnWidth = columnWidths.get(i);
                    graphics.drawLine(leftPosition, topPosition, leftPosition + columnWidth - 1, topPosition, getHorizontalCharacter(headerVerticalBorderStyle));
                    leftPosition += columnWidth;
                }
                topPosition++;
            }

            for(int rowIndex = 0; rowIndex < table.getRowCount(); rowIndex++) {
                leftPosition = 0;
                List<Component> rowComponents = table.tableModel.getRow(rowIndex);
                for(int columnIndex = 0; columnIndex < rowComponents.size(); columnIndex++) {
                    if(columnIndex > 0) {
                        graphics.setCharacter(leftPosition, topPosition, getVerticalCharacter(cellHorizontalBorderStyle));
                        leftPosition++;
                    }
                    Component component = rowComponents.get(columnIndex);
                    if(component != null) {
                        TerminalSize componentArea = new TerminalSize(columnWidths.get(columnIndex), rowHeights.get(rowIndex));
                        component.draw(graphics.newTextGraphics(new TerminalPosition(leftPosition, topPosition), componentArea));
                    }
                    leftPosition += columnWidths.get(columnIndex);
                    if(leftPosition > area.getColumns()) {
                        break;
                    }
                }
                topPosition += rowHeights.get(rowIndex);
                if(cellVerticalBorderStyle != TableCellBorderStyle.None) {
                    leftPosition = 0;
                    for(int i = 0; i < columnWidths.size(); i++) {
                        if(i > 0) {
                            graphics.setCharacter(
                                    leftPosition,
                                    topPosition,
                                    getJunctionCharacter(
                                            cellVerticalBorderStyle,
                                            cellHorizontalBorderStyle,
                                            cellHorizontalBorderStyle));
                            leftPosition++;
                        }
                        int columnWidth = columnWidths.get(i);
                        graphics.drawLine(leftPosition, topPosition, leftPosition + columnWidth - 1, topPosition, getHorizontalCharacter(cellVerticalBorderStyle));
                        leftPosition += columnWidth;
                    }
                    topPosition += 1;
                }
                if(topPosition > area.getRows()) {
                    break;
                }
            }
        }

        private char getHorizontalCharacter(TableCellBorderStyle style) {
            switch(style) {
                case SingleLine:
                    return Symbols.SINGLE_LINE_HORIZONTAL;
                case DoubleLine:
                    return Symbols.DOUBLE_LINE_HORIZONTAL;
            }
            return ' ';
        }

        private char getVerticalCharacter(TableCellBorderStyle style) {
            switch(style) {
                case SingleLine:
                    return Symbols.SINGLE_LINE_VERTICAL;
                case DoubleLine:
                    return Symbols.DOUBLE_LINE_VERTICAL;
            }
            return ' ';
        }

        private char getJunctionCharacter(TableCellBorderStyle mainStyle, TableCellBorderStyle styleAbove, TableCellBorderStyle styleBelow) {
            if(mainStyle == TableCellBorderStyle.SingleLine) {
                if(styleAbove == TableCellBorderStyle.SingleLine) {
                    if(styleBelow == TableCellBorderStyle.SingleLine) {
                        return Symbols.SINGLE_LINE_CROSS;
                    }
                    else if(styleBelow == TableCellBorderStyle.DoubleLine) {
                        //There isn't any character for this, give upper side priority
                        return Symbols.SINGLE_LINE_T_UP;
                    }
                    else {
                        return Symbols.SINGLE_LINE_T_UP;
                    }
                }
                else if(styleAbove == TableCellBorderStyle.DoubleLine) {
                    if(styleBelow == TableCellBorderStyle.SingleLine) {
                        //There isn't any character for this, give upper side priority
                        return Symbols.SINGLE_LINE_T_DOUBLE_UP;
                    }
                    else if(styleBelow == TableCellBorderStyle.DoubleLine) {
                        return Symbols.DOUBLE_LINE_VERTICAL_SINGLE_LINE_CROSS;
                    }
                    else {
                        return Symbols.SINGLE_LINE_T_DOUBLE_UP;
                    }
                }
                else {
                    if(styleBelow == TableCellBorderStyle.SingleLine) {
                        return Symbols.SINGLE_LINE_T_DOWN;
                    }
                    else if(styleBelow == TableCellBorderStyle.DoubleLine) {
                        return Symbols.SINGLE_LINE_T_DOUBLE_DOWN;
                    }
                    else {
                        return Symbols.SINGLE_LINE_HORIZONTAL;
                    }
                }
            }
            else if(mainStyle == TableCellBorderStyle.DoubleLine) {
                if(styleAbove == TableCellBorderStyle.SingleLine) {
                    if(styleBelow == TableCellBorderStyle.SingleLine) {
                        return Symbols.DOUBLE_LINE_HORIZONTAL_SINGLE_LINE_CROSS;
                    }
                    else if(styleBelow == TableCellBorderStyle.DoubleLine) {
                        //There isn't any character for this, give upper side priority
                        return Symbols.DOUBLE_LINE_T_SINGLE_UP;
                    }
                    else {
                        return Symbols.DOUBLE_LINE_T_SINGLE_UP;
                    }
                }
                else if(styleAbove == TableCellBorderStyle.DoubleLine) {
                    if(styleBelow == TableCellBorderStyle.SingleLine) {
                        //There isn't any character for this, give upper side priority
                        return Symbols.DOUBLE_LINE_T_UP;
                    }
                    else if(styleBelow == TableCellBorderStyle.DoubleLine) {
                        return Symbols.DOUBLE_LINE_CROSS;
                    }
                    else {
                        return Symbols.DOUBLE_LINE_T_UP;
                    }
                }
                else {
                    if(styleBelow == TableCellBorderStyle.SingleLine) {
                        return Symbols.DOUBLE_LINE_T_SINGLE_DOWN;
                    }
                    else if(styleBelow == TableCellBorderStyle.DoubleLine) {
                        return Symbols.DOUBLE_LINE_T_DOWN;
                    }
                    else {
                        return Symbols.DOUBLE_LINE_HORIZONTAL;
                    }
                }
            }
            else {
                return ' ';
            }
        }
    }

    private class TableModel {
        private final List<String> columns;
        private final List<List<Component>> rows;
        private final Map<Component, Integer[]> lookupMap;

        TableModel(String... columnLabels) {
            this.columns = new ArrayList<String>(Arrays.asList(columnLabels));
            this.rows = new ArrayList<List<Component>>();
            this.lookupMap = new IdentityHashMap<Component, Integer[]>();
        }

        Collection<Component> getAllComponents() {
            return Collections.unmodifiableCollection(lookupMap.keySet());
        }

        List<Component> getAllComponentsSorted() {
            List<Component> components = new ArrayList<Component>();
            for(List<Component> row: rows) {
                for(Component component: row) {
                    if(component != null) {
                        components.add(component);
                    }
                }
            }
            return components;
        }

        public List<Component> getRow(int index) {
            return Collections.unmodifiableList(rows.get(index));
        }

        void insertRow(int index, Collection<? extends Component> components) {
            ArrayList<Component> list = new ArrayList<Component>(components);
            while(list.size() < columns.size()) {
                list.add(null);
            }
            rows.add(index, list);
            recalculateLookupMapFromRow(index);
        }

        void insertColumn(int index, String label, Component[] newColumnComponents) {
            columns.add(index, label);
            for(int i = 0; i < rows.size(); i++) {
                if(i < newColumnComponents.length && newColumnComponents[i] != null) {
                    rows.get(i).add(index, newColumnComponents[i]);
                }
                else {
                    rows.get(i).add(index, null);
                }
            }
            recalculateLookupMapFromColumn(index);
        }

        int getRowCount() {
            return rows.size();
        }

        void removeRow(int index) {
            for(Component component: rows.get(index)) {
                removeComponent(component);
            }
            rows.remove(index);
            recalculateLookupMapFromRow(index);
        }

        void removeColumn(int index) {
            columns.remove(index);
            for(List<Component> row: rows) {
                removeComponent(row.get(index));
                row.remove(index);
            }
            recalculateLookupMapFromColumn(index);
        }

        void setCellComponent(int row, int column, Component component) {
            if(row < 0 || row >= getRowCount() ||
                    column < 0 || column >= columns.size()) {
                throw new IndexOutOfBoundsException("Table has " + columns.size() + " columns and " + getRowCount() +
                        " rows, cannot set component at column " + column + ", row " + row);
            }
            Component existingComponent = rows.get(row).get(column);
            if(existingComponent == component) {
                return;
            }
            removeComponent(existingComponent);
            rows.get(row).set(column, component);
            lookupMap.put(component, new Integer[]{row, column});
        }

        boolean removeComponent(Component component) {
            if(component == null || !lookupMap.containsKey(component)) {
                return false;
            }
            int row = lookupMap.get(component)[0];
            int column = lookupMap.get(component)[1];
            lookupMap.remove(component);
            rows.get(row).set(column, null);
            component.onRemoved(Table.this);
            return true;
        }

        int getColumnCount() {
            return columns.size();
        }

        void recalculateLookupMapFromColumn(int fromColumnIndex) {
            for(int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
                List<Component> row = rows.get(rowIndex);
                for(int columnIndex = fromColumnIndex; columnIndex < row.size(); columnIndex++) {
                    Component component = row.get(columnIndex);
                    if(component != null) {
                        lookupMap.put(component, new Integer[] { rowIndex, columnIndex });
                    }
                }
            }
        }

        void recalculateLookupMapFromRow(int fromRowIndex) {
            for(int rowIndex = fromRowIndex; rowIndex < rows.size(); rowIndex++) {
                List<Component> row = rows.get(rowIndex);
                for(int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
                    Component component = row.get(columnIndex);
                    if(component != null) {
                        lookupMap.put(component, new Integer[] { rowIndex, columnIndex });
                    }
                }
            }
        }
    }
}
