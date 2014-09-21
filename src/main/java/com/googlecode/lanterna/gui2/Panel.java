package com.googlecode.lanterna.gui2;

/**
 * Created by martin on 21/09/14.
 */
public class Panel extends AbstractInteractableContainer {


    @Override
    public Panel withBorder(Border border) {
        super.withBorder(border);
        return this;
    }
}
