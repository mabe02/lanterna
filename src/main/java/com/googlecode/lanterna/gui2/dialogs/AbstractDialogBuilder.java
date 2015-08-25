package com.googlecode.lanterna.gui2.dialogs;

import com.googlecode.lanterna.gui2.Window;

import java.util.Set;

/**
 * Created by martin on 23/06/15.
 */
public abstract class AbstractDialogBuilder<B, T extends DialogWindow> {
    protected String title;
    protected String description;
    protected boolean centered;

    public AbstractDialogBuilder(String title) {
        this.title = title;
        this.description = null;
        this.centered = true;
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

    public B setCentered(boolean centered) {
        this.centered = centered;
        return self();
    }

    protected abstract B self();

    protected abstract T buildDialog();

    public final T build() {
        T dialog = buildDialog();
        if(centered) {
            Set<Window.Hint> hints = dialog.getHints();
            hints.add(Window.Hint.CENTERED);
            dialog.setHints(hints);
        }
        return dialog;
    }
}
