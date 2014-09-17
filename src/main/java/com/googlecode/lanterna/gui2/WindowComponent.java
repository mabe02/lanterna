package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.Window;

/**
 * Component that can be inside a window
 */
public interface WindowComponent extends Component {
    Window getWindow();
}
