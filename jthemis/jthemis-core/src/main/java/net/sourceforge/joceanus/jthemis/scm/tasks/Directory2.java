/*******************************************************************************
 * jThemis: Java Project Framework
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jthemis.scm.tasks;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.JThemisIOException;

/**
 * Utility classes to manage directories with Java7 Files.
 * @author Tony Washer
 */
public final class Directory2 {
    /**
     * Pause duration for object delete.
     */
    private static final long PAUSE_DURATION = 100;

    /**
     * Pause repeat for object delete.
     */
    private static final long PAUSE_REPEAT = 100;

    /**
     * Private constructor.
     */
    private Directory2() {
    }

    /**
     * Create a directory.
     * @param pDir the create
     * @throws OceanusException on error
     */
    public static void createDirectory(final File pDir) throws OceanusException {
        /* Use standard method */
        createDirectory(pDir == null
                                    ? null
                                    : pDir.toPath());
    }

    /**
     * Create a directory.
     * @param pDir the create
     * @throws OceanusException on error
     */
    public static void createDirectory(final Path pDir) throws OceanusException {
        /* Handle null directory */
        if (pDir == null) {
            return;
        }

        /* Remove any existing directory */
        removeDirectory(pDir);

        /* Protect against exceptions */
        try {
            /* Create the new directory */
            Files.createDirectory(pDir);
        } catch (IOException e) {
            throw new JThemisIOException("Failed to create directory: "
                                         + pDir.toAbsolutePath(), e);
        }
    }

    /**
     * Remove a directory and all of its contents.
     * @param pDir the directory to remove
     * @throws OceanusException on error
     */
    public static void removeDirectory(final File pDir) throws OceanusException {
        /* Remove the directory */
        removeDirectory(pDir == null
                                    ? null
                                    : pDir.toPath());
    }

    /**
     * Remove a directory and all of its contents.
     * @param pDir the directory to remove
     * @throws OceanusException on error
     */
    public static void removeDirectory(final Path pDir) throws OceanusException {
        /* If the directory does not exist just return */
        if ((pDir == null)
            || (!Files.exists(pDir))) {
            return;
        }

        /* Clear the directory */
        clearDirectory(pDir);

        /* Protect against exceptions */
        try {
            /* Finally remove the directory */
            removeFile(pDir);
        } catch (IOException e) {
            throw new JThemisIOException("Failed to delete directory: "
                                         + pDir.toAbsolutePath(), e);
        }
    }

    /**
     * Clear a directory of all of its contents.
     * @param pDir the directory to clear
     * @throws OceanusException on error
     */
    public static void clearDirectory(final File pDir) throws OceanusException {
        /* Clear the directory */
        clearDirectory(pDir == null
                                   ? null
                                   : pDir.toPath());
    }

    /**
     * Clear a directory of all of its contents.
     * @param pDir the directory to clear
     * @param pKeep the name of the element to keep
     * @throws OceanusException on error
     */
    public static void clearDirectory(final File pDir,
                                      final String pKeep) throws OceanusException {
        /* Clear the directory */
        clearDirectory(pDir == null
                                   ? null
                                   : pDir.toPath(), pKeep);
    }

    /**
     * Clear a directory of all of its contents.
     * @param pDir the directory to clear
     * @throws OceanusException on error
     */
    public static void clearDirectory(final Path pDir) throws OceanusException {
        /* Clear the directory without keeping anything */
        clearDirectory(pDir, null);
    }

    /**
     * Clear a directory of all of its contents.
     * @param pDir the directory to clear
     * @param pKeep the name of the element to keep
     * @throws OceanusException on error
     */
    public static void clearDirectory(final Path pDir,
                                      final String pKeep) throws OceanusException {
        /* If the directory does not exist just return */
        if ((pDir == null)
            || (!Files.exists(pDir))) {
            return;
        }

        /* Protect against exceptions */
        try {
            /* Clear and remove the directory */
            Files.walkFileTree(pDir, new DirVisitor(pDir, pKeep));
        } catch (IOException e) {
            throw new JThemisIOException("Failed to clear directory: "
                                         + pDir.toAbsolutePath(), e);
        }
    }

    /**
     * Remove a file.
     * @param pFile the file to remove
     * @throws IOException on error
     */
    private static void removeFile(final Path pFile) throws IOException {
        /* Loop to retry deleting the file */
        for (int i = 0; i < PAUSE_REPEAT; i++) {
            /* Try to delete the file */
            try {
                /* Delete the file and return */
                Files.delete(pFile);
                return;

            } catch (IOException e) {
                /* This can be a transient error so wait a bit before retrying */
                try {
                    Thread.sleep(PAUSE_DURATION);
                } catch (InterruptedException ex) {
                    throw new IOException("Interrupted", ex);
                }
            }
        }

        /* Report the failure */
        throw new IOException("Failed to delete file: "
                              + pFile.toAbsolutePath());
    }

    /**
     * Visitor class.
     */
    private static final class DirVisitor
            extends SimpleFileVisitor<Path> {
        /**
         * Name of top-level directory.
         */
        private final Path theBase;

        /**
         * Name of directory to keep.
         */
        private String theKeep;

        /**
         * Constructor.
         * @param pBase the base directory
         */
        private DirVisitor(final Path pBase) {
            this(pBase, null);
        }

        /**
         * Constructor.
         * @param pBase the base directory
         * @param pKeep the directory to keep
         */
        private DirVisitor(final Path pBase,
                           final String pKeep) {
            /* Store parameters */
            theBase = pBase;
            theKeep = pKeep;
        }

        @Override
        public FileVisitResult visitFile(final Path pFile,
                                         final BasicFileAttributes pAttrs) throws IOException {

            /* delete the file and continue */
            removeFile(pFile);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(final Path pDir,
                                                 final BasicFileAttributes pAttrs) {
            /* If we have a directory that we wish to keep */
            if (theKeep != null) {
                /* If this is the directory */
                if (theKeep.equals(pDir.getFileName().toString())) {
                    /* Remove flag and skip this directory */
                    theKeep = null;
                    return FileVisitResult.SKIP_SUBTREE;
                }
            }

            /* Handle directory normally */
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(final Path pDir,
                                                  final IOException pException) throws IOException {
            /* If we successfully deleted the files */
            if (pException == null) {
                /* If this is not the base directory */
                if (!theBase.equals(pDir)) {
                    /* Delete the directory */
                    removeFile(pDir);
                }
                return FileVisitResult.CONTINUE;

                /* else re-throw the exception */
            } else {
                // directory iteration failed
                throw pException;
            }
        }
    }
}
