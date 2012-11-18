package com.googlecode.lanterna.test.issue;

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.Component;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.EmptySpace;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.component.Panel.Orientation;
import com.googlecode.lanterna.gui.component.Table;
import com.googlecode.lanterna.gui.layout.LinearLayout;
import com.googlecode.lanterna.gui.layout.VerticalLayout;
import com.googlecode.lanterna.test.TestTerminalFactory;

public class Issue49 {
    public static void main(String[] args) {
        final GUIScreen guiScreen = new TestTerminalFactory(args).createGUIScreen();
        final Window window = new Window("Error while showing a wide table");
        
        Panel panelHolder = new Panel("Holder panel",Orientation.VERTICAL);
        
        Panel panel = new Panel("Panel with a right-aligned button");

        panel.setLayoutManager(new VerticalLayout());
        Button button = new Button("Button");
        button.setAlignment(Component.Alignment.RIGHT_CENTER);
        panel.addComponent(button, LinearLayout.GROWS_HORIZONTALLY);
        
        Table table = new Table(11);
        table.setColumnPaddingSize(5);
        
        Component[] row1 = new Component[11];
        row1[0] = new Label("Field-1");
        row1[1] = new Label("Field-2");
        row1[2] = new Label("Field-3");
        row1[3] = new Label("Field-4");
        row1[4] = new Label("Field-5");
        row1[5] = new Label("Field-6");
        row1[6] = new Label("Field-7");
        row1[7] = new Label("Field-8");
        row1[8] = new Label("Field-9");
        row1[9] = new Label("Field-10");
        row1[10] = new Label("Field-11");
        
        table.addRow(row1);
        panel.addComponent(table);
        
        panelHolder.addComponent(panel);

        window.addComponent(panelHolder);
        window.addComponent(new EmptySpace());

        Button quitButton = new Button("Exit", new Action() {
            @Override
            public void doAction() {
                window.close();
            }
        });
        quitButton.setAlignment(Component.Alignment.RIGHT_CENTER);
        window.addComponent(quitButton, LinearLayout.GROWS_HORIZONTALLY);

        guiScreen.getScreen().startScreen();
        guiScreen.showWindow(window);
        guiScreen.getScreen().stopScreen();
    }
}
