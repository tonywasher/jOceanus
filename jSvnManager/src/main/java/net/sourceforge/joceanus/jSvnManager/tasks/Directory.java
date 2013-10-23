/*******************************************************************************
 * jSvnManager: Java SubVersion Management
 * Copyright 2012,2013 Tony Washer
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
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.jOceanus.jSvnManager.tasks;

import java.io.File;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;

/**
 * Utility classes to manage directories.
 * @author Tony Washer
 */
public final class Directory {
    /**
     * Private constructor.
     */
    private Directory() {
    }

    /**
     * Create a directory.
     * @param pDir the create
     * @throws JDataException on error
     */
    public static void createDirectory(final File pDir) throws JDataException {
        /* Remove any existing directory */
        removeDirectory(pDir);

        /* Create the new directory */
        if (!pDir.mkdir()) {
            throw new JDataException(ExceptionClass.DATA, "Failed to create directory: "
                    + pDir.getAbsolutePath());
        }
    }

    /**
     * Remove a directory and all of its contents.
     * @param pDir the directory to remove
     * @throws JDataException on error
     */
    public static void removeDirectory(final File pDir) throws JDataException {
        /* If the directory does not exist just return */
        if ((pDir == null) || (!pDir.exists())) {
            return;
        }

        /* Clear the directory */
        clearDirectory(pDir);

        /* Delete the directory itself */
        if (!pDir.delete()) {
            throw new JDataException(ExceptionClass.DATA, "Failed to delete directory: "
                    + pDir.getAbsolutePath());
        }
    }

    /**
     * Clear a directory of all of its contents.
     * @param pDir the directory to clear
     * @throws JDataException on error
     */
    public static void clearDirectory(final File pDir) throws JDataException {
        /* Handle trivial operations */
        if ((pDir == null) || (!pDir.exists())) {
            return;
        }

        /* Handle invalid call */
        if (!pDir.isDirectory()) {
            throw new JDataException(ExceptionClass.DATA, "Attempt to clear non-directory: "
                    + pDir.getAbsolutePath());
        }

        /* Loop through all items */
        for (File myFile : pDir.listFiles()) {
            /* If the file is a directory */
            if (myFile.isDirectory()) {
                /* Remove the directory */
                removeDirectory(myFile);

                /* else remove the file */
            } else if (!myFile.delete()) {
                throw new JDataException(ExceptionClass.DATA, "Failed to delete file: "
                        + myFile.getAbsolutePath());
            }
        }
    }
}
