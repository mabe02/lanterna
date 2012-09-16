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
 * Copyright (C) 2010-2012 Martin
 */
package com.googlecode.lanterna.gui.util;

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.input.Key;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Martin
 */
public class ShortcutHelper {    
    private final Map<Key.Kind, Action> specialKeysShortcut;
    private final Map<NormalKeyShortcut, Action> normalKeysShortcut;

    public ShortcutHelper() {
        specialKeysShortcut = new EnumMap<Key.Kind, Action>(Key.Kind.class);
        normalKeysShortcut = new HashMap<NormalKeyShortcut, Action>();
    }
    
    public void addShortcut(Key.Kind kindOfKey, Action action) {
        if(kindOfKey == Key.Kind.NormalKey)
            throw new IllegalArgumentException("Can't bind a normal key shortcut using this method, "
                    + "please use addShortcut(char, boolean, boolean, Action) instead");
        if(kindOfKey == null || action == null) 
            throw new IllegalArgumentException("Called addShortcut with either a null kind or null action");
        
        synchronized(specialKeysShortcut) {
            specialKeysShortcut.put(kindOfKey, action);
        }
    }

    public void addShortcut(char character, boolean withCtrl, boolean withAlt, Action action) {
        if(action == null) 
            throw new IllegalArgumentException("Called addShortcut with a null action");
        synchronized(normalKeysShortcut) {
            normalKeysShortcut.put(new NormalKeyShortcut(character, withCtrl, withAlt), action);
        }
    }

    public boolean triggerShortcut(Key key) {
        if(key.getKind() == Key.Kind.NormalKey) {
            NormalKeyShortcut asShortcutKey = new NormalKeyShortcut(key.getCharacter(), key.isCtrlPressed(), key.isAltPressed());
            synchronized(normalKeysShortcut) {
                if(normalKeysShortcut.containsKey(asShortcutKey)) {
                    normalKeysShortcut.get(asShortcutKey).doAction();
                    return true;
                }
                return false;
            }
        }
        else {
            synchronized(specialKeysShortcut) {
                if(specialKeysShortcut.containsKey(key.getKind())) {
                    specialKeysShortcut.get(key.getKind()).doAction();
                    return true;
                }
                return false;
            }
        }
    }
    
    private static class NormalKeyShortcut {
        private final char character;
        private final boolean withCtrl;
        private final boolean withAlt;

        public NormalKeyShortcut(char character, boolean withCtrl, boolean withAlt) {
            this.character = character;
            this.withCtrl = withCtrl;
            this.withAlt = withAlt;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 71 * hash + this.character;
            hash = 71 * hash + (this.withCtrl ? 1 : 0);
            hash = 71 * hash + (this.withAlt ? 1 : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final NormalKeyShortcut other = (NormalKeyShortcut) obj;
            if (this.character != other.character) {
                return false;
            }
            if (this.withCtrl != other.withCtrl) {
                return false;
            }
            if (this.withAlt != other.withAlt) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return (withCtrl ? "ctrl + " : "") + (withAlt ? "alt + " : "") + character;
        }
    }
}
