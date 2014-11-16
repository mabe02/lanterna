package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.BasicTextImage;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.TextImage;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * Created by martin on 27/10/14.
 */
public class FullScreenTextGUITest {
    public static void main(String[] args) throws IOException, InterruptedException {
        Screen screen = new TestTerminalFactory(args).createScreen();
        screen.startScreen();
        FullScreenTextGUI textGUI = new FullScreenTextGUI(screen);
        textGUI.addListener(new TextGUI.Listener() {
            @Override
            public boolean onUnhandledKeyStroke(TextGUI textGUI, KeyStroke key) {
                if(key.getKeyType() == KeyType.Escape) {
                    textGUI.getGUIThread().stop();
                    return true;
                }
                return false;
            }
        });
        try {
            textGUI.setComponent(new BIOS());
            TextGUIThread guiThread = textGUI.getGUIThread();
            guiThread.start();
            guiThread.waitForStop();
        }
        finally {
            screen.stopScreen();
        }
    }

    private static class BIOS extends Panel {
        private final TextImage background;
        private final Label helpLabel;
        
        private BIOS() {
            setLayoutManager(new AbsoluteLayout());
            background = createBackground();
            
            helpLabel = new Label("");
            helpLabel.setForegroundColor(TextColor.ANSI.YELLOW);
            helpLabel.setBackgroundColor(TextColor.ANSI.BLUE);
            helpLabel.addStyle(SGR.BOLD);
            
            BIOSButton button1 = new BIOSButton("Standard Lanterna Features", "Time, Date, Type...");
            BIOSButton button2 = new BIOSButton("Advanced Lanterna Features", "Well, what could this possibly be?");
            BIOSButton button3 = new BIOSButton("Advanced Terminal Features", "As you can see, I can change the description here");
            BIOSButton button4 = new BIOSButton("Unintegrated Peripherals", "Joystick, VirtualBoy, Coffee Machines, ...");
            BIOSButton button5 = new BIOSButton("Power Management Setup", "Only for superheroes!");
            BIOSButton button6 = new BIOSButton("Non-PnP/ISA Configurations", "Going back to the '80s");
            BIOSButton button7 = new BIOSButton("Terminal Health Status", "For those who really *care*");
            BIOSButton button8 = new BIOSButton("Frequency/Current Control", "To overclock your terminal; NOT covered by warranty!");
            BIOSButton button9 = new BIOSButton("Load Fail-Safe Defaults", "So you can't go wrong");
            BIOSButton button10 = new BIOSButton("Load Optimized Defaults", "And still you play the sycophant and revel in my pain");
            BIOSButton button11 = new BIOSButton("Set Supervisor Password", "To make sure no one else can touch your dear terminal");
            BIOSButton button12 = new BIOSButton("Set User Password", "What would you even need this for?");
            BIOSButton button13 = new BIOSButton("Save & Exit Setup", "...and you can have some cake");
            BIOSButton button14 = new BIOSButton("Exit Without Saving", "僕の事が思い出せなくても泣かないでね");
            
            button1.setSize(new TerminalSize(35, 1));
            button1.setPosition(new TerminalPosition(3, 3));
            button2.setSize(new TerminalSize(35, 1));
            button2.setPosition(new TerminalPosition(3, 5));
            button3.setSize(new TerminalSize(35, 1));
            button3.setPosition(new TerminalPosition(3, 7));
            button4.setSize(new TerminalSize(35, 1));
            button4.setPosition(new TerminalPosition(3, 9));
            button5.setSize(new TerminalSize(35, 1));
            button5.setPosition(new TerminalPosition(3, 11));
            button6.setSize(new TerminalSize(35, 1));
            button6.setPosition(new TerminalPosition(3, 13));
            button7.setSize(new TerminalSize(35, 1));
            button7.setPosition(new TerminalPosition(3, 15));
            
            button8.setSize(new TerminalSize(35, 1));
            button8.setPosition(new TerminalPosition(43, 3));
            button9.setSize(new TerminalSize(35, 1));
            button9.setPosition(new TerminalPosition(43, 5));
            button10.setSize(new TerminalSize(35, 1));
            button10.setPosition(new TerminalPosition(43, 7));
            button11.setSize(new TerminalSize(35, 1));
            button11.setPosition(new TerminalPosition(43, 9));
            button12.setSize(new TerminalSize(35, 1));
            button12.setPosition(new TerminalPosition(43, 11));
            button13.setSize(new TerminalSize(35, 1));
            button13.setPosition(new TerminalPosition(43, 13));
            button14.setSize(new TerminalSize(35, 1));
            button14.setPosition(new TerminalPosition(43, 15));
            
            helpLabel.setPosition(new TerminalPosition(2, 22));
            helpLabel.setSize(new TerminalSize(76, 1));
            addComponent(helpLabel);
            for(BIOSButton button: Arrays.asList(button1, button2, button3, button4, button5, button6, button7, button8, button9, button10, button11, button12, button13, button14)) {
                addComponent(button);
            }
            addComponent(button14);
        }

        private TextImage createBackground() {
            BasicTextImage image = new BasicTextImage(80, 25);
            TextGraphics graphics = image.newTextGraphics();
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.setBackgroundColor(TextColor.ANSI.BLUE);
            graphics.fill(' ');

            graphics.enableModifiers(SGR.BOLD);

            graphics.putString(7, 0, "Reminds you of some BIOS, doesn't it?");
            graphics.setCharacter(0, 1, Symbols.DOUBLE_LINE_TOP_LEFT_CORNER);
            graphics.drawLine(1, 1, 78, 1, Symbols.DOUBLE_LINE_HORIZONTAL);
            graphics.setCharacter(79, 1, Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER);
            graphics.drawLine(79, 2, 79, 23, Symbols.DOUBLE_LINE_VERTICAL);
            graphics.setCharacter(79, 24, Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER);
            graphics.drawLine(1, 24, 78, 24, Symbols.DOUBLE_LINE_HORIZONTAL);
            graphics.setCharacter(0, 24, Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER);
            graphics.drawLine(0, 2, 0, 23, Symbols.DOUBLE_LINE_VERTICAL);

            graphics.setCharacter(0, 17, Symbols.DOUBLE_LINE_T_SINGLE_RIGHT);
            graphics.drawLine(1, 17, 78, 17, Symbols.SINGLE_LINE_HORIZONTAL);
            graphics.setCharacter(79, 17, Symbols.DOUBLE_LINE_T_SINGLE_LEFT);
            graphics.setCharacter(40, 17, Symbols.SINGLE_LINE_T_UP);
            graphics.drawLine(40, 2, 40, 16, Symbols.SINGLE_LINE_VERTICAL);
            graphics.setCharacter(40, 1, Symbols.DOUBLE_LINE_T_SINGLE_DOWN);

            graphics.setCharacter(0, 20, Symbols.DOUBLE_LINE_T_SINGLE_RIGHT);
            graphics.drawLine(1, 20, 78, 20, Symbols.SINGLE_LINE_HORIZONTAL);
            graphics.setCharacter(79, 20, Symbols.DOUBLE_LINE_T_SINGLE_LEFT);
            
            graphics.putString(2, 18, "Esc : Quit");
            graphics.putString(42, 18, Symbols.ARROW_UP + " " + Symbols.ARROW_DOWN + " " + Symbols.ARROW_RIGHT + " " + 
                    Symbols.ARROW_LEFT + "   : Select Item");
            graphics.putString(2, 19, "F10 : Save & Exit Setup");
            return image;
        }
        
        @Override
        protected ComponentRenderer createDefaultRenderer() {
            final ComponentRenderer panelRenderer = super.createDefaultRenderer();
            return new ComponentRenderer() {
                @Override
                public TerminalSize getPreferredSize(Component component) {
                    return new TerminalSize(80, 24);
                }

                @Override
                public void drawComponent(TextGUIGraphics graphics, Component component) {
                    //Clear all data
                    graphics.setBackgroundColor(TextColor.ANSI.BLACK).fill(' ');
                    
                    //Draw the background image
                    graphics.drawImage(TerminalPosition.TOP_LEFT_CORNER, background);
                    
                    //Then draw all the child components
                    panelRenderer.drawComponent(graphics, BIOS.this);
                }
            };
        }
        
        private class BIOSButton extends Button {
            private final String description;

            public BIOSButton(String label, String description) {
                super(label);
                this.description = description;
            }

            @Override
            protected void afterEnterFocus(FocusChangeDirection direction, Interactable previouslyInFocus) {
                helpLabel.setText(description);
            }

            @Override
            protected ButtonRenderer createDefaultRenderer() {
                return new ButtonRenderer() {
                    @Override
                    public TerminalPosition getCursorLocation(Button component) {
                        return null;
                    }

                    @Override
                    public TerminalSize getPreferredSize(Button component) {
                        return new TerminalSize(CJKUtils.getTrueWidth(getLabel()), 1);
                    }

                    @Override
                    public void drawComponent(TextGUIGraphics graphics, Button component) {
                        graphics.setBackgroundColor(TextColor.ANSI.BLUE);
                        graphics.fill(' ');
                        if(isFocused()) {
                            graphics.setForegroundColor(TextColor.ANSI.WHITE);
                            graphics.setBackgroundColor(TextColor.ANSI.RED);
                            graphics.setModifiers(EnumSet.of(SGR.BOLD));
                            graphics.putString(0, 0, "  " + getLabel());
                        }
                        else {
                            graphics.setForegroundColor(TextColor.ANSI.YELLOW);
                            graphics.setBackgroundColor(TextColor.ANSI.BLUE);
                            graphics.setModifiers(EnumSet.of(SGR.BOLD));
                            graphics.putString(0, 0, "  " + getLabel());
                        }
                    }
                };
            }
        }
    }
}
