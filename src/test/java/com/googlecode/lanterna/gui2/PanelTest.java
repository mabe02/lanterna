package com.googlecode.lanterna.gui2;

import java.io.IOException;

/**
 * Created by martin on 21/09/14.
 */
public class PanelTest extends TestBase {
    public static void main(String[] args) throws IOException, InterruptedException {
        new PanelTest().run(args);
    }

    @Override
    public void init(WindowBasedTextGUI textGUI) {
        final BasicWindow window = new BasicWindow("Grid layout test");

        Panel mainPanel = new Panel();
        mainPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        Panel leftPanel = new Panel();
        mainPanel.addComponent(leftPanel.withBorder(Borders.singleLine("Left")));

        Panel panel = new Panel();
        panel.addComponent(new Button("Panel 1 Button"));
        leftPanel.addComponent(panel.withBorder(Borders.singleLine()));
        panel = new Panel();
        panel.addComponent(new Button("Panel 2 Button"));
        leftPanel.addComponent(panel.withBorder(Borders.singleLine("Title")));
        panel = new Panel();
        panel.addComponent(new Button("Panel 3 Button"));
        leftPanel.addComponent(panel.withBorder(Borders.doubleLine()));
        panel = new Panel();
        panel.addComponent(new Button("Panel 4 Button"));
        leftPanel.addComponent(panel.withBorder(Borders.doubleLine("Title")));

        Panel rightPanel = new Panel();
        mainPanel.addComponent(rightPanel.withBorder(Borders.singleLine("Right")));

        panel = new Panel();
        panel.addComponent(new Button("Panel 1 Button"));
        panel.addComponent(new Panel().withBorder(Borders.singleLine("A")));
        panel.addComponent(new Panel().withBorder(Borders.singleLine("Some Text")));
        rightPanel.addComponent(panel.withBorder(Borders.singleLine("B")));
        panel = new Panel();
        panel.addComponent(new Button("Panel 2 Button"));
        rightPanel.addComponent(panel.withBorder(Borders.singleLine("Title")));
        panel = new Panel();
        panel.addComponent(new Button("Panel 3 Button"));
        rightPanel.addComponent(panel.withBorder(Borders.doubleLine()));
        panel = new Panel();
        panel.addComponent(new Button("Panel 4 Button"));
        rightPanel.addComponent(panel.withBorder(Borders.doubleLine("Title")));

        window.setComponent(Panels.vertical(
                mainPanel.withBorder(Borders.singleLine("Main")),
                new Button("OK", new Runnable() {
                    @Override
                    public void run() {
                        window.close();
                    }
                })));
        mainPanel.getPreferredSize();
        textGUI.addWindow(window);
    }
}
