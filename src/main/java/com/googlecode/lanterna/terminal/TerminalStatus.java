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

package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.LanternaException;

/**
 * This class used to do terminal size querying, but that's been removed now.
 * It is now only used for turning off echo mode, configuring the input mode
 * and cbreak
 * @author mabe02
 */
class TerminalStatus
{
    static void setKeyEcho(final boolean enable) throws LanternaException
    {
        /*
        String tty = getTTY();
        if(tty == null)
            return; */

        ShellCommand.exec("/bin/sh", "-c",
                            "/bin/stty " + (enable ? "echo" : "-echo") + " < /dev/tty");
/*
        if("SunOS".equals(System.getProperty("os.name")))
            ShellCommand.exec("stty", enable ? "echo" : "-echo");
        else
            ShellCommand.exec("stty", "-F", tty, enable ? "echo" : "-echo");
 */
    }

    static void setMinimumCharacterForRead(final int nrCharacters) throws LanternaException
    {
        ShellCommand.exec("/bin/sh", "-c",
                            "/bin/stty min " + nrCharacters + " < /dev/tty");
        /*
        String tty = getTTY();
        if(tty == null)
            return;

        if("SunOS".equals(System.getProperty("os.name")))
            ShellCommand.exec("stty", "min", nrCharacters + "");
        else
            ShellCommand.exec("stty", "-F", tty, "min", nrCharacters + "");
         */
    }

    static void setCBreak(final boolean enable) throws LanternaException
    {
        //if("SunOS".equals(System.getProperty("os.name")))
            ShellCommand.exec("/bin/sh", "-c",
                            "/bin/stty " + (enable ? "-icanon" : "icanon") + " < /dev/tty");
        //else
          //  ShellCommand.exec("/bin/sh", "-c",
            //                "/bin/stty " + (enable ? "cbreak" : "-cbreak") + " < /dev/tty");

        /*
        String tty = getTTY();
        if(tty == null)
            return;

        if("SunOS".equals(System.getProperty("os.name")))
            ShellCommand.exec("stty", "-F", tty, enable ? "cbreak" : "-cbreak");
        else
            ShellCommand.exec("stty", enable ? "raw" : "-raw");
         */
    }
}
