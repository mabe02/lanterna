package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.terminal.ansi.UnixTerminal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Created by martin on 27/03/16.
 */
public class NativeUnixTerminal extends UnixTerminal {
    public NativeUnixTerminal() throws IOException {
        this(System.in,
                System.out,
                Charset.defaultCharset(),
                CtrlCBehaviour.CTRL_C_KILLS_APPLICATION);
    }

    public NativeUnixTerminal(
            InputStream terminalInput,
            OutputStream terminalOutput,
            Charset terminalCharset,
            CtrlCBehaviour terminalCtrlCBehaviour) throws IOException {

        super(new NativeUnixTerminalDeviceController(),
                terminalInput,
                terminalOutput,
                terminalCharset,
                terminalCtrlCBehaviour);
    }
}
