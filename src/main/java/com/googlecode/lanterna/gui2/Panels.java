package com.googlecode.lanterna.gui2;

/**
 * Utility class for quickly bunching up components in a panel, arranged in a particular pattern
 * @author Martin
 */
public class Panels {

    public static Panel horizontal(Component... components) {
        Panel panel = new Panel();
        panel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        for(Component component: components) {
            panel.addComponent(component);
        }
        return panel;
    }

    public static Panel vertical(Component... components) {
        Panel panel = new Panel();
        panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        for(Component component: components) {
            panel.addComponent(component);
        }
        return panel;
    }

    //Cannot instantiate
    private Panels() {}
}
