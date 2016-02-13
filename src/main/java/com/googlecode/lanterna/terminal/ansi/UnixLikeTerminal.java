package com.googlecode.lanterna.terminal.ansi;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    protected final CtrlCBehaviour terminalCtrlCBehaviour;
    protected final File ttyDev;
    private String sttyStatusToRestore;

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
     * @param ttyDev File to redirect standard input from in exec(), if not null.
     */
    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    public UnixLikeTerminal(
            InputStream terminalInput,
            OutputStream terminalOutput,
            Charset terminalCharset,
            CtrlCBehaviour terminalCtrlCBehaviour,
            File ttyDev) {
        super(terminalInput, terminalOutput, terminalCharset);
        this.terminalCtrlCBehaviour = terminalCtrlCBehaviour;
        this.sttyStatusToRestore = null;
        this.ttyDev = ttyDev;
    }

    protected String exec(String... cmd) throws IOException {
        if (ttyDev != null) {
            //Here's what we try to do, but that is Java 7+ only:
            // processBuilder.redirectInput(ProcessBuilder.Redirect.from(ttyDev));
            //instead, for Java 6, we join the cmd into a scriptlet with redirection
            //and replace cmd by a call to sh with the scriptlet:
            StringBuilder sb = new StringBuilder();
            for (String arg : cmd) { sb.append(arg).append(' '); }
            sb.append("< ").append(ttyDev);
            cmd = new String[] { "sh", "-c", sb.toString() };
        }
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
        String line;
        while((line = reader.readLine()) != null) {
            builder.append(line);
        }
        reader.close();
        return builder.toString();
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
                    restoreSTTY();
                }
                catch(IOException ignored) {}
            }
        });
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
        sttyICanon(!cbreakOn);
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

    protected void saveSTTY() throws IOException {
        if(sttyStatusToRestore == null) {
            sttyStatusToRestore = sttySave();
        }
    }

    protected synchronized void restoreSTTY() throws IOException {
        if(sttyStatusToRestore != null) {
            sttyRestore( sttyStatusToRestore );
            sttyStatusToRestore = null;
        }
    }

    // A couple of system-dependent helpers:
    protected abstract void sttyKeyEcho(final boolean enable) throws IOException;
    protected abstract void sttyMinimum1CharacterForRead() throws IOException;
    protected abstract void sttyICanon(final boolean enable) throws IOException;
    protected abstract String sttySave() throws IOException;
    protected abstract void sttyRestore(String tok) throws IOException;

}