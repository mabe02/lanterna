package com.googlecode.lanterna;

/**
 * Standard character that fits within Java's native char data type. This will include almost all
 * characters you would use for regular text, but as Unicode ran out of code code points in UTF16,
 * there are some scripts and more recently a lot of emoji that cannot be represented by a single
 * char character and instead is using sequences of char:s (surrogates).
 * <p/>
 * You wouldn't be instantiating this class directly, rather you should be using
 * {@link TextCharacter#fromCharacter(char)} or {@link TextCharacter#fromString(String)}.
 */
class BMPTextCharacter extends AbstractTextCharacter {
    BMPTextCharacter(char c, TextColor foregroundColor, TextColor backgroundColor) {

    }
}
