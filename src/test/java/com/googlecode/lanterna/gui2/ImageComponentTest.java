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
import com.googlecode.lanterna.input.*;

public class ImageComponentTest extends TestBase {
    public static void main(String[] args) throws Exception {
        new ImageComponentTest().run(args);
    }
    
    static final class ExampleController {
        ImageComponent selectedImageComponent;
        public void setSelectedImage(TextImage image) {
            selectedImageComponent.setTextImage(image);
        }
    }
    
    static String[] IMAGE = new String[] {
        "-====================================================-",
        "xx                                                  xx",
        "xx  X                                            X  xx",
        "xx                                                  xx",
        "xx    .d8b.  d8888b.  .o88b.                        xx",
        "xx   d8' `8b 88  `8D d8P  Y8                        xx",
        "xx   88ooo88 88oooY' 8P            asdfasdf         xx",
        "xx   88~~~88 88~~~b. 8b                             xx",
        "xx   88   88 88   8D Y8b  d8              1234      xx",
        "xx   YP   YP Y8888P'  `Y88P'                        xx",
        "xx                                 asdfasdf         xx",
        "xx                                                  xx",
        "xx   db    db db    db d88888D                      xx",
        "xx   `8b  d8' `8b  d8' YP  d8'                      xx",
        "xx    `8bd8'   `8bd8'     d8'          xxxxxxx      xx",
        "xx    .dPYb.     88      d8'           x     x      xx",
        "xx   .8P  Y8.    88     d8' db         x     x      xx",
        "xx   YP    YP    YP    d88888P         x     x      xx",
        "xx                                     xxxxxxx      xx",
        "xx  X                                            X  xx",
        "xx                                                  xx",
        "-====================================================-"
    };
    
    static String[] IMAGE_BLANK = new String[] {
        "-=================================-",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "-=================================-"
    };
    
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
        final BasicWindow window = new BasicWindow("ImageComponentTest");
        window.setTheme(LanternaThemes.getRegisteredTheme("conqueror"));

        ExampleController controller = new ExampleController();
        controller.selectedImageComponent = makeImageComponent(controller, IMAGE_BLANK);
        
        ImageComponent imageComponentX = makeImageComponent(controller, IMAGE_X);
        ImageComponent imageComponentY = makeImageComponent(controller, IMAGE_Y);
        ImageComponent imageComponentZ = makeImageComponent(controller, IMAGE_Z);
        
        
        
        Panel mainPanel = new Panel();
        mainPanel.setLayoutManager(new GridLayout(2));
        mainPanel.addComponent(imageComponentX.withBorder(Borders.singleLine("x")));
        mainPanel.addComponent(imageComponentY.withBorder(Borders.singleLine("y")));
        mainPanel.addComponent(imageComponentZ.withBorder(Borders.singleLine("z")));
        mainPanel.addComponent(controller.selectedImageComponent.withBorder(Borders.singleLine("selection")));
        
        
        window.setComponent(mainPanel);
        textGUI.addWindow(window);
    }
    
    
    ImageComponent makeImageComponent(ExampleController controller, String[] image) {
        TerminalSize imageSize = new TerminalSize(image[0].length(), image.length);
        TextImage textImage = new BasicTextImage(imageSize);
        for (int row = 0; row < image.length; row++) {
            fillImageLine(textImage, row, image[row]);
        }
        
        ImageComponent imageComponent = new ImageComponent() {
            @Override
            public Result handleKeyStroke(KeyStroke keyStroke) {
                if (isMouseDown(keyStroke)) {
                    controller.setSelectedImage(textImage);
                    return Result.HANDLED;
                }
                return super.handleKeyStroke(keyStroke);
            }
        };
        
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

