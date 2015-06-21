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

import java.io.IOException;

/**
 * Class that manages a TextGUI in a separate thread, doing input processing and screen updates automatically.
 * @author Martin
 */
public interface TextGUIThread {
    /**
     * Invokes custom code on the GUI thread. If the caller is already on the GUI thread, the code is executed immediately
     * @param runnable Code to run
     * @throws java.lang.IllegalStateException If the GUI thread is not running
     */
    void invokeLater(Runnable runnable) throws IllegalStateException;

    /**
     *
     * @return
     * @throws IOException
     */
    boolean processEventsAndUpdate() throws IOException;

    /**
     * Schedules custom code to be executed on the GUI thread and waits until the code has been executed before
     * returning.
     * @param runnable Code to run
     * @throws IllegalStateException If the GUI thread is not running
     * @throws InterruptedException If the caller thread was interrupted while waiting for the task to be executed
     */
    void invokeAndWait(Runnable runnable) throws IllegalStateException, InterruptedException;

    /**
     * Updates the exception handler used by this TextGUIThread. The exception handler will be invoked when an exception
     * occurs in the main event loop. You can then decide how to log this exception and if you want to terminate the
     * thread or not.
     * @param exceptionHandler Handler to inspect exceptions
     */
    void setExceptionHandler(ExceptionHandler exceptionHandler);

    /**
     * Returns the Java thread which is processing GUI events and updating the screen
     * @return Thread which is processing events and updating the screen
     */
    Thread getThread();

    /**
     * This interface defines an exception handler, that is used for looking at exceptions that occurs during the main
     * event loop of the TextGUIThread. You can for example use this for logging, but also decide if you want the
     * exception to kill the thread.
     */
    interface ExceptionHandler {
        /**
         * Will be called when an IOException has occurred in the main event thread
         * @param e IOException that occurred
         * @return If you return {@code true}, the event thread will be terminated
         */
        boolean onIOException(IOException e);

        /**
         * Will be called when a RuntimeException has occurred in the main event thread
         * @param e RuntimeException that occurred
         * @return If you return {@code true}, the event thread will be terminated
         */
        boolean onRuntimeException(RuntimeException e);
    }
}
