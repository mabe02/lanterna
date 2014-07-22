package com.googlecode.lanterna.terminal;

import java.io.IOException;

/**
 * This class extends the normal Terminal interface and adds a few more methods that are considered rare and shouldn't
 * be encouraged to be used. Some of these may move into Terminal if it turns out that they are indeed well-supported.
 * @author Martin
 */
public interface ExtendedTerminal extends Terminal {

    /**
     * Attempts to resize the terminal through dtterm extensions "CSI 8 ; rows ; columns ; t". This isn't widely
     * supported, which is why the method is not exposed through the common Terminal interface.
     * @throws java.io.IOException
     */
    void setTerminalSize(int columns, int rows) throws IOException;
}
