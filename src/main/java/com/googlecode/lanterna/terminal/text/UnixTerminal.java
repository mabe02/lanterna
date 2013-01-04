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
import com.googlecode.lanterna.input.Key;
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
    public static enum Behaviour {
        /**
         * Pressing ctrl+c doesn't kill the application, it will be added to the input queue as usual
         */
        DEFAULT,
        
        /**
         * Pressing ctrl+c will restore the terminal and kill the application
         */
        CTRL_C_KILLS_APPLICATION,
    }
    
    private final Behaviour terminalBehaviour;
    private String sttyStatusToRestore;
            
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
        this(terminalInput, terminalOutput, terminalCharset, customSizeQuerier, Behaviour.DEFAULT);
    }
    
    /**
     * Creates a UnixTerminal using a specified input stream, output stream and character set.
     * @param terminalInput Input stream to read terminal input from
     * @param terminalOutput Output stream to write terminal output to
     * @param terminalCharset Character set to use when converting characters to bytes
     * @param customSizeQuerier Object to use for looking up the size of the terminal, or null to
     * use the built-in method
     * @param terminalBehaviour Special settings on how the terminal will behave, see 
     * {@code UnixTerminalMode} for more details
     */  
    public UnixTerminal(
            InputStream terminalInput, 
            OutputStream terminalOutput, 
            Charset terminalCharset,
            UnixTerminalSizeQuerier customSizeQuerier,
            Behaviour terminalBehaviour)
    {
        super(terminalInput, terminalOutput, terminalCharset);
        this.terminalSizeQuerier = customSizeQuerier;
        this.terminalBehaviour = terminalBehaviour;
        this.sttyStatusToRestore = null;
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
                    m.invoke(null, signalClass.getConstructor(String.class).newInstance("WINCH"), windowResizeHandler);
                }
            }
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
    public Key readInput() {
        //Check if we have ctrl+c coming
        Key key = super.readInput();
        if(key != null &&
                terminalBehaviour == Behaviour.CTRL_C_KILLS_APPLICATION &&
                key.getCharacter() == 'c' && 
                !key.isAltPressed() && 
                key.isCtrlPressed()) {
            
            exitPrivateMode();
            System.exit(1);
        }
        return key;
    }
    
    @Override
    public void enterPrivateMode()
    {
        super.enterPrivateMode();
        saveSTTY();
        setCBreak(true);
        setEcho(false);
        sttyMinimumCharacterForRead(1);
        disableSpecialCharacters();
    }

    @Override
    public void exitPrivateMode()
    {
        super.exitPrivateMode();
        restoreSTTY();
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
        exec("/bin/sh", "-c", "/bin/stty eof ^d < /dev/tty");
    }

    private static void disableSpecialCharacters()
    {
        exec("/bin/sh", "-c", "/bin/stty intr undef < /dev/tty");
        exec("/bin/sh", "-c", "/bin/stty start undef < /dev/tty");
        exec("/bin/sh", "-c", "/bin/stty stop undef < /dev/tty");
        exec("/bin/sh", "-c", "/bin/stty susp undef < /dev/tty");
    }

    private static void restoreSpecialCharacters()
    {
        exec("/bin/sh", "-c", "/bin/stty intr ^C < /dev/tty");
        exec("/bin/sh", "-c", "/bin/stty start ^Q < /dev/tty");
        exec("/bin/sh", "-c", "/bin/stty stop ^S < /dev/tty");
        exec("/bin/sh", "-c", "/bin/stty susp ^Z < /dev/tty");
    }
    
    private void saveSTTY() {
        sttyStatusToRestore = exec("/bin/sh", "-c", "stty -g < /dev/tty").trim();
    }
    
    private void restoreSTTY() {
        if(sttyStatusToRestore == null) {
            //Nothing to restore
            return;
        }
        
        exec("/bin/sh", "-c", "stty " + sttyStatusToRestore + " < /dev/tty");
        sttyStatusToRestore = null;
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
