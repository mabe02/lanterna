package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.ThemeDefinition;

/**
 * Special component that is by default displayed as the background of a text gui unless you override it with something
 * else. Themes can control how this backdrop is drawn, the normal is one solid color.
 */
public class GUIBackdrop extends EmptySpace {
    @Override
    protected ComponentRenderer<EmptySpace> createDefaultRenderer() {
        return new ComponentRenderer<EmptySpace>() {

            @Override
            public TerminalSize getPreferredSize(EmptySpace component) {
                return TerminalSize.ONE;
            }

            @Override
            public void drawComponent(TextGUIGraphics graphics, EmptySpace component) {
                ThemeDefinition themeDefinition = graphics.getThemeDefinition(GUIBackdrop.class);
                graphics.applyThemeStyle(themeDefinition.getNormal());
                graphics.fill(themeDefinition.getCharacter("BACKGROUND", ' '));
            }
        };
    }
}
