/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jthemis.tasks;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

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

import net.sourceforge.joceanus.jgordianknot.api.impl.GordianSecurityManager;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipFactory;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipFileEntry;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipReadFile;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipWriteFile;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnPreference.ThemisSvnPreferenceKey;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnPreference.ThemisSvnPreferences;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnRepository;

/**
 * Handles backup of a repository.
 * @author Tony Washer
 */
public class ThemisSvnBackup {
    /**
     * The Number Of Revisions.
     */
    private static final String PROP_NUMREV = "NumRevisions";

    /**
     * The Data file name.
     */
    private static final String DATA_NAME = "zipData";

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
    public ThemisSvnBackup(final MetisThreadStatusReport pStatus,
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
        final SVNClientManager myManager = SVNClientManager.newInstance();
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
                               final GordianSecurityManager pSecurity,
                               final File pZipFile) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Open file */
            final GordianZipFactory myZips = pSecurity.getSecurityFactory().getZipFactory();
            final GordianZipReadFile myFile = myZips.openZipFile(pZipFile);

            /* Install an event handler */
            theAdminClient.setEventHandler(new SubversionHandler());

            /* Obtain the hash bytes from the file */
            final byte[] myHashBytes = myFile.getHashBytes();

            /* Obtain the initialised password hash */
            final GordianKeySetHash myHash = pSecurity.resolveKeySetHash(myHashBytes, pZipFile.getName());

            /* Associate this keySetHash with the ZipFile */
            myFile.setKeySetHash(myHash);

            /* Access the relevant entry and obtain the number of revisions */
            final GordianZipFileEntry myEntry = myFile.getContents().findFileEntry(DATA_NAME);
            final Long myNumRevs = myEntry.getUserLongProperty(PROP_NUMREV);

            /* Declare the stage */
            theStatus.setNewStage("Repository");

            /* Declare the number of revisions */
            theStatus.setNumSteps(myNumRevs.intValue());

            /* Access the input stream for the relevant file */
            try (InputStream myStream = myFile.createInputStream(myEntry)) {
                /* Re-create the repository */
                theAdminClient.doCreateRepository(pRepository, null, true, true);

                /* Read the data from the input stream */
                theAdminClient.doLoad(pRepository, myStream);
            }
        } catch (SVNException
                | IOException e) {
            throw new ThemisIOException("Failed", e);
        }
    }

    /**
     * Build URL.
     * @param pName the name of the repository
     * @return the Repository path
     */
    private String buildURL(final String pName) {
        /* Build the underlying string */
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Build the repository URL */
        myBuilder.append(thePreferences.getStringValue(ThemisSvnPreferenceKey.BASE))
                .append(ThemisSvnRepository.SEP_URL)
                .append(ThemisSvnRepository.PFIX_URL)
                .append(ThemisSvnRepository.SEP_URL)
                .append(pName);

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
    private void backUpRepository(final GordianSecurityManager pManager,
                                  final File pRepository,
                                  final File pBackupDir) throws OceanusException {
        /* Access the name of the repository */
        final String myName = pRepository.getName();

        /* Determine the prefix for backups */
        final String myPrefix = thePreferences.getStringValue(ThemisSvnPreferenceKey.PFIX);
        File myZipName = null;
        final long revLast;

        /* Protect against exceptions */
        try {
            /* Determine the repository name */
            final String myRepoName = buildURL(myName);
            final SVNURL myURL = SVNURL.parseURIEncoded(myRepoName);

            /* Access the repository */
            final SVNRepository myRepo = SVNRepositoryFactory.create(myURL);
            myRepo.setAuthenticationManager(theAuth);

            /* Determine the most recent revision # in the repository */
            revLast = myRepo.getDatedRevision(new Date());

            /* Determine the name of the zip file */
            myZipName = new File(pBackupDir.getPath(), myPrefix + myName + GordianSecurityManager.SECUREZIPFILE_EXT);

            /* If the backup file exists */
            if (myZipName.exists()) {
                /* Access the last modified time of the backup */
                final Date myDate = new Date();
                myDate.setTime(myZipName.lastModified());

                /* Access the revision for the zip file */
                final long revZip = myRepo.getDatedRevision(myDate);

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
        final int myNumRevisions = (int) revLast;
        theStatus.setNumSteps(myNumRevisions);

        /* Create a new password hash */
        final GordianKeySetHash myHash = pManager.newKeySetHash(myName);
        final File myEntryName = new File(DATA_NAME);

        /* Protect against exceptions */
        boolean writeFailed = false;
        final GordianZipFactory myZips = pManager.getSecurityFactory().getZipFactory();
        try (GordianZipWriteFile myZipFile = myZips.createZipFile(myHash, myZipName);
             OutputStream myStream = myZipFile.createOutputStream(myEntryName, true)) {
            /* Access the current entry and set the number of revisions */
            final GordianZipFileEntry myEntry = myZipFile.getCurrentEntry();
            myEntry.setUserLongProperty(PROP_NUMREV, revLast);

            /* Dump the data to the zip file */
            theAdminClient.doDump(pRepository, myStream, SVNRevision.UNDEFINED, SVNRevision.create(revLast), false, true);

            /* Close the stream */
            myStream.close();
            myZipFile.close();

            /* Handle other exceptions */
        } catch (SVNException
                | IOException e) {
            writeFailed = true;
            throw new ThemisIOException("Failed to dump repository to zipfile", e);

            /* Clean up on exit */
        } finally {
            /* Try to delete the file if required */
            if (writeFailed) {
                MetisToolkit.cleanUpFile(myZipName);
            }
        }
    }

    /**
     * Backup repositories.
     * @param pManager the secure manager
     * @throws OceanusException on error
     */
    public void backUpRepositories(final GordianSecurityManager pManager) throws OceanusException {
        /* Install an event handler */
        theAdminClient.setEventHandler(new SubversionHandler());

        /* Determine the repository and backup directories directory */
        final File myRepo = new File(thePreferences.getStringValue(ThemisSvnPreferenceKey.INSTALL));
        final File myBackup = new File(thePreferences.getStringValue(ThemisSvnPreferenceKey.BACKUP));

        /* Report start of backup */
        theStatus.initTask("Backing up subVersion");

        /* Loop through the repository directories */
        int iNumStages = 0;
        for (final File myRepository : myRepo.listFiles()) {
            /* Count if its is a directory */
            if (myRepository.isDirectory()) {
                iNumStages++;
            }
        }

        /* Declare the number of stages */
        theStatus.setNumStages(iNumStages);

        /* Loop through the repository directories */
        for (final File myRepository : myRepo.listFiles()) {
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
            final SVNAdminEventAction myAction = pEvent.getAction();
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
