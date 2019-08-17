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

import java.io.IOException;

public class LinearLayoutTest extends TestBase {
    public static void main(String[] args) throws InterruptedException, IOException {
        new LinearLayoutTest().run(args);
    }

    @Override
    public void init(WindowBasedTextGUI textGUI) {
        final BasicWindow window = new BasicWindow("Linear layout test");
        final Panel mainPanel = new Panel();
        final Panel labelPanel = new Panel();
        final LinearLayout linearLayout = new LinearLayout(Direction.VERTICAL);
        linearLayout.setSpacing(1);
        labelPanel.setLayoutManager(linearLayout);

        for(int i = 0; i < 5; i++) {
            new Label("LABEL COMPONENT").addTo(labelPanel);
        }
        mainPanel.addComponent(labelPanel);

        new Separator(Direction.HORIZONTAL)
                .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill))
                .addTo(mainPanel);

        mainPanel.addComponent(Panels.horizontal(
                new Button("Add", () -> new Label("LABEL COMPONENT").addTo(labelPanel)),
                new Button("Spacing", () -> linearLayout.setSpacing(linearLayout.getSpacing() == 1 ? 0 : 1)),
                new Button("Close", window::close)
        ));

        window.setComponent(mainPanel);
        textGUI.addWindow(window);
    }
}
