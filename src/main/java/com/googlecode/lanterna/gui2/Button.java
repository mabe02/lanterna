package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

/**
 * Simple labeled button with an option action attached to it. You trigger the action by pressing the Enter key on the
 * keyboard.
 * @author Martin
 */
public class Button extends AbstractInteractableComponent {
    private final Runnable action;
    private String label;

    public Button(String label) {
        this(label, new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    public Button(String label, Runnable action) {
        this.action = action;
        setLabel(label);
        setThemeRenderer(new DefaultButtonRenderer());
    }

    @Override
    public TerminalPosition getCursorLocation() {
        return getThemeRenderer().getCursorLocation(this);
    }

    @Override
    public TerminalSize getPreferredSize() {
        return getThemeRenderer().getPreferredSize(this);
    }

    @Override
    public Result handleKeyStroke(KeyStroke keyStroke) {
        if(keyStroke.getKeyType() == KeyType.Enter) {
            action.run();
            return Result.HANDLED;
        }
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

    @Override
    public String toString() {
        return "Button{" + label + "}";
    }

    private static interface ButtonRenderer extends InteractableRenderer<Button> {
    }

    public static class DefaultButtonRenderer implements ButtonRenderer {
        @Override
        public TerminalPosition getCursorLocation(Button button) {
            return new TerminalPosition(1 + getLabelShift(button, button.getSize()), 0);
        }

        @Override
        public TerminalSize getPreferredSize(Button button) {
            TerminalSize preferredSize = new TerminalSize(Math.max(8, button.getLabel().length() + 2), 1);
            return preferredSize;
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

            if(button.isFocused()) {
                graphics.applyThemeStyle(getThemeDefinition(graphics).getActive());
            }
            else {
                graphics.applyThemeStyle(getThemeDefinition(graphics).getPreLight());
            }
            int labelShift = getLabelShift(button, graphics.getSize());
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

        private int getLabelShift(Button button, TerminalSize size) {
            int availableSpace = size.getColumns() - 2;
            if(availableSpace <= 0) {
                return 0;
            }
            int labelShift = 0;
            if(availableSpace > button.getLabel().length()) {
                labelShift = (size.getColumns() - 2 - button.getLabel().length()) / 2;
            }
            return labelShift;
        }
    }

    private static ThemeDefinition getThemeDefinition(TextGUIGraphics graphics) {
        return graphics.getThemeDefinition(Button.class);
    }
}
