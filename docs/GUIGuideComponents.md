# Components
This guide explains Lanterna’s GUI widgets for building terminal UIs. 

## ActionListBox

The `ActionListBox` is a specialized list component that displays a vertical list of labeled actions. Each item is backed by a `Runnable` that is invoked when the item is activated by the user (keyboard or mouse).

Use it when you want to present a compact, keyboard‑friendly menu of actions inside a window or panel.

Key points:
- Extends `AbstractListBox<Runnable, ActionListBox>`; it behaves like a regular list box for navigation and selection, but activation runs the selected `Runnable`.
- Activation keys: Enter or Space. Mouse click on an item also runs it. Standard list navigation (arrows, PageUp/PageDown, Home/End) applies as with `ListBox`.
- Sizing: If you pass a preferred size, scrollbars appear automatically when the content exceeds that size.
- Labeling: If you add a raw `Runnable`, the list label is `runnable.toString()`. Prefer `addItem(String label, Runnable action)` for user‑friendly labels.
- Threading note: `ActionListBox` simply calls `Runnable.run()`. If your action does long‑running work, execute it on a background thread to keep the UI responsive.

Constructors:
- `ActionListBox()` — requests just enough space to fit all items (subject to the container layout).
- `ActionListBox(TerminalSize preferredSize)` — forces a specific size; overflow is handled via scrollbars.

Core API:
- `addItem(Runnable runnable)` — adds an item; label is `runnable.toString()`.
- `addItem(String label, Runnable action)` — adds an item with an explicit label.
- `runSelectedItem()` — programmatically runs the currently selected item (no-op if none selected).

Basic example:
```
TerminalSize size = new TerminalSize(20, 8);
ActionListBox list = new ActionListBox(size);

list.addItem("Open", () -> doOpen());
list.addItem("Save", () -> doSave());
list.addItem("Quit", () -> System.exit(0));

Panel panel = new Panel();
panel.addComponent(list.withBorder(Borders.singleLine("Actions")));

window.setComponent(panel);
textGUI.addWindowAndWait(window);
```

Tips:
- For actions that need parameters or dynamic labels, create small `Runnable` instances or lambdas that close over required values; override `toString()` if you use `addItem(Runnable)` directly.
- To ensure a specific visible height regardless of item count, prefer the size‑taking constructor, e.g., `new ActionListBox(new TerminalSize(cols, rows))`.
- You can set layout data on the list (e.g., `LinearLayout` or `GridLayout`) just like any other component.

Related components and dialogs:
- `ActionListDialog` and `ActionListDialogBuilder` wrap an internal `ActionListBox` to present actions in a modal dialog.
- `FileDialog` and `DirectoryDialog` use `ActionListBox` to list files/directories.
- `ComboBox` uses an internal `ActionListBox` to render and interact with its popup list.

## AnimatedLabel

An `AnimatedLabel` is a specialized `Label` that cycles through a list of text frames on a timer. It is useful for simple, non-blocking visual feedback such as “spinner” indicators while background work is in progress.

### Key features
- Frames: You provide one or more frames (each frame is a string; multi-line frames are supported).
- Auto-sizing: Preferred size accounts for the maximum width/height across all frames to avoid layout jitter.
- Timer-managed: Internally schedules a shared `Timer` and periodically advances frames.
- Lifecycle-aware: Stops automatically when removed from its container or when it no longer has a base pane.

### Basic usage
```
// Create a classic spinner and start it immediately (default ~150 ms between frames)
AnimatedLabel spinner = AnimatedLabel.createClassicSpinningLine();

// Add to your layout
Panel content = new Panel();
content.addComponent(spinner);

// ... later, when the operation finishes
spinner.stopAnimation();
```

### Custom animation
```
// Start with the first frame
AnimatedLabel anim = new AnimatedLabel("Loading");

// Add more frames; multi-line is allowed
anim.addFrame("Loading.")
    .addFrame("Loading..")
    .addFrame("Loading...");

// Start the animation at a custom speed (milliseconds per frame)
anim.startAnimation(200);

// Add to a container as usual
panel.addComponent(anim);
```

Notes:
- You can call `addFrame(String)` any number of times before or after adding the component to a container.
- Use `startAnimation(long millisecondsPerFrame)` to begin advancing frames; use `stopAnimation()` to halt it.
- `nextFrame()` is public for manual control, but you typically won’t need to call it when the timer is running.

### Behavior and lifecycle details
- Preferred size: `AnimatedLabel` computes the maximum preferred size across all frames, preventing the layout from changing as frames advance.
- Automatic stop: When the component is removed from its container (`onRemoved`) or if the component loses its base pane, the animation is stopped and the shared timer may be reclaimed when unused.
- Shared timer: All `AnimatedLabel` instances share a single internal `Timer`. It is automatically created on first start and shut down when no animations remain. Instances are tracked with weak references to help avoid leaks.

### Theming
`AnimatedLabel` inherits styling from `Label`. Apply themes and styles to it as you would a regular `Label`.

### When to use
- Showing progress feedback where a full `ProgressBar` is unnecessary.
- Indicating background activity in dialogs or panels.

### Tips
- Keep frame strings short and consistent in width for smoother visuals.
- For multi-line animations, ensure each frame has matching line counts where possible to avoid visual jumps.

## Button

The `com.googlecode.lanterna.gui2.Button` is a simple, focusable, labeled component that users can trigger with Enter or Space when the button has input focus.

### Creating buttons

There are two primary constructors:

```java
import com.googlecode.lanterna.gui2.Button;

public class ButtonCreateExample {
    void demo() {
        // No action initially; add listeners later
        Button b1 = new Button("OK");

        // With an initial action (Runnable) that runs when the button is triggered
        Button b2 = new Button("Save", () -> doSave());
    }

    private void doSave() {
        // ...
    }
}
```

You can update the label later:

```java
import com.googlecode.lanterna.gui2.Button;

public class ButtonLabelExample {
    void rename(Button b1) {
        b1.setLabel("Confirm");
        String current = b1.getLabel();
    }
}
```

### Attaching actions (listeners)

Buttons notify `Button.Listener` instances when they are triggered. Listeners are called serially in the order they were added.

```java
import com.googlecode.lanterna.gui2.Button;

public class ButtonListenerExample {
    void attach() {
        Button btn = new Button("Run");

        Button.Listener listener = button -> System.out.println("Triggered: " + button);

        // Add a listener
        btn.addListener(listener);

        // Remove a listener later if needed
        boolean removed = btn.removeListener(listener);
    }
}
```

If you used the `Button(String, Runnable)` constructor, Lanterna internally adds a listener that runs the provided `Runnable`.

### Triggering and focus/keyboard behavior

- Activation keystrokes: Enter and Space. The button must be focused for activation to be handled.
- Navigation and focus are governed by the surrounding `TextGUI` and layout; Tab/Shift+Tab typically move focus between interactables.
- When focused, the button’s theme style changes to the “active/selected” variants as described by the current `Theme`.

### Rendering and sizing

The button has pluggable renderers. The default renderer draws the label on a single line with lightweight borders using theme-provided characters (typically `<` and `>`). The label is horizontally centered when there is extra space.

Built-in renderers:

- `Button.DefaultButtonRenderer` (default):
    - Preferred size is at least 8 columns, or `labelWidth + 2`, height 1.
    - Renders a left and right border character from the theme (`LEFT_BORDER`, `RIGHT_BORDER`).
    - Shows a caret position (cursor) over the first label character when the theme exposes a visible cursor.
- `Button.FlatButtonRenderer`:
    - Minimal decoration; preferred width equals the label width, height 1.
    - No cursor position is reported.
- `Button.BorderedButtonRenderer`:
    - Draws a box with single-line border and a subtle shadow; preferred size is `labelWidth + 5` by 4 rows.

You can switch renderer per instance:

```java
import com.googlecode.lanterna.gui2.Button;

public class ButtonRendererExample {
    void demo() {
        Button flat = new Button("Flat").setRenderer(new Button.FlatButtonRenderer());
        Button boxed = new Button("Boxed").setRenderer(new Button.BorderedButtonRenderer());
    }
}
```

Themes can also alter visual aspects (styles and, for the default renderer, the border characters). See the theming guide for details.

### Layout and usage example

```java
import com.googlecode.lanterna.gui2.*;

public class ButtonLayoutExample {
    public static void main(String[] args) {
        // Assume a TextGUI is available
        WindowBasedTextGUI gui = obtainGui();

        Panel content = new Panel(new GridLayout(2));

        Button ok = new Button("OK", () -> gui.getActiveWindow().close());
        Button cancel = new Button("Cancel", () -> System.out.println("Canceled"));

        content.addComponent(ok);
        content.addComponent(cancel);

        BasicWindow window = new BasicWindow("Example");
        window.setComponent(content);
        gui.addWindowAndWait(window);
    }

    private static WindowBasedTextGUI obtainGui() {
        // Provide a concrete TextGUI in your application
        return null;
    }
}
```

### Notes and caveats for developers

- Labels cannot be null; an empty label is converted to a single space.
- Multicolumn characters (e.g., some Unicode) are accounted for using the label’s column width when computing preferred sizes and center alignment.
- Programmatic triggering is performed internally in response to activation keystrokes; external code should normally attach listeners or supply a `Runnable` rather than trying to call protected internals.

## CheckBox

The `CheckBox` is a simple on/off toggle with an optional text label, similar to check boxes in graphical UIs. It renders as `[ ]` plus your label, and shows an `X` inside the brackets when checked.

### Creating and adding to a container
- No-label checkbox (unchecked by default):
```
CheckBox cb = new CheckBox();
```
- With a label:
```
CheckBox cb = new CheckBox("Enable feature");
```
- Add to a `Panel` (or any container) just like other components:
```
Panel panel = new Panel();
panel.addComponent(new CheckBox("Enable logs"));
```

Notes:
- Labels must be single-line; passing a label containing `\n` or `\r` throws `IllegalArgumentException`.
- `null` labels are not allowed.

### Programmatic state and label
- Read state: `cb.isChecked()`
- Set state: `cb.setChecked(true /* or false */)`
- Read/modify label: `cb.getLabel()`, `cb.setLabel("New label")`

`setChecked(...)` updates the UI and notifies listeners (see below). Listener notifications are delivered on the GUI thread when available.

### User interaction
- Keyboard: pressing Space or Enter toggles the check box when it has focus.
- Mouse: clicking the check box toggles it and requests focus.

### Listening to state changes
Register a listener to be notified whenever the user (or your code) changes the checked state:
```
CheckBox cb = new CheckBox("Send telemetry");
cb.addListener(checked -> {
    // React to the new state
    System.out.println("Telemetry is now " + (checked ? "ON" : "OFF"));
});

// Later, to stop listening
cb.removeListener(myListener);
```

The listener interface is `CheckBox.Listener` with a single method `onStatusChanged(boolean checked)`.

### Rendering, size, and theming
- Default rendering: `[ ]` followed by a space and the label; shows `X` when checked.
- Preferred size: one row high; width is `3 + (label.isEmpty() ? 0 : 1 + labelColumnWidth)`.
- Theming: uses the component theme; when focused it applies the theme’s “active” style, otherwise “normal”.

Advanced: To completely customize appearance, subclass `CheckBox` and override `createDefaultRenderer()` to return a custom renderer (`CheckBoxRenderer`). You can control cursor position, preferred size, and drawing.

### Minimal working example
```
WindowBasedTextGUI gui = ...; // See using-gui.md for setup

Panel content = new Panel();
content.addComponent(new Label("Options:"));

CheckBox verbose = new CheckBox("Verbose output");
verbose.addListener(checked -> {
    // Enable/disable verbose logging
});

content.addComponent(verbose);

BasicWindow window = new BasicWindow("Demo");
window.setComponent(content);
gui.addWindowAndWait(window);
```

## CheckBoxList

CheckBoxList is a list component where each item has an independent checked state. It allows selecting any number of items and provides both keyboard and mouse interaction for toggling items.

### Creating a CheckBoxList

```
TerminalSize size = new TerminalSize(20, 8); // optional preferred size
CheckBoxList<String> list = new CheckBoxList<>(size);

// Without preferred size (grows to fit items):
// CheckBoxList<String> list = new CheckBoxList<>();
```

### Adding items

```
list.addItem("Item A");                   // unchecked by default
list.addItem("Item B", true);            // explicitly checked
list.addItem("Item C", false);
```

Items are generic, their `toString()` value is used for rendering the label by default.

### Querying and changing state

```
// Read state
Boolean checkedByIndex = list.isChecked(1);    // null if index out of range
Boolean checkedByValue = list.isChecked("Item B"); // null if not found

// Toggle / set state
list.toggleChecked(0);          // flips the state of item at index 0
list.setChecked("Item C", true); // set by value (no-op if not present)

// Get all checked items as a list of values
List<String> selected = list.getCheckedItems();

// Remove / clear items (states are kept in sync)
list.removeItem(1);
list.clearItems();
```

### Events (listeners)

Register a listener to be notified when the user changes an item’s state.

```
list.addListener((index, checked) -> {
    System.out.println("Item at index " + index + " is now " + (checked ? "checked" : "unchecked"));
});

// Later
list.removeListener(theSameListenerInstance);
```

Listeners are invoked on the GUI thread when the user toggles items (keyboard or mouse) or when code calls `setChecked`/`toggleChecked` on the GUI thread.

### Keyboard interaction

- Enter/Space (keyboard activation stroke): Toggle the currently selected item.
- Arrow keys/PageUp/PageDown/Home/End: Navigate items (provided by ListBox navigation).

### Mouse interaction

- Click on an item to toggle its state.
- Click-drag across multiple items: all items passed over will be set to the same state as the first item clicked.
- Mouse wheel scroll: scrolls the list (does not toggle items).

### Rendering and theming

By default, items are rendered like: `[x] Label` when checked and `[ ] Label` when unchecked. The default renderer is `CheckBoxList.CheckBoxListItemRenderer`.

Theme keys for `com.googlecode.lanterna.gui2.CheckBoxList` include:

- Characters
    - `char[LEFT_BRACKET]` (default `[`)
    - `char[RIGHT_BRACKET]` (default `]`)
    - `char[MARKER]` (default `x`)
- Booleans
    - `property[FIXED_BRACKET_COLOR]` — draw brackets with `preLight` style
    - `property[CLEAR_WITH_NORMAL]` — clear line with `normal` before drawing item
    - `property[MARKER_WITH_NORMAL]` — draw marker with `normal` style instead of item style
    - `property[HOTSPOT_PRELIGHT]` — prelight the `[ ]` hotspot when selected and focused

Style states used: `normal`, `selected`, `active`, `insensitive`, `preLight` (for hotspot or brackets when configured).

To change the label formatting or hotspot position, supply a custom `ListItemRenderer` by overriding `createDefaultListItemRenderer()` in a subclass or via `setListItemRenderer` on the instance.

### Example: using in a layout

```
Panel panel = new Panel();
panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

CheckBoxList<String> features = new CheckBoxList<>(new TerminalSize(24, 6));
features.addItem("Logging", true);
features.addItem("Metrics");
features.addItem("Tracing");

panel.addComponent(features.withBorder(Borders.singleLine("Features")));
```

## ComboBox

The `ComboBox<V>` is a drop-down selection component. In read-only mode it lets users pick one item from a list. In editable mode it also allows free-text entry similar to a `TextBox` while still offering the drop-down list for quick selection.

### Creating and populating
- Empty, default (read-only) combo box:
```
ComboBox<String> combo = new ComboBox<>();
```
- From a collection (first item selected if not empty):
```
ComboBox<String> combo = new ComboBox<>(List.of("Item 1", "Item 2"));
```
- With explicit initial text (no selection until the user picks one):
```
ComboBox<String> combo = new ComboBox<>("Type here…", List.of("Item 1", "Item 2"));
// getSelectedIndex() will return -1 initially
```
- With explicit initial selection index:
```
ComboBox<String> combo = new ComboBox<>(List.of("A", "B", "C"), 1); // selects "B"
```

Populate and manage items (nulls are not allowed and will throw `IllegalArgumentException`):
```
combo.addItem("Item 3");
combo.addItem(0, "First");
combo.setItem(1, "Second (updated)");
combo.removeItem("Item 3");
combo.removeItem(0);
combo.clearItems();
int count = combo.getItemCount();
String item = combo.getItem(0);
```

### Read-only vs editable
- Default is read-only. To allow free text editing:
```
combo.setReadOnly(false);
```
- Reading user text and selection:
```
String text = combo.getText();           // current text in the field
int index = combo.getSelectedIndex();    // -1 if no selection
String selected = combo.getSelectedItem(); // null if no selection
```
- Programmatic selection (fires listener with `changedByUserInteraction=false`):
```
combo.setSelectedIndex(2);
combo.setSelectedItem("Item 2");
```
- Update only the text (does not change selection):
```
combo.updateText("new text");
```

### Events
Register a `ComboBox.Listener` to observe selection changes:
```
combo.addListener((selectedIndex, previousIndex, changedByUser) -> {
    // respond to selection change
});

combo.removeListener(listener);
```

The `changedByUser` flag is `true` when the user changes selection via keyboard/mouse interaction and `false` when changed programmatically.

### Sizing and drop-down rows
- Set preferred size of the field (height is typically `1`):
```
combo.setPreferredSize(new TerminalSize(15, 1));
```
- Control how many rows the drop-down shows before scrolling (default 10):
```
combo.setDropDownNumberOfRows(15);
```

### Keyboard and mouse interaction (default renderer)
- Read-only:
    - Enter/typing or mouse click opens the drop-down.
    - Arrow Up/Down navigates items; Enter selects.
- Editable:
    - Typing edits the text; Left/Right moves the text cursor.
    - Tab switches focus between text field and drop-down navigation.
    - Arrow Up/Down while in text mode changes the selection; while in drop-down focus, behaves like read-only.
    - Backspace/Delete edit text.

### Theming
Theme keys (component class: `com.googlecode.lanterna.gui2.ComboBox`) that themes may provide:
- `foreground`, `background`, `sgr` for states like `PRELIGHT`, `SELECTED`, `INSENSITIVE`, `ACTIVE`.
- Some themes may also define `cursor` visibility.

See the provided themes under `src/main/resources/*-theme.properties` for examples.

### Example and screenshots
See detailed example: `docs/examples/gui/combo_boxes.md`.

Non-activated:
![](examples/gui/screenshots/combo_box.png)

Activated:
![](examples/gui/screenshots/combo_box_activated.png)

## EmptySpace
`com.googlecode.lanterna.gui2.EmptySpace` is a simple, lightweight component that paints a solid background over its area. It is useful as a spacer/filler in layouts or as a colored block behind other content.

### Key points
- Preferred size is fixed by constructor
    - `new EmptySpace()` defaults to `1x1`.
    - `new EmptySpace(TerminalSize size)` lets you define the preferred size.
    - Layout managers may stretch or shrink it to fit, but its reported preferred size is exactly what you pass in.
- Color
    - By default, the background color comes from the current theme (`getThemeDefinition().getNormal()`).
    - You can override the background with:
        - `new EmptySpace(TextColor color)`
        - `new EmptySpace(TextColor color, TerminalSize size)`
        - Or later via `setColor(TextColor color)` (set to `null` to return to theme-driven color).
- Rendering behavior
    - Applies the component’s theme style, optionally sets the explicit background color, then fills its bounds with the space character.
    - It does not render any text or borders on its own.

### Typical use cases
- Add flexible gaps around or between components in a `Panel`.
- Create visual padding/margins when a layout manager doesn’t provide built-in spacing.
- Draw a block of background color to separate UI regions.

### Examples
Basic spacer with theme color:
```java
Panel content = new Panel();
content.setLayoutManager(new LinearLayout(Direction.VERTICAL));

content.addComponent(new Label("Header"));
content.addComponent(new EmptySpace(new TerminalSize(1, 1))); // 1 line gap
content.addComponent(new Label("Body"));
```

Colored block with fixed size:
```java
TextColor brand = TextColor.ANSI.BLUE;
EmptySpace block = new EmptySpace(brand, new TerminalSize(10, 3));

Panel row = new Panel(new LinearLayout(Direction.HORIZONTAL));
row.addComponent(new Label("Left"));
row.addComponent(new EmptySpace(new TerminalSize(2, 1))); // small gap
row.addComponent(block); // blue 10x3 rectangle
```

Changing color at runtime (revert to theme with `null`):
```java
EmptySpace spacer = new EmptySpace(new TerminalSize(1, 2));
spacer.setColor(TextColor.ANSI.GREEN);
// ... later
spacer.setColor(null); // back to theme color
```

### Notes on layout interaction
- In `LinearLayout`, `EmptySpace` behaves like any other component: it reports its preferred size; the layout may expand/compress it depending on available space and the other components’ needs.
- If you need a visually empty area that must always occupy a certain size, construct `EmptySpace` with that `TerminalSize` and ensure your chosen layout manager and container constraints allow that space.

## ImageComponent

`com.googlecode.lanterna.gui2.ImageComponent` is a lightweight component that displays a `TextImage` inside the GUI. It is useful when you want to render pre‑composed text/graphics (ASCII art, logos, thumbnails, or any off‑screen drawing) as a single component.

Key characteristics:
- Content: Renders a `TextImage` at the component's top‑left corner.
- Sizing: Preferred size equals the underlying image size (`TextImage.getSize()`). It does not scale the image.
- Cursor: Returns `null` for cursor location, so Lanterna hides the cursor over this component.
- Interaction: Extends `AbstractInteractableComponent`, but by default only basic focus navigation is handled; no component‑specific key handling is implemented. You can subclass to add custom input behavior.

### Creating and showing an ImageComponent

```
// Create some image content (for example, from strings)
String[] ascii = new String[] {
    "-========-",
    "|  LAN  |",
    "| TERNA |",
    "-========-"
};

// Convert to a TextImage
TextImage image = BasicTextImage.newBuilder()
        .fromLines(ascii)
        .build();

// Wrap it in an ImageComponent
ImageComponent imageComponent = new ImageComponent();
imageComponent.setTextImage(image);

// Add to your layout like any other component
Panel panel = new Panel(new GridLayout(1));
panel.addComponent(imageComponent.withBorder(Borders.singleLine("Logo")));
```

Notes:
- If you build the image manually, you can also use `new BasicTextImage(width, height)` and draw onto it with `TextGraphics`. When finished, call `imageComponent.setTextImage(image)`.
- After calling `setTextImage`, the component invalidates itself and will be re‑laid out/repainted automatically.

### Building a TextImage programmatically

```
int w = 10, h = 3;
BasicTextImage img = new BasicTextImage(w, h);
TextGraphics g = img.newTextGraphics();
g.setBackgroundColor(TextColor.ANSI.BLUE);
g.setForegroundColor(TextColor.ANSI.WHITE);
g.fill(' ');
g.putString(2, 1, "Hello");

ImageComponent comp = new ImageComponent();
comp.setTextImage(img);
```

### Layout and borders

- Since preferred size equals the image size, wrapping the component with a border or placing it in layouts that add insets will increase the total occupied size accordingly.
- If you need the component to expand, place it in a layout that enforces size or surround it with an expanding container, but be aware that the image will not stretch; extra space will remain empty around it.

### Custom interaction

`ImageComponent` handles only navigation keys via `AbstractInteractableComponent`. To react to key presses, subclass and override `handleKeyStroke(KeyStroke)`:

```
ImageComponent interactiveImage = new ImageComponent() {
    @Override
    public Result handleKeyStroke(KeyStroke key) {
        Result r = super.handleKeyStroke(key);
        if (r != Result.UNHANDLED) return r;
        // Custom behavior here
        // e.g., swap to a different TextImage on ENTER
        if (key.getKeyType() == KeyType.Enter) {
            setTextImage(/* another TextImage */);
            return Result.HANDLED;
        }
        return Result.UNHANDLED;
    }
};
```

### Typical use cases

- Displaying static ASCII art or logos.
- Showing a small preview/thumbnail generated elsewhere in the UI.
- Rendering off‑screen drawings (via `TextGraphics`) and inserting the result into layouts.

## Label
Label is a simple read-only text component used to display one or more lines of text. It supports:

- Multi-line text (splitting on \n, \r is ignored)
- Word-wrapping or truncation depending on configuration and available size
- Theme integration (colors and styles) with optional per-label overrides

### Basics
- Create with `new Label(String text)`.
- Update text using `setText(String text)`; retrieve with `getText()`.
- Line breaks: `setText` normalizes line endings by removing `\r` and splitting on `\n`.

  // Basic usage
  Panel content = new Panel();
  content.addComponent(new Label("Hello, world!"));

  // Multi-line label
  content.addComponent(new Label("Line 1\nLine 2\nLine 3"));

### Sizing and wrapping
Label calculates a preferred size from its content. You can influence width and wrapping with `setLabelWidth(Integer)`:

- null (default behavior for preferred size): No width limit when computing preferred size; preferred width equals the longest line. At draw time, if the actual allocated width is smaller, text will be truncated to fit.
- 0: Preferred size behaves as with null, but at draw time the label applies word-wrapping to fit the allocated width instead of truncating.
- Positive integer n: Preferred width is capped at n and preferred height grows to fit the word-wrapped lines. At draw time, the content is wrapped to the allocated width.

Notes:
- Actual wrapping is also influenced by the container’s layout manager and the space assigned at render time.
- When truncation applies (no wrapping and insufficient width), the label draws a fitted string for the available columns.

  Label noWrap = new Label("A long line that may not fit");
  noWrap.setLabelWidth(null); // preferred width = longest line; will truncate if space is too small

  Label wrapOnRender = new Label("A long line that will wrap when needed");
  wrapOnRender.setLabelWidth(0); // same preferred width as above, but wraps at draw time

  Label wrappedPreferred = new Label("This text will be word-wrapped to 20 columns when computing preferred size");
  wrappedPreferred.setLabelWidth(20); // preferred size accounts for wrapping

### Colors and styles
Labels use the current theme by default. You can override foreground/background colors and enable additional SGR styles.

- `setForegroundColor(TextColor)` / `setBackgroundColor(TextColor)`: Set to null to clear the override and use the theme again.
- `addStyle(SGR)` / `removeStyle(SGR)`: Adds/removes additional styles (for example, `SGR.BOLD`, `SGR.UNDERLINE`). Styles provided by the theme remain unless you change the theme.

  Label styled = new Label("Important");
  styled
  .setForegroundColor(TextColor.ANSI.RED)
  .setBackgroundColor(TextColor.ANSI.BLACK)
  .addStyle(SGR.BOLD)
  .addStyle(SGR.UNDERLINE);

### Rendering behavior (summary)
- Preferred size equals the measured bounds of the label’s content, considering `labelWidth` as described above.
- When drawing:
    - If `labelWidth == null`: draws original lines; truncates lines if the allocated width is smaller than the preferred width.
    - Otherwise (0 or positive): wraps content to the allocated width before drawing.
- Theme style is applied first; any specified per-label colors and added SGR modifiers are then applied on top.

## Panel

Panels are the primary container component in Lanterna’s GUI. A `Panel` can host multiple child components (including other panels) and delegates sizing and positioning to a `LayoutManager`.

### Key properties and behavior
- Default layout: If you don’t specify one, a `Panel` uses a vertical `LinearLayout` by default. If you pass `null` to the constructor or `setLayoutManager`, it falls back to `AbsoluteLayout`.
- Child management: Use `addComponent(...)`, `addComponent(index, ...)`, and `removeComponent(...)`. `removeAllComponents()` clears the panel. Adding a component automatically detaches it from any previous parent panel.
- Layout data: Use `addComponent(component, layoutData)` or `component.setLayoutData(layoutData)` to pass per‑component layout hints required by the current layout manager (`LinearLayout`, `GridLayout`, `BorderLayout`, etc.).
- Theming/fill: Panels don’t draw content themselves except for filling background. The default renderer clears the area and fills it using the theme’s normal style. You can override the background fill color via `setFillColorOverride(TextColor)`. Set to `null` to revert to the theme.
- Focus traversal: A `Panel` participates in focus traversal. It will traverse its children and nested containers using `nextFocus(...)`/`previousFocus(...)`. Non‑visible components are skipped, and disabled/unfocusable interactables are ignored.
- Preferred size/invalidation: The preferred size is derived from the layout manager and cached. Any structural change (adding/removing children, layout changes) calls `invalidate()`, which will trigger relayout and preferred size recomputation when needed.
- Rendering control: The default renderer has an internal flag to skip clearing the area first. This is useful only for advanced custom drawing; typical usage keeps the default behavior.
- Borders: Panels don’t have a visual frame by themselves. Wrap any component (including a panel) with a border using the `withBorder(...)` helper, for example `panel.withBorder(Borders.singleLine("Title"))`.

### Common layout managers to use with Panel
- LinearLayout: Simple vertical or horizontal stacking. Optionally control alignment and size allocation via `LinearLayout` layout data.
- GridLayout: Table‑like placement in rows/columns; each child can specify constraints such as horizontal/vertical span.
- BorderLayout: Place components in `Location.TOP`, `BOTTOM`, `LEFT`, `RIGHT`, or `CENTER`.
- AbsoluteLayout: Manual positioning/sizing; used when you need explicit coordinates.

### Quick examples

#### Vertical stack with gaps
```java
Panel panel = new Panel(); // defaults to LinearLayout vertical
panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
panel.addComponent(new Label("Name"));
panel.addComponent(new TextBox());
panel.addComponent(new Button("Save"));
```

#### Horizontal toolbar
```java
Panel toolbar = new Panel(new LinearLayout(Direction.HORIZONTAL));
toolbar.addComponent(new Button("New"));
toolbar.addComponent(new Button("Open"));
toolbar.addComponent(new Button("Save"));
```

#### Using GridLayout with layout data
```java
Panel form = new Panel(new GridLayout(2)); // 2 columns
form.addComponent(new Label("User:"));
form.addComponent(new TextBox());
form.addComponent(new Label("Pass:"));
form.addComponent(new TextBox().setMask('*'));
// Span a button across both columns
Button ok = new Button("OK");
ok.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, true, false, 2, 1));
form.addComponent(ok);
```

#### Bordered panel with custom background fill
```java
Panel logPanel = new Panel(new LinearLayout(Direction.VERTICAL));
logPanel.setFillColorOverride(TextColor.ANSI.BLACK);
logPanel = logPanel.withBorder(Borders.doubleLine("Logs"));
```

### Notes and best practices
- Changing the layout manager of a non‑empty panel may cause existing children to appear in unexpected positions until you re‑assign appropriate layout data for the new layout. Prefer setting the layout manager first, then adding children.
- Visibility affects layout and focus: invisible children are ignored during layout and focus traversal.
- When embedding many components, consider composing multiple panels rather than placing everything into one large panel. This often simplifies layout logic and improves readability.

## ProgressBar

The `ProgressBar` component provides a visual indication of how far a task has progressed between a minimum and maximum value. It supports a centered, formatted label (for example a percentage), a preferred width hint, and multiple renderers.

### Key features
- Range-based progress: define `min`, `max`, and current `value`.
- Formatted label: display progress text (e.g. `"67%"`) centered over the bar.
- Two renderers:
    - `DefaultProgressBarRenderer` — single-line bar.
    - `LargeProgressBarRenderer` — three-line bar with 0/25/50/75/100 markers.
- Theming support, including a `FILLER` character override.

### API overview
- Constructors:
    - `new ProgressBar()` — range 0..100.
    - `new ProgressBar(int min, int max)`
    - `new ProgressBar(int min, int max, int preferredWidth)` — width hint in columns (renderer may ignore).
- Range and value:
    - `setMin(int)`, `setMax(int)`, `setValue(int)` — all synchronized, auto-clamp to keep a valid range.
    - `getMin()`, `getMax()`, `getValue()`
    - `getProgress()` — progress as a float in the range 0.0..1.0.
- Label:
    - `setLabelFormat(String)` — `String.format` template receiving a single `float` argument scaled 0..100. Set to `null` or empty to disable.
    - `getLabelFormat()`, `getFormattedLabel()`
- Layout:
    - `setPreferredWidth(int)`, `getPreferredWidth()`
    - `setRenderer(ComponentRenderer<ProgressBar>)` — switch between default and large renderer (or provide your own).

All mutating methods are synchronized, making it safe to update a `ProgressBar` from a background thread while it is visible. Updates call `invalidate()` to trigger a redraw by the GUI.

### Usage example
```java
// Create a window with a progress bar that advances over time
final ProgressBar progressBar = new ProgressBar(0, 100, 24);
progressBar.setRenderer(new ProgressBar.LargeProgressBarRenderer());
progressBar.setLabelFormat("%2.0f%%"); // centered percentage label

Panel panel = new Panel();
panel.addComponent(progressBar.withBorder(Borders.singleLine("Progress")));

// Update from a timer/background thread
Timer timer = new Timer("ProgressBarTimer", true);
timer.scheduleAtFixedRate(new TimerTask() {
    @Override public void run() {
        if (progressBar.getValue() >= progressBar.getMax()) {
            progressBar.setValue(progressBar.getMin());
        } else {
            progressBar.setValue(progressBar.getValue() + 1);
        }
    }
}, 0, 100);

Window window = new BasicWindow("Demo");
window.setComponent(panel);
textGUI.addWindowAndWait(window);
```

### Renderers
- Default (single-line)
    - Preferred height: 1 row
    - Preferred width: `preferredWidth` if set; otherwise wide enough for the formatted label plus padding; otherwise 10 columns.
    - Label: centered; truncated to fit if necessary (CJK-aware width handling).
- Large (three-line)
    - Preferred size: `preferredWidth` x 3, or 42 x 3 by default.
    - Draws 0/25/50/75/100 markers above the bar when space allows.
    - Highlights the advancing edge.

Switch at runtime using `progressBar.setRenderer(new ProgressBar.LargeProgressBarRenderer());` or revert to default via `progressBar.setRenderer(new ProgressBar.DefaultProgressBarRenderer());`.

### Theming
`ProgressBar` uses standard theme states and allows customizing characters:
- Supported states: `NORMAL`, `ACTIVE` (filled area), `PRELIGHT` (edge indicator), `INSENSITIVE` (unreached markers/labels).
- Character keys: `char[FILLER]` for the empty area fill.
- You can also select a renderer via theme, using the fully qualified class name.

Example theme snippet:
```
# ProgressBar
com.googlecode.lanterna.gui2.ProgressBar.foreground = white
com.googlecode.lanterna.gui2.ProgressBar.background = blue
com.googlecode.lanterna.gui2.ProgressBar.sgr = bold
com.googlecode.lanterna.gui2.ProgressBar.background[ACTIVE] = red
com.googlecode.lanterna.gui2.ProgressBar.foreground[PRELIGHT] = red
com.googlecode.lanterna.gui2.ProgressBar.char[FILLER] =
# Optional: choose the large renderer by default
com.googlecode.lanterna.gui2.ProgressBar.renderer = com.googlecode.lanterna.gui2.ProgressBar$LargeProgressBarRenderer
```

### Tips
- If you disable the label (`setLabelFormat(null)`), the default renderer will prefer a compact width when no preferred width is set.
- When using narrow widths, labels are truncated to fit; consider shorter formats like `"%d%%"` or removing the label.
- For big displays or when you want visual tick marks, use the large renderer.

## RadioBoxList

RadioBoxList is a single-selection list component that displays items with a radio indicator. At most one item can be checked at a time; selecting a new item automatically unchecks the previous one.

### Creating a RadioBoxList

You can construct a `RadioBoxList<V>` without a preferred size (it will expand to fit its content) or with an explicit `TerminalSize` (scrollbars appear if items do not fit):

```
// Expands to fit items
RadioBoxList<String> radio = new RadioBoxList<>();

// Fixed size; scrollbars used if needed
TerminalSize size = new TerminalSize(20, 6);
RadioBoxList<String> sized = new RadioBoxList<>(size);
```

Add items using the list box API inherited from `AbstractListBox`:

```
radio.addItem("Option A");
radio.addItem("Option B");
radio.addItem("Option C");
```

### Programmatic selection

- `setCheckedItem(V item)`: Checks the given item (or clears selection if `null`).
- `setCheckedItemIndex(int index)`: Checks the item by index (ignores out-of-range).
- `getCheckedItem()`: Returns the currently checked item or `null`.
- `getCheckedItemIndex()`: Returns the checked index or `-1` if none.
- `isChecked(V item)` / `isChecked(int index)`: Test current selection.
- `clearSelection()`: Leaves the list with no item checked.

Selection also reacts to user input: pressing Space or Enter checks the currently highlighted item. Mouse clicks on items check them as well; dragging over items will highlight, and release/click will check.

### Keyboard and mouse interaction

`RadioBoxList` supports all navigation from `AbstractListBox` (arrow keys, PageUp/Down, Home/End). Activation keys (Space, Enter) check the highlighted item. Mouse interactions include move, click, drag, and scroll to navigate; clicking checks the item.

### Listening to selection changes

Attach a listener to be notified when the checked item changes:

```
radio.addListener(new RadioBoxList.Listener() {
    @Override
    public void onSelectionChanged(int selectedIndex, int previousSelection) {
        System.out.println("Checked index: " + selectedIndex + " (previous: " + previousSelection + ")");
    }
});
```

`selectedIndex` is `-1` when the selection is cleared programmatically (for example via `clearSelection()` or `setCheckedItem(null)`).

### Theming

`RadioBoxList` uses the component key `com.googlecode.lanterna.gui2.RadioBoxList` in theme property files. Useful properties used by the default renderer include:

- `char[LEFT_BRACKET]` and `char[RIGHT_BRACKET]`: Characters for the brackets surrounding the marker (defaults `<` and `>` in the default theme).
- `char[MARKER]`: Character used to indicate the checked state (default `o`).
- `property[HOTSPOT_PRELIGHT]`: If `true`, highlights the marker area when selected and focused.
- `property[CLEAR_WITH_NORMAL]`: If `true`, clears the line with normal style before drawing.
- `property[FIXED_BRACKET_COLOR]`: If `true`, draws the brackets using `PreLight` style regardless of state.
- `property[MARKER_WITH_NORMAL]`: If `true`, draws the marker using `Normal` style.

You can preview theme effects in the provided theme test dialog (`ThemeTest`). Example defaults are shown in `src/main/resources/default-theme.properties` under the `# RadioBoxList` section.

### Custom rendering

The default item renderer is `RadioBoxList.RadioBoxListItemRenderer<V>`, which renders items like `"<o> Label"` when checked and `"< > Label"` when not. To customize how items are displayed, provide your own `ListItemRenderer`:

```
radio.setListItemRenderer(new ListItemRenderer<String, RadioBoxList<String>>() {
    @Override
    public int getHotSpotPositionOnLine(int selectedIndex) {
        return 1; // position of the marker hot-spot if you keep a marker
    }

    @Override
    public String getLabel(RadioBoxList<String> listBox, int index, String item) {
        boolean checked = listBox.getCheckedItemIndex() == index;
        return (checked ? "(x) " : "( ) ") + item;
    }

    @Override
    public void drawItem(TextGUIGraphics g,
                         RadioBoxList<String> list,
                         int index,
                         String item,
                         boolean selected,
                         boolean focused) {
        // You can delegate most work to default styles
        ThemeDefinition def = list.getTheme().getDefinition(RadioBoxList.class);
        ThemeStyle style = selected ? (focused ? def.getActive() : def.getSelected())
                                    : (focused ? def.getInsensitive() : def.getNormal());
        g.applyThemeStyle(style);
        g.fill(' ');
        g.putString(0, 0, getLabel(list, index, item));
    }
});
```

If you only need to customize the label text, subclass `RadioBoxListItemRenderer` and override `getItemText(...)` or `getLabel(...)`.

### Example

```
TerminalSize size = new TerminalSize(14, 6);
RadioBoxList<String> radio = new RadioBoxList<>(size);
radio.addItem("Small");
radio.addItem("Medium");
radio.addItem("Large");

radio.setCheckedItem("Medium");

// Later
String choice = radio.getCheckedItem();
```

See also the short example and screenshot in `docs/examples/gui/radio_boxes.md`.

## ScrollBar
Classic non-interactable indicator showing where a viewport is within a larger model. A `ScrollBar` is either vertical or horizontal and must be driven by another component: you update its state when your content scrolls or when the visible area changes. Users cannot focus or interact with the `ScrollBar` directly.

Key concepts
- Direction: `Direction.VERTICAL` or `Direction.HORIZONTAL` (constructor argument).
- Maximum: total size of the model along the scroll axis. Set with `setScrollMaximum(int)` and read with `getScrollMaximum()`.
- Position: current scroll offset within the model. Set with `setScrollPosition(int)` and read with `getScrollPosition()`.
- View size: size of the viewport along the axis. Set with `setViewSize(int)` and read with `getViewSize()`.
    - If you don’t set a positive view size, the renderer will fall back to the component’s current size along the axis.

Typical wiring
1) When your content size is known, call `scrollBar.setScrollMaximum(totalModelLength)`. For vertical bars this is typically total rows; for horizontal, total columns/characters.
2) When the GUI or container resizes, update `scrollBar.setViewSize(visibleLength)`.
3) Whenever your component scrolls, call `scrollBar.setScrollPosition(currentOffset)`.

Sizing and layout
- Preferred size is `TerminalSize.ONE` (1×1). In layouts, give a vertical `ScrollBar` width 1 and a flexible height; give a horizontal `ScrollBar` height 1 and a flexible width.

Rendering
- The default renderer draws arrow glyphs at the ends, a background track, and a tracker (thumb).
- The tracker can auto-grow to represent the view size. You can toggle this via the renderer:
  ```java
  ScrollBar bar = new ScrollBar(Direction.VERTICAL);
  ScrollBar.DefaultScrollBarRenderer renderer = new ScrollBar.DefaultScrollBarRenderer();
  renderer.setGrowScrollTracker(true); // default is true
  bar.setRenderer(renderer);
  ```

Theme keys
The following theme characters are consulted by the default renderer; your theme can override them:
- Vertical: `UP_ARROW`, `DOWN_ARROW`, `VERTICAL_BACKGROUND`, `VERTICAL_SMALL_TRACKER`, `VERTICAL_TRACKER_TOP`, `VERTICAL_TRACKER_BOTTOM`, `VERTICAL_TRACKER_BACKGROUND`
- Horizontal: `LEFT_ARROW`, `RIGHT_ARROW`, `HORIZONTAL_BACKGROUND`, `HORIZONTAL_SMALL_TRACKER`, `HORIZONTAL_TRACKER_LEFT`, `HORIZONTAL_TRACKER_RIGHT`, `HORIZONTAL_TRACKER_BACKGROUND`

Notes
- Position is clamped so that `position + viewSize` never exceeds `maximum`.
- If the bar’s size is too small (1 cell total along the axis), it will render a minimal background or the two arrows only.

Minimal usage example
```java
// Create a vertical scrollbar and place it next to your scrolling content
ScrollBar vbar = new ScrollBar(Direction.VERTICAL);

// In your component setup / model update
vbar.setScrollMaximum(totalRowsInModel);
vbar.setViewSize(visibleRowCount);
vbar.setScrollPosition(currentTopRowOffset);

// Add to layout (example with a 2-column GridLayout: content | bar)
Panel panel = new Panel(new GridLayout(2));
panel.addComponent(contentComponent);
panel.addComponent(vbar);

// When content scrolls or the window resizes, keep the bar in sync
vbar.setScrollMaximum(totalRowsInModel);
vbar.setViewSize(visibleRowCount);
vbar.setScrollPosition(currentTopRowOffset);
```

## Separator
The `Separator` is a static, non‑interactive component used to visually divide areas of your UI with a single line. It’s useful when a bordered `Panel` would be too heavy and you simply want a thin horizontal or vertical rule.

- Renders a line across all space it is given
- Default preferred size is `1x1` (so layout managers should expand it)
- Direction is mandatory and controls if the line is horizontal or vertical

### Key API notes (from `com.googlecode.lanterna.gui2.Separator`):
- Constructor: `new Separator(Direction direction)` where `direction` is `Direction.HORIZONTAL` or `Direction.VERTICAL`.
- Preferred size: the default renderer reports `TerminalSize.ONE`, but the separator will fill its allocated area at draw time.
- Theming: the character used to draw the line is fetched from the component’s `ThemeDefinition` using the key equal to the direction name (`"HORIZONTAL"` or `"VERTICAL"`). If not provided by the theme, it falls back to `Symbols.SINGLE_LINE_HORIZONTAL` or `Symbols.SINGLE_LINE_VERTICAL`.

### Usage examples

#### Basic horizontal split inside a vertical layout:

```
Panel root = new Panel(new LinearLayout(Direction.VERTICAL));
root.addComponent(new Label("Section A"));
root.addComponent(new Separator(Direction.HORIZONTAL));
root.addComponent(new Label("Section B"));
```

#### Basic vertical split inside a horizontal layout:

```
Panel row = new Panel(new LinearLayout(Direction.HORIZONTAL));
row.addComponent(new Label("Left"));
row.addComponent(new Separator(Direction.VERTICAL));
row.addComponent(new Label("Right"));
```

### Controlling size with layout data

Because the separator’s preferred size is `1x1`, you typically rely on the layout to stretch it. With `LinearLayout`, the separator will expand to fill available space along the layout’s main axis. You can also constrain its size explicitly with layout data:

```
Panel column = new Panel(new LinearLayout(Direction.VERTICAL));
// Fixed 1-row separator (won't expand vertically even if space exists)
column.addComponent(
    new Separator(Direction.HORIZONTAL)
        .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill))
);

// In a GridLayout, you can span cells to create longer separators
Panel grid = new Panel(new GridLayout(3));
grid.addComponent(new Label("A1"));
grid.addComponent(new Label("A2"));
grid.addComponent(new Label("A3"));
grid.addComponent(
    new Separator(Direction.HORIZONTAL)
        .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.Fill,
                                                   GridLayout.Alignment.Center,
                                                   true,  // grab extra horizontal space
                                                   false, // don't grab extra vertical space
                                                   3, 1)) // span all 3 columns
);
```

### Theming

The separator uses the component theme’s normal style, and the draw character can be customized per theme. Theme keys checked are:
- `HORIZONTAL` (default: `Symbols.SINGLE_LINE_HORIZONTAL`)
- `VERTICAL` (default: `Symbols.SINGLE_LINE_VERTICAL`)

If you provide these characters in your `ThemeDefinition`, separators throughout the UI will automatically use them.

### Common pitfalls
- Forgetting to choose a direction produces an error: the constructor rejects `null`.
- Expecting the separator to size itself: you must give it space via the layout manager; otherwise it may remain 1 character long/tall.

## SplitPanel

SplitPanel is a container that divides its area into two resizable regions separated by a draggable thumb (splitter). It can be oriented horizontally (left/right panes) or vertically (top/bottom panes).

- Create a horizontal split (left | right):
    - `SplitPanel.ofHorizontal(Component left, Component right)`
- Create a vertical split (top / bottom):
    - `SplitPanel.ofVertical(Component top, Component bottom)`

### Ratio and resizing
- The space is divided according to an internal ratio in the range 0.0–1.0 representing the first component’s share of the available width/height (excluding the thumb’s thickness).
- Set the ratio using `setRatio(int left, int right)`. The ratio is computed as `left / (abs(left) + abs(right))`.
    - Example: `setRatio(1, 3)` gives a 25% / 75% split; `setRatio(10, 10)` yields a 50% / 50% split.
    - If `left == 0` or `right == 0`, the ratio falls back to 0.5 (even split).
- The thumb can be dragged with the mouse to adjust the ratio interactively.
    - Keyboard-based resizing is not implemented yet; focus on the thumb affects its rendering (becomes bold with the default theme).

### Thumb (splitter) appearance and visibility
- The thumb is rendered as a single-line character (vertical bar for horizontal splits, horizontal bar for vertical splits) and spans the cross-axis of the panel.
- Use `setThumbVisible(boolean visible)` to toggle the thumb:
    - `true` (default): The thumb is visible and the panel uses its normal preferred size.
    - `false`: The thumb is hidden; internally its preferred size is reduced to `1x1` so it minimally affects layout.

### Layout behavior and sizing notes
- SplitPanel respects each child’s preferred size along the cross-axis (height for horizontal splits, width for vertical splits) while distributing the main axis according to the ratio.
- The available space for the two components is the full width/height minus the thumb’s thickness (one column or one row).
- You can nest SplitPanels inside other containers (it extends `Panel`).

### Minimal example
```
// Horizontal split: navigation on the left, content on the right
ActionListBox navigation = new ActionListBox();
navigation.addItem("Home", () -> {});
navigation.addItem("Settings", () -> {});

TextBox content = new TextBox();
content.setText("Details...");
content.setReadOnly(true);

SplitPanel split = SplitPanel.ofHorizontal(navigation, content);
split.setRatio(1, 2);           // ~33% left, 67% right
split.setThumbVisible(true);    // show draggable splitter

// Add to your window/panel as usual
window.setComponent(split);
```

## Table

The `Table` component (`com.googlecode.lanterna.gui2.table.Table`) displays data in a grid with a header row and supports keyboard and mouse interaction, scrolling, and both row- and cell-based selection. It is backed by a `TableModel<V>` and uses pluggable renderers with theming support.

### When to use
- Show tabular data with a fixed number of columns.
- Let users navigate rows (and optionally cells), scroll through long lists, and trigger an action on selection.

### Key classes
- `Table<V>`: The GUI component users interact with.
- `TableModel<V>`: Backing data model (columns + rows).
- `TableRenderer<V>`: Overall table renderer (header + rows) — default: `DefaultTableRenderer`.
- `TableHeaderRenderer<V>`: Header renderer — default: `DefaultTableHeaderRenderer`.
- `TableCellRenderer<V>`: Cell renderer — default: `DefaultTableCellRenderer`.

### Basic usage
```java
// Create a table with three columns and add a few rows
Table<String> table = new Table<>("ID", "Name", "Status");
table.getTableModel().addRow("1", "Alpha", "Running");
table.getTableModel().addRow("2", "Beta",  "Stopped");
table.getTableModel().addRow("3", "Gamma", "Queued");

// Optional: choose selection mode (row selection by default)
table.setCellSelection(false); // set to true to allow Left/Right to move between cells

// Optional: limit the visible area (viewport hint)
table.setVisibleRows(10);
table.setVisibleColumns(3);

// Optional: provide an activation action (Enter/Space or mouse selection)
table.setSelectAction(() -> {
int row = table.getSelectedRow();
int col = table.isCellSelection() ? table.getSelectedColumn() : -1;
List<String> currentRow = table.getTableModel().getRow(row);
    System.out.println("Activated row=" + row + ", col=" + col + ", data=" + currentRow);
});

// Add to a container and display in a WindowBasedTextGUI
Panel container = new Panel();
container.addComponent(table);
```

Tip: You can also control the on-screen size using the container’s preferred size:
```java
Panel p = new Panel();
p.setPreferredSize(new TerminalSize(40, 12));
        p.addComponent(table);
```

### Navigation and interaction
- Up/Down Arrow: Move selection to previous/next row.
- Page Up/Page Down: Jump by approximately one viewport page (based on last render’s visible rows).
- Home/End: Jump to first/last row.
- Left/Right Arrow: When `cellSelection` is enabled, move to previous/next column; otherwise, optionally escape focus.
- Enter/Space: Activation (invokes `selectAction` if set).
- Mouse:
    - Click selects the row (and column if `cellSelection` is enabled). Clicking a different cell triggers activation.
    - Mouse move is ignored; scroll is handled by the surrounding container if present.

Focus escape: By default, pressing an arrow key at the edge of the table moves focus to neighboring components. Control this with `setEscapeByArrowKey(boolean)`.

### Selection and viewport
- Selection is row-based by default. Use `setCellSelection(true)` to enable per-cell selection; `getSelectedRow()` and `getSelectedColumn()` expose the current selection.
- Viewport controls:
    - `setVisibleRows(int)` and `setVisibleColumns(int)` hint how many rows/columns the renderer should try to show without scrolling.
    - `getViewTopRow()` / `setViewTopRow(int)` control the vertical scroll position.
    - `getViewLeftColumn()` / `setViewLeftColumn(int)` control the horizontal scroll position.
    - `getFirstViewedRowIndex()` / `getLastViewedRowIndex()` expose the currently visible row range.

### Working with the model
- Construct with headers: `new Table<>("Col A", "Col B", ...)` or with an existing `TableModel<V>`.
- Add/remove rows and columns through `TableModel<V>`; the `Table` listens to model changes and redraws automatically.
- Cell values are displayed using `toString()` of the cell value type `V`.

### Renderers and theming
- Customize appearance by supplying your own renderers:
    - `table.setTableHeaderRenderer(TableHeaderRenderer<V>)`
    - `table.setTableCellRenderer(TableCellRenderer<V>)`
    - Or replace the overall renderer via `table.setRenderer(TableRenderer<V>)`.
- The default renderer respects the component theme (`ThemeDefinition`) for colors/styles. You can use `SimpleTheme` or a custom theme to adjust header vs. row styles, selection, etc.

### Notes and tips
- If you only need row selection, keep `cellSelection` disabled; Left/Right can then be used to move focus between components (if `escapeByArrowKey` is enabled).
- Use `setSelectAction(Runnable)` to react uniformly to Enter/Space and mouse selection on a new cell.
- For large tables, Page Up/Down navigation uses the number of rows visible on the last draw to page accurately.


## TextBox

The `TextBox` component displays and edits text. It supports both single-line and multi-line modes, caret navigation, optional input masking, validation, and scrollbars when content exceeds the view.

### Creating a TextBox
- Empty single-line with default size (10×1):
  ```java
  TextBox tb = new TextBox();
  ```
- From initial content, auto-detecting style (multi-line if it contains `\n`):
  ```java
  TextBox tb = new TextBox("Hello World");
  TextBox multi = new TextBox("Line 1\nLine 2");
  ```
- With explicit size and/or style:
  ```java
  TextBox fixed = new TextBox(new TerminalSize(20, 1)); // single-line (rows == 1)
  TextBox area  = new TextBox(new TerminalSize(30, 8), TextBox.Style.MULTI_LINE);
  TextBox sizedFromText = new TextBox(null, "Prefill", TextBox.Style.SINGLE_LINE);
  ```

Tip: `TextBox` does not try to compute a “good” size automatically. Prefer using constructors that specify `TerminalSize`, especially in multi-line use cases.

### Single-line vs Multi-line
- `TextBox.Style.SINGLE_LINE`: one line, horizontal focus switching enabled by default (left/right can move focus when at edge).
- `TextBox.Style.MULTI_LINE`: multiple lines, vertical and horizontal scrolling as needed, `Enter` inserts new lines, scrollbars may appear.

You can force the style via constructors. If not forced, it is inferred from preferred size (rows > 1) or presence of `\n` in initial content.

### Core API
- Getting/setting content:
  ```java
  tb.setText("New text");
  String text = tb.getText();
  String valueOrDefault = tb.getTextOrDefault("<empty>");
  ```
- Line access (mostly useful in multi-line):
  ```java
  int lines = tb.getLineCount();
  String first = tb.getLine(0);
  tb.addLine("Another line");
  tb.removeLine(1);
  ```
- Caret and navigation behavior:
  ```java
  TerminalPosition pos = tb.getCaretPosition();
  tb.setCaretPosition(5);          // column for single-line or current line
  tb.setCaretPosition(2, 0);       // line, column (multi-line)
  tb.setCaretWarp(true);           // allow wrap to prev/next line when hitting edges (multi-line)
  ```
- Read-only and masking:
  ```java
  tb.setReadOnly(true);            // disables editing and cursor rendering
  tb.setMask('*');                 // displays mask char instead of actual text (like a password field)
  tb.setMask(null);                // disable masking
  ```
- Validation (applied to the line being edited):
  ```java
  tb.setValidationPattern(Pattern.compile("[0-9]*")); // only digits allowed on a line
  ```
  If an edit would make the current line not match the pattern, the edit is rejected.

- Text change notifications:
  ```java
  tb.setTextChangeListener((newText, byUser) -> {
      // react to content changes
  });
  ```
  The listener is called for both programmatic and user-initiated changes; `byUser` indicates if the change came from user interaction.

- Focus switching at edges (useful inside layouts):
  ```java
  tb.setHorizontalFocusSwitching(true); // default true for single-line
  tb.setVerticalFocusSwitching(true);   // default true
  ```
  When enabled, pressing an arrow key at the content edge can request moving focus to the neighboring component.

### Key bindings (default)
- Character typing inserts at caret (subject to `maxLineLength` internal constraints and validation).
- Backspace/Delete remove characters (or join/split lines in multi-line where applicable).
- Arrow Left/Right/Up/Down move the caret; with `caretWarp` in multi-line, moving beyond the edge wraps to previous/next line.
- Home/End move the caret to start/end of the current line.
- Page Up/Down scroll the view.
- Enter inserts a new line in multi-line; in single-line it requests moving focus to the next component.
- Mouse wheel: scrolls the multi-line view vertically.

### Renderer and scrollbars
`TextBox` uses a `TextBox.TextBoxRenderer`. The default implementation draws a solid background with text and optional scrollbars when content exceeds the view.

You can obtain and customize the default renderer:
```java
TextBox.DefaultTextBoxRenderer renderer = (TextBox.DefaultTextBoxRenderer) tb.getRenderer();
renderer.setHideScrollBars(false);   // show scrollbars when needed (default: false)
renderer.setUnusedSpaceCharacter(' '); // optional: set character for unused cells (must not be double-width)

// Control the top-left of the visible text viewport (for programmatic scrolling):
TerminalPosition topLeft = renderer.getViewTopLeft();
renderer.setViewTopLeft(topLeft.withRow(0).withColumn(0));
```

`getPreferredSize()` for a `TextBox` is based on longest line and number of lines, but you generally should give it an explicit size via constructor or layout constraints.

### Examples
- Single-line input:
  ```java
  Panel p = new Panel();
  TextBox name = new TextBox(new TerminalSize(20, 1));
  p.addComponent(new Label("Name:"));
  p.addComponent(name);
  ```

- Multi-line editor with validation and listener:
  ```java
  TextBox notes = new TextBox(new TerminalSize(50, 10), TextBox.Style.MULTI_LINE)
      .setValidationPattern(Pattern.compile("[\\p{Print}\\t ]*")) // printable, tab, space
      .setTextChangeListener((text, byUser) -> System.out.println("Updated (user=" + byUser + ")"));

  TextBox.DefaultTextBoxRenderer r = (TextBox.DefaultTextBoxRenderer) notes.getRenderer();
  r.setHideScrollBars(false);
  ```

## Tree

The `Tree` component (`com.googlecode.lanterna.gui2.Tree`) displays a hierarchical list of items that can be expanded/collapsed and navigated by keyboard or mouse. It is built on top of the lightweight `TreeNode` data structure and uses a pluggable renderer with theming support.

### When to use
- Present hierarchical data such as folders, menus, or nested options.
- Allow users to expand/collapse branches and select items using the keyboard or mouse.

### Key classes
- `Tree<V>`: The GUI component that renders and handles interaction.
- `TreeNode<V>`: A simple node model storing a value and presentation attributes (`label`, `expanded`, `visible`, etc.).

### Basic usage
1) Build a `TreeNode` hierarchy.
2) Create a `Tree` with the root node and configure behavior.
3) Add the `Tree` to a `Panel`/`Window` and display it in a `WindowBasedTextGUI`.

```java
// 1) Build the data model
TreeNode<String> root = new TreeNode<>("root", true); // expanded root
TreeNode<String> child1 = root.addChild("child_1", false); // collapsed child
TreeNode<String> child2 = root.addChild("child_2", true);  // expanded child

TreeNode<String> leaf = child1.addChild("child_1_1", true);
child1.addChild("child_1_2", true);

// 2) Create and configure the Tree
int columns = 35;          // rendering width budget for content area
int scrollWindowHeight = 15; // number of visible rows (vertical viewport height)
Tree<String> tree = new Tree<>(root, columns, scrollWindowHeight);

// Optional: hide the root label and start from first child
tree.setDisplayRoot(false);

// Optional: wrap focus when navigating past the ends with Up/Down
tree.setOverflowCircle(true);

// Optional: set a consumer for activation (Enter/Space).
tree.setNodeSelectedConsumer(node -> {
        System.out.println("Activated node: " + node.getLabel());
        });

// 3) Add to layout
Panel container = new Panel();
container.addComponent(tree);
```

Tip: You can also set `Panel` preferred size to control the visible area:

```java
Panel treePanel = new Panel();
treePanel.addComponent(tree);
treePanel.setPreferredSize(new TerminalSize(35, 15));
```

### Navigation and interaction
- Up/Down Arrow: Move selection to previous/next visible node.
- Home/End: Jump to the first/last visible node.
- Page Up/Page Down: Move by the viewport height (configured via `scrollWindowHeight`); scroll position updates accordingly.
- Enter/Space (keyboard activation stroke): Toggle expansion on the selected node and invoke `nodeSelectedConsumer`.
- Mouse:
    - Scroll wheel up/down: Move selection up/down.
    - Click: Select the node under the cursor and toggle its expansion state.

Note: Activation (Enter/Space) invokes the `nodeSelectedConsumer`.

### Showing or hiding the root
- `tree.setDisplayRoot(boolean)` controls whether the root node label is rendered.
    - When set to `false`, focus/selection moves to the first child automatically (if present).
    - `tree.isDisplayRoot()` reflects the current state.

### Accessing the selection
- `tree.getSelectedNode()` returns the currently selected `TreeNode<V>`.

### Building trees with TreeNode
`TreeNode<V>` supports convenient helpers for building and navigating the model:
- `addChild(V value)` / `addChild(V value, boolean expanded)` / `addChild(TreeNode<V> node)`
- `removeChild(TreeNode<V> node)`
- `setExpanded(boolean)` / `toggleExpanded()`
- `setVisible(boolean)` to hide a subtree without removing it
- `setLabel(String)` and `getLabel()`

Example to build a deeper chain and hide one node:

```java
TreeNode<String> chainParent = child2;
for (int i = 20; i < 30; i++) {
chainParent = chainParent.addChild("child_" + i, true);
}

TreeNode<String> hidden = child1.addChild("child_9", true);
hidden.setVisible(false);
hidden.setLabel("child_9 (hidden)");
```

### Theming and renderer options
`Tree` uses `DefaultTreeRenderer` by default. You can influence appearance via theme properties on the component’s `ThemeDefinition` (e.g., using `SimpleTheme`) or by creating a custom renderer.

Renderer properties (keys are in `Tree.DefaultTreeRenderer`):
- Display Characters:
    - `LEFT_BRACKET`, `RIGHT_BRACKET`
    - `EXPANDED_MARKER`, `COLLAPSED_MARKER`, `LEAF_MARKER`
- Tree indentation:
    - `TREE_LEVEL_INDENT` (default: `1`) — indentation per tree level.
- Additional display options:
    - `DISPLAY_BRACKETS` (default: `true`) — include tree brackets/glyphs.
    - `DISPLAY_BLOCK` (default: `false`) — draw a block line for the current selection row.
- Characters (block filler):
    - `DISPLAY_BLOCK_FILLER` — the filler character used when `DISPLAY_BLOCK` is enabled.

Example configuring a `SimpleTheme`:

```java
SimpleTheme theme = new SimpleTheme(TextColor.ANSI.BLUE, TextColor.ANSI.WHITE);
// Customize active style and glyphs
theme.getDefaultDefinition().setActive(TextColor.ANSI.RED, TextColor.ANSI.CYAN);
theme.getDefaultDefinition().setCharacter(Tree.DefaultTreeRenderer.LEAF_MARKER, '*');
theme.getDefaultDefinition().setCharacter(Tree.DefaultTreeRenderer.DISPLAY_BLOCK_FILLER, '.');
theme.getDefaultDefinition().setIntegerProperty(Tree.DefaultTreeRenderer.TREE_LEVEL_INDENT, 2);
theme.getDefaultDefinition().setBooleanProperty(Tree.DefaultTreeRenderer.DISPLAY_BRACKETS, false);
theme.getDefaultDefinition().setBooleanProperty(Tree.DefaultTreeRenderer.DISPLAY_BLOCK, true);

Tree<String> themedTree = new Tree<>(root, 35, 15);
themedTree.setTheme(theme);
```

### Scrolling behavior
- The `scrollWindowHeight` constructor parameter defines the viewport height in rows; the component draws up to this many visible nodes and then displays a vertical scrollbar if needed.
- `setOverflowCircle(true)` enables wrap-around when navigating with Up/Down beyond the first/last visible node.

### Notes and tips
- To start without showing the root label, call `setDisplayRoot(false)` right after constructing the tree.
- `Tree` computes preferred size based on the expanded content; the containing layout and any `setPreferredSize` calls will determine the final on-screen area.


















