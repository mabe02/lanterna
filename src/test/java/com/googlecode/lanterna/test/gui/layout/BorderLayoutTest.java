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
package com.googlecode.lanterna.test.gui.layout;

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.Component;
import com.googlecode.lanterna.gui.DefaultBackgroundRenderer;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.EmptySpace;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.dialog.ActionListDialog;
import com.googlecode.lanterna.gui.layout.BorderLayout;
import com.googlecode.lanterna.gui.layout.LayoutParameter;
import com.googlecode.lanterna.gui.layout.LinearLayout;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.test.TestTerminalFactory;
import com.googlecode.lanterna.test.gui.MockComponent;

/**
 *
 * @author Martin
 */
public class BorderLayoutTest {
    public static void main(String[] args)
    {
        final GUIScreen guiScreen = new TestTerminalFactory(args).createGUIScreen();
        guiScreen.getScreen().startScreen();
        guiScreen.setBackgroundRenderer(new DefaultBackgroundRenderer("GUI Test"));

        final Window mainWindow = new Window("Window with BorderLayout");
        final Panel borderLayoutPanel = new Panel("BorderLayout");
        borderLayoutPanel.setLayoutManager(new BorderLayout());
        final Component topComponent = new MockComponent('T', new TerminalSize(50, 3));
        final Component centerComponent = new MockComponent('C', new TerminalSize(5, 5));
        final Component leftComponent = new MockComponent('L', new TerminalSize(6, 3));
        final Component rightComponent = new MockComponent('R', new TerminalSize(6, 3));
        final Component bottomComponent = new MockComponent('B', new TerminalSize(50, 3));
        
        borderLayoutPanel.addComponent(topComponent, BorderLayout.TOP);
        borderLayoutPanel.addComponent(centerComponent, BorderLayout.CENTER);
        borderLayoutPanel.addComponent(bottomComponent, BorderLayout.BOTTOM);
        borderLayoutPanel.addComponent(leftComponent, BorderLayout.LEFT);
        borderLayoutPanel.addComponent(rightComponent, BorderLayout.RIGHT);
        mainWindow.addComponent(borderLayoutPanel);
        
        Panel buttonPanel = new Panel(Panel.Orientation.HORISONTAL);
        buttonPanel.addComponent(new EmptySpace(), LinearLayout.GROWS_HORIZONTALLY);
        Button toggleButton = new Button("Toggle components", new Action() {
            @Override
            public void doAction() {
                ActionListDialog.showActionListDialog(
                        guiScreen,
                        "Toggle component",
                        "Choose a component to show/hide",
                        new ToggleAction("Top", borderLayoutPanel, topComponent, BorderLayout.TOP),
                        new ToggleAction("Left", borderLayoutPanel, leftComponent, BorderLayout.LEFT),
                        new ToggleAction("Center", borderLayoutPanel, centerComponent, BorderLayout.CENTER),
                        new ToggleAction("Right", borderLayoutPanel, rightComponent, BorderLayout.RIGHT),
                        new ToggleAction("Bottom", borderLayoutPanel, bottomComponent, BorderLayout.BOTTOM));
            }
        });
        buttonPanel.addComponent(toggleButton);
        Button closeButton = new Button("Close", new Action() {
            @Override
            public void doAction()
            {
                mainWindow.close();
            }
        });
        buttonPanel.addComponent(closeButton);
        mainWindow.addComponent(buttonPanel, LinearLayout.GROWS_HORIZONTALLY);

        guiScreen.showWindow(mainWindow, GUIScreen.Position.CENTER);
        guiScreen.getScreen().stopScreen();
    }
    
    private static class ToggleAction implements Action {
        private final String description;
        private final Panel panel;
        private final Component component;
        private final LayoutParameter layoutParameter;

        public ToggleAction(String description, Panel panel, Component component, LayoutParameter layoutParameter) {
            this.description = description;
            this.panel = panel;
            this.component = component;
            this.layoutParameter = layoutParameter;
        }

        @Override
        public void doAction() {
            if(panel.containsComponent(component)) {
                panel.removeComponent(component);
            }
            else {
                panel.addComponent(component, layoutParameter);
            }
        }

        @Override
        public String toString() {
            return description;
        }        
    }
}
