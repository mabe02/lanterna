/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
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
 * Copyright (C) 2010-2020 Martin Berglund
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.*;
import com.googlecode.lanterna.bundle.*;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;
import java.util.*;

/**
 * Some common code for the GUI tests to get a text system up and running on a separate thread
 * @author Martin
 */
public abstract class TestBase {

    MultiWindowTextGUI textGui;
    

    void run(String[] args) throws IOException, InterruptedException {
        Screen screen = new TestTerminalFactory(args).createScreen();
        screen.startScreen();
        textGui = createTextGUI(screen);
        assignTheme(extractTheme(args));
        textGui.setBlockingIO(false);
        textGui.setEOFWhenNoWindows(true);
        //noinspection ResultOfMethodCallIgnored
        textGui.isEOFWhenNoWindows();   //No meaning, just to silence IntelliJ:s "is never used" alert

        try {
            textGui.addWindow(makeThemeChangerWindow());
         
            init(textGui);
            
            arrangeWindows();
            
            AsynchronousTextGUIThread guiThread = (AsynchronousTextGUIThread)textGui.getGUIThread();
            guiThread.start();
            afterGUIThreadStarted(textGui);
            guiThread.waitForStop();
        }
        finally {
            screen.stopScreen();
        }
    }

    private String extractTheme(String[] args) {
        for(int i = 0; i < args.length; i++) {
            if(args[i].equals("--theme") && i + 1 < args.length) {
                return args[i+1];
            }
        }
        return null;
    }

    protected MultiWindowTextGUI createTextGUI(Screen screen) {
        MultiWindowTextGUI gui = new MultiWindowTextGUI(new SeparateTextGUIThread.Factory(), screen, new DefaultWindowManager());
        //MultiWindowTextGUI gui = new MultiWindowTextGUI(new SeparateTextGUIThread.Factory(), screen);
        return gui;
    }

    public abstract void init(WindowBasedTextGUI textGui);
    public void afterGUIThreadStarted(WindowBasedTextGUI textGui) {
        // By default do nothing
    }
    
    
    public Window makeThemeChangerWindow() {
		    final Window window = new BasicWindow("Themes");
        ActionListBox themes = new ActionListBox();
        themes.setPreferredSize(new TerminalSize(30, 16));
        themes.addItem( "0, theme: default        ", () -> assignTheme("default"));
        themes.addItem( "1, theme: defrost        ", () -> assignTheme("defrost"));
        themes.addItem( "2, theme: bigsnake       ", () -> assignTheme("bigsnake"));
        themes.addItem( "3, theme: conqueror      ", () -> assignTheme("conqueror"));
        themes.addItem( "4, theme: businessmachine", () -> assignTheme("businessmachine"));
        themes.addItem( "5, theme: blaster        ", () -> assignTheme("blaster"));
        
        window.setComponent(themes);
        
        // 
        // unsure why, there is still some flicker case if ScrollPanel not quite used preferred size 
        //ScrollPanel scrollPanel = new ScrollPanel(themes);
        //scrollPanel.setPreferredSize(new TerminalSize(40, 20));
        //window.setComponent(scrollPanel);
        return window;
    }
    
    public void assignTheme(String themeName) {
        if (themeName == null) {
            return;
        }
        Theme theme = LanternaThemes.getRegisteredTheme(themeName);
        Collection<Window> windows = textGui.getWindows();
        if (theme != null && windows != null) {
            for (Window w : windows) {
                w.setTheme(theme);
            }
            textGui.setTheme(theme);
        }
    }
    
    public void arrangeWindows() {
        final int PAD = 8;
        int x = 1;
        int y = 1;
        for (Window w : textGui.getWindows()) {
            TerminalSize size = w.getPreferredSize();
            w.setPosition(new TerminalPosition(x, y));
            w.setHints(Collections.singletonList(Window.Hint.FIXED_POSITION));
            x += size.getColumns() + PAD;
        }
    }
    
}
