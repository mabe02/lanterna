package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialogBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Created by martin on 05/06/15.
 */
public class DynamicGridLayoutTest extends TestBase {
    public static void main(String[] args) throws IOException, InterruptedException {
        new DynamicGridLayoutTest().run(args);
    }

    private static final TextColor[] GOOD_COLORS = new TextColor[] {
            TextColor.ANSI.RED, TextColor.ANSI.BLUE, TextColor.ANSI.CYAN,
            TextColor.ANSI.GREEN, TextColor.ANSI.MAGENTA, TextColor.ANSI.YELLOW
    };
    private static final Random RANDOM = new Random();

    @Override
    public void init(final WindowBasedTextGUI textGUI) {
        final BasicWindow window = new BasicWindow("Grid layout test");

        Panel mainPanel = new Panel();
        mainPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL).setSpacing(1));

        final Panel gridPanel = new Panel();
        GridLayout gridLayout = newGridLayout(4);
        gridPanel.setLayoutManager(gridLayout);

        for(int i = 0; i < 16; i++) {
            gridPanel.addComponent(new EmptySpace(getRandomColor(), new TerminalSize(4, 1)));
        }

        Panel controlPanel = new Panel();
        controlPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        controlPanel.addComponent(new Button("Reset Grid", new Runnable() {
            @Override
            public void run() {
                String columns = new TextInputDialogBuilder()
                        .setTitle("Reset Grid")
                        .setDescription("Reset grid to how many columns?")
                        .setInitialContent("4")
                        .setValidationPattern(Pattern.compile("[0-9]+"), "Not a number")
                        .build()
                        .showDialog(textGUI);
                if(columns == null) {
                    return;
                }
                String prepopulate = new TextInputDialogBuilder()
                        .setTitle("Reset Grid")
                        .setDescription("Pre-populate grid with how many dummy components?")
                        .setInitialContent(columns)
                        .setValidationPattern(Pattern.compile("[0-9]+"), "Not a number")
                        .build()
                        .showDialog(textGUI);
                gridPanel.removeAllComponents();
                gridPanel.setLayoutManager(newGridLayout(Integer.parseInt(columns)));
                for(int i = 0; i < Integer.parseInt(prepopulate); i++) {
                    gridPanel.addComponent(new EmptySpace(getRandomColor(), new TerminalSize(4, 1)));
                }
            }
        }));
        controlPanel.addComponent(new Button("Exit", new Runnable() {
            @Override
            public void run() {
                window.close();
            }
        }));

        mainPanel.addComponent(gridPanel);
        mainPanel.addComponent(
                new Separator(Direction.HORIZONTAL)
                .setLayoutData(
                        LinearLayout.createLayoutData(LinearLayout.Alignment.Fill)));
        mainPanel.addComponent(controlPanel);

        window.setComponent(mainPanel);
        textGUI.addWindow(window);
    }

    private TextColor getRandomColor() {
        return GOOD_COLORS[RANDOM.nextInt(GOOD_COLORS.length)];
    }

    private GridLayout newGridLayout(int columns) {
        GridLayout gridLayout = new GridLayout(columns);
        gridLayout.setTopMarginSize(1);
        gridLayout.setVerticalSpacing(1);
        gridLayout.setHorizontalSpacing(1);
        return gridLayout;
    }
}
