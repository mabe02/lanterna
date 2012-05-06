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

package com.googlecode.lanterna.terminal.text;

import com.googlecode.lanterna.LanternaException;
import com.googlecode.lanterna.input.GnomeTerminalProfile;
import com.googlecode.lanterna.input.PuttyProfile;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.io.*;
import java.nio.charset.Charset;
import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * A common ANSI terminal extention with support for Unix resize signals and 
 * the stty program to control cbreak and key echo
 * @author mabe02
 */
public class UnixTerminal extends ANSITerminal
{
    private final TerminalSizeQuerier terminalSizeQuerier;
            
    public UnixTerminal(
            InputStream terminalInput, 
            OutputStream terminalOutput, 
            Charset terminalCharset)
    {
        this(terminalInput, terminalOutput, terminalCharset, null);
    }
            
    public UnixTerminal(
            InputStream terminalInput, 
            OutputStream terminalOutput, 
            Charset terminalCharset,
            TerminalSizeQuerier customSizeQuerier)
    {
        super(terminalInput, terminalOutput, terminalCharset);
        this.terminalSizeQuerier = customSizeQuerier;
        addInputProfile(new GnomeTerminalProfile());
        addInputProfile(new PuttyProfile());

        Signal.handle(new Signal("WINCH"), new SignalHandler() {
            public void handle(Signal signal)
            {
                try {
                    queryTerminalSize();
                }
                catch(LanternaException e) {
                }
            }
        });
    }

    public TerminalSize queryTerminalSize() throws LanternaException
    {
        if(terminalSizeQuerier != null)
            return terminalSizeQuerier.queryTerminalSize();
        
        synchronized(writerMutex) {
            saveCursorPosition();
            moveCursor(5000, 5000);
            reportPosition();
            restoreCursorPosition();
        }
        return getLastKnownSize();
    }
    
    @Override
    public void enterPrivateMode() throws LanternaException
    {
        super.enterPrivateMode();
        setCBreak(true);
        setEcho(false);
        sttyMinimumCharacterForRead(1);
    }

    @Override
    public void exitPrivateMode() throws LanternaException
    {
        super.exitPrivateMode();
        setCBreak(false);
        setEcho(true);
    }

    public void setCBreak(boolean cbreakOn) throws LanternaException {
        sttyICanon(cbreakOn);
    }

    public void setEcho(boolean echoOn) throws LanternaException {
        sttyKeyEcho(echoOn);
    }
    
    private static void sttyKeyEcho(final boolean enable) throws LanternaException
    {
        exec("/bin/sh", "-c",
                            "/bin/stty " + (enable ? "echo" : "-echo") + " < /dev/tty");
    }

    private static void sttyMinimumCharacterForRead(final int nrCharacters) throws LanternaException
    {
        exec("/bin/sh", "-c",
                            "/bin/stty min " + nrCharacters + " < /dev/tty");
    }

    private static void sttyICanon(final boolean enable) throws LanternaException
    {
        exec("/bin/sh", "-c",
                            "/bin/stty " + (enable ? "-icanon" : "icanon") + " < /dev/tty");
    }
    
    private static String exec(String ...cmd) throws LanternaException
    {
        try {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            Process process = pb.start();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream stdout = process.getInputStream();
            int readByte = stdout.read();
            while(readByte >= 0) {
                baos.write(readByte);
                readByte = stdout.read();
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            BufferedReader reader = new BufferedReader(new InputStreamReader(bais));
            StringBuilder builder = new StringBuilder();
            while(reader.ready()) {
                builder.append(reader.readLine());
            }
            reader.close();
            return builder.toString();
        }
        catch(IOException e) {
            throw new LanternaException(e);
        }
    }
}
