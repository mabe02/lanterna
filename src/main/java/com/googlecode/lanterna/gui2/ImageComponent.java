package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.*;

/**
	
*/
public class ImageComponent extends AbstractInteractableComponent {
    
    TextImage textImage;
    
    public ImageComponent() {
        setTextImage(new BasicTextImage(0,0));
    }
    
    public void setTextImage(TextImage textImage) {
        this.textImage = textImage;
        invalidate();
    }
    
    @Override
    public InteractableRenderer<ImageComponent> createDefaultRenderer() {
        return new InteractableRenderer<ImageComponent>() {
            @Override
            public void drawComponent(TextGUIGraphics graphics, ImageComponent panel) {
                graphics.drawImage(TerminalPosition.TOP_LEFT_CORNER, textImage);
            }
            @Override
            public TerminalSize getPreferredSize(ImageComponent panel) {
                return textImage.getSize();
            }
            @Override
            public TerminalPosition getCursorLocation(ImageComponent component) {
                // when null, lanterna hidden cursor for this component
                return null;
            }
        };
    }
    
    @Override
    public Result handleKeyStroke(com.googlecode.lanterna.input.KeyStroke keyStroke) {
        Result superResult = super.handleKeyStroke(keyStroke);
        
        // just arrows and focus move stuff
        if (superResult != Result.UNHANDLED) {
            return superResult;
        }
        
        return Result.UNHANDLED;
    }
    
}
