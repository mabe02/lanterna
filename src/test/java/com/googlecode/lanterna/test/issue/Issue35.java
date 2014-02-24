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
 * Copyright (C) 2010-2014 Martin
 */
package com.googlecode.lanterna.test.issue;

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.Component;
import com.googlecode.lanterna.gui.DefaultBackgroundRenderer;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.TextGraphics;
import com.googlecode.lanterna.gui.Theme.Category;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.AbstractComponent;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.layout.LinearLayout;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.test.TestTerminalFactory;

/**
 *
 * @author Martin
 */
public class Issue35
{
    public static void main(String[] args)
    {
        final GUIScreen guiScreen = new TestTerminalFactory(args).createGUIScreen();
        guiScreen.getScreen().startScreen();
        guiScreen.setBackgroundRenderer(new DefaultBackgroundRenderer("GUI Test"));

        final Window mainWindow = new Window("Window with panels");
        mainWindow.addComponent(new AbstractComponent() {
            @Override
            public void repaint(TextGraphics graphics)
            {
                graphics.applyTheme(graphics.getTheme().getDefinition(Category.SHADOW));
                for(int y = 0; y < graphics.getHeight(); y++)
                    for(int x = 0; x < graphics.getWidth(); x++)
                        graphics.drawString(x, y, "X");
            }

            @Override
            protected TerminalSize calculatePreferredSize() {
                return new TerminalSize(20, 6);
            }
        });
        Button button1 = new Button("Close", new Action() {
            @Override
            public void doAction()
            {
                mainWindow.close();
            }
        });
        button1.setAlignment(Component.Alignment.RIGHT_CENTER);
        mainWindow.addComponent(button1, LinearLayout.GROWS_HORIZONTALLY);
        
        //Override the size of the window
        mainWindow.setWindowSizeOverride(new TerminalSize(35, 15));

        guiScreen.showWindow(mainWindow, GUIScreen.Position.CENTER);
        guiScreen.getScreen().stopScreen();
    }
}
