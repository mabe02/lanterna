package com.googlecode.lanterna.gui2;

/**
 * Extended interface of TextGUIThread for implementations that uses a separate thread for all GUI event processing and
 * updating.
 *
 * @author Martin
 */
public interface AsynchronousTextGUIThread extends TextGUIThread {
    /**
     * Starts the AsynchronousTextGUIThread, typically meaning that the event processing loop will start.
     */
    void start();

    /**
     * Requests that the AsynchronousTextGUIThread stops, typically meaning that the event processing loop will exit
     */
    void stop();

    /**
     * Blocks until the GUI loop has stopped
     * @throws InterruptedException In case this thread was interrupted while waiting for the GUI thread to exit
     */
    void waitForStop() throws InterruptedException;

    /**
     * Returns the current status of this GUI thread
     * @return Current status of the GUI thread
     */
    State getState();

    /**
     * Enum representing the states of the GUI thread life-cycle
     */
    enum State {
        /**
         * The instance has been created but not yet started
         */
        CREATED,
        /**
         * The thread has started an is running
         */
        STARTED,
        /**
         * The thread is trying to stop but is still running
         */
        STOPPING,
        /**
         * The thread has stopped
         */
        STOPPED,
        ;
    }
}
