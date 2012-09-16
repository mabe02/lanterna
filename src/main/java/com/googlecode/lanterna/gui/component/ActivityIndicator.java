package com.googlecode.lanterna.gui.component;

import com.googlecode.lanterna.gui.*;
               
/**
For indicating that the program is working on something and not frozen.
*/
public abstract class ActivityIndicator extends AbstractComponent {

	/** Trigger an update indicating that activity occured. */
	public abstract void tick();
	
	public abstract void clear();
	
}
