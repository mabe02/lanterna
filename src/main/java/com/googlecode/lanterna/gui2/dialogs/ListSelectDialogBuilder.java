package com.googlecode.lanterna.gui2.dialogs;

import com.googlecode.lanterna.TerminalSize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by martin on 23/06/15.
 */
public class ListSelectDialogBuilder<T> extends AbstractDialogBuilder<ListSelectDialogBuilder<T>, ListSelectDialog<T>> {
    private TerminalSize listBoxSize;
    private boolean canCancel;
    private List<T> content;

    public ListSelectDialogBuilder() {
        super("ListSelectDialog");
        this.listBoxSize = null;
        this.canCancel = true;
        this.content = new ArrayList<T>();
    }

    @Override
    protected ListSelectDialogBuilder<T> self() {
        return this;
    }

    @Override
    protected ListSelectDialog<T> buildDialog() {
        return new ListSelectDialog<T>(
                title,
                description,
                listBoxSize,
                canCancel,
                content);
    }

    public ListSelectDialogBuilder<T> setListBoxSize(TerminalSize listBoxSize) {
        this.listBoxSize = listBoxSize;
        return this;
    }

    public TerminalSize getListBoxSize() {
        return listBoxSize;
    }

    public ListSelectDialogBuilder<T> setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
        return this;
    }

    public boolean isCanCancel() {
        return canCancel;
    }

    public ListSelectDialogBuilder<T> addListItem(T item) {
        this.content.add(item);
        return this;
    }

    public ListSelectDialogBuilder<T> addListItems(T... items) {
        this.content.addAll(Arrays.asList(items));
        return this;
    }

    public List<T> getListItems() {
        return new ArrayList<T>(content);
    }
}
