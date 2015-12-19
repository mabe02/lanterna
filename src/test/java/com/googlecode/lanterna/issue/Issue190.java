package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.*;
import com.googlecode.lanterna.terminal.*;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by martin on 19/12/15.
 */
public class Issue190 {
    public static void main(String[] args) throws IOException {
        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        factory.setInitialTerminalSize(new TerminalSize(150,50));
        factory.setSwingTerminalFrameTitle("name");
        Terminal terminal = factory.createTerminal();
        TerminalScreen screen = new TerminalScreen(terminal);
        screen.startScreen();

        Panel panel = new Panel();
        panel.setLayoutManager(new BorderLayout());

        ActionListBox channels = new ActionListBox();
        channels.setLayoutData(BorderLayout.Location.LEFT);
        panel.addComponent(channels.withBorder(Borders.singleLine("Channels")));

        TextBox log = new TextBox("", TextBox.Style.MULTI_LINE);
        log.setReadOnly(true);
        log.setLayoutData(BorderLayout.Location.CENTER);
        panel.addComponent(log.withBorder(Borders.singleLine("Log")));

        Panel options = new Panel();
        options.setLayoutData(BorderLayout.Location.BOTTOM);

        options.withBorder(Borders.singleLine("Send Message"));

        options.setLayoutManager(new BorderLayout());

        final TextBox input = new TextBox("Message", TextBox.Style.SINGLE_LINE);
        input.setLayoutData(BorderLayout.Location.CENTER);
        options.addComponent(input);

        Button send = new Button("Send", new Runnable() {
            @Override
            public void run() {
                input.setText("");
            }
        });
        send.setLayoutData(BorderLayout.Location.RIGHT);
        options.addComponent(send);

        panel.addComponent(options.withBorder(Borders.singleLine("Send Message")));

        BasicWindow window = new BasicWindow();
        window.setComponent(panel.withBorder(Borders.doubleLine("DarkOwlBot")));

        window.setHints(Arrays.asList(Window.Hint.EXPANDED, Window.Hint.FIT_TERMINAL_WINDOW));

        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
        gui.addWindowAndWait(window);
    }
}
