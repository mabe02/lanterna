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
    
    /**
     * This enum lets you control some more low-level behaviors of this terminal object.
     */
    public static enum UnixTerminalMode {
        /**
         * This is the default mode for the UnixTerminal, SIGINT and SIGTSTP will be caught and
         * presented as a normal keyboard interaction on the input stream
         */
        CATCH_SIGNALS,
        /**
         * Using this mode, SIGINT and SIGTSTP won't be caught be the program and the java process
         * will behave normally when these signals are received. SIGINT will kill the process right
         * then and there and SIGTSTP will suspend the process, just like with normal programs.
         */
        DONT_CATCH_SIGNALS
    }
            
    /**
     * Creates a UnixTerminal using a specified input stream, output stream and character set.
     * @param terminalInput Input stream to read terminal input from
     * @param terminalOutput Output stream to write terminal output to
     * @param terminalCharset Character set to use when converting characters to bytes
     */
    public UnixTerminal(
            InputStream terminalInput, 
            OutputStream terminalOutput, 
            Charset terminalCharset)
    {
        this(terminalInput, terminalOutput, terminalCharset, null);
    }
          
    /**
     * Creates a UnixTerminal using a specified input stream, output stream and character set.
     * @param terminalInput Input stream to read terminal input from
     * @param terminalOutput Output stream to write terminal output to
     * @param terminalCharset Character set to use when converting characters to bytes
     * @param customSizeQuerier Object to use for looking up the size of the terminal, or null to
     * use the built-in method
     */  
    public UnixTerminal(
            InputStream terminalInput, 
            OutputStream terminalOutput, 
            Charset terminalCharset,
            UnixTerminalSizeQuerier customSizeQuerier)
    {
        this(terminalInput, terminalOutput, terminalCharset, customSizeQuerier, UnixTerminalMode.CATCH_SIGNALS);
    }
    
    /**
     * Creates a UnixTerminal using a specified input stream, output stream and character set.
     * @param terminalInput Input stream to read terminal input from
     * @param terminalOutput Output stream to write terminal output to
     * @param terminalCharset Character set to use when converting characters to bytes
     * @param customSizeQuerier Object to use for looking up the size of the terminal, or null to
     * use the built-in method
     * @param terminalMode Special settings on how the terminal will behave, see 
     * {@code UnixTerminalMode} for more details
     */  
    public UnixTerminal(
            InputStream terminalInput, 
            OutputStream terminalOutput, 
            Charset terminalCharset,
            UnixTerminalSizeQuerier customSizeQuerier,
            UnixTerminalMode terminalMode)
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
                    Object windowResizeHandler = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {Class.forName("sun.misc.SignalHandler")}, new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            if("handle".equals(method.getName())) {
                                queryTerminalSize();
                            }
                            return null;
                        }
                    });
                    Object restoreTerminalOnInterruptHandler = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {Class.forName("sun.misc.SignalHandler")}, new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            exitPrivateMode();
                            System.exit(1);
                            return null;
                        }
                    });
                    Object doNothingHandler = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {Class.forName("sun.misc.SignalHandler")}, new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            return null;
                        }
                    });
                    m.invoke(null, signalClass.getConstructor(String.class).newInstance("WINCH"), windowResizeHandler);
                    if(terminalMode == UnixTerminalMode.CATCH_SIGNALS) {
                        m.invoke(null, signalClass.getConstructor(String.class).newInstance("INT"), doNothingHandler);
                        m.invoke(null, signalClass.getConstructor(String.class).newInstance("TSTP"), doNothingHandler);
                    }
                    else {
                        m.invoke(null, signalClass.getConstructor(String.class).newInstance("INT"), restoreTerminalOnInterruptHandler);
                    }
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
        restoreEOFCtrlD();
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

    private static void restoreEOFCtrlD()
    {
        exec("/bin/sh", "-c",
                            "/bin/stty eof ^d < /dev/tty");
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
