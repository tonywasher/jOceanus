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

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;
import net.sourceforge.joceanus.jthemis.jira.data.Issue;
import net.sourceforge.joceanus.jthemis.svn.data.JSvnReporter.ReportStatus;
import net.sourceforge.joceanus.jthemis.svn.data.Repository;
import net.sourceforge.joceanus.jthemis.svn.data.WorkingCopy.WorkingCopySet;

import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.wc.SVNCommitPacket;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNEventAction;
import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * Handles commit of changes in a WorkingCopy.
 * @author Tony Washer
 */
public class CommitMgr {
    /**
     * Issue prefix.
     */
    private static final String ISSUE_PFIX = "Issue #: ";

    /**
     * Buffer length.
     */
    private static final int BUFFER_LEN = 1000;

    /**
     * Repository.
     */
    private final Repository theRepository;

    /**
     * WorkingSet.
     */
    private final WorkingCopySet theWorkingSet;

    /**
     * Report object.
     */
    private final ReportStatus theReport;

    /**
     * Client manager.
     */
    private final SVNClientManager theMgr;

    /**
     * Revision.
     */
    private SVNRevision theRevision = null;

    /**
     * Event Handler.
     */
    private final CommitHandler theHandler = new CommitHandler();

    /**
     * Obtain revision.
     * @return the revision
     */
    public SVNRevision getNewRevision() {
        return theRevision;
    }

    /**
     * Constructor.
     * @param pWorkingSet the workingSet
     * @param pReport the report object
     */
    public CommitMgr(final WorkingCopySet pWorkingSet,
                     final ReportStatus pReport) {
        /* Store parameters */
        theWorkingSet = pWorkingSet;
        theRepository = theWorkingSet.getRepository();
        theReport = pReport;
        theMgr = theRepository.getClientManager();
        theMgr.setEventHandler(theHandler);
    }

    /**
     * Dispose of resources.
     */
    public void dispose() {
        theRepository.releaseClientManager(theMgr);
    }

    /**
     * Commit the changes to the repository.
     * @param pIssue the issue to commit against.
     * @param pComments the comments for the change
     * @throws JDataException on error
     */
    public void commitChanges(final Issue pIssue,
                              final String pComments) throws JDataException {
        /* Access commit client */
        SVNCommitClient myCommit = theMgr.getCommitClient();

        /* Protect against exceptions */
        try {
            /* Access paths in question */
            File[] myPaths = theWorkingSet.getLocationsArray();

            /* Build the commit message */
            StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
            myBuilder.append(ISSUE_PFIX);
            myBuilder.append(pIssue.getKey());
            myBuilder.append("\nWorking ");
            myBuilder.append(pIssue.getKey());
            myBuilder.append(" : ");
            myBuilder.append(pIssue.getSummary());
            myBuilder.append('\n');
            myBuilder.append(pComments);
            String myMessage = myBuilder.toString();

            /* Build the commit package */
            SVNCommitPacket[] myPackets = myCommit.doCollectCommitItems(myPaths, false, true, SVNDepth.INFINITY, true, null);

            /* Ensure that there is only one packet */
            if (myPackets.length > 1) {
                throw new JDataException(ExceptionClass.SUBVERSION, "Too many commit packets");
            }

            /* Commit the changes */
            SVNCommitInfo myInfo = myCommit.doCommit(myPackets[0], false, myMessage);

            /* Obtain the new revision */
            theRevision = SVNRevision.create(myInfo.getNewRevision());

        } catch (SVNException e) {
            throw new JDataException(ExceptionClass.SUBVERSION, "Failed to commit changes", e);
        }
    }

    /**
     * EventHandler.
     */
    private final class CommitHandler
            implements ISVNEventHandler {
        @Override
        public void checkCancelled() throws SVNCancelException {
            if (theReport.isCancelled()) {
                throw new SVNCancelException();
            }
        }

        @Override
        public void handleEvent(final SVNEvent pEvent,
                                final double pProgress) throws SVNException {
            /* Access the Action */
            SVNEventAction myAction = pEvent.getAction();

            /* If this is the start of the checkOut */
            if (myAction.equals(SVNEventAction.COMMIT_COMPLETED)) {
                /* Record the revision */
                theReport.setNewStage("Commit completed at revision "
                                      + pEvent.getRevision());

            } else if (myAction.equals(SVNEventAction.COMMIT_ADDED)) {
                /* Report activity */
                theReport.setNewStage("A "
                                      + pEvent.getFile().getPath());

            } else if (myAction.equals(SVNEventAction.COMMIT_MODIFIED)) {
                /* Report activity */
                theReport.setNewStage("M "
                                      + pEvent.getFile().getPath());

            } else if (myAction.equals(SVNEventAction.COMMIT_DELETED)) {
                /* Report activity */
                theReport.setNewStage("D "
                                      + pEvent.getFile().getPath());

            } else if (myAction.equals(SVNEventAction.COMMIT_REPLACED)) {
                /* Report activity */
                theReport.setNewStage("R "
                                      + pEvent.getFile().getPath());

            } else {
                theReport.setNewStage(pEvent.getFile().getPath());
            }
        }
    }
}
