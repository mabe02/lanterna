package com.googlecode.lanterna.terminal.swing;

import com.googlecode.lanterna.TerminalSize;

import javax.swing.*;
import java.awt.*;

/**
 * Created by martin on 08/02/16.
 */
public class SwingTerminal extends JComponent {

    private final SwingTerminalImplementation terminalImplementation;

    /**
     * Creates a new SwingTerminal with all the defaults set and no scroll controller connected.
     */
    public SwingTerminal() {
        this(new TerminalScrollController.Null());
    }


    /**
     * Creates a new SwingTerminal with a particular scrolling controller that will be notified when the terminals
     * history size grows and will be called when this class needs to figure out the current scrolling position.
     * @param scrollController Controller for scrolling the terminal history
     */
    @SuppressWarnings("WeakerAccess")
    public SwingTerminal(TerminalScrollController scrollController) {
        this(TerminalEmulatorDeviceConfiguration.getDefault(),
                SwingTerminalFontConfiguration.getDefault(),
                TerminalEmulatorColorConfiguration.getDefault(),
                scrollController);
    }

    /**
     * Creates a new SwingTerminal component using custom settings and no scroll controller.
     * @param deviceConfiguration Device configuration to use for this SwingTerminal
     * @param fontConfiguration Font configuration to use for this SwingTerminal
     * @param colorConfiguration Color configuration to use for this SwingTerminal
     */
    public SwingTerminal(
            TerminalEmulatorDeviceConfiguration deviceConfiguration,
            SwingTerminalFontConfiguration fontConfiguration,
            TerminalEmulatorColorConfiguration colorConfiguration) {

        this(null, deviceConfiguration, fontConfiguration, colorConfiguration);
    }

    /**
     * Creates a new SwingTerminal component using custom settings and no scroll controller.
     * @param initialTerminalSize Initial size of the terminal, which will be used when calculating the preferred size
     *                            of the component. If null, it will default to 80x25. If the AWT layout manager forces
     *                            the component to a different size, the value of this parameter won't have any meaning
     * @param deviceConfiguration Device configuration to use for this SwingTerminal
     * @param fontConfiguration Font configuration to use for this SwingTerminal
     * @param colorConfiguration Color configuration to use for this SwingTerminal
     */
    public SwingTerminal(
            TerminalSize initialTerminalSize,
            TerminalEmulatorDeviceConfiguration deviceConfiguration,
            SwingTerminalFontConfiguration fontConfiguration,
            TerminalEmulatorColorConfiguration colorConfiguration) {

        this(initialTerminalSize,
                deviceConfiguration,
                fontConfiguration,
                colorConfiguration,
                new TerminalScrollController.Null());
    }

    /**
     * Creates a new SwingTerminal component using custom settings and a custom scroll controller. The scrolling
     * controller will be notified when the terminal's history size grows and will be called when this class needs to
     * figure out the current scrolling position.
     * @param deviceConfiguration Device configuration to use for this SwingTerminal
     * @param fontConfiguration Font configuration to use for this SwingTerminal
     * @param colorConfiguration Color configuration to use for this SwingTerminal
     * @param scrollController Controller to use for scrolling, the object passed in will be notified whenever the
     *                         scrollable area has changed
     */
    public SwingTerminal(
            TerminalEmulatorDeviceConfiguration deviceConfiguration,
            SwingTerminalFontConfiguration fontConfiguration,
            TerminalEmulatorColorConfiguration colorConfiguration,
            TerminalScrollController scrollController) {

        this(null, deviceConfiguration, fontConfiguration, colorConfiguration, scrollController);
    }



    /**
     * Creates a new SwingTerminal component using custom settings and a custom scroll controller. The scrolling
     * controller will be notified when the terminal's history size grows and will be called when this class needs to
     * figure out the current scrolling position.
     * @param initialTerminalSize Initial size of the terminal, which will be used when calculating the preferred size
     *                            of the component. If null, it will default to 80x25. If the AWT layout manager forces
     *                            the component to a different size, the value of this parameter won't have any meaning
     * @param deviceConfiguration Device configuration to use for this SwingTerminal
     * @param fontConfiguration Font configuration to use for this SwingTerminal
     * @param colorConfiguration Color configuration to use for this SwingTerminal
     * @param scrollController Controller to use for scrolling, the object passed in will be notified whenever the
     *                         scrollable area has changed
     */
    public SwingTerminal(
            TerminalSize initialTerminalSize,
            TerminalEmulatorDeviceConfiguration deviceConfiguration,
            SwingTerminalFontConfiguration fontConfiguration,
            TerminalEmulatorColorConfiguration colorConfiguration,
            TerminalScrollController scrollController) {

        //Enforce valid values on the input parameters
        if(deviceConfiguration == null) {
            deviceConfiguration = TerminalEmulatorDeviceConfiguration.getDefault();
        }
        if(fontConfiguration == null) {
            fontConfiguration = SwingTerminalFontConfiguration.getDefault();
        }
        if(colorConfiguration == null) {
            colorConfiguration = TerminalEmulatorColorConfiguration.getDefault();
        }

        terminalImplementation = new SwingTerminalImplementation(
                this,
                fontConfiguration,
                initialTerminalSize,
                deviceConfiguration,
                colorConfiguration,
                scrollController);
    }

    public synchronized Dimension getPreferredSize() {
        return terminalImplementation.getPreferredSize();
    }

    protected synchronized void paintComponent(Graphics componentGraphics) {
        terminalImplementation.paintComponent(componentGraphics);
    }
}
