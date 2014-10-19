package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;

import java.io.IOException;

/**
 * Created by martin on 05/10/14.
 */
public class TextBoxTest extends TestBase {
    public static void main(String[] args) throws IOException, InterruptedException {
        new TextBoxTest().run(args);
    }

    @Override
    public void init(WindowBasedTextGUI textGUI) {
        final BasicWindow window = new BasicWindow("TextBoxTest");
        Container contentArea = window.getContentArea();

        Panel mainPanel = new Panel();
        mainPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        Panel leftPanel = new Panel();
        Panel rightPanel = new Panel();

        leftPanel.addComponent(new TextBox().withBorder(Borders.singleLine("Default")));
        leftPanel.addComponent(new TextBox("Some text").withBorder(Borders.singleLine("With init")));
        leftPanel.addComponent(new TextBox(new TerminalSize(10, 1), "Here is some text that is too long to fit in the text box").withBorder(Borders.singleLine("Long text")));

        rightPanel.addComponent(new TextBox(new TerminalSize(10, 5),
                "Well here we are again\n" +
                "It's always such a pleasure\n" +
                "Remember when you tried\n" +
                "to kill me twice?").withBorder(Borders.singleLine()));

        mainPanel.addComponent(leftPanel.withBorder(Borders.singleLine("Single line")));
        mainPanel.addComponent(rightPanel.withBorder(Borders.singleLine("Multiline")));

        contentArea.addComponent(mainPanel.withBorder(Borders.singleLine("Main")));
        contentArea.addComponent(new Button("OK", new Runnable() {
            @Override
            public void run() {
                window.close();
            }
        }));
        textGUI.getWindowManager().addWindow(window);
    }
}
