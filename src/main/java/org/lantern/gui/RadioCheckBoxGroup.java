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
 * Copyright (C) 2010-2011 mabe02
 */

package org.lantern.gui;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mabe02
 */
public class RadioCheckBoxGroup
{
    private final List<RadioCheckBox> radioBoxes;
    private int selectedIndex;

    public RadioCheckBoxGroup()
    {
        this.radioBoxes = new ArrayList<RadioCheckBox>();
        this.selectedIndex = 0;
    }

    boolean isSelected(RadioCheckBox radioBox)
    {
        if(radioBox == null)
            return false;

        return radioBoxes.indexOf(radioBox) == selectedIndex;
    }

    void setSelected(RadioCheckBox radioBox)
    {
        if(radioBoxes.indexOf(radioBox) == -1)
            return;

        selectedIndex = radioBoxes.indexOf(radioBox);
    }

    void addRadioBox(RadioCheckBox radioCheckBox)
    {
        radioBoxes.add(radioCheckBox);
    }

    public void removeRadioCheckBox(RadioCheckBox radioCheckBox)
    {
        radioBoxes.remove(radioCheckBox);
    }
}
