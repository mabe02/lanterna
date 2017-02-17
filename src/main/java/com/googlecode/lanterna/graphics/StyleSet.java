package com.googlecode.lanterna.graphics;

import java.util.Arrays;
import java.util.EnumSet;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;

public interface StyleSet<T extends StyleSet<T>> {

    /**
     * Returns the current background color
     * @return Current background color
     */
    TextColor getBackgroundColor();

    /**
     * Updates the current background color
     * @param backgroundColor New background color
     * @return Itself
     */
    T setBackgroundColor(TextColor backgroundColor);

    /**
     * Returns the current foreground color
     * @return Current foreground color
     */
    TextColor getForegroundColor();

    /**
     * Updates the current foreground color
     * @param foregroundColor New foreground color
     * @return Itself
     */
    T setForegroundColor(TextColor foregroundColor);

    /**
     * Adds zero or more modifiers to the set of currently active modifiers
     * @param modifiers Modifiers to add to the set of currently active modifiers
     * @return Itself
     */
    T enableModifiers(SGR... modifiers);

    /**
     * Removes zero or more modifiers from the set of currently active modifiers
     * @param modifiers Modifiers to remove from the set of currently active modifiers
     * @return Itself
     */
    T disableModifiers(SGR... modifiers);

    /**
     * Sets the active modifiers to exactly the set passed in to this method. Any previous state of which modifiers are
     * enabled doesn't matter.
     * @param modifiers Modifiers to set as active
     * @return Itself
     */
    T setModifiers(EnumSet<SGR> modifiers);

    /**
     * Removes all active modifiers
     * @return Itself
     */
    T clearModifiers();

    /**
     * Returns all the SGR codes that are currently active
     * @return Currently active SGR modifiers
     */
    EnumSet<SGR> getActiveModifiers();

    /**
     * copy colors and set of SGR codes
     * @param source Modifiers to set as active
     * @return Itself
     */
    T setStyleFrom(StyleSet<?> source);
    
    
    class Set implements StyleSet<Set> {
        TextColor foregroundColor, backgroundColor;
        EnumSet<SGR> style = EnumSet.noneOf(SGR.class);
        
        public Set() {}
        public Set(StyleSet<?> source) {
            setStyleFrom(source);
        }
        
        @Override
        public TextColor getBackgroundColor() {
            return backgroundColor;
        }
        @Override
        public Set setBackgroundColor(TextColor backgroundColor) {
            this.backgroundColor = backgroundColor; 
            return this;
        }
        @Override
        public TextColor getForegroundColor() {
            return foregroundColor;
        }
        @Override
        public Set setForegroundColor(TextColor foregroundColor) {
            this.foregroundColor = foregroundColor;
            return this;
        }
        @Override
        public Set enableModifiers(SGR... modifiers) {
            style.addAll(Arrays.asList(modifiers));
            return this;
        }
        @Override
        public Set disableModifiers(SGR... modifiers) {
            style.removeAll(Arrays.asList(modifiers));
            return this;
        }
        @Override
        public Set setModifiers(EnumSet<SGR> modifiers) {
            style.clear(); style.addAll(modifiers);
            return this;
        }
        @Override
        public Set clearModifiers() {
            style.clear();
            return this;
        }
        @Override
        public EnumSet<SGR> getActiveModifiers() {
            return EnumSet.copyOf(style);
        }
        @Override
        public Set setStyleFrom(StyleSet<?> source) {
            setBackgroundColor(source.getBackgroundColor());
            setForegroundColor(source.getForegroundColor());
            setModifiers(source.getActiveModifiers());
            return this;
        }
        
    }
}
