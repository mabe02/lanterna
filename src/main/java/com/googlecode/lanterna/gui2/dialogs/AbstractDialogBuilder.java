package com.googlecode.lanterna.gui2.dialogs;

import com.googlecode.lanterna.gui2.Window;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by martin on 23/06/15.
 */
public abstract class AbstractDialogBuilder<B, T extends DialogWindow> {
    protected String title;
    protected String description;
    protected Set<Window.Hint> extraWindowHints;

    public AbstractDialogBuilder(String title) {
        this.title = title;
        this.description = null;
        this.extraWindowHints = Collections.singleton(Window.Hint.CENTERED);
    }

    public B setTitle(String title) {
        if(title == null) {
            title = "";
        }
        this.title = title;
        return self();
    }

    public String getTitle() {
        return title;
    }

    public B setDescription(String description) {
        this.description = description;
        return self();
    }

    public String getDescription() {
        return description;
    }

    public void setExtraWindowHints(Set<Window.Hint> extraWindowHints) {
        this.extraWindowHints = extraWindowHints;
    }

    public Set<Window.Hint> getExtraWindowHints() {
        return extraWindowHints;
    }

    protected abstract B self();

    protected abstract T buildDialog();

    public final T build() {
        T dialog = buildDialog();
        if(!extraWindowHints.isEmpty()) {
            Set<Window.Hint> combinedHints = new HashSet<Window.Hint>(dialog.getHints());
            combinedHints.addAll(extraWindowHints);
            dialog.setHints(combinedHints);
        }
        return dialog;
    }
}
