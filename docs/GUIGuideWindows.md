# Basic Windows

## Introduction
Normally, all windows you create in Lanterna are modal. That means, there is no window management involved. When you 
create and show one window, it will overlap all other windows and exclusively take input from the user. This window will 
remain in focus until either it's closed or another window is shown. There is no way to switch between windows without 
closing the currently focused. 

However, Lanterna *does* support multi-windows mode where each window is not necessarily modal. Due to the nature of the
terminal and how input works, there isn't any general standard for how to switch windows in a text console environment,
and as such Lanterna only provides programmatic support for switching active window. You'll have to register key 
listeners and do the switching yourself if you want to support this.

## The `Window` class
Like in Swing/AWT, you will probably want to subclass the `BasicWindow` class (implementing the `Window` interface)when
you create your own windows. This is not a strict requirement but can make it easier when coding. 

    public class MyWindow extends BasicWindow {
        public MyWindow() {
            super("WindowTitle");
        }
    }

To show your window, you use the `WindowBasedTextGUI` object you have and call the `addWindow`-method. 

    MyWindow myWindow = new MyWindow();
    textGUI.addWindow(myWindow);

This call will not block, the window is added to the GUI system and is ready to be drawn.

If your GUI system is configured with the default same-thread mode, your thread is responsible for telling lanterna when
to draw the GUI to the `Screen`. There is a lower-level way of doing this (`TextGUIThread#processEventsAndUpdate()`) but
here we will use something a little bit more intuitive:

    myWindow.waitUntilClosed();

In this case though, we haven't added any way to close the window so effectively the call above would never come back.
We'll add an exit button further down.

### Window hints
The window system uses a special `WindowManager` to figure out how to place the windows inside the screen. This has a 
reasonable default which will place windows in a traditional cascading pattern. To tell the window manager that you
would like something else for your window, rather than writing a custom window manager you can attach hints. There are
also other hints which are interpreted by the GUI system itself rather than the window manager. As of lanterna 3, these
are the available hints, which you can find in `Window.Hint`:

#### `NO_DECORATIONS`
With this hint, the `TextGUI` system should not draw any decorations around the window. Decorated size will be the same 
as the window size.

#### `NO_POST_RENDERING`
With this hint, the `TextGUI` system should skip running any post-renderers for the window. By default this means the 
window won't have any shadow.

#### `NO_FOCUS`
With this hint, the window should never receive focus by the window manager

#### `CENTERED`
With this hint, the window wants to be at the center of the terminal instead of using the cascading layout which is the 
standard.

#### `FIXED_POSITION`
Windows with this hint should not be positioned by the window manager, rather they should use whatever position is 
pre-set programmatically.

#### `FIXED_SIZE`
Windows with this hint should not be automatically sized by the window manager (using `getPreferredSize()`), rather 
should rely on the code manually setting the size of the window using `setSize(..)`

#### `FIT_TERMINAL_WINDOW`
With this hint, don't let the window grow larger than the terminal screen, rather set components to a smaller size than 
they prefer.

#### `MODAL`
This hint tells the window manager that this window should have exclusive access to the keyboard input until it is 
closed. For window managers that allows the user to switch between open windows, putting a window on the screen with 
this hint should make the window manager temporarily disable that function until the window is closed.

#### `FULL_SCREEN`
A window with this hint would like to be placed covering the entire screen. Use this in combination with 
`NO_DECORATIONS` if you want the content area to take up the entire terminal.

#### `EXPANDED`
This window hint tells the window manager that the window should be taking up almost the entire screen, leaving only a 
small space around it. This is different from `FULL_SCREEN` which takes all available space and completely hide 
the background and any other window behind it.

## Adding components
In order to make windows a bit useful, you'll need to add some components. Lanterna is using layout system greatly 
inspired by SWT and Swing/AWT based on `LayoutManager` implementations that are attached to container components. A 
window itself contains only one component so you probably want to set that component to a container of some sort so you
can show more than one component inside the window. 

The most simple layout manager to attach to a container is `LinearLayout`, which places all components that are added to
it in either a horizontal or a vertical line, one after another. You can customize this a little bit by using alignments
but this is very simple to use.

The basic components we will look at here (the rest can be found in the Components guide) are `Label`, `TextBox`, 
`Button` and `Panel`.

### Panel
Panels enable you to visually group together one or more components, but more importantly also gives you some command 
over the component layout. By default a panel will not have any border, but you can easily decorate it with one that
even has a title. Here's how you can create a couple of panels, laying them out horizontally.

    public class MyWindow extends BasicWindow {
        public MyWindow() {
            super("My Window!");
            Panel horizontalPanel = new Panel();
            horizontalPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
            Panel leftPanel = new Panel();
            Panel middlePanel = new Panel();
            Panel rightPanel = new Panel();
    
            horizontalPanel.addComponent(leftPanel);
            horizontalPanel.addComponent(Borders.singleLineBevel(middlePanel, "Panel Title"));
            horizontalPanel.addComponent(Borders.doubleLineBevel(rightPanel));
    
            // This ultimately links in the panels as the window content 
            setComponent(horizontalPanel);
        }
    }

### Label
Simple text labels are created with the `Label` class. The label can be a multi line String, separated by `\n`. The 
color of the text is determined by the current theme, but you can override this by calling `setForegroundColor` and
`setBackgroundColor` directly on the object.

Here is a simple example:

    public class MyWindow extends BasicWindow {
        public MyWindow() {
            super("My Window!");
            Panel contentPane = new Panel();
            contentPane.setLayoutManager(new LinearLayout(Direction.VERTICAL));
            contentPane.addComponent(new Label("This is the first label"));
            contentPane.addComponent(new Label("This is the second label, red").setForegroundColor(TextColor.ANSI.RED));
            contentPane.addComponent(new Label("This is the last label\nSpanning\nMultiple\nRows"));
            setComponent(contentPane);
        }
    }


### Button
Button is a component that the user can interact with, by pressing the return key when they are currently highlighted. 
Upon creation, you'll assign a label and an Action to a button; the Action will be executed by the GUI system's event 
processor when the user activates it. 

To add a button that closes its window and thereby allow up to break out from the `waitUntilClosed()` invocation above,
here is a simple example:

    public class MyWindow extends BasicWindow {
        public MyWindow() {
            super("My Window!");
            setComponent(new Button("Exit", new Runnable() {
                    @Override
                    public void run() {
                        MyWindow.this.close();
                    }
                }));
        }
    }

## Next
Continue to [Components](GUIGuideComponents.md)
