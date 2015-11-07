Component Sizing
---

Throughout the GUI layer, the `TerminalSize` class is used to represent how many rows and columns a given component takes up on screen.

For example, to set the on screen size of a `Panel`, call the `setPreferredSize` method and pass in a new `TerminalSize`:

```
	Panel panel = new Panel();
	panel.setPreferredSize(new TerminalSize(40, 2));
```

You can also pass a `TerminalSize` object into the constructor of many GUI components, for example, the `TextBox` component:

```
	// Creates a textbox 40 columns long, 1 row high
	TextBox textBox = new TextBox(new TerminalSize(40, 1))

```