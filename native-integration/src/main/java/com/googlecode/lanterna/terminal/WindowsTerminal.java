package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.terminal.ansi.TerminalDeviceControlStrategy;
import com.googlecode.lanterna.terminal.ansi.UnixLikeTerminal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Created by Martin on 2016-03-27.
 */
public class WindowsTerminal extends UnixLikeTerminal {

    public WindowsTerminal() throws IOException {
        this(System.in, System.out, Charset.defaultCharset(), CtrlCBehaviour.CTRL_C_KILLS_APPLICATION);
    }

    public WindowsTerminal(
            InputStream terminalInput,
            OutputStream terminalOutput,
            Charset terminalCharset,
            CtrlCBehaviour terminalCtrlCBehaviour) throws IOException {

        super(new WindowsTerminalDeviceController(),
                terminalInput,
                terminalOutput,
                terminalCharset,
                terminalCtrlCBehaviour);
    }
}
