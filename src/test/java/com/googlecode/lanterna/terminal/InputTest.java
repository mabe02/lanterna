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
 * Copyright (C) 2010-2016 Martin
 */
package com.googlecode.lanterna.terminal;

import java.io.*;

/**
 * Use this program to see what the terminal emulator is sending through stdin; byte for byte
 */
public class InputTest {
    public static void main(String[] args) throws IOException {
        boolean useReader = false;
        boolean privateMode = false;
        for(String parameter: args) {
            if("--mouse-click".equals(parameter)) {
                writeCSISequenceToTerminal((byte) '?', (byte) '1', (byte) '0', (byte) '0', (byte) '0', (byte) 'h');
                writeCSISequenceToTerminal((byte) '?', (byte) '1', (byte) '0', (byte) '0', (byte) '5', (byte) 'h');
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        try {
                            writeCSISequenceToTerminal((byte) '?', (byte) '1', (byte) '0', (byte) '0', (byte) '0', (byte) 'l');
                        }
                        catch(IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            else if("--mouse-drag".equals(parameter)) {
                writeCSISequenceToTerminal((byte) '?', (byte) '1', (byte) '0', (byte) '0', (byte) '2', (byte) 'h');
                writeCSISequenceToTerminal((byte) '?', (byte) '1', (byte) '0', (byte) '0', (byte) '5', (byte) 'h');
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        try {
                            writeCSISequenceToTerminal((byte) '?', (byte) '1', (byte) '0', (byte) '0', (byte) '2', (byte) 'l');
                        }
                        catch(IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            else if("--mouse-move".equals(parameter)) {
                writeCSISequenceToTerminal((byte) '?', (byte) '1', (byte) '0', (byte) '0', (byte) '3', (byte) 'h');
                writeCSISequenceToTerminal((byte) '?', (byte) '1', (byte) '0', (byte) '0', (byte) '5', (byte) 'h');
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        try {
                            writeCSISequenceToTerminal((byte) '?', (byte) '1', (byte) '0', (byte) '0', (byte) '3', (byte) 'l');
                        }
                        catch(IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            else if("--reader".equals(parameter)) {
                useReader = true;
            }
            else if("--cbreak".equals(parameter)) {
                exec("sh", "-c", "stty -icanon < /dev/tty");
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        try {
                            exec("sh", "-c", "stty icanon < /dev/tty");
                        }
                        catch(IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            else if("--no-echo".equals(parameter)) {
                exec("sh", "-c", "stty -echo < /dev/tty");
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        try {
                            exec("sh", "-c", "stty echo < /dev/tty");
                        }
                        catch(IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            else if("--private".equals(parameter)) {
                privateMode = true;
            }
            else {
                System.err.println("Unknown parameter " + parameter);
                return;
            }
        }
        if(privateMode) {
            writeCSISequenceToTerminal((byte) '?', (byte) '1', (byte) '0', (byte) '4', (byte) '9', (byte) 'h');
            Runtime.getRuntime().addShutdownHook(new Thread("RestoreTerminal") {
                @Override
                public void run() {
                    try {
                        writeCSISequenceToTerminal((byte) '?', (byte) '1', (byte) '0', (byte) '4', (byte) '9', (byte) 'l');
                    }
                    catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        if(useReader) {
            InputStreamReader reader = new InputStreamReader(System.in);
            while(true) {
                int inChar = reader.read();
                if(inChar == -1) {
                    break;
                }
                System.out.println(formatData(inChar));
            }
        }
        else {
            while(true) {
                int inByte = System.in.read();
                if(inByte == -1) {
                    break;
                }
                System.out.println(formatData(inByte));
            }
        }
    }

    private static String formatData(int inByte) {
        String charString = Character.toString((char)inByte);
        if(Character.isISOControl(inByte)) {
            charString = "<control character>";
        }
        return inByte + " (0x" + Integer.toString(inByte, 16) + ", b" + Integer.toString(inByte, 2) + ", '" + charString + "')";
    }

    private static void writeCSISequenceToTerminal(byte... bytes) throws IOException {
        System.out.write(new byte[] { (byte)0x1b, (byte)'['});
        System.out.write(bytes);
        System.out.flush();
    }

    private static String exec(String... cmd) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process process = pb.start();
        ByteArrayOutputStream stdoutBuffer = new ByteArrayOutputStream();
        InputStream stdout = process.getInputStream();
        int readByte = stdout.read();
        while(readByte >= 0) {
            stdoutBuffer.write(readByte);
            readByte = stdout.read();
        }
        ByteArrayInputStream stdoutBufferInputStream = new ByteArrayInputStream(stdoutBuffer.toByteArray());
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdoutBufferInputStream));
        StringBuilder builder = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null) {
            builder.append(line);
        }
        reader.close();
        return builder.toString();
    }
}
