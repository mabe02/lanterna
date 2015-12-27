package com.googlecode.lanterna.gui2.dialogs;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;

import java.util.List;

/**
 * Dialog containing a multiple item action list box
 * @author Martin
 */
public class ActionListDialog extends DialogWindow {

    ActionListDialog(
            String title,
            String description,
            TerminalSize actionListPreferredSize,
            boolean canCancel,
            List<Runnable> actions) {

        super(title);
        if(actions.isEmpty()) {
            throw new IllegalStateException("ActionListDialog needs at least one item");
        }

        ActionListBox listBox = new ActionListBox(actionListPreferredSize);
        for(final Runnable action: actions) {
            listBox.addItem(action.toString(), new Runnable() {
                @Override
                public void run() {
                    action.run();
                    close();
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
            buttonPanel.addComponent(new Button(LocalizedString.Cancel.toString(), new Runnable() {
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

    private void onCancel() {
        close();
    }

    /**
     * Helper method for immediately displaying a {@code ActionListDialog}, the method will return when the dialog is
     * closed
     * @param textGUI Text GUI the dialog should be added to
     * @param title Title of the dialog
     * @param description Description of the dialog
     * @param items Items in the {@code ActionListBox}, the label will be taken from each {@code Runnable} by calling
     *              {@code toString()} on each one
     */
    public static void showDialog(WindowBasedTextGUI textGUI, String title, String description, Runnable... items) {
        ActionListDialog actionListDialog = new ActionListDialogBuilder()
                .setTitle(title)
                .setDescription(description)
                .addActions(items)
                .build();
        actionListDialog.showDialog(textGUI);
    }
}
