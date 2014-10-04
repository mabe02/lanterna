package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

/**
 * Created by martin on 04/10/14.
 */
public class ActionListBox extends AbstractListBox {

    public ActionListBox() {
        this(null);
    }

    public ActionListBox(TerminalSize preferredSize) {
        super(preferredSize);
    }

    @Override
    public void addItem(Object object) {
        if(object instanceof Runnable == false) {
            throw new IllegalArgumentException("Can only add objects implementing Runnable to ActionListBox, got " + object);
        }
        super.addItem(object);
    }

    public void addItem(final String label, final Runnable action) {
        addItem(new Runnable() {
            @Override
            public void run() {
                action.run();
            }

            @Override
            public String toString() {
                return label;
            }
        });
    }

    @Override
    public TerminalPosition getCursorLocation() {
        return null;
    }

    @Override
    public Result handleKeyStroke(KeyStroke keyStroke) {
        Object selectedItem = getSelectedItem();
        if(selectedItem != null &&
                keyStroke.getKeyType() == KeyType.Enter ||
                (keyStroke.getKeyType() == KeyType.Character && keyStroke.getCharacter() == ' ')) {

            ((Runnable)selectedItem).run();
            return Result.HANDLED;
        }
        return super.handleKeyStroke(keyStroke);
    }
}
