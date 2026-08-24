package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

import static java.lang.Math.max;
import static java.lang.Math.min;

public final class Rectangle {
    public static final Rectangle EMPTY = new Rectangle(TerminalPosition.TOP_LEFT_CORNER, TerminalSize.ZERO);

    private final TerminalSize size;
    private final TerminalPosition position;

    private Rectangle(final TerminalPosition position, final TerminalSize size) {
        if (size == null) {
            throw new NullPointerException("Size cannot be null.");
        }
        if (position == null) {
            throw new NullPointerException("Position cannot be null.");
        }

        this.size = size;
        this.position = position;
    }

    public static Rectangle of(final TerminalPosition position, final TerminalSize size) {
        return new Rectangle(position, size);
    }

    public static Rectangle of(final TerminalSize size) {
        return new Rectangle(TerminalPosition.TOP_LEFT_CORNER, size);
    }

    public static Rectangle of(final int x, final int y, final int width, final int height) {
        return new Rectangle(new TerminalPosition(x, y), new TerminalSize(width, height));
    }

    public TerminalSize getSize() {
        return size;
    }

    public TerminalPosition getPosition() {
        return position;
    }

    public int getWidth() {
        return size.getColumns();
    }

    public int getHeight() {
        return size.getRows();
    }

    public int getLeft() {
        return position.getColumn();
    }

    public int getTop() {
        return position.getRow();
    }

    public int getRight() {
        return getLeft() + getWidth() - 1;
    }

    public int getBottom() {
        return getTop() + getHeight() - 1;
    }

    public boolean isEmpty() {
        return size.getColumns() == 0 && size.getRows() == 0;
    }

    public boolean intersectsWith(final Rectangle r) {
        return !isEmpty() &&
               !r.isEmpty() &&
               r.getLeft() <= getRight() &&
               r.getRight() >= getLeft() &&
               r.getTop() <= getBottom() &&
               r.getBottom() >= getTop();
    }

    public Rectangle intersection(final Rectangle r) {
        if (!this.intersectsWith(r)) {
            return EMPTY;
        }

        return new Rectangle(
            new TerminalPosition(
                max(getLeft(), r.getLeft()),
                max(getTop(), r.getTop())
            ),
            new TerminalSize(
                max(min(getRight(), r.getRight()) - max(getLeft(), r.getLeft()), 0),
                max(min(getBottom(), r.getBottom()) - max(getTop(), r.getTop()), 0)
            )
        );
    }

    public Rectangle union(final Rectangle rectangle) {
        if (isEmpty()) {
            return rectangle;
        }

        if (rectangle.isEmpty()) {
            return this;
        }

        final int left = min(getLeft(), rectangle.getLeft());
        final int top = min(getTop(), rectangle.getTop());
        final int width = max(max(getRight(), rectangle.getRight()) - left, 0);
        final int height = max(max(getBottom(), rectangle.getBottom()) - top, 0);

        return new Rectangle(new TerminalPosition(left, top), new TerminalSize(width, height));
    }

    public Rectangle offset(final TerminalPosition relativeOffset) {
        return offset(relativeOffset.getColumn(), relativeOffset.getRow());
    }

    public Rectangle offset(final int deltaX, final int deltaY) {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot offset an empty Rectangle.");
        }

        if (deltaX == 0 && deltaY == 0) {
            return this;
        }

        return new Rectangle(
            new TerminalPosition(getLeft() + deltaX, getTop() + deltaY),
            getSize()
        );
    }

    public boolean contains(final int column, final int row) {
        return column >= getLeft() &&
               column <= getRight() &&
               row >= getTop() &&
               row <= getBottom();
    }

    public boolean contains(final TerminalPosition position) {
        return contains(position.getColumn(), position.getRow());
    }

    public boolean contains(final Rectangle rectangle) {
        return rectangle.getLeft() >= getLeft() &&
               rectangle.getRight() <= getRight() &&
               rectangle.getTop() >= getTop() &&
               rectangle.getBottom() <= getBottom();
    }

    @Override
    public String toString() {
        return "[" + getWidth() + 'x' + getHeight() + " at " + getLeft() + ',' + getTop() + ']';
    }
}
