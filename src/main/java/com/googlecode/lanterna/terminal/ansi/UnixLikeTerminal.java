/*
 * This file is part of lanterna (http://code.google.com/p/lanterna/).
 *
 * lanterna is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2010-2017 Martin Berglund
 */
package com.googlecode.lanterna.terminal.ansi;

import com.googlecode.lanterna.input.KeyStroke;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Base class for all terminals that generally behave like Unix terminals. This class defined a number of abstract
 * methods that needs to be implemented which are all used to setup the terminal environment (turning off echo,
 * canonical mode, etc) and also a control variable for how to react to CTRL+c keystroke.
 */
public abstract class UnixLikeTerminal extends ANSITerminal {
    /**
     * This enum lets you control how Lanterna will handle a ctrl+c keystroke from the user.
     */
    public enum CtrlCBehaviour {
        /**
         * Pressing ctrl+c doesn't kill the application, it will be added to the input queue as any other key stroke
         */
        TRAP,
        /**
         * Pressing ctrl+c will restore the terminal and kill the application as it normally does with terminal
         * applications. Lanterna will restore the terminal and then call {@code System.exit(1)} for this.
         */
        CTRL_C_KILLS_APPLICATION,
    }

    private final CtrlCBehaviour terminalCtrlCBehaviour;
    private final boolean catchSpecialCharacters;
    private final Thread shutdownHook;
    private boolean acquired;

    protected UnixLikeTerminal(InputStream terminalInput,
                            OutputStream terminalOutput,
                            Charset terminalCharset,
                            CtrlCBehaviour terminalCtrlCBehaviour) throws IOException {

        super(terminalInput, terminalOutput, terminalCharset);
        this.acquired = false;

        String catchSpecialCharactersPropValue = System.getProperty(
                "com.googlecode.lanterna.terminal.UnixTerminal.catchSpecialCharacters",
                "");
        this.catchSpecialCharacters = !"false".equals(catchSpecialCharactersPropValue.trim().toLowerCase());
        this.terminalCtrlCBehaviour = terminalCtrlCBehaviour;
        shutdownHook = new Thread("Lanterna STTY restore") {
            @Override
            public void run() {
                exitPrivateModeAndRestoreState();
            }
        };
        acquire();
    }

    /**
     * Effectively taking over the terminal and enabling it for Lanterna to use, by turning off echo and canonical mode,
     * adding resize listeners and optionally trap unix signals. This should be called automatically by the constructor
     * of any end-user class extending from {@link UnixLikeTerminal}
     * @throws IOException If there was an I/O error
     */
    protected void acquire() throws IOException {
        //Make sure to set an initial size
        onResized(80, 24);

        saveTerminalSettings();
        canonicalMode(false);
        keyEchoEnabled(false);
        if(catchSpecialCharacters) {
            keyStrokeSignalsEnabled(false);
        }
        registerTerminalResizeListener(new Runnable() {
            @Override
            public void run() {
                // This will trigger a resize notification as the size will be different than before
                try {
                    getTerminalSize();
                }
                catch(IOException ignore) {
                    // Not much to do here, we can't re-throw it
                }
            }
        });
        Runtime.getRuntime().addShutdownHook(shutdownHook);
        acquired = true;
    }

    @Override
    public void close() throws IOException {
        exitPrivateModeAndRestoreState();
        Runtime.getRuntime().removeShutdownHook(shutdownHook);
        acquired = false;
        super.close();
    }

    @Override
    public KeyStroke pollInput() throws IOException {
        //Check if we have ctrl+c coming
        KeyStroke key = super.pollInput();
        isCtrlC(key);
        return key;
    }

    @Override
    public KeyStroke readInput() throws IOException {
        //Check if we have ctrl+c coming
        KeyStroke key = super.readInput();
        isCtrlC(key);
        return key;
    }

    protected CtrlCBehaviour getTerminalCtrlCBehaviour() {
        return terminalCtrlCBehaviour;
    }

    protected abstract void registerTerminalResizeListener(Runnable onResize) throws IOException;

    /**
     * Stores the current terminal device settings (the ones that are modified through this interface) so that they can
     * be restored later using {@link #restoreTerminalSettings()}
     * @throws IOException If there was an I/O error when altering the terminal environment
     */
    protected abstract void saveTerminalSettings() throws IOException;

    /**
     * Restores the terminal settings from last time {@link #saveTerminalSettings()} was called
     * @throws IOException If there was an I/O error when altering the terminal environment
     */
    protected abstract void restoreTerminalSettings() throws IOException;

    private void restoreTerminalSettingsAndKeyStrokeSignals() throws IOException {
        restoreTerminalSettings();
        if(catchSpecialCharacters) {
            keyStrokeSignalsEnabled(true);
        }
    }

    /**
     * Enables or disable key echo mode, which means when the user press a key, the terminal will immediately print that
     * key to the terminal. Normally for Lanterna, this should be turned off so the software can take the key as an
     * input event, put it on the input queue and then depending on the code decide what to do with it.
     * @param enabled {@code true} if key echo should be enabled, {@code false} otherwise
     * @throws IOException If there was an I/O error when altering the terminal environment
     */
    protected abstract void keyEchoEnabled(boolean enabled) throws IOException;

    /**
     * In canonical mode, data are accumulated in a line editing buffer, and do not become "available for reading" until
     * line editing has been terminated by the user sending a line delimiter character. This is usually the default mode
     * for a terminal. Lanterna wants to read each character as they are typed, without waiting for the final newline,
     * so it will attempt to turn canonical mode off on initialization.
     * @param enabled {@code true} if canonical input mode should be enabled, {@code false} otherwise
     * @throws IOException If there was an I/O error when altering the terminal environment
     */
    protected abstract void canonicalMode(boolean enabled) throws IOException;

    /**
     * This method causes certain keystrokes (at the moment only ctrl+c) to be passed in to the program as a regular
     * {@link com.googlecode.lanterna.input.KeyStroke} instead of as a signal to the JVM process. For example,
     * <i>ctrl+c</i> will normally send an interrupt that causes the JVM to shut down, but this method will make it pass
     * in <i>ctrl+c</i> as a regular {@link com.googlecode.lanterna.input.KeyStroke} instead. You can of course still
     * make <i>ctrl+c</i> kill the application through your own input handling if you like.
     * <p>
     * Please note that this method is called automatically by lanterna to disable signals unless you define a system
     * property "com.googlecode.lanterna.terminal.UnixTerminal.catchSpecialCharacters" and set it to the string "false".
     * @param enabled Pass in {@code true} if you want keystrokes to generate system signals (like process interrupt),
     *                {@code false} if you want lanterna to catch and interpret these keystrokes are regular keystrokes
     * @throws IOException If there was an I/O error when attempting to disable special characters
     * @see UnixLikeTTYTerminal.CtrlCBehaviour
     */
    protected abstract void keyStrokeSignalsEnabled(boolean enabled) throws IOException;

    private void isCtrlC(KeyStroke key) throws IOException {
        if(key != null
                && terminalCtrlCBehaviour == CtrlCBehaviour.CTRL_C_KILLS_APPLICATION
                && key.getCharacter() != null
                && key.getCharacter() == 'c'
                && !key.isAltDown()
                && key.isCtrlDown()) {

            if (isInPrivateMode()) {
                exitPrivateMode();
            }
            System.exit(1);
        }
    }

    private void exitPrivateModeAndRestoreState() {
        if(!acquired) {
            return;
        }
        try {
            if (isInPrivateMode()) {
                exitPrivateMode();
            }
        }
        catch(IOException ignored) {}
        catch(IllegalStateException ignored) {} // still possible!

        try {
            restoreTerminalSettingsAndKeyStrokeSignals();
        }
        catch(IOException ignored) {}
    }
}
