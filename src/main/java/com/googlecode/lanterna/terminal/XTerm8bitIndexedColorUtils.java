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
 * Copyright (C) 2010-2012 Martin
 */
package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.terminal.swing.TerminalPalette;
import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * This class can help you convert to and from the 8-bit indexed color standard that is supported
 * by some terminal emulators. For details on how this works, please see 
 * <a href="https://github.com/robertknight/konsole/blob/master/user-doc/README.moreColors">this</a>
 * commit log message.
 * @author Martin
 */
public class XTerm8bitIndexedColorUtils {
    
    private XTerm8bitIndexedColorUtils() {}
    
    /**
     * I didn't make this myself, it is from http://www.vim.org/scripts/script.php?script_id=1349
     */
    private static final Collection<Entry> colorEntries = Collections.unmodifiableList(
            Arrays.asList(
            //These are the standard 16-color VGA palette entries
            new Entry(0, 0, 0, 0),
            new Entry(1, 170, 0, 0),
            new Entry(2, 0, 170, 0),
            new Entry(3, 170, 85, 0),
            new Entry(4, 0, 0, 170),
            new Entry(5, 170, 0, 170),
            new Entry(6, 0, 170, 170),
            new Entry(7, 170, 170, 170),
            new Entry(8, 85, 85, 85),
            new Entry(9, 255, 85, 85),
            new Entry(10, 85, 255, 85),
            new Entry(11, 255, 255, 85),
            new Entry(12, 85, 85, 255),
            new Entry(13, 255, 85, 255),
            new Entry(14, 85, 255, 255),
            new Entry(15, 255, 255, 255),  
            
            //Starting indexed colors from 16
            new Entry(16, 0x00, 0x00, 0x00),
            new Entry(17, 0x00, 0x00, 0x5f),
            new Entry(18, 0x00, 0x00, 0x87),
            new Entry(19, 0x00, 0x00, 0xaf),
            new Entry(20, 0x00, 0x00, 0xd7),
            new Entry(21, 0x00, 0x00, 0xff),
            new Entry(22, 0x00, 0x5f, 0x00),
            new Entry(23, 0x00, 0x5f, 0x5f),
            new Entry(24, 0x00, 0x5f, 0x87),
            new Entry(25, 0x00, 0x5f, 0xaf),
            new Entry(26, 0x00, 0x5f, 0xd7),
            new Entry(27, 0x00, 0x5f, 0xff),
            new Entry(28, 0x00, 0x87, 0x00),
            new Entry(29, 0x00, 0x87, 0x5f),
            new Entry(30, 0x00, 0x87, 0x87),
            new Entry(31, 0x00, 0x87, 0xaf),
            new Entry(32, 0x00, 0x87, 0xd7),
            new Entry(33, 0x00, 0x87, 0xff),
            new Entry(34, 0x00, 0xaf, 0x00),
            new Entry(35, 0x00, 0xaf, 0x5f),
            new Entry(36, 0x00, 0xaf, 0x87),
            new Entry(37, 0x00, 0xaf, 0xaf),
            new Entry(38, 0x00, 0xaf, 0xd7),
            new Entry(39, 0x00, 0xaf, 0xff),
            new Entry(40, 0x00, 0xd7, 0x00),
            new Entry(41, 0x00, 0xd7, 0x5f),
            new Entry(42, 0x00, 0xd7, 0x87),
            new Entry(43, 0x00, 0xd7, 0xaf),
            new Entry(44, 0x00, 0xd7, 0xd7),
            new Entry(45, 0x00, 0xd7, 0xff),
            new Entry(46, 0x00, 0xff, 0x00),
            new Entry(47, 0x00, 0xff, 0x5f),
            new Entry(48, 0x00, 0xff, 0x87),
            new Entry(49, 0x00, 0xff, 0xaf),
            new Entry(50, 0x00, 0xff, 0xd7),
            new Entry(51, 0x00, 0xff, 0xff),
            new Entry(52, 0x5f, 0x00, 0x00),
            new Entry(53, 0x5f, 0x00, 0x5f),
            new Entry(54, 0x5f, 0x00, 0x87),
            new Entry(55, 0x5f, 0x00, 0xaf),
            new Entry(56, 0x5f, 0x00, 0xd7),
            new Entry(57, 0x5f, 0x00, 0xff),
            new Entry(58, 0x5f, 0x5f, 0x00),
            new Entry(59, 0x5f, 0x5f, 0x5f),
            new Entry(60, 0x5f, 0x5f, 0x87),
            new Entry(61, 0x5f, 0x5f, 0xaf),
            new Entry(62, 0x5f, 0x5f, 0xd7),
            new Entry(63, 0x5f, 0x5f, 0xff),
            new Entry(64, 0x5f, 0x87, 0x00),
            new Entry(65, 0x5f, 0x87, 0x5f),
            new Entry(66, 0x5f, 0x87, 0x87),
            new Entry(67, 0x5f, 0x87, 0xaf),
            new Entry(68, 0x5f, 0x87, 0xd7),
            new Entry(69, 0x5f, 0x87, 0xff),
            new Entry(70, 0x5f, 0xaf, 0x00),
            new Entry(71, 0x5f, 0xaf, 0x5f),
            new Entry(72, 0x5f, 0xaf, 0x87),
            new Entry(73, 0x5f, 0xaf, 0xaf),
            new Entry(74, 0x5f, 0xaf, 0xd7),
            new Entry(75, 0x5f, 0xaf, 0xff),
            new Entry(76, 0x5f, 0xd7, 0x00),
            new Entry(77, 0x5f, 0xd7, 0x5f),
            new Entry(78, 0x5f, 0xd7, 0x87),
            new Entry(79, 0x5f, 0xd7, 0xaf),
            new Entry(80, 0x5f, 0xd7, 0xd7),
            new Entry(81, 0x5f, 0xd7, 0xff),
            new Entry(82, 0x5f, 0xff, 0x00),
            new Entry(83, 0x5f, 0xff, 0x5f),
            new Entry(84, 0x5f, 0xff, 0x87),
            new Entry(85, 0x5f, 0xff, 0xaf),
            new Entry(86, 0x5f, 0xff, 0xd7),
            new Entry(87, 0x5f, 0xff, 0xff),
            new Entry(88, 0x87, 0x00, 0x00),
            new Entry(89, 0x87, 0x00, 0x5f),
            new Entry(90, 0x87, 0x00, 0x87),
            new Entry(91, 0x87, 0x00, 0xaf),
            new Entry(92, 0x87, 0x00, 0xd7),
            new Entry(93, 0x87, 0x00, 0xff),
            new Entry(94, 0x87, 0x5f, 0x00),
            new Entry(95, 0x87, 0x5f, 0x5f),
            new Entry(96, 0x87, 0x5f, 0x87),
            new Entry(97, 0x87, 0x5f, 0xaf),
            new Entry(98, 0x87, 0x5f, 0xd7),
            new Entry(99, 0x87, 0x5f, 0xff),
            new Entry(100, 0x87, 0x87, 0x00),
            new Entry(101, 0x87, 0x87, 0x5f),
            new Entry(102, 0x87, 0x87, 0x87),
            new Entry(103, 0x87, 0x87, 0xaf),
            new Entry(104, 0x87, 0x87, 0xd7),
            new Entry(105, 0x87, 0x87, 0xff),
            new Entry(106, 0x87, 0xaf, 0x00),
            new Entry(107, 0x87, 0xaf, 0x5f),
            new Entry(108, 0x87, 0xaf, 0x87),
            new Entry(109, 0x87, 0xaf, 0xaf),
            new Entry(110, 0x87, 0xaf, 0xd7),
            new Entry(111, 0x87, 0xaf, 0xff),
            new Entry(112, 0x87, 0xd7, 0x00),
            new Entry(113, 0x87, 0xd7, 0x5f),
            new Entry(114, 0x87, 0xd7, 0x87),
            new Entry(115, 0x87, 0xd7, 0xaf),
            new Entry(116, 0x87, 0xd7, 0xd7),
            new Entry(117, 0x87, 0xd7, 0xff),
            new Entry(118, 0x87, 0xff, 0x00),
            new Entry(119, 0x87, 0xff, 0x5f),
            new Entry(120, 0x87, 0xff, 0x87),
            new Entry(121, 0x87, 0xff, 0xaf),
            new Entry(122, 0x87, 0xff, 0xd7),
            new Entry(123, 0x87, 0xff, 0xff),
            new Entry(124, 0xaf, 0x00, 0x00),
            new Entry(125, 0xaf, 0x00, 0x5f),
            new Entry(126, 0xaf, 0x00, 0x87),
            new Entry(127, 0xaf, 0x00, 0xaf),
            new Entry(128, 0xaf, 0x00, 0xd7),
            new Entry(129, 0xaf, 0x00, 0xff),
            new Entry(130, 0xaf, 0x5f, 0x00),
            new Entry(131, 0xaf, 0x5f, 0x5f),
            new Entry(132, 0xaf, 0x5f, 0x87),
            new Entry(133, 0xaf, 0x5f, 0xaf),
            new Entry(134, 0xaf, 0x5f, 0xd7),
            new Entry(135, 0xaf, 0x5f, 0xff),
            new Entry(136, 0xaf, 0x87, 0x00),
            new Entry(137, 0xaf, 0x87, 0x5f),
            new Entry(138, 0xaf, 0x87, 0x87),
            new Entry(139, 0xaf, 0x87, 0xaf),
            new Entry(140, 0xaf, 0x87, 0xd7),
            new Entry(141, 0xaf, 0x87, 0xff),
            new Entry(142, 0xaf, 0xaf, 0x00),
            new Entry(143, 0xaf, 0xaf, 0x5f),
            new Entry(144, 0xaf, 0xaf, 0x87),
            new Entry(145, 0xaf, 0xaf, 0xaf),
            new Entry(146, 0xaf, 0xaf, 0xd7),
            new Entry(147, 0xaf, 0xaf, 0xff),
            new Entry(148, 0xaf, 0xd7, 0x00),
            new Entry(149, 0xaf, 0xd7, 0x5f),
            new Entry(150, 0xaf, 0xd7, 0x87),
            new Entry(151, 0xaf, 0xd7, 0xaf),
            new Entry(152, 0xaf, 0xd7, 0xd7),
            new Entry(153, 0xaf, 0xd7, 0xff),
            new Entry(154, 0xaf, 0xff, 0x00),
            new Entry(155, 0xaf, 0xff, 0x5f),
            new Entry(156, 0xaf, 0xff, 0x87),
            new Entry(157, 0xaf, 0xff, 0xaf),
            new Entry(158, 0xaf, 0xff, 0xd7),
            new Entry(159, 0xaf, 0xff, 0xff),
            new Entry(160, 0xd7, 0x00, 0x00),
            new Entry(161, 0xd7, 0x00, 0x5f),
            new Entry(162, 0xd7, 0x00, 0x87),
            new Entry(163, 0xd7, 0x00, 0xaf),
            new Entry(164, 0xd7, 0x00, 0xd7),
            new Entry(165, 0xd7, 0x00, 0xff),
            new Entry(166, 0xd7, 0x5f, 0x00),
            new Entry(167, 0xd7, 0x5f, 0x5f),
            new Entry(168, 0xd7, 0x5f, 0x87),
            new Entry(169, 0xd7, 0x5f, 0xaf),
            new Entry(170, 0xd7, 0x5f, 0xd7),
            new Entry(171, 0xd7, 0x5f, 0xff),
            new Entry(172, 0xd7, 0x87, 0x00),
            new Entry(173, 0xd7, 0x87, 0x5f),
            new Entry(174, 0xd7, 0x87, 0x87),
            new Entry(175, 0xd7, 0x87, 0xaf),
            new Entry(176, 0xd7, 0x87, 0xd7),
            new Entry(177, 0xd7, 0x87, 0xff),
            new Entry(178, 0xd7, 0xaf, 0x00),
            new Entry(179, 0xd7, 0xaf, 0x5f),
            new Entry(180, 0xd7, 0xaf, 0x87),
            new Entry(181, 0xd7, 0xaf, 0xaf),
            new Entry(182, 0xd7, 0xaf, 0xd7),
            new Entry(183, 0xd7, 0xaf, 0xff),
            new Entry(184, 0xd7, 0xd7, 0x00),
            new Entry(185, 0xd7, 0xd7, 0x5f),
            new Entry(186, 0xd7, 0xd7, 0x87),
            new Entry(187, 0xd7, 0xd7, 0xaf),
            new Entry(188, 0xd7, 0xd7, 0xd7),
            new Entry(189, 0xd7, 0xd7, 0xff),
            new Entry(190, 0xd7, 0xff, 0x00),
            new Entry(191, 0xd7, 0xff, 0x5f),
            new Entry(192, 0xd7, 0xff, 0x87),
            new Entry(193, 0xd7, 0xff, 0xaf),
            new Entry(194, 0xd7, 0xff, 0xd7),
            new Entry(195, 0xd7, 0xff, 0xff),
            new Entry(196, 0xff, 0x00, 0x00),
            new Entry(197, 0xff, 0x00, 0x5f),
            new Entry(198, 0xff, 0x00, 0x87),
            new Entry(199, 0xff, 0x00, 0xaf),
            new Entry(200, 0xff, 0x00, 0xd7),
            new Entry(201, 0xff, 0x00, 0xff),
            new Entry(202, 0xff, 0x5f, 0x00),
            new Entry(203, 0xff, 0x5f, 0x5f),
            new Entry(204, 0xff, 0x5f, 0x87),
            new Entry(205, 0xff, 0x5f, 0xaf),
            new Entry(206, 0xff, 0x5f, 0xd7),
            new Entry(207, 0xff, 0x5f, 0xff),
            new Entry(208, 0xff, 0x87, 0x00),
            new Entry(209, 0xff, 0x87, 0x5f),
            new Entry(210, 0xff, 0x87, 0x87),
            new Entry(211, 0xff, 0x87, 0xaf),
            new Entry(212, 0xff, 0x87, 0xd7),
            new Entry(213, 0xff, 0x87, 0xff),
            new Entry(214, 0xff, 0xaf, 0x00),
            new Entry(215, 0xff, 0xaf, 0x5f),
            new Entry(216, 0xff, 0xaf, 0x87),
            new Entry(217, 0xff, 0xaf, 0xaf),
            new Entry(218, 0xff, 0xaf, 0xd7),
            new Entry(219, 0xff, 0xaf, 0xff),
            new Entry(220, 0xff, 0xd7, 0x00),
            new Entry(221, 0xff, 0xd7, 0x5f),
            new Entry(222, 0xff, 0xd7, 0x87),
            new Entry(223, 0xff, 0xd7, 0xaf),
            new Entry(224, 0xff, 0xd7, 0xd7),
            new Entry(225, 0xff, 0xd7, 0xff),
            new Entry(226, 0xff, 0xff, 0x00),
            new Entry(227, 0xff, 0xff, 0x5f),
            new Entry(228, 0xff, 0xff, 0x87),
            new Entry(229, 0xff, 0xff, 0xaf),
            new Entry(230, 0xff, 0xff, 0xd7),
            new Entry(231, 0xff, 0xff, 0xff),
            new Entry(232, 0x08, 0x08, 0x08),
            new Entry(233, 0x12, 0x12, 0x12),
            new Entry(234, 0x1c, 0x1c, 0x1c),
            new Entry(235, 0x26, 0x26, 0x26),
            new Entry(236, 0x30, 0x30, 0x30),
            new Entry(237, 0x3a, 0x3a, 0x3a),
            new Entry(238, 0x44, 0x44, 0x44),
            new Entry(239, 0x4e, 0x4e, 0x4e),
            new Entry(240, 0x58, 0x58, 0x58),
            new Entry(241, 0x62, 0x62, 0x62),
            new Entry(242, 0x6c, 0x6c, 0x6c),
            new Entry(243, 0x76, 0x76, 0x76),
            new Entry(244, 0x80, 0x80, 0x80),
            new Entry(245, 0x8a, 0x8a, 0x8a),
            new Entry(246, 0x94, 0x94, 0x94),
            new Entry(247, 0x9e, 0x9e, 0x9e),
            new Entry(248, 0xa8, 0xa8, 0xa8),
            new Entry(249, 0xb2, 0xb2, 0xb2),
            new Entry(250, 0xbc, 0xbc, 0xbc),
            new Entry(251, 0xc6, 0xc6, 0xc6),
            new Entry(252, 0xd0, 0xd0, 0xd0),
            new Entry(253, 0xda, 0xda, 0xda),
            new Entry(254, 0xe4, 0xe4, 0xe4),
            new Entry(255, 0xee, 0xee, 0xee)));
    
    /**
     * Given a RGB value, finds the closest color from the 8-bit palette. The calculation is done
     * using distance in three dimensional space so it's not actually 100% accurate (color 
     * similarity is a whole research area of its own) but it's a good approximation.
     * @param red Red component value (0-255)
     * @param green Green component value (0-255)
     * @param blue Blue component value (0-255)
     * @return Index of the closest color
     */
    public static int getClosestColor(int red, int green, int blue) {
        if(red < 0 || red > 255)
            throw new IllegalArgumentException("getClosestColor: red is outside of valid range (0-255)");
        if(green < 0 || green > 255)
            throw new IllegalArgumentException("getClosestColor: green is outside of valid range (0-255)");
        if(blue < 0 || blue > 255)
            throw new IllegalArgumentException("getClosestColor: blue is outside of valid range (0-255)");
        
        double closestMatch = Double.MAX_VALUE;
        int closestIndex = 0;
        for(Entry entry : colorEntries) {
            double distance = entry.distanceTo(red, green, blue);
            if(distance < closestMatch) {
                closestIndex = entry.index;
                closestMatch = distance;
            }
        }
        return closestIndex;
    }
    
    /**
     * Returns the supplied color index as a AWT Color object
     * @param index Index of the color (0-255)
     * @return AWT Color object representing the color
     */
    public static Color getAWTColor(int index) {
        return getAWTColor(index, null);
    }
    
    /**
     * Returns the supplied color index as a AWT Color object
     * @param index Index of the color (0-255)
     * @param terminalPalette Palette to use for the initial 16 color values (index 0-15)
     * @return AWT Color object representing the color
     */
    public static Color getAWTColor(int index, TerminalPalette terminalPalette) {
        if(index < 0 || index > 255)
            throw new IllegalArgumentException("getClosestColor: red is outside of valid range (0-255)");
        
        if(terminalPalette != null && index < 16) {
            switch(index) {
                case 0: return terminalPalette.getNormalBlack();
                case 1: return terminalPalette.getNormalRed();
                case 2: return terminalPalette.getNormalGreen();
                case 3: return terminalPalette.getNormalYellow();
                case 4: return terminalPalette.getNormalBlue();
                case 5: return terminalPalette.getNormalMagenta();
                case 6: return terminalPalette.getNormalCyan();
                case 7: return terminalPalette.getNormalWhite();
                case 8: return terminalPalette.getBrightBlack();
                case 9: return terminalPalette.getBrightRed();
                case 10: return terminalPalette.getBrightGreen();
                case 11: return terminalPalette.getBrightYellow();
                case 12: return terminalPalette.getBrightBlue();
                case 13: return terminalPalette.getBrightMagenta();
                case 14: return terminalPalette.getBrightCyan();
                case 15: return terminalPalette.getBrightWhite();
            }
        }
        
        for(Entry entry: colorEntries) {
            if(entry.index == index) {
                return new Color(entry.red, entry.green, entry.blue);
            }
        }
        throw new IllegalArgumentException("getClosestColor: couldn't find color for index " + index);
    }

    private static class Entry {
        int index;
        int red;
        int green;
        int blue;

        Entry(int index, int red, int green, int blue) {
            this.index = index;
            this.red = red;
            this.green = green;
            this.blue = blue;
        }
        
        double distanceTo(int red, int green, int blue) {
            return Math.pow(this.red - red, 2) +
                    Math.pow(this.green - green, 2) + 
                    Math.pow(this.blue - blue, 2);
        }
    }
}
