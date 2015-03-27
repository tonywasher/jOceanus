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
package net.sourceforge.joceanus.jthemis.svn.tasks;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import net.sourceforge.joceanus.jgordianknot.crypto.PasswordHash;
import net.sourceforge.joceanus.jgordianknot.swing.SecureManager;
import net.sourceforge.joceanus.jgordianknot.zip.ZipFileEntry;
import net.sourceforge.joceanus.jgordianknot.zip.ZipReadFile;
import net.sourceforge.joceanus.jgordianknot.zip.ZipWriteFile;
import net.sourceforge.joceanus.jmetis.preference.PreferenceManager;
import net.sourceforge.joceanus.jprometheus.preferences.BackupPreferences;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jthemis.JThemisIOException;
import net.sourceforge.joceanus.jthemis.scm.data.ScmReporter.ReportStatus;
import net.sourceforge.joceanus.jthemis.svn.data.SubVersionPreferences;
import net.sourceforge.joceanus.jthemis.svn.data.SvnRepository;

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

/**
 * Handles backup of a repository.
 * @author Tony Washer
 */
public class Backup {
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
    private static final Logger LOGGER = LoggerFactory.getLogger(Backup.class);

    /**
     * The Buffer length.
     */
    private static final int BUFFER_LEN = 100;

    /**
     * The preference manager.
     */
    private final PreferenceManager thePreferenceMgr;

    /**
     * The Subversion preferences.
     */
    private final SubVersionPreferences thePreferences;

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
    private final ReportStatus theStatus;

    /**
     * Constructor.
     * @param pStatus the status reporter
     * @param pPrefMgr the preference manager
     */
    public Backup(final ReportStatus pStatus,
                  final PreferenceManager pPrefMgr) {
        /* Store parameters */
        theStatus = pStatus;
        thePreferenceMgr = pPrefMgr;

        /* Access the SubVersion preferences */
        thePreferences = thePreferenceMgr.getPreferenceSet(SubVersionPreferences.class);

        /* Access a default client manager */
        theAuth = SVNWCUtil.createDefaultAuthenticationManager(thePreferences.getStringValue(SubVersionPreferences.NAME_SVN_USER),
                thePreferences.getStringValue(SubVersionPreferences.NAME_SVN_PASS));

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
     * @throws JOceanusException on error
     */
    public void loadRepository(final File pRepository,
                               final SecureManager pSecurity,
                               final File pZipFile) throws JOceanusException {
        /* Protect against exceptions */
        try (ZipReadFile myFile = new ZipReadFile(pZipFile)) {
            /* Install an event handler */
            theAdminClient.setEventHandler(new SubversionHandler());

            /* Obtain the hash bytes from the file */
            byte[] myHashBytes = myFile.getHashBytes();

            /* Obtain the initialised password hash */
            PasswordHash myHash = pSecurity.resolvePasswordHash(myHashBytes, pZipFile.getName());

            /* Associate this password hash with the ZipFile */
            myFile.setPasswordHash(myHash);

            /* Access the relevant entry and obtain the number of revisions */
            ZipFileEntry myEntry = myFile.getContents().findFileEntry(DATA_NAME);
            Long myNumRevs = myEntry.getUserLongProperty(PROP_NUMREV);

            /* Declare the stage */
            if (!theStatus.setNewStage("Repository")) {
                return;
            }

            /* Declare the number of revisions */
            if (!theStatus.setNumSteps(myNumRevs.intValue())) {
                return;
            }

            /* Access the input stream for the relevant file */
            InputStream myStream = myFile.getInputStream(myEntry);

            /* Re-create the repository */
            theAdminClient.doCreateRepository(pRepository, null, true, true);

            /* Read the data from the input stream */
            theAdminClient.doLoad(pRepository, myStream);
        } catch (IOException
                | SVNException e) {
            throw new JThemisIOException("Failed", e);
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
        myBuilder.append(thePreferences.getStringValue(SubVersionPreferences.NAME_SVN_REPO));

        /* Build the prefix directory */
        myBuilder.append(SvnRepository.SEP_URL);
        myBuilder.append(SvnRepository.PFIX_URL);

        /* Build the component directory */
        myBuilder.append(SvnRepository.SEP_URL);
        myBuilder.append(pName);

        /* Return the path */
        return myBuilder.toString();
    }

    /**
     * Dump a repository to a Backup directory.
     * @param pManager the secure manager
     * @param pRepository the repository directory
     * @param pBackupDir the backup directory
     * @throws JOceanusException on error
     */
    private void backUpRepository(final SecureManager pManager,
                                  final File pRepository,
                                  final File pBackupDir) throws JOceanusException {
        /* Access the name of the repository */
        String myName = pRepository.getName();
        File myEntryName = new File(DATA_NAME);

        /* Determine the prefix for backups */
        String myPrefix = thePreferences.getStringValue(SubVersionPreferences.NAME_REPO_PFIX);
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
            myZipName = new File(pBackupDir.getPath(), myPrefix + myName + ZipReadFile.ZIPFILE_EXT);

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
            throw new JThemisIOException("Failed to analyse existing backup", e);
        }

        /* Declare the number of revisions */
        int myNumRevisions = (int) revLast;
        if (!theStatus.setNumSteps(myNumRevisions)) {
            return;
        }

        /* Note presumption of failure */
        boolean doDelete = true;

        /* Create a new password hash */
        PasswordHash myHash = pManager.resolvePasswordHash(null, myName);

        /* Protect against exceptions */
        try (ZipWriteFile myZipFile = new ZipWriteFile(myHash, myZipName);
             OutputStream myStream = myZipFile.getOutputStream(myEntryName)) {
            /* Access the current entry and set the number of revisions */
            ZipFileEntry myEntry = myZipFile.getCurrentEntry();
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
            throw new JThemisIOException("Failed to dump repository to zipfile", e);

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
     * @throws JOceanusException on error
     */
    public void backUpRepositories(final SecureManager pManager) throws JOceanusException {
        /* Install an event handler */
        theAdminClient.setEventHandler(new SubversionHandler());

        /* Access the BackUp preferences */
        BackupPreferences myBUPreferences = thePreferenceMgr.getPreferenceSet(BackupPreferences.class);

        /* Determine the repository and backup directories directory */
        File myRepo = new File(thePreferences.getStringValue(SubVersionPreferences.NAME_SVN_DIR));
        File myBackup = new File(myBUPreferences.getStringValue(BackupPreferences.NAME_BACKUP_DIR));

        /* Report start of backup */
        if (!theStatus.initTask("Backing up subVersion")) {
            return;
        }

        /* Loop through the repository directories */
        int iNumStages = 0;
        for (File myRepository : myRepo.listFiles()) {
            /* Count if its is a directory */
            if (myRepository.isDirectory()) {
                iNumStages++;
            }
        }

        /* Declare the number of stages */
        boolean bContinue = theStatus.setNumStages(iNumStages);

        /* Ignore if cancelled */
        if (!bContinue) {
            return;
        }

        /* Loop through the repository directories */
        for (File myRepository : myRepo.listFiles()) {
            /* Ignore if its is not a directory */
            if (!myRepository.isDirectory()) {
                continue;
            }

            /* Set new stage and break if cancelled */
            if (!theStatus.setNewStage(myRepository.getName())) {
                break;
            }

            /* Backup the repositories */
            backUpRepository(pManager, myRepository, myBackup);
        }

        /* Report end of backup */
        if (!theStatus.isCancelled()) {
            theStatus.initTask("Backup completed");
        }
    }

    /**
     * Event Handler class.
     */
    private final class SubversionHandler
            implements ISVNAdminEventHandler {

        @Override
        public void checkCancelled() throws SVNCancelException {
            if (theStatus.isCancelled()) {
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
                /* Set new step */
                theStatus.setNewStep("Revision");
            }
        }

        @Override
        public void handleEvent(final SVNEvent arg0,
                                final double arg1) throws SVNException {
            /* Not needed */
        }
    }
}
