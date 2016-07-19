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
package net.sourceforge.joceanus.jthemis.svn.data;

import java.util.Iterator;
import java.util.LinkedHashMap;

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

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataFormat;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnRevisionHistory.SvnRevisionKey;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnRevisionHistory.SvnSourceDir;

/**
 * Map of RevisionHistory.
 * @author Tony Washer
 */
public class ThemisSvnRevisionHistoryMap
        extends LinkedHashMap<SvnRevisionKey, ThemisSvnRevisionHistory>
        implements MetisDataFormat {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 6314367951380041536L;

    /**
     * The repository.
     */
    private final transient ThemisSvnRepository theRepository;

    /**
     * The current owner.
     */
    private transient Object theOwner;

    /**
     * Constructor.
     * @param pRepository the repository
     */
    protected ThemisSvnRevisionHistoryMap(final ThemisSvnRepository pRepository) {
        /* Access repository and log client */
        theRepository = pRepository;
    }

    @Override
    public String formatObject() {
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
    protected SvnRevisionPath discoverBranch(final ThemisSvnBranch pBranch) throws OceanusException {
        /* Set current owner */
        theOwner = pBranch;

        /* Discover the path */
        SvnRevisionPath myPath = new SvnRevisionPath(this, pBranch.getURLPath());

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
    protected SvnRevisionPath discoverTag(final ThemisSvnTag pTag) throws OceanusException {
        /* Set current owner */
        theOwner = pTag;

        /* Discover the path */
        SvnRevisionPath myPath = new SvnRevisionPath(this, pTag.getURLPath());

        /* Expand SourceDirectories */
        myPath.expandSourceDirs();

        /* Return the path */
        return myPath;
    }

    /**
     * Details of a revision path for a directory in a SubVersion repository.
     */
    public static class SvnRevisionPath
            implements MetisDataContents {
        /**
         * DataFields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(SvnRevisionPath.class.getSimpleName());

        /**
         * Path field.
         */
        private static final MetisField FIELD_PATH = FIELD_DEFS.declareLocalField("Path");

        /**
         * Revision field.
         */
        private static final MetisField FIELD_REVISION = FIELD_DEFS.declareLocalField("Revision");

        /**
         * Initial History field.
         */
        private static final MetisField FIELD_HISTORY = FIELD_DEFS.declareLocalField("History");

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
        private SvnRevisionKey theOrigin;

        /**
         * The sourcePath.
         */
        private SvnRevisionPath theSourcePath;

        /**
         * Constructor.
         * @param pHistoryMap the History Map
         * @param pPath the path to document
         * @throws OceanusException on error
         */
        public SvnRevisionPath(final ThemisSvnRevisionHistoryMap pHistoryMap,
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
        public SvnRevisionPath(final ThemisSvnRevisionHistoryMap pHistoryMap,
                               final SvnRevisionKey pKey) throws OceanusException {
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
        public SvnRevisionPath(final ThemisSvnRevisionHistoryMap pHistoryMap,
                               final String pPath,
                               final SVNRevision pRevision) throws OceanusException {
            /* Determine the prefix */
            ThemisSvnRepository myRepo = pHistoryMap.getRepository();
            String myPrefix = myRepo.getPath();

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
                theSourcePath = new SvnRevisionPath(theHistoryMap, theOrigin);
                theLastHistory.setBasedOn(theSourcePath.getBasedOn());
            }
        }

        @Override
        public String formatObject() {
            return FIELD_DEFS.getName();
        }

        @Override
        public MetisFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final MetisField pField) {
            if (FIELD_PATH.equals(pField)) {
                return thePath;
            }
            if (FIELD_REVISION.equals(pField)) {
                return theBaseRevision;
            }
            if (FIELD_HISTORY.equals(pField)) {
                return theFirstHistory;
            }
            return MetisFieldValue.UNKNOWN;
        }

        /**
         * Obtain the basedOn link.
         * @return the log entry
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
            ThemisSvnRepository myRepo = theHistoryMap.getRepository();
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
                Iterator<SvnSourceDir> myIterator = myRevision.sourceDirIterator();
                while (myIterator.hasNext()) {
                    SvnSourceDir mySourceDir = myIterator.next();

                    /* If the directory has not been expanded */
                    if (mySourceDir.getBasedOn() == null) {
                        /* Analyse the source */
                        SvnRevisionKey mySource = mySourceDir.getSource();
                        SvnRevisionPath myPath = new SvnRevisionPath(theHistoryMap, mySource);

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
            StringBuilder myBuilder = new StringBuilder();

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
                implements ISVNLogEntryHandler {
            @Override
            public void handleLogEntry(final SVNLogEntry pEntry) throws SVNException {
                /* Create the revisionKey */
                SvnRevisionKey myKey = new SvnRevisionKey(thePath, SVNRevision.create(pEntry.getRevision()));

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
                    SVNErrorMessage myMessage = SVNErrorMessage.create(SVNErrorCode.EXTERNAL_PROGRAM, e);
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
