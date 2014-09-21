/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.lanterna.gui2;

/**
 *
 * @author Martin
 */
public interface Composite extends TextGUIElement {

    void addComponent(Component component);

    boolean containsComponent(Component component);

    void removeComponent(Component component);
    
}
