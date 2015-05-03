///*
// * This file is part of lanterna (http://code.google.com/p/lanterna/).
// *
// * lanterna is free software: you can redistribute it and/or modify
// * it under the terms of the GNU Lesser General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// *
// * Copyright (C) 2010-2015 Martin
// */
//package com.googlecode.lanterna.gui2;
//
//import com.googlecode.lanterna.TerminalPosition;
//import com.googlecode.lanterna.TerminalSize;
//
//import java.util.*;
//import java.util.concurrent.CopyOnWriteArrayList;
//
///**
// *
// * @author Martin
// */
//public class SimpleWindowManager implements WindowManager {
//    private static final int CASCADE_SHIFT_RIGHT = 2;
//    private static final int CASCADE_SHIFT_DOWN = 1;
//
//    private final SortedSet<ManagedWindow> windowStack;
//    private final List<Listener> listeners;
//    private final WindowDecorationRenderer windowDecorationRenderer;
//    private TerminalPosition nextTopLeftPosition;
//
//    public SimpleWindowManager() {
//        this.windowStack = new TreeSet<ManagedWindow>();
//        this.listeners = new CopyOnWriteArrayList<Listener>();
//        this.nextTopLeftPosition = new TerminalPosition(CASCADE_SHIFT_RIGHT, CASCADE_SHIFT_DOWN);
//        this.windowDecorationRenderer = new DefaultWindowDecorationRenderer();
//    }
//
//    @Override
//    public synchronized void addWindow(Window window) {
//        if(window == null) {
//            throw new IllegalArgumentException("Cannot call addWindow(...) with null window");
//        }
//        TerminalPosition topLeftPosition;
//        if(!window.getHints().contains(Window.Hint.CENTERED)) {
//            topLeftPosition = nextTopLeftPosition;
//            nextTopLeftPosition = nextTopLeftPosition
//                                    .withColumn(nextTopLeftPosition.getColumn() + CASCADE_SHIFT_RIGHT)
//                                    .withRow(nextTopLeftPosition.getRow() + CASCADE_SHIFT_DOWN);
//        }
//        else {
//            topLeftPosition = null;
//        }
//        ManagedWindow managedWindow = new ManagedWindow(
//                window,
//                topLeftPosition,
//                windowStack.size());
//        windowStack.add(managedWindow);
//        if(window instanceof AbstractWindow) {
//            ((AbstractWindow)window).setWindowManager(this);
//        }
//        fireWindowAdded(window);
//    }
//
//    @Override
//    public synchronized void removeWindow(Window window) {
//        if(window == null) {
//            throw new IllegalArgumentException("Cannot call removeWindow(...) with null window");
//        }
//        ManagedWindow managedWindow = getManagedWindow(window);
//        if(managedWindow == null) {
//            throw new IllegalArgumentException("Unknown window passed to removeWindow(...), this window manager doesn't"
//                    + " contain " + window);
//        }
//        windowStack.remove(managedWindow);
//        fireWindowRemoved(window);
//    }
//
//    @Override
//    public synchronized Collection<Window> getWindows() {
//        List<Window> result = new ArrayList<Window>();
//        for(ManagedWindow managedWindow: windowStack) {
//            result.add(managedWindow.window);
//        }
//        return result;
//    }
//
//    @Override
//    public synchronized Window getActiveWindow() {
//        if(windowStack.isEmpty()) {
//            return null;
//        }
//        else {
//            return windowStack.last().window;
//        }
//    }
//
//    @Override
//    public boolean isInvalid() {
//        for(ManagedWindow managedWindow: windowStack) {
//            if(managedWindow.window.isInvalid()) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public WindowDecorationRenderer getWindowDecorationRenderer(Window window) {
//        if(window.getHints().contains(Window.Hint.NO_DECORATIONS)) {
//            return new EmptyWindowDecorationRenderer();
//        }
//        return windowDecorationRenderer;
//    }
//
//    @Override
//    public synchronized TerminalPosition getTopLeftPosition(Window window, TerminalSize screenSize) {
//        ManagedWindow managedWindow = getManagedWindow(window);
//        if(managedWindow == null) {
//            throw new IllegalArgumentException("Cannot call getTopLeftPosition of " + window + " on " + toString() +
//                    " as it's not managed by this window manager");
//        }
//        if(managedWindow.topLeftPosition != null) {
//            //If the window is outside of the screen, pull it back in
//            while(screenSize.getColumns() > 5 && screenSize.getColumns() <= managedWindow.topLeftPosition.getColumn()) {
//                managedWindow.topLeftPosition = managedWindow.topLeftPosition.withRelativeColumn(-screenSize.getColumns());
//            }
//            while(screenSize.getRows() > 3 && screenSize.getRows() <= managedWindow.topLeftPosition.getRow()) {
//                managedWindow.topLeftPosition = managedWindow.topLeftPosition.withRelativeRow(-screenSize.getRows());
//            }
//            return managedWindow.topLeftPosition;
//        }
//
//        //If the stored position was null, then center the window
//        TerminalSize size = getSize(window, null, screenSize);
//        return new TerminalPosition(
//                (screenSize.getColumns() / 2) - (size.getColumns() / 2),
//                (screenSize.getRows() / 2) - (size.getRows() / 2));
//    }
//
//    @Override
//    public synchronized TerminalSize getSize(Window window, TerminalPosition topLeft, TerminalSize screenSize) {
//        TerminalSize undecoratedSize = getUndecoratedSize(window, topLeft, screenSize);
//        undecoratedSize = undecoratedSize.max(TerminalSize.ONE);    //Make sure the size is it least one
//        if(window.getHints().contains(Window.Hint.NO_DECORATIONS)) {
//            return undecoratedSize;
//        }
//        return getWindowDecorationRenderer(window).getDecoratedSize(window, undecoratedSize);
//    }
//
//    @Override
//    public void addListener(Listener listener) {
//        listeners.add(listener);
//    }
//
//    @Override
//    public void removeListener(Listener listener) {
//        listeners.remove(listener);
//    }
//
//    private void fireWindowAdded(Window window) {
//        for(Listener listener: listeners) {
//            listener.onWindowAdded(this, window);
//        }
//    }
//
//    private void fireWindowRemoved(Window window) {
//        for(Listener listener: listeners) {
//            listener.onWindowRemoved(this, window);
//        }
//    }
//
//    private TerminalSize getUndecoratedSize(Window window, TerminalPosition topLeft, TerminalSize screenSize) throws IllegalArgumentException {
//        ManagedWindow managedWindow = getManagedWindow(window);
//        if(managedWindow == null) {
//            throw new IllegalArgumentException("Cannot call getTopLeftPosition of " + window + " on " + toString() +
//                    " as it's not managed by this window manager");
//        }
//
//        TerminalSize preferredSize = window.getPreferredSize();
//        if(!window.getHints().contains(Window.Hint.FIT_TERMINAL_WINDOW)) {
//            return preferredSize;
//        }
//        if(topLeft == null) {
//            //Assume the window can take up the full screen
//            return preferredSize
//                    .withColumns(Math.min(preferredSize.getColumns(), screenSize.getColumns()))
//                    .withRows(Math.min(preferredSize.getRows(), screenSize.getRows()));
//        }
//
//        //We can only take up screen size - top left
//        return preferredSize
//                .withColumns(Math.min(preferredSize.getColumns(), screenSize.getColumns() - topLeft.getColumn()))
//                .withRows(Math.min(preferredSize.getRows(), screenSize.getRows() - topLeft.getRow()));
//    }
//
//    private ManagedWindow getManagedWindow(Window forWindow) {
//        for(ManagedWindow managedWindow: windowStack) {
//            if(forWindow == managedWindow.window) {
//                return managedWindow;
//            }
//        }
//        return null;
//    }
//
//    private static class ManagedWindow implements Comparable<ManagedWindow>{
//        private final Window window;
//        private TerminalPosition topLeftPosition;
//        private int ordinal;
//
//        private ManagedWindow(Window window, TerminalPosition topLeftPosition, int ordinal) {
//            this.window = window;
//            this.topLeftPosition = topLeftPosition;
//            this.ordinal = ordinal;
//        }
//
//        @Override
//        @SuppressWarnings("NullableProblems")   //I can't find the correct way to fix this!
//        public int compareTo(ManagedWindow o) {
//            if(ordinal < o.ordinal) {
//                return -1;
//            }
//            else if(ordinal == o.ordinal) {
//                return 0;
//            }
//            else {
//                return 1;
//            }
//        }
//    }
//}
