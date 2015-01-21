/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.lanterna.gui.component;

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.Border;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Interactable;
import com.googlecode.lanterna.gui.TextGraphics;
import com.googlecode.lanterna.gui.Theme;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;

/**
 *
 * @author maslbl4
 */
public class ComboBox extends AbstractInteractableComponent {

    private Label buttonLabel;
    private Action onPressEvent;

    String emptyText;
    GUIScreen screen;
    ComboBoxItemList listBox = new ComboBoxItemList();
    Window window;

    public ComboBox(final GUIScreen screen, String emptyText) {
        Init(screen, emptyText, "");
    }

    public ComboBox(final GUIScreen screen, String emptyText, String title) {
        Init(screen, emptyText, title);
    }

    private void Init(final GUIScreen screen, String emptyText, String title) {
        this.screen = screen;
        this.emptyText = emptyText;
        this.buttonLabel = new Label(emptyText);
        this.buttonLabel.setStyle(Theme.Category.BUTTON_LABEL_INACTIVE);
        onPressEvent = new Action() {

            @Override
            public void doAction() {
                selectItemAction();
            }
        };
        listBox.setSelectedAction(onPressEvent);

        window = new Window(title);
        window.setDrawShadow(false);
        window.setBorder(new Border.Standard());

        Panel panel = new Panel(Panel.Orientation.VERTICAL);
        panel.addShortcut(Key.Kind.Escape, new Action() {

            @Override
            public void doAction() {
                window.close();
            }
        });
        panel.addComponent(listBox);
        window.addComponent(panel);
    }

    Action itemStateChanged;

    /**
     * Adds an action to the list, using toString() of the action as a label
     *
     * @param action Action to be performed when the user presses enter key
     */
    public void selectedChanged(final Action action) {
        itemStateChanged = action;
    }

    @Override
    public void repaint(TextGraphics graphics) {
        if (hasFocus()) {
            graphics.applyTheme(graphics.getTheme().getDefinition(Theme.Category.BUTTON_ACTIVE));
        } else {
            graphics.applyTheme(graphics.getTheme().getDefinition(Theme.Category.BUTTON_INACTIVE));
        }

        TerminalSize preferredSize = calculatePreferredSize();
        graphics = transformAccordingToAlignment(graphics, preferredSize);

        if (graphics.getWidth() < preferredSize.getColumns()) {
            graphics.drawString(0, 0, "[ ");
            graphics.drawString(graphics.getWidth() - 2, 0, " ]");

            int allowedSize = graphics.getWidth() - 4;
            if (allowedSize > 0) {
                TextGraphics subGraphics = graphics.subAreaGraphics(new TerminalPosition(2, 0),
                        new TerminalSize(allowedSize, buttonLabel.getPreferredSize().getRows()));
                buttonLabel.repaint(subGraphics);
            }
        } else {
            int leftPosition = (graphics.getWidth() - preferredSize.getColumns()) / 2;
            graphics.drawString(leftPosition, 0, "[ ");
            final TerminalSize labelPrefSize = buttonLabel.getPreferredSize();
            TextGraphics subGraphics = graphics.subAreaGraphics(
                    new TerminalPosition(leftPosition + 2, 0),
                    new TerminalSize(labelPrefSize.getColumns(), labelPrefSize.getRows()));
            buttonLabel.repaint(subGraphics);
            graphics.drawString(leftPosition + 2 + labelPrefSize.getColumns(), 0, " ]");
        }
        setHotspot(null);
    }

    @Override
    protected TerminalSize calculatePreferredSize() {
        TerminalSize labelSize = buttonLabel.getPreferredSize();
        return new TerminalSize(labelSize.getColumns() + 2 + 2, labelSize.getRows());
    }

    @Override
    public void afterEnteredFocus(Interactable.FocusChangeDirection direction) {
        buttonLabel.setStyle(Theme.Category.BUTTON_LABEL_ACTIVE);
    }

    @Override
    public void afterLeftFocus(Interactable.FocusChangeDirection direction) {
        buttonLabel.setStyle(Theme.Category.BUTTON_LABEL_INACTIVE);
    }

    @Override
    public Interactable.Result keyboardInteraction(Key key) {
        switch (key.getKind()) {
            case Enter:
                showItems();
                return Interactable.Result.EVENT_HANDLED;

            case ArrowDown:
                return Interactable.Result.NEXT_INTERACTABLE_DOWN;

            case ArrowRight:
            case Tab:
                return Interactable.Result.NEXT_INTERACTABLE_RIGHT;

            case ArrowUp:
                return Interactable.Result.PREVIOUS_INTERACTABLE_UP;

            case ArrowLeft:
            case ReverseTab:
                return Interactable.Result.PREVIOUS_INTERACTABLE_LEFT;

            default:
                return Interactable.Result.EVENT_NOT_HANDLED;
        }
    }

    Object selectedItem = null;

    public void addItem(Object item) {

        if (listBox.getSize() == 0) {
            selectedItemChange(item);
        }
        listBox.addItem(item);
    }

    public void removeAllItems() {
        listBox.clearItems();
        selectedItemChange(null);
    }

    public Object getSelectedItem() {
        return selectedItem;
    }

    void showItems() {
        if (listBox.getSize() <= 0) {
            return;
        }
        screen.showWindow(window, GUIScreen.Position.CENTER);
    }

    void selectItemAction() {

        Object item = listBox.getSelectedItem();

        if (selectedItem == null && item == null) {
            window.close();
            return;
        }
        if (selectedItem != null && selectedItem.equals(item)) {
            window.close();
            return;
        }

        selectedItemChange(item);
        window.close();
    }

    public void setSelectedIndex(int index) {

        int size = listBox.getSize();

        if (index == -1) {
            selectedItemChange(null);
        } else if (index < -1 || index >= size) {
            throw new IllegalArgumentException("setSelectedIndex: " + index + " out of bounds");
        } else {
            selectedItemChange(listBox.getItemAt(index));
        }

    }

    public void setSelectedItem(Object item) {

        Object oldSelection = selectedItem;
        Object objectToSelect = item;
        if (oldSelection == null || !oldSelection.equals(item)) {

            if (item != null) {
                boolean found = false;
                for (int i = 0; i < listBox.getSize(); i++) {
                    Object element = listBox.getItemAt(i);
                    if (item.equals(element)) {
                        found = true;
                        objectToSelect = element;
                        break;
                    }
                }
                if (!found) {
                    return;
                }
            }
        }
        selectedItemChange(objectToSelect);
    }

    private void selectedItemChange(Object item) {
        if (item == null) {
            selectedItem = null;
            buttonLabel.setText(emptyText);
            if (itemStateChanged != null) {
                itemStateChanged.doAction();
            }
            return;
        }

        selectedItem = item;
        buttonLabel.setText(item.toString());

        if (itemStateChanged != null) {
            itemStateChanged.doAction();
        }
    }
}
