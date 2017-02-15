_Please note: You should read the UsingTerminal guide before you start reading this guide_

### About the Screen layer ###
When creating text-based GUIs using low-level operations, it's very likely that the advantages of buffering screen data will be apparent. Instead of redrawing the whole screen every time something changes, wouldn't it be better to calculate the difference and only apply the changes there? The Screen layer is doing precisely this; keeping a back buffer that you write to and then allow you to perform a refresh operation that will calculate and update the differences between your back buffer and the visible screen.

### Getting the `Screen` object ###
In order to use the Screen layer, you will need a `com.googlecode.lanterna.screen.Screen` object. Just like the `Terminal` object was the basis for all operations in the Terminal layer, you will use the `Screen` object here. To create a `Screen` object, you'll need an already existing `Terminal`. Create the `Screen` like this:
```
    Screen screen = new Screen(terminal);
```

#### Using the `TerminalFacade` ####
Just like with the Terminal layer, you can also use the `TerminalFacade` class to quickly create a `Screen`. Please see JavaDoc documentation for more information about the `TerminalFacade` class.
```
    Screen screen = TerminalFacade.getScreen();
```

If you want to force a particular `Terminal` implementation, you can do this too:
```
    Screen screen = TerminalFacade.getScreen(new SwingTerminal());
```

### Start the screen ###
Instead of the notion of _private mode_, using the Screen layer requires you to _start_ the screen before you use it. In the same way you can also stop the screen.
```
    screen.startScreen();
    
    //draw something

    screen.stopScreen();
```

### Drawing text ###
The `Screen` object exposes a `putString` method that will put a certain string at a certain position with certain attributes (color and style). It's very straight-forward:
```
    screen.putString(10, 5, "Hello Lanterna!", Terminal.Color.Red, Terminal.Color.Green,
                         false, false, false);
    screen.refresh();
```

After writing text to the screen, in order to make it show up you have to call the `refresh()` method, like in the example above. Please see below.

#### Using a `ScreenWriter` object ####
When writing text using the `Screen` object, you have to specify properties such as color and style every time. There is a helper-class available for you, the `ScreenWriter`, which will keep an internal state so you don't have to do it yourself. Here's how to use it:
```
    ScreenWriter writer = new ScreenWriter(screen);
    writer.setForegroundColor(Terminal.Color.BLACK);
    writer.setBackgroundColor(Terminal.Color.WHITE);
    writer.drawString(5, 3, "Hello Lanterna!", Terminal.Style.Bold);
    writer.setForegroundColor(Terminal.Color.DEFAULT);
    writer.setBackgroundColor(Terminal.Color.DEFAULT);
    writer.drawString(5, 5, "Hello Lanterna!");
    writer.drawString(5, 7, "Hello Lanterna!");
    screen.refresh();
```

### Reading input from the keyboard ###
The Screen also exposes a `readInput()` method which will be delegated to the underlying terminal. Please read the section on keyboard input in
UsingTerminal.

### Clearing the screen ###
The correct way to reset a `Screen` is to call `clear()` on it.
```
    //Let's say the terminal contains some random characters...
    screen.clear();
    screen.refresh();
    //The terminal is now blank to the user
```

### Refreshing the screen ###
As have been noted above, when you have been modifying your screen you need to call the `refresh()` method to make the changes show up. This is because the `Screen` will keep an in-memory buffer of the terminal window. When you draw strings to the screen, you actually modify the buffer and not the the real terminal. Calling `refresh()` will make the screen compare what's on the screen and what's in the buffer, and output commands to the underlying `Terminal` so that the screen looks like the buffer. This way, you can print text on the same location over and over and in the end won't waste anything when the changes are flushed to standard out (or whatever your terminal is using).

### Handling terminal resize ###
Currently, a `Screen` doesn't handle resizes automatically (**why? what was the reason for this again??**). However, when you call `refresh()`, if the terminal window has been resized since the last refresh, it will resize the screen (and more importantly, the buffer) for you. So all you need to do it call `refresh()` after a resize event. You can also periodically call `resizePending()` which will return `true` if it has detected a resize event and the next `refresh()` will resize the screen.
```
    while(keepRunning) {
        Key key = screen.readInput();
        if(key != null)
            handleInput(key);

        if(screen.resizePending())
            screen.refresh();
    }
```

### Manipulating the underlying terminal ###
You can actually fetch the underlying `Terminal` from the `Screen` by calling `getTerminal()`. Please keep in mind that if you start modifying the terminal by sending characters to the screen or modifying its SGR or color status, the screen may not work as you intend anymore. The only way the screen can know what visible in the real terminal window is by remembering what the buffer looked like before the last refresh. When you modify the underlying terminal, this buffer-memory will get out of sync and undefined behaviour will begin.