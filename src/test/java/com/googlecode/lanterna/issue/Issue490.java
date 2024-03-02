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
    
    Window window;
    
    void assignTheme(String themeName) {
        window.setTheme(LanternaThemes.getRegisteredTheme(themeName));
    }
    
    private void logAppendMax(int lineCount, String message) {
        TextBox log = logTextBox;
        try {
            while (log.getLineCount() >= lineCount) {
                log.removeLine(0);
            }
        } finally {
            log.addLine(message);
            // unfortunately some methods expect (row, column), some (column, row)
            log.setCaretPosition(new TerminalPosition(Integer.MAX_VALUE, log.getLineCount()));
        }
    }

    private TextBox logTextBox;
    
    void go() throws Exception {
        try (Screen screen = new DefaultTerminalFactory()
                .setTelnetPort(23000)
                .setMouseCaptureMode(MouseCaptureMode.CLICK_RELEASE_DRAG_MOVE)
                .setInitialTerminalSize(new TerminalSize(100, 140))
                .createScreen()) {
            screen.startScreen();
            WindowBasedTextGUI gui = new MultiWindowTextGUI(screen);
            window = new BasicWindow("Issue490");
            window.addWindowListener(new WindowListenerAdapter() {
                @Override
                public void onInput(com.googlecode.lanterna.gui2.Window basePane, com.googlecode.lanterna.input.KeyStroke keyStroke, java.util.concurrent.atomic.AtomicBoolean deliverEvent) {
                    log("input: " + keyStroke);
                }
            });
            window.setTheme(LanternaThemes.getRegisteredTheme("blaster"));
            window.setComponent(makeUi());
            gui.addWindowAndWait(window);
        }
    }

    Component makeUi() {
        
        // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        // instantiate ui components (no layout activities)
        logTextBox = new TextBox(new TerminalSize(80, 12));
        logTextBox.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        Button clearLogButton = new Button("CLEAR LOG", () -> logTextBox.setText(""));
        
        ActionListBox themes = new ActionListBox(new TerminalSize(40, 62));
        themes.addItem("theme: default        ", () -> assignTheme("default"));
        themes.addItem("theme: defrost        ", () -> assignTheme("defrost"));
        themes.addItem("theme: bigsnake       ", () -> assignTheme("bigsnake"));
        themes.addItem("theme: conqueror      ", () -> assignTheme("conqueror"));
        themes.addItem("theme: businessmachine", () -> assignTheme("businessmachine"));
        themes.addItem("theme: blaster        ", () -> assignTheme("blaster"));
		
        ActionListBox listBox = new ActionListBox();
        ActionListBox listBox2 = new ActionListBox();
        
        eachOf(245, i -> listBox.addItem("assign: " + (5*i), () -> reassignItems(5*i, listBox2)));
        eachOf(245, i -> listBox2.addItem("item: " + i, () -> log("listBox2 item: " + i)));
        
        RadioBoxList radioBoxList = new RadioBoxList();
        eachOf(245, i -> radioBoxList.addItem("radio item: " + i));
        
        CheckBoxList<String> checkboxList = new CheckBoxList<>();
        eachOf(45, i -> checkboxList.addItem("heckboxList: " + i));
//=======
//        logTextBox = new TextBox(new TerminalSize(80, 10));
//        logTextBox.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
//        Button clearLogButton = new Button("CLEAR LOG", () -> logTextBox.setText(""));
//        
//        ActionListBox listBox = new ActionListBox();
//        eachOf(200, i -> listBox.addItem("item: " + i, () -> log("item: " + i)));
//        
//        ActionListBox listBox2 = new ActionListBox();
//        eachOf(200, i -> listBox2.addItem("item: " + i, () -> log("listBox2 item: " + i)));
//        
//        RadioBoxList radioBoxList = new RadioBoxList();
//        eachOf(200, i -> radioBoxList.addItem("radio item: " + i));
//        
//        CheckBoxList<String> checkboxList = new CheckBoxList<>();
//        eachOf(200, i -> checkboxList.addItem("heckboxList: " + i));
//>>>>>>> 91107fdf (rough cut of ScrollPanel)
        eachOf(245, i -> checkboxList.addItem("heckboxList: " + i));
        
        
        ActionListBox listBox3 = new ActionListBox();
        eachOf(245, i -> listBox3.addItem("item: " + i, () -> log("listBox3 item: " + i)));
        
        RadioBoxList radioBoxList2 = new RadioBoxList();
        eachOf(245, i -> radioBoxList2.addItem("radio item: " + i));
        
        CheckBoxList<String> checkboxList2 = new CheckBoxList<>();
        eachOf(245, i -> checkboxList2.addItem("heckboxList2: " + i));
        // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        
        // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        // arrange components
        Panel ui = new Panel(new LinearLayout(Direction.VERTICAL));
        ui.setPreferredSize(new TerminalSize(160, 40));
        ui.addComponent(logTextBox.withBorder(Borders.singleLine("log")));
        
        Panel hpanel = new Panel(new GridLayout(100));
        hpanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        hpanel.addComponent(themes.withBorder(Borders.singleLine("themes")));
        hpanel.addComponent(listBox.withBorder(Borders.singleLine("listBox")));
        hpanel.addComponent(new ScrollPanel(listBox2).withBorder(Borders.singleLine("scrollPanel listBox")));
        hpanel.addComponent(new ScrollPanel(radioBoxList).withBorder(Borders.singleLine("scrollPanel radio list")));
        hpanel.addComponent(new ScrollPanel(checkboxList).withBorder(Borders.singleLine("scrollPanel checkbox")));
        
        Panel hpanel2 = new Panel(new GridLayout(100));
        hpanel2.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        hpanel2.addComponent(listBox3.withBorder(Borders.singleLine("listBox3")));
        hpanel2.addComponent(radioBoxList2.withBorder(Borders.singleLine("radio list 2")));
        hpanel2.addComponent(checkboxList2.withBorder(Borders.singleLine("checkbox 2")));
        TextBox textBox = new TextBox("", TextBox.Style.MULTI_LINE);
        ScrollPanel textBoxScrollPanel = new ScrollPanel(textBox);
        textBoxScrollPanel.setPreferredSize(new TerminalSize(32, 16));
        eachOf(30, i -> textBox.addLine("-> " + i + ", aklkjh 0 "+i+" 876  "+i+" 76 s   "+i+" ==ssss55 "+i+" 55 555   "+i+" 5 5 55 "+i+"  55555 s "+i+" sssfa --> " + i ));
        hpanel2.addComponent(textBoxScrollPanel.withBorder(Borders.singleLine("scroll TextBox")));
        
        
        TextBox textBox2 = new TextBox("", TextBox.Style.MULTI_LINE);
        textBox2.setPreferredSize(new TerminalSize(32, 16));
        eachOf(30, i -> textBox2.addLine("abc: "+i+", aklkjh 0 "+i+" 876  "+i+" 76 s   "+i+" ==ssss55 "+i+" 55 555   "+i+" 5 5 55 "+i+"  55555 s "+i+" sssfa --> " + i ));
        hpanel2.addComponent(textBox2.withBorder(Borders.singleLine("TextBox old style")));
        
        ui.addComponent(Panels.vertical(hpanel, hpanel2));
        ui.addComponent(clearLogButton);
        // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        
        return ui;
    }
    
    void reassignItems(int count, ActionListBox listBox) {
        log("reassignItems(" + count + ", " + listBox + ")");
        listBox.clearItems();
        eachOf(count, i -> listBox.addItem("item: " + i, () -> log("item: " + i)));
    }

    void log(String message) {
        logAppendMax(10, message);
    }
    
    void eachOf(int count, Consumer<Integer> op) {
        for (int i = 0; i < count; i++) op.accept(i);
    }
    
    <T> void eachOf(Collection<T> items, Consumer<T> op) {
        for (T item : items) op.accept(item);
    }
}
