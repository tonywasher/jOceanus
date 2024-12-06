/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2024 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.metis.toolkit;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import net.sourceforge.joceanus.metis.exc.MetisIOException;
import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.oceanus.logger.OceanusLogManager;
import net.sourceforge.joceanus.oceanus.logger.OceanusLogger;

/**
 * Safe File Create.
 */
public class MetisSafeFileManager {
    /**
     * The Logger.
     */
    private static final OceanusLogger LOGGER = OceanusLogManager.getLogger(MetisSafeFileManager.class);

    /**
     * The temp extension.
     */
    static final String TEMP_EXT = ".temp";

    /**
     * The commit extension.
     */
    static final String COMMIT_EXT = ".commit";

    /**
     * The commit extension.
     */
    static final String COMMITX_EXT = ".commitx";

    /**
     * The dot character.
     */
    static final char CHAR_DOT = '.';

    /**
     * The path name.
     */
    private final String thePathName;

    /**
     * The base file name.
     */
    private final String theBaseName;

    /**
     * The extension.
     */
    private final String theExtension;

    /**
     * The number of backups.
     */
    private int theNumBackups;

    /**
     * Constructor.
     * @param pFileName the file name
     */
    public MetisSafeFileManager(final File pFileName) {
        thePathName = pFileName.getParent();
        final String myName = pFileName.getName();
        final int myIndex = myName.lastIndexOf(CHAR_DOT);
        theExtension = myName.substring(myIndex);
        theBaseName = myName.substring(0, myIndex);
    }

    /**
     * Set the number of backups.
     * @param pNumBackups the number of backups
     */
    public void setNumBackups(final int pNumBackups) {
        theNumBackups = pNumBackups;
    }

    /**
     * Commit file.
     * @throws OceanusException on error
     */
    public void commitFile() throws OceanusException {
        /* Signal commit */
        final File myCommit = getCommitFile();
        final File myTemp = getFileToWriteTo();
        renameFile(myTemp, myCommit);

        /* CleanUp */
        cleanUp();
    }

    /**
     * Cleanup file.
     */
    public void cleanUpFile() {
        /* Perform cleanUp and catch any exception */
        try {
            cleanUp();
        } catch (OceanusException e) {
            LOGGER.error("Failed to cleanUp file", e);
        }
    }

    /**
     * Cleanup file.
     * @throws OceanusException on error
     */
    private void cleanUp() throws OceanusException {
        /* Delete temp file if it exists */
        final File myTemp = getFileToWriteTo();
        if (myTemp.exists()) {
            deleteFile(myTemp);
        }

        /* If we have a commit file */
        final File myCommit = getCommitFile();
        if (myCommit.exists()) {
            /* If the real file exists */
            final File myTarget = getRealFile();
            if (myTarget.exists()) {
                /* If we have backups */
                if (theNumBackups > 0) {
                    /* Clear space */
                    clearBackup(1);

                    /* Rename to back-up */
                    final File myBackup = getBackupFile(1);
                    renameFile(myTarget, myBackup);

                    /* else just delete the file */
                } else {
                    deleteFile(myTarget);
                }
            }

            /* Commit the file */
            renameFile(myCommit, myTarget);
        }
    }

    /**
     * Cleanup backup file.
     * @param pIndex the backup index
     * @throws OceanusException on error
     */
    private void clearBackup(final int pIndex) throws OceanusException {
        /* Access filenames */
        final File mySource = getBackupFile(pIndex);

        /* If the source file exists */
        if (mySource.exists()) {
            /* If we have more backups */
            if (pIndex < theNumBackups) {
                /* Clear space */
                clearBackup(pIndex + 1);

                /* Rename file */
                final File myTarget = getBackupFile(pIndex + 1);
                renameFile(mySource, myTarget);

                /* else just delete the file */
            } else {
                deleteFile(mySource);
            }
        }
    }

    /**
     * Delete file.
     * @param pFile the file to delete
     * @throws OceanusException on error
     */
    private static void deleteFile(final File pFile) throws OceanusException {
        try {
            final Path myPath = pFile.toPath();
            Files.delete(myPath);
        } catch (IOException e) {
            throw new MetisIOException("Failed to delete file: " + pFile.getName(), e);
        }
    }

    /**
     * Rename file.
     * @param pSource the file to rename
     * @param pTarget the target file
     * @throws OceanusException on error
     */
    private static void renameFile(final File pSource,
                                   final File pTarget) throws OceanusException {
        try {
            final Path mySource = pSource.toPath();
            final Path myTarget = pTarget.toPath();
            Files.move(mySource, myTarget);
        } catch (IOException e) {
            throw new MetisIOException("Failed to rename file: " + pSource.getName(), e);
        }
    }

    /**
     * Obtain name of backup file.
     * @param pIndex the index number
     * @return the backup name
     */
    private File getBackupFile(final int pIndex) {
        return new File(thePathName, theBaseName + CHAR_DOT + pIndex + theExtension);
    }

    /**
     * Obtain name of commit file.
     * @return the commit file name
     */
    private File getCommitFile() {
        return theNumBackups == 0
            ? new File(thePathName, theBaseName + theExtension + COMMIT_EXT)
            : new File(thePathName, theBaseName + theExtension + CHAR_DOT + theNumBackups + COMMITX_EXT);
    }

    /**
     * Obtain name of temporary file to write to.
     * @return the temporary name
     */
    public File getFileToWriteTo() {
        return new File(thePathName, theBaseName + theExtension + TEMP_EXT);
    }

    /**
     * Obtain standard name.
     * @return the standard name
     */
    public File getRealFile() {
        return new File(thePathName, theBaseName + theExtension);
    }

    /**
     * Strip the last extension.
     * @param pName the name
     * @return the stripped name
     */
    private static String minusExtension(final String pName) {
        final int myIndex = pName.lastIndexOf(CHAR_DOT);
        return pName.substring(0, myIndex);
    }

    /**
     * Obtain the number of backups.
     * @param pName the name
     * @return the number of backups
     */
    private static int obtainNumBackups(final String pName) {
        final int myIndex = pName.lastIndexOf(CHAR_DOT);
        final String myBackups = pName.substring(myIndex + 1);
        return Integer.parseInt(myBackups);
    }

    /**
     * ClearUp directory.
     * @param pDirectory the directory to clean
     */
    public static void clearDirectory(final File pDirectory) {
        /* Delete all temporary files */
        final FilenameFilter myTempFilter = (dir, name) -> name.endsWith(TEMP_EXT);
        final File[] myTemps = pDirectory.listFiles(myTempFilter);
        if (myTemps != null) {
            for (File myTemp : myTemps) {
                try {
                    deleteFile(myTemp);
                } catch (OceanusException e) {
                    LOGGER.error("Failed to delete file: " + myTemp.getName(), e);
                }
            }
        }

        /* Process all commit files */
        final FilenameFilter myCommitFilter = (dir, name) -> name.endsWith(COMMIT_EXT);
        final File[] myCommits = pDirectory.listFiles(myCommitFilter);
        if (myCommits != null) {
            for (File myCommit : myCommits) {
                try {
                    final String myName = minusExtension(myCommit.getAbsolutePath());
                    final MetisSafeFileManager myManager = new MetisSafeFileManager(new File(myName));
                    myManager.cleanUp();
                } catch (OceanusException e) {
                    LOGGER.error("Failed to commit file: " + myCommit.getName(), e);
                }
            }
        }

        /* Process all commitx files */
        final FilenameFilter myCommitXFilter = (dir, name) -> name.endsWith(COMMITX_EXT);
        final File[] myCommitXs = pDirectory.listFiles(myCommitXFilter);
        if (myCommitXs != null) {
            for (File myCommit : myCommitXs) {
                try {
                    String myName = minusExtension(myCommit.getAbsolutePath());
                    final int myNumBackUps = obtainNumBackups(myName);
                    myName = minusExtension(myName);
                    final MetisSafeFileManager myManager = new MetisSafeFileManager(new File(myName));
                    myManager.setNumBackups(myNumBackUps);
                    myManager.cleanUp();
                } catch (OceanusException e) {
                    LOGGER.error("Failed to commit file: " + myCommit.getName(), e);
                }
            }
        }
    }
}
