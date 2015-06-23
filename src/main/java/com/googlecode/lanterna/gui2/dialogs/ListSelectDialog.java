package com.googlecode.lanterna.gui2.dialogs;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;

import java.util.List;

/**
 * Created by martin on 23/06/15.
 */
public class ListSelectDialog<T> extends DialogWindow {
    private final ActionListBox listBox;
    private T result;

    ListSelectDialog(
            String title,
            String description,
            TerminalSize listBoxPreferredSize,
            boolean canCancel,
            List<T> content) {

        super(title);
        this.result = null;
        this.listBox = new ActionListBox(listBoxPreferredSize);
        if(content.isEmpty()) {
            throw new IllegalStateException("ListSelectDialog needs at least one item");
        }

        for(final T item: content) {
            listBox.addItem(item.toString(), new Runnable() {
                @Override
                public void run() {
                    onSelect(item);
                }
            });
        }

        Panel mainPanel = new Panel();
        mainPanel.setLayoutManager(
                new GridLayout(1)
                        .setLeftMarginSize(1)
                        .setRightMarginSize(1));
        if(description != null) {
            mainPanel.addComponent(new Label(description));
            mainPanel.addComponent(new EmptySpace(TerminalSize.ONE));
        }
        listBox.setLayoutData(
                GridLayout.createLayoutData(
                        GridLayout.Alignment.FILL,
                        GridLayout.Alignment.CENTER,
                        true,
                        false))
                .addTo(mainPanel);
        mainPanel.addComponent(new EmptySpace(TerminalSize.ONE));

        if(canCancel) {
            Panel buttonPanel = new Panel();
            buttonPanel.setLayoutManager(new GridLayout(2).setHorizontalSpacing(1));
            buttonPanel.addComponent(new Button("Cancel", new Runnable() {
                @Override
                public void run() {
                    onCancel();
                }
            }).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, true, false)));
            buttonPanel.setLayoutData(
                    GridLayout.createLayoutData(
                            GridLayout.Alignment.END,
                            GridLayout.Alignment.CENTER,
                            false,
                            false))
                    .addTo(mainPanel);
        }
        setComponent(mainPanel);
    }

    private void onSelect(T item) {
        result = item;
        close();
    }

    private void onCancel() {
        close();
    }

    public T showDialog(WindowBasedTextGUI textGUI) {
        result = null;
        textGUI.addWindow(this);

        //Wait for the window to close, in case the window manager doesn't honor the MODAL hint
        waitUntilClosed();

        return result;
    }

    public static <T> T showDialog(WindowBasedTextGUI textGUI, String title, String description, T... items) {
        ListSelectDialog<T> listSelectDialog = new ListSelectDialogBuilder<T>()
                .setTitle(title)
                .setDescription(description)
                .addListItems(items)
                .build();
        return listSelectDialog.showDialog(textGUI);
    }
}
