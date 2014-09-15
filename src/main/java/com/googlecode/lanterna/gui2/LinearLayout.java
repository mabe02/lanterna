package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

import java.util.List;

/**
 * Simple layout manager the puts all components on a single line
 */
public class LinearLayout implements LayoutManager {
    public static enum Direction {
        HORIZONTAL, //See? I can spell it!
        VERTICAL,
        ;
    }

    private final Direction direction;

    public LinearLayout() {
        this(Direction.VERTICAL);
    }

    public LinearLayout(Direction direction) {
        this.direction = direction;
    }

    @Override
    public TerminalSize getPreferredSize(List<Component> components) {
        int maxWidth = 0;
        int height = 0;
        for(Component component: components) {
            TerminalSize preferredSize = component.getPreferredSize();
            if(maxWidth < preferredSize.getColumns()) {
                maxWidth = preferredSize.getColumns();
            }
            height += preferredSize.getRows();
        }
        return new TerminalSize(maxWidth, height);
    }

    @Override
    public void doLayout(TerminalSize area, List<Component> components) {
        int remainingVerticalSpace = area.getRows();
        int availableHorizontalSpace = area.getColumns();
        for(Component component: components) {
            if(remainingVerticalSpace <= 0) {
                component.setPosition(TerminalPosition.TOP_LEFT_CORNER);
                component.setSize(TerminalSize.ZERO);
            }
            else {
                TerminalSize preferredSize = component.getPreferredSize();
                TerminalSize decidedSize = new TerminalSize(
                        Math.min(availableHorizontalSpace, preferredSize.getColumns()),
                        Math.min(remainingVerticalSpace, preferredSize.getRows()));

                component.setPosition(component.getPosition().withColumn(0).withRow(area.getRows() - remainingVerticalSpace));
                component.setSize(component.getSize().withColumns(decidedSize.getColumns()).withRows(decidedSize.getRows()));
                remainingVerticalSpace -= decidedSize.getRows();
            }
        }
    }
}
