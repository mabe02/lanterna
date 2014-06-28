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
 * Copyright (C) 2010-2014 Martin
 */
package com.googlecode.lanterna.screen;

import com.googlecode.lanterna.CJKUtils;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.io.IOException;

/**
 * This class wraps another Screen and presents it as having half the number of columns. The purpose for this is to 
 * make drawing text graphics easier, since normally a terminal's individual 'cell' is twice as tall as it is wide.
 * When you put a character to a SquaredScreen, it will actually put the same character twice, which won't make much 
 * sense for text but does for solid blocks. For a demonstation of this, take a look at the Triangle test in 
 * com.googlecode.lanterna.screen and compare it when running with the --square parameter and without.
 * @author Martin
 */
public class SquaredScreen extends AbstractScreen {
    private final Screen backend;

    public SquaredScreen(Screen backend) {
        this.backend = backend;
    }

    @Override
    public void startScreen() throws IOException {
        backend.startScreen();
    }

    @Override
    public void stopScreen() throws IOException {
        backend.stopScreen();
    }

    @Override
    public void clear() {
        backend.clear();
    }

    @Override
    public TerminalPosition getCursorPosition() {
        TerminalPosition actualPosition = backend.getCursorPosition();
        return actualPosition.withColumn(actualPosition.getColumn() / 2);
    }

    @Override
    public void setCursorPosition(TerminalPosition position) {
        backend.setCursorPosition(position.withColumn(position.getColumn() * 2));
    }

    @Override
    public TabBehaviour getTabBehaviour() {
        return backend.getTabBehaviour();
    }

    @Override
    public void setTabBehaviour(TabBehaviour tabBehaviour) {
        backend.setTabBehaviour(tabBehaviour);
    }

    @Override
    public TerminalSize getTerminalSize() {
        TerminalSize actualSize = backend.getTerminalSize();
        return actualSize.withColumns(actualSize.getColumns() / 2);
    }

    @Override
    public void setCharacter(int column, int row, ScreenCharacter screenCharacter) {
        column *= 2;
        backend.setCharacter(column, row, screenCharacter);
        if(!CJKUtils.isCharCJK(screenCharacter.getCharacter())) {
            backend.setCharacter(column + 1, row, screenCharacter);
        }
    }

    @Override
    public ScreenCharacter getFrontCharacter(TerminalPosition position) {
        return backend.getFrontCharacter(position.withColumn(position.getColumn() * 2));
    }

    @Override
    public ScreenCharacter getBackCharacter(TerminalPosition position) {
        return backend.getBackCharacter(position.withColumn(position.getColumn() * 2));
    }

    @Override
    public KeyStroke readInput() throws IOException {
        return backend.readInput();
    }

    @Override
    public void refresh() throws IOException {
        backend.refresh();
    }

    @Override
    public void refresh(RefreshType refreshType) throws IOException {
        backend.refresh(refreshType);
    }

    @Override
    public TerminalSize doResizeIfNecessary() {
        TerminalSize resized = backend.doResizeIfNecessary();
        if(resized != null) {
            resized = resized.withColumns(resized.getColumns() / 2);
        }
        return resized;
    }
}
