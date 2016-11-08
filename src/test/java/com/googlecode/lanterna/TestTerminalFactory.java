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

import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.MouseCaptureMode;

/**
 * This class provides a unified way for the test program to get their terminal
 * objects
 * @author Martin
 */
public class TestTerminalFactory extends DefaultTerminalFactory {

    public TestTerminalFactory() {
    }

    public TestTerminalFactory(String[] args) {
        parseArgs(args);
    }

    public void parseArgs(String[] args) {
        if (args == null) { return; }
        for(String arg: args) {
            if (arg == null) { continue; }
            String tok[] = arg.split("=", 2);
            arg = tok[0]; // only the part before "="
            String par = tok.length > 1 ? tok[1] : "";
            if("--text-terminal".equals(arg) || "--no-swing".equals(arg)) {
                setPreferTerminalEmulator(false);
                setForceTextTerminal(true);
            }
            else if("--awt".equals(arg)) {
                setForceTextTerminal(false);
                setPreferTerminalEmulator(true);
                setForceAWTOverSwing(true);
            }
            else if("--swing".equals(arg)) {
                setForceTextTerminal(false);
                setPreferTerminalEmulator(true);
                setForceAWTOverSwing(false);
            }
            else if("--mouse-click".equals(arg)) {
                setMouseCaptureMode(MouseCaptureMode.CLICK_RELEASE);
            }
            else if("--mouse-drag".equals(arg)) {
                setMouseCaptureMode(MouseCaptureMode.CLICK_RELEASE_DRAG);
            }
            else if("--mouse-move".equals(arg)) {
                setMouseCaptureMode(MouseCaptureMode.CLICK_RELEASE_DRAG_MOVE);
            }
            else if("--telnet-port".equals(arg)) {
                int port = 1024; // default for option w/o param
                try { port = Integer.valueOf(par); }
                catch (NumberFormatException e) {}
                setTelnetPort(port);
            }
            else if("--with-timeout".equals(arg)) {
                int inputTimeout = 40; // default for option w/o param
                try { inputTimeout = Integer.valueOf(par); }
                catch (NumberFormatException e) {}
                setInputTimeout(inputTimeout);
            }
        }
    }
}
