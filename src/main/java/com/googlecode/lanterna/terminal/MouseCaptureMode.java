package com.googlecode.lanterna.terminal;

/**
 * Constant describing different modes for capturing mouse input. By default, no mouse capturing is enabled (unless
 * previously enabled before starting the Lanterna application. These are the different modes of input capturing
 * supported. Please note that terminal emulators vary widely in how these are implemented!
 * Created by martin on 26/07/15.
 */
public enum MouseCaptureMode {
    /**
     * Mouse clicks are captured on the down-motion but not the up-motion. This corresponds to the X10 xterm protocol.
     * KDE's Konsole (tested with 15.04) does not implement this extension, but xfce4-terminal, gnome-terminal and
     * xterm does.
     */
    CLICK,
    /**
     * Mouse clicks are captured both on down and up, this is the normal mode for capturing mouse input. KDE's konsole
     * interprets this as CLICK_RELEASE_DRAG.
     */
    CLICK_RELEASE,
    /**
     * Mouse clicks are captured both on down and up and if the mouse if moved while holding down one of the button, a
     * drag event is generated.
     */
    CLICK_RELEASE_DRAG,
    /**
     * Mouse clicks are captured both on down and up and also all mouse movements, no matter if any button is held down
     * or not.
     */
    CLICK_RELEASE_DRAG_MOVE,
    ;
}
