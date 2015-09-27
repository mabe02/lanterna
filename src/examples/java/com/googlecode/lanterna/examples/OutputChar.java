package com.googlecode.lanterna.examples;

import java.io.IOException;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;


/**
 * Creates a terminal and prints a '*' to the (10,10) position.
 * Waits for a keypress then exit.
 * 
 * @author Peter Borkuti
 *
 */
public class OutputChar {

	public static void main(String[] args) throws IOException {
		Terminal terminal = new DefaultTerminalFactory().createTerminal();
		Screen screen = new TerminalScreen(terminal);

		screen.startScreen();
		screen.clear();

		screen.setCharacter(10, 10, new TextCharacter('*'));
		screen.refresh();

		screen.readInput();
		screen.stopScreen();
	}

}
