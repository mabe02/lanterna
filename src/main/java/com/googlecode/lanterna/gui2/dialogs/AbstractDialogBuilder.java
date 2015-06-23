package com.googlecode.lanterna.gui2.dialogs;

/**
 * Created by martin on 23/06/15.
 */
public abstract class AbstractDialogBuilder<B, T> {
    protected String title;
    protected String description;

    public AbstractDialogBuilder(String title) {
        this.title = title;
        this.description = null;
    }

    public B setTitle(String title) {
        if(title == null) {
            title = "";
        }
        this.title = title;
        return self();
    }

    public B setDescription(String description) {
        this.description = description;
        return self();
    }

    protected abstract B self();

    public abstract T build();
}
