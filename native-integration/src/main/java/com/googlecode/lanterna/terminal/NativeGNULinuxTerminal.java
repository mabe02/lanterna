package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.terminal.ansi.UnixTerminal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Created by martin on 27/03/16.
 */
public class NativeGNULinuxTerminal extends UnixTerminal {
    public NativeGNULinuxTerminal() throws IOException {
        this(System.in,
                System.out,
                Charset.defaultCharset(),
                CtrlCBehaviour.CTRL_C_KILLS_APPLICATION);
    }

    public NativeGNULinuxTerminal(
            InputStream terminalInput,
            OutputStream terminalOutput,
            Charset terminalCharset,
            CtrlCBehaviour terminalCtrlCBehaviour) throws IOException {

        super(new NativeGNULinuxTerminalDeviceController(),
                terminalInput,
                terminalOutput,
                terminalCharset,
                terminalCtrlCBehaviour);
    }
}
