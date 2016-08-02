/*******************************************************************************
 * jThemis: Java Project Framework
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jthemis.svn.tasks;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.wc.admin.ISVNAdminEventHandler;
import org.tmatesoft.svn.core.wc.admin.SVNAdminClient;
import org.tmatesoft.svn.core.wc.admin.SVNAdminEvent;
import org.tmatesoft.svn.core.wc.admin.SVNAdminEventAction;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jgordianknot.zip.GordianZipFileEntry;
import net.sourceforge.joceanus.jgordianknot.zip.GordianZipReadFile;
import net.sourceforge.joceanus.jgordianknot.zip.GordianZipWriteFile;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jprometheus.preference.PrometheusBackup.PrometheusBackupPreferenceKey;
import net.sourceforge.joceanus.jprometheus.preference.PrometheusBackup.PrometheusBackupPreferences;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnPreference.ThemisSvnPreferenceKey;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnPreference.ThemisSvnPreferences;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnRepository;

/**
 * Handles backup of a repository.
 * @author Tony Washer
 */
public class ThemisBackup {
    /**
     * The Number Of Revisions.
     */
    private static final String PROP_NUMREV = "NumRevisions";

    /**
     * The Data file name.
     */
    private static final String DATA_NAME = "zipData";

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ThemisBackup.class);

    /**
     * The Buffer length.
     */
    private static final int BUFFER_LEN = 100;

    /**
     * The preference manager.
     */
    private final MetisPreferenceManager thePreferenceMgr;

    /**
     * The Subversion preferences.
     */
    private final ThemisSvnPreferences thePreferences;

    /**
     * The Authentication manager.
     */
    private final ISVNAuthenticationManager theAuth;

    /**
     * The Administration Client.
     */
    private final SVNAdminClient theAdminClient;

    /**
     * The Status reporter.
     */
    private final MetisThreadStatusReport theStatus;

    /**
     * Constructor.
     * @param pStatus the status reporter
     * @param pPrefMgr the preference manager
     */
    public ThemisBackup(final MetisThreadStatusReport pStatus,
                        final MetisPreferenceManager pPrefMgr) {
        /* Store parameters */
        theStatus = pStatus;
        thePreferenceMgr = pPrefMgr;

        /* Access the SubVersion preferences */
        thePreferences = thePreferenceMgr.getPreferenceSet(ThemisSvnPreferences.class);

        /* Access a default client manager */
        theAuth = SVNWCUtil.createDefaultAuthenticationManager(thePreferences.getStringValue(ThemisSvnPreferenceKey.USER),
                thePreferences.getCharArrayValue(ThemisSvnPreferenceKey.PASS));

        /* Access a default client manager */
        SVNClientManager myManager = SVNClientManager.newInstance();
        myManager.setAuthenticationManager(theAuth);

        /* Access Administration and Look clients */
        theAdminClient = myManager.getAdminClient();
    }

    /**
     * Load a repository from the input stream.
     * @param pRepository the repository directory
     * @param pSecurity the secure manager
     * @param pZipFile the zipFile to load
     * @throws OceanusException on error
     */
    public void loadRepository(final File pRepository,
                               final GordianHashManager pSecurity,
                               final File pZipFile) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Open file */
            GordianZipReadFile myFile = new GordianZipReadFile(pZipFile);

            /* Install an event handler */
            theAdminClient.setEventHandler(new SubversionHandler());

            /* Obtain the hash bytes from the file */
            byte[] myHashBytes = myFile.getHashBytes();

            /* Obtain the initialised password hash */
            GordianKeySetHash myHash = pSecurity.resolveKeySetHash(myHashBytes, pZipFile.getName());

            /* Associate this keySetHash with the ZipFile */
            myFile.setKeySetHash(myHash);

            /* Access the relevant entry and obtain the number of revisions */
            GordianZipFileEntry myEntry = myFile.getContents().findFileEntry(DATA_NAME);
            Long myNumRevs = myEntry.getUserLongProperty(PROP_NUMREV);

            /* Declare the stage */
            theStatus.setNewStage("Repository");

            /* Declare the number of revisions */
            theStatus.setNumSteps(myNumRevs.intValue());

            /* Access the input stream for the relevant file */
            InputStream myStream = myFile.getInputStream(myEntry);

            /* Re-create the repository */
            theAdminClient.doCreateRepository(pRepository, null, true, true);

            /* Read the data from the input stream */
            theAdminClient.doLoad(pRepository, myStream);
        } catch (SVNException e) {
            throw new ThemisIOException("Failed", e);
        }

        /* Return to caller */
        return;
    }

    /**
     * Build URL.
     * @param pName the name of the repository
     * @return the Repository path
     */
    private String buildURL(final String pName) {
        /* Build the underlying string */
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Build the repository */
        myBuilder.append(thePreferences.getStringValue(ThemisSvnPreferenceKey.BASE));

        /* Build the prefix directory */
        myBuilder.append(ThemisSvnRepository.SEP_URL);
        myBuilder.append(ThemisSvnRepository.PFIX_URL);

        /* Build the component directory */
        myBuilder.append(ThemisSvnRepository.SEP_URL);
        myBuilder.append(pName);

        /* Return the path */
        return myBuilder.toString();
    }

    /**
     * Dump a repository to a Backup directory.
     * @param pManager the secure manager
     * @param pRepository the repository directory
     * @param pBackupDir the backup directory
     * @throws OceanusException on error
     */
    private void backUpRepository(final GordianHashManager pManager,
                                  final File pRepository,
                                  final File pBackupDir) throws OceanusException {
        /* Access the name of the repository */
        String myName = pRepository.getName();
        File myEntryName = new File(DATA_NAME);

        /* Determine the prefix for backups */
        String myPrefix = thePreferences.getStringValue(ThemisSvnPreferenceKey.PFIX);
        File myZipName = null;
        long revLast;

        /* Protect against exceptions */
        try {
            /* Determine the repository name */
            String myRepoName = buildURL(myName);
            SVNURL myURL = SVNURL.parseURIEncoded(myRepoName);

            /* Access the repository */
            SVNRepository myRepo = SVNRepositoryFactory.create(myURL);
            myRepo.setAuthenticationManager(theAuth);

            /* Determine the most recent revision # in the repository */
            revLast = myRepo.getDatedRevision(new Date());

            /* Determine the name of the zip file */
            myZipName = new File(pBackupDir.getPath(), myPrefix + myName + GordianZipReadFile.ZIPFILE_EXT);

            /* If the backup file exists */
            if (myZipName.exists()) {
                /* Access the last modified time of the backup */
                Date myDate = new Date();
                myDate.setTime(myZipName.lastModified());

                /* Access the revision for the zip file */
                long revZip = myRepo.getDatedRevision(myDate);

                /* If the Backup date is later than the repository date */
                if (revZip >= revLast) {
                    /* No need to backup the repository so just return */
                    return;
                }
            }
        } catch (SVNException e) {
            throw new ThemisIOException("Failed to analyse existing backup", e);
        }

        /* Declare the number of revisions */
        int myNumRevisions = (int) revLast;
        theStatus.setNumSteps(myNumRevisions);

        /* Note presumption of failure */
        boolean doDelete = true;

        /* Create a new password hash */
        GordianKeySetHash myHash = pManager.resolveKeySetHash(null, myName);

        /* Protect against exceptions */
        try (GordianZipWriteFile myZipFile = new GordianZipWriteFile(myHash, myZipName);
             OutputStream myStream = myZipFile.getOutputStream(myEntryName)) {
            /* Access the current entry and set the number of revisions */
            GordianZipFileEntry myEntry = myZipFile.getCurrentEntry();
            myEntry.setUserLongProperty(PROP_NUMREV, revLast);

            /* Dump the data to the zip file */
            theAdminClient.doDump(pRepository, myStream, SVNRevision.UNDEFINED, SVNRevision.create(revLast), false, true);

            /* Close the stream */
            myStream.close();
            myZipFile.close();

            /* Note success */
            doDelete = false;

            /* Handle other exceptions */
        } catch (SVNException
                | IOException e) {
            throw new ThemisIOException("Failed to dump repository to zipfile", e);

            /* Clean up on exit */
        } finally {
            /* Delete the file on error */
            if (doDelete && !myZipName.delete()) {
                LOGGER.error("Failed to delete file on failure");
            }
        }
    }

    /**
     * Backup repositories.
     * @param pManager the secure manager
     * @throws OceanusException on error
     */
    public void backUpRepositories(final GordianHashManager pManager) throws OceanusException {
        /* Install an event handler */
        theAdminClient.setEventHandler(new SubversionHandler());

        /* Access the BackUp preferences */
        PrometheusBackupPreferences myBUPreferences = thePreferenceMgr.getPreferenceSet(PrometheusBackupPreferences.class);

        /* Determine the repository and backup directories directory */
        File myRepo = new File(thePreferences.getStringValue(ThemisSvnPreferenceKey.INSTALL));
        File myBackup = new File(myBUPreferences.getStringValue(PrometheusBackupPreferenceKey.BACKUPDIR));

        /* Report start of backup */
        theStatus.initTask("Backing up subVersion");

        /* Loop through the repository directories */
        int iNumStages = 0;
        for (File myRepository : myRepo.listFiles()) {
            /* Count if its is a directory */
            if (myRepository.isDirectory()) {
                iNumStages++;
            }
        }

        /* Declare the number of stages */
        theStatus.setNumStages(iNumStages);

        /* Loop through the repository directories */
        for (File myRepository : myRepo.listFiles()) {
            /* Ignore if its is not a directory */
            if (!myRepository.isDirectory()) {
                continue;
            }

            /* Set new stage and break if cancelled */
            theStatus.setNewStage(myRepository.getName());

            /* Backup the repositories */
            backUpRepository(pManager, myRepository, myBackup);
        }
    }

    /**
     * Event Handler class.
     */
    private final class SubversionHandler
            implements ISVNAdminEventHandler {

        @Override
        public void checkCancelled() throws SVNCancelException {
            try {
                theStatus.checkForCancellation();
            } catch (OceanusException e) {
                throw new SVNCancelException();
            }
        }

        @Override
        public void handleAdminEvent(final SVNAdminEvent pEvent,
                                     final double arg1) throws SVNException {
            /* If this is a step */
            SVNAdminEventAction myAction = pEvent.getAction();
            if (myAction.equals(SVNAdminEventAction.REVISION_DUMPED)
                || myAction.equals(SVNAdminEventAction.REVISION_LOADED)) {
                try {
                    /* Set new step */
                    theStatus.setNextStep("Revision");
                } catch (OceanusException e) {
                    throw new SVNCancelException();
                }
            }
        }

        @Override
        public void handleEvent(final SVNEvent arg0,
                                final double arg1) throws SVNException {
            /* Not needed */
        }
    }
}