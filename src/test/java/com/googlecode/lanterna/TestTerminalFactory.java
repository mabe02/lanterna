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

package com.googlecode.lanterna;

import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import java.io.IOException;
import javax.swing.JFrame;

/**
 * This class provides a unified way for the test program to get their terminal
 * objects
 * @author Martin
 */
public class TestTerminalFactory {

    private final DefaultTerminalFactory factory;

    public TestTerminalFactory(String[] args) {
        this(args, SwingTerminalFrame.AutoCloseTrigger.CloseOnExitPrivateMode);
    }
    
    public TestTerminalFactory(String[] args, SwingTerminalFrame.AutoCloseTrigger autoCloseTrigger) {
        factory = new DefaultTerminalFactory();
        factory.setSwingTerminalFrameAutoCloseTrigger(autoCloseTrigger);
        factory.setSuppressSwingTerminalFrame(args.length > 0 && "--no-swing".equals(args[0]));
    }

    public TestTerminalFactory withInitialTerminalSize(TerminalSize initialTerminalSize) {
        factory.setInitialTerminalSize(initialTerminalSize);
        return this;
    }

    public SwingTerminalFrame createSwingTerminal() {
        try {
            return (SwingTerminalFrame)createTerminal();
        }
        catch(Throwable e) {
            throw new IllegalStateException("Unable to create a SwingTerminalFrame", e);
        }
    }

    public Terminal createTerminal() throws IOException {
        Terminal terminal = factory.createTerminal();
        if(terminal instanceof SwingTerminalFrame) {
            ((SwingTerminalFrame)terminal).setVisible(true);
            ((SwingTerminalFrame)terminal).setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
        return terminal;
    }

    public Screen createScreen() throws IOException {
        return new TerminalScreen(createTerminal());
    }

    public GUIScreen createGUIScreen() throws IOException {
        return new GUIScreen(createScreen());
    }
}
