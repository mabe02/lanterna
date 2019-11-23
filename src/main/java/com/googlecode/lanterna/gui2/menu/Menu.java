package com.googlecode.lanterna.gui2.menu;

import com.googlecode.lanterna.gui2.MenuPopupWindow;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.WindowListenerAdapter;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Menu extends MenuItem {
    private final List<MenuItem> subItems;

    public Menu(String label) {
        super(label);
        this.subItems = new ArrayList<MenuItem>();
    }

    public void add(MenuItem menuItem) {
        synchronized (subItems) {
            subItems.add(menuItem);
        }
    }

    @Override
    protected boolean onActivated() {
        boolean result = true;
        if (!subItems.isEmpty()) {
            final MenuPopupWindow popupMenu = new MenuPopupWindow(this);
            final AtomicBoolean popupCancelled = new AtomicBoolean(false);
            for (MenuItem menuItem: subItems) {
                popupMenu.addMenuItem(menuItem);
            }
            if (getParent() instanceof MenuBar) {
                final MenuBar menuBar = (MenuBar)getParent();
                popupMenu.addWindowListener(new WindowListenerAdapter() {
                    @Override
                    public void onUnhandledInput(Window basePane, KeyStroke keyStroke, AtomicBoolean hasBeenHandled) {
                        if (keyStroke.getKeyType() == KeyType.ArrowLeft) {
                            int thisMenuIndex = menuBar.getChildren().indexOf(Menu.this);
                            if (thisMenuIndex > 0) {
                                popupMenu.close();
                                Menu nextSelectedMenu = menuBar.getMenu(thisMenuIndex - 1);
                                nextSelectedMenu.takeFocus();
                                nextSelectedMenu.onActivated();
                            }
                        }
                        else if (keyStroke.getKeyType() == KeyType.ArrowRight) {
                            int thisMenuIndex = menuBar.getChildren().indexOf(Menu.this);
                            if (thisMenuIndex >= 0 && thisMenuIndex < menuBar.getMenuCount() - 1) {
                                popupMenu.close();
                                Menu nextSelectedMenu = menuBar.getMenu(thisMenuIndex + 1);
                                nextSelectedMenu.takeFocus();
                                nextSelectedMenu.onActivated();
                            }
                        }
                    }
                });
            }
            popupMenu.addWindowListener(new WindowListenerAdapter() {
                @Override
                public void onUnhandledInput(Window basePane, KeyStroke keyStroke, AtomicBoolean hasBeenHandled) {
                    if (keyStroke.getKeyType() == KeyType.Escape) {
                        popupCancelled.set(true);
                        popupMenu.close();
                    }
                }
            });
            ((WindowBasedTextGUI)getTextGUI()).addWindowAndWait(popupMenu);
            result = !popupCancelled.get();
        }
        return result;
    }
}
