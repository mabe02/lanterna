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
 * Copyright (C) 2010-2012 mabe02
 */

package com.googlecode.lanterna.gui;

import com.googlecode.lanterna.gui.listener.ComponentListener;
import com.googlecode.lanterna.terminal.TerminalSize;

/**
 *
 * @author mabe02
 */
public interface Component
{
    Container getParent();
    void addComponentListener(ComponentListener cl);
    void removeComponentListener(ComponentListener cl);
    void repaint(TextGraphics graphics);
    void setVisible(boolean visible);
    boolean isVisible();
    TerminalSize getPreferredSize();
}
