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
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;
import java.util.Collections;

/**
 * Simple container for other components
 * @author Martin
 */
public class Panel extends AbstractInteractableContainer {
    
    private LayoutManager layoutManager;
    private boolean needsReLayout;

    public Panel() {
        layoutManager = new LinearLayout();
        needsReLayout = false;
    }
    
    public void setLayoutManager(LayoutManager layoutManager) {
        if(layoutManager == null) {
            throw new IllegalArgumentException("Cannot set a null layout manager");
        }
        this.layoutManager = layoutManager;
        onStructureChanged();
    }

    @Override
    public void drawComponent(TextGUIGraphics graphics) {
        if(needsReLayout) {
            layout(graphics.getSize());
        }
        super.drawComponent(graphics);
    }

    @Override
    public TerminalSize calculatePreferredSize() {
        setPreferredSize(layoutManager.getPreferredSize(getComponents()));
        return getPreferredSize();
    }    
    
    @Override
    protected void onStructureChanged() {
        needsReLayout = true;
        setPreferredSize(null);
        invalidate();
    }

    private void layout(TerminalSize size) {
        layoutManager.doLayout(size, getComponents());
        needsReLayout = false;
    }
}
