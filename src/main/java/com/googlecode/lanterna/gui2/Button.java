package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;

/**
 * Created by martin on 17/09/14.
 */
public class Button extends AbstractInteractableComponent {
    private final String label;

    public Button(String label) {
        this.label = label;
    }

    @Override
    public TerminalPosition getCursorLocation() {
        return new TerminalPosition(3, 0);
    }

    @Override
    protected TerminalSize getPreferredSizeWithoutBorder() {
        return new TerminalSize(label.length() + 6, 1);
    }

    @Override
    public Result handleKeyStroke(KeyStroke keyStroke) {
        return Result.UNHANDLED;
    }

    @Override
    public void drawComponent(TextGUIGraphics graphics) {
        graphics.setBackgroundColor(TextColor.ANSI.RED);
        graphics.setForegroundColor(TextColor.ANSI.BLACK);
        graphics.putString(0, 0, " < " + label + " > ");
    }
}
