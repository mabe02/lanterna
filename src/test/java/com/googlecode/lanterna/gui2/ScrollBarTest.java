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
        Panel contentPanel = new Panel();
        contentPanel.setLayoutManager(new GridLayout(2));

        Panel controlPanel = new Panel();
        final TextBox textBoxVerticalSize = new TextBox("10").setValidationPattern(Pattern.compile("[0-9]+"));
        final TextBox textBoxHorizontalSize = new TextBox("10").setValidationPattern(Pattern.compile("[0-9]+"));
        final TextBox textBoxVerticalPosition = new TextBox("0").setValidationPattern(Pattern.compile("[0-9]+"));
        final TextBox textBoxHorizontalPosition = new TextBox("0").setValidationPattern(Pattern.compile("[0-9]+"));
        final TextBox textBoxVerticalMax = new TextBox("100").setValidationPattern(Pattern.compile("[0-9]+"));
        final TextBox textBoxHorizontalMax = new TextBox("100").setValidationPattern(Pattern.compile("[0-9]+"));
        final ScrollBar verticalScroll = new ScrollBar(Direction.VERTICAL) {
            @Override
            public int getViewSize(TerminalSize componentSize) {
                return getInteger(textBoxVerticalSize.getText(), 1);
            }
        };
        final ScrollBar horizontalScroll = new ScrollBar(Direction.HORIZONTAL) {
            @Override
            public int getViewSize(TerminalSize componentSize) {
                return getInteger(textBoxHorizontalSize.getText(), 1);
            }
        };
        Button buttonRefresh = new Button("Refresh", new Runnable() {
            @Override
            public void run() {
                verticalScroll.setScrollMaximum(getInteger(textBoxVerticalMax.getText(), 100));
                verticalScroll.setScrollPosition(getInteger(textBoxVerticalPosition.getText(), 100));
                horizontalScroll.setScrollMaximum(getInteger(textBoxHorizontalMax.getText(), 0));
                horizontalScroll.setScrollPosition(getInteger(textBoxHorizontalPosition.getText(), 0));
            }
        });

        verticalScroll.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.FILL, false, true));
        horizontalScroll.setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2));
        buttonRefresh.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.BEGINNING, true, true, 2, 1));

        contentPanel.addComponent(controlPanel.withBorder(Borders.singleLine("Control")));
        contentPanel.addComponent(verticalScroll);
        contentPanel.addComponent(horizontalScroll);

        controlPanel.setLayoutManager(new GridLayout(2));
        controlPanel.addComponent(new Label("Vertical view size:")).addComponent(textBoxVerticalSize);
        controlPanel.addComponent(new Label("Vertical scroll position:")).addComponent(textBoxVerticalPosition);
        controlPanel.addComponent(new Label("Vertical scroll max:")).addComponent(textBoxVerticalMax);
        controlPanel.addComponent(new EmptySpace(TerminalSize.ONE)).addComponent(new EmptySpace(TerminalSize.ONE));
        controlPanel.addComponent(new Label("Horizontal view size:")).addComponent(textBoxHorizontalSize);
        controlPanel.addComponent(new Label("Horizontal scroll position:")).addComponent(textBoxHorizontalPosition);
        controlPanel.addComponent(new Label("Horizontal scroll max:")).addComponent(textBoxHorizontalMax);
        controlPanel.addComponent(new EmptySpace(TerminalSize.ONE)).addComponent(new EmptySpace(TerminalSize.ONE));
        controlPanel.addComponent(buttonRefresh);

        BasicWindow basicWindow = new BasicWindow("ScrollBar test");
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
