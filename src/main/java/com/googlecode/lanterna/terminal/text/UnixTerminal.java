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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.Charset;

/**
 * A common ANSI terminal extention with support for Unix resize signals and 
 * the stty program to control cbreak and key echo
 * @author Martin
 */
public class UnixTerminal extends ANSITerminal
{
    private final UnixTerminalSizeQuerier terminalSizeQuerier;
            
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
            UnixTerminalSizeQuerier customSizeQuerier)
    {
        super(terminalInput, terminalOutput, terminalCharset);
        this.terminalSizeQuerier = customSizeQuerier;
        addInputProfile(new GnomeTerminalProfile());
        addInputProfile(new PuttyProfile());

        //Make sure to set an initial size
        onResized(80, 20);
        try {
            Class signalClass = Class.forName("sun.misc.Signal");
            for(Method m: signalClass.getDeclaredMethods()) {
                if("handle".equals(m.getName())) {
                    Object handler = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {Class.forName("sun.misc.SignalHandler")}, new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            if("handle".equals(method.getName())) {
                                queryTerminalSize();
                            }
                            return null;
                        }
                    });
                    m.invoke(null, signalClass.getConstructor(String.class).newInstance("WINCH"), handler);
                }
            }
            /*
            Signal.handle(new Signal("WINCH"), new SignalHandler() {
                public void handle(Signal signal)
                {
                    queryTerminalSize();
                }
            });
            */
        }
        catch(Throwable e) {
            System.err.println(e.getMessage());
        }
    }

    @Deprecated
    @Override
    public TerminalSize queryTerminalSize()
    {
        if(terminalSizeQuerier != null)
            return terminalSizeQuerier.queryTerminalSize();
        
        return super.queryTerminalSize();
    }

    @Override
    public TerminalSize getTerminalSize() {
        if(terminalSizeQuerier != null)
            return terminalSizeQuerier.queryTerminalSize();
        
        return super.getTerminalSize();
    }
    
    @Override
    public void enterPrivateMode()
    {
        super.enterPrivateMode();
        setCBreak(true);
        setEcho(false);
        sttyMinimumCharacterForRead(1);
    }

    @Override
    public void exitPrivateMode()
    {
        super.exitPrivateMode();
        setCBreak(false);
        setEcho(true);
    }

    @Override
    public void setCBreak(boolean cbreakOn) {
        sttyICanon(cbreakOn);
    }

    @Override
    public void setEcho(boolean echoOn) {
        sttyKeyEcho(echoOn);
    }
    
    private static void sttyKeyEcho(final boolean enable)
    {
        exec("/bin/sh", "-c",
                            "/bin/stty " + (enable ? "echo" : "-echo") + " < /dev/tty");
    }

    private static void sttyMinimumCharacterForRead(final int nrCharacters)
    {
        exec("/bin/sh", "-c",
                            "/bin/stty min " + nrCharacters + " < /dev/tty");
    }

    private static void sttyICanon(final boolean enable)
    {
        exec("/bin/sh", "-c",
                            "/bin/stty " + (enable ? "-icanon" : "icanon") + " < /dev/tty");
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
