package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;

/**
 * Component which draws a solid color over its area. The preferred size is customizable.
 * @author Martin
 */
public class EmptySpace extends AbstractComponent {
    private final TerminalSize size;
    private TextColor color;

    /**
     * Creates an EmptySpace with a specified color and preferred size of 1x1
     * @param color Color to use (null will make it use the theme)
     */
    public EmptySpace(TextColor color) {
        this(color, TerminalSize.ONE);
    }

    /**
     * Creates an EmptySpace with a specified preferred size (color will be chosen from the theme)
     * @param size Preferred size
     */
    public EmptySpace(TerminalSize size) {
        this(null, size);
    }

    /**
     * Creates an EmptySpace with a specified color (null will make it use a color from the theme) and preferred size
     * @param color Color to use (null will make it use the theme)
     * @param size Preferred size
     */
    public EmptySpace(TextColor color, TerminalSize size) {
        this.color = color;
        this.size = size;
    }

    public void setColor(TextColor color) {
        this.color = color;
    }

    public TextColor getColor() {
        return color;
    }

    @Override
    public TerminalSize getPreferredSize() {
        return size;
    }

    @Override
    public void drawComponent(TextGUIGraphics graphics) {
        graphics.applyThemeStyle(graphics.getThemeDefinition(EmptySpace.class).getNormal());
        if(color != null) {
            graphics.setBackgroundColor(color);
        }
        graphics.fill(' ');
    }
}
