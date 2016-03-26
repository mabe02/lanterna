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
package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.*;
import com.googlecode.lanterna.terminal.*;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by martin on 19/12/15.
 */
public class Issue190 {
    public static void main(String[] args) throws IOException {
        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        factory.setInitialTerminalSize(new TerminalSize(150,50));
        factory.setTerminalEmulatorTitle("name");
        Terminal terminal = factory.createTerminal();
        TerminalScreen screen = new TerminalScreen(terminal);
        screen.startScreen();

        Panel panel = new Panel();
        panel.setLayoutManager(new BorderLayout());

        ActionListBox channels = new ActionListBox();
        channels.setLayoutData(BorderLayout.Location.LEFT);
        panel.addComponent(channels.withBorder(Borders.singleLine("Channels")));

        TextBox log = new TextBox("", TextBox.Style.MULTI_LINE);
        log.setReadOnly(true);
        log.setLayoutData(BorderLayout.Location.CENTER);
        panel.addComponent(log.withBorder(Borders.singleLine("Log")));

        Panel options = new Panel();
        options.setLayoutData(BorderLayout.Location.BOTTOM);

        options.withBorder(Borders.singleLine("Send Message"));

        options.setLayoutManager(new BorderLayout());

        final TextBox input = new TextBox("Message", TextBox.Style.SINGLE_LINE);
        input.setLayoutData(BorderLayout.Location.CENTER);
        options.addComponent(input);

        Button send = new Button("Send", new Runnable() {
            @Override
            public void run() {
                input.setText("");
            }
        });
        send.setLayoutData(BorderLayout.Location.RIGHT);
        options.addComponent(send);

        panel.addComponent(options.withBorder(Borders.singleLine("Send Message")));

        BasicWindow window = new BasicWindow();
        window.setComponent(panel.withBorder(Borders.doubleLine("DarkOwlBot")));

        window.setHints(Arrays.asList(Window.Hint.EXPANDED, Window.Hint.FIT_TERMINAL_WINDOW));

        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
        gui.addWindowAndWait(window);
    }
}
