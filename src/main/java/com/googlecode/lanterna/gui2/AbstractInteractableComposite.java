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

/**
 *
 * @author martin
 */
public abstract class AbstractInteractableComposite extends AbstractCompositeContainer implements InteractableContainer {
    
    @Override
    public boolean hasInteractable(Interactable interactable) {
        return interactable != null && (interactable == getComponent() ||
                (getComponent() instanceof InteractableContainer && ((InteractableContainer)getComponent()).hasInteractable(interactable)));
    }

    @Override
    public Interactable nextFocus(Interactable fromThis) {
        if(fromThis == null && getComponent() instanceof Interactable) {
            return (Interactable)getComponent();
        }
        else if(getComponent() instanceof InteractableContainer) {
            return ((InteractableContainer)getComponent()).nextFocus(fromThis);
        }
        return null;
    }

    @Override
    public Interactable previousFocus(Interactable fromThis) {
        if(fromThis == null && getComponent() instanceof Interactable) {
            return (Interactable)getComponent();
        }
        else if(getComponent() instanceof InteractableContainer) {
            return ((InteractableContainer)getComponent()).previousFocus(fromThis);
        }
        return null;
    }

    @Override
    public void updateLookupMap(InteractableLookupMap interactableLookupMap) {
        if(getComponent() instanceof InteractableContainer) {
            ((InteractableContainer)getComponent()).updateLookupMap(interactableLookupMap);
        }
        else if(getComponent() instanceof Interactable) {
            interactableLookupMap.add((Interactable)getComponent());
        }
    }
}
