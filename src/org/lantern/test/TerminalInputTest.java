/*
 *  Copyright (C) 2010 mabe02
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.lantern.test;

import org.lantern.LanternException;
import org.lantern.LanternTerminal;
import org.lantern.input.Key;
import org.lantern.terminal.Terminal;

/**
 *
 * @author martin
 */
public class TerminalInputTest
{
    public static void main(String[] args) throws LanternException, InterruptedException
    {
        if(args.length > 0) {
            try {
                Thread.sleep(15000);
            }
            catch(InterruptedException e) {
            }
        }

        Terminal rawTerminal = new LanternTerminal().getUnderlyingTerminal();
        if(rawTerminal == null) {
            System.err.println("Couldn't allocate a terminal!");
            return;
        }
        rawTerminal.enterPrivateMode();

        Key key = null;
        while(key == null) {
            Thread.sleep(400);
            key = rawTerminal.readInput();
        }
        rawTerminal.exitPrivateMode();
        do {
            System.out.println(key.toString());
            key = rawTerminal.readInput();
        }
        while(key != null);
    }
}
