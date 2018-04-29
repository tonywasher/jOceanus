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
package net.sourceforge.joceanus.jthemis.svn.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataMap;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.ThemisResource;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnRevisionHistory.ThemisSvnRevisionKey;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnRevisionHistory.ThemisSvnSourceDir;

/**
 * Map of RevisionHistory.
 * @author Tony Washer
 */
public class ThemisSvnRevisionHistoryMap
        implements
        MetisDataObjectFormat,
        MetisDataMap<ThemisSvnRevisionKey, ThemisSvnRevisionHistory> {
    /**
     * The repository.
     */
    private final ThemisSvnRepository theRepository;

    /**
     * The current owner.
     */
    private Object theOwner;

    /**
     * HistoryMap.
     */
    private final Map<ThemisSvnRevisionKey, ThemisSvnRevisionHistory> theHistoryMap;

    /**
     * Constructor.
     * @param pRepository the repository
     */
    protected ThemisSvnRevisionHistoryMap(final ThemisSvnRepository pRepository) {
        /* Access repository and log client */
        theRepository = pRepository;
        theHistoryMap = new HashMap<>();
    }

    @Override
    public Map<ThemisSvnRevisionKey, ThemisSvnRevisionHistory> getUnderlyingMap() {
        return theHistoryMap;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    /**
     * Get the repository for this map.
     * @return the repository
     */
    public ThemisSvnRepository getRepository() {
        return theRepository;
    }

    /**
     * Obtain the owner.
     * @return the owner
     */
    private Object getOwner() {
        return theOwner;
    }

    /**
     * Discover branch history.
     * @param pBranch the branch
     * @return the revisionPath
     * @throws OceanusException on error
     */
    protected ThemisSvnRevisionPath discoverBranch(final ThemisSvnBranch pBranch) throws OceanusException {
        /* Set current owner */
        theOwner = pBranch;

        /* Discover the path */
        final ThemisSvnRevisionPath myPath = new ThemisSvnRevisionPath(this, pBranch.getURLPath());

        /* Expand SourceDirectories */
        myPath.expandSourceDirs();

        /* Return the path */
        return myPath;
    }

    /**
     * Discover tag history.
     * @param pTag the tag
     * @return the revisionPath
     * @throws OceanusException on error
     */
    protected ThemisSvnRevisionPath discoverTag(final ThemisSvnTag pTag) throws OceanusException {
        /* Set current owner */
        theOwner = pTag;

        /* Discover the path */
        final ThemisSvnRevisionPath myPath = new ThemisSvnRevisionPath(this, pTag.getURLPath());

        /* Expand SourceDirectories */
        myPath.expandSourceDirs();

        /* Return the path */
        return myPath;
    }

    /**
     * Details of a revision path for a directory in a SubVersion repository.
     */
    public static class ThemisSvnRevisionPath
            implements
            MetisFieldItem {
        /**
         * DataFields.
         */
        private static final MetisFieldSet<ThemisSvnRevisionPath> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisSvnRevisionPath.class);

        /**
         * fieldIds.
         */
        static {
            FIELD_DEFS.declareLocalField(ThemisResource.SVN_PATH, ThemisSvnRevisionPath::getPath);
            FIELD_DEFS.declareLocalField(ThemisResource.SVN_REVISION, ThemisSvnRevisionPath::getRevisionNo);
            FIELD_DEFS.declareLocalField(ThemisResource.SVN_HISTORY, ThemisSvnRevisionPath::getHistory);
        }

        /**
         * The HistoryMap.
         */
        private final ThemisSvnRevisionHistoryMap theHistoryMap;

        /**
         * The owner.
         */
        private final Object theOwner;

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
        private ThemisSvnRevisionHistory theFirstHistory;

        /**
         * The last history.
         */
        private ThemisSvnRevisionHistory theLastHistory;

        /**
         * The origin.
         */
        private ThemisSvnRevisionKey theOrigin;

        /**
         * The sourcePath.
         */
        private ThemisSvnRevisionPath theSourcePath;

        /**
         * Constructor.
         * @param pHistoryMap the History Map
         * @param pPath the path to document
         * @throws OceanusException on error
         */
        public ThemisSvnRevisionPath(final ThemisSvnRevisionHistoryMap pHistoryMap,
                                     final String pPath) throws OceanusException {
            /* Default to HEAD revision */
            this(pHistoryMap, pPath, SVNRevision.HEAD);
        }

        /**
         * Constructor.
         * @param pHistoryMap the History Map
         * @param pKey the revisionPath
         * @throws OceanusException on error
         */
        public ThemisSvnRevisionPath(final ThemisSvnRevisionHistoryMap pHistoryMap,
                                     final ThemisSvnRevisionKey pKey) throws OceanusException {
            /* Extract the details from the revisionKey */
            this(pHistoryMap, pKey.getPath(), pKey.getRevision());
        }

        /**
         * Constructor.
         * @param pHistoryMap the History Map
         * @param pPath the path to document
         * @param pRevision the base revision
         * @throws OceanusException on error
         */
        public ThemisSvnRevisionPath(final ThemisSvnRevisionHistoryMap pHistoryMap,
                                     final String pPath,
                                     final SVNRevision pRevision) throws OceanusException {
            /* Determine the prefix */
            final ThemisSvnRepository myRepo = pHistoryMap.getRepository();
            final String myPrefix = myRepo.getPath();

            /* Store the path */
            theOwner = pHistoryMap.getOwner();
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
                theSourcePath = new ThemisSvnRevisionPath(theHistoryMap, theOrigin);
                theLastHistory.setBasedOn(theSourcePath.getBasedOn());
            }
        }

        @Override
        public String formatObject(final MetisDataFormatter pFormatter) {
            return FIELD_DEFS.getName();
        }

        @Override
        public MetisFieldSet<ThemisSvnRevisionPath> getDataFieldSet() {
            return FIELD_DEFS;
        }

        /**
         * Obtain the path.
         * @return the path
         */
        private String getPath() {
            return thePath;
        }

        /**
         * Obtain the revision#.
         * @return the revision
         */
        private long getRevisionNo() {
            return theBaseRevision.getNumber();
        }

        /**
         * Obtain the history.
         * @return the history
         */
        private ThemisSvnRevisionHistory getHistory() {
            return theFirstHistory;
        }

        /**
         * Obtain the basedOn link.
         * @return the link
         */
        public ThemisSvnRevisionHistory getBasedOn() {
            return theFirstHistory;
        }

        /**
         * Obtain the owner.
         * @return the owner
         */
        public Object getOwner() {
            return theOwner;
        }

        /**
         * Discover Revisions.
         * @throws OceanusException on error
         */
        private void discoverRevisions() throws OceanusException {
            /* Access the log client */
            final ThemisSvnRepository myRepo = theHistoryMap.getRepository();
            final SVNClientManager myClientMgr = myRepo.getClientManager();
            final SVNLogClient myLogClient = myClientMgr.getLogClient();

            /* Protect against exceptions */
            try {
                /* Build the location details */
                final SVNURL myURL = SVNURL.parseURIEncoded(myRepo.getPath() + thePath);

                /* Obtain information about the revision */
                myLogClient.doLog(myURL, null, theBaseRevision, theBaseRevision,
                        SVNRevision.create(0), false, true, 0, new LogHandler());

                /* Ignore cancel exceptions */
            } catch (SVNCancelException e) {
                return;
            } catch (SVNException e) {
                throw new ThemisIOException("Failed to get revision history", e);
            } finally {
                myRepo.releaseClientManager(myClientMgr);
            }
        }

        /**
         * Expand SourceDirectories.
         * @throws OceanusException on error
         */
        private void expandSourceDirs() throws OceanusException {
            /* loop through the revisions */
            for (ThemisSvnRevisionHistory myRevision = theFirstHistory; myRevision != null; myRevision = myRevision.getBasedOn()) {
                /* Ignore if no sourceDirs */
                if (!myRevision.hasSourceDirs()) {
                    continue;
                }

                /* Loop through any SourceDirectories */
                final Iterator<ThemisSvnSourceDir> myIterator = myRevision.sourceDirIterator();
                while (myIterator.hasNext()) {
                    final ThemisSvnSourceDir mySourceDir = myIterator.next();

                    /* If the directory has not been expanded */
                    if (mySourceDir.getBasedOn() == null) {
                        /* Analyse the source */
                        final ThemisSvnRevisionKey mySource = mySourceDir.getSource();
                        final ThemisSvnRevisionPath myPath = new ThemisSvnRevisionPath(theHistoryMap, mySource);

                        /* Store the details */
                        mySourceDir.setBasedOn(myPath.getBasedOn());

                        /* Expand the source path */
                        myPath.expandSourceDirs();
                    }
                }
            }
        }

        @Override
        public String toString() {
            /* Create a stringBuilder */
            final StringBuilder myBuilder = new StringBuilder();

            /* Output the title */
            myBuilder.append("Revisions for ");
            myBuilder.append(theBaseRevision);
            myBuilder.append(":");
            myBuilder.append(thePath);

            /* loop through the revisions */
            for (ThemisSvnRevisionHistory myRevision = theFirstHistory; myRevision != null; myRevision = myRevision.getBasedOn()) {
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
                implements
                ISVNLogEntryHandler {
            @Override
            public void handleLogEntry(final SVNLogEntry pEntry) throws SVNException {
                /* Create the revisionKey */
                final ThemisSvnRevisionKey myKey = new ThemisSvnRevisionKey(thePath, SVNRevision.create(pEntry.getRevision()));

                /* Check whether we have analysed this already */
                ThemisSvnRevisionHistory myHistory = theHistoryMap.get(myKey);
                if (myHistory != null) {
                    /* Link in and halt the search */
                    linkHistory(myHistory);
                    throw new SVNCancelException();
                }

                /* Analyse the entry */
                try {
                    myHistory = new ThemisSvnRevisionHistory(theOwner, thePath, pEntry);
                } catch (OceanusException e) {
                    final SVNErrorMessage myMessage = SVNErrorMessage.create(SVNErrorCode.EXTERNAL_PROGRAM, e);
                    throw new SVNException(myMessage);
                }

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
            private void linkHistory(final ThemisSvnRevisionHistory pHistory) {
                if (theLastHistory != null) {
                    theLastHistory.setBasedOn(pHistory);
                } else {
                    theFirstHistory = pHistory;
                }
            }
        }
    }
}
