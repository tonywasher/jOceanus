/*
 * Tethys: GUI Utilities
 * Copyright 2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.github.tonywasher.joceanus.tethys.javafx.dialog;

import io.github.tonywasher.joceanus.oceanus.logger.OceanusLogManager;
import io.github.tonywasher.joceanus.oceanus.logger.OceanusLogger;
import javafx.application.Platform;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * javaFX Utility to safely invoke dialogs.
 */

public final class TethysUIFXDialog {
    /**
     * Logger.
     */
    private static final OceanusLogger LOGGER = OceanusLogManager.getLogger(TethysUIFXDialog.class);

    /**
     * Private constructor.
     */
    private TethysUIFXDialog() {
    }

    /**
     * Run method in FX Application thread.
     *
     * @param pRunnable the runnable method
     */
    static void runInFXThread(final Runnable pRunnable) {
        /* If this is the event dispatcher thread */
        if (Platform.isFxApplicationThread()) {
            /* invoke the task directly */
            pRunnable.run();

            /* else we must use invokeAndWait */
        } else {
            /* Create a FutureTask so that we will wait */
            final FutureTask<Void> myTask = new FutureTask<>(() -> {
                pRunnable.run();
                return null;
            });

            /* Protect against exceptions */
            try {
                /* Run on Application thread and wait for completion */
                Platform.runLater(myTask);
                myTask.get();
            } catch (IllegalStateException
                     | ExecutionException e) {
                LOGGER.error("Failed to run dialog", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
