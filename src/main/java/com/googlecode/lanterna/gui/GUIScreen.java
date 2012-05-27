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
 * Copyright (C) 2010-2012 Martin
 */

package com.googlecode.lanterna.gui;

import com.googlecode.lanterna.LanternaException;
import com.googlecode.lanterna.gui.listener.WindowAdapter;
import com.googlecode.lanterna.gui.theme.Theme;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 *
 * @author Martin
 */
public class GUIScreen
{
    private final Screen screen;
    private final LinkedList<WindowPlacement> windowStack;
    private final Queue<Action> actionToRunInEventThread;
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
        this.windowStack = new LinkedList<WindowPlacement>();
        this.actionToRunInEventThread = new LinkedList<Action>();
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
    
    public TerminalSize getTerminalSize() {
        return screen.getTerminalSize();
    }

    public Screen getScreen() {
        return screen;
    }
    
    private synchronized void repaint() throws LanternaException
    {
        final TextGraphics textGraphics = new TextGraphics(new TerminalPosition(0, 0),
                new TerminalSize(screen.getTerminalSize()), screen, guiTheme);

        textGraphics.applyThemeItem(guiTheme.getItem(Theme.Category.ScreenBackground));

        //Clear the background
        textGraphics.fillRectangle(' ', new TerminalPosition(0, 0), new TerminalSize(screen.getTerminalSize()));

        //Write the title
        textGraphics.drawString(3, 0, title);

        //Write memory usage
        if(showMemoryUsage)
            drawMemoryUsage(textGraphics);

        //Go through the windows
        for(WindowPlacement windowPlacement: windowStack) {
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

        if(windowStack.size() > 0 && windowStack.getLast().getWindow().getWindowHotspotPosition() != null)
            screen.setCursorPosition(windowStack.getLast().getWindow().getWindowHotspotPosition());
        else
            screen.setCursorPosition(new TerminalPosition(screen.getWidth() - 1, screen.getHeight() - 1));
        screen.refresh();
    }

    private void update() throws LanternaException
    {
        if(needsRefresh || !screen.resizePending()) {
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
        if(windowStack.size() > 0 && windowStack.getLast().getWindow() == window)
            return true;
        else
            return false;
    }

    private void doEventLoop() throws LanternaException
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
                List<Action> actions = new ArrayList<Action>(actionToRunInEventThread);
                actionToRunInEventThread.clear();
                for(Action nextAction: actions)
                    nextAction.doAction();
            }

            update();

            Key nextKey = screen.readInput();
            if(nextKey != null) {
                windowStack.getLast().window.onKeyPressed(nextKey);
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

    public void showWindow(Window window) throws LanternaException
    {
        showWindow(window, Position.OVERLAPPING);
    }

    public void showWindow(Window window, Position position) throws LanternaException
    {
        if(window == null)
            return;
        if(position == null)
            position = Position.OVERLAPPING;

        int newWindowX = 2;
        int newWindowY = 1;

        if(position == Position.OVERLAPPING &&
                windowStack.size() > 0) {
            WindowPlacement lastWindow = windowStack.getLast();
            if(lastWindow.getPositionPolicy() != Position.CENTER) {
                newWindowX = lastWindow.getTopLeft().getColumn() + 2;
                newWindowY = lastWindow.getTopLeft().getRow() + 1;
            }
        }

        window.addWindowListener(new WindowAdapter() {
            @Override
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
        if(windowStack.getLast().window != window)
            return;

        WindowPlacement windowPlacement = windowStack.removeLast();
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

    public enum Position
    {
        OVERLAPPING,
        NEW_CORNER_WINDOW,
        CENTER
    }

    private boolean hasSoloWindowAbove(WindowPlacement windowPlacement)
    {
        int index = windowStack.indexOf(windowPlacement);
        for(int i = index + 1; i < windowStack.size(); i++) {
            if(windowStack.get(i).window.isSoloWindow())
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
                screen.getTerminalSize().getRows() - 1, memUsageString);
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
