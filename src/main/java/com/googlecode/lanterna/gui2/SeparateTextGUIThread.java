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
 * Copyright (C) 2010-2015 Martin
 */
package com.googlecode.lanterna.gui2;

import java.io.EOFException;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Default implementation of TextGUIThread
 * @author Martin
 */
class SeparateTextGUIThread extends AbstractTextGUIThread {
    private Status status;
    private Thread textGUIThread;
    private CountDownLatch waitLatch;

    SeparateTextGUIThread(TextGUI textGUI) {
        super(textGUI);
        this.status = Status.CREATED;
        this.waitLatch = new CountDownLatch(0);
        this.textGUIThread = null;
    }

    /**
     * This will start the thread responsible for processing the input queue and update the screen.
     * @throws java.lang.IllegalStateException If the thread is already started
     */
    public void start() throws IllegalStateException {
        if(status == Status.STARTED) {
            throw new IllegalStateException("TextGUIThread is already started");
        }

        textGUIThread = new Thread("LanternaGUI") {
            @Override
            public void run() {
                mainGUILoop();
            }
        };
        textGUIThread.start();
        status = Status.STARTED;
        this.waitLatch = new CountDownLatch(1);
    }

    /**
     * Calling this will mark the GUI thread to be stopped after all pending events have been processed. It will exit
     * immediately however, call {@code waitForStop()} to block the current thread until the GUI thread has exited.
     */
    public void stop() {
        if(status != Status.STARTED) {
            return;
        }

        status = Status.STOPPING;
    }

    /**
     * Awaits the GUI thread to reach stopped state
     * @throws InterruptedException In case this thread was interrupted while waiting for the GUI thread to exit
     */
    public void waitForStop() throws InterruptedException {
        waitLatch.await();
    }

    /**
     * Returns the current status of the GUI thread
     * @return Current status of the GUI thread
     */
    public Status getStatus() {
        return status;
    }

    @Override
    public Thread getThread() {
        return textGUIThread;
    }

    @Override
    public void invokeLater(Runnable runnable) throws IllegalStateException {
        if(status != Status.STARTED) {
            throw new IllegalStateException("Cannot schedule " + runnable + " for execution on the TextGUIThread " +
                    "because the thread is in " + status + " state");
        }
        super.invokeLater(runnable);
    }

    private void mainGUILoop() {
        try {
            //Draw initial screen, after this only draw when the GUI is marked as invalid
            try {
                textGUI.updateScreen();
            }
            catch(IOException e) {
                exceptionHandler.onIOException(e);
            }
            catch(RuntimeException e) {
                exceptionHandler.onRuntimeException(e);
            }
            while(status == Status.STARTED) {
                try {
                    if (!processEventsAndUpdate()) {
                        try {
                            Thread.sleep(1);
                        }
                        catch(InterruptedException ignored) {}
                    }
                }
                catch(EOFException e) {
                    stop();
                    break; //Break out quickly from the main loop
                }
                catch(IOException e) {
                    if(exceptionHandler.onIOException(e)) {
                        stop();
                        break;
                    }
                }
                catch(RuntimeException e) {
                    if(exceptionHandler.onRuntimeException(e)) {
                        stop();
                        break;
                    }
                }
            }
        }
        finally {
            status = Status.STOPPED;
            waitLatch.countDown();
        }
    }


    enum Status {
        CREATED,
        STARTED,
        STOPPING,
        STOPPED,
        ;
    }

    public static class Factory implements TextGUIThreadFactory {
        @Override
        public TextGUIThread createTextGUIThread(TextGUI textGUI) {
            return new SeparateTextGUIThread(textGUI);
        }
    }
}
