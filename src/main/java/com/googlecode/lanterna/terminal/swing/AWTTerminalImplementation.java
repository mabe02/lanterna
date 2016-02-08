package com.googlecode.lanterna.terminal.swing;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;

/**
 * Created by martin on 08/02/16.
 */
public class AWTTerminalImplementation extends GraphicalTerminalImplementation {
    private final Component component;
    private final AWTTerminalFontConfiguration fontConfiguration;

    public AWTTerminalImplementation(
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
        /*
        component.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {

            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                cancelTimer();
            }

            @Override
            public void ancestorMoved(AncestorEvent event) { }
        });
        */
    }


    /**
     * Returns the current font configuration. Note that it is immutable and cannot be changed.
     * @return This SwingTerminal's current font configuration
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
    protected boolean isEventDispatchThread() {
        return EventQueue.isDispatchThread();
    }

    @Override
    protected void repaint() {
        if(isEventDispatchThread()) {
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
    public void flush() {

    }
}
