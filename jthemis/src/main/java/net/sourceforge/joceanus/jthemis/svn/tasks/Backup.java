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
import java.util.logging.Level;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;
import net.sourceforge.joceanus.jdatamodels.data.TaskControl;
import net.sourceforge.joceanus.jdatamodels.preferences.BackupPreferences;
import net.sourceforge.joceanus.jdatamodels.sheets.SpreadSheet;
import net.sourceforge.joceanus.jgordianknot.PasswordHash;
import net.sourceforge.joceanus.jgordianknot.SecureManager;
import net.sourceforge.joceanus.jgordianknot.zipfile.ZipFileEntry;
import net.sourceforge.joceanus.jgordianknot.zipfile.ZipReadFile;
import net.sourceforge.joceanus.jgordianknot.zipfile.ZipWriteFile;
import net.sourceforge.joceanus.jpreferenceset.PreferenceManager;
import net.sourceforge.joceanus.jthemis.svn.data.Repository;
import net.sourceforge.joceanus.jthemis.svn.data.SubVersionPreferences;

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
     * The Client Manager.
     */
    private final SVNClientManager theManager;

    /**
     * The Administration Client.
     */
    private final SVNAdminClient theAdminClient;

    /**
     * The Task Control.
     */
    private final TaskControl<?> theTask;

    /**
     * Constructor.
     * @param pTask the task control
     * @param pPreferenceMgr the preference manager
     */
    public Backup(final TaskControl<?> pTask,
                  final PreferenceManager pPreferenceMgr) {
        /* Store parameters */
        theTask = pTask;
        thePreferenceMgr = pPreferenceMgr;

        /* Access the SubVersion preferences */
        thePreferences = thePreferenceMgr.getPreferenceSet(SubVersionPreferences.class);

        /* Access a default client manager */
        theAuth = SVNWCUtil.createDefaultAuthenticationManager(thePreferences.getStringValue(SubVersionPreferences.NAME_SVN_USER),
                thePreferences.getStringValue(SubVersionPreferences.NAME_SVN_PASS));

        /* Access a default client manager */
        theManager = SVNClientManager.newInstance();
        theManager.setAuthenticationManager(theAuth);

        /* Access Administration and Look clients */
        theAdminClient = theManager.getAdminClient();
    }

    /**
     * Load a repository from the input stream.
     * @param pRepository the repository directory
     * @param pSecurity the secure manager
     * @param pZipFile the zipFile to load
     * @throws JDataException on error
     */
    public void loadRepository(final File pRepository,
                               final SecureManager pSecurity,
                               final File pZipFile) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Access the zipFile */
            ZipReadFile myFile = new ZipReadFile(pZipFile, theTask.getLogger());

            /* Obtain the hash bytes from the file */
            byte[] myHashBytes = myFile.getHashBytes();

            /* Obtain the initialised password hash */
            PasswordHash myHash = pSecurity.resolvePasswordHash(myHashBytes, pZipFile.getName());

            /* Associate this password hash with the ZipFile */
            myFile.setPasswordHash(myHash);

            /* Access the relevant entry and obtain the number of revisions */
            ZipFileEntry myEntry = myFile.getContents().findFileEntry(DATA_NAME);
            // Long myNumRevs = myEntry.getUserLongProperty(PROP_NUMREV);

            /* Access the input stream for the relevant file */
            InputStream myStream = myFile.getInputStream(myEntry);

            /* Re-create the repository */
            theAdminClient.doCreateRepository(pRepository, null, true, true);

            /* Read the data from the input stream */
            theAdminClient.doLoad(pRepository, myStream);
        } catch (SVNException e) {
            throw new JDataException(ExceptionClass.SUBVERSION, "Failed", e);
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
        myBuilder.append(Repository.SEP_URL);
        myBuilder.append(Repository.PFIX_URL);

        /* Build the component directory */
        myBuilder.append(Repository.SEP_URL);
        myBuilder.append(pName);

        /* Return the path */
        return myBuilder.toString();
    }

    /**
     * Dump a repository to a Backup directory.
     * @param pManager the secure manager
     * @param pHash the password hash
     * @param pRepository the repository directory
     * @param pBackupDir the backup directory
     * @throws JDataException on error
     */
    private void backUpRepository(final SecureManager pManager,
                                  final PasswordHash pHash,
                                  final File pRepository,
                                  final File pBackupDir) throws JDataException {
        ZipWriteFile myZipFile = null;
        OutputStream myStream = null;
        boolean bSuccess = true;
        File myZipName = null;

        /* Protect against exceptions */
        try {
            /* Access the name of the repository */
            String myName = pRepository.getName();
            File myEntryName = new File(DATA_NAME);

            /* Determine the prefix for backups */
            String myPrefix = thePreferences.getStringValue(SubVersionPreferences.NAME_REPO_PFIX);

            /* Determine the repository name */
            String myRepoName = buildURL(myName);
            SVNURL myURL = SVNURL.parseURIEncoded(myRepoName);

            /* Access the repository */
            SVNRepository myRepo = SVNRepositoryFactory.create(myURL);
            myRepo.setAuthenticationManager(theAuth);

            /* Determine the most recent revision # in the repository */
            long revLast = myRepo.getDatedRevision(new Date());

            /* Determine the name of the zip file */
            myZipName = new File(pBackupDir.getPath(), myPrefix
                                                       + myName
                                                       + SpreadSheet.ZIPFILE_EXT);

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

            /* Determine the number of revisions */
            int myNumRevisions = (int) revLast;

            /* Declare the number of revisions */
            if (!theTask.setNumSteps(myNumRevisions)) {
                return;
            }

            /* Note presumption of failure */
            bSuccess = false;

            /* Create a clone of the password hash */
            PasswordHash myHash = pManager.clonePasswordHash(pHash);

            /* Create the new zip file */
            myZipFile = new ZipWriteFile(myHash, myZipName);
            myStream = myZipFile.getOutputStream(myEntryName);

            /* Access the current entry and set the number of revisions */
            ZipFileEntry myEntry = myZipFile.getCurrentEntry();
            myEntry.setUserLongProperty(PROP_NUMREV, revLast);

            /* Dump the data to the zip file */
            theAdminClient.doDump(pRepository, myStream, SVNRevision.UNDEFINED, SVNRevision.create(revLast), false, true);

            /* Close the stream */
            myStream.close();
            myStream = null;

            /* Close the Zip file */
            myZipFile.close();
            myZipFile = null;

            /* Note success */
            bSuccess = true;

            /* Handle other exceptions */
        } catch (SVNException e) {
            throw new JDataException(ExceptionClass.SUBVERSION, "Failed to dump repository to zipfile", e);

            /* Handle other exceptions */
        } catch (IOException e) {
            throw new JDataException(ExceptionClass.SUBVERSION, "Failed to dump repository to zipfile", e);

            /* Clean up on exit */
        } finally {
            /* Protect while cleaning up */
            try {
                /* Close the output stream */
                if (myStream != null) {
                    myStream.close();
                }

                /* Close the Zip file */
                if (myZipFile != null) {
                    myZipFile.close();
                }

                /* Ignore errors */
            } catch (Exception ex) {
                thePreferenceMgr.getLogger().log(Level.SEVERE, "Close failure", ex);
            }

            /* Delete the file on error */
            if ((!bSuccess)
                && (myZipName != null)) {
                myZipName.delete();
            }
        }
    }

    /**
     * Backup repositories.
     * @param pManager the secure manager
     * @param pHash the password hash
     * @throws JDataException on error
     */
    public void backUpRepositories(final SecureManager pManager,
                                   final PasswordHash pHash) throws JDataException {
        int iNumStages = 0;

        /* Install an event handler */
        theAdminClient.setEventHandler(new SubversionHandler());

        /* Access the BackUp preferences */
        BackupPreferences myBUPreferences = thePreferenceMgr.getPreferenceSet(BackupPreferences.class);

        /* Determine the repository and backup directories directory */
        File myRepo = new File(thePreferences.getStringValue(SubVersionPreferences.NAME_SVN_DIR));
        File myBackup = new File(myBUPreferences.getStringValue(BackupPreferences.NAME_BACKUP_DIR));

        /* Loop through the repository directories */
        for (File myRepository : myRepo.listFiles()) {
            /* Count if its is a directory */
            if (myRepository.isDirectory()) {
                iNumStages++;
            }
        }

        /* Declare the number of stages */
        boolean bContinue = theTask.setNumStages(iNumStages);

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
            if (!theTask.setNewStage(myRepository.getName())) {
                break;
            }

            /* Backup the repositories */
            backUpRepository(pManager, pHash, myRepository, myBackup);
        }
    }

    /**
     * Event Handler class.
     */
    private final class SubversionHandler
            implements ISVNAdminEventHandler {

        @Override
        public void checkCancelled() throws SVNCancelException {
            if (theTask.isCancelled()) {
                throw new SVNCancelException();
            }
        }

        @Override
        public void handleAdminEvent(final SVNAdminEvent pEvent,
                                     final double arg1) throws SVNException {
            /* Ignore if not an interesting event */
            if (pEvent.getAction() != SVNAdminEventAction.REVISION_DUMPED) {
                return;
            }

            /* Set steps done value */
            theTask.setStepsDone((int) pEvent.getRevision());
        }

        @Override
        public void handleEvent(final SVNEvent arg0,
                                final double arg1) throws SVNException {
            /* Not needed */
        }
    }
}
