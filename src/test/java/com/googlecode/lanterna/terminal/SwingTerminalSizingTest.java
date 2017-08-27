/*
 * Copyright (C) Klaus Hauschild - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Klaus Hauschild <klaus.hauschild.1984@gmail.com>, 2017
 */
package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;

import javax.swing.WindowConstants;

import static javax.swing.SwingUtilities.invokeLater;

/**
 * @author Klaus Hauschild
 */
public class SwingTerminalSizingTest {

    public static void main(String[] args) {
        invokeLater(new Runnable() {

            @Override
            public void run() {
                final SwingTerminalFrame swingTerminal = new DefaultTerminalFactory().setInitialTerminalSize(new TerminalSize
                        (30, 5))
                        .createSwingTerminal();
                swingTerminal.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                swingTerminal.setLocationRelativeTo(null);
                swingTerminal.pack();
                swingTerminal.setVisible(true);
                swingTerminal.addResizeListener(new TerminalResizeListener() {

                    @Override
                    public void onResized(Terminal terminal, TerminalSize size) {
                        printTerminalSize(swingTerminal);
                    }

                });
                printTerminalSize(swingTerminal);
            }

        });
    }

    private static void printTerminalSize(SwingTerminalFrame swingTerminal) {
        TextGraphics textGraphics = swingTerminal.newTextGraphics();
        textGraphics.putString(0, 0, "" + swingTerminal.getTerminalSize());
    }

}
