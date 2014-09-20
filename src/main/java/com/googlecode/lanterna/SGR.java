package com.googlecode.lanterna;

/**
 * SGR - Select Graphic Rendition, changes the state of the terminal as to what kind of text to print after this
 * command. When applying an ENTER SGR code, it normally applies until you send the corresponding EXIT code.
 * RESET_ALL will clear any code currently enabled.
 */
public enum SGR {
    /**
     * Please note that on some terminal implementations, instead of making the text bold, it will draw the text in
     * a slightly different color
     */
    BOLD,

    /**
     * Reverse mode will flip the foreground and background colors
     */
    REVERSE,

    /**
     * Not widely supported
     */
    UNDERLINE,

    /**
     * Not widely supported
     */
    BLINK,

    /**
     * Rarely supported
     */
    BORDERED,

    /**
     * Exotic extension, please send me a reference screen shots!
     */
    FRAKTUR,

    /**
     * Rarely supported
     */
    CROSSED_OUT,

    /**
     * Rarely supported
     */
    CIRCLED,
    ;
}
