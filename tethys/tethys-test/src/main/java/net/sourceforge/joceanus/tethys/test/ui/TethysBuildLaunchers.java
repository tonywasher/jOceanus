/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2024 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sourceforge.joceanus.tethys.test.ui;

import java.io.File;

import net.sourceforge.joceanus.tethys.OceanusException;
import net.sourceforge.joceanus.tethys.jar.TethysLauncher;
import net.sourceforge.joceanus.tethys.logger.TethysLogManager;
import net.sourceforge.joceanus.tethys.logger.TethysLogger;

/**
 * Harness to build launcher scripts.
 */
public final class TethysBuildLaunchers {
    /**
     * Create Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(TethysBuildLaunchers.class);

    /**
     * Private constructor.
     */
    private TethysBuildLaunchers() {
    }

    /**
     * Main entry point.
     *
     * @param pArgs the program arguments
     */
    public static void main(final String[] pArgs) {
        /* Protect against exceptions */
        try {
            /* Access the Backup location */
            final String myBackup = System.getenv("BACKUPDIR");
            TethysLauncher.processJarFiles(new File(myBackup + "/bin"));

            /* Handle exceptions */
        } catch (OceanusException e) {
            LOGGER.fatal("Failed to create launch scripts", e);
        }
    }
}
