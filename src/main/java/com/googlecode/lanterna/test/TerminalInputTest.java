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

package com.googlecode.lanterna.test;

import com.googlecode.lanterna.LanternaException;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalFactory;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.nio.charset.Charset;

/**
 *
 * @author martin
 */
public class TerminalInputTest
{
    public static void main(String[] args) throws LanternaException, InterruptedException
    {
        if(args.length > 0) {
            try {
                Thread.sleep(15000);
            }
            catch(InterruptedException e) {
            }
        }

        final Terminal rawTerminal = new TerminalFactory.Common().createTerminal(System.in, System.out, Charset.defaultCharset());
        if(rawTerminal == null) {
            System.err.println("Couldn't allocate a terminal!");
            return;
        }
        rawTerminal.enterPrivateMode();

        TerminalSize size = rawTerminal.queryTerminalSize();
        if(size != null) {
            String sizeString = size.toString();
            for(int i = 0; i < sizeString.length(); i++)
                rawTerminal.putCharacter(sizeString.charAt(i));
        }

        rawTerminal.addResizeListener(new Terminal.ResizeListener() {
            public void onResized(TerminalSize newSize)
            {
                try {
                    if(newSize != null) {
                        String sizeString = " Resized: " + newSize.toString();
                        for(int i = 0; i < sizeString.length(); i++)
                            rawTerminal.putCharacter(sizeString.charAt(i));
                    }
                }
                catch(LanternaException e) {}
            }
        });

        Key key = null;
        while(key == null) {
            Thread.sleep(400);
            key = rawTerminal.readInput();
        }

        size = rawTerminal.queryTerminalSize();
        if(size != null) {
            String sizeString = size.toString();
            for(int i = 0; i < sizeString.length(); i++)
                rawTerminal.putCharacter(sizeString.charAt(i));
        }
        
        Thread.sleep(3000);
        rawTerminal.exitPrivateMode();
        do {
            System.out.println(key.toString());
            key = rawTerminal.readInput();
        }
        while(key != null);
    }
}
