package com.googlecode.lanterna.gui2;

/**
 * Factory class for creating {@code TextGUIThread} objects. This is used by {@code TextGUI} implementations to assign
 * their local {@code TextGUIThread} reference
 */
public interface TextGUIThreadFactory {
    /**
     * Creates a new {@code TextGUIThread} objects
     * @param textGUI {@code TextGUI} this {@code TextGUIThread} should be associated with
     * @return The new {@code TextGUIThread}
     */
    TextGUIThread createTextGUIThread(TextGUI textGUI);
}
