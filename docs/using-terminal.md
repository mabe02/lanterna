# Direct terminal access
## Introduction
The `Terminal` layer is the lowest level available in Lanterna, giving you very precise control of what data is sent to 
the client. You will find these classes in the `com.googlecode.lanterna.terminal` package.

## Getting the `Terminal` object
The core of the Terminal layer is the `Terminal` interface which you must retrieve. This can be done either by directly 
instantiating one of the implementing classes (for example `UnixTerminal` or `SwingTerminalFrame`) or by using the 
`com.googlecode.lanterna.terminal.DefaultTerminalFactory` class which auto-detects and guesses what kind of implementation is
most suitable given the system environment.

### Different implementations
in Lanterna, a terminal is something that implements `Terminal` and with a correct implementation of the interface, the 
entire stack (`Screen` and `TextGUI`) will work on top of it without any modifications. The regular implementation of
`Terminal` can be thought of as the `UnixTerminal` class, which translates the interface calls on `Terminal` to the
ANSI control sequences required to perform those operations, by default to `stdout` and reading from `stdin`. But 
lanterna also contains a simple built-in terminal emulator that implements the `Terminal` interface, accessible through 
`SwingTerminal` and `AWTTerminal` (and related helper classes) that will open a new window containing the terminal and
 each API call is directly mutating the internal state of this emulator. There is also a build-in telnet server that
 presents incoming clients as a `Terminal` object that you can control.   

### Using `DefaultTerminalFactory` to choose implementation
By using the `DefaultTerminalFactory` class, you can use some build-in code that tries to auto-detect the system 
environment during runtime. The encoding will be picked up through the `file.encoding` system property (which is 
automatically set by the JVM) and the type of `Terminal` implementation to use will be decided based on if the system 
has a graphical environment (then use `SwingTerminal`) or not (then use `UnixTerminal`). The idea for this class is to 
help you create an application that works on both a graphical system and a headless system without requiring any change 
of configuration or recompilation. Also, if you are on a system with a windowing environment but want to force a 
text-based (`stdout`/`stdin`) terminal, you can pass the following option to the JRE:

    java -Djava.awt.headless=true  ...

While not exposed on the `TerminalFactory` interface (that `DefaultTerminalFactory` implements), 
`DefaultTerminalFactory` has a number of extra methods you can use to further customize the terminals it creates.

#### Example

    Terminal terminal = new DefaultTerminalFactory(System.out, System.in, Charset.forName("UTF8")).createTerminal();

or you can do just this which will use `stdout`, `stdin` and the platform encoding.  

    Terminal terminal = new DefaultTerminalFactory().createTerminal();

###### Note:
On Windows, you need to use [javaw](http://pages.citebite.com/p6q0p5r4h7sny) to start your application or
`IOException` will be thrown while invoking `DefaultTerminalFactory.createTerminal()`, see mabe02/lanterna#335.

## Entering and exiting private mode
Before you can print any text or start to move the cursor around, you should enter what's called the _private mode_. In 
this mode the screen is cleared, scrolling is disabled and the previous content is stored away. When you exit private 
mode, the previous content is restored and displayed on screen again as it was before private mode was entered. Some 
systems/terminals doesn't support this mode at all, but will still perform some screen cleaning operations for you. 
It's always recommended to enter private mode when you start your GUI and exit when you finish.

### Example

    terminal.enterPrivateMode();
    ...
    terminal.exitPrivateMode();

## Moving the cursor
When you start your GUI, you can never make any assumptions on where the text cursor is. Since text printing will always
appear at the text cursor position, it is very important to be able to move this around. Here is how you do it:

    terminal.setCursorPosition(10, 5);

The first parameter is to which column (the first is 0) and the record to which row (again, first row is 0).

## Get the size of the terminal
In order to be able to make good decisions on moving the cursor, you might want to know how big the terminal is. The 
`Terminal` object will expose a `getTerminalSize()` method that will do precisely this.

    TerminalSize screenSize = terminal.getTerminalSize();

    //Place the cursor in the bottom right corner
    terminal.setCursorPosition(screenSize.getColumns() - 1, screenSize.getRows() - 1);


## Printing text
Printing text is another very useful operation, it's simple enough but a bit limited as you have to print character by 
character.

    terminal.setCursorPosition(10, 5);
    terminal.putCharacter('H');
    terminal.putCharacter('e');
    terminal.putCharacter('l');
    terminal.putCharacter('l');
    terminal.putCharacter('o');
    terminal.putCharacter('!');
    terminal.setCursorPosition(0, 0);

Notice that just like when you type in text manually, the cursor position will move one column to the right for every 
character you put. What happens after you put a character on the last column is undefined and may differ between 
different terminal emulators. You should always use the `moveCursor(..)` method to place the cursor somewhere else after 
writing something to the end of the row.

## Using colors
If you want to make your text a bit more beautiful, try adding some color! The underlying terminal emulator will keep a 
state of various modifiers, including which foreground color and which background color is currently active, so after 
modifying this state it will be applied to all text written until you change it again.

The following colors are the ANSI standard ones, most of which has a brighter version also available (see _bold_ mode):
  * BLACK
  * RED
  * GREEN
  * YELLOW
  * BLUE
  * MAGENTA
  * CYAN
  * WHITE
  * DEFAULT

Notice the default color, which is up to the terminal emulator to decide, you can't know what it will be. There are two
xterm color extensions supported by some terminal emulators which you can use through Lanterna (indexed mode and RGB) 
but avoid doing this as most terminal emulators won't understand them.

### Example

    terminal.setForegroundColor(TextColor.ANSI.RED);
    terminal.setBackgroundColor(TextColor.ANSI.BLUE);

    //Print something

    terminal.setForegroundColor(TextColor.ANSI.DEFAULT);
    terminal.setBackgroundColor(TextColor.ANSI.DEFAULT);

## Using text styles
More than color, you can also add certain styles to the text. How well this is supported depends completely on the 
client terminal so results may vary. Typically at least bold mode is supported, but usually not rendering the text in 
bold font but rather as a brighter color. These style modes are referred to as SGR and just like with colors the 
terminal emulator will keep their state until you explicitly change it. The following SGR are available in lanterna as 
of version 3:

 * `BOLD` - text usually drawn in a brighter color rather than in bold font
 * `REVERSE` - Reverse text mode, will flip the foreground and background colors
 * `UNDERLINE` - Draws a horizontal line under the text. Surprisingly not widely supported.
 * `BLINK` - Text will blink on the screen by alternating the foreground color between the real foreground color and the
background color. Not widely supported but your users will love it!
 * `BORDERED` - Draws a border around the text. Rarely supported.
 * `FRAKTUR` - I have no idea, exotic extension, please send me a reference screen shots!
 * `CROSSED_OUT` - Draws a horizontal line through the text. Rarely supported.
 * `CIRCLED` - Draws a circle around the text. Rarely supported.
 * `ITALIC` - Italic (cursive) text mode. Some terminals seem to support it, but rarely encountered in use 

Here is how you apply the SGR states:

    terminal.enableSGR(SGR.BOLD);
    
    //Draw text highlighted
    
    terminal.disableSGR(SGR.BOLD);   //or terminal.resetColorAndSGR()

The `Terminal` interface method `resetColorAndSGR()` is the preferred way to reset all colors and SGR states.

## Clearing the screen
If you want to completely reset the screen, you can use the `clearScreen()` method. This will (probably) remove all 
characters on the screen and replace them with the empty character (' ') having `TextColor.ANSI.DEFAULT` set as 
background.

    terminal.clearScreen();


## Flushing
To be sure that the text has been sent to the client terminal, you should call the `flush()` method on the `Terminal` 
interface when you have done all your operations.

## Read keyboard input
Finally, retrieving user input is necessary unless the GUI is totally hands-off. Normally, when the user presses a key, 
it will be added to an input buffer awaiting retrieval. You can poll this buffer by calling `pollInput()` on the 
`Terminal` object (blocking version also exists: `readInput()`), which will return you a `KeyStroke` object representing 
the key pressed, or null is no key has been pressed. The `pollInput()` method is actually not directly on the `Terminal` 
interface, but on another interface, `InputProvider`, which the `Terminal` interface extends. To interpret the 
`KeyStroke` returned, you first need to call `getKeyType()` which returns a `KeyType` enum value that tells you what 
kind of key was pressed. The `KeyType` enum has the following values as of Lanterna 3:
 * Character
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
 * Tab
 * ReverseTab
 * Enter
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
 * F13
 * F14
 * F15
 * F16
 * F17
 * F18
 * F19
 * Unknown
 * CursorLocation
 * MouseEvent
 * EOF

If you get the type `KeyType.Character`, the key is then a alpha-numerical-symbol key and it can be retrieved using the 
`KeyStroke` object's `getCharacter()` method. At the moment, there is no way to detect when the shift-key is pressed, 
you will see the result on `KeyStroke` objects read while the key is pressed though (characters will be in upper-case, 
numbers will turn into symbols, etc). You can detect if CTRL and/or ALT keys were pressed down while the keystroke 
happened, by using the `isAltDown()` and `isCtrlDown()` methods on `KeyStroke` (unfortunately, not all combinations
can be detected because of ambiguities that sometimes occur; if you want to use special key combinations in your 
program, please test with multiple terminals to make sure it's working as you intend). Unfortunately, you cannot detect 
CTRL or ALT as individual keystrokes due to how the terminal is designed to work

## Learn more
To learn about how to use an abstraction API in Lanterna built on top of the terminal API described in this page, 
continue reading at [Buffered screen API](using-screen.md).
