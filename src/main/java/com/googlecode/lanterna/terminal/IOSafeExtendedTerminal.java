package com.googlecode.lanterna.terminal;

/**
 * Interface extending ExtendedTerminal that removes the IOException throw clause.
 * 
 * @author Martin
 * @author Andreas
 */
public interface IOSafeExtendedTerminal extends IOSafeTerminal,ExtendedTerminal {

    @Override
    void setTerminalSize(int columns, int rows);

    @Override
    void setTitle(String title);

    @Override
    void pushTitle();

    @Override
    void popTitle();

    @Override
    void iconify();

    @Override
    void deiconify();

    @Override
    void maximize();

    @Override
    void unmaximize();

    @Override
    void setMouseCaptureMode(MouseCaptureMode mouseCaptureMode);

    @Override
    void scrollLines(int firstLine, int lastLine, int distance);
}
