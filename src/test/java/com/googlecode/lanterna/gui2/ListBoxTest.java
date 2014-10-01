package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;

import java.io.IOException;

/**
 * Simple test for the different kinds of list boxes
 * @author Martin
 */
public class ListBoxTest extends TestBase {
    public static void main(String[] args) throws IOException, InterruptedException {
        new ListBoxTest().run(args);
    }

    @Override
    public void init(WindowBasedTextGUI textGUI) {
        final BasicWindow window = new BasicWindow("ListBox test");
        Container contentArea = window.getContentArea();

        Panel horizontalPanel = new Panel();
        horizontalPanel.setLayoutManager(new LinearLayout(LinearLayout.Direction.HORIZONTAL));

        TerminalSize size = new TerminalSize(14, 10);
        CheckBoxList checkBoxList = new CheckBoxList(size);
        RadioBoxList radioBoxList = new RadioBoxList(size);
        for(int i = 0; i < 30; i++) {
            String itemText = "Item " + (i + 1);
            checkBoxList.addItem(itemText);
            radioBoxList.addItem(itemText);
        }
        horizontalPanel.addComponent(checkBoxList.withBorder(Borders.singleLine("CheckBoxList")));
        horizontalPanel.addComponent(radioBoxList.withBorder(Borders.singleLine("RadioBoxList")));

        contentArea.addComponent(horizontalPanel);
        contentArea.addComponent(new Button("OK", new Runnable() {
            @Override
            public void run() {
                window.close();
            }
        }));
        textGUI.getWindowManager().addWindow(window);
    }
}
