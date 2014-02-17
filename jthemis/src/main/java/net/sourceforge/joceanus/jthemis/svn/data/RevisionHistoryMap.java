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
package net.sourceforge.joceanus.jthemis.svn.data;

import java.util.LinkedHashMap;

import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataFormat;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jthemis.JThemisIOException;
import net.sourceforge.joceanus.jthemis.svn.data.RevisionHistory.RevisionKey;

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * Map of RevisionHistory.
 * @author Tony Washer
 */
public class RevisionHistoryMap
        extends LinkedHashMap<RevisionKey, RevisionHistory>
        implements JDataFormat {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 6314367951380041536L;

    /**
     * The repository.
     */
    private final transient SvnRepository theRepository;

    @Override
    public String formatObject() {
        return getClass().getSimpleName();
    }

    /**
     * Get the repository for this map.
     * @return the repository
     */
    public SvnRepository getRepository() {
        return theRepository;
    }

    /**
     * Constructor.
     * @param pRepository the repository
     */
    protected RevisionHistoryMap(final SvnRepository pRepository) {
        /* Access repository and log client */
        theRepository = pRepository;
    }

    /**
     * Discover branch history.
     * @param pBranch the branch
     * @return the revisionPath
     * @throws JOceanusException on error
     */
    protected RevisionPath discoverBranch(final SvnBranch pBranch) throws JOceanusException {
        return new RevisionPath(this, pBranch.getURLPath());
    }

    /**
     * Discover tag history.
     * @param pTag the tag
     * @return the revisionPath
     * @throws JOceanusException on error
     */
    protected RevisionPath discoverTag(final SvnTag pTag) throws JOceanusException {
        return new RevisionPath(this, pTag.getURLPath());
    }

    /**
     * Details of a revision path for a directory in a SubVersion repository.
     */
    public static class RevisionPath
            implements JDataContents {
        /**
         * DataFields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(RevisionPath.class.getSimpleName());

        /**
         * Path field.
         */
        private static final JDataField FIELD_PATH = FIELD_DEFS.declareLocalField("Path");

        /**
         * Revision field.
         */
        private static final JDataField FIELD_REVISION = FIELD_DEFS.declareLocalField("Revision");

        /**
         * Initial History field.
         */
        private static final JDataField FIELD_HISTORY = FIELD_DEFS.declareLocalField("History");

        /**
         * The HistoryMap.
         */
        private final RevisionHistoryMap theHistoryMap;

        /**
         * The path for the revision.
         */
        private final String thePath;

        /**
         * The starting revision.
         */
        private final SVNRevision theBaseRevision;

        /**
         * The first history.
         */
        private RevisionHistory theFirstHistory;

        /**
         * The last history.
         */
        private RevisionHistory theLastHistory;

        /**
         * The origin.
         */
        private RevisionKey theOrigin;

        /**
         * The sourcePath.
         */
        private RevisionPath theSourcePath;

        @Override
        public String formatObject() {
            return FIELD_DEFS.getName();
        }

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_PATH.equals(pField)) {
                return thePath;
            }
            if (FIELD_REVISION.equals(pField)) {
                return theBaseRevision;
            }
            if (FIELD_HISTORY.equals(pField)) {
                return theFirstHistory;
            }
            return JDataFieldValue.UNKNOWN;
        }

        /**
         * Constructor.
         * @param pHistoryMap the History Map
         * @param pPath the path to document
         * @throws JOceanusException on error
         */
        public RevisionPath(final RevisionHistoryMap pHistoryMap,
                            final String pPath) throws JOceanusException {
            /* Default to HEAD revision */
            this(pHistoryMap, pPath, SVNRevision.HEAD);
        }

        /**
         * Constructor.
         * @param pHistoryMap the History Map
         * @param pPath the path to document
         * @param pRevision the base revision
         * @throws JOceanusException on error
         */
        public RevisionPath(final RevisionHistoryMap pHistoryMap,
                            final String pPath,
                            final SVNRevision pRevision) throws JOceanusException {
            /* Determine the prefix */
            SvnRepository myRepo = pHistoryMap.getRepository();
            String myPrefix = myRepo.getPath();

            /* Store the path */
            theHistoryMap = pHistoryMap;
            thePath = pPath.startsWith(myPrefix)
                                                ? pPath.substring(myPrefix.length())
                                                : pPath;
            theBaseRevision = pRevision;

            /* Discover revisions */
            discoverRevisions();

            /* If we have a source for the directory */
            if (theOrigin != null) {
                /* Obtain the source path */
                theSourcePath = new RevisionPath(theHistoryMap, theOrigin.getPath(), theOrigin.getRevision());
                theLastHistory.setBasedOn(theSourcePath.theFirstHistory);
            }
        }

        /**
         * Discover Revisions.
         * @throws JOceanusException on error
         */
        private void discoverRevisions() throws JOceanusException {
            /* Access the log client */
            SvnRepository myRepo = theHistoryMap.getRepository();
            SVNClientManager myClientMgr = myRepo.getClientManager();
            SVNLogClient myLogClient = myClientMgr.getLogClient();

            /* Protect against exceptions */
            try {
                /* Build the location details */
                SVNURL myURL = SVNURL.parseURIEncoded(myRepo.getPath() + thePath);

                /* Obtain information about the revision */
                myLogClient.doLog(myURL, null, theBaseRevision, theBaseRevision,
                        SVNRevision.create(0), false, true, 0, new LogHandler());

                /* Ignore cancel exceptions */
            } catch (SVNCancelException e) {
                return;
            } catch (SVNException e) {
                throw new JThemisIOException("Failed to get revision history", e);
            } finally {
                myRepo.releaseClientManager(myClientMgr);
            }
        }

        @Override
        public String toString() {
            /* Create a stringBuilder */
            StringBuilder myBuilder = new StringBuilder();

            /* Output the title */
            myBuilder.append("Revisions for ");
            myBuilder.append(theBaseRevision);
            myBuilder.append(":");
            myBuilder.append(thePath);

            /* loop through the revisions */
            for (RevisionHistory myRevision = theFirstHistory; myRevision != null; myRevision = myRevision.getBasedOn()) {
                /* Add revision Details */
                myBuilder.append(myRevision.toString());
            }

            /* Return the details */
            return myBuilder.toString();
        }

        /**
         * EventHandler.
         */
        private final class LogHandler
                implements ISVNLogEntryHandler {
            @Override
            public void handleLogEntry(final SVNLogEntry pEntry) throws SVNException {
                /* Create the revisionKey */
                RevisionKey myKey = new RevisionKey(thePath, SVNRevision.create(pEntry.getRevision()));

                /* Check whether we have analysed this already */
                RevisionHistory myHistory = theHistoryMap.get(myKey);
                if (myHistory != null) {
                    /* Link in and halt the search */
                    linkHistory(myHistory);
                    throw new SVNCancelException();
                }

                /* Analyse the entry */
                myHistory = new RevisionHistory(thePath, pEntry);

                /* If it is relevant */
                if (myHistory.isRelevant()) {
                    /* Link in and record last history */
                    linkHistory(myHistory);
                    theLastHistory = myHistory;

                    /* Add to map */
                    theHistoryMap.put(myKey, myHistory);
                    if (myHistory.isOrigin()) {
                        theOrigin = myHistory.getOrigin();
                        throw new SVNCancelException();
                    }
                }
            }

            /**
             * Link history.
             * @param pHistory the new history
             */
            private void linkHistory(final RevisionHistory pHistory) {
                if (theLastHistory != null) {
                    theLastHistory.setBasedOn(pHistory);
                } else {
                    theFirstHistory = pHistory;
                }
            }
        }
    }
}
