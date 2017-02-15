### Introduction ###
The **Terminal** layer is the lowest level available in Lanterna, giving you very precise control of what data is sent to the client. You will find these classes in the `com.googlecode.lanterna.terminal` package.

### Getting the `Terminal` object ###
The core of the Terminal layer is the `Terminal` interface which you must retrieve. This can be done either by directly instantiating one of the implementing classes (`UnixTerminal`, `CygwinTerminal` or `SwingTerminal` or by using the `com.googlecode.lanterna.TerminalFacade` class.

#### What's the big deal anyway? ####
So what is the difference between these implementations of `Terminal`? If, by terminal, you think of applications like `xterm`, `konsole`, `putty` or `Terminal.app`, then using Lanterna with a terminal would involve writing control sequences to standard output (_stdout_) and reading from standard input (_stdin_). You would run your java application in this terminal and calling the various methods on the `Terminal` interface will be translated by the implementation to the equivalent control sequences. But a terminal could be anything and we don't want to lock ourselves to a particular idea of what a terminal is (something using _stdout_/_stdin_ for example). Instead, in Lanterna, a terminal is something that implements `Terminal` and with a correct implementation of the interface, the entire stack (`Screen` and `GUIScreen`) will work on top of it without any modifications.

#### `SwingTerminal` ####
One alternative implementation of `Terminal` is the `SwingTerminal` class. This is a basic terminal emulator implemented in Swing and it will create a JFrame that represents your terminal. Calling the `Terminal` methods on a `SwingTerminal` won't do anything with _stdout_ or _stdin_, instead it will translate it to paint operations on the JFrame. This is useful, for example, when you are developing your application that uses Lanterna. Probably you are using an IDE like Eclipse, NetBeans or IntelliJ and the console window in this IDE probably doesn't support any of the control sequences the `UnixTerminal` will write to _stdout_ when you try to move around the cursor or change the color of the text. By using `SwingTerminal`, running the application will instead create this new window that emulates a terminal and you can develop and debug like you do with normal Java GUIs.

#### `TerminalFacade` ####
By using the `TerminalFacade` class, you will have some helper methods available that tries to auto-detect the system environment during runtime. The encoding will be picked up through the `file.encoding` system property (which is automatically set by the JVM) and the type of `Terminal` implementation to use will be decided based on if the system has a graphical environment (then use `SwingTerminal`) or not (then use `UnixTerminal`). The idea for this class is to help you create an application that works on both a graphical system and a headless system without requiring any change of configuration or recompilation. Also, if you are on a system with a windowing environment but want to force a text-based (_stdout_/_stdin_) terminal, you can pass the following option to the JRE:
```
   java -Djava.awt.headless=true  ...
```
The method you want to call on `TerminalFacade` for the auto-detection functionality is `createTerminal` but there are also several customizable overloads available.

#### Example ####
```
    Terminal terminal = TerminalFacade.createTerminal(System.in, System.out, Charset.forName("UTF8"));
```
or you can do this, same thing:
```
    Terminal terminal = TerminalFacade.createTerminal(Charset.forName("UTF8"));
```
or you can do this, if your `file.encoding` is already set to UTF-8:
```
    Terminal terminal = TerminalFacade.createTerminal();
```

### Entering and exiting private mode ###
Before you can print any text or start to move the cursor around, you should enter what's called the _private mode_. In this mode the screen is cleared, scrolling is (sometimes) disabled and the previous content is stored away. When you exit private mode, the previous content is restored and displayed on screen again as it was before private mode was entered. Some systems/terminals doesn't support this mode at all, but will still perform some screen cleaning operations for you. It's always recommended to enter private mode when you start your GUI and exit when you finish.

#### Example ####
```
    terminal.enterPrivateMode();
    ...
    terminal.exitPrivateMode();
```

### Moving the cursor ###
When you start your GUI, you can never make any assumptions on where the text cursor is. Since text printing will always appear at the text cursor position, it is very important to be able to move this around. Here is how you do it:
```
   terminal.moveCursor(10, 5);
```
The first parameter is to which column (the first is 0) and the record to which row (again, first row is 0).

### Get the size of the terminal ###
In order to be able to make good decisions on moving the cursor, you might want to know how big the terminal is. The `Terminal` object will expose a `getTerminalSize()` method that will do precisely this.
```
    TerminalSize screenSize = terminal.getTerminalSize();

    //Place the cursor in the bottom right corner
    terminal.moveCursor(screenSize.getColumns() - 1, screenSize.getRows() - 1);
```

### Printing text ###
Printing text is another very useful operation, it's simple enough but a bit limited as you have to print character by character.
```
    terminal.moveCursor(10, 5);
    terminal.putCharacter('H');
    terminal.putCharacter('e');
    terminal.putCharacter('l');
    terminal.putCharacter('l');
    terminal.putCharacter('o');
    terminal.putCharacter('!');
    terminal.moveCursor(0, 0);
```
Notice that just like when you type in text manually, the cursor position will move one column to the right for every character you put. What happens after you put a character on the last column is undefined and may differ between different terminal emulators. You should always use the moveCursor method to place the cursor somewhere else after writing something to the end of the row.

### Using colors ###
If you want to make your text a bit more beautiful, try adding some color! The `Terminal` object will keep a state of which foreground color and which background color is currently active, so after setting this state it will be applied to all text written until you change it again.

The following colors can be used:
  * BLACK
  * RED
  * GREEN
  * YELLOW
  * BLUE
  * MAGENTA
  * CYAN
  * WHITE
  * DEFAULT

Notice the default color, which is up to the terminal emulator to decide, you can't know what it will be. RGB color is supported by some (few) terminals and may be added to Lanterna in the future.

#### Example ####
```
    terminal.applyForegroundColor(Terminal.Color.RED);
    terminal.applyBackgroundColor(Terminal.Color.BLUE);

    //Print something

    terminal.applyForegroundColor(Terminal.Color.DEFAULT);
    terminal.applyBackgroundColor(Terminal.Color.DEFAULT);
```

### Using text styles ###
More than color, you can also add certain styles to the text. How well this is supported depends completely on the client terminal so results may vary. Typically at least bold mode is supported, but usually not rendering as bold but rather as a brighter color. These style modes are referred to as SGR and just like with colors the terminal will keep their state until you explicitly change it. The following SGR are avaiable in version 2.0.x:
  * RESET\_ALL
  * ENTER\_BOLD
  * ENTER\_REVERSE
  * ENTER\_UNDERLINE
  * ENTER\_BLINK
  * EXIT\_BOLD
  * EXIT\_REVERSE
  * EXIT\_UNDERLINE
  * EXIT\_BLINK

RESET\_ALL is a good way to make sure there are no SGRs active. You apply these by calling `applySGR` on the `Terminal` object:
```
   terminal.applySGR(Terminal.SGR.ENTER_BOLD);

   //Draw text highlighted

   terminal.applySGR(Terminal.SGR.EXIT_BOLD);   //or terminal.applySGR(Terminal.SGR.RESET_ALL);
```

### Clearing the screen ###
If you want to completely reset the screen, you can use the `clearScreen()` method. This will (probably) remove all characters on the screen and replace them with the empty character (' ') having Terminal.Color.DEFAULT set as background.

```
    terminal.clearScreen();
```

### Flushing ###
To be sure that the text has been sent to the client terminal, you should call the `flush()` method on the `Terminal` interface when you have done all your operations.

### Read keyboard input ###
Finally, retrieving user input is necessary unless the GUI is totally hands-off. Normally, when the user presses a key, it will be added to a buffer awaiting retrieval. You can poll this buffer by calling `readInput()` on the terminal object, which will return you a `Key` object representing the key pressed, or null is no key has been pressed. The `readInput()` method is actually not directly on the `Terminal` interface, but on another interface, `InputProvider`, which the `Terminal` interface extends. The `Key` class has a `Key.Kind` enum which tells you what kind of key was pressed. The `Kind` object can take any of the following values (v 2.1.x):
  * NormalKey
  * Escape
  * Backspace
  * ArrowLeft
  * ArrowRight
  * ArrowUp
  * ArrowDown
  * Insert
  * Delete
  * Home
  * End
  * PageUp
  * PageDown
  * Tab('\t')
  * ReverseTab
  * Enter('\n')
  * F1
  * F2
  * F3
  * F4
  * F5
  * F6
  * F7
  * F8
  * F9
  * F10
  * F11
  * F12
  * Unknown

If you get the kind `NormalKey`, the key is then a alpha-numerical-symbol key and it can be retrieved using the `Key` object's `getCharacter()` method. At the moment, there is no way to detect when the shift-key is pressed, you will see the result on `Key` objects read while the key is pressed though (characters will be in upper-case, numbers will turn into symbols, etc). You can detect if CTRL and/or ALT keys were pressed down while the keystroke happened, by using the `isAltPressed()` and `isCtrlPressed()` methods on the `Key` (unfortunately, not all combinations are supported yet! If you want to use special key combinations in your program, please test with multiple terminals to make sure it's working as you intend). Unfortunately, you cannot detect CTRL or ALT as individual keystrokes.