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
 * Copyright (C) 2010-2011 mabe02
 */

package org.lantern.gui;

import java.util.ArrayList;
import java.util.Iterator;
import org.lantern.gui.theme.Theme;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.lantern.LanternException;
import org.lantern.gui.listener.WindowAdapter;
import org.lantern.input.Key;
import org.lantern.screen.Screen;
import org.lantern.terminal.Terminal;
import org.lantern.terminal.TerminalPosition;
import org.lantern.terminal.TerminalSize;

/**
 *
 * @author mabe02
 */
public class GUIScreen
{
    private final Screen screen;
    private final LinkedList windowStack;
    private final Queue actionToRunInEventThread;
    private String title;
    private boolean showMemoryUsage;
    private Theme guiTheme;
    private boolean needsRefresh;
    private Thread eventThread;

    public GUIScreen(final Screen screen)
    {
        this.title = "";
        this.showMemoryUsage = false;
        this.screen = screen;
        this.guiTheme = Theme.getDefaultTheme();
        this.windowStack = new LinkedList();
        this.actionToRunInEventThread = new LinkedList();
        this.needsRefresh = false;
        this.eventThread = Thread.currentThread();  //We'll be expecting the thread who created us is the same as will be the event thread later
    }

    public void setTheme(Theme newTheme)
    {
        if(newTheme == null)
            return;
        
        this.guiTheme = newTheme;
        needsRefresh = true;
    }

    public void setTitle(String title)
    {
        if(title == null)
            title = "";
        
        this.title = title;
    }
    
    private synchronized void repaint() throws LanternException
    {
        final TextGraphics textGraphics = new TextGraphics(new TerminalPosition(0, 0),
                new TerminalSize(screen.getTerminalSize()), screen, guiTheme);

        textGraphics.applyThemeItem(guiTheme.getItem(Theme.Category.ScreenBackground));

        //Clear the background
        textGraphics.fillRectangle(' ', new TerminalPosition(0, 0), new TerminalSize(screen.getTerminalSize()));

        //Write the title
        textGraphics.drawString(3, 0, title, new Terminal.Style[0]);

        //Write memory usage
        if(showMemoryUsage)
            drawMemoryUsage(textGraphics);

        //Go through the windows
        Iterator iter = windowStack.iterator();
        while(iter.hasNext()) {
            WindowPlacement windowPlacement = (WindowPlacement)iter.next();
            if(hasSoloWindowAbove(windowPlacement))
                continue;
            
            TerminalPosition topLeft = windowPlacement.getTopLeft();
            TerminalSize preferredSize = windowPlacement.getWindow().getPreferredSize();
            if(windowPlacement.positionPolicy == Position.CENTER) {
                if(windowPlacement.getWindow().maximisesHorisontally())
                    topLeft.setColumn(2);
                else
                    topLeft.setColumn((screen.getWidth() / 2) - (preferredSize.getColumns() / 2));

                if(windowPlacement.getWindow().maximisesVertically())
                    topLeft.setRow(1);
                else
                    topLeft.setRow((screen.getHeight() / 2) - (preferredSize.getRows() / 2));
            }            
            int maxSizeWidth = screen.getWidth() - windowPlacement.getTopLeft().getColumn() - 1;
            int maxSizeHeight = screen.getHeight() - windowPlacement.getTopLeft().getRow() - 1;

            if(preferredSize.getColumns() > maxSizeWidth || windowPlacement.getWindow().maximisesHorisontally())
                preferredSize.setColumns(maxSizeWidth);
            if(preferredSize.getRows() > maxSizeHeight || windowPlacement.getWindow().maximisesVertically())
                preferredSize.setRows(maxSizeHeight);
            
            if(topLeft.getColumn() < 0)
                topLeft.setColumn(0);
            if(topLeft.getRow() < 0)
                topLeft.setRow(0);

            TextGraphics subGraphics = textGraphics.subAreaGraphics(topLeft,
                    new TerminalSize(preferredSize.getColumns(), preferredSize.getRows()));

            //First draw the shadow
            textGraphics.applyThemeItem(guiTheme.getItem(Theme.Category.Shadow));
            textGraphics.fillRectangle(' ', new TerminalPosition(topLeft.getColumn() + 2, topLeft.getRow() + 1),
                    new TerminalSize(subGraphics.getWidth(), subGraphics.getHeight()));

            //Then draw the window
            windowPlacement.getWindow().repaint(subGraphics);
        }

        if(windowStack.size() > 0 && ((WindowPlacement)windowStack.getLast()).getWindow().getWindowHotspotPosition() != null)
            screen.setCursorPosition(((WindowPlacement)windowStack.getLast()).getWindow().getWindowHotspotPosition());
        else
            screen.setCursorPosition(new TerminalPosition(screen.getWidth() - 1, screen.getHeight() - 1));
        screen.refresh();
    }

    private void update() throws LanternException
    {
        if(needsRefresh || !screen.verifySize()) {
            repaint();
            needsRefresh = false;
        }
    }

    public void invalidate()
    {
        needsRefresh = true;
    }

    boolean isWindowTopLevel(Window window)
    {
        if(windowStack.size() > 0 && ((WindowPlacement)windowStack.getLast()).getWindow() == window)
            return true;
        else
            return false;
    }

    private void doEventLoop() throws LanternException
    {
        int currentStackLength = windowStack.size();
        if(currentStackLength == 0)
            return;

        while(true) {
            if(currentStackLength > windowStack.size()) {
                //The window was removed from the stack ( = it was closed)
                break;
            }

            synchronized(actionToRunInEventThread) {
                List actions = new ArrayList(actionToRunInEventThread);
                actionToRunInEventThread.clear();
                for(int i = 0; i < actions.size(); i++)
                    ((Action)actions.get(i)).doAction();
            }

            update();

            Key nextKey = screen.readInput();
            if(nextKey != null) {
                ((WindowPlacement)windowStack.getLast()).window.onKeyPressed(nextKey);
                invalidate();
            }
            else {
                try {
                    Thread.sleep(5);
                }
                catch(InterruptedException e) {}
            }
        }
    }

    public void showWindow(Window window) throws LanternException
    {
        showWindow(window, Position.OVERLAPPING);
    }

    public void showWindow(Window window, Position position) throws LanternException
    {
        if(window == null)
            return;
        if(position == null)
            position = Position.OVERLAPPING;

        int newWindowX = 2;
        int newWindowY = 1;

        if(position == Position.OVERLAPPING &&
                windowStack.size() > 0) {
            WindowPlacement lastWindow = ((WindowPlacement)windowStack.getLast());
            if(lastWindow.getPositionPolicy() != Position.CENTER) {
                newWindowX = lastWindow.getTopLeft().getColumn() + 2;
                newWindowY = lastWindow.getTopLeft().getRow() + 1;
            }
        }

        window.addWindowListener(new WindowAdapter() {
            public void onWindowInvalidated(Window window)
            {
                needsRefresh = true;
            }
        });
        windowStack.add(new WindowPlacement(window, position, new TerminalPosition(newWindowX, newWindowY)));
        window.setOwner(this);
        window.onVisible();
        needsRefresh = true;
        doEventLoop();
    }

    public void closeWindow(Window window)
    {
        if(windowStack.size() == 0)
            return;
        if(((WindowPlacement)windowStack.getLast()).window != window)
            return;

        WindowPlacement windowPlacement = (WindowPlacement)windowStack.removeLast();
        windowPlacement.getWindow().onClosed();
    }

    public void runInEventThread(Action codeToRun)
    {
        synchronized(actionToRunInEventThread) {
            actionToRunInEventThread.add(codeToRun);
        }
    }

    public boolean isInEventThread()
    {
        return eventThread == Thread.currentThread();
    }

    public void setShowMemoryUsage(boolean showMemoryUsage)
    {
        this.showMemoryUsage = showMemoryUsage;
    }

    public boolean isShowingMemoryUsage()
    {
        return showMemoryUsage;
    }

    public static class Position
    {
        public static final int OVERLAPPING_ID = 1;
        public static final int NEW_CORNER_WINDOW_ID = 2;
        public static final int CENTER_ID = 3;
        
        public static final Position OVERLAPPING = new Position(OVERLAPPING_ID);
        public static final Position NEW_CORNER_WINDOW = new Position(NEW_CORNER_WINDOW_ID);
        public static final Position CENTER = new Position(CENTER_ID);
        
        private final int index;

        public Position(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    private boolean hasSoloWindowAbove(WindowPlacement windowPlacement)
    {
        int index = windowStack.indexOf(windowPlacement);
        for(int i = index + 1; i < windowStack.size(); i++) {
            if(((WindowPlacement)windowStack.get(i)).window.isSoloWindow())
                return true;
        }
        return false;
    }

    private void drawMemoryUsage(TextGraphics textGraphics)
    {
        Runtime runtime = Runtime.getRuntime();
        long freeMemory = runtime.freeMemory();
        long totalMemory = runtime.totalMemory();
        long usedMemory = totalMemory - freeMemory;

        usedMemory /= (1024 * 1024);
        totalMemory /= (1024 * 1024);

        String memUsageString = "Memory usage: " + usedMemory + " MB of " + totalMemory + " MB";
        textGraphics.drawString(screen.getTerminalSize().getColumns() - memUsageString.length() - 1,
                screen.getTerminalSize().getRows() - 1, memUsageString, new Terminal.Style[0]);
    }

    private class WindowPlacement
    {
        private Window window;
        private Position positionPolicy;
        private TerminalPosition topLeft;

        public WindowPlacement(Window window, Position positionPolicy, TerminalPosition topLeft)
        {
            this.window = window;
            this.positionPolicy = positionPolicy;
            this.topLeft = topLeft;
        }

        public TerminalPosition getTopLeft()
        {
            return topLeft;
        }

        public void setTopLeft(TerminalPosition topLeft)
        {
            this.topLeft = topLeft;
        }

        public Window getWindow()
        {
            return window;
        }

        public void setWindow(Window window)
        {
            this.window = window;
        }

        public Position getPositionPolicy()
        {
            return positionPolicy;
        }
    }
}
