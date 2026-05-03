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

package io.github.tonywasher.joceanus.tethys.swing.dialog;

import io.github.tonywasher.joceanus.oceanus.logger.OceanusLogManager;
import io.github.tonywasher.joceanus.oceanus.logger.OceanusLogger;

import javax.swing.SwingUtilities;
import java.lang.reflect.InvocationTargetException;

public final class TethysUISwingDialog {
    /**
     * Logger.
     */
    private static final OceanusLogger LOGGER = OceanusLogManager.getLogger(TethysUISwingDialog.class);

    /**
     * Private constructor.
     */
    private TethysUISwingDialog() {
    }

    /**
     * Run method in FX Application thread.
     *
     * @param pRunnable the runnable method
     */
    static void runInSwingThread(final Runnable pRunnable) {
        /* If this is the event dispatcher thread */
        if (SwingUtilities.isEventDispatchThread()) {
            /* invoke the task directly */
            pRunnable.run();

            /* else we must use invokeAndWait */
        } else {
            try {
                SwingUtilities.invokeAndWait(pRunnable);
            } catch (InvocationTargetException e) {
                LOGGER.error("Failed to run dialog", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
