package com.googlecode.lanterna.gui2.dialogs;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;

/**
 * Simple message dialog that displays a message and has optional selection/confirmation buttons
 *
 * @author Martin
 */
public class MessageDialog extends DialogWindow {

    private MessageDialogButton result;

    MessageDialog(
            String title,
            String text,
            MessageDialogButton... buttons) {

        super(title);
        this.result = null;
        if(buttons == null || buttons.length == 0) {
            buttons = new MessageDialogButton[] { MessageDialogButton.OK };
        }

        Panel buttonPanel = new Panel();
        buttonPanel.setLayoutManager(new GridLayout(buttons.length).setHorizontalSpacing(1));
        for(final MessageDialogButton button: buttons) {
            buttonPanel.addComponent(new Button(button.toString(), new Runnable() {
                @Override
                public void run() {
                    result = button;
                    close();
                }
            }));
        }

        Panel mainPanel = new Panel();
        mainPanel.setLayoutManager(
                new GridLayout(1)
                        .setLeftMarginSize(1)
                        .setRightMarginSize(1));
        mainPanel.addComponent(new Label(text));
        mainPanel.addComponent(new EmptySpace(TerminalSize.ONE));
        buttonPanel.setLayoutData(
                GridLayout.createLayoutData(
                        GridLayout.Alignment.END,
                        GridLayout.Alignment.CENTER,
                        false,
                        false))
                .addTo(mainPanel);
        setComponent(mainPanel);
    }

    /**
     * {@inheritDoc}
     * @param textGUI Text GUI to add the dialog to
     * @return The selected button's enum value
     */
    @Override
    public MessageDialogButton showDialog(WindowBasedTextGUI textGUI) {
        result = null;
        super.showDialog(textGUI);
        return result;
    }

    /**
     * Shortcut for quickly displaying a message box
     * @param textGUI The GUI to display the message box on
     * @param title Title of the message box
     * @param text Main message of the message box
     * @param buttons Buttons that the user can confirm the message box with
     * @return Which button the user selected
     */
    public static MessageDialogButton showMessageDialog(
            WindowBasedTextGUI textGUI,
            String title,
            String text,
            MessageDialogButton... buttons) {
        MessageDialogBuilder builder = new MessageDialogBuilder()
                .setTitle(title)
                .setText(text);
        if(buttons.length == 0) {
            builder.addButton(MessageDialogButton.OK);
        }
        for(MessageDialogButton button: buttons) {
            builder.addButton(button);
        }
        return builder.build().showDialog(textGUI);
    }
}
