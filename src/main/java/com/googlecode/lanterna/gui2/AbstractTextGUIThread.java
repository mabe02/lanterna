package com.googlecode.lanterna.gui2;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by martin on 20/06/15.
 */
public abstract class AbstractTextGUIThread implements TextGUIThread {

    protected final TextGUI textGUI;
    protected final Queue<Runnable> customTasks;
    protected ExceptionHandler exceptionHandler;

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
        if(Thread.currentThread() == getThread()) {
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
