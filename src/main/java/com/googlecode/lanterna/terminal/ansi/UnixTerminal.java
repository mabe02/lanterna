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
 * Copyright (C) 2010-2015 Martin
 */
package com.googlecode.lanterna.terminal.ansi;


import java.io.*;
import java.nio.charset.Charset;

import com.googlecode.lanterna.TerminalSize;

/**
 * This class extends UnixLikeTerminal and implements the Unix-specific parts.
 * <p>
 * If you need to have Lanterna to call stty at a different location, you'll need to
 * subclass this and override {@code getSTTYCommand()}.
 *
 * @author Martin
 */
@SuppressWarnings("WeakerAccess")
public class UnixTerminal extends UnixLikeTerminal {

    protected final UnixTerminalSizeQuerier terminalSizeQuerier;
    private final boolean catchSpecialCharacters;

    /**
     * Creates a UnixTerminal with default settings, using System.in and System.out for input/output, using the default
     * character set on the system as the encoding and trap ctrl+c signal instead of killing the application.
     * @throws IOException If there was an I/O error initializing the terminal
     */
    public UnixTerminal() throws IOException {
        this(System.in, System.out, Charset.defaultCharset());
    }

    /**
     * Creates a UnixTerminal using a specified input stream, output stream and character set. Ctrl+c signal will be
     * trapped instead of killing the application.
     *
     * @param terminalInput Input stream to read terminal input from
     * @param terminalOutput Output stream to write terminal output to
     * @param terminalCharset Character set to use when converting characters to bytes
     * @throws java.io.IOException If there was an I/O error initializing the terminal
     */
    public UnixTerminal(
            InputStream terminalInput,
            OutputStream terminalOutput,
            Charset terminalCharset) throws IOException {
        this(terminalInput, terminalOutput, terminalCharset, null);
    }

    /**
     * Creates a UnixTerminal using a specified input stream, output stream and character set, with a custom size
     * querier instead of using the default one. This way you can override size detection (if you want to force the
     * terminal to a fixed size, for example). Ctrl+c signal will be trapped instead of killing the application.
     *
     * @param terminalInput Input stream to read terminal input from
     * @param terminalOutput Output stream to write terminal output to
     * @param terminalCharset Character set to use when converting characters to bytes
     * @param customSizeQuerier Object to use for looking up the size of the terminal, or null to use the built-in
     * method
     * @throws java.io.IOException If there was an I/O error initializing the terminal
     */
    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    public UnixTerminal(
            InputStream terminalInput,
            OutputStream terminalOutput,
            Charset terminalCharset,
            UnixTerminalSizeQuerier customSizeQuerier) throws IOException {
        this(terminalInput, terminalOutput, terminalCharset, customSizeQuerier, CtrlCBehaviour.CTRL_C_KILLS_APPLICATION);
    }

    /**
     * Creates a UnixTerminal using a specified input stream, output stream and character set, with a custom size
     * querier instead of using the default one. This way you can override size detection (if you want to force the
     * terminal to a fixed size, for example). You also choose how you want ctrl+c key strokes to be handled.
     *
     * @param terminalInput Input stream to read terminal input from
     * @param terminalOutput Output stream to write terminal output to
     * @param terminalCharset Character set to use when converting characters to bytes
     * @param customSizeQuerier Object to use for looking up the size of the terminal, or null to use the built-in
     * method
     * @param terminalCtrlCBehaviour Special settings on how the terminal will behave, see {@code UnixTerminalMode} for more
     * details
     * @throws java.io.IOException If there was an I/O error initializing the terminal
     */
    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    public UnixTerminal(
            InputStream terminalInput,
            OutputStream terminalOutput,
            Charset terminalCharset,
            UnixTerminalSizeQuerier customSizeQuerier,
            CtrlCBehaviour terminalCtrlCBehaviour) throws IOException {
        super(terminalInput,
                terminalOutput,
                terminalCharset,
                terminalCtrlCBehaviour,
                new File("/dev/tty"));

        this.terminalSizeQuerier = customSizeQuerier;

        //Make sure to set an initial size
        onResized(80, 24);
        
        setupWinResizeHandler();
        saveSTTY();
        setCBreak(true);
        setEcho(false);
        sttyMinimum1CharacterForRead();
        if("false".equals(System.getProperty("com.googlecode.lanterna.terminal.UnixTerminal.catchSpecialCharacters", "").trim().toLowerCase())) {
            catchSpecialCharacters = false;
        }
        else {
            catchSpecialCharacters = true;
            disableSpecialCharacters();
        }
        setupShutdownHook();
    }

    @Override
    public TerminalSize getTerminalSize() throws IOException {
        if(terminalSizeQuerier != null) {
            return terminalSizeQuerier.queryTerminalSize();
        }
        
        return super.getTerminalSize();
    }

    @Override
    protected void sttyKeyEcho(final boolean enable) throws IOException {
        exec(getSTTYCommand(), enable ? "echo" : "-echo");
    }

    @Override
    protected void sttyMinimum1CharacterForRead() throws IOException {
        exec(getSTTYCommand(), "min", "1");
    }

    @Override
    protected void sttyICanon(final boolean enable) throws IOException {
        exec(getSTTYCommand(), enable ? "icanon" : "-icanon");
    }

    @Override
    protected String sttySave() throws IOException {
        return exec(getSTTYCommand(), "-g").trim();
    }

    @Override
    protected void sttyRestore(String tok) throws IOException {
        exec(getSTTYCommand(), tok);
    }

    /*
    //What was the problem with this one? I don't remember... Restoring ctrl+c for now (see below)
    private void restoreEOFCtrlD() throws IOException {
        exec(getShellCommand(), "-c", getSTTYCommand() + " eof ^d < /dev/tty");
    }

    private void disableSpecialCharacters() throws IOException {
        exec(getShellCommand(), "-c", getSTTYCommand() + " intr undef < /dev/tty");
        exec(getShellCommand(), "-c", getSTTYCommand() + " start undef < /dev/tty");
        exec(getShellCommand(), "-c", getSTTYCommand() + " stop undef < /dev/tty");
        exec(getShellCommand(), "-c", getSTTYCommand() + " susp undef < /dev/tty");
    }

    private void restoreSpecialCharacters() throws IOException {
        exec(getShellCommand(), "-c", getSTTYCommand() + " intr ^C < /dev/tty");
        exec(getShellCommand(), "-c", getSTTYCommand() + " start ^Q < /dev/tty");
        exec(getShellCommand(), "-c", getSTTYCommand() + " stop ^S < /dev/tty");
        exec(getShellCommand(), "-c", getSTTYCommand() + " susp ^Z < /dev/tty");
    }
    */


    /**
     * This method causes certain keystrokes (at the moment only ctrl+c) to be passed in to the program instead of
     * interpreted by the shell and affect the program. For example, ctrl+c will send an interrupt that causes the
     * JVM to shut down, but this method will make it pass in ctrl+c as a normal KeyStroke instead (you can still make
     * ctrl+c kill the application, but Lanterna can do this for you after having restored the terminal).
     * <p>
     * Please note that this method is generally called automatically (i.e. it's turned on by default), unless you
     * define a system property "com.googlecode.lanterna.terminal.UnixTerminal.catchSpecialCharacters" and set it to
     * the string "false".
     * @throws IOException If there was an I/O error when attempting to disable special characters
     * @see com.googlecode.lanterna.terminal.ansi.UnixLikeTerminal.CtrlCBehaviour
     */
    public void disableSpecialCharacters() throws IOException {
        exec(getSTTYCommand(), "intr", "undef");
    }

    /**
     * This method restores the special characters disabled by {@code disableSpecialCharacters()}, if it has been
     * called.
     * @throws IOException If there was an I/O error when attempting to restore special characters
     */
    public void restoreSpecialCharacters() throws IOException {
        exec(getSTTYCommand(), "intr", "^C");
    }

    @Override
    protected synchronized void restoreSTTY() throws IOException {
        super.restoreSTTY();
        if(catchSpecialCharacters) {
            restoreSpecialCharacters();
        }
    }

    protected String getSTTYCommand() {
        return "/bin/stty";
    }

}
