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
package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.ansi.CygwinTerminal;
import com.googlecode.lanterna.terminal.ansi.UnixTerminal;
import com.googlecode.lanterna.terminal.swing.*;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * This TerminalFactory implementation uses a simple auto-detection mechanism for figuring out which terminal 
 * implementation to create based on characteristics of the system the program is running on.
 * <p>
 * Note that for all systems with a graphical environment present, the SwingTerminalFrame will be chosen. You can 
 * suppress this by calling setForceTextTerminal(true) on this factory.
 * @author martin
 */
public final class DefaultTerminalFactory implements TerminalFactory {
    private static final OutputStream DEFAULT_OUTPUT_STREAM = System.out;
    private static final InputStream DEFAULT_INPUT_STREAM = System.in;
    private static final Charset DEFAULT_CHARSET = Charset.forName(System.getProperty("file.encoding"));

    private final OutputStream outputStream;
    private final InputStream inputStream;
    private final Charset charset;

    private TerminalSize initialTerminalSize;
    private boolean forceTextTerminal;
    private boolean forceAWTOverSwing;
    private String title;
    private boolean autoOpenTerminalFrame;
    private TerminalEmulatorAutoCloseTrigger autoCloseTrigger;
    private TerminalEmulatorColorConfiguration colorConfiguration;
    private TerminalEmulatorDeviceConfiguration deviceConfiguration;
    private AWTTerminalFontConfiguration fontConfiguration;
    private MouseCaptureMode mouseCaptureMode;
    
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
        this.autoOpenTerminalFrame = true;
        this.title = null;
        this.autoCloseTrigger = TerminalEmulatorAutoCloseTrigger.CloseOnExitPrivateMode;
        this.mouseCaptureMode = null;

        //SwingTerminal will replace these null values for the default implementation if they are unchanged
        this.colorConfiguration = null;
        this.deviceConfiguration = null;
        this.fontConfiguration = null;
    }
    
    @Override
    public Terminal createTerminal() throws IOException {
        if (GraphicsEnvironment.isHeadless() || forceTextTerminal || System.console() != null) {
            if(isOperatingSystemWindows()) {
                return createCygwinTerminal(outputStream, inputStream, charset);
            }
            else {
                return createUnixTerminal(outputStream, inputStream, charset);
            }
        }
        else {
            return createTerminalEmulator();
        }
    }

    /**
     * Creates a new terminal emulator window which will be either Swing-based or AWT-based depending on what is
     * available on the system
     * @return New terminal emulator exposed as a {@link Terminal} interface
     */
    public Terminal createTerminalEmulator() {
        Window window;
        if(!forceAWTOverSwing && hasSwing()) {
            window = createSwingTerminal();
        }
        else {
            window = createAWTTerminal();
        }

        if(autoOpenTerminalFrame) {
            window.setVisible(true);
        }
        return (Terminal)window;
    }

    public AWTTerminalFrame createAWTTerminal() {
        return new AWTTerminalFrame(
                title,
                initialTerminalSize,
                deviceConfiguration,
                fontConfiguration,
                colorConfiguration,
                autoCloseTrigger);
    }

    public SwingTerminalFrame createSwingTerminal() {
        return new SwingTerminalFrame(
                title,
                initialTerminalSize,
                deviceConfiguration,
                fontConfiguration instanceof SwingTerminalFontConfiguration ? (SwingTerminalFontConfiguration)fontConfiguration : null,
                colorConfiguration,
                autoCloseTrigger);
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
     * Controls whether a SwingTerminalFrame shall always be created if the system is one with a graphical environment
     * @param forceTextTerminal If true, will always create a text-based Terminal
     * @return Reference to itself, so multiple .set-calls can be chained
     */
    public DefaultTerminalFactory setForceTextTerminal(boolean forceTextTerminal) {
        this.forceTextTerminal = forceTextTerminal;
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
     */
    public void setAutoOpenTerminalEmulatorWindow(boolean autoOpenTerminalFrame) {
        this.autoOpenTerminalFrame = autoOpenTerminalFrame;
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
     * Sets the auto-close trigger to use on created SwingTerminalFrames created by this factory
     * @param autoCloseTrigger Auto-close trigger to use on created SwingTerminalFrames created by this factory
     * @return Reference to itself, so multiple .set-calls can be chained
     */
    public DefaultTerminalFactory setTerminalEmulatorFrameAutoCloseTrigger(TerminalEmulatorAutoCloseTrigger autoCloseTrigger) {
        this.autoCloseTrigger = autoCloseTrigger;
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
     * @param mouseCaptureMode Capture mode for mouse interactions
     * @return Itself
     */
    public DefaultTerminalFactory setMouseCaptureMode(MouseCaptureMode mouseCaptureMode) {
        this.mouseCaptureMode = mouseCaptureMode;
        return this;
    }
    
    private Terminal createCygwinTerminal(OutputStream outputStream, InputStream inputStream, Charset charset) throws IOException {
        return new CygwinTerminal(inputStream, outputStream, charset);
    }

    private Terminal createUnixTerminal(OutputStream outputStream, InputStream inputStream, Charset charset) throws IOException {
        UnixTerminal unixTerminal = new UnixTerminal(inputStream, outputStream, charset);
        if(mouseCaptureMode != null) {
            unixTerminal.setMouseCaptureMode(mouseCaptureMode);
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
