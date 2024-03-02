/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
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
 * Copyright (C) 2010-2020 Martin Berglund
 */
package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.gui2.AnimatedLabel;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.WaitingDialog;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public
class Issue595 {
    private static final int SLEEP_SECONDS = 5;
    private static final long SLEEP_MILLIS = SLEEP_SECONDS * 1000L;

    public static
    void main (String... args) throws IOException {
        int exit_code = 0;
        final DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        try (final Screen screen = terminalFactory.createScreen()) {
            screen.startScreen();
            final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);

            // POC
            final WaitingDialog waitingDialog = WaitingDialog.createDialog("TITLE", "TEXT");
            final ExecutorService executorService = Executors.newSingleThreadExecutor();
            Thread.sleep(SLEEP_MILLIS);
            waitingDialog.showDialog(textGUI, false);
            CompletableFuture.runAsync(() -> {
                                 try {
                                     Thread.sleep(SLEEP_MILLIS);
                                 } catch (final InterruptedException e) {
                                     Thread.currentThread()
                                           .interrupt();
                                     throw new RuntimeException(e);
                                 } finally {
                                     waitingDialog.close();
                                 }
                             }, executorService)
                             .exceptionally(e -> {
                                 throw new RuntimeException(e);
                             });
            waitingDialog.waitUntilClosed();

            // Ensure Executor Thread Dead
            executorService.shutdownNow();
            if (!executorService.awaitTermination(SLEEP_SECONDS, TimeUnit.SECONDS)) {
                throw new IllegalStateException("ExecutorService Unstoppable");
            }

            // Check for Animated Label Hanging Thread
            final String animatedLabelName = AnimatedLabel.class.getSimpleName()
                                                                .toLowerCase();
            final Optional<Thread> optionalAnimatedLabelThread = Thread.getAllStackTraces()
                                                                       .keySet()
                                                                       .stream()
                                                                       .filter(thread -> thread.getName()
                                                                                               .toLowerCase()
                                                                                               .contains(animatedLabelName))
                                                                       .findAny();
            if (!optionalAnimatedLabelThread.isPresent()) {
                return;
            }
            final Thread thread = optionalAnimatedLabelThread.get();
            thread.join(SLEEP_MILLIS);
            if (thread.isAlive()) {
                throw new IllegalStateException("AnimatedLabel Thread Waiting");
            }

        } catch (final Throwable e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread()
                      .interrupt();
            }
            ++exit_code;
            final StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            System.err.print(stringWriter);
        } finally {
            System.exit(exit_code);
        }
    }
}
