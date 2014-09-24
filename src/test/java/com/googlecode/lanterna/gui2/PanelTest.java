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
        Container contentArea = window.getContentArea();

        Panel mainPanel = new Panel();
        mainPanel.setLayoutManager(new LinearLayout(LinearLayout.Direction.HORIZONTAL));

        Panel leftPanel = new Panel();
        mainPanel.addComponent(leftPanel.withBorder(Borders.singleLine()));

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
        mainPanel.addComponent(rightPanel.withBorder(Borders.singleLine()));

        panel = new Panel();
        panel.addComponent(new Button("Panel 1 Button"));
        rightPanel.addComponent(panel.withBorder(Borders.singleLine()));
        panel = new Panel();
        panel.addComponent(new Button("Panel 2 Button"));
        rightPanel.addComponent(panel.withBorder(Borders.singleLine("Title")));
        panel = new Panel();
        panel.addComponent(new Button("Panel 3 Button"));
        rightPanel.addComponent(panel.withBorder(Borders.doubleLine()));
        panel = new Panel();
        panel.addComponent(new Button("Panel 4 Button"));
        rightPanel.addComponent(panel.withBorder(Borders.doubleLine("Title")));

        contentArea.addComponent(mainPanel.withBorder(Borders.singleLine()));
        contentArea.addComponent(new Button("OK", new Runnable() {
            @Override
            public void run() {
                window.close();
            }
        }));
        textGUI.getWindowManager().addWindow(window);
    }
}
