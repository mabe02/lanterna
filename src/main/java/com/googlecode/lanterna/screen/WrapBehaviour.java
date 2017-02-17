/*
 * This file is part of lanterna (http://code.google.com/p/lanterna/).
 * 
 * lanterna is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright (C) 2010-2017 Martin Berglund
 */
package com.googlecode.lanterna.screen;

/**
 * What to do when line length is exceeded.
 *
 * @author avl42
 */
public enum WrapBehaviour {
    /**
     * Never ever leave current line.
     */
    SINGLE_LINE(false,false,false),
    /**
     * Don't wrap lines automatically, but honor explicit line-feeds.
     */
    CLIP(true,false,false),
    /**
     * Wrap at any character boundaries.
     */
    CHAR(true,true,false),
    /**
     * Only wrap at word boundaries. If a single word exceeds line
     * length, it will still be broken to line length.
     */
    WORD(true,true,true);
    
    private final boolean allowLineFeed, autoWrap, keepWords;
    
    WrapBehaviour(boolean allowLineFeed,boolean autoWrap,boolean keepWords) {
        this.allowLineFeed = allowLineFeed;
        this.autoWrap = autoWrap;
        this.keepWords = keepWords;
    }
    
    public boolean allowLineFeed() {
        return allowLineFeed;
    }
    public boolean autoWrap() {
        return autoWrap;
    }
    public boolean keepWords() {
        return keepWords;
    }
}
