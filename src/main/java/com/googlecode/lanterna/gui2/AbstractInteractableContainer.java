package com.googlecode.lanterna.gui2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by martin on 15/09/14.
 */
public abstract class AbstractInteractableContainer extends AbstractContainer implements InteractableContainer {
    private final List<Component> interactables;

    protected AbstractInteractableContainer() {
        //Make sure the user hasn't implemented Interactable too
        if(this instanceof Interactable) {
            throw new IllegalStateException("Class " + this.getClass().getName() + " is implementing Interactable and " +
                    "extending InteractableContainer, which isn't allowed. Interactable should only be implemented by " +
                    "components that receives input. If you need a component to both contain other components and at " +
                    "the same time receive input, split it up into multiple classes");
        }
        this.interactables = new ArrayList<Component>();
    }

    @Override
    public void addComponent(Component component) {
        super.addComponent(component);
        if (component instanceof Interactable || component instanceof InteractableContainer) {
            synchronized (interactables) {
                if (!interactables.contains(component)) {
                    interactables.add((Interactable) component);
                }
            }
        }
    }

    @Override
    public void removeComponent(Component component) {
        super.removeComponent(component);
        if (component instanceof Interactable) {
            synchronized (interactables) {
                interactables.remove(component);
            }
        }
    }

    @Override
    public boolean hasInteractable(Interactable interactable) {
        for (Component component : interactables) {
            if (component instanceof InteractableContainer) {
                if (((InteractableContainer) (component)).hasInteractable(interactable)) {
                    return true;
                }
            }
            if (component == interactable) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Interactable nextFocus(Interactable previous) {
        boolean chooseNextAvailable = (previous == null);

        for (Component component : interactables) {
            if (chooseNextAvailable) {
                if (component instanceof Interactable) {
                    return (Interactable) component;
                }
                if (component instanceof InteractableContainer) {
                    Interactable firstInteractable = ((InteractableContainer) (component)).nextFocus(null);
                    if (firstInteractable != null) {
                        return firstInteractable;
                    }
                }
                continue;
            }

            if (component == previous) {
                chooseNextAvailable = true;
                continue;
            }

            if (component instanceof InteractableContainer) {
                InteractableContainer ic = (InteractableContainer) component;
                if (ic.hasInteractable(previous)) {
                    Interactable next = ic.nextFocus(previous);
                    if (next == null) {
                        chooseNextAvailable = true;
                    } else {
                        return next;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Interactable previousFocus(Interactable fromThis) {
        boolean chooseNextAvailable = (fromThis == null);

        List<Component> revComponents = new ArrayList<Component>(interactables);
        Collections.reverse(revComponents);

        for (Component component : revComponents) {
            if (chooseNextAvailable) {
                if (component instanceof Interactable) {
                    return (Interactable) component;
                }
                if (component instanceof InteractableContainer) {
                    Interactable lastInteractable = ((InteractableContainer) (component)).previousFocus(null);
                    if (lastInteractable != null) {
                        return lastInteractable;
                    }
                }
                continue;
            }

            if (component == fromThis) {
                chooseNextAvailable = true;
                continue;
            }

            if (component instanceof InteractableContainer) {
                InteractableContainer ic = (InteractableContainer) component;
                if (ic.hasInteractable(fromThis)) {
                    Interactable next = ic.previousFocus(fromThis);
                    if (next == null) {
                        chooseNextAvailable = true;
                    } else {
                        return next;
                    }
                }
            }
        }
        return null;
    }
}
