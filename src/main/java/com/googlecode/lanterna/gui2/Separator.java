package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.ThemeDefinition;

/**
 * Static non-interactive component that is typically rendered as a single line. Normally this component is used to
 * separate component from each other in situations where a bordered panel isn't ideal. By default the separator will
 * ask for a size of 1x1 so you'll need to make it bigger, either through the layout manager or by overriding the
 * preferred size.
 * @author Martin
 */
public class Separator extends AbstractRenderableComponent {

    private final Direction direction;

    public Separator(Direction direction) {
        if(direction == null) {
            throw new IllegalArgumentException("Cannot create a separator with a null direction");
        }
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    protected ComponentRenderer createDefaultRenderer() {
        return new DefaultSeparatorRenderer();
    }

    public static abstract class SeparatorRenderer implements ComponentRenderer<Separator> {
    }

    public static class DefaultSeparatorRenderer extends SeparatorRenderer {
        @Override
        public TerminalSize getPreferredSize(Separator component) {
            return TerminalSize.ONE;
        }

        @Override
        public void drawComponent(TextGUIGraphics graphics, Separator component) {
            ThemeDefinition themeDefinition = graphics.getThemeDefinition(Separator.class);
            graphics.applyThemeStyle(themeDefinition.getNormal());
            char character = themeDefinition.getCharacter(component.getDirection().name().toUpperCase(),
                    component.getDirection() == Direction.HORIZONTAL ? Symbols.SINGLE_LINE_HORIZONTAL : Symbols.SINGLE_LINE_VERTICAL);
            graphics.fill(character);
        }
    }
}
