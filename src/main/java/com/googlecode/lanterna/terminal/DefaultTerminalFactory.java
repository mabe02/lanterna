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
import com.googlecode.lanterna.terminal.swing.SwingTerminalColorConfiguration;
import com.googlecode.lanterna.terminal.swing.SwingTerminalDeviceConfiguration;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * This TerminalFactory implementation uses a simple auto-detection mechanism for figuring out which terminal 
 * implementation to create based on characteristics of the system the program is running on.
 * <p/>
 * Note that for all systems with a graphical environment present, the SwingTerminalFrame will be chosen. You can 
 * suppress this by calling setSuppressSwingTerminalFrame(true) on this factory.
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
    private boolean suppressSwingTerminalFrame;
    private String title;
    private boolean autoOpenSwingTerminalFrame;
    private SwingTerminalFrame.AutoCloseTrigger autoCloseTrigger;
    private SwingTerminalColorConfiguration colorConfiguration;
    private SwingTerminalDeviceConfiguration deviceConfiguration;
    private SwingTerminalFontConfiguration fontConfiguration;
    
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
        
        this.suppressSwingTerminalFrame = false;
        this.autoOpenSwingTerminalFrame = true;
        this.title = "SwingTerminalFrame";
        this.autoCloseTrigger = SwingTerminalFrame.AutoCloseTrigger.CloseOnExitPrivateMode;
        this.colorConfiguration = SwingTerminalColorConfiguration.DEFAULT;
        this.deviceConfiguration = SwingTerminalDeviceConfiguration.DEFAULT;
        this.fontConfiguration = SwingTerminalFontConfiguration.DEFAULT;
    }
    
    @Override
    public Terminal createTerminal() throws IOException {
        if (GraphicsEnvironment.isHeadless() || suppressSwingTerminalFrame) {
            if(isOperatingSystemWindows()) {
                return createCygwinTerminal(outputStream, inputStream, charset);
            }
            else {
                return createUnixTerminal(outputStream, inputStream, charset);
            }
        }
        else {
            SwingTerminalFrame swingTerminalFrame = new SwingTerminalFrame(
                    title,
                    initialTerminalSize,
                    deviceConfiguration,
                    fontConfiguration,
                    colorConfiguration,
                    autoCloseTrigger);

            if(autoOpenSwingTerminalFrame) {
                swingTerminalFrame.setVisible(true);
            }
            return swingTerminalFrame;
        }
    }

    /**
     * Sets a hint to the TerminalFactory of what size to use when creating the terminal. Most terminals are not created
     * on request but for example the SwingTerminal and SwingTerminalFrame are and this value will be passed down on
     * creation.
     *
     * @param initialTerminalSize Size (in rows and columns) of the newly created terminal
     */
    public void setInitialTerminalSize(TerminalSize initialTerminalSize) {
        this.initialTerminalSize = initialTerminalSize;
    }

    /**
     * Controls whether a SwingTerminalFrame shall always be created if the system is one with a graphical environment
     * @param suppressSwingTerminalFrame If true, will always create a text-based Terminal
     * @return Reference to itself, so multiple .set-calls can be chained
     */
    public DefaultTerminalFactory setSuppressSwingTerminalFrame(boolean suppressSwingTerminalFrame) {
        this.suppressSwingTerminalFrame = suppressSwingTerminalFrame;
        return this;
    }

    /**
     * Controls whether a SwingTerminalFrame shall be automatically shown (.setVisible(true)) immediately after 
     * creation. If {@code false}, you will manually need to call {@code .setVisible(true)} on the JFrame to actually
     * see the terminal window. Default for this value is {@code true}.
     * @param autoOpenSwingTerminalFrame Automatically open SwingTerminalFrame after creation
     */
    public void setAutoOpenSwingTerminalFrame(boolean autoOpenSwingTerminalFrame) {
        this.autoOpenSwingTerminalFrame = autoOpenSwingTerminalFrame;
    }
    
    /**
     * Sets the title to use on created SwingTerminalFrames created by this factory
     * @param title Title to use on created SwingTerminalFrames created by this factory
     * @return Reference to itself, so multiple .set-calls can be chained
     */
    public DefaultTerminalFactory setSwingTerminalFrameTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Sets the auto-close trigger to use on created SwingTerminalFrames created by this factory
     * @param autoCloseTrigger Auto-close trigger to use on created SwingTerminalFrames created by this factory
     * @return Reference to itself, so multiple .set-calls can be chained
     */
    public DefaultTerminalFactory setSwingTerminalFrameAutoCloseTrigger(SwingTerminalFrame.AutoCloseTrigger autoCloseTrigger) {
        this.autoCloseTrigger = autoCloseTrigger;
        return this;
    }

    /**
     * Sets the color configuration to use on created SwingTerminalFrames created by this factory
     * @param colorConfiguration Color configuration to use on created SwingTerminalFrames created by this factory
     * @return Reference to itself, so multiple .set-calls can be chained
     */
    public DefaultTerminalFactory setSwingTerminalFrameColorConfiguration(SwingTerminalColorConfiguration colorConfiguration) {
        this.colorConfiguration = colorConfiguration;
        return this;
    }

    /**
     * Sets the device configuration to use on created SwingTerminalFrames created by this factory
     * @param deviceConfiguration Device configuration to use on created SwingTerminalFrames created by this factory
     * @return Reference to itself, so multiple .set-calls can be chained
     */
    public DefaultTerminalFactory setSwingTerminalFrameDeviceConfiguration(SwingTerminalDeviceConfiguration deviceConfiguration) {
        this.deviceConfiguration = deviceConfiguration;
        return this;
    }

    /**
     * Sets the font configuration to use on created SwingTerminalFrames created by this factory
     * @param fontConfiguration Font configuration to use on created SwingTerminalFrames created by this factory
     * @return Reference to itself, so multiple .set-calls can be chained
     */
    public DefaultTerminalFactory setSwingTerminalFrameFontConfiguration(SwingTerminalFontConfiguration fontConfiguration) {
        this.fontConfiguration = fontConfiguration;
        return this;
    }
    
    private Terminal createCygwinTerminal(OutputStream outputStream, InputStream inputStream, Charset charset) throws IOException {
        return new CygwinTerminal(inputStream, outputStream, charset);
    }

    private Terminal createUnixTerminal(OutputStream outputStream, InputStream inputStream, Charset charset) throws IOException {
        return new UnixTerminal(inputStream, outputStream, charset);
    }

    /**
     * Detects whether the running platform is Windows* by looking at the
     * operating system name system property
     */
    private static boolean isOperatingSystemWindows() {
        return System.getProperty("os.name", "").toLowerCase().startsWith("windows");
    }
}
