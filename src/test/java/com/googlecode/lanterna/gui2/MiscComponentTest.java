package com.googlecode.lanterna.gui2;

import java.io.IOException;

/**
 * Created by martin on 19/10/14.
 */
public class MiscComponentTest extends TestBase {
    public static void main(String[] args) throws IOException, InterruptedException {
        new MiscComponentTest().run(args);
    }

    @Override
    public void init(WindowBasedTextGUI textGUI) {
        final BasicWindow window = new BasicWindow("Grid layout test");
        Container contentArea = window.getContentArea();
        Panel checkBoxPanel = new Panel();

        for(int i = 0; i < 4; i++) {
            CheckBox checkBox = new CheckBox("Checkbox #" + (i+1));
            checkBoxPanel.addComponent(checkBox);
        }

        contentArea.addComponent(checkBoxPanel.withBorder(Borders.singleLine("CheckBoxes")));
        contentArea.addComponent(new Button("OK", new Runnable() {
            @Override
            public void run() {
                window.close();
            }
        }));
        textGUI.getWindowManager().addWindow(window);
    }
}
