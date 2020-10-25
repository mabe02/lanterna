package com.googlecode.lanterna;

import java.util.Arrays;
import java.util.EnumSet;

abstract class AbstractTextCharacter implements TextCharacter {
    static EnumSet<SGR> toEnumSet(SGR... modifiers) {
        if(modifiers.length == 0) {
            return EnumSet.noneOf(SGR.class);
        }
        else {
            return EnumSet.copyOf(Arrays.asList(modifiers));
        }
    }

    private final TextColor foregroundColor;
    private final TextColor backgroundColor;
    private final EnumSet<SGR> modifiers;  //This isn't immutable, but we should treat it as such and not expose it!

    AbstractTextCharacter(TextColor foregroundColor, TextColor backgroundColor, EnumSet<SGR> modifiers) {
        if(foregroundColor == null) {
            foregroundColor = TextColor.ANSI.DEFAULT;
        }
        if(backgroundColor == null) {
            backgroundColor = TextColor.ANSI.DEFAULT;
        }

        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        this.modifiers = EnumSet.copyOf(modifiers);
    }

    @Override
    public TextColor getForegroundColor() {
        return foregroundColor;
    }

    @Override
    public TextColor getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public EnumSet<SGR> getModifiers() {
        return EnumSet.copyOf(modifiers);
    }

    @Override
    public boolean isBold() {
        return modifiers.contains(SGR.BOLD);
    }

    @Override
    public boolean isReversed() {
        return modifiers.contains(SGR.REVERSE);
    }

    @Override
    public boolean isUnderlined() {
        return modifiers.contains(SGR.UNDERLINE);
    }

    @Override
    public boolean isBlinking() {
        return modifiers.contains(SGR.BLINK);
    }

    @Override
    public boolean isBordered() {
        return modifiers.contains(SGR.BORDERED);
    }

    @Override
    public boolean isCrossedOut() {
        return modifiers.contains(SGR.CROSSED_OUT);
    }

    @Override
    public boolean isItalic() {
        return modifiers.contains(SGR.ITALIC);
    }
}
