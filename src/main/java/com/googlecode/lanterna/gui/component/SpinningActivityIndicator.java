package com.googlecode.lanterna.gui.component;

import java.util.*;

import com.googlecode.lanterna.gui.*;
import com.googlecode.lanterna.terminal.*;
                                
/**
Indicates activity by cycling through a set of characters.
*/
public class SpinningActivityIndicator extends ActivityIndicator {
	/** hyphen, backslash, pipe, forward slash */
	public static char[] BARS = new char[] { '-', '\\',  '|', '/' };
	/** caret, greater-than, uppercase 'V', less-than */
	public static char[] CHEVRONS = new char[] { '^', '>', 'V', '<' };
	/** Unicode dice characters (not supported by all terminals) */
	public static char[] DICE = new char[] { '\u2680', '\u2681', '\u2682', '\u2683', '\u2684', '\u2685' };
	/** Unicode trigram characters (not supported by all terminals) */
	public static char[] TRIGRAMS = new char[] { '\u2630', '\u2631', '\u2632', '\u2633', '\u2634', '\u2635', '\u2636', '\u2637' };
	
	private static char[] states = BARS;
	private static int index = 0;
		
	/** Creates an ActivityIndicator which cycles through the {@link #BARS} characters. */
	public SpinningActivityIndicator() {
		this(BARS);
	}
	
	public SpinningActivityIndicator(char[] chars) {
    	Set<Character> set = new HashSet<Character>();
    	for (Character ch : chars) 
    		set.add(ch);
    	
    	if (set.size() < 2)
    		throw new IllegalArgumentException("you must use at least two different characters");
    	
    	states = chars;
    	index = 0;
	}
	
	@Override
	public void tick() {
		if (++index >= states.length)
			index = 0;

    	invalidate();
	}
	
	@Override
	public void clear() {
		index = -1;
	}
	
	@Override
    public void repaint(TextGraphics graphics) {
    	if (index >= 0)
    		graphics.drawString(0, 0, states[index] + "");
    	else
    		graphics.drawString(0, 0, " ");
    }

	
	@Override
    protected TerminalSize calculatePreferredSize() {
    	return new TerminalSize(1,1);
	}
}
