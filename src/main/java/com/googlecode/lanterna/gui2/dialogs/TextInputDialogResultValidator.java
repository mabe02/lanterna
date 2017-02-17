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
package com.googlecode.lanterna.gui2.dialogs;

/**
 * Interface to implement for custom validation of text input in a {@code TextInputDialog}
 * @author Martin
 */
public interface TextInputDialogResultValidator {
    /**
     * Tests the content in the text box if it is valid or not
     * @param content Current content of the text box
     * @return {@code null} if the content is valid, or an error message explaining what's wrong with the content
     * otherwise
     */
    String validate(String content);
}
