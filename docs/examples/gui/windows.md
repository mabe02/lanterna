Windows
---

Windows are there to hold your components.

To create a basic window:

```
	// Setup terminal and screen layers
	Terminal terminal = new DefaultTerminalFactory().createTerminal();
	Screen screen = new TerminalScreen(terminal);
	screen.startScreen();

	// Create window to hold the panel
	BasicWindow window = new BasicWindow();

	// Create gui and start gui
	MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
	gui.addWindowAndWait(window);
```

This will create a basic window, ready for you to fill with components:

What if you wanted to create a full screen window? No problem:

```
	BasicWindow window = new BasicWindow();
	window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN));
```

You can combine many hints together, to customise how your window should look:

```
	// Full screen without the border
	BasicWindow window = new BasicWindow();
	window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN, Window.Hint.NO_DECORATIONS));
```

How about a center screen window?

```
	BasicWindow window = new BasicWindow();
	window.setHints(Arrays.asList(Window.Hint.CENTERED));
```

Experiment with a number of `Hint`s to get the `Window` you want.