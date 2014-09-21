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
        Panel panel = new Panel().withBorder(Borders.singleLine());
        panel.addComponent(new Button("Panel 1 Button"));
        contentArea.addComponent(panel);
        panel = new Panel().withBorder(Borders.singleLine());
        panel.addComponent(new Button("Panel 2 Button"));
        contentArea.addComponent(panel);
        panel = new Panel().withBorder(Borders.singleLine());
        panel.addComponent(new Button("Panel 3 Button"));
        contentArea.addComponent(panel);
        panel = new Panel().withBorder(Borders.singleLine());
        panel.addComponent(new Button("Panel 4 Button"));
        contentArea.addComponent(panel);
        contentArea.addComponent(new Button("OK", new Runnable() {
            @Override
            public void run() {
                window.close();
            }
        }));
        textGUI.getWindowManager().addWindow(window);
    }
}
