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

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.WaitingDialog;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Issue595 {
    public static void main(String... args) throws IOException {
        final DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        try (final Screen screen = terminalFactory.createScreen()) {
            screen.startScreen();

            // POC
            final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);
            final WaitingDialog waitingDialog = WaitingDialog.createDialog("TITLE", "TEXT");
            waitingDialog.showDialog(textGUI, false);
            CompletableFuture.runAsync(() -> {
                        try {
                            Thread.sleep(5000);
                        } catch (final InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException(e);
                        } finally {
                            waitingDialog.close();
                        }
                    }, executorService)
                    .exceptionally(e -> {
                        throw new RuntimeException(e);
                    });
            waitingDialog.waitUntilClosed();
            System.out.println("WAIT DIALOG CLOSED");
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        executorService.shutdown();
    }
}
