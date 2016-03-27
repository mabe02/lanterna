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
 * Copyright (C) 2010-2016 Martin
 */
package com.googlecode.lanterna.terminal.ansi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.Charset;

import com.googlecode.lanterna.input.KeyStroke;

/**
 * UnixLikeTerminal extends from ANSITerminal and defines functionality that is common to
 *  {@code UnixTerminal} and {@code CygwinTerminal}, like setting tty modes; echo, cbreak
 *  and minimum characters for reading as well as a shutdown hook to set the tty back to
 *  original state at the end.
 * <p>
 *  If requested, it handles Control-C input to terminate the program, and hooks
 *  into Unix WINCH signal to detect when the user has resized the terminal,
 *  if supported by the JVM.
 *
 * @author Andreas
 * @author Martin
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

    protected final TerminalDeviceControlStrategy deviceControlStrategy;
    protected final CtrlCBehaviour terminalCtrlCBehaviour;

    /**
     * Creates a UnixTerminal using a specified input stream, output stream and character set, with a custom size
     * querier instead of using the default one. This way you can override size detection (if you want to force the
     * terminal to a fixed size, for example). You also choose how you want ctrl+c key strokes to be handled.
     *
     * @param terminalInput Input stream to read terminal input from
     * @param terminalOutput Output stream to write terminal output to
     * @param terminalCharset Character set to use when converting characters to bytes
     * @param terminalCtrlCBehaviour Special settings on how the terminal will behave, see {@code UnixTerminalMode} for more
     * details
     */
    protected UnixLikeTerminal(
            TerminalDeviceControlStrategy deviceControlStrategy,
            InputStream terminalInput,
            OutputStream terminalOutput,
            Charset terminalCharset,
            CtrlCBehaviour terminalCtrlCBehaviour) throws IOException {

        super(terminalInput,
                terminalOutput,
                terminalCharset);

        this.deviceControlStrategy = deviceControlStrategy;
        this.terminalCtrlCBehaviour = terminalCtrlCBehaviour;

        saveTerminalSettings();
        canonicalMode(false);
        keyEchoEnabled(false);
        setupShutdownHook();
        setupWinResizeHandler();
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

    private void isCtrlC(KeyStroke key) throws IOException {
        if(key != null
                && terminalCtrlCBehaviour == CtrlCBehaviour.CTRL_C_KILLS_APPLICATION
                && key.getCharacter() != null
                && key.getCharacter() == 'c'
                && !key.isAltDown()
                && key.isCtrlDown()) {
   
            exitPrivateMode();
            System.exit(1);
        }
    }

    /**
     * Stores the current terminal device settings (the ones that are modified through this interface) so that they can
     * be restored later using {@link #restoreTerminalSettings()}
     */
    void saveTerminalSettings() throws IOException {
        deviceControlStrategy.saveTerminalSettings();
    }

    /**
     * Restores the terminal settings from last time {@link #saveTerminalSettings()} was called
     */
    void restoreTerminalSettings() throws IOException {
        deviceControlStrategy.restoreTerminalSettings();
    }

    /**
     * Enables or disable key echo mode, which means when the user press a key, the terminal will immediately print that
     * key to the terminal. Normally for Lanterna, this should be turned off so the software can take the key as an
     * input event, put it on the input queue and then depending on the code decide what to do with it.
     * @param enabled {@code true} if key echo should be enabled, {@code false} otherwise
     */
    void keyEchoEnabled(boolean enabled) throws IOException {
        deviceControlStrategy.keyEchoEnabled(enabled);
    }

    /**
     * In canonical mode, data are accumulated in a line editing buffer, and do not become "available for reading" until
     * line editing has been terminated by the user sending a line delimiter character. This is usually the default mode
     * for a terminal. Lanterna wants to read each character as they are typed, without waiting for the final newline,
     * so it will attempt to turn canonical mode off on initialization.
     * @param enabled {@code true} if canonical input mode should be enabled, {@code false} otherwise
     */
    void canonicalMode(boolean enabled) throws IOException {
        deviceControlStrategy.canonicalMode(enabled);
    }

    /**
     * This method causes certain keystrokes (at the moment only ctrl+c) to be passed in to the program as a regular
     * {@link com.googlecode.lanterna.input.KeyStroke} instead of as a signal to the JVM process. For example,
     * <i>ctrl+c</i> will normally send an interrupt that causes the JVM to shut down, but this method will make it pass
     * in <i>ctrl+c</i> as a regular {@link com.googlecode.lanterna.input.KeyStroke} instead. You can of course still
     * make <i>ctrl+c</i> kill the application through your own input handling if you like.
     * <p>
     * Please note that this method is called automatically by lanterna to disable signals unless you define a system
     * property "com.googlecode.lanterna.terminal.UnixTerminal.catchSpecialCharacters" and set it to the string "false".
     * @throws IOException If there was an I/O error when attempting to disable special characters
     * @see com.googlecode.lanterna.terminal.ansi.UnixLikeTerminal.CtrlCBehaviour
     */
    void keyStrokeSignalsEnabled(boolean enabled) throws IOException {
        deviceControlStrategy.keyStrokeSignalsEnabled(enabled);
    }

    protected void setupWinResizeHandler() {
        try {
            Class<?> signalClass = Class.forName("sun.misc.Signal");
            for(Method m : signalClass.getDeclaredMethods()) {
                if("handle".equals(m.getName())) {
                    Object windowResizeHandler = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Class.forName("sun.misc.SignalHandler")}, new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            if("handle".equals(method.getName())) {
                                getTerminalSize();
                            }
                            return null;
                        }
                    });
                    m.invoke(null, signalClass.getConstructor(String.class).newInstance("WINCH"), windowResizeHandler);
                }
            }
        } catch(Throwable e) {
            System.err.println(e.getMessage());
        }
    }

    protected void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread("Lanterna STTY restore") {
            @Override
            public void run() {
                try {
                    if (isInPrivateMode()) {
                        exitPrivateMode();
                    }
                }
                catch(IOException ignored) {}
                catch(IllegalStateException ignored) {} // still possible!

                try {
                    restoreTerminalSettings();
                }
                catch(IOException ignored) {}
            }
        });
    }


}