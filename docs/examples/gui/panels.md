Panels
---

Panels are a way for you to split up your UI and components and group them into seperate sections, similar to JPanels in Swing.

To create a `Panel`:

```
	Panel panel = new Panel();
```

To add a component to a Panel:

```
	panel.addComponent(new Button("Enter"));
```

You can also nest panels:

```
	BasicWindow window = new BasicWindow();
	
	Panel mainPanel = new Panel();
	mainPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

	Panel leftPanel = new Panel();
	mainPanel.addComponent(leftPanel.withBorder(Borders.singleLine("Left Panel")));

	Panel rightPanel = new Panel();
	mainPanel.addComponent(rightPanel.withBorder(Borders.singleLine("Right Panel")));

	window.setComponent(Panels.vertical(mainPanel.withBorder(Borders.singleLine("Main Panel"))));
	textGUI.addWindow(window);
```

In the example above, the "Main Panel" holds two seperate panels: the "Left Panel" and the "Right Panel".

The left and right panel sit next to each other because the `Panels` utility class was used:

```
	Panel panel = Panels.vertical(mainPanel.withBorder(Borders.singleLine("Main Panel")));
```

The `Panels` class is a utility class for quickly bunching up components in a panel, arranged in a particular pattern.

### Screenshot

![](screenshots/panels.png)