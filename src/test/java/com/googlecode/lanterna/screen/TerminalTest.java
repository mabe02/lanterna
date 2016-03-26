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
package com.googlecode.lanterna.screen;

import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.graphics.TextGraphics;

import java.io.IOException;

/**
 *
 * @author martin
 */
public class TerminalTest {

    public static void main(String[] args) throws IOException {
        Screen screen = new TestTerminalFactory(args).createScreen();
        TextGraphics writer = new ScreenTextGraphics(screen);
        screen.startScreen();
        try {
            Thread.sleep(500);
        }
        catch(InterruptedException ignored) {
        }
        writer.putString(10, 10, "Hello world!");
        screen.refresh();
        try {
            Thread.sleep(500);
        }
        catch(InterruptedException ignored) {
        }
        writer.putString(10, 10, " ello world!");
        screen.refresh();
        try {
            Thread.sleep(500);
        }
        catch(InterruptedException ignored) {
        }
        writer.putString(10, 10, "  llo world!");
        screen.refresh();
        try {
            Thread.sleep(500);
        }
        catch(InterruptedException ignored) {
        }
        writer.putString(10, 10, "   lo world!");
        screen.refresh();
        try {
            Thread.sleep(500);
        }
        catch(InterruptedException ignored) {
        }
        writer.putString(10, 10, "    o world!");
        screen.refresh();
        try {
            Thread.sleep(500);
        }
        catch(InterruptedException ignored) {
        }
        writer.putString(10, 10, "      world!");
        screen.refresh();
        try {
            Thread.sleep(500);
        }
        catch(InterruptedException ignored) {
        }
        writer.putString(10, 10, "       orld!");
        screen.refresh();
        try {
            Thread.sleep(500);
        }
        catch(InterruptedException ignored) {
        }
        writer.putString(10, 10, "        rld!");
        screen.refresh();
        try {
            Thread.sleep(500);
        }
        catch(InterruptedException ignored) {
        }
        writer.putString(10, 10, "         ld!");
        screen.refresh();
        try {
            Thread.sleep(500);
        }
        catch(InterruptedException ignored) {
        }
        writer.putString(10, 10, "          d!");
        screen.refresh();
        try {
            Thread.sleep(500);
        }
        catch(InterruptedException ignored) {
        }
        writer.putString(10, 10, "           !");
        screen.refresh();
        try {
            Thread.sleep(500);
        }
        catch(InterruptedException ignored) {
        }
        writer.putString(10, 10, "            ");
        screen.refresh();
        try {
            Thread.sleep(500);
        }
        catch(InterruptedException ignored) {
        }
        screen.stopScreen();
    }
}
