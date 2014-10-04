package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by martin on 29/09/14.
 */
public class CheckBoxList extends AbstractListBox {
    private final List<Boolean> itemStatus;

    public CheckBoxList() {
        this(null);
    }

    public CheckBoxList(TerminalSize preferredSize) {
        super(preferredSize);
        this.itemStatus = new ArrayList<Boolean>();
    }

    @Override
    public void clearItems() {
        itemStatus.clear();
        super.clearItems();
    }

    @Override
    public void addItem(Object object) {
        itemStatus.add(Boolean.FALSE);
        super.addItem(object);
    }

    public Boolean isChecked(Object object) {
        if(indexOf(object) == -1)
            return null;

        return itemStatus.get(indexOf(object));
    }

    public Boolean isChecked(int index) {
        if(index < 0 || index >= itemStatus.size())
            return null;

        return itemStatus.get(index);
    }

    public void setChecked(Object object, boolean checked) {
        if(indexOf(object) == -1)
            return;

        itemStatus.set(indexOf(object), checked);
    }

    @Override
    public Result handleKeyStroke(KeyStroke keyStroke) {
        if(keyStroke.getKeyType() == KeyType.Enter ||
                (keyStroke.getKeyType() == KeyType.Character && keyStroke.getCharacter() == ' ')) {
            if(itemStatus.get(getSelectedIndex()))
                itemStatus.set(getSelectedIndex(), Boolean.FALSE);
            else
                itemStatus.set(getSelectedIndex(), Boolean.TRUE);
            return Result.HANDLED;
        }
        return super.handleKeyStroke(keyStroke);
    }
/*
    @Override
    protected int getHotSpotPositionOnLine(int selectedIndex) {
        return 1;
    }

    @Override
    protected String getLabel(int index, Object item) {
        String check = " ";
        if(itemStatus.get(index))
            check = "x";

        String text = item.toString();
        return "[" + check + "] " + text;
    }
    */
}
