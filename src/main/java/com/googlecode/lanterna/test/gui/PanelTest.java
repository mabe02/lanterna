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
 * Copyright (C) 2010-2012 Martin
 */

package com.googlecode.lanterna.test.gui;

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.TextGraphics;
import com.googlecode.lanterna.gui.Theme.Category;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.AbstractComponent;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.layout.SizePolicy;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.test.TestTerminalFactory;

/**
 *
 * @author Martin
 */
public class PanelTest
{
    public static void main(String[] args)
    {
        final GUIScreen guiScreen = new TestTerminalFactory(args).createGUIScreen();
        guiScreen.getScreen().startScreen();
        guiScreen.setTitle("GUI Test");
        final Window mainWindow = new Window("Window with panels");
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
                guiScreen.closeWindow();
            }
        }));

        guiScreen.showWindow(mainWindow, GUIScreen.Position.CENTER);
        guiScreen.getScreen().stopScreen();
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
            graphics.applyThemeItem(Category.DialogArea);
            for(int i = 0; i < graphics.getWidth(); i++)
                sb.append(fillCharacter);
            for(int i = 0; i < graphics.getHeight(); i++)
                graphics.drawString(0, i, sb.toString());
        }

    }
}
