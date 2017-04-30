# Buffered screen API
_Please note: You should read the [Direct terminal access](using-terminal.md) guide before you start reading this guide_

## About the Screen layer
When creating text-based GUIs using low-level operations, it's very likely that the advantages of buffering screen data 
will be apparent. Instead of redrawing the whole screen every time something changes, wouldn't it be better to calculate 
the difference and only apply the changes there? The `Screen` layer is doing precisely this; keeping a back buffer that 
you write to and then allow you to perform a refresh operation that will calculate and update the differences between 
your back buffer and the visible screen.

## Getting the `Screen` object
In order to use the Screen layer, you will need a `com.googlecode.lanterna.screen.Screen` object. Just like the 
`Terminal` object was the basis for all operations in the lowest layer, you will use the `Screen` object here. To create 
a `Screen` object, you'll need an already existing `Terminal`. Create the `Screen` like this:

    Screen screen = new TerminalScreen(terminal);

### Using the `DefaultTerminalFactory`
Just like with the Terminal layer, you can also use the `DefaultTerminalFactory` class to quickly create a `Screen`. 
Please see JavaDoc documentation for more information about the `DefaultTerminalFactory` class.

    Screen screen = new DefaultTerminalFactory().createScreen();

## Start the screen
Instead of the notion of _private mode_, using the Screen layer requires you to _start_ the screen before you use it. 
In the same way you can also stop the screen when the application is done (or at least doesn't need the screen anymore).

    screen.startScreen();
    
    // do text GUI application logic here until done

    screen.stopScreen();

## Drawing text ##
The `Screen` object exposes a `setCharacter(..)` method that will put a certain character at a certain position with 
certain attributes (color and style). It's very straight-forward:

    screen.setCharacter(10, 5, new TextCharacter('!', TextColor.ANSI.RED, TextColor.ANSI.GREEN));
    
When drawing full strings, it's easier to use the `TextGraphics` helper interface instead.

    TextGraphics textGraphics = screen.newTextGraphics();
    textGraphics.setForegroundColor(TextColor.ANSI.RED);
    textGraphics.setBackgroundColor(TextColor.ANSI.GREEN);
    textGraphics.putString(10, 5, "Hello Lanterna!");

After writing text to the screen, in order to make it show up you have to call the `refresh()` method.

    screen.refresh();

## Reading input from the keyboard
The Screen also exposes the `pollInput()` and `readInput()` methods from `InputProvider`, which in this case will be 
delegated to the underlying terminal. Please read the section on keyboard input in [Direct terminal access](using-terminal.md).

## Clearing the screen
The correct way to reset a `Screen` is to call `clear()` on it.

    //Let's say the terminal contains some random characters...
    screen.clear();
    screen.refresh();
    //The terminal is now blank to the user

## Refreshing the screen
As have been noted above, when you have been modifying your screen you need to call the `refresh()` method to make the 
changes show up. This is because the `Screen` will keep an in-memory buffer of the terminal window. When you draw 
strings to the screen, you actually modify the buffer and not the the real terminal. Calling `refresh()` will make the 
screen compare what's on the screen and what's in the buffer, and output commands to the underlying `Terminal` so that 
the screen looks like the buffer. This way, you can print text on the same location over and over and in the end won't 
waste anything when the changes are flushed to standard out (or whatever your terminal is using).

## Handling terminal resize
Screens will automatically listen and record size changes, but you have to let the Screen know when is
a good time to update its internal buffers. Usually you should do this at the start of your "drawing"
loop, if you have one. This ensures that the dimensions of the buffers stays constant and doesn't change
while you are drawing content. The method doReizeIfNecessary() will check if the terminal has been
resized since last time it was called (or since the screen was created if this is the first time
calling) and update the buffer dimensions accordingly. It returns null if the terminal has not changed
size since last time.

    TerminalSize newSize = screen.doResizeIfNecessary();
    if(newSize != null) {
        terminalSize = newSize;
    }

## Manipulating the underlying terminal
The only way a `Screen` can know what visible in the real terminal window is by remembering what the buffer looked like 
before the last refresh. When you modify the underlying directly terminal, this buffer-memory will get out of sync and 
the `refresh()` is likely to not properly update the content. So if you use `Screen`, a general rule of thumb is to 
avoid operating on the `Terminal` you passed in when creating the `Screen`.  

## Learn more
To learn about how to use the bundled text GUI API in Lanterna built on top of the screen API described in this page, 
continue reading at [Text GUI](using-gui.md).