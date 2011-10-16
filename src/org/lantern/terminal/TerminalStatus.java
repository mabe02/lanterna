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
 * Copyright (C) 2010-2011 mabe02
 */

package org.lantern.terminal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.lantern.LanternException;

/**
 * A rather hacky class for figuring out and controlling the terminal environment.
 * Much of this is sort of Linux specific, but I've got some of it working 
 * on Solaris too.
 * @author mabe02
 */
class TerminalStatus
{
    private static String CACHED_TTY = null;
    private static final String STTY_ENV_VARIABLE = System.getenv("STTY_PATH");
    private static final String STTY_PROGRAM = 
            (STTY_ENV_VARIABLE != null && !STTY_ENV_VARIABLE.trim().equals("")) ?
                STTY_ENV_VARIABLE : "stty";
    public static TerminalSize USE_THIS_SIZE_INSTEAD_OF_QUERY_OS = null;

    private static int getPID() throws LanternException
    {
        return Integer.parseInt(ShellCommand.exec("bash", "-c", "echo $PPID"));
    }

    private static String getTTY() throws LanternException
    {
        if(CACHED_TTY != null)
            return CACHED_TTY;
        
        Integer myPid = getPID();
        String processRow = ShellCommand.exec("bash", "-c", "ps -A|grep " + myPid);
        Pattern psPattern = Pattern.compile(" *([0-9]+) ([^ ]+) +([^ ]*) (.*)");
        Matcher matcher = psPattern.matcher(processRow);
        if(!matcher.matches()) {
            return null;
        }
        
        String tty = matcher.group(2);
        if(tty.equals("?")) {
            //System.err.println("Error: no pts terminal!");
            return null;
        }
        CACHED_TTY = "/dev/" + tty;
        return CACHED_TTY;
    }

    static TerminalSize querySize() throws LanternException
    {
        if(USE_THIS_SIZE_INSTEAD_OF_QUERY_OS != null)
            return USE_THIS_SIZE_INSTEAD_OF_QUERY_OS;
        
        String tty = getTTY();
        if(tty == null)
            return null;
        
        String stty = (ShellCommand.exec(STTY_PROGRAM, "-F", tty, "-a"));
        String []splittedSTTY = stty.split(";");
        int terminalWidth = -1;
        int terminalHeight = -1;
        final Pattern columnsPattern = Pattern.compile("columns ([0-9]+)");
        final Pattern rowsPattern = Pattern.compile("rows ([0-9]+)");
        for(String sttyElement: splittedSTTY) {
            if(terminalHeight >= 0 && terminalWidth >= 0)
                break;

            final String element = sttyElement.trim();
            final Matcher colMatcher = columnsPattern.matcher(element);
            if(colMatcher.matches()) {
                terminalWidth = Integer.parseInt(colMatcher.group(1));
                continue;
            }
            
            final Matcher rowMatcher = rowsPattern.matcher(element);
            if(rowMatcher.matches()) {
                terminalHeight = Integer.parseInt(rowMatcher.group(1));
                continue;
            }
        }
        if(terminalHeight == -1 || terminalWidth == -1)
            return null;
        return new TerminalSize(terminalWidth, terminalHeight);
    }

    static boolean verifySizeQuery() throws LanternException
    {
        String tty = getTTY();
        if(tty == null)
            return false;

        String stty = (ShellCommand.exec(STTY_PROGRAM, "-F", tty, "-a"));
        String []splittedSTTY = stty.split(";");
        int terminalWidth = -1;
        int terminalHeight = -1;
        final Pattern columnsPattern = Pattern.compile("columns ([0-9]+)");
        final Pattern rowsPattern = Pattern.compile("rows ([0-9]+)");
        for(String sttyElement: splittedSTTY) {
            if(terminalHeight >= 0 && terminalWidth >= 0)
                break;

            final String element = sttyElement.trim();
            final Matcher colMatcher = columnsPattern.matcher(element);
            if(colMatcher.matches()) {
                terminalWidth = Integer.parseInt(colMatcher.group(1));
                continue;
            }

            final Matcher rowMatcher = rowsPattern.matcher(element);
            if(rowMatcher.matches()) {
                terminalHeight = Integer.parseInt(rowMatcher.group(1));
                continue;
            }
        }
        return terminalHeight != -1 && terminalWidth != -1;
    }

    static void setKeyEcho(final boolean enable) throws LanternException
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

    static void setMinimumCharacterForRead(final int nrCharacters) throws LanternException
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

    static void setCBreak(final boolean enable) throws LanternException
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
