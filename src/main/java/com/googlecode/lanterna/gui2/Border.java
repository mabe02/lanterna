package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

/**
 *
 * @author Martin
 */
public interface Border extends Component, InteractableComposite {
    public static interface BorderRenderer extends ComponentRenderer {
        /**
         * How large is the offset from the top left corner of the border to the top left corner of the wrapped component?
         * @return Position of the wrapped components top left position, relative to the top left corner of the border
         */
        TerminalPosition getWrappedComponentTopLeftOffset();

        /**
         * Given a total size of the border composite and it's wrapped component, how large would the actual wrapped
         * component be?
         * @param borderSize Size to calculate for, this should be the total size of the border and the inner component
         * @return Size of the inner component if the total size of inner + border is borderSize
         */
        TerminalSize getWrappedComponentSize(TerminalSize borderSize);
    }
}
