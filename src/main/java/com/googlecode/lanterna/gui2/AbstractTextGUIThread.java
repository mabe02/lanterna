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
 * Copyright (C) 2010-2017 Martin Berglund
 */
package com.googlecode.lanterna.gui2;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Abstract implementation of {@link TextGUIThread} with common logic for both available concrete implementations.
 */
public abstract class AbstractTextGUIThread implements TextGUIThread {

    protected final TextGUI textGUI;
    protected final Queue<Runnable> customTasks;
    protected ExceptionHandler exceptionHandler;

    /**
     * Sets up this {@link AbstractTextGUIThread} for operations on the supplies {@link TextGUI}
     * @param textGUI Text GUI this {@link TextGUIThread} implementations will be operating on
     */
    public AbstractTextGUIThread(TextGUI textGUI) {
        this.exceptionHandler = new ExceptionHandler() {
            @Override
            public boolean onIOException(IOException e) {
                e.printStackTrace();
                return true;
            }

            @Override
            public boolean onRuntimeException(RuntimeException e) {
                e.printStackTrace();
                return true;
            }
        };
        this.textGUI = textGUI;
        this.customTasks = new LinkedBlockingQueue<Runnable>();
    }

    @Override
    public void invokeLater(Runnable runnable) throws IllegalStateException {
        customTasks.add(runnable);
    }

    @Override
    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        if(exceptionHandler == null) {
            throw new IllegalArgumentException("Cannot call setExceptionHandler(null)");
        }
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public synchronized boolean processEventsAndUpdate() throws IOException {
        if(getThread() != Thread.currentThread()) {
            throw new IllegalStateException("Calling processEventAndUpdate outside of GUI thread");
        }
        textGUI.processInput();
        while(!customTasks.isEmpty()) {
            Runnable r = customTasks.poll();
            if(r != null) {
                r.run();
            }
        }
        if(textGUI.isPendingUpdate()) {
            textGUI.updateScreen();
            return true;
        }
        return false;
    }

    @Override
    public void invokeAndWait(final Runnable runnable) throws IllegalStateException, InterruptedException {
        Thread guiThread = getThread();
        if(guiThread == null || Thread.currentThread() == guiThread) {
            runnable.run();
        }
        else {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        runnable.run();
                    }
                    finally {
                        countDownLatch.countDown();
                    }
                }
            });
            countDownLatch.await();
        }
    }
}
