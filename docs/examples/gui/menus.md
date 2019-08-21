Menus
---

Menus consists of basically three components:

* *menu bar* - contains one or more drop-down menus
* *menu* - contains one or more menu items
* *menu item* - a label associated with an action

The menu bar itself is just a panel with buttons that pop up an action-list-like
dialog window beneath the button, aka the menu, that just got clicked. This 
dialog window contains all the menu items that were added to this menu.
The menu can be closed by pressing the ESC key on the keyboard.

You can either add menu items using objects that implement the `Runnable`
interface via the `addMenuItem(Runnable)` method or call the
`addMenuItem(String,Runnable)` method which will take a separate label and not
derive it from the `toString()` of the `Runnable`.

To add a sub-menu, you can create a add another `Menu` as a menu item to another
`Menu` using the `addSubMenu(Menu)` method.

In the following example, a menu bar with two menus, *File* and *Help*, is 
created, each containing two menu items:

```java
        MenuBar menubar = new MenuBar();

        // "File" menu
        Menu menuFile = new Menu("File");
        menubar.addMenu(menuFile);
        menuFile.addMenuItem("Open...", new Runnable() {
            public void run() {
                File file = new FileDialogBuilder().build().showDialog(textGUI);
                if (file != null)
                    MessageDialog.showMessageDialog(
                            textGUI, "Open", "Selected file:\n" + file, MessageDialogButton.OK);
            }
        });
        menuFile.addMenuItem("Exit", new Runnable() {
            public void run() {
                System.exit(0);
            }
        });

        // "Help" menu
        Menu menuHelp = new Menu("Help");
        menubar.addMenu(menuHelp);
        menuHelp.addMenuItem("Homepage", new Runnable() {
            public void run() {
                MessageDialog.showMessageDialog(
                        textGUI, "Homepage", "https://github.com/mabe02/lanterna", MessageDialogButton.OK);
            }
        });
        menuHelp.addMenuItem("About", new Runnable() {
            public void run() {
                MessageDialog.showMessageDialog(
                        textGUI, "About", "Lanterna drop-down menu", MessageDialogButton.OK);
            }
        });

        // Create window to hold the panel
        BasicWindow window = new BasicWindow();
        window.setComponent(menubar);

        textGUI.addWindow(window);
```

### Screenshot

![](screenshots/menus.png)