/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
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
 * Copyright (C) 2010-2020 Martin Berglund
 */
package com.googlecode.lanterna.terminal.ansi;

import com.googlecode.lanterna.TerminalSize;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class extends UnixLikeTerminal and implements the Cygwin-specific implementations. This means, running a Java
 * application using Lanterna inside the Cygwin Terminal application. The standard Windows command prompt (cmd.exe) is
 * not supported by this class.
 * <p>
 * <b>NOTE:</b> This class is experimental and does not fully work! Some of the operations, like disabling echo and
 * changing cbreak seems to be impossible to do without resorting to native code. Running "stty raw" before starting the
 * JVM will improve compatibility.
 * <p>
 * <b>NOTE:</b> This class will try to find Cygwin by scanning the directories on java.library.path, but you can also
 * tell it where Cygwin is installed by setting the CYGWIN_HOME environment variable.
 *
 * @author Martin
 * @author Andreas
 */
public class CygwinTerminal extends UnixLikeTTYTerminal {
    private static final String STTY_LOCATION = findProgram("stty.exe");
    private static final Pattern STTY_SIZE_PATTERN = Pattern.compile(".*rows ([0-9]+);.*columns ([0-9]+);.*");
    private static final String JAVA_LIBRARY_PATH_PROPERTY = "java.library.path";
    private static final String CYGWIN_HOME_ENV = "CYGWIN_HOME";

    /**
     * Creates a new CygwinTerminal based off input and output streams and a character set to use
     * @param terminalInput Input stream to read input from
     * @param terminalOutput Output stream to write output to
     * @param terminalCharset Character set to use when writing to the output stream
     * @throws IOException If there was an I/O error when trying to initialize the class and setup the terminal
     */
    public CygwinTerminal(
            InputStream terminalInput,
            OutputStream terminalOutput,
            Charset terminalCharset) throws IOException {
        super(null,
                terminalInput,
                terminalOutput,
                terminalCharset,
                CtrlCBehaviour.TRAP);
    }

    @Override
    protected TerminalSize findTerminalSize() {
        try {
            String stty = runSTTYCommand("-a");
            Matcher matcher = STTY_SIZE_PATTERN.matcher(stty);
            if(matcher.matches()) {
                return new TerminalSize(Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(1)));
            }
            else {
                return new TerminalSize(80, 24);
            }
        }
        catch(Throwable e) {
            return new TerminalSize(80, 24);
        }
    }

    @Override
    protected String runSTTYCommand(String... parameters) throws IOException {
        List<String> commandLine = new ArrayList<>(Arrays.asList(
                findSTTY(),
                "-F",
                getPseudoTerminalDevice()));
        commandLine.addAll(Arrays.asList(parameters));
        return exec(commandLine.toArray(new String[0]));
    }

    @Override
    protected void acquire() throws IOException {
        super.acquire();
        // Placeholder in case we want to add extra stty invocations for Cygwin
    }

    private String findSTTY() {
        return STTY_LOCATION;
    }

    private String getPseudoTerminalDevice() {
        //This will only work if you only have one terminal window open, otherwise we'll need to figure out somehow
        //which pty to use, which could be very tricky...
        return "/dev/pty0";
    }

    private static String findProgram(String programName) {
        if (System.getenv(CYGWIN_HOME_ENV) != null) {
            File cygwinHomeBinFile = new File(System.getenv(CYGWIN_HOME_ENV) + "/bin", programName);
            if (cygwinHomeBinFile.exists()) {
                return cygwinHomeBinFile.getAbsolutePath();
            }
        }
        String[] paths = System.getProperty(JAVA_LIBRARY_PATH_PROPERTY).split(";");
        for(String path : paths) {
            File shBin = new File(path, programName);
            if(shBin.exists()) {
                return shBin.getAbsolutePath();
            }
        }
        return programName;
    }
}
