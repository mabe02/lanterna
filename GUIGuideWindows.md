### Introduction ###
In the Lanterna GUI system, all windows are modal. That means, there is no window management involved. When you create and show one window, it will overlap all other windows and exclusively take input from the user. This window will remain in focus until either it's closed or another window is shown. There is no way to switch between windows without closing the currently focused. However, you can re-use a window to display it once again after it's been closed.

### The `Window` class ###
Like in Swing/AWT, you will probably want to subclass the `Window` class when you create your own windows. This is not a strict requirement but it's recommended. The only thing you need to specify when calling the super-constructor is the title of the window.

```
import com.googlecode.lanterna.gui.*;

public class MyWindow extends Window
{
    public MyWindow()
    {
        super("My Window!");
    }
}
```

To show your window, you use the `GUIScreen` object you have and call the `showWindow`-method. This method accepts a second parameter too, which will be the placement of the window. You have three choices:
  * `GUIScreen.Position.CENTER` - This will put the window centered on the screen.
  * `GUIScreen.Position.NEW_CORNER_WINDOW` - This will put the window in the top left corner and all following windows using `OVERLAPPING` will adjust from here.
  * `GUIScreen.Position.OVERLAPPING` - This will put the windows in a stack, starting in the top left corner and going down-right.

```
    MyWindow myWindow = new MyWindow();
    guiScreen.showWindow(myWindow, GUIScreen.Position.CENTER);
```

<insert screenshot here>

#### Solo windows ####
If a window is set to be solo, it means when it's shown all the windows behind it won't be rendered, giving the looks it's the only window on the screen. This can be useful if a lot of windows are open and the screen starts to look cluttered or it becomes hard to make out each window from the other. When a window is to be rendered, the GUI system will check if there are any solo windows on top of it. If there are, then window is not rendered. This way, you can open new windows on top of a solo window and still keep the windows behind the solo window invisible. Bringing a new solo window on top of the stack will again hide all other windows behind it.

### Adding components ###
In order to make windows a bit useful, you'll need to add some components. Lanterna is using a rather simple layout management system which basically works in rows and columns, using a _prefered size_ attribute to determine how big to make the window. When you add components to a window, they will line up vertically. You can create and nest panels with either horizontal or vertical layouts to create more complicated content.

The basic components we will look at here (the rest can be found in the Components guide) are `Label`, `TextBox`, `Button` and `Panel`.

#### Panel ####
Panels enable you to visually group together one or more components, but more importantly also gives you some command over the component layout. By default a panel will have a simple border, but you can suppress this by using an invisible border. Here's how you can create a couple of panels, laying them out horisontally.
```
import com.googlecode.lanterna.gui.*;

public class MyWindow extends Window
{
    public MyWindow()
    {
        super("My Window!");
        Panel horisontalPanel = new Panel(new Border.Invisible(), Panel.Orientation.HORIZONTAL);
        Panel leftPanel = new Panel(new Border.Bevel(true), Panel.Orientation.VERTICAL);
        Panel middlePanel = new Panel(new Border.Bevel(true), Panel.Orientation.VERTICAL);
        Panel rightPanel = new Panel(new Border.Bevel(true), Panel.Orientation.VERTICAL);

        horisontalPanel.addComponent(leftPanel);
        horisontalPanel.addComponent(middlePanel);
        horisontalPanel.addComponent(rightPanel);

        addComponent(horisontalPanel);
    }
}
```

#### Label ####
Simple text labels are created with the `Label` class. The label can be a multi line String, separated by \n. By default, the label will be dynamically resized depending on the length of the String, but you can also force a width by using a specific constructor for this. Also by default, the color that the String is rendered with will be whatever is the "DefaultDialog" in the current Theme, but you can also set a specific color.

Here is a simple example:
```
public class MyWindow extends Window
{
    public MyWindow()
    {
        super("My Window!");
        addComponent(new Label("This is the first label"));
        addComponent(new Label("This is the second label, red", Terminal.Color.RED));
        addComponent(new Label("This is the third label, fixed 50 columns", 50));
        addComponent(new Label("This is the last label\nSpanning\nMultiple\nRows"));
    }
}
```

#### Button ####
Buttons are selectable items that the user can interact with, by pressing the return key when they are selected. Upon creation, you'll assign a label and an Action to a button; the Action will be executed by the GUI system's event thread when the user activates it. Because of this, it is perfectly safe to open new windows or close the current one in the Action.

Here is a simple example:
```
public class MyWindow extends Window
{
    public MyWindow()
    {
        super("My Window!");
        addComponent(new Button("Button with no action"));
        addComponent(new Button("Button with action", new Action() {
                @Override
                public void doAction() {
                   MessageBox.showMessageBox(getOwner(), "Hello", "You selected the button with an action attached to it!");
                }
            }));
    }
}
```