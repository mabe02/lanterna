package com.googlecode.lanterna.gui2;


/**
 *
 * @author Martin
 */
public abstract class AbstractBorder extends AbstractComponent implements Border {

    private Component component;

    public AbstractBorder() {
        component = null;
    }
    
    @Override
    public boolean hasInteractable(Interactable interactable) {
        return interactable != null && interactable == component;
    }

    @Override
    public Interactable nextFocus(Interactable fromThis) {
        if(fromThis == null && component instanceof Interactable) {
            return (Interactable)component;
        }
        return null;
    }

    @Override
    public Interactable previousFocus(Interactable fromThis) {
        if(fromThis == null && component instanceof Interactable) {
            return (Interactable)component;
        }
        return null;
    }

    @Override
    public void addComponent(Component component) {
        if(this.component != null) {
            throw new IllegalStateException("Cannot add component " + component.toString() + " to Border " + toString() + 
                    " because border already has a component");
        }
        this.component = component;
        this.component.setParent(this);
    }

    @Override
    public boolean containsComponent(Component component) {
        return this.component == component;
    }

    @Override
    public void removeComponent(Component component) {
        if(this.component != component) {
            throw new IllegalArgumentException("Cannot remove component " + component + " from Border " + toString() + 
                    " because Border had component " + this.component);
        }
        this.component = null;
        this.component.setParent(null);
    }

    @Override
    public boolean isInvalid() {
        if(component != null) {
            return component.isInvalid();
        }
        return false;
    }

    @Override
    public void updateLookupMap(InteractableLookupMap interactableLookupMap) {
        if(component instanceof InteractableComposite) {
            ((InteractableComposite)component).updateLookupMap(interactableLookupMap);
        }
        else if(component instanceof Interactable) {
            interactableLookupMap.add((Interactable)component);
        }
    }
    
    protected Component getWrappedComponent() {
        return component;
    }
}
