/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
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
 * Copyright (C) 2010-2020 Martin Berglund
 */
module com.googlecode.lanterna {
    exports com.googlecode.lanterna;
    exports com.googlecode.lanterna.bundle;
    exports com.googlecode.lanterna.graphics;
    exports com.googlecode.lanterna.gui2;
    exports com.googlecode.lanterna.gui2.dialogs;
    exports com.googlecode.lanterna.gui2.table;
    exports com.googlecode.lanterna.input;
    exports com.googlecode.lanterna.screen;
    exports com.googlecode.lanterna.terminal;
    exports com.googlecode.lanterna.terminal.ansi;
    exports com.googlecode.lanterna.terminal.swing;
    exports com.googlecode.lanterna.terminal.virtual;

    requires static java.desktop;
}
