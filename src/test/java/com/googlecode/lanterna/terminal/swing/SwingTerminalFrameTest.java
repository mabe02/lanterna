package com.googlecode.lanterna.terminal.swing;

import org.junit.Before;
import org.junit.Test;

import java.awt.GraphicsEnvironment;
import java.util.EnumSet;
import javax.swing.SwingUtilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

public class SwingTerminalFrameTest {
    @Before
    public void requireDisplay() {
        assumeFalse(GraphicsEnvironment.isHeadless());
    }

    @Test
    public void testEmptyAutoCloseTriggers() throws Exception {
        SwingUtilities.invokeAndWait(() -> checkAutoCloseTriggers(new SwingTerminalFrame()));
    }

    @Test
    public void testNullAutoCloseTriggers() throws Exception {
        SwingUtilities.invokeAndWait(() -> checkAutoCloseTriggers(
                new SwingTerminalFrame((TerminalEmulatorAutoCloseTrigger[]) null)));
    }

    private void checkAutoCloseTriggers(SwingTerminalFrame frame) {
        try {
            assertTrue(frame.getAutoCloseTrigger().isEmpty());
            frame.setAutoCloseTrigger(TerminalEmulatorAutoCloseTrigger.CLOSE_ON_ESCAPE);
            assertEquals(EnumSet.of(TerminalEmulatorAutoCloseTrigger.CLOSE_ON_ESCAPE),
                    frame.getAutoCloseTrigger());
            frame.addAutoCloseTrigger(TerminalEmulatorAutoCloseTrigger.CLOSE_ON_EXIT_PRIVATE_MODE);
            assertEquals(EnumSet.of(TerminalEmulatorAutoCloseTrigger.CLOSE_ON_ESCAPE,
                    TerminalEmulatorAutoCloseTrigger.CLOSE_ON_EXIT_PRIVATE_MODE), frame.getAutoCloseTrigger());
            frame.setAutoCloseTrigger(null);
            assertTrue(frame.getAutoCloseTrigger().isEmpty());
        } finally {
            frame.dispose();
        }
    }
}
