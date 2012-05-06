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
 * Copyright (C) 2010-2012 mabe02
 */

package com.googlecode.lanterna.test.gui;

import com.googlecode.lanterna.LanternaException;
import com.googlecode.lanterna.LanternTerminal;
import com.googlecode.lanterna.gui.*;
import com.googlecode.lanterna.gui.layout.SizePolicy;
import com.googlecode.lanterna.gui.theme.Theme.Category;
import com.googlecode.lanterna.terminal.TerminalSize;

/**
 *
 * @author mabe02
 */
public class PanelTest
{
    public static void main(String[] args) throws LanternaException
    {
        if(args.length > 0) {
            try {
                Thread.sleep(15000);
            }
            catch(InterruptedException e) {
            }
        }

        final LanternTerminal terminal = new LanternTerminal();
        if(terminal == null) {
            System.err.println("Couldn't allocate a terminal!");
            return;
        }

        terminal.start();
        final GUIScreen textGUI = terminal.getGUIScreen();

        textGUI.setTitle("GUI Test");
        final Window mainWindow = new Window("Ett fönster med paneler");
        TextFillComponent oneComponent = new TextFillComponent(5,5, '1');
        TextFillComponent twoComponent = new TextFillComponent(5,5, '2');
        TextFillComponent xComponent = new TextFillComponent(5,5, 'X');
        TextFillComponent threeComponent = new TextFillComponent(5,5, '3');
        TextFillComponent fourComponent = new TextFillComponent(5,5, '4');
        TextFillComponent fiveComponent = new TextFillComponent(5,5, '5');
        TextFillComponent sixComponent = new TextFillComponent(5,5, '6');
        Panel componentPanel = new Panel(Panel.Orientation.VERTICAL);
        componentPanel.addComponent(oneComponent);
        componentPanel.addComponent(twoComponent);
        componentPanel.addComponent(xComponent, SizePolicy.MAXIMUM);
        componentPanel.addComponent(threeComponent);
        componentPanel.addComponent(fourComponent);
        componentPanel.addComponent(fiveComponent);
        componentPanel.addComponent(sixComponent);
        mainWindow.addComponent(componentPanel, SizePolicy.MAXIMUM);
        mainWindow.addComponent(new Button("Close", new Action() {
            public void doAction()
            {
                textGUI.closeWindow(mainWindow);
            }
        }));

        textGUI.showWindow(mainWindow, GUIScreen.Position.CENTER);
        terminal.stopAndRestoreTerminal();
    }

    private static class TextFillComponent extends AbstractComponent
    {
        private final TerminalSize preferredSize;
        private final char fillCharacter;

        public TextFillComponent(int width, int height, char fillCharacter)
        {
            this.preferredSize = new TerminalSize(width, height);
            this.fillCharacter = fillCharacter;
        }

        public TerminalSize getPreferredSize()
        {
            return new TerminalSize(preferredSize);
        }

        public void repaint(TextGraphics graphics)
        {
            StringBuilder sb = new StringBuilder();
            graphics.applyThemeItem(Category.DefaultDialog);
            for(int i = 0; i < graphics.getWidth(); i++)
                sb.append(fillCharacter);
            for(int i = 0; i < graphics.getHeight(); i++)
                graphics.drawString(0, i, sb.toString());
        }

    }
}
