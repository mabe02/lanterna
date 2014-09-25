package com.googlecode.lanterna.screen;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.io.IOException;

/**
 * VirtualScreen wraps a normal screen and presents it as a screen that has a configurable minimum size; if the real
 * screen is smaller than this size, the presented screen will add scrolling to get around it. To anyone using this
 * class, it will appear and behave just as a normal screen.
 * @author Martin
 */
public class VirtualScreen extends AbstractScreen {
    private final Screen realScreen;
    private TerminalSize minimumSize;
    private TerminalPosition topLeft;

    public VirtualScreen(Screen screen) {
        super(screen.getTerminalSize());
        this.realScreen = screen;
        this.minimumSize = screen.getTerminalSize();
        this.topLeft = TerminalPosition.TOP_LEFT_CORNER;
    }

    public void setMinimumSize(TerminalSize minimumSize) {
        this.minimumSize = minimumSize;
    }

    public TerminalSize getMinimumSize() {
        return minimumSize;
    }

    @Override
    public void startScreen() throws IOException {
        realScreen.startScreen();
    }

    @Override
    public void stopScreen() throws IOException {
        realScreen.stopScreen();
    }

    @Override
    public TextCharacter getFrontCharacter(TerminalPosition position) {
        return null;
    }

    @Override
    public synchronized TerminalSize doResizeIfNecessary() {
        TerminalSize underlyingSize = realScreen.doResizeIfNecessary();
        if(underlyingSize == null) {
            return null;
        }

        TerminalSize newVirtualSize = getTerminalSize().max(underlyingSize);
        if(topLeft.getColumn() + underlyingSize.getColumns() > newVirtualSize.getColumns()) {
            topLeft = topLeft.withColumn(newVirtualSize.getColumns() - underlyingSize.getColumns());
        }
        if(topLeft.getRow() + underlyingSize.getRows() > newVirtualSize.getRows()) {
            topLeft = topLeft.withRow(newVirtualSize.getRows() - underlyingSize.getRows());
        }
        System.out.println("newVirtualSize = " + newVirtualSize + ", underlyingSize = " + underlyingSize + ", topLeft = " + topLeft);
        if(!getTerminalSize().equals(newVirtualSize)) {
            addResizeRequest(newVirtualSize);
            return super.doResizeIfNecessary();
        }
        return newVirtualSize;
    }

    @Override
    public void refresh(RefreshType refreshType) throws IOException {
        //Copy the rows
        if(realScreen instanceof AbstractScreen) {
            AbstractScreen asAbstractScreen = (AbstractScreen)realScreen;
            TerminalSize realSize = realScreen.getTerminalSize();
            getBackBuffer().copyTo(asAbstractScreen.getBackBuffer(), topLeft.getRow(), realSize.getRows(), topLeft.getColumn(), realSize.getColumns());
        }
        else {
            TerminalSize realSize = realScreen.getTerminalSize();
            for(int y = 0; y < realSize.getRows(); y++) {
                for(int x = 0; x < realSize.getColumns(); x++) {
                    realScreen.setCharacter(x, y, getBackBuffer().getCharacterAt(x, y));
                }
            }
        }
        realScreen.refresh(refreshType);
    }

    @Override
    public KeyStroke pollInput() throws IOException {
        return filter(realScreen.pollInput());
    }

    @Override
    public KeyStroke readInput() throws IOException {
        return filter(realScreen.readInput());
    }

    private KeyStroke filter(KeyStroke keyStroke) throws IOException {
        if(keyStroke == null) {
            return null;
        }
        else if(keyStroke.isCtrlDown() && keyStroke.getKeyType() == KeyType.ArrowLeft) {
            if(topLeft.getColumn() > 0) {
                topLeft = topLeft.withRelativeColumn(-1);
                refresh();
                return null;
            }
        }
        else if(keyStroke.isCtrlDown() && keyStroke.getKeyType() == KeyType.ArrowRight) {
            if(topLeft.getColumn() + realScreen.getTerminalSize().getColumns() < getTerminalSize().getColumns()) {
                topLeft = topLeft.withRelativeColumn(1);
                refresh();
                return null;
            }
        }
        else if(keyStroke.isCtrlDown() && keyStroke.getKeyType() == KeyType.ArrowUp) {
            if(topLeft.getRow() > 0) {
                topLeft = topLeft.withRelativeRow(-1);
                refresh();
                return null;
            }
        }
        else if(keyStroke.isCtrlDown() && keyStroke.getKeyType() == KeyType.ArrowDown) {
            if(topLeft.getRow() + realScreen.getTerminalSize().getRows() < getTerminalSize().getRows()) {
                topLeft = topLeft.withRelativeRow(1);
                refresh();
                return null;
            }
        }
        return keyStroke;
    }
}
