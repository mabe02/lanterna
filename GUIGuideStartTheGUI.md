### Getting the `GUIScreen` object ###
In order to use the text GUI system at all, you'll need a `GUIScreen` object. You can manually instantiate it if you have a `Screen` object available (please see the [Screen layer guide](UsingScreen.md)):
```
    GUIScreen gui = new GUIScreen(screen);
```

Alternatively, you can use the `TerminalFacade`:
```
    GUIScreen gui = TerminalFacade.createGUIScreen(); //Multiple overloads exist
```

Please note that there are no start/stop methods in the GUIScreen class, so you'll need to use the underlying `Screen` to do such operations.

Example:
```
    GUIScreen textGUI = TerminalFacade.createGUIScreen();
    if(textGUI == null) {
        System.err.println("Couldn't allocate a terminal!");
        return;
    }
    textGUI.getScreen().startScreen();
    textGUI.setTitle("GUI Test");

    //Do GUI logic here

    textGUI.getScreen().stopScreen();
```

Please note that you can start the screen (enter private mode) anytime, it doesn't have to be after the `GUIScreen` in initialized. The `GUIScreen` class doesn't necessarily know that it is ultimately drawing to a `Terminal`; all it's actions go through the `Screen`. This is why you can at any time create a `GUIScreen` on top of a `Screen` and start using it, then exit from the GUI and continue using the `Screen` as you like.