package com.googlecode.lanterna.gui2;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by martin on 20/06/15.
 */
public class SameTextGUIThread extends AbstractTextGUIThread {

    Thread currentThread;

    public SameTextGUIThread(TextGUI textGUI) {
        super(textGUI);
    }

    @Override
    public Thread getThread() {
        return currentThread;
    }

    @Override
    public void invokeAndWait(Runnable runnable) throws IllegalStateException, InterruptedException {
        if(currentThread == null || currentThread == Thread.currentThread()) {
            runnable.run();
        }
        super.invokeAndWait(runnable);
    }

    @Override
    public synchronized boolean processEventsAndUpdate() throws IOException {
        if(currentThread != null && currentThread != Thread.currentThread()) {
            throw new IllegalStateException(currentThread + " is already processing TextGUI events, " +
                    Thread.currentThread() + " tried to call processEventsAndUpdate()");
        }
        boolean resetCurrentThread = currentThread == null;
        currentThread = Thread.currentThread();
        try {
            return super.processEventsAndUpdate();
        }
        finally {
            if(resetCurrentThread) {
                currentThread = null;
            }
        }
    }

    public static class Factory implements TextGUIThreadFactory {
        @Override
        public TextGUIThread createTextGUIThread(TextGUI textGUI) {
            return new SameTextGUIThread(textGUI);
        }
    }
}
