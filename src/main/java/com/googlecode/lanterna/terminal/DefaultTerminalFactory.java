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
 * Copyright (C) 2010-2019 Martin Berglund
 */
package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.ansi.CygwinTerminal;
import com.googlecode.lanterna.terminal.ansi.TelnetTerminal;
import com.googlecode.lanterna.terminal.ansi.TelnetTerminalServer;
import com.googlecode.lanterna.terminal.ansi.UnixLikeTTYTerminal;
import com.googlecode.lanterna.terminal.ansi.UnixTerminal;
import com.googlecode.lanterna.terminal.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.EnumSet;

/**
 * This TerminalFactory implementation uses a simple auto-detection mechanism for figuring out which terminal 
 * implementation to create based on characteristics of the system the program is running on.
 * <p>
 * Note that for all systems with a graphical environment present, the SwingTerminalFrame will be chosen. You can 
 * suppress this by calling setForceTextTerminal(true) on this factory.
 * @author martin
 */
public class DefaultTerminalFactory implements TerminalFactory {
    private static final OutputStream DEFAULT_OUTPUT_STREAM = System.out;
    private static final InputStream DEFAULT_INPUT_STREAM = System.in;
    private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();

    private final OutputStream outputStream;
    private final InputStream inputStream;
    private final Charset charset;

    private TerminalSize initialTerminalSize;
    private boolean forceTextTerminal;
    private boolean preferTerminalEmulator;
    private boolean forceAWTOverSwing;
    private int telnetPort;
    private int inputTimeout;
    private String title;
    private boolean autoOpenTerminalFrame;
    private final EnumSet<TerminalEmulatorAutoCloseTrigger> autoCloseTriggers;
    private TerminalEmulatorColorConfiguration colorConfiguration;
    private TerminalEmulatorDeviceConfiguration deviceConfiguration;
    private AWTTerminalFontConfiguration fontConfiguration;
    private MouseCaptureMode mouseCaptureMode;
    private UnixTerminal.CtrlCBehaviour unixTerminalCtrlCBehaviour;
    
    /**
     * Creates a new DefaultTerminalFactory with all properties set to their defaults
     */   
    public DefaultTerminalFactory() {
        this(DEFAULT_OUTPUT_STREAM, DEFAULT_INPUT_STREAM, DEFAULT_CHARSET);
    }

    /**
     * Creates a new DefaultTerminalFactory with I/O and character set options customisable.
     * @param outputStream Output stream to use for text-based Terminal implementations
     * @param inputStream Input stream to use for text-based Terminal implementations
     * @param charset Character set to assume the client is using
     */
    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    public DefaultTerminalFactory(OutputStream outputStream, InputStream inputStream, Charset charset) {
        this.outputStream = outputStream;
        this.inputStream = inputStream;
        this.charset = charset;
        
        this.forceTextTerminal = false;
        this.preferTerminalEmulator = false;
        this.forceAWTOverSwing = false;

        this.telnetPort = -1;
        this.inputTimeout = -1;
        this.autoOpenTerminalFrame = true;
        this.title = null;
        this.autoCloseTriggers = EnumSet.of(TerminalEmulatorAutoCloseTrigger.CloseOnExitPrivateMode);
        this.mouseCaptureMode = null;
        this.unixTerminalCtrlCBehaviour = UnixTerminal.CtrlCBehaviour.CTRL_C_KILLS_APPLICATION;

        //SwingTerminal will replace these null values for the default implementation if they are unchanged
        this.colorConfiguration = null;
        this.deviceConfiguration = null;
        this.fontConfiguration = null;
    }
    
    @Override
    public Terminal createTerminal() throws IOException {
        // 3 different reasons for tty-based terminal:
        //   "explicit preference", "no alternative",
        //       ("because we can" - unless "rather not")
        if (forceTextTerminal || isAwtHeadless() ||
                (System.console() != null && !preferTerminalEmulator) ) {
            // if tty but have no tty, but do have a port, then go telnet:
            if( telnetPort > 0 && System.console() == null) {
                return createTelnetTerminal();
            }
            if(isOperatingSystemWindows()) {
                return createWindowsTerminal();
            }
            else {
                return createUnixTerminal(outputStream, inputStream, charset);
            }
        }
        else {
            // while Lanterna's TerminalEmulator lacks mouse support:
            // if user wanted mouse AND set a telnetPort, and didn't
            //   explicitly ask for a graphical Terminal, then go telnet:
            if (!preferTerminalEmulator && mouseCaptureMode != null && telnetPort > 0) {
                return createTelnetTerminal();
            } else {
                return createTerminalEmulator();
            }
        }
    }

    /**
     * Creates a new terminal emulator window which will be either Swing-based or AWT-based depending on what is
     * available on the system
     * @return New terminal emulator exposed as a {@link Terminal} interface
     */
    public Terminal createTerminalEmulator() {
        Terminal terminal;
        if (!forceAWTOverSwing && hasSwing()) {
            terminal = createSwingTerminal();
        } else {
            terminal = createAWTTerminal();
        }

        if (autoOpenTerminalFrame) {
            makeWindowVisible(terminal);
        }
        return terminal;
    }

    public AWTTerminalFrame createAWTTerminal() {
        return new AWTTerminalFrame(
                title,
                initialTerminalSize,
                deviceConfiguration,
                fontConfiguration,
                colorConfiguration,
                autoCloseTriggers.toArray(new TerminalEmulatorAutoCloseTrigger[autoCloseTriggers.size()]));
    }

    public SwingTerminalFrame createSwingTerminal() {
        return new SwingTerminalFrame(
                title,
                initialTerminalSize,
                deviceConfiguration,
                fontConfiguration instanceof SwingTerminalFontConfiguration ? (SwingTerminalFontConfiguration)fontConfiguration : null,
                colorConfiguration,
                autoCloseTriggers.toArray(new TerminalEmulatorAutoCloseTrigger[autoCloseTriggers.size()]));
    }

    /**
     * Creates a new TelnetTerminal
     *
     * Note: a telnetPort should have been set with setTelnetPort(),
     * otherwise creation of TelnetTerminal will most likely fail.
     *
     * @return New terminal emulator exposed as a {@link Terminal} interface
     */
    public TelnetTerminal createTelnetTerminal() {
        try {
            System.err.print("Waiting for incoming telnet connection on port "+telnetPort+" ... ");
            System.err.flush();

            TelnetTerminalServer tts = new TelnetTerminalServer(telnetPort);
            TelnetTerminal rawTerminal = tts.acceptConnection();
            tts.close(); // Just for single-shot: free up the port!

            System.err.println("Ok, got it!");

            if(mouseCaptureMode != null) {
                rawTerminal.setMouseCaptureMode(mouseCaptureMode);
            }
            if(inputTimeout >= 0) {
                rawTerminal.getInputDecoder().setTimeoutUnits(inputTimeout);
            }
            return rawTerminal;
        } catch(IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private boolean isAwtHeadless() {
        try {
            Class cls = Class.forName("java.awt.GraphicsEnvironment");
            Method method = cls.getDeclaredMethod("isHeadless");
            return (Boolean) method.invoke(null);
        } catch (Exception ignore) {
            // Most likely cause is that the java.desktop module is not available in the runtime image.
            return true;
        }
    }

    private boolean hasSwing() {
        try {
            Class.forName("javax.swing.JComponent");
            return true;
        }
        catch(Exception ignore) {
            return false;
        }
    }

    private void makeWindowVisible(Terminal terminal) {
        try {
            Class cls = Class.forName("java.awt.Window");
            Method method = cls.getDeclaredMethod("setVisible", boolean.class);
            method.invoke(terminal, true);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to make terminal emulator window visible.", ex);
        }
    }

    /**
     * Sets a hint to the TerminalFactory of what size to use when creating the terminal. Most terminals are not created
     * on request but for example the SwingTerminal and SwingTerminalFrame are and this value will be passed down on
     * creation.
     * @param initialTerminalSize Size (in rows and columns) of the newly created terminal
     * @return Reference to itself, so multiple .set-calls can be chained
     */
    public DefaultTerminalFactory setInitialTerminalSize(TerminalSize initialTerminalSize) {
        this.initialTerminalSize = initialTerminalSize;
        return this;
    }

    /**
     * Controls whether a text-based Terminal shall be created even if the system
     *    supports a graphical environment
     * @param forceTextTerminal If true, will always create a text-based Terminal
     * @return Reference to itself, so multiple .set-calls can be chained
     */
    public DefaultTerminalFactory setForceTextTerminal(boolean forceTextTerminal) {
        this.forceTextTerminal = forceTextTerminal;
        return this;
    }

    /**
     * Controls whether a Swing or AWT TerminalFrame shall be preferred if the system
     *    has both a Console and a graphical environment
     * @param preferTerminalEmulator If true, will prefer creating a graphical terminal emulator
     * @return Reference to itself, so multiple .set-calls can be chained
     */
    public DefaultTerminalFactory setPreferTerminalEmulator(boolean preferTerminalEmulator) {
        this.preferTerminalEmulator = preferTerminalEmulator;
        return this;
    }

    /**
     * Sets the default CTRL-C behavior to use for all {@link UnixTerminal} objects created by this factory. You can
     * use this to tell Lanterna to trap CTRL-C instead of exiting the application. Non-UNIX terminals are not affected
     * by this.
     * @param unixTerminalCtrlCBehaviour CTRL-C behavior to use for {@link UnixTerminal}:s
     */
    public DefaultTerminalFactory setUnixTerminalCtrlCBehaviour(UnixTerminal.CtrlCBehaviour unixTerminalCtrlCBehaviour) {
        this.unixTerminalCtrlCBehaviour = unixTerminalCtrlCBehaviour;
        return this;
    }

    /**
     * Primarily for debugging applications with mouse interactions:
     * If no Console is available (e.g. from within an IDE), then fall
     * back to TelnetTerminal on specified port.
     *
     * If both a non-null mouseCapture mode and a positive telnetPort
     * are specified, then as long as Swing/AWT Terminal emulators do
     * not support MouseCapturing, a TelnetTerminal will be preferred
     * over the graphical Emulators.
     *
     * @param telnetPort the TCP/IP port on which to eventually wait for a connection.
     *         A value less or equal 0 disables creation of a TelnetTerminal.
     *         Note, that ports less than 1024 typically require system
     *         privileges to listen on.
     * @return Reference to itself, so multiple .set-calls can be chained
     */
    public DefaultTerminalFactory setTelnetPort(int telnetPort) {
        this.telnetPort = telnetPort;
        return this;
    }

    /**
     * Only for StreamBasedTerminals: After seeing e.g. an Escape (but nothing
     *         else yet), wait up to the specified number of time units for more
     *         bytes to make up a complete sequence. This may be necessary on
     *         slow channels, or if some client terminal sends each byte of a
     *         sequence in its own TCP packet.
     *
     * @param inputTimeout how long to wait for possible completions of sequences.
     *         units are of a 1/4 second, so e.g. 12 would wait up to 3 seconds.
     * @return Reference to itself, so multiple .set-calls can be chained
     */
    public DefaultTerminalFactory setInputTimeout(int inputTimeout) {
        this.inputTimeout = inputTimeout;
        return this;
    }

    /**
     * Normally when a graphical terminal emulator is created by the factory, it will create a
     * {@link SwingTerminalFrame} unless Swing is not present in the system. Setting this property to {@code true} will
     * make it create an {@link AWTTerminalFrame} even if Swing is present
     * @param forceAWTOverSwing If {@code true}, will always create an {@link AWTTerminalFrame} over a
     * {@link SwingTerminalFrame} if asked to create a graphical terminal emulator
     * @return Reference to itself, so multiple .set-calls can be chained
     */
    public DefaultTerminalFactory setForceAWTOverSwing(boolean forceAWTOverSwing) {
        this.forceAWTOverSwing = forceAWTOverSwing;
        return this;
    }

    /**
     * Controls whether a SwingTerminalFrame shall be automatically shown (.setVisible(true)) immediately after 
     * creation. If {@code false}, you will manually need to call {@code .setVisible(true)} on the JFrame to actually
     * see the terminal window. Default for this value is {@code true}.
     * @param autoOpenTerminalFrame Automatically open SwingTerminalFrame after creation
     * @return Itself
     */
    public DefaultTerminalFactory setAutoOpenTerminalEmulatorWindow(boolean autoOpenTerminalFrame) {
        this.autoOpenTerminalFrame = autoOpenTerminalFrame;
        return this;
    }
    
    /**
     * Sets the title to use on created SwingTerminalFrames created by this factory
     * @param title Title to use on created SwingTerminalFrames created by this factory
     * @return Reference to itself, so multiple .set-calls can be chained
     */
    public DefaultTerminalFactory setTerminalEmulatorTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Sets the auto-close trigger to use on created SwingTerminalFrames created by this factory. This will reset any
     * previous triggers. If called with {@code null}, all triggers are cleared.
     * @param autoCloseTrigger Auto-close trigger to use on created SwingTerminalFrames created by this factory, or {@code null} to clear all existing triggers
     * @return Reference to itself, so multiple .set-calls can be chained
     */
    public DefaultTerminalFactory setTerminalEmulatorFrameAutoCloseTrigger(TerminalEmulatorAutoCloseTrigger autoCloseTrigger) {
        this.autoCloseTriggers.clear();
        if(autoCloseTrigger != null) {
            this.autoCloseTriggers.add(autoCloseTrigger);
        }
        return this;
    }

    /**
     * Adds an auto-close trigger to use on created SwingTerminalFrames created by this factory
     * @param autoCloseTrigger Auto-close trigger to add to the created SwingTerminalFrames created by this factory
     * @return Reference to itself, so multiple calls can be chained
     */
    public DefaultTerminalFactory addTerminalEmulatorFrameAutoCloseTrigger(TerminalEmulatorAutoCloseTrigger autoCloseTrigger) {
        if(autoCloseTrigger != null) {
            this.autoCloseTriggers.add(autoCloseTrigger);
        }
        return this;
    }

    /**
     * Sets the color configuration to use on created SwingTerminalFrames created by this factory
     * @param colorConfiguration Color configuration to use on created SwingTerminalFrames created by this factory
     * @return Reference to itself, so multiple .set-calls can be chained
     */
    public DefaultTerminalFactory setTerminalEmulatorColorConfiguration(TerminalEmulatorColorConfiguration colorConfiguration) {
        this.colorConfiguration = colorConfiguration;
        return this;
    }

    /**
     * Sets the device configuration to use on created SwingTerminalFrames created by this factory
     * @param deviceConfiguration Device configuration to use on created SwingTerminalFrames created by this factory
     * @return Reference to itself, so multiple .set-calls can be chained
     */
    public DefaultTerminalFactory setTerminalEmulatorDeviceConfiguration(TerminalEmulatorDeviceConfiguration deviceConfiguration) {
        this.deviceConfiguration = deviceConfiguration;
        return this;
    }

    /**
     * Sets the font configuration to use on created SwingTerminalFrames created by this factory
     * @param fontConfiguration Font configuration to use on created SwingTerminalFrames created by this factory
     * @return Reference to itself, so multiple .set-calls can be chained
     */
    public DefaultTerminalFactory setTerminalEmulatorFontConfiguration(AWTTerminalFontConfiguration fontConfiguration) {
        this.fontConfiguration = fontConfiguration;
        return this;
    }

    /**
     * Sets the mouse capture mode the terminal should use. Please note that this is an extension which isn't widely
     * supported!
     *
     * If both a non-null mouseCapture mode and a positive telnetPort
     * are specified, then as long as Swing/AWT Terminal emulators do
     * not support MouseCapturing, a TelnetTerminal will be preferred
     * over the graphical Emulators.
     *
     * @param mouseCaptureMode Capture mode for mouse interactions
     * @return Itself
     */
    public DefaultTerminalFactory setMouseCaptureMode(MouseCaptureMode mouseCaptureMode) {
        this.mouseCaptureMode = mouseCaptureMode;
        return this;
    }

    /**
     * Create a {@link Terminal} and immediately wrap it up in a {@link TerminalScreen}
     * @return New {@link TerminalScreen} created with a terminal from {@link #createTerminal()}
     * @throws IOException In case there was an I/O error
     */
    public TerminalScreen createScreen() throws IOException {
        return new TerminalScreen(createTerminal());
    }

    private Terminal createWindowsTerminal() throws IOException {
        try {
            Class<?> nativeImplementation = Class.forName("com.googlecode.lanterna.terminal.WindowsTerminal");
            Constructor<?> constructor = nativeImplementation.getConstructor(InputStream.class, OutputStream.class, Charset.class, UnixLikeTTYTerminal.CtrlCBehaviour.class);
            return (Terminal)constructor.newInstance(inputStream, outputStream, charset, UnixLikeTTYTerminal.CtrlCBehaviour.CTRL_C_KILLS_APPLICATION);
        }
        catch(Exception ignore) {
            try {
                return createCygwinTerminal(outputStream, inputStream, charset);
            } catch(IOException e) {
                throw new IOException("To start java on Windows, use javaw! (see https://github.com/mabe02/lanterna/issues/335 )", e);
            }
        }
    }
    
    private Terminal createCygwinTerminal(OutputStream outputStream, InputStream inputStream, Charset charset) throws IOException {
        CygwinTerminal cygTerminal = new CygwinTerminal(inputStream, outputStream, charset);
        if(inputTimeout >= 0) {
            cygTerminal.getInputDecoder().setTimeoutUnits(inputTimeout);
        }
        return cygTerminal;
    }

    private Terminal createUnixTerminal(OutputStream outputStream, InputStream inputStream, Charset charset) throws IOException {
        UnixTerminal unixTerminal;
        try {
            Class<?> nativeImplementation = Class.forName("com.googlecode.lanterna.terminal.NativeGNULinuxTerminal");
            Constructor<?> constructor = nativeImplementation.getConstructor(InputStream.class, OutputStream.class, Charset.class, UnixLikeTTYTerminal.CtrlCBehaviour.class);
            unixTerminal = (UnixTerminal)constructor.newInstance(inputStream, outputStream, charset, unixTerminalCtrlCBehaviour);
        }
        catch(Exception ignore) {
            unixTerminal = new UnixTerminal(inputStream, outputStream, charset, unixTerminalCtrlCBehaviour);
        }
        if(mouseCaptureMode != null) {
            unixTerminal.setMouseCaptureMode(mouseCaptureMode);
        }
        if(inputTimeout >= 0) {
            unixTerminal.getInputDecoder().setTimeoutUnits(inputTimeout);
        }
        return unixTerminal;
    }

    /**
     * Detects whether the running platform is Windows* by looking at the
     * operating system name system property
     */
    private static boolean isOperatingSystemWindows() {
        return System.getProperty("os.name", "").toLowerCase().startsWith("windows");
    }
}
