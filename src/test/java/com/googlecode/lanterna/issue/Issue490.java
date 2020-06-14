/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
 *
 * lanterna is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2010-2020 Martin Berglund
 */
package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.*;
import com.googlecode.lanterna.terminal.*;
import com.googlecode.lanterna.bundle.LanternaThemes;

import java.io.IOException;
import java.util.function.*;
import java.util.*;

/**
 * <p>
 * Serves to manually test ScrollPanel during development of mouse support.
 * Uses Telnet port 23000 as you need something different than swing terminal
 * provided by IDE. After launching main method you can connect to it via terminal "telnet localhost 23000" (or something of that nature)
 * 
 * Or, this can be simply launched at the command line in a suitable terminal.
 * 
 * <p>
 */
public class Issue490 {

    public static void main(String[] args) throws Exception {
        new Issue490().go();
    }
    
    // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    
    private TextBox logTextBox;
    
    void go() throws Exception {
        try (Screen screen = new DefaultTerminalFactory()
                .setTelnetPort(23000)
                .setMouseCaptureMode(MouseCaptureMode.CLICK_RELEASE_DRAG)
                .setInitialTerminalSize(new TerminalSize(100, 100))
                .createScreen()) {
            screen.startScreen();
            WindowBasedTextGUI gui = new MultiWindowTextGUI(screen);
            Window window = new BasicWindow("Issue490");
            window.addWindowListener(new WindowListenerAdapter() {
                @Override
                public void onInput(com.googlecode.lanterna.gui2.Window basePane, com.googlecode.lanterna.input.KeyStroke keyStroke, java.util.concurrent.atomic.AtomicBoolean deliverEvent) {
                    log("input: " + keyStroke);
                }
            });
            window.setTheme(LanternaThemes.getRegisteredTheme("businessmachine"));
            window.setComponent(makeUi());
            gui.addWindowAndWait(window);
        }
    }

    Component makeUi() {
        
        // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        // instantiate ui components (no layout activities)
        logTextBox = new TextBox(new TerminalSize(80, 10));
        logTextBox.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        Button clearLogButton = new Button("CLEAR LOG", () -> logTextBox.setText(""));
        
        ActionListBox listBox = new ActionListBox();
        eachOf(200, i -> listBox.addItem("item: " + i, () -> log("item: " + i)));
        
        ActionListBox listBox2 = new ActionListBox();
        eachOf(200, i -> listBox2.addItem("item: " + i, () -> log("listBox2 item: " + i)));
        
        RadioBoxList radioBoxList = new RadioBoxList();
        eachOf(200, i -> radioBoxList.addItem("radio item: " + i));
        
        CheckBoxList<String> checkboxList = new CheckBoxList<>();
        eachOf(200, i -> checkboxList.addItem("heckboxList: " + i));
        // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        
        // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        // arrange components
        Panel ui = new Panel(new LinearLayout(Direction.VERTICAL));
        ui.setPreferredSize(new TerminalSize(160, 40));
        ui.addComponent(logTextBox.withBorder(Borders.singleLine("log")));
        
        Panel hpanel = new Panel(new GridLayout(100));
        hpanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        ui.addComponent(hpanel.withBorder(Borders.singleLine("components")));
        hpanel.addComponent(listBox.withBorder(Borders.singleLine("listBox")));
        hpanel.addComponent(new ScrollPanel(listBox2).withBorder(Borders.singleLine("scrollPanel listBox")));
        hpanel.addComponent(new ScrollPanel(radioBoxList).withBorder(Borders.singleLine("scrollPanel radio list")));
        hpanel.addComponent(new ScrollPanel(checkboxList).withBorder(Borders.singleLine("scrollPanel checkbox")));
        ui.addComponent(clearLogButton);
        // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        
        return ui;
    }
    
    void log(String message) {
        if (logTextBox.getText().trim().length() == 0) {
            logTextBox.setText(message);
        } else {
            logTextBox.addLine(message);
        }
    }
    
    void eachOf(int count, Consumer<Integer> op) {
        for (int i = 0; i < count; i++) op.accept(i);
    }
    
    <T> void eachOf(Collection<T> items, Consumer<T> op) {
        for (T item : items) op.accept(item);
    }
}