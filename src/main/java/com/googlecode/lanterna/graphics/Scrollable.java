package com.googlecode.lanterna.graphics;

import java.io.IOException;

public interface Scrollable {
    /**
     * Scroll a range of lines of this Scrollable according to given distance.
     * 
     * If scroll-range is empty (firstLine > lastLine || distance == 0) then 
     * this method does nothing.
     * 
     * Lines that are scrolled away from are cleared.
     * 
     * If absolute value of distance is equal or greater than number of lines
     * in range, then all lines within the range will be cleared.
     *  
     * @param firstLine first line of the range to be scrolled (top line is 0)
     * @param lastLine last (inclusive) line of the range to be scrolled
     * @param distance if > 0: move lines up, else if < 0: move lines down.
     */
    void scrollLines(int firstLine, int lastLine, int distance) throws IOException;
}
