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

package com.googlecode.lanterna;

import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.MouseCaptureMode;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import com.googlecode.lanterna.terminal.swing.TerminalEmulatorAutoCloseTrigger;

import java.awt.*;
import java.io.IOException;
import javax.swing.JFrame;

/**
 * This class provides a unified way for the test program to get their terminal
 * objects
 * @author Martin
 */
public class TestTerminalFactory {

    private final DefaultTerminalFactory factory;
    private boolean forceTerminalEmulator;

    public TestTerminalFactory(String[] args) {
        this(args, TerminalEmulatorAutoCloseTrigger.CloseOnExitPrivateMode);
    }
    
    public TestTerminalFactory(String[] args, TerminalEmulatorAutoCloseTrigger autoCloseTrigger) {
        factory = new DefaultTerminalFactory();
        factory.setTerminalEmulatorFrameAutoCloseTrigger(autoCloseTrigger);
        for(String arg: args) {
            if("--text-terminal".equals(arg) || "--no-swing".equals(arg)) {
                factory.setForceTextTerminal(true);
            }
            else if("--awt".equals(arg)) {
                factory.setForceAWTOverSwing(true);
                forceTerminalEmulator = true;
            }
            else if("--swing".equals(arg)) {
                factory.setForceAWTOverSwing(false);
                forceTerminalEmulator = true;
            }
            else if("--mouse-click".equals(arg)) {
                factory.setMouseCaptureMode(MouseCaptureMode.CLICK_RELEASE);
            }
            else if("--mouse-drag".equals(arg)) {
                factory.setMouseCaptureMode(MouseCaptureMode.CLICK_RELEASE_DRAG);
            }
            else if("--mouse-move".equals(arg)) {
                factory.setMouseCaptureMode(MouseCaptureMode.CLICK_RELEASE_DRAG_MOVE);
            }
        }

    }

    public TestTerminalFactory withInitialTerminalSize(TerminalSize initialTerminalSize) {
        factory.setInitialTerminalSize(initialTerminalSize);
        return this;
    }

    public Terminal createTerminal() throws IOException {
        Terminal terminal;
        if(forceTerminalEmulator) {
            terminal = factory.createTerminalEmulator();
        }
        else {
            terminal = factory.createTerminal();
        }
        if(terminal instanceof Window) {
            ((Window) terminal).setVisible(true);
        }
        if(terminal instanceof JFrame) {
            ((JFrame)terminal).setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
        return terminal;
    }

    public Screen createScreen() throws IOException {
        return new TerminalScreen(createTerminal());
    }

    public SwingTerminalFrame createSwingTerminal() {
        return factory.createSwingTerminal();
    }
}
