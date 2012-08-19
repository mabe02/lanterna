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
 * Copyright (C) 2010-2012 Martin
 */

package com.googlecode.lanterna.terminal.text;

import com.googlecode.lanterna.LanternaException;
import com.googlecode.lanterna.input.GnomeTerminalProfile;
import com.googlecode.lanterna.input.PuttyProfile;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is trying to provide some special workarounds in order to function
 * in Cygwin terminal environments. 
 * <b>This is work in progress and is not functional at the moment</b>
 * @author Martin
 */
public class CygwinTerminal extends ANSITerminal {
    
    private static final Pattern STTY_SIZE_PATTERN = Pattern.compile(".*rows ([0-9]+);.*columns ([0-9]+);.*");
    
    private Timer resizeCheckTimer;
    
    public CygwinTerminal(
            InputStream terminalInput, 
            OutputStream terminalOutput, 
            Charset terminalCharset)
    {
        super(terminalInput, terminalOutput, terminalCharset);
        addInputProfile(new GnomeTerminalProfile());
        addInputProfile(new PuttyProfile());

        //Make sure to set an initial size
        onResized(80, 20);
        resizeCheckTimer = null;
    }

    @Override
    public TerminalSize getTerminalSize() {
        try {
            String stty = exec(findSTTY(), "-F", "/dev/pty0", "-a"); //exec(findShell(), "-c", "echo $PPID");
            Matcher matcher = STTY_SIZE_PATTERN.matcher(stty);
            if(matcher.matches())
                return new TerminalSize(Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(1)));
            else
                return new TerminalSize(80, 20);
        }
        catch(LanternaException e) {
            return new TerminalSize(80, 20);
        }
    }
    
    @Override
    public void enterPrivateMode()
    {
        super.enterPrivateMode();
        setCBreak(true);
        setEcho(false);
        sttyMinimumCharacterForRead(1);
        resizeCheckTimer = new Timer("CygwinTerminalResizeChecker");
        resizeCheckTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //queryTerminalSize();
            }            
        }, 1000, 1000);
    }

    @Override
    public void exitPrivateMode()
    {
        resizeCheckTimer.cancel();
        setEcho(true);
        super.exitPrivateMode();
        setCBreak(false);
    }

    @Override
    public void setCBreak(boolean cbreakOn) {
        sttyCBreak(cbreakOn);
    }

    @Override
    public void setEcho(boolean echoOn) {
        sttyKeyEcho(echoOn);
    }
    
    private static void sttyKeyEcho(final boolean enable)
    {
        exec(findSTTY(), "-F", "/dev/pty0", (enable ? "echo" : "-echo"));
        /*
        exec(findShell(), "-c",
                            "/bin/stty.exe " + (enable ? "echo" : "-echo") + " < /dev/tty");
                            */ 
    }

    private static void sttyMinimumCharacterForRead(final int nrCharacters)
    {
        exec(findSTTY(), "-F", "/dev/pty0", "min", nrCharacters + "");
        /*
        exec(findShell(), "-c",
                            "/bin/stty.exe min " + nrCharacters + " < /dev/tty");
                            */
    }

    private static void sttyCBreak(final boolean enable)
    {
        exec(findSTTY(), "-F", "/dev/pty0", (enable ? "cbreak" : "icanon"));
        /*
        exec(findShell(), "-c",
                            "/bin/stty.exe " + (enable ? "-icanon" : "icanon") + " < /dev/tty");
                            */ 
    }
    
    private static String findShell() {
        return findProgram("sh.exe");
    }
    
    private static String findSTTY() {
        return findProgram("stty.exe");
    }
    
    private static String findProgram(String programName) {
        String[] paths = System.getProperty("java.library.path").split(";");
        for(String path: paths) {
            File shBin = new File(path, programName);
            if(shBin.exists())
                return shBin.getAbsolutePath();
        }
        return programName;
    }
    
    private static String exec(String ...cmd)
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
