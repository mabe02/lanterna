package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import com.googlecode.lanterna.input.KeyStroke;

/**
 * Created by martin on 17/09/14.
 */
public class Button extends AbstractInteractableComponent {
    private String label;

    public Button(String label) {
        setLabel(label);
        setThemeRenderer(new DefaultButtonRenderer());
    }

    @Override
    public TerminalPosition getCursorLocation() {
        return getThemeRenderer().getCursorLocation(this);
    }

    @Override
    protected TerminalSize getPreferredSizeWithoutBorder() {
        return getThemeRenderer().getPreferredSizeWithoutBorder(this);
    }

    @Override
    public Result handleKeyStroke(KeyStroke keyStroke) {
        return super.handleKeyStroke(keyStroke);
    }

    @Override
    public void drawComponent(TextGUIGraphics graphics) {
        updateRenderer(getThemeDefinition(graphics).getRenderer());
        getThemeRenderer().drawComponent(graphics, this);
    }

    @Override
    protected ButtonRenderer getThemeRenderer() {
        return (ButtonRenderer)super.getThemeRenderer();
    }

    public final void setLabel(String label) {
        if(label == null) {
            throw new IllegalArgumentException("null label to a button is not allowed");
        }
        if(label.isEmpty()) {
            label = " ";
        }
        this.label = label;
        invalidate();
    }

    public String getLabel() {
        return label;
    }

    private static interface ButtonRenderer extends InteractableRenderer<Button> {
    }

    public static class DefaultButtonRenderer implements ButtonRenderer {
        @Override
        public TerminalPosition getCursorLocation(Button button) {
            return new TerminalPosition(1 + Math.max(0, 2 - button.getLabel().length() / 2), 0);
        }

        @Override
        public TerminalSize getPreferredSizeWithoutBorder(Button button) {
            return new TerminalSize(Math.max(8, button.getLabel().length() + 2), 1);
        }

        @Override
        public void drawComponent(TextGUIGraphics graphics, Button button) {
            if(button.isFocused()) {
                graphics.applyThemeStyle(getThemeDefinition(graphics).getActive());
            }
            else {
                graphics.applyThemeStyle(getThemeDefinition(graphics).getInsensitive());
            }
            graphics.fill(' ');
            graphics.setPosition(0, 0);
            graphics.setCharacter(getThemeDefinition(graphics).getCharacter("LEFT_BORDER", '<'));
            graphics.setPosition(graphics.getSize().getColumns() - 1, 0);
            graphics.setCharacter(getThemeDefinition(graphics).getCharacter("RIGHT_BORDER", '>'));

            int availableSpace = graphics.getSize().getColumns() - 2;
            if(availableSpace <= 0) {
                return;
            }

            int labelShift = 0;
            if(availableSpace > button.getLabel().length()) {
                labelShift = (graphics.getSize().getColumns() - 2 - button.getLabel().length()) / 2;
            }

            if(button.isFocused()) {
                graphics.applyThemeStyle(getThemeDefinition(graphics).getActive());
            }
            else {
                graphics.applyThemeStyle(getThemeDefinition(graphics).getPreLight());
            }
            graphics.setPosition(1 + labelShift, 0);
            graphics.setCharacter(button.getLabel().charAt(0));

            if(button.getLabel().length() == 1) {
                return;
            }
            if(button.isFocused()) {
                graphics.applyThemeStyle(getThemeDefinition(graphics).getSelected());
            }
            else {
                graphics.applyThemeStyle(getThemeDefinition(graphics).getNormal());
            }
            graphics.putString(1 + labelShift + 1, 0, button.getLabel().substring(1));
        }
    }

    private static ThemeDefinition getThemeDefinition(TextGUIGraphics graphics) {
        return graphics.getThemeDefinition(Button.class);
    }
}
