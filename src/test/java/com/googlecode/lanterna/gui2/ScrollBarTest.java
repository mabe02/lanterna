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
 * Copyright (C) 2010-2016 Martin
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Created by martin on 08/08/15.
 */
public class ScrollBarTest extends TestBase {
    public static void main(String[] args) throws IOException, InterruptedException {
        new ScrollBarTest().run(args);
    }

    @Override
    public void init(WindowBasedTextGUI textGUI) {
        final BasicWindow basicWindow = new BasicWindow("ScrollBar test");
        Panel contentPanel = new Panel();
        contentPanel.setLayoutManager(new GridLayout(2));

        Panel controlPanel = new Panel();
        final CheckBox checkVerticalTrackerGrow = new CheckBox().setChecked(true);
        final CheckBox checkHorizontalTrackerGrow = new CheckBox().setChecked(true);
        final TextBox textBoxVerticalSize = new TextBox("10").setValidationPattern(Pattern.compile("[0-9]+"));
        final TextBox textBoxHorizontalSize = new TextBox("10").setValidationPattern(Pattern.compile("[0-9]+"));
        final TextBox textBoxVerticalPosition = new TextBox("0").setValidationPattern(Pattern.compile("[0-9]+"));
        final TextBox textBoxHorizontalPosition = new TextBox("0").setValidationPattern(Pattern.compile("[0-9]+"));
        final TextBox textBoxVerticalMax = new TextBox("100").setValidationPattern(Pattern.compile("[0-9]+"));
        final TextBox textBoxHorizontalMax = new TextBox("100").setValidationPattern(Pattern.compile("[0-9]+"));
        final ScrollBar verticalScroll = new ScrollBar(Direction.VERTICAL);
        final ScrollBar horizontalScroll = new ScrollBar(Direction.HORIZONTAL);
        Button buttonRefresh = new Button("Refresh", new Runnable() {
            @Override
            public void run() {
                ((ScrollBar.DefaultScrollBarRenderer)verticalScroll.getRenderer()).setGrowScrollTracker(checkVerticalTrackerGrow.isChecked());
                verticalScroll.setScrollMaximum(getInteger(textBoxVerticalMax.getText(), 100));
                verticalScroll.setScrollPosition(getInteger(textBoxVerticalPosition.getText(), 100));
                verticalScroll.setViewSize(getInteger(textBoxVerticalSize.getText(), 1));
                ((ScrollBar.DefaultScrollBarRenderer)horizontalScroll.getRenderer()).setGrowScrollTracker(checkHorizontalTrackerGrow.isChecked());
                horizontalScroll.setScrollMaximum(getInteger(textBoxHorizontalMax.getText(), 0));
                horizontalScroll.setScrollPosition(getInteger(textBoxHorizontalPosition.getText(), 0));
                horizontalScroll.setViewSize(getInteger(textBoxHorizontalSize.getText(), 1));
            }
        });
        Button closeButton = new Button("Close", new Runnable() {
            @Override
            public void run() {
                basicWindow.close();
            }
        });

        verticalScroll.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.FILL, false, true));
        horizontalScroll.setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2));
        buttonRefresh.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.BEGINNING, true, true, 2, 1));

        contentPanel.addComponent(controlPanel.withBorder(Borders.singleLine("Control")));
        contentPanel.addComponent(verticalScroll);
        contentPanel.addComponent(horizontalScroll);

        controlPanel.setLayoutManager(new GridLayout(2));
        controlPanel.addComponent(new Label("Vertical tracker grows:")).addComponent(checkVerticalTrackerGrow);
        controlPanel.addComponent(new Label("Vertical view size:")).addComponent(textBoxVerticalSize);
        controlPanel.addComponent(new Label("Vertical scroll position:")).addComponent(textBoxVerticalPosition);
        controlPanel.addComponent(new Label("Vertical scroll max:")).addComponent(textBoxVerticalMax);
        controlPanel.addComponent(new EmptySpace(TerminalSize.ONE)).addComponent(new EmptySpace(TerminalSize.ONE));
        controlPanel.addComponent(new Label("Horizontal tracker grows:")).addComponent(checkHorizontalTrackerGrow);
        controlPanel.addComponent(new Label("Horizontal view size:")).addComponent(textBoxHorizontalSize);
        controlPanel.addComponent(new Label("Horizontal scroll position:")).addComponent(textBoxHorizontalPosition);
        controlPanel.addComponent(new Label("Horizontal scroll max:")).addComponent(textBoxHorizontalMax);
        controlPanel.addComponent(new EmptySpace(TerminalSize.ONE)).addComponent(new EmptySpace(TerminalSize.ONE));
        controlPanel.addComponent(buttonRefresh);
        contentPanel.addComponent(closeButton);

        basicWindow.setComponent(contentPanel);
        textGUI.addWindow(basicWindow);
    }

    private int getInteger(String text, int defaultValue) {
        try {
            return Integer.parseInt(text);
        }
        catch(NumberFormatException e) {
            return defaultValue;
        }
    }
}
