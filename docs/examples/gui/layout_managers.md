Layout Managers
---

Absolute Layout
---
A Layout manager that places components where they are manually specified to be and sizes them to the size they are  manually assigned to. When using the AbsoluteLayout, please use `setPosition()` and `setSize()` manually on each component to choose where to place them.

To set a given panel's layout to be absolute:

```
	panel.setLayoutManager(new AbsoluteLayout());
```

Linear Layout
---
A simple layout manager the puts all components on a single line. Linear Layout has two `Direction`'s, `HORIZONTAL` and `VERTICAL`.

```
	// To have the components sit next to each other
	panel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL))

	// To have the components sit on top of each other
	panel.setLayoutManager(new LinearLayout(Direction.VERTICAL))	
```


Border Layout
---
Imitates the BorderLayout class from AWT, allowing you to add a center component with optional components around it in top, bottom, left and right locations. The edge components will be sized at their preferred size and the center component will take up whatever remains.

In the following example, the text box will be full screen, centered and multiline:

```
	panel.setLayoutManager(new BorderLayout());

	// Sets the textbox to be in the center of the screen	
	TextBox textBox = new TextBox("", TextBox.Style.MULTI_LINE);
	textBox.setLayoutData(BorderLayout.Location.CENTER);
	
	//...

	// Tip: Sets the window to be full screen.
	window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN));
```


Grid Layout
---
This emulates the behaviour of the GridLayout in SWT (as opposed to the one in AWT/Swing).

To set a given panel's layout to be a grid layout:

```
	// Grid will have two columns
	panel.setLayoutManager(new GridLayout(2));

	// As this grid is a 2xN grid, each row must have 2 elements.
	// An empty space is added before the button to fill in the cell before the buttons cell.
	buttonPanel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
	buttonPanel.addComponent(new Button("Enter"));
```

Each time a component is added to the panel, it will be added to the grid in a left to right fashion, starting at 0x0. When the components get to the end of the column, it will start a new row.