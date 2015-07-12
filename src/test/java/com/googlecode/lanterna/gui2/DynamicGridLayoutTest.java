package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.dialogs.ListSelectDialog;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialogBuilder;

import java.io.IOException;
import java.math.BigInteger;
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
        controlPanel.addComponent(new Button("Add Component", new Runnable() {
            @Override
            public void run() {
                onAddComponent(textGUI, gridPanel);
            }
        }));
        controlPanel.addComponent(new Button("Reset Grid", new Runnable() {
            @Override
            public void run() {
                onResetGrid(textGUI, gridPanel);
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

    private void onAddComponent(WindowBasedTextGUI textGUI, Panel gridPanel) {
        SelectableComponentType componentType = ListSelectDialog.showDialog(
                textGUI,
                "Add Component",
                "Select component to add",
                SelectableComponentType.values());
        if(componentType == null) {
            return;
        }
        Component component = null;
        switch(componentType) {
            case Block:
            case TextBox:
                String sizeString = new TextInputDialogBuilder()
                        .setInitialContent(componentType == SelectableComponentType.Block ? "4x1" : "16x1")
                        .setTitle("Add " + componentType)
                        .setDescription("Enter size of " + componentType + " (<columns>x<rows>)")
                        .setValidationPattern(Pattern.compile("[0-9]+x[0-9]+"), "Invalid format, please use <columns>x<rows>")
                        .build()
                        .showDialog(textGUI);
                if(sizeString == null) {
                    return;
                }
                TerminalSize size = new TerminalSize(Integer.parseInt(sizeString.split("x")[0]), Integer.parseInt(sizeString.split("x")[1]));
                component = componentType == SelectableComponentType.Block ? new EmptySpace(getRandomColor(), size) : new TextBox(size);
                break;

            case Label:
                String text = TextInputDialog.showDialog(textGUI, "Add " + componentType, "Enter the text of the new Label", "Label");
                component = new Label(text);
                break;
        }
        if(component != null) {
            gridPanel.addComponent(component);
        }
    }

    private void onResetGrid(WindowBasedTextGUI textGUI, Panel gridPanel) {
        BigInteger columns = TextInputDialog.showNumberDialog(textGUI, "Reset Grid", "Reset grid to how many columns?", "4");
        if(columns == null) {
            return;
        }
        BigInteger prepopulate = TextInputDialog.showNumberDialog(
                textGUI,
                "Reset Grid",
                "Pre-populate grid with how many dummy components?",
                columns.toString());
        gridPanel.removeAllComponents();
        gridPanel.setLayoutManager(newGridLayout(columns.intValue()));
        for(int i = 0; i < prepopulate.intValue(); i++) {
            gridPanel.addComponent(new EmptySpace(getRandomColor(), new TerminalSize(4, 1)));
        }
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

    private enum SelectableComponentType {
        Block,
        Label,
        TextBox,
    }
}
