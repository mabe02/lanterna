package com.googlecode.lanterna.gui2;


import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

/**
 *
 * @author Martin
 */
public abstract class AbstractBorder extends AbstractRenderableComponent implements Border {

    private Component component;

    public AbstractBorder() {
        component = null;
    }
    
    @Override
    public boolean hasInteractable(Interactable interactable) {
        return interactable != null && (interactable == component ||
                (component instanceof InteractableComposite && ((InteractableComposite)component).hasInteractable(interactable)));
    }

    @Override
    public Interactable nextFocus(Interactable fromThis) {
        if(fromThis == null && component instanceof Interactable) {
            return (Interactable)component;
        }
        else if(component instanceof InteractableComposite) {
            return ((InteractableComposite)component).nextFocus(fromThis);
        }
        return null;
    }

    @Override
    public Interactable previousFocus(Interactable fromThis) {
        if(fromThis == null && component instanceof Interactable) {
            return (Interactable)component;
        }
        else if(component instanceof InteractableComposite) {
            return ((InteractableComposite)component).previousFocus(fromThis);
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
        this.component.setPosition(getWrappedComponentTopLeftOffset());
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
        component.setParent(null);
    }

    @Override
    public boolean isInvalid() {
        return component != null && component.isInvalid();
    }

    @Override
    public AbstractBorder setSize(TerminalSize size) {
        super.setSize(size);
        component.setSize(getWrappedComponentSize(size));
        return this;
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

    @Override
    protected BorderRenderer getRenderer() {
        return (BorderRenderer)super.getRenderer();
    }
    
    protected Component getWrappedComponent() {
        return component;
    }

    private TerminalPosition getWrappedComponentTopLeftOffset() {
        return getRenderer().getWrappedComponentTopLeftOffset();
    }

    private TerminalSize getWrappedComponentSize(TerminalSize borderSize) {
        return getRenderer().getWrappedComponentSize(borderSize);
    }
}
