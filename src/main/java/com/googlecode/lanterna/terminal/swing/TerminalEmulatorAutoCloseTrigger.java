package com.googlecode.lanterna.terminal.swing;

/**
 * This enum stored various ways the AWTTerminalFrame and SwingTerminalFrame can automatically close (hide and dispose)
 * themselves when a certain condition happens. By default, auto-close is not active.
 */
public enum TerminalEmulatorAutoCloseTrigger {
    /**
     * Auto-close disabled
     */
    DoNotAutoClose,
    /**
     * Close the frame when exiting from private mode
     */
    CloseOnExitPrivateMode,
    /**
     * Close if the user presses ESC key on the keyboard
     */
    CloseOnEscape,
    ;
}
