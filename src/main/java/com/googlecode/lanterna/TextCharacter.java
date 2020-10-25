package com.googlecode.lanterna;

import java.util.Collection;
import java.util.EnumSet;

public interface TextCharacter {
    public static final TextCharacter DEFAULT_CHARACTER = new BMPTextCharacter(' ', TextColor.ANSI.DEFAULT, TextColor.ANSI.DEFAULT);

    /**
     * Foreground color specified for this TextCharacter
     * @return Foreground color of this TextCharacter
     */
    TextColor getForegroundColor();

    /**
     * Background color specified for this TextCharacter
     * @return Background color of this TextCharacter
     */
    TextColor getBackgroundColor();

    /**
     * Returns a set of all active modifiers on this TextCharacter
     * @return Set of active SGR codes
     */
    EnumSet<SGR> getModifiers();

    /**
     * Returns true if this TextCharacter has the bold modifier active
     * @return {@code true} if this TextCharacter has the bold modifier active
     */
    boolean isBold();

    /**
     * Returns true if this TextCharacter has the reverse modifier active
     * @return {@code true} if this TextCharacter has the reverse modifier active
     */
    boolean isReversed();

    /**
     * Returns true if this TextCharacter has the underline modifier active
     * @return {@code true} if this TextCharacter has the underline modifier active
     */
    boolean isUnderlined();

    /**
     * Returns true if this TextCharacter has the blink modifier active
     * @return {@code true} if this TextCharacter has the blink modifier active
     */
    boolean isBlinking();

    /**
     * Returns true if this TextCharacter has the bordered modifier active
     * @return {@code true} if this TextCharacter has the bordered modifier active
     */
    boolean isBordered();

    /**
     * Returns true if this TextCharacter has the crossed-out modifier active
     * @return {@code true} if this TextCharacter has the crossed-out modifier active
     */
    boolean isCrossedOut();

    /**
     * Returns true if this TextCharacter has the italic modifier active
     * @return {@code true} if this TextCharacter has the italic modifier active
     */
    boolean isItalic();

    boolean is(char otherCharacter);

    /**
     * Returns a new TextCharacter with the same colors and modifiers but a different underlying character
     * @param character Character the copy should have
     * @return Copy of this TextCharacter with different underlying character
     */
    @SuppressWarnings("SameParameterValue")
    TextCharacter withCharacter(char character);

    /**
     * Returns a copy of this TextCharacter with a specified foreground color
     * @param foregroundColor Foreground color the copy should have
     * @return Copy of the TextCharacter with a different foreground color
     */
    TextCharacter withForegroundColor(TextColor foregroundColor);

    /**
     * Returns a copy of this TextCharacter with a specified background color
     * @param backgroundColor Background color the copy should have
     * @return Copy of the TextCharacter with a different background color
     */
    TextCharacter withBackgroundColor(TextColor backgroundColor);

    /**
     * Returns a copy of this TextCharacter with specified list of SGR modifiers. None of the currently active SGR codes
     * will be carried over to the copy, only those in the passed in value.
     * @param modifiers SGR modifiers the copy should have
     * @return Copy of the TextCharacter with a different set of SGR modifiers
     */
    TextCharacter withModifiers(Collection<SGR> modifiers);

    /**
     * Returns a copy of this TextCharacter with an additional SGR modifier. All of the currently active SGR codes
     * will be carried over to the copy, in addition to the one specified.
     * @param modifier SGR modifiers the copy should have in additional to all currently present
     * @return Copy of the TextCharacter with a new SGR modifier
     */
    TextCharacter withModifier(SGR modifier);

    /**
     * Returns a copy of this TextCharacter with an SGR modifier removed. All of the currently active SGR codes
     * will be carried over to the copy, except for the one specified. If the current TextCharacter doesn't have the
     * SGR specified, it will return itself.
     * @param modifier SGR modifiers the copy should not have
     * @return Copy of the TextCharacter without the SGR modifier
     */
    TextCharacter withoutModifier(SGR modifier);

    boolean isDoubleWidth();
}
