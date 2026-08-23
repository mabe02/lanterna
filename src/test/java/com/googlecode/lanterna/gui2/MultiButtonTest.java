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
 * Copyright (C) 2010-2026 Martin Berglund
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;

public class MultiButtonTest extends TestBase {
    public static void main(String[] args) throws IOException, InterruptedException {
        new MultiButtonTest().run(args);
    }
    
    /*                 
     ┌──Button test───┐
     │<      >        │
     │< TRE  >        │
     │<Button>        │
     │<Another button>│
     │                │
     │<  OK  >        │
     └────────────────┘
    */
    @Override
    public void init(final WindowBasedTextGUI textGui) {
        final BasicWindow window = new BasicWindow("Button test");
        Panel contentArea = new Panel();
        contentArea.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        contentArea.addComponent(new Button("", () -> log("blank button clicked")));
        contentArea.addComponent(new Button("TRE", () -> log("TRE button clicked")));
        contentArea.addComponent(new Button("Button", () -> log("'Button' button clicked")));
        contentArea.addComponent(new Button("Another button", () -> log("'Another' button clicked")));
        contentArea.addComponent(new EmptySpace(new TerminalSize(5, 1)));
        //contentArea.addComponent(new Button("Here is a\nmulti-line\ntext segment that is using \\n"));
        contentArea.addComponent(new Button("OK", window::close));

        window.setComponent(contentArea);
        textGUI.addWindow(window);
    }
}
