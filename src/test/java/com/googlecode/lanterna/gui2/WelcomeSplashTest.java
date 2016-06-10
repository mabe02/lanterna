package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;

import java.io.IOException;
import java.util.EnumSet;

/**
 * Created to supply us with a screenshot for the Github page
 */
public class WelcomeSplashTest extends TestBase {
    public static void main(String[] args) throws IOException, InterruptedException {
        new WelcomeSplashTest().run(args);
    }

    @Override
    public void init(WindowBasedTextGUI textGUI) {
        textGUI.getBackgroundPane().setComponent(new EmptySpace(TextColor.ANSI.BLUE) {
            @Override
            protected ComponentRenderer<EmptySpace> createDefaultRenderer() {
                return new ComponentRenderer<EmptySpace>() {
                    @Override
                    public TerminalSize getPreferredSize(EmptySpace component) {
                        return TerminalSize.ONE;
                    }

                    @Override
                    public void drawComponent(TextGUIGraphics graphics, EmptySpace component) {
                        graphics.setForegroundColor(TextColor.ANSI.CYAN);
                        graphics.setBackgroundColor(TextColor.ANSI.BLUE);
                        graphics.setModifiers(EnumSet.of(SGR.BOLD));
                        graphics.fill(' ');
                        graphics.putString(3, 0, "Text GUI in 100% Java");
                    }
                };
            }
        });
    }

    @Override
    public void afterGUIThreadStarted(WindowBasedTextGUI textGUI) {
        new MessageDialogBuilder()
                .setTitle("Information")
                .setText("Welcome to Lanterna!")
                .build()
                .showDialog(textGUI);
    }
}
