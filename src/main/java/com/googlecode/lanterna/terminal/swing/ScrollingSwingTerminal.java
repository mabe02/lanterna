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
 * Copyright (C) 2010-2014 Martin
 */
package com.googlecode.lanterna.terminal.swing;

import com.googlecode.lanterna.input.KeyDecodingProfile;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.IOSafeTerminal;
import com.googlecode.lanterna.terminal.ResizeListener;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.TextColor;
import java.awt.BorderLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.concurrent.TimeUnit;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;

/**
 *
 * @author Martin
 */
public class ScrollingSwingTerminal extends JComponent implements IOSafeTerminal {
    
    private final SwingTerminal swingTerminal;
    private final JScrollBar scrollBar;
            
    public ScrollingSwingTerminal() {
        this(SwingTerminalDeviceConfiguration.DEFAULT,
                SwingTerminalFontConfiguration.DEFAULT,
                SwingTerminalColorConfiguration.DEFAULT);
    }
    
    public ScrollingSwingTerminal(
            SwingTerminalDeviceConfiguration deviceConfiguration,
            SwingTerminalFontConfiguration fontConfiguration,
            SwingTerminalColorConfiguration colorConfiguration) {
        
        this.scrollBar = new JScrollBar(JScrollBar.VERTICAL);
        this.swingTerminal = new SwingTerminal(
                deviceConfiguration, 
                fontConfiguration, 
                colorConfiguration, 
                new ScrollController());
        
        setLayout(new BorderLayout());
        add(swingTerminal, BorderLayout.CENTER);
        add(scrollBar, BorderLayout.EAST);
        this.scrollBar.setMinimum(0);
        this.scrollBar.setMaximum(20);
        this.scrollBar.setValue(0);
        this.scrollBar.setVisibleAmount(20);
        this.scrollBar.addAdjustmentListener(new ScrollbarListener());
        /*
        this.swingTerminal.addResizeListener(new ResizeListener() {
            @Override
            public void onResized(Terminal terminal, final TerminalSize newSize) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if(scrollBar.getVisibleAmount() < newSize.getRows()) {
                            scrollBar.setValue(scrollBar.getValue() - (newSize.getRows() - scrollBar.getVisibleAmount()));
                            scrollBar.setVisibleAmount(newSize.getRows());
                        }
                        else {
                            scrollBar.setVisibleAmount(newSize.getRows());
                        }
                    }
                });
            }
        });
        */
    }
    
    private class ScrollController implements SwingTerminal.ScrollingController {
        @Override
        public void updateModel(int totalSize, int screenSize) {
            if(scrollBar.getMaximum() != totalSize) {
                System.out.println("Setting maximum to " + totalSize);
                scrollBar.setMaximum(totalSize);
            }
            if(scrollBar.getVisibleAmount() != screenSize) {
                System.out.println("Setting visible amount to " + screenSize);
                scrollBar.setVisibleAmount(screenSize);
            }
        }
    }
    
    private class ScrollbarListener implements AdjustmentListener {
        int lastValue = -1;
        
        @Override
        public synchronized void adjustmentValueChanged(AdjustmentEvent e) {
            /*
            if(scrollBar.getValue() == lastValue) {
                return;
            }
            swingTerminal.setScrollOffset(-scrollBar.getValue() - scrollBar.getVisibleAmount());
            lastValue = scrollBar.getValue();
            */
        }
    }

    ///////////
    // Delegate all Terminal interface implementations to SwingTerminal
    ///////////
    @Override
    public KeyStroke readInput() {
        return swingTerminal.readInput();
    }

    @Override
    public void enterPrivateMode() {
        swingTerminal.enterPrivateMode();
    }

    @Override
    public void exitPrivateMode() {
        swingTerminal.exitPrivateMode();
    }

    @Override
    public void clearScreen() {
        swingTerminal.clearScreen();
    }

    @Override
    public void moveCursor(int x, int y) {
        swingTerminal.moveCursor(x, y);
    }

    @Override
    public void setCursorVisible(boolean visible) {
        swingTerminal.setCursorVisible(visible);
    }

    @Override
    public void putCharacter(char c) {
        swingTerminal.putCharacter(c);
    }

    @Override
    public void enableSGR(SGR sgr) {
        swingTerminal.enableSGR(sgr);
    }

    @Override
    public void disableSGR(SGR sgr) {
        swingTerminal.disableSGR(sgr);
    }

    @Override
    public void resetAllSGR() {
        swingTerminal.resetAllSGR();
    }

    @Override
    public void setForegroundColor(TextColor color) {
        swingTerminal.setForegroundColor(color);
    }

    @Override
    public void setBackgroundColor(TextColor color) {
        swingTerminal.setBackgroundColor(color);
    }

    @Override
    public TerminalSize getTerminalSize() {
        return swingTerminal.getTerminalSize();
    }

    @Override
    public byte[] enquireTerminal(int timeout, TimeUnit timeoutUnit) {
        return swingTerminal.enquireTerminal(timeout, timeoutUnit);
    }

    @Override
    public void flush() {
        swingTerminal.flush();
    }

    @Override
    public void addResizeListener(ResizeListener listener) {
        swingTerminal.addResizeListener(listener);
    }

    @Override
    public void removeResizeListener(ResizeListener listener) {
        swingTerminal.removeResizeListener(listener);
    }
}
