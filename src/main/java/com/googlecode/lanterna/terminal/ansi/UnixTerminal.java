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

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.TerminalSize;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.Charset;

/**
 * UnixTerminal extends from ANSITerminal and adds functionality for querying the terminal size, setting echo mode and
 * cbreak. It will use Unix WINCH signal to detect when the user has resized the terminal, if supported by the JVM, and
 * rely on being able to call /bin/sh and /bin/stty to set echo, cbreak and minimum characters for reading.
 * <p/>
 * If you need to have Lanterna to call the shell and/or stty at a different location, you'll need to subclass this and
 * override {@code getShellCommand()} and {@code getSTTYCommand()}.
 *
 * @author Martin
 */
@SuppressWarnings("WeakerAccess")
public class UnixTerminal extends ANSITerminal {

    private final UnixTerminalSizeQuerier terminalSizeQuerier;

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
    private String sttyStatusToRestore;

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
        this(terminalInput, terminalOutput, terminalCharset, customSizeQuerier, CtrlCBehaviour.TRAP);
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
        super(terminalInput, terminalOutput, terminalCharset);
        this.terminalSizeQuerier = customSizeQuerier;
        this.terminalCtrlCBehaviour = terminalCtrlCBehaviour;
        this.sttyStatusToRestore = null;

        //Make sure to set an initial size
        onResized(80, 20);
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
        
        saveSTTY();
        sttyMinimum1CharacterForRead();
        setCBreak(true);
        setEcho(false);
        Runtime.getRuntime().addShutdownHook(new Thread("Lanterna STTY restore") {
            @Override
            public void run() {
                try {
                    restoreSTTY();
                }
                catch(IOException ignored) {}
            }
        });
    }

    @Override
    public TerminalSize getTerminalSize() throws IOException {
        if(terminalSizeQuerier != null) {
            return terminalSizeQuerier.queryTerminalSize();
        }
        
        return super.getTerminalSize();
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

    @Override
    public void enterPrivateMode() throws IOException {
        if(isInPrivateMode()) {
            super.enterPrivateMode();   //This will throw IllegalStateException
        }
        super.enterPrivateMode();
    }

    @Override
    public void exitPrivateMode() throws IOException {
        if(!isInPrivateMode()) {
            super.exitPrivateMode();   //This will throw IllegalStateException
        }
        super.exitPrivateMode();
    }

    /**
     * Enabling cbreak mode will allow you to read user input immediately as the user enters the characters, as opposed
     * to reading the data in lines as the user presses enter. If you want your program to respond to user input by the
     * keyboard, you probably want to enable cbreak mode.
     *
     * @see <a href="http://en.wikipedia.org/wiki/POSIX_terminal_interface">POSIX terminal interface</a>
     * @param cbreakOn Should cbreak be turned on or not
     * @throws IOException
     */
    public void setCBreak(boolean cbreakOn) throws IOException {
        sttyICanon(cbreakOn);
    }

    /**
     * Enables or disables keyboard echo, meaning the immediate output of the characters you type on your keyboard. If
     * your users are going to interact with this application through the keyboard, you probably want to disable echo
     * mode.
     *
     * @param echoOn true if keyboard input will immediately echo, false if it's hidden
     * @throws IOException
     */
    public void setEcho(boolean echoOn) throws IOException {
        sttyKeyEcho(echoOn);
    }

    private void sttyKeyEcho(final boolean enable) throws IOException {
        exec(getShellCommand(), "-c",
                getSTTYCommand() + " " + (enable ? "echo" : "-echo") + " < /dev/tty");
    }

    private void sttyMinimum1CharacterForRead() throws IOException {
        exec(getShellCommand(), "-c",
                getSTTYCommand() + " min 1 < /dev/tty");
    }

    private void sttyICanon(final boolean enable) throws IOException {
        exec(getShellCommand(), "-c",
                getSTTYCommand() + " " + (enable ? "-icanon" : "icanon") + " < /dev/tty");
    }

    @SuppressWarnings("unused")
    private void restoreEOFCtrlD() throws IOException {
        exec(getShellCommand(), "-c", getSTTYCommand() + " eof ^d < /dev/tty");
    }

    @SuppressWarnings("unused")
    private void disableSpecialCharacters() throws IOException {
        exec(getShellCommand(), "-c", getSTTYCommand() + " intr undef < /dev/tty");
        exec(getShellCommand(), "-c", getSTTYCommand() + " start undef < /dev/tty");
        exec(getShellCommand(), "-c", getSTTYCommand() + " stop undef < /dev/tty");
        exec(getShellCommand(), "-c", getSTTYCommand() + " susp undef < /dev/tty");
    }

    @SuppressWarnings("unused")
    private void restoreSpecialCharacters() throws IOException {
        exec(getShellCommand(), "-c", getSTTYCommand() + " intr ^C < /dev/tty");
        exec(getShellCommand(), "-c", getSTTYCommand() + " start ^Q < /dev/tty");
        exec(getShellCommand(), "-c", getSTTYCommand() + " stop ^S < /dev/tty");
        exec(getShellCommand(), "-c", getSTTYCommand() + " susp ^Z < /dev/tty");
    }

    private void saveSTTY() throws IOException {
        if(sttyStatusToRestore == null) {
            sttyStatusToRestore = exec(getShellCommand(), "-c", getSTTYCommand() + " -g < /dev/tty").trim();
        }
    }

    private synchronized void restoreSTTY() throws IOException {
        if(sttyStatusToRestore == null) {
            //Nothing to restore
            return;
        }

        exec(getShellCommand(), "-c", getSTTYCommand() + " " + sttyStatusToRestore + " < /dev/tty");
        sttyStatusToRestore = null;
    }

    protected String getSTTYCommand() {
        return "/bin/stty";
    }

    protected String getShellCommand() {
        return "/bin/sh";
    }

    private static String exec(String... cmd) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process process = pb.start();
        ByteArrayOutputStream stdoutBuffer = new ByteArrayOutputStream();
        InputStream stdout = process.getInputStream();
        int readByte = stdout.read();
        while(readByte >= 0) {
            stdoutBuffer.write(readByte);
            readByte = stdout.read();
        }
        ByteArrayInputStream stdoutBufferInputStream = new ByteArrayInputStream(stdoutBuffer.toByteArray());
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdoutBufferInputStream));
        StringBuilder builder = new StringBuilder();
        while(reader.ready()) {
            builder.append(reader.readLine());
        }
        reader.close();
        return builder.toString();
    }
}
