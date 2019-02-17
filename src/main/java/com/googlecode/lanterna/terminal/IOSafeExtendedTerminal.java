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
 * Copyright (C) 2010-2019 Martin Berglund
 */
package com.googlecode.lanterna.terminal;

/**
 * Interface extending ExtendedTerminal that removes the IOException throw clause.
 * 
 * @author Martin
 * @author Andreas
 */
public interface IOSafeExtendedTerminal extends IOSafeTerminal,ExtendedTerminal {

    @Override
    void setTerminalSize(int columns, int rows);

    @Override
    void setTitle(String title);

    @Override
    void pushTitle();

    @Override
    void popTitle();

    @Override
    void iconify();

    @Override
    void deiconify();

    @Override
    void maximize();

    @Override
    void unmaximize();

    @Override
    void setMouseCaptureMode(MouseCaptureMode mouseCaptureMode);

    @Override
    void scrollLines(int firstLine, int lastLine, int distance);
}
