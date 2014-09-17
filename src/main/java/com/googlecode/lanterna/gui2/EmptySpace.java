package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;

/**
 * Created by mberglun on 14/09/14.
 */
public class EmptySpace extends AbstractComponent {
    private final TerminalSize size;
    private TextColor color;

    public EmptySpace(TerminalSize size) {
        this.size = size;
        this.color = null;
    }

    public void setColor(TextColor color) {
        this.color = color;
    }

    public TextColor getColor() {
        return color;
    }

    @Override
    protected TerminalSize getPreferredSizeWithoutBorder() {
        return size;
    }

    @Override
    public void drawComponent(TextGUIGraphics graphics) {
        graphics.applyThemeStyle(graphics.getThemeDefinition(EmptySpace.class).getNormal());
        if(color != null) {
            graphics.setBackgroundColor(color);
        }
        graphics.fillScreen(' ');
    }
}
