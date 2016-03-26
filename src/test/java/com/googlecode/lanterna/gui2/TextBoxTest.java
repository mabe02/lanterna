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
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;

import java.io.IOException;

/**
 * Created by martin on 05/10/14.
 */
public class TextBoxTest extends TestBase {
    public static void main(String[] args) throws IOException, InterruptedException {
        new TextBoxTest().run(args);
    }

    @Override
    public void init(WindowBasedTextGUI textGUI) {
        final BasicWindow window = new BasicWindow("TextBoxTest");
        Panel mainPanel = new Panel();
        mainPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        Panel leftPanel = new Panel();
        Panel rightPanel = new Panel();

        leftPanel.addComponent(new TextBox().withBorder(Borders.singleLine("Default")));
        leftPanel.addComponent(new TextBox("Some text").withBorder(Borders.singleLine("With init")));
        leftPanel.addComponent(new TextBox(new TerminalSize(10, 1), "Here is some text that is too long to fit in the text box").withBorder(Borders.singleLine("Long text")));
        leftPanel.addComponent(new TextBox("password").setMask('*').withBorder(Borders.singleLine("Password")));

        rightPanel.addComponent(new TextBox(new TerminalSize(15, 5),
                "Well here we are again\n" +
                "It's always such a pleasure\n" +
                "Remember when you tried\n" +
                "to kill me twice?\n" +
                "\n" +
                "あのときは笑いが止まりませんでしたね\n" +
                "私は笑っていませんが\n" +
                "状況を振り返ると\n" +
                "自分のやさしさに驚くほどです").withBorder(Borders.singleLine()));

        mainPanel.addComponent(leftPanel.withBorder(Borders.singleLine("Single line")));
        mainPanel.addComponent(rightPanel.withBorder(Borders.singleLine("Multiline")));

        window.setComponent(
                Panels.vertical(
                    mainPanel.withBorder(Borders.singleLine("Main")),
                    new Button("OK", new Runnable() {
                        @Override
                        public void run() {
                            window.close();
                        }
                    })));
        textGUI.addWindow(window);
    }
}
