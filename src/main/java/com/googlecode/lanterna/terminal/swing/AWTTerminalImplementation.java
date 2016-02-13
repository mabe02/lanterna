package com.googlecode.lanterna.terminal.swing;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.input.KeyStroke;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Collections;

/**
 * AWT implementation of {@link GraphicalTerminalImplementation} that contains all the overrides for AWT
 * Created by martin on 08/02/16.
 */
class AWTTerminalImplementation extends GraphicalTerminalImplementation {
    private final Component component;
    private final AWTTerminalFontConfiguration fontConfiguration;

    /**
     * Creates a new {@code AWTTerminalImplementation}
     * @param component Component that is the AWT terminal surface
     * @param fontConfiguration Font configuration to use
     * @param initialTerminalSize Initial size of the terminal
     * @param deviceConfiguration Device configuration
     * @param colorConfiguration Color configuration
     * @param scrollController Controller to be used when inspecting scroll status
     */
    AWTTerminalImplementation(
            Component component,
            AWTTerminalFontConfiguration fontConfiguration,
            TerminalSize initialTerminalSize,
            TerminalEmulatorDeviceConfiguration deviceConfiguration,
            TerminalEmulatorColorConfiguration colorConfiguration,
            TerminalScrollController scrollController) {

        super(initialTerminalSize, deviceConfiguration, colorConfiguration, scrollController);
        this.component = component;
        this.fontConfiguration = fontConfiguration;

        //Prevent us from shrinking beyond one character
        component.setMinimumSize(new Dimension(fontConfiguration.getFontWidth(), fontConfiguration.getFontHeight()));

        //noinspection unchecked
        component.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.<AWTKeyStroke>emptySet());
        //noinspection unchecked
        component.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.<AWTKeyStroke>emptySet());

        component.addKeyListener(new TerminalInputListener());
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                AWTTerminalImplementation.this.component.requestFocusInWindow();
            }
        });

        component.addHierarchyListener(new HierarchyListener() {
            @Override
            public void hierarchyChanged(HierarchyEvent e) {
                if(e.getChangeFlags() == HierarchyEvent.DISPLAYABILITY_CHANGED) {
                    if(e.getChanged().isDisplayable()) {
                        startBlinkTimer();
                    }
                    else {
                        stopBlinkTimer();
                    }
                }
            }
        });
    }


    /**
     * Returns the current font configuration. Note that it is immutable and cannot be changed.
     * @return This {@link AWTTerminal}'s current font configuration
     */
    public AWTTerminalFontConfiguration getFontConfiguration() {
        return fontConfiguration;
    }

    @Override
    protected int getFontHeight() {
        return fontConfiguration.getFontHeight();
    }

    @Override
    protected int getFontWidth() {
        return fontConfiguration.getFontWidth();
    }

    @Override
    protected int getHeight() {
        return component.getHeight();
    }

    @Override
    protected int getWidth() {
        return component.getWidth();
    }

    @Override
    protected Font getFontForCharacter(TextCharacter character) {
        return fontConfiguration.getFontForCharacter(character);
    }

    @Override
    protected boolean isTextAntiAliased() {
        return fontConfiguration.isAntiAliased();
    }

    @Override
    protected void repaint() {
        if(EventQueue.isDispatchThread()) {
            component.repaint();
        }
        else {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    component.repaint();
                }
            });
        }
    }

    @Override
    public KeyStroke readInput() throws IOException {
        if(EventQueue.isDispatchThread()) {
            throw new UnsupportedOperationException("Cannot call SwingTerminal.readInput() on the AWT thread");
        }
        return super.readInput();
    }
}
