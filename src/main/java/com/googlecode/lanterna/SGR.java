package com.googlecode.lanterna;

/**
 * SGR - Select Graphic Rendition, changes the state of the terminal as to what kind of text to print after this
 * command. When working with the Terminal interface, its keeping a state of which SGR codes are active, so activating
 * one of these codes will make it apply to all text until you explicitly deactivate it. When you work with Screen and
 * GUI systems, usually the SGR is a property of an independent character and won't affect others.
 */
public enum SGR {
    /**
     * Bold text mode. Please note that on some terminal implementations, instead of (or in addition to) making the text
     * bold, it will draw the text in a slightly different color
     */
    BOLD,

    /**
     * Reverse text mode, will flip the foreground and background colors while active
     */
    REVERSE,

    /**
     * Draws a horizontal line under the text. Not widely supported.
     */
    UNDERLINE,

    /**
     * Text will blink on the screen by alternating the foreground color between the real foreground color and the
     * background color. Not widely supported.
     */
    BLINK,

    /**
     * Draws a border around the text. Rarely supported.
     */
    BORDERED,

    /**
     * I have no idea, exotic extension, please send me a reference screen shots!
     */
    FRAKTUR,

    /**
     * Draws a horizontal line through the text. Rarely supported.
     */
    CROSSED_OUT,

    /**
     * Draws a circle around the text. Rarely supported.
     */
    CIRCLED,
    ;
}
