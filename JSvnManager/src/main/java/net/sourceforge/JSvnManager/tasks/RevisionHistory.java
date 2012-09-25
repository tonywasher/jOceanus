/*******************************************************************************
 * Subversion: Java SubVersion Management
 * Copyright 2012 Tony Washer
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
 * $URL: http://tony-laptop/svn/finance/JSvnManager/branches/v1.1.0/src/main/java/net/sourceforge/JSvnManager/tasks/VersionMgr.java $
 * $Revision: 153 $
 * $Author: Tony $
 * $Date: 2012-09-07 16:50:07 +0100 (Fri, 07 Sep 2012) $
 ******************************************************************************/
package net.sourceforge.JSvnManager.tasks;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JSvnManager.data.Repository;

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * Methods to access revision history.
 * @author Tony Washer
 */
public class RevisionHistory {
    /**
     * The Client Manager.
     */
    private final SVNClientManager theMgr;

    /**
     * Repository.
     */
    private final Repository theRepository;

    /**
     * Report object.
     */
    // private final ReportStatus theReport;

    /**
     * Result set.
     */
    private final List<SVNLogEntry> theResults;

    /**
     * Obtain the results.
     * @return the results
     */
    public List<SVNLogEntry> getResults() {
        return theResults;
    }

    /**
     * Constructor.
     * @param pRepository the repository
     */
    public RevisionHistory(final Repository pRepository) {
        // final ReportStatus pReport) {
        /* Store Parameters */
        theRepository = pRepository;
        theMgr = theRepository.getClientManager();
        theResults = new ArrayList<SVNLogEntry>();
    }

    /**
     * Obtain revision history.
     * @param pRevision the revision to get history for
     * @throws JDataException on error
     */
    public void getRevisionHistory(final long pRevision) throws JDataException {
        /* Access client */
        SVNLogClient myLog = theMgr.getLogClient();
        theResults.clear();

        /* Protect against exceptions */
        try {
            /* Obtain information about the revision */
            SVNURL myURL = SVNURL.parseURIEncoded(theRepository.getPath());
            SVNRevision myRevision = SVNRevision.create(pRevision);
            myLog.doLog(myURL, null, myRevision, myRevision, myRevision, false, true, 0, new LogHandler());
        } catch (SVNException e) {
            throw new JDataException(ExceptionClass.SUBVERSION, "Failed to get revision history", e);
        }
    }

    /**
     * Obtain file revision history.
     * @param pFile the file to get history for
     * @throws JDataException on error
     */
    public void getFileRevisionHistory(final SVNURL pFile) throws JDataException {
        /* Access client */
        SVNLogClient myLog = theMgr.getLogClient();
        theResults.clear();

        /* Protect against exceptions */
        try {
            /* Obtain information about the revision */
            myLog.doLog(pFile, null, SVNRevision.HEAD, SVNRevision.HEAD, SVNRevision.create(0), false, true,
                        0, new LogHandler());
        } catch (SVNException e) {
            throw new JDataException(ExceptionClass.SUBVERSION, "Failed to get revision history", e);
        }
    }

    /**
     * EventHandler.
     */
    private final class LogHandler implements ISVNLogEntryHandler {

        @Override
        public void handleLogEntry(final SVNLogEntry pEntry) throws SVNException {
            /* Add the entry */
            theResults.add(pEntry);
        }
    }
}
