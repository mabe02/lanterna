package com.googlecode.lanterna.gui2;

import javax.sound.sampled.Line;
import java.io.IOException;

/**
 * Created by martin on 22/06/15.
 */
public class LinearLayoutTest extends TestBase {
    public static void main(String[] args) throws InterruptedException, IOException {
        new LinearLayoutTest().run(args);
    }

    @Override
    public void init(WindowBasedTextGUI textGUI) {
        final BasicWindow window = new BasicWindow("Linear layout test");
        final Panel mainPanel = new Panel();
        final Panel labelPanel = new Panel();
        final LinearLayout linearLayout = new LinearLayout(Direction.VERTICAL);
        linearLayout.setSpacing(1);
        labelPanel.setLayoutManager(linearLayout);

        for(int i = 0; i < 5; i++) {
            new Label("LABEL COMPONENT").addTo(labelPanel);
        }
        mainPanel.addComponent(labelPanel);

        new Separator(Direction.HORIZONTAL)
                .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill))
                .addTo(mainPanel);

        mainPanel.addComponent(Panels.horizontal(
                new Button("Add", new Runnable() {
                    @Override
                    public void run() {
                        new Label("LABEL COMPONENT").addTo(labelPanel);
                    }
                }),
                new Button("Spacing", new Runnable() {
                    @Override
                    public void run() {
                        linearLayout.setSpacing(linearLayout.getSpacing() == 1 ? 0 : 1);
                    }
                }),
                new Button("Close", new Runnable() {
                    @Override
                    public void run() {
                        window.close();
                    }
                })
        ));

        window.setComponent(mainPanel);
        textGUI.addWindow(window);
    }
}
