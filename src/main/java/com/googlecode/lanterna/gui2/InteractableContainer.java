package com.googlecode.lanterna.gui2;

/**
 * This interface adds the ability to a Container to keep Interactables and switch between them.
 * @author Martin
 */
public interface InteractableContainer extends Container {
    /**
     * Returns true if this container contains the {@code interactable} passed in as the parameter
     *
     * @param interactable {@code interactable} to look for
     * @return {@code true} if the container has {@code interactable}, otherwise {@code false}
     */
    boolean hasInteractable(Interactable interactable);

    /**
     * Given an interactable, find the next one in line to receive focus.
     *
     * @param fromThis Component from which to get the next interactable, or if
     *                 null, pick the first available interactable
     * @return The next interactable component, or null if there are no more
     * interactables in the list
     */
    Interactable nextFocus(Interactable fromThis);

    /**
     * Given an interactable, find the previous one in line to receive focus.
     *
     * @param fromThis Component from which to get the previous interactable,
     *                 or if null, pick the last interactable in the list
     * @return The previous interactable component, or null if there are no more
     * interactables in the list
     */
    Interactable previousFocus(Interactable fromThis);
}
