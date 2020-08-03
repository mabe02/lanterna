/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
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
 * Copyright (C) 2010-2020 Martin Berglund
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.bundle.*;
import com.googlecode.lanterna.graphics.*;

/**
 * 
 * @author ginkoblongata
 */
public class SplitPanelTest extends TestBase {
    public static void main(String[] args) throws Exception {
        new SplitPanelTest().run(args);
    }
    
    static String[] IMAGE_X = new String[] {
        "-=================================-",
        "xx                               xx",
        "xx  X                         X  xx",
        "xx                               xx",
        "xx     XXXXXXX       XXXXXXX     xx",
        "xx     X:::::X       X:::::X     xx",
        "xx     X:::::X       X:::::X     xx",
        "xx     X::::::X     X::::::X     xx",
        "xx     XXX:::::X   X:::::XXX     xx",
        "xx        X:::::X X:::::X        xx",
        "xx         X:::::X:::::X         xx",
        "xx          X:::::::::X          xx",
        "xx          X:::::::::X          xx",
        "xx         X:::::X:::::X         xx",
        "xx        X:::::X X:::::X        xx",
        "xx     XXX:::::X   X:::::XXX     xx",
        "xx     X::::::X     X::::::X     xx",
        "xx     X:::::X       X:::::X     xx",
        "xx     X:::::X       X:::::X     xx",
        "xx     XXXXXXX       XXXXXXX     xx",
        "xx                               xx",
        "xx  X                         X  xx",
        "xx                               xx",
        "-=================================-"
    };
    
    static String[] IMAGE_Y = new String[] {
        "-=================================-",
        "xx                               xx",
        "xx  X                         X  xx",
        "xx                               xx",
        "xx     YYYYYYY       YYYYYYY     xx",
        "xx     Y:::::Y       Y:::::Y     xx",
        "xx     Y:::::Y       Y:::::Y     xx",
        "xx     Y::::::Y     Y::::::Y     xx",
        "xx     YYY:::::Y   Y:::::YYY     xx",
        "xx        Y:::::Y Y:::::Y        xx",
        "xx         Y:::::Y:::::Y         xx",
        "xx          Y:::::::::Y          xx",
        "xx           Y:::::::Y           xx",
        "xx            Y:::::Y            xx",
        "xx            Y:::::Y            xx",
        "xx            Y:::::Y            xx",
        "xx            Y:::::Y            xx",
        "xx         YYYY:::::YYYY         xx",
        "xx         Y:::::::::::Y         xx",
        "xx         YYYYYYYYYYYYY         xx",
        "xx                               xx",
        "xx  X                         X  xx",
        "xx                               xx",
        "-=================================-"
    };

    
    static String[] IMAGE_Z = new String[] {
        "-=================================-",
        "xx                               xx",
        "xx  X                         X  xx",
        "xx                               xx",
        "xx     ZZZZZZZZZZZZZZZZZZZ       xx",
        "xx     Z:::::::::::::::::Z       xx",
        "xx     Z:::::::::::::::::Z       xx",
        "xx     Z:::ZZZZZZZZ:::::Z        xx",
        "xx     ZZZZZ     Z:::::Z         xx",
        "xx             Z:::::Z           xx",
        "xx            Z:::::Z            xx",
        "xx           Z:::::Z             xx",
        "xx          Z:::::Z              xx",
        "xx         Z:::::Z               xx",
        "xx        Z:::::Z                xx",
        "xx     ZZZ:::::Z     ZZZZZ       xx",
        "xx     Z::::::ZZZZZZZZ:::Z       xx",
        "xx     Z:::::::::::::::::Z       xx",
        "xx     Z:::::::::::::::::Z       xx",
        "xx     ZZZZZZZZZZZZZZZZZZZ       xx",
        "xx                               xx",
        "xx  X                         X  xx",
        "xx                               xx",
        "-=================================-"
    };
    
    @Override
    public void init(WindowBasedTextGUI textGUI) {
        final BasicWindow window = new BasicWindow("SplitPanelTest");
        window.setTheme(LanternaThemes.getRegisteredTheme("businessmachine"));

        ImageComponent left = makeImageComponent(IMAGE_X);
        ImageComponent right = makeImageComponent(IMAGE_Y);
        //SplitPanel splitH = SplitPanel.ofHorizontal(left.withBorder(Borders.singleLine("left")), right.withBorder(Borders.singleLine("right")));
        SplitPanel splitH = SplitPanel.ofHorizontal(left, right);
        splitH.setPreferredSize(new TerminalSize(40, 40));
        splitH.setRatio(45, 35);
        
        ImageComponent top = makeImageComponent(IMAGE_Y);
        ImageComponent bottom = makeImageComponent(IMAGE_Z);
        //SplitPanel splitV = SplitPanel.ofVertical(top.withBorder(Borders.singleLine("top")), bottom.withBorder(Borders.singleLine("bottom")));
        SplitPanel splitV = SplitPanel.ofVertical(top, bottom);
        splitV.setPreferredSize(new TerminalSize(40, 40));
        splitV.setRatio(20, 80);
        
        Panel mainPanel = new Panel();
        mainPanel.setLayoutManager(new GridLayout(2));
        SplitPanel splitboth = SplitPanel.ofHorizontal(splitH.withBorder(Borders.singleLine("horiontal split")), splitV.withBorder(Borders.singleLine("vertical split")));
        mainPanel.addComponent(splitboth);
        
        window.setComponent(mainPanel);
        textGUI.addWindow(window);
    }
    
    ImageComponent makeImageComponent(String[] image) {
        ImageComponent imageComponent = new ImageComponent();
        TerminalSize imageSize = new TerminalSize(image[0].length(), image.length);
        TextImage textImage = new BasicTextImage(imageSize);
        
        for (int row = 0; row < image.length; row++) {
            fillImageLine(textImage, row, image[row]);
        }
        
        imageComponent.setTextImage(textImage);
        return imageComponent;
    }
    
    void fillImageLine(TextImage textImage, int row, String line) {
        for (int x = 0; x < line.length(); x++) {
            char c = line.charAt(x);
            TextCharacter textCharacter = new TextCharacter(c);
            textImage.setCharacterAt(x, row, textCharacter);
        }
    }
}

