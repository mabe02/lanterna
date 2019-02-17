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
 * Copyright (C) 2010-2019 Martin Berglund
 */

package com.googlecode.lanterna.terminal.swing;

import com.googlecode.lanterna.TextColor;
import java.awt.Color;

/**
 * This class specifies the palette of colors the terminal will use for the normally available 8 + 1 ANSI colors but
 * also their 'bright' versions with are normally enabled through bold mode. There are several palettes available, all
 * based on popular terminal emulators. All colors are defined in the AWT format.
 * @author Martin
 */
@SuppressWarnings("WeakerAccess")
public class TerminalEmulatorPalette {
    /**
     * Values taken from gnome-terminal on Ubuntu
     */
    public static final TerminalEmulatorPalette GNOME_TERMINAL =
            new TerminalEmulatorPalette(
                    new java.awt.Color(211, 215, 207),
                    new java.awt.Color(238, 238, 236),
                    new java.awt.Color(46, 52, 54),
                    new java.awt.Color(46, 52, 54),
                    new java.awt.Color(85, 87, 83),
                    new java.awt.Color(204, 0, 0),
                    new java.awt.Color(239, 41, 41),
                    new java.awt.Color(78, 154, 6),
                    new java.awt.Color(138, 226, 52),
                    new java.awt.Color(196, 160, 0),
                    new java.awt.Color(252, 233, 79),
                    new java.awt.Color(52, 101, 164),
                    new java.awt.Color(114, 159, 207),
                    new java.awt.Color(117, 80, 123),
                    new java.awt.Color(173, 127, 168),
                    new java.awt.Color(6, 152, 154),
                    new java.awt.Color(52, 226, 226),
                    new java.awt.Color(211, 215, 207),
                    new java.awt.Color(238, 238, 236));

    /**
     * Values taken from <a href="http://en.wikipedia.org/wiki/ANSI_escape_code">
     * wikipedia</a>, these are supposed to be the standard VGA palette.
     */
    public static final TerminalEmulatorPalette STANDARD_VGA =
            new TerminalEmulatorPalette(
                    new java.awt.Color(170, 170, 170),
                    new java.awt.Color(255, 255, 255),
                    new java.awt.Color(0, 0, 0),
                    new java.awt.Color(0, 0, 0),
                    new java.awt.Color(85, 85, 85),
                    new java.awt.Color(170, 0, 0),
                    new java.awt.Color(255, 85, 85),
                    new java.awt.Color(0, 170, 0),
                    new java.awt.Color(85, 255, 85),
                    new java.awt.Color(170, 85, 0),
                    new java.awt.Color(255, 255, 85),
                    new java.awt.Color(0, 0, 170),
                    new java.awt.Color(85, 85, 255),
                    new java.awt.Color(170, 0, 170),
                    new java.awt.Color(255, 85, 255),
                    new java.awt.Color(0, 170, 170),
                    new java.awt.Color(85, 255, 255),
                    new java.awt.Color(170, 170, 170),
                    new java.awt.Color(255, 255, 255));

    /**
     * Values taken from <a href="http://en.wikipedia.org/wiki/ANSI_escape_code">
     * wikipedia</a>, these are supposed to be what Windows XP cmd is using.
     */
    public static final TerminalEmulatorPalette WINDOWS_XP_COMMAND_PROMPT =
            new TerminalEmulatorPalette(
                    new java.awt.Color(192, 192, 192),
                    new java.awt.Color(255, 255, 255),
                    new java.awt.Color(0, 0, 0),
                    new java.awt.Color(0, 0, 0),
                    new java.awt.Color(128, 128, 128),
                    new java.awt.Color(128, 0, 0),
                    new java.awt.Color(255, 0, 0),
                    new java.awt.Color(0, 128, 0),
                    new java.awt.Color(0, 255, 0),
                    new java.awt.Color(128, 128, 0),
                    new java.awt.Color(255, 255, 0),
                    new java.awt.Color(0, 0, 128),
                    new java.awt.Color(0, 0, 255),
                    new java.awt.Color(128, 0, 128),
                    new java.awt.Color(255, 0, 255),
                    new java.awt.Color(0, 128, 128),
                    new java.awt.Color(0, 255, 255),
                    new java.awt.Color(192, 192, 192),
                    new java.awt.Color(255, 255, 255));

    /**
     * Values taken from <a href="http://en.wikipedia.org/wiki/ANSI_escape_code">
     * wikipedia</a>, these are supposed to be what terminal.app on MacOSX is using.
     */
    public static final TerminalEmulatorPalette MAC_OS_X_TERMINAL_APP =
            new TerminalEmulatorPalette(
                    new java.awt.Color(203, 204, 205),
                    new java.awt.Color(233, 235, 235),
                    new java.awt.Color(0, 0, 0),
                    new java.awt.Color(0, 0, 0),
                    new java.awt.Color(129, 131, 131),
                    new java.awt.Color(194, 54, 33),
                    new java.awt.Color(252,57,31),
                    new java.awt.Color(37, 188, 36),
                    new java.awt.Color(49, 231, 34),
                    new java.awt.Color(173, 173, 39),
                    new java.awt.Color(234, 236, 35),
                    new java.awt.Color(73, 46, 225),
                    new java.awt.Color(88, 51, 255),
                    new java.awt.Color(211, 56, 211),
                    new java.awt.Color(249, 53, 248),
                    new java.awt.Color(51, 187, 200),
                    new java.awt.Color(20, 240, 240),
                    new java.awt.Color(203, 204, 205),
                    new java.awt.Color(233, 235, 235));

    /**
     * Values taken from <a href="http://en.wikipedia.org/wiki/ANSI_escape_code">
     * wikipedia</a>, these are supposed to be what putty is using.
     */
    public static final TerminalEmulatorPalette PUTTY =
            new TerminalEmulatorPalette(
                    new java.awt.Color(187, 187, 187),
                    new java.awt.Color(255, 255, 255),
                    new java.awt.Color(0, 0, 0),
                    new java.awt.Color(0, 0, 0),
                    new java.awt.Color(85, 85, 85),
                    new java.awt.Color(187, 0, 0),
                    new java.awt.Color(255, 85, 85),
                    new java.awt.Color(0, 187, 0),
                    new java.awt.Color(85, 255, 85),
                    new java.awt.Color(187, 187, 0),
                    new java.awt.Color(255, 255, 85),
                    new java.awt.Color(0, 0, 187),
                    new java.awt.Color(85, 85, 255),
                    new java.awt.Color(187, 0, 187),
                    new java.awt.Color(255, 85, 255),
                    new java.awt.Color(0, 187, 187),
                    new java.awt.Color(85, 255, 255),
                    new java.awt.Color(187, 187, 187),
                    new java.awt.Color(255, 255, 255));

    /**
     * Values taken from <a href="http://en.wikipedia.org/wiki/ANSI_escape_code">
     * wikipedia</a>, these are supposed to be what xterm is using.
     */
    public static final TerminalEmulatorPalette XTERM =
            new TerminalEmulatorPalette(
                    new java.awt.Color(229, 229, 229),
                    new java.awt.Color(255, 255, 255),
                    new java.awt.Color(0, 0, 0),
                    new java.awt.Color(0, 0, 0),
                    new java.awt.Color(127, 127, 127),
                    new java.awt.Color(205, 0, 0),
                    new java.awt.Color(255, 0, 0),
                    new java.awt.Color(0, 205, 0),
                    new java.awt.Color(0, 255, 0),
                    new java.awt.Color(205, 205, 0),
                    new java.awt.Color(255, 255, 0),
                    new java.awt.Color(0, 0, 238),
                    new java.awt.Color(92, 92, 255),
                    new java.awt.Color(205, 0, 205),
                    new java.awt.Color(255, 0, 255),
                    new java.awt.Color(0, 205, 205),
                    new java.awt.Color(0, 255, 255),
                    new java.awt.Color(229, 229, 229),
                    new java.awt.Color(255, 255, 255));

    /**
     * Default colors the SwingTerminal is using if you don't specify anything
     */
    public static final TerminalEmulatorPalette DEFAULT = GNOME_TERMINAL;

    private final Color defaultColor;
    private final Color defaultBrightColor;
    private final Color defaultBackgroundColor;
    private final Color normalBlack;
    private final Color brightBlack;
    private final Color normalRed;
    private final Color brightRed;
    private final Color normalGreen;
    private final Color brightGreen;
    private final Color normalYellow;
    private final Color brightYellow;
    private final Color normalBlue;
    private final Color brightBlue;
    private final Color normalMagenta;
    private final Color brightMagenta;
    private final Color normalCyan;
    private final Color brightCyan;
    private final Color normalWhite;
    private final Color brightWhite;

    /**
     * Creates a new palette with all colors specified up-front
     * @param defaultColor Default color which no specific color has been selected
     * @param defaultBrightColor Default color which no specific color has been selected but bold is enabled
     * @param defaultBackgroundColor Default color to use for the background when no specific color has been selected
     * @param normalBlack Color for normal black
     * @param brightBlack Color for bright black
     * @param normalRed Color for normal red
     * @param brightRed Color for bright red
     * @param normalGreen Color for normal green
     * @param brightGreen Color for bright green
     * @param normalYellow Color for normal yellow
     * @param brightYellow Color for bright yellow
     * @param normalBlue Color for normal blue
     * @param brightBlue Color for bright blue
     * @param normalMagenta Color for normal magenta
     * @param brightMagenta Color for bright magenta
     * @param normalCyan Color for normal cyan
     * @param brightCyan Color for bright cyan
     * @param normalWhite Color for normal white
     * @param brightWhite Color for bright white
     */
    public TerminalEmulatorPalette(
            Color defaultColor,
            Color defaultBrightColor,
            Color defaultBackgroundColor,
            Color normalBlack,
            Color brightBlack,
            Color normalRed,
            Color brightRed,
            Color normalGreen,
            Color brightGreen,
            Color normalYellow,
            Color brightYellow,
            Color normalBlue,
            Color brightBlue,
            Color normalMagenta,
            Color brightMagenta,
            Color normalCyan,
            Color brightCyan,
            Color normalWhite,
            Color brightWhite) {
        this.defaultColor = defaultColor;
        this.defaultBrightColor = defaultBrightColor;
        this.defaultBackgroundColor = defaultBackgroundColor;
        this.normalBlack = normalBlack;
        this.brightBlack = brightBlack;
        this.normalRed = normalRed;
        this.brightRed = brightRed;
        this.normalGreen = normalGreen;
        this.brightGreen = brightGreen;
        this.normalYellow = normalYellow;
        this.brightYellow = brightYellow;
        this.normalBlue = normalBlue;
        this.brightBlue = brightBlue;
        this.normalMagenta = normalMagenta;
        this.brightMagenta = brightMagenta;
        this.normalCyan = normalCyan;
        this.brightCyan = brightCyan;
        this.normalWhite = normalWhite;
        this.brightWhite = brightWhite;
    }

    /**
     * Returns the AWT color from this palette given an ANSI color and two hints for if we are looking for a background
     * color and if we want to use the bright version.
     * @param color Which ANSI color we want to extract
     * @param isForeground Is this color we extract going to be used as a background color?
     * @param useBrightTones If true, we should return the bright version of the color
     * @return AWT color extracted from this palette for the input parameters
     */
    public Color get(TextColor.ANSI color, boolean isForeground, boolean useBrightTones) {
        if(useBrightTones) {
            switch(color) {
                case BLACK:
                    return brightBlack;
                case BLUE:
                    return brightBlue;
                case CYAN:
                    return brightCyan;
                case DEFAULT:
                    return isForeground ? defaultBrightColor : defaultBackgroundColor;
                case GREEN:
                    return brightGreen;
                case MAGENTA:
                    return brightMagenta;
                case RED:
                    return brightRed;
                case WHITE:
                    return brightWhite;
                case YELLOW:
                    return brightYellow;
            }
        }
        else {
            switch(color) {
                case BLACK:
                    return normalBlack;
                case BLUE:
                    return normalBlue;
                case CYAN:
                    return normalCyan;
                case DEFAULT:
                    return isForeground ? defaultColor : defaultBackgroundColor;
                case GREEN:
                    return normalGreen;
                case MAGENTA:
                    return normalMagenta;
                case RED:
                    return normalRed;
                case WHITE:
                    return normalWhite;
                case YELLOW:
                    return normalYellow;
            }
        }
        throw new IllegalArgumentException("Unknown text color " + color);
    }

    @SuppressWarnings({"SimplifiableIfStatement", "ConstantConditions"})
    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        final TerminalEmulatorPalette other = (TerminalEmulatorPalette) obj;
        if(this.defaultColor != other.defaultColor && (this.defaultColor == null || !this.defaultColor.equals(other.defaultColor))) {
            return false;
        }
        if(this.defaultBrightColor != other.defaultBrightColor && (this.defaultBrightColor == null || !this.defaultBrightColor.equals(other.defaultBrightColor))) {
            return false;
        }
        if(this.defaultBackgroundColor != other.defaultBackgroundColor && (this.defaultBackgroundColor == null || !this.defaultBackgroundColor.equals(other.defaultBackgroundColor))) {
            return false;
        }
        if(this.normalBlack != other.normalBlack && (this.normalBlack == null || !this.normalBlack.equals(other.normalBlack))) {
            return false;
        }
        if(this.brightBlack != other.brightBlack && (this.brightBlack == null || !this.brightBlack.equals(other.brightBlack))) {
            return false;
        }
        if(this.normalRed != other.normalRed && (this.normalRed == null || !this.normalRed.equals(other.normalRed))) {
            return false;
        }
        if(this.brightRed != other.brightRed && (this.brightRed == null || !this.brightRed.equals(other.brightRed))) {
            return false;
        }
        if(this.normalGreen != other.normalGreen && (this.normalGreen == null || !this.normalGreen.equals(other.normalGreen))) {
            return false;
        }
        if(this.brightGreen != other.brightGreen && (this.brightGreen == null || !this.brightGreen.equals(other.brightGreen))) {
            return false;
        }
        if(this.normalYellow != other.normalYellow && (this.normalYellow == null || !this.normalYellow.equals(other.normalYellow))) {
            return false;
        }
        if(this.brightYellow != other.brightYellow && (this.brightYellow == null || !this.brightYellow.equals(other.brightYellow))) {
            return false;
        }
        if(this.normalBlue != other.normalBlue && (this.normalBlue == null || !this.normalBlue.equals(other.normalBlue))) {
            return false;
        }
        if(this.brightBlue != other.brightBlue && (this.brightBlue == null || !this.brightBlue.equals(other.brightBlue))) {
            return false;
        }
        if(this.normalMagenta != other.normalMagenta && (this.normalMagenta == null || !this.normalMagenta.equals(other.normalMagenta))) {
            return false;
        }
        if(this.brightMagenta != other.brightMagenta && (this.brightMagenta == null || !this.brightMagenta.equals(other.brightMagenta))) {
            return false;
        }
        if(this.normalCyan != other.normalCyan && (this.normalCyan == null || !this.normalCyan.equals(other.normalCyan))) {
            return false;
        }
        if(this.brightCyan != other.brightCyan && (this.brightCyan == null || !this.brightCyan.equals(other.brightCyan))) {
            return false;
        }
        if(this.normalWhite != other.normalWhite && (this.normalWhite == null || !this.normalWhite.equals(other.normalWhite))) {
            return false;
        }
        return !(this.brightWhite != other.brightWhite && (this.brightWhite == null || !this.brightWhite.equals(other.brightWhite)));
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.defaultColor != null ? this.defaultColor.hashCode() : 0);
        hash = 47 * hash + (this.defaultBrightColor != null ? this.defaultBrightColor.hashCode() : 0);
        hash = 47 * hash + (this.defaultBackgroundColor != null ? this.defaultBackgroundColor.hashCode() : 0);
        hash = 47 * hash + (this.normalBlack != null ? this.normalBlack.hashCode() : 0);
        hash = 47 * hash + (this.brightBlack != null ? this.brightBlack.hashCode() : 0);
        hash = 47 * hash + (this.normalRed != null ? this.normalRed.hashCode() : 0);
        hash = 47 * hash + (this.brightRed != null ? this.brightRed.hashCode() : 0);
        hash = 47 * hash + (this.normalGreen != null ? this.normalGreen.hashCode() : 0);
        hash = 47 * hash + (this.brightGreen != null ? this.brightGreen.hashCode() : 0);
        hash = 47 * hash + (this.normalYellow != null ? this.normalYellow.hashCode() : 0);
        hash = 47 * hash + (this.brightYellow != null ? this.brightYellow.hashCode() : 0);
        hash = 47 * hash + (this.normalBlue != null ? this.normalBlue.hashCode() : 0);
        hash = 47 * hash + (this.brightBlue != null ? this.brightBlue.hashCode() : 0);
        hash = 47 * hash + (this.normalMagenta != null ? this.normalMagenta.hashCode() : 0);
        hash = 47 * hash + (this.brightMagenta != null ? this.brightMagenta.hashCode() : 0);
        hash = 47 * hash + (this.normalCyan != null ? this.normalCyan.hashCode() : 0);
        hash = 47 * hash + (this.brightCyan != null ? this.brightCyan.hashCode() : 0);
        hash = 47 * hash + (this.normalWhite != null ? this.normalWhite.hashCode() : 0);
        hash = 47 * hash + (this.brightWhite != null ? this.brightWhite.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "SwingTerminalPalette{" +
                "defaultColor=" + defaultColor +
                ", defaultBrightColor=" + defaultBrightColor +
                ", defaultBackgroundColor=" + defaultBackgroundColor +
                ", normalBlack=" + normalBlack +
                ", brightBlack=" + brightBlack +
                ", normalRed=" + normalRed +
                ", brightRed=" + brightRed +
                ", normalGreen=" + normalGreen +
                ", brightGreen=" + brightGreen +
                ", normalYellow=" + normalYellow +
                ", brightYellow=" + brightYellow +
                ", normalBlue=" + normalBlue +
                ", brightBlue=" + brightBlue +
                ", normalMagenta=" + normalMagenta +
                ", brightMagenta=" + brightMagenta +
                ", normalCyan=" + normalCyan +
                ", brightCyan=" + brightCyan +
                ", normalWhite=" + normalWhite +
                ", brightWhite=" + brightWhite + '}';
    }
}
